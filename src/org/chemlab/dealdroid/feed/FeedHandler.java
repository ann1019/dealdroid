package org.chemlab.dealdroid.feed;

import org.chemlab.dealdroid.Item;
import org.xml.sax.ContentHandler;

/**
 * @author shade
 * @version $Id$
 */
public interface FeedHandler extends ContentHandler {
	
	public Item getCurrentItem();
	
}
