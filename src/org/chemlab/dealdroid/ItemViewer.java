package org.chemlab.dealdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
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
		
	private final List<Integer> keyBuffer = new ArrayList<Integer>(4);
	
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
			webview.loadUrl("content://org.chemlab.dealdroid/" + site.name());
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		
		// There is 1337 shit happening here, and I am bored.
		keyBuffer.add(keyCode);
		
		if (keyBuffer.size() == 4) {
			if (keyBuffer.get(0) == 35 && keyBuffer.get(1) == 42 && keyBuffer.get(2) == 29 && keyBuffer.get(3) == 46) {
				keyBuffer.clear();
				if (webview != null) {
					webview.loadUrl("http://n0rp.chemlab.org/dd/tita.html");
				}
			} else {
				keyBuffer.remove(0);
			}
		}
	
		return super.onKeyDown(keyCode, event);
	}

	
}
