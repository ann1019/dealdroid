package org.chemlab.dealdroid;

import java.net.MalformedURLException;
import java.net.URL;

import org.chemlab.dealdroid.feed.BCFeedHandler;
import org.chemlab.dealdroid.feed.FeedHandler;

/**
 * The various sites, RSS URLs, and associated icons.
 * 
 * @author shade
 * @version $Id$
 */
public enum Site {
	
	WHISKEYMILITIA("Whiskey Militia", 
			"http://www.whiskeymilitia.com/docs/wm/rssaff.xml", 
			"http://www.whiskeymilitia.com",
			R.drawable.icon_whiskeymilitia, 
			BCFeedHandler.class, "avad", "14749"),
	STEEPANDCHEAP("Steep and Cheap", 
			"http://www.steepandcheap.com/docs/steepcheap/rssaff.xml", 
			"http://www.steepandcheap.com",
			R.drawable.icon_steepandcheep, 
			BCFeedHandler.class, "avad", "14749"),
	CHAINLOVE("Chain Love", 
			"http://www.chainlove.com/docs/chainlove/rssaff.xml", 
			"http://www.chainlove.com", 
			R.drawable.icon_chainlove, 
			BCFeedHandler.class, "avad", "14749"),
	TRAMDOCK("Tramdock", 
			"http://www.tramdock.com/docs/tramdock/rssaff.xml", 
			"http://www.tramdock.com", 
			R.drawable.icon_tramdock, 
			BCFeedHandler.class, "avad", "14749"),
	BONKTOWN("Bonktown",
			"http://www.bonktown.com/docs/bonktown/rssaff.xml",
			"http://www.bonktown.com",
			R.drawable.icon_bonktown,
			BCFeedHandler.class, "avad", "14749");
	
	private final String name;
	
	private final URL url;
	
	private final URL site;
	
	private final int drawable;
	
	private final Class<? extends FeedHandler> handler;
	
	private final String affiliationKey;
	
	private final String affiliationValue;
	
	Site(String name, String url, String site, int drawable, Class<? extends FeedHandler> handler, String affiliationKey, String affiliationValue) {
		try {
			this.name = name;
			this.url = new URL(url);
			this.site = new URL(site);
			this.drawable = drawable;
			this.handler = handler;
			this.affiliationKey = affiliationKey;
			this.affiliationValue = affiliationValue;
			
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
	 * @return the site
	 */
	public URL getSite() {
		return site;
	}

	/**
	 * @return the drawable
	 */
	public int getDrawable() {
		return drawable;
	}

	/**
	 * @return the handler
	 */
	public Class<? extends FeedHandler> getHandler() {
		return handler;
	}

	/**
	 * @return the affiliationKey
	 */
	public String getAffiliationKey() {
		return affiliationKey;
	}

	/**
	 * @return the affiliationValue
	 */
	public String getAffiliationValue() {
		return affiliationValue;
	}

}
