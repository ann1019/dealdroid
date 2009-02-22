package org.chemlab.dealdroid;

import android.app.Activity;
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

	public static final String DEALDROID_VIEWER = "org.chemlab.dealdroid.VIEWER";

	private WebView webview;

	private Site currentSite;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		currentSite = Site.valueOf(getIntent().getExtras().getString("site"));

		webview = (WebView) findViewById(R.id.webview);
		webview.loadUrl("content://org.chemlab.dealdroid/" + currentSite.name());

	}

}
