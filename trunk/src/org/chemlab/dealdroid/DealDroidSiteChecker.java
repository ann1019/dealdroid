package org.chemlab.dealdroid;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.chemlab.dealdroid.DealDroidPreferences.PREFS_NAME;
import static org.chemlab.dealdroid.DealDroidPreferences.isEnabled;

import java.net.URLConnection;
import java.util.EnumMap;
import java.util.Map;

import org.chemlab.dealdroid.rss.RSSHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

/**
 * @author shade
 * @version $Id$
 */
public class DealDroidSiteChecker extends BroadcastReceiver {

	public static String INTENT_CHECK_SITES = "org.chemlab.dealdroid.CHECK_SITES";

	public final Map<DealSite, Item> results = new EnumMap<DealSite, Item>(DealSite.class);

	private boolean isActive = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (INTENT_CHECK_SITES.equals(intent.getAction())) {
			checkSites(context);
		}

	}

	/**
	 * @param context
	 */
	private void checkSites(final Context context) {

		if (!isActive) {
			try {
				isActive = true;

				final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

				for (DealSite site : DealSite.values()) {

					Log.d(this.getClass().getSimpleName(), "Handling " + site);

					if (isEnabled(preferences, site)) {
						try {

							final Item item = new Item();

							final URLConnection conn = site.getUrl().openConnection();
							conn.setConnectTimeout(10000);
							conn.setReadTimeout(60000);

							Xml.parse(conn.getInputStream(), Encoding.UTF_8, new RSSHandler(item));

							if (item.getTitle() != null) {
								notify(site, item, context);
							}

						} catch (Exception e) {
							Log.e(this.getClass().getSimpleName(), e.getMessage());
						}
					} else {
						Log.d(this.getClass().getSimpleName(), "Skipping " + site + " (disabled)");
					}
				}
			} finally {
				isActive = false;
			}

		} else {
			Log.w(this.getClass().getSimpleName(), "Task already running.");
		}
	}

	/**
	 * @param key
	 * @param item
	 */
	private void notify(final DealSite key, final Item item, final Context context) {

		if (item != null) {
			final Item previousItem = results.get(key);
			if (previousItem == null || !previousItem.equals(item)) {

				Log.d(this.getClass().getSimpleName(), "Creating new notification.");

				results.put(key, item);
				
				((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(key.ordinal(), createNotification(key, item, context));

			} else {

				Log.d(this.getClass().getSimpleName(), "Not creating notification.");
			}
		}
	}

	/**
	 * @param key
	 * @param item
	 * @param context
	 * @return
	 */
	private Notification createNotification(final DealSite key, final Item item, final Context context) {
		
		final Notification notification = new Notification(key.getDrawable(), item.getTitle(), System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(Intent.ACTION_VIEW, item.getLink()), 0);
		notification.setLatestEventInfo(context, item.getTitle(), item.getPrice(), contentIntent);

		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

		// Notification options
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		if (preferences.getBoolean(DealDroidPreferences.NOTIFY_VIBRATE, false)) {
			notification.vibrate = new long[] { 100, 250, 100, 500 };
		}

		if (preferences.getBoolean(DealDroidPreferences.NOTIFY_LED, false)) {
			notification.ledARGB = 0xFFFF5171;
			notification.ledOnMS = 100;
			notification.ledOffMS = 100;
			notification.flags = Notification.FLAG_SHOW_LIGHTS;
		}
		
		return notification;

	}
}
