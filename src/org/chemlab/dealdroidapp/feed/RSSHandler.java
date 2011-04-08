package org.chemlab.dealdroidapp.feed;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import org.chemlab.dealdroidapp.Item;
import org.chemlab.dealdroidapp.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.Uri;
import android.util.Log;

/**
 * SAX event handler which has some idea of the kind of RSS that we are
 * interested in, and creates a sorted map of Item objects.
 * 
 * @author shade
 * @version $Id$
 */
public class RSSHandler extends DefaultHandler implements FeedHandler {

	private enum ItemTag {
		
		TITLE("title"), 
		LINK("link"), 
		DESCRIPTION("description"), 
		PRICE("price"), 
		PUBDATE("pubdate"), 
		IMAGE_LINK("thumbnailimage"), 
		SHORT_DESCRIPTION("subtitle"), 
		WOOTOFF("wootoff");
		
		final String[] tags;

		ItemTag(String... tags) {
			this.tags = tags;
		}

		public boolean matches(final String text) {
			boolean ret = false;
			for (String tag : tags) {
				if (tag.equalsIgnoreCase(text)) {
					ret = true;
					break;
				}
			}
			return ret;
		}
	}

	private final String LOG_TAG = this.getClass().getSimpleName();
	
	private boolean inItem = false;

	private ItemTag currentTag = null;

	private Item currentItem;

	private Date currentItemDate;

	private StringBuilder currentString;

	private final TreeMap<Date, Item> items = new TreeMap<Date, Item>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		currentString = new StringBuilder();
		
		final String tag = localName.trim();
		
		if (tag.equalsIgnoreCase("item")) {
			
			inItem = true;
			currentItem = new Item();
			
		} else {
			
			currentTag = null;
			for (ItemTag itemTag : ItemTag.values()) {
				if (itemTag.matches(tag)) {
					currentTag = itemTag;
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (inItem && currentItem != null) {
			if (localName.trim().equalsIgnoreCase("item")) {
							
				inItem = false;
				if (currentItemDate != null) {
					final Item clone = (Item) currentItem.clone();
					items.put((Date) currentItemDate.clone(), clone);
					currentItemDate = null;
				}

			} else if (currentTag != null) {

				final String chars = currentString.toString().trim();

				if (chars != null) {
					switch (currentTag) {
					case TITLE:
						currentItem.setTitle(chars);
						break;
					case LINK:
						currentItem.setLink(Uri.parse(chars));
						break;
					case IMAGE_LINK:
						currentItem.setImageLink(Uri.parse(chars));
						break;
					case DESCRIPTION:
						currentItem.setDescription(chars);
						break;
					case PRICE:
						currentItem.setSalePrice(chars);
						break;
					case SHORT_DESCRIPTION:
						currentItem.setShortDescription(chars);
						break;
					case WOOTOFF:
						// if there is no woot-off, force an expiration
						if (chars.equalsIgnoreCase("false")) {
							final Calendar c = Calendar.getInstance();
							c.add(Calendar.HOUR_OF_DAY, 1);
							currentItem.setExpiration(c.getTime());
						}
						break;
					case PUBDATE:
						try {
							currentItemDate = Utils.parseRFC822Date(chars);
						} catch (ParseException e) {

							// BC likes to just send "MDT" sometimes as the pubDate
							Log.e(LOG_TAG, "[data: " + chars + "] " + e.getMessage(), e);
						}
						break;
					}
				}

			}
		}
		currentTag = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (inItem && currentTag != null) {
			final String chars = new String(ch).substring(start, start + length);
			if (chars.length() > 0) {
				currentString.append(chars);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chemlab.dealdroid.feed.FeedHandler#getCurrentItem()
	 */
	@Override
	public Item getCurrentItem() {
		final Item ret = items.size() == 0 ? null : items.get(items.lastKey());
		if (ret != null && ret.getSalePrice() == null) {
			ret.setSalePrice(Utils.searchForPrice(ret.getDescription()));
		}
		return ret;
	}

	
	
}
