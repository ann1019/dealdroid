package org.chemlab.dealdroid;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.chemlab.dealdroid.Preferences.PREFS_NAME;
import static org.chemlab.dealdroid.Preferences.isEnabled;

import java.net.URLConnection;

import org.chemlab.dealdroid.rss.RSSHandler;

import android.app.AlarmManager;
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
 * @version $Id: DealDroidSiteChecker.java 15 2009-02-16 17:06:44Z steve.kondik$
 */
public class SiteChecker extends BroadcastReceiver {

	public static final String BOOT_INTENT = "android.intent.action.BOOT_COMPLETED";

	public static final String DEALDROID_START = "org.chemlab.dealdroid.DEALDROID_START";

	public static final String DEALDROID_STOP = "org.chemlab.dealdroid.DEALDROID_STOP";

	public static final String INTENT_CHECK_SITES = "org.chemlab.dealdroid.CHECK_SITES";

	private static final long UPDATE_INTERVAL = 120000;

	private boolean isActive = false;
	
	private Database database;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		database = new Database(context);
		
		if (INTENT_CHECK_SITES.equals(intent.getAction())) {
			checkSites(context);
		} else if (BOOT_INTENT.equals(intent.getAction()) || DEALDROID_START.equals(intent.getAction())) {
			enable(context);
		} else if (DEALDROID_STOP.equals(intent.getAction())) {
			disable(context);
		}

	}

	/**
	 * @param context
	 */
	private void enable(final Context context) {
		Log.i(this.getClass().getSimpleName(), "Starting DealDroid updater..");
		getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP, 0, UPDATE_INTERVAL,
				getSiteCheckerIntent(context));
	}

	/**
	 * @param context
	 */
	private void disable(final Context context) {
		Log.i(this.getClass().getSimpleName(), "Stopping DealDroid updater..");
		getAlarmManager(context).cancel(getSiteCheckerIntent(context));
	}

	/**
	 * @param context
	 * @return
	 */
	private AlarmManager getAlarmManager(final Context context) {
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * @param context
	 * @return
	 */
	private PendingIntent getSiteCheckerIntent(final Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(SiteChecker.INTENT_CHECK_SITES), 0);
	}

	/**
	 * @param context
	 */
	private void checkSites(final Context context) {

		if (!isActive) {
			try {
				isActive = true;
				
				database.open();
				
				final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

				for (Site site : Site.values()) {

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
							
							Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
						}
					} else {
						Log.d(this.getClass().getSimpleName(), "Skipping " + site + " (disabled)");
					}
				}
			} finally {
				database.close();
				isActive = false;
			}

		} else {
			Log.w(this.getClass().getSimpleName(), "Task already running.");
		}
	}

	/**
	 * @param site
	 * @param item
	 */
	private void notify(final Site site, final Item item, final Context context) {

		if (item != null) {

				if (!database.isItemCurrent(site, item)) {

					Log.d(this.getClass().getSimpleName(), "Creating new notification.");

					database.updateState(site, item);

					((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(site.ordinal(),
							createNotification(site, item, context));

				} else {

					Log.d(this.getClass().getSimpleName(), "Not creating notification.");
				}

		}
	}

	/**
	 * @param site
	 * @param item
	 * @param context
	 * @return
	 */
	private Notification createNotification(final Site site, final Item item, final Context context) {

		final Notification notification = new Notification(site.getDrawable(), item.getTitle(), System.currentTimeMillis());
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(Intent.ACTION_VIEW, item.getLink()), 0);
		
		notification.setLatestEventInfo(context, item.getTitle(), item.getPrice(), contentIntent);

		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

		// Notification options
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		if (preferences.getBoolean(Preferences.NOTIFY_VIBRATE, false)) {
			notification.vibrate = new long[] { 100, 250, 100, 500 };
		}

		if (preferences.getBoolean(Preferences.NOTIFY_LED, false)) {
			notification.ledARGB = 0xFFFF5171;
			notification.ledOnMS = 500;
			notification.ledOffMS = 500;
			notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
		}

		return notification;

	}
}
