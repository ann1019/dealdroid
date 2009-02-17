package org.chemlab.dealdroid.rss;

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

/**
 * SAX event handler which has some idea of the kind of RSS that we are interested in,
 * and creates a sorted map of Item objects.
 * 
 * @author shade
 * @version $Id$
 */
public class RSSHandler extends DefaultHandler {

	private enum ItemTag {
		TITLE, LINK, DESCRIPTION, PRICE, PUBDATE;
	}

	private boolean inItem = false;

	private ItemTag currentTag = null;

	private Item currentItem;

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
		
		if (localName.trim().equals("item")) {
			inItem = true;
			currentItem = new Item();
		} else if (localName.trim().equals("title")) {
			currentTag = ItemTag.TITLE;
		} else if (localName.trim().equals("link")) {
			currentTag = ItemTag.LINK;
		} else if (localName.trim().equals("listDescription")) {
			currentTag = ItemTag.DESCRIPTION;
		} else if (localName.trim().equals("price")) {
			currentTag = ItemTag.PRICE;
		} else if (localName.trim().equals("pubDate")) {
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

		if (localName.trim().equals("item")) {
			inItem = false;
			if (currentItem.getPubDate() != null) {
				final Item clone = (Item)currentItem.clone();
				items.put(clone.getPubDate(), clone);
			}
			
		} else {
			
			if (currentItem != null && currentTag != null) {
				
				final String chars = currentString.toString();
			
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
					currentItem.setPrice(chars);
					break;
				case PUBDATE:
					try {
						currentItem.setPubDate(pubDateFormat.parse(chars));
					} catch (ParseException e) {
						throw new SAXException(e);
					}
					break;
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

			final String chars = (new String(ch).substring(start, start + length)).trim();
						
			if (chars.length() > 0) {
				currentString.append(chars);
			}
		}
	}

	/**
	 * @return the items
	 */
	public SortedMap<Date, Item> getItems() {
		return items;
	}

}
