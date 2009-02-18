package org.chemlab.dealdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author shade
 * @version $Id$
 */
public class Database {

	private static final String KEY_ID = "id";

	private static final String KEY_TITLE = "title";
		
	private static final String DATABASE_NAME = "dealdroid.db";

	private static final int DATABASE_VERSION = 1;

	private static final String STATE_TABLE = "dealdroid_state";

	private final DatabaseHelper dbHelper;

	private SQLiteDatabase db;

	public Database(Context context) {
		this.dbHelper = new DatabaseHelper(context);
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	private String idMatch(Site site) {
		return new StringBuilder(KEY_ID).append("='").append(site.name()).append("'").toString();
	}
	
	public boolean delete(Site site) {
		return db.delete(STATE_TABLE, idMatch(site), null) > 0;
	}

	public synchronized boolean updateStateIfNotCurrent(Site site, Item item) {
		db.beginTransaction();
		boolean ret = false;
		try {
			if (isItemNew(site, item)) {
				
				delete(site);
				
				final ContentValues v = new ContentValues();
				v.put(KEY_ID, site.name());
				v.put(KEY_TITLE, item.getTitle());
				
				db.insert(STATE_TABLE, null, v);
				ret = true;
			}
			db.setTransactionSuccessful();
			
		} finally {
			db.endTransaction();
		}
		return ret;
	}

	private boolean isItemNew(Site site, Item item) {

		final Cursor c = db.query(STATE_TABLE, new String[] { KEY_ID, KEY_TITLE }, idMatch(site), null, null, null, null);
			
		boolean ret = false;
		if (c.getCount() == 0) {
			ret = true;
		} else {
			c.moveToFirst();
			ret = !item.getTitle().equals(c.getString(1));
		}
		
		c.close();
		
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
			db.execSQL("CREATE TABLE dealdroid_state (id TEXT PRIMARY KEY, title TEXT NOT NULL);");
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
