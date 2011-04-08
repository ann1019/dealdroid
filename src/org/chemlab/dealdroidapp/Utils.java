package org.chemlab.dealdroidapp;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

/**
 * @author shade
 * @version $Id$
 */
public class Utils {
	
	private static final String LOG_TAG = "DealDroidUtils";
	
	private static final String RFC822_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	private static final String PRICE_REGEX = "Price.*\\$(\\d+\\.\\d+)";

	private static final String REPLACE_HTML_REGEX = "\\<.*?\\>";

	private static final DateFormat formatter = new SimpleDateFormat(RFC822_DATE_FORMAT);
	
	/**
	 * Parses an RFC822 formatted date string.
	 * 
	 * @param date
	 * @return the parsed Date object
	 * @throws ParseException
	 */
	public static Date parseRFC822Date(final String date) throws ParseException {
		return formatter.parse(date);
	}

	/**
	 * Formats a Date according to RFC822.
	 * 
	 * @param date
	 * @return the Date object formatted according to RFC822
	 */
	public static String formatRFC822Date(final Date date) {
		return formatter.format(date);
	}

	/**
	 * Attempts to extract the price of an item from a long description of it
	 * using a regex and HTML stripping.
	 * 
	 * @param text
	 * @return the price of the item
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
	
	/**
	 * Do we have a custom template for the site preview?
	 * 
	 * @param site
	 * @return
	 */
	public static boolean hasSiteAsset(final Context context, final Site site) {
		boolean ret = false;
		final String siteAsset = site.name().toLowerCase(Locale.getDefault()) + ".html";
		try {
			for (String asset : context.getAssets().list("")) {
				if (siteAsset.equals(asset)) {
					ret = true;
					break;
				}
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return ret;
	}
	
	
}
