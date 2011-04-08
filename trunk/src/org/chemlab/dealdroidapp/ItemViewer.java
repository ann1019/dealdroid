package org.chemlab.dealdroidapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * A simple viewer to quickly view new items before launching
 * the full browser.
 * 
 * @author shade
 * @version $Id$
 */
public class ItemViewer extends Activity {

	private WebView webview;
			
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Site site = Site.valueOf(getIntent().getExtras().getString("site"));

		webview = (WebView) findViewById(R.id.webview);
		
		// If we have a custom asset for the site, use it.  Otherwise just go to the page.
		// Sorry, I am not doing custom assets unless there is an affiliation.
		if (Utils.hasSiteAsset(this, site)) {
			webview.loadUrl("content://org.chemlab.dealdroidapp/" + site.name());
		} else {
			
			final Database db = new Database(this);
			try {
				db.open();
				final Uri uri = site.isForceUrl() ? Uri.parse(site.getSite().toExternalForm()) : db.getCurrentItem(site).getLink();
				webview.loadUrl(site.applyAffiliation(uri).toString());
			} finally {
				db.close();
			}
		}

	}
}
