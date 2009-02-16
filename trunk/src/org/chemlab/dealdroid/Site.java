package org.chemlab.dealdroid;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The various sites, RSS URLs, and associated icons.
 * 
 * @author shade
 * @version $Id$
 */
public enum Site {
	
	WHISKEYMILITIA("Whiskey Militia", "http://www.whiskeymilitia.com/docs/wm/rssplus.xml", R.drawable.icon_whiskeymilitia),
	STEEPANDCHEAP("Steep and Cheap", "http://www.steepandcheap.com/docs/steepcheap/rssplus.xml", R.drawable.icon_steepandcheep),
	CHAINLOVE("Chain Love", "http://www.chainlove.com/docs/chainlove/rssplus.xml", R.drawable.icon_chainlove),
	TRAMDOCK("Tramdock", "http://www.tramdock.com/docs/tramdock/rssplus.xml", R.drawable.icon_tramdock);
	
	private final String name;
	
	private final URL url;
	
	private final int drawable;
	
	Site(String name, String url, int drawable) {
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
