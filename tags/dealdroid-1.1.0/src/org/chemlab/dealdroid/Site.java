package org.chemlab.dealdroid;

import java.net.MalformedURLException;
import java.net.URL;
import android.util.Xml.Encoding;

import org.chemlab.dealdroid.feed.BCFeedHandler;
import org.chemlab.dealdroid.feed.FeedHandler;
import org.chemlab.dealdroid.feed.RSSHandler;

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
			"http://www.avantlink.com/click.php?tt=dotd&ti=12021&pw=14749", Encoding.UTF_8),
			
	BROCIETY("Brociety",
			"Snowboarding",
			"http://www.brociety.com/docs/brociety/rssaff.xml",
			"http://www.brociety.com",
			R.drawable.icon_brociety,
			BCFeedHandler.class, "avad", "14749", false,
			"http://www.avantlink.com/click.php?tt=dotd&ti=13197&pw=14749", Encoding.UTF_8),
			
	CHAINLOVE("Chainlove", 
			"Cycling",
			"http://www.chainlove.com/docs/chainlove/rssaff.xml", 
			"http://www.chainlove.com", 
			R.drawable.icon_chainlove, 
			BCFeedHandler.class, "avad", "14749", false,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8761&pw=14749", Encoding.UTF_8),
			
	STEEPANDCHEAP("Steep and Cheap", 
			"Outdoor Gear",
			"http://www.steepandcheap.com/docs/steepcheap/rssaff.xml", 
			"http://www.steepandcheap.com",
			R.drawable.icon_steepandcheep, 
			BCFeedHandler.class, "avad", "14749", true,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8733&pw=14749", Encoding.UTF_8),
			
	TRAMDOCK("Tramdock", 
			"Skiing",
			"http://www.tramdock.com/docs/tramdock/rssaff.xml", 
			"http://www.tramdock.com", 
			R.drawable.icon_tramdock, 
			BCFeedHandler.class, "avad", "14749", false,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8773&pw=14749", Encoding.UTF_8),
			
	WHISKEYMILITIA("Whiskey Militia", 
			"Snow, Skate, Surf",
			"http://www.whiskeymilitia.com/docs/wm/rssaff.xml", 
			"http://www.whiskeymilitia.com",
			R.drawable.icon_whiskeymilitia, 
			BCFeedHandler.class, "avad", "14749", true,
			"http://www.avantlink.com/click.php?tt=dotd&ti=8801&pw=14749", Encoding.UTF_8),
	
	SLICKDEALS("SlickDeals",
			"Various, Community-Driven",
			"http://feeds.feedburner.com/SlickdealsnetFP?format=xml",
			"http://www.slickdeals.net",
			R.drawable.icon_slickdeals,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8),
					
	THINGFLING("ThingFling",
			"Electronics",
			"http://feed.thingfling.com/ThingflingRssFeed?format=xml",
			"http://www.thingfling.com",
			R.drawable.icon_thingfling,
			RSSHandler.class, null, null, false, null, Encoding.ISO_8859_1),
	
	WOOT("Woot",
			"Anything and Everything",
			"http://www.woot.com/salerss.aspx",
			"http://www.woot.com",
			R.drawable.icon_woot,
			RSSHandler.class, null, null, true, null, Encoding.UTF_8),
	
	WOOTSELLOUT("Woot Sellout",
			"Anything and Everything",
			"http://sellout.woot.com/salerss.aspx",
			"http://sellout.woot.com",
			R.drawable.icon_wootsellout,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8),
			
	WOOTSHIRT("Woot Shirt",
			"T-Shirts",
			"http://shirt.woot.com/salerss.aspx",
			"http://shirt.woot.com",
			R.drawable.icon_wootshirt,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8),
	
	WOOTWINE("Woot Wine",
			"Wine",
			"http://wine.woot.com/salerss.aspx",
			"http://wine.woot.com",
			R.drawable.icon_wootwine,
			RSSHandler.class, null, null, false, null, Encoding.UTF_8);
			
	
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
	
	Site(String name, String category, String url, String site, int drawable, Class<? extends FeedHandler> handler, String affiliationKey, String affiliationValue, boolean enabledByDefault, String clickThru, Encoding encoding) {
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
