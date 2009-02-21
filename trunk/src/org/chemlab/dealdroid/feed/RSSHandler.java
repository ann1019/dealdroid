package org.chemlab.dealdroid.feed;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.chemlab.dealdroid.Item;
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
		TITLE, LINK, DESCRIPTION, PRICE, PUBDATE;
	}

	private boolean inItem = false;

	private ItemTag currentTag = null;

	private Item currentItem;

	private Date currentItemDate;

	private StringBuilder currentString;

	private final DateFormat pubDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private final SortedMap<Date, Item> items = new TreeMap<Date, Item>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		currentString = new StringBuilder();
		
		final String tag = localName.trim().toLowerCase();
		
		if (tag.equals("item")) {
			inItem = true;
			currentItem = new Item();
		} else if (tag.equals("title")) {
			currentTag = ItemTag.TITLE;
		} else if (tag.equals("link")) {
			currentTag = ItemTag.LINK;
		} else if (tag.equals("listDescription")) {
			currentTag = ItemTag.DESCRIPTION;
		} else if (tag.equals("price")) {
			currentTag = ItemTag.PRICE;
		} else if (tag.equals("pubDate")) {
			currentTag = ItemTag.PUBDATE;
		} else {
			currentTag = null;
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

		if (currentItem != null && localName.trim().equals("item")) {
			inItem = false;
			if (currentItemDate != null) {
				final Item clone = (Item) currentItem.clone();
				items.put((Date) currentItemDate.clone(), clone);
				currentItemDate = null;
			}

		} else if (currentItem != null && currentTag != null) {

			final String chars = currentString.toString().trim();

			switch (currentTag) {
			case TITLE:
				currentItem.setTitle(chars);
				break;
			case LINK:
				currentItem.setLink(Uri.parse(chars));
				break;
			case DESCRIPTION:
				currentItem.setDescription(chars);
				break;
			case PRICE:
				currentItem.setSalePrice(chars);
				break;
			case PUBDATE:
				try {
					currentItemDate = pubDateFormat.parse(chars);
				} catch (ParseException e) {

					// BC likes to just send "MDT" sometimes as the pubDate
					Log.e(this.getClass().getSimpleName(), e.getMessage());
				}
				break;
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

			final String chars = (new String(ch).substring(start, start + length));

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
		return items.size() == 0 ? null : items.get(items.lastKey());
	}

}
