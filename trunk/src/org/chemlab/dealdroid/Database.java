package org.chemlab.dealdroid;

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

	private static final String KEY_ID = "id";

	private static final String KEY_TITLE = "title";
	
	private static final String KEY_DESCRIPTION = "description";
	
	private static final String KEY_URL = "url";
	
	private static final String KEY_IMAGE_URL = "image_url";
	
	private static final String KEY_RETAIL_PRICE = "retail_price";
	
	private static final String KEY_SALE_PRICE = "sale_price";
	
	private static final String KEY_SAVINGS = "savings";
	
	private static final String DATABASE_NAME = "dealdroid.db";

	private static final int DATABASE_VERSION = 1;

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
		return db.delete(STATE_TABLE, KEY_ID + "=?", new String[] { site.name() }) > 0;
	}

	/**
	 * @param site
	 * @return
	 */
	public Item getCurrentItem(final Site site) {
		
		final Cursor c = db.query(STATE_TABLE, new String[] { KEY_ID, KEY_TITLE, KEY_SALE_PRICE, KEY_RETAIL_PRICE, KEY_SAVINGS, KEY_DESCRIPTION, KEY_URL, KEY_IMAGE_URL }, KEY_ID + "=?", new String[] { site.name() }, null, null, null);
		final Item item;
		if (c.getCount() == 1) {
			c.moveToFirst();
			item = new Item(c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), Uri.parse(c.getString(6)), Uri.parse(c.getString(7)));
		} else {
			item = null;
		}
		return item;
	}
	
	/**
	 * Updates the state of a site with a new item.
	 * 
	 * @param site
	 * @param item
	 * @return if the state was updated
	 */
	public synchronized boolean updateStateIfNotCurrent(Site site, Item item) {
		db.beginTransaction();
		boolean ret = false;
		try {
			if (isItemNew(site, item)) {
				
				delete(site);
				
				final ContentValues v = new ContentValues();
				v.put(KEY_ID, site.name());
				v.put(KEY_TITLE, item.getTitle());
				v.put(KEY_DESCRIPTION, item.getDescription());
				v.put(KEY_RETAIL_PRICE, item.getRetailPrice());
				v.put(KEY_SALE_PRICE, item.getSalePrice());
				v.put(KEY_SAVINGS, item.getSavings());
				v.put(KEY_URL, item.getLink().toString());
				v.put(KEY_IMAGE_URL, item.getImageLink().toString());
				
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
			final Cursor c = db.query(STATE_TABLE, new String[] { KEY_ID, KEY_TITLE }, KEY_ID + "=?", new String[] { site.name() }, null, null, null);
			
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
			db.execSQL("CREATE TABLE dealdroid_state (id TEXT PRIMARY KEY, title TEXT NOT NULL, url TEXT NOT NULL, image_url TEXT NOT NULL, description TEXT NOT NULL, sale_price TEXT NOT NULL, retail_price TEXT NOT NULL, savings TEXT NOT NULL);");
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
			// nothing yet
		}

	}
}