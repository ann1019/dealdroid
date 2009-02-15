package org.chemlab.dealdroid;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author shade
 * @version $Id$
 */
public enum DealSite {

	WHISKEYMILITIA("Whiskey Militia", "http://www.whiskeymilitia.com/docs/wm/rssplus.xml", R.drawable.icon_whiskeymilitia),
	STEEPANDCHEAP("Steep and Cheap", "http://www.steepandcheap.com/docs/steepcheap/rssplus.xml", R.drawable.icon_steepandcheep);
	
	private final String name;
	
	private final URL url;
	
	private final int drawable;
	
	DealSite(String name, String url, int drawable) {
		try {
			this.name = name;
			this.url = new URL(url);
			this.drawable = drawable;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @return the drawable
	 */
	public int getDrawable() {
		return drawable;
	}
	
}
