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

	public void begin() {
		db.beginTransaction();
	}
	
	public void commit() {
		db.endTransaction();
	}
	
	public boolean delete(Site site) {
		return db.delete(STATE_TABLE, KEY_ID + " = " + site.ordinal(), null) > 0;
	}

	public long updateState(Site site, Item item) {
		delete(site);
		final ContentValues v = new ContentValues();
		v.put(KEY_ID, site.ordinal());
		v.put(KEY_TITLE, item.getTitle());
		return db.insert(STATE_TABLE, null, v);
	}

	public boolean isItemCurrent(Site site, Item item) {

		final Cursor c = db.query(STATE_TABLE, new String[] { KEY_ID, KEY_TITLE }, KEY_ID + " = " + site.ordinal(),
				null, null, null, null);
			
		boolean ret = false;
		if (c.getCount() > 0) {
			c.moveToFirst();
			ret = item.getTitle().equals(c.getString(1));
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
			db.execSQL("CREATE TABLE dealdroid_state (id INTEGER PRIMARY KEY, title TEXT NOT NULL);");
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
