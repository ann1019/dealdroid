package org.chemlab.dealdroid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shade
 * @version $Id$
 */
public class Utils {

	public static final String RFC822_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
	
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
}
