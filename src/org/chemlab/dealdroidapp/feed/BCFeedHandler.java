package org.chemlab.dealdroidapp.feed;

import org.chemlab.dealdroidapp.Item;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.Uri;

/**
 * A parser that understands Backcountry.com's affiliate feeds.
 * 
 * @author shade
 * @version $Id$
 */
public class BCFeedHandler extends DefaultHandler implements FeedHandler {

	private enum ItemTag {
		TITLE("product_name"), 
		LINK("buy_url"), 
		DESCRIPTION("long_description"), 
		RETAIL_PRICE("retail_price"), 
		SALE_PRICE("sale_price"), 
		IMAGE_LINK("small_image_url");
		
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
	
	private boolean inItem = false;

	private ItemTag currentTag = null;

	private final Item currentItem = new Item();
	
	private StringBuilder currentString;
	
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
		
		if (tag.equalsIgnoreCase("product")) {
			
			inItem = true;
			
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
		
		final String tag = localName.trim();
		
		if (tag.equalsIgnoreCase("product")) {
			
			inItem = false;
			
			final double sp = Double.valueOf(currentItem.getSalePrice());
			final double rp = Double.valueOf(currentItem.getRetailPrice());
			if (sp > 0 && rp > 0 && rp >= sp) {
				final int discount = (int)(100 * ( 1 - (sp / rp)));		
				currentItem.setSavings(String.valueOf(discount));
			}
			
		} else if (currentTag != null) {

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
			case IMAGE_LINK:
				currentItem.setImageLink(Uri.parse(chars));
				break;
			case RETAIL_PRICE:
				currentItem.setRetailPrice(chars);
				break;
			case SALE_PRICE:
				currentItem.setSalePrice(chars);
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

			final String chars = new String(ch).substring(start, start + length);
						
			if (chars.length() > 0) {
				currentString.append(chars);
			}
		}
	}

	/**
	 * @return the currentItem
	 */
	public Item getCurrentItem() {
		return currentItem;
	}
	
}
