package org.chemlab.dealdroidapp.feed;

import org.chemlab.dealdroidapp.Item;
import org.xml.sax.ContentHandler;

/**
 * To handle feeds, a SAX ContentHandler is used, which should return
 * a single Item when it has completed parsing.  
 * 
 * @author shade
 * @version $Id$
 */
public interface FeedHandler extends ContentHandler {
	
	Item getCurrentItem();
	
}
