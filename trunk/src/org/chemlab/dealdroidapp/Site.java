package org.chemlab.dealdroidapp;

import java.net.MalformedURLException;
import java.net.URL;
import android.util.Xml.Encoding;

import org.chemlab.dealdroidapp.feed.BCFeedHandler;
import org.chemlab.dealdroidapp.feed.FeedHandler;
import org.chemlab.dealdroidapp.feed.RSSHandler;

import android.net.Uri;

/**
 * The various sites, RSS URLs, and associated icons.
 * 
 * @author shade
 * @version $Id$
 */
public enum Site {

	BONKTOWN("Bonktown",
			"Cycling",
			"http://www.bonktown.com/docs/bonktown/rssaff.xml",
			"http://www.bonktown.com",
			R.drawable.icon_bonktown,
			BCFeedHandler.class, "avad", "14749", false,
			"http://www.avantlink.com/click.php?tt=dotd&ti=12021&pw=14749", Encoding.UTF_8, false),
			
	CHAINLOVE("Chainlove", 
			"Cycling",
			"http://www.chainlove.com/docs/chainlove/rssaff.xml", 
			"http://www.chainlove.com", 
			R.drawable.icon_chainlove, 
			BCFeedHandler.class, "avad", "14749", false,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8761&pw=14749", Encoding.UTF_8, false),
			
	STEEPANDCHEAP("Steep and Cheap", 
			"Outdoor Gear",
			"http://www.steepandcheap.com/docs/steepcheap/rssaff.xml", 
			"http://www.steepandcheap.com",
			R.drawable.icon_steepandcheep, 
			BCFeedHandler.class, "avad", "14749", true,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8733&pw=14749", Encoding.UTF_8, false),
			
	TRAMDOCK("Tramdock", 
			"Skiing",
			"http://www.tramdock.com/docs/tramdock/rssaff.xml", 
			"http://www.tramdock.com", 
			R.drawable.icon_tramdock, 
			BCFeedHandler.class, "avad", "14749", false,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8773&pw=14749", Encoding.UTF_8, false),
			
	WHISKEYMILITIA("Whiskey Militia", 
			"Snow, Skate, Surf",
			"http://www.whiskeymilitia.com/docs/wm/rssaff.xml", 
			"http://www.whiskeymilitia.com",
			R.drawable.icon_whiskeymilitia, 
			BCFeedHandler.class, "avad", "14749", true,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8801&pw=14749", Encoding.UTF_8, false),
	
	SLICKDEALS("SlickDeals",
			"Various, Community-Driven",
			"http://feeds.feedburner.com/SlickdealsnetFP?format=xml",
			"http://www.slickdeals.net",
			R.drawable.icon_slickdeals,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8, false),
					
	THINGFLING("ThingFling",
			"Electronics",
			"http://feed.thingfling.com/ThingflingRssFeed?format=xml",
			"http://www.thingfling.com",
			R.drawable.icon_thingfling,
			RSSHandler.class, null, null, false, null, Encoding.ISO_8859_1, false),
	
	WOOT("Woot",
			"Anything and Everything",
			"http://www.woot.com/salerss.aspx",
			"http://www.woot.com",
			R.drawable.icon_woot,
			RSSHandler.class, null, null, true, null, Encoding.UTF_8, false),
	
	WOOTSELLOUT("Woot Sellout",
			"Anything and Everything",
			"http://sellout.woot.com/salerss.aspx",
			"http://shopping.yahoo.com/#woot",
			R.drawable.icon_wootsellout,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8, true),
			
	WOOTSHIRT("Woot Shirt",
			"T-Shirts",
			"http://shirt.woot.com/salerss.aspx",
			"http://shirt.woot.com",
			R.drawable.icon_wootshirt,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8, false),
	
	WOOTWINE("Woot Wine",
			"Wine",
			"http://wine.woot.com/salerss.aspx",
			"http://wine.woot.com",
			R.drawable.icon_wootwine,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8, false);
			
	
	private final String name;
	
	private final String category;
	
	private final URL url;
	
	private final URL site;
	
	private final int drawable;
	
	private final Class<? extends FeedHandler> handler;
	
	private final String affiliationKey;
	
	private final String affiliationValue;
	
	private final boolean enabledByDefault;
	
	private final URL clickThru;
	
	private final Encoding encoding;
	
	private boolean forceUrl;
	
	Site(String name, String category, String url, String site, int drawable, Class<? extends FeedHandler> handler, String affiliationKey, String affiliationValue, 
			boolean enabledByDefault, String clickThru, Encoding encoding, boolean forceUrl) {
		try {
			this.name = name;
			this.category = category;
			this.url = new URL(url);
			this.site = new URL(site);
			this.drawable = drawable;
			this.handler = handler;
			this.affiliationKey = affiliationKey;
			this.affiliationValue = affiliationValue;
			this.enabledByDefault = enabledByDefault;
			this.clickThru = clickThru == null ? null : new URL(clickThru);
			this.encoding = encoding;
			this.forceUrl = forceUrl;
			
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
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

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the enabledByDefault
	 */
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}

	/**
	 * @return the clickThru
	 */
	public URL getClickThru() {
		return clickThru;
	}

	/**
	 * @return the encoding
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/**
	 * @return the forceUrl
	 */
	public boolean isForceUrl() {
		return forceUrl;
	}

	/**
	 * @param forceUrl the forceUrl to set
	 */
	public void setForceUrl(boolean forceUrl) {
		this.forceUrl = forceUrl;
	}

	/**
	 * @param uri
	 * @return the uri with affiliation applied
	 */
	public Uri applyAffiliation(final Uri uri) {
		
		final Uri link;
		if (getClickThru() != null) {
			link = Uri.parse(getClickThru().toString()).buildUpon().appendQueryParameter("url", uri.toString()).build();
		} else if (getAffiliationKey() != null && getAffiliationValue() != null) {
			link = uri.buildUpon().appendQueryParameter(getAffiliationKey(), getAffiliationValue()).build();
		} else {
			link = uri;
		} 
				
		return link;
	}
}
