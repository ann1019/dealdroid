package org.chemlab.dealdroid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shade
 * @version $Id$
 */
public class Utils {

	private static final String RFC822_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
	
	private static final String PRICE_REGEX = "Price.*\\$(\\d+\\.\\d+)";
	
	private static final String REPLACE_HTML_REGEX = "\\<.*?\\>";
	
	/**
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date parseRFC822Date(final String date) throws ParseException {
		final DateFormat f = new SimpleDateFormat(RFC822_DATE_FORMAT);
		return f.parse(date);
	}
	
	/**
	 * @param date
	 * @return
	 */
	public static String formatRFC822Date(final Date date) {
		final DateFormat f = new SimpleDateFormat(RFC822_DATE_FORMAT);
		return f.format(date);
	}
	
	/**
	 * @param item
	 * @return
	 */
	public static String searchForPrice(final String text) {
		
		String price = null;
		if (text != null) {

			final String cleanDesc = text.replaceAll(REPLACE_HTML_REGEX, "");
			if (cleanDesc != null) {
				final Pattern p = Pattern.compile(PRICE_REGEX);
				final Matcher m = p.matcher(cleanDesc);
				if (m.find() && m.groupCount() > 0) {
					price = m.group(1);
				}
			}
		}
		
		return price;
	}
}
