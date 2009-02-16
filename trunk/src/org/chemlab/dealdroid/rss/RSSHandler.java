package org.chemlab.dealdroid.rss;

import org.chemlab.dealdroid.Item;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.Uri;

/**
 * SAX event handler which has some idea of the kind of RSS that we are interested in,
 * and creates and Item object out of the most recent entry in the feed.
 * 
 * @author shade
 * @version $Id$
 */
public class RSSHandler extends DefaultHandler {

	private enum ItemTag {
		TITLE, LINK, DESCRIPTION, PRICE;
	}

	private boolean inItem = false;

	private boolean firstItemComplete = false;

	private ItemTag currentTag = null;

	private final Item currentItem;

	public RSSHandler(final Item currentItem) {
		this.currentItem = currentItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (!firstItemComplete) {
			if (localName.trim().equals("item")) {
				inItem = true;
			} else if (localName.trim().equals("title")) {
				currentTag = ItemTag.TITLE;
			} else if (localName.trim().equals("link")) {
				currentTag = ItemTag.LINK;
			} else if (localName.trim().equals("listDescription")) {
				currentTag = ItemTag.DESCRIPTION;
			} else if (localName.trim().equals("price")) {
				currentTag = ItemTag.PRICE;
			} else {
				currentTag = null;
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

		if (!firstItemComplete && localName.trim().equals("item")) {
			inItem = false;
			firstItemComplete = true;
			currentTag = null;
		}
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
				}

			}
		}
	}

}
