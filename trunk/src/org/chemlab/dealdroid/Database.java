package org.chemlab.dealdroid;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Encapsulates access to the SQLite database used for keeping
 * the current application state.
 * 
 * @author shade
 * @version $Id$
 */
public class Database {

	private enum Field {
			
		ID("TEXT PRIMARY KEY"),
		TITLE("TEXT NOT NULL"), 
		DESCRIPTION("TEXT"), 
		SHORT_DESCRIPTION("TEXT"),
		URL("TEXT NOT NULL"),
		IMAGE_URL("TEXT"), 
		RETAIL_PRICE("TEXT"), 
		SALE_PRICE("TEXT"), 
		SAVINGS("TEXT"), 
		TIMESTAMP("NUMBER NOT NULL"), 
		EXPIRATION("NUMBER");
		
		private final String modifier;
		
		Field(final String modifier) {
			this.modifier = modifier;
		}
			
		/**
		 * @return
		 */
		public String key() {
			return this.name().toLowerCase();
		}

		/**
		 * @return the modifier
		 */
		public String getModifier() {
			return modifier;
		}
	}
		
	private static final String DATABASE_NAME = "dealdroid.db";

	private static final String STATE_TABLE = "dealdroid_state";

	private final DatabaseHelper dbHelper;

	private SQLiteDatabase db;

	/**
	 * @param context
	 */
	public Database(Context context) {
		this.dbHelper = new DatabaseHelper(context);
	}

	/**
	 * Opens the database for use.
	 */
	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Deletes data for a site.
	 * 
	 * @param site
	 * @return if any sites were deleted
	 */
	public boolean delete(Site site) {
		return db.delete(STATE_TABLE, Field.ID.key() + "=?", new String[] { site.name() }) > 0;
	}

	/**
	 * @param site
	 * @return
	 */
	public Item getCurrentItem(final Site site) {
		
		final Cursor c = db.query(STATE_TABLE, new String[] { Field.ID.key(), Field.TITLE.key(), Field.SALE_PRICE.key(),
				Field.RETAIL_PRICE.key(), Field.SAVINGS.key(), Field.DESCRIPTION.key(), 
				Field.SHORT_DESCRIPTION.key(), Field.URL.key(), Field.IMAGE_URL.key(), Field.EXPIRATION.key(), Field.TIMESTAMP.key() }, 
				Field.ID.key() + "=?", new String[] { site.name() }, null, null, null);
		
		final Item item;
		if (c.getCount() == 1) {
			c.moveToFirst();
			item = new Item(c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), 
					c.isNull(7) ? null : Uri.parse(c.getString(7)), 
					c.isNull(8) ? null : Uri.parse(c.getString(8)),
					c.isNull(9) ? null : new Date(c.getLong(9)),
					c.isNull(10) ? null : new Date(c.getLong(10)));
		} else {
			item = null;
		}
		c.close();
		return item;
	}
	
	/**
	 * Updates the state of a site with a new item.
	 * 
	 * @param site
	 * @param item
	 * @return if the state was updated
	 */
	public boolean updateStateIfNotCurrent(Site site, Item item) {
		db.beginTransaction();
		boolean ret = false;
		try {
			if (isItemNew(site, item)) {
				
				delete(site);
				
				final ContentValues v = new ContentValues();
				v.put(Field.ID.key(), site.name());
				v.put(Field.TITLE.key(), item.getTitle());
				v.put(Field.DESCRIPTION.key(), item.getDescription());
				v.put(Field.RETAIL_PRICE.key(), item.getRetailPrice());
				v.put(Field.SALE_PRICE.key(), item.getSalePrice());
				v.put(Field.SAVINGS.key(), item.getSavings());
				v.put(Field.URL.key(), item.getLink().toString());
				v.put(Field.TIMESTAMP.key(), item.getTimestamp().getTime());
				v.put(Field.SHORT_DESCRIPTION.key(), item.getShortDescription());
				
				if (item.getImageLink() != null) {
					v.put(Field.IMAGE_URL.key(), item.getImageLink().toString());
				}
				
				if (item.getExpiration() != null) {
					v.put(Field.EXPIRATION.key(), item.getExpiration().getTime());
				}
				
				db.insert(STATE_TABLE, null, v);
				ret = true;
			}
			db.setTransactionSuccessful();
			
		} finally {
			db.endTransaction();
		}
		return ret;
	}

	/**
	 * @param site
	 * @param item
	 * @return true if this is a new item
	 */
	private boolean isItemNew(Site site, Item item) {

		boolean ret = false;
		if (site != null && item != null && item.getTitle() != null) {
			final Cursor c = db.query(STATE_TABLE, new String[] { Field.ID.key(), Field.TITLE.key() }, Field.ID.key() + "=?", new String[] { site.name() }, null, null, null);
			
			if (c.getCount() == 0) {
				ret = true;
			} else {
				c.moveToFirst();
				ret = !item.getTitle().equals(c.getString(1));
				if (ret) {
					Log.d(this.getClass().getSimpleName(), "New item found!  Old: [" + c.getString(1) + "], New: [" + item.getTitle() + "]");
				}
			}
		
			c.close();
		}
		return ret;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 3;
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 * .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			final StringBuilder sb = new StringBuilder();
			for (Field f : Field.values()) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(f.key()). append(" ").append(f.getModifier());
			}
			
			final StringBuilder createSQL = new StringBuilder("CREATE TABLE ").append(STATE_TABLE).append(" (").append(sb).append(");");
			db.execSQL(createSQL.toString());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
		 * .sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion > oldVersion) {
				Log.i(this.getClass().getSimpleName(), "Upgrading database from version " + oldVersion + " to " + newVersion + "..");
				db.execSQL("DROP TABLE " + STATE_TABLE);
				onCreate(db);
			}
		}

	}
}
