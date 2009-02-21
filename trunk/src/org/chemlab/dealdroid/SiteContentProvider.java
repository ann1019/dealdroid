package org.chemlab.dealdroid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * @author shade
 * @version $Id$
 */
public class SiteContentProvider extends ContentProvider {

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#openFile(android.net.Uri, java.lang.String)
	 */
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		
		final Site site = Site.valueOf(uri.getPathSegments().get(0));
		
		Log.i(this.getClass().getName(), site.toString());
				
		final Context c = getContext();
		c.deleteFile("site.html");
		
		BufferedWriter writer = null;
		
		try {

			writer = new BufferedWriter(new OutputStreamWriter(c.openFileOutput("site.html", Context.MODE_PRIVATE)));
			final String data = readAsset(site.name().toLowerCase() + ".html");
			final String populated = populate(site, data);
			writer.write(populated);
			
		} catch (IOException e) {
			
			throw new RuntimeException(e);
			
		} finally {

			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return ParcelFileDescriptor.open(c.getFileStreamPath("site.html"), ParcelFileDescriptor.MODE_READ_ONLY);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param site
	 * @param template
	 * @return
	 */
	private String populate(final Site site, final String template) {
		
		final Database db = new Database(getContext());
		
		String p = template;
		
		try {
			db.open();
			final Item item = db.getCurrentItem(site);			
			if (item != null) {
				p = p.replaceAll("\\{title\\}", item.getTitle());
				p = p.replaceAll("\\{buy_url\\}", item.getLink().toString());
				p = p.replaceAll("\\{image_url\\}", item.getImageLink().toString());
				p = p.replaceAll("\\{description\\}", item.getDescription());
				p = p.replaceAll("\\{price\\}", item.getSalePrice());
				p = p.replaceAll("\\{savings\\}", item.getSavings());
			}
			
		} finally {
			db.close();
		}
		
		return p;
	}
	
	/**
	 * @param assetName
	 * @return
	 */
	private String readAsset(final String assetName) {
		
		BufferedReader reader = null;
		final StringBuffer sb = new StringBuffer();
		
		try {

			final InputStream asset = this.getContext().getAssets().open(assetName);
			reader = new BufferedReader(new InputStreamReader(asset));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		
		} catch (IOException e) {
			
			Log.e(this.getClass().getName(), e.getMessage(), e);
			
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
				}
			}
		}
		
		return sb.toString();
			
	}
}
