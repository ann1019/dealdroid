package org.chemlab.dealdroid;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.chemlab.dealdroid.Intents.BOOT_INTENT;
import static org.chemlab.dealdroid.Intents.DEALDROID_DISABLE;
import static org.chemlab.dealdroid.Intents.DEALDROID_ENABLE;
import static org.chemlab.dealdroid.Intents.DEALDROID_RESTART;
import static org.chemlab.dealdroid.Intents.DEALDROID_START;
import static org.chemlab.dealdroid.Intents.DEALDROID_STOP;
import static org.chemlab.dealdroid.Intents.DEALDROID_UPDATE;
import static org.chemlab.dealdroid.Preferences.CHECK_INTERVAL;
import static org.chemlab.dealdroid.Preferences.KEEP_AWAKE;
import static org.chemlab.dealdroid.Preferences.NOTIFY_LED;
import static org.chemlab.dealdroid.Preferences.NOTIFY_RINGTONE;
import static org.chemlab.dealdroid.Preferences.NOTIFY_VIBRATE;
import static org.chemlab.dealdroid.Preferences.PREFS_NAME;
import static org.chemlab.dealdroid.Preferences.getNumSitesEnabled;
import static org.chemlab.dealdroid.Preferences.isEnabled;

import java.io.InputStream;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.chemlab.dealdroid.feed.FeedHandler;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.Xml;

/**
 * BroadcastReceiver that deals with various Intents, such as updating sites,
 * managing the alarms. The actual checkers will run in separate threads, and
 * take care of acquiring WakeLocks while running. Notifications are sent when
 * new items appear, and clicking on these notifications launches an ItemViewer.
 * 
 * @author shade
 * @version $Id: DealDroidSiteChecker.java 15 2009-02-16 17:06:44Z steve.kondik$
 */
public class SiteChecker extends BroadcastReceiver {

	private final String LOG_TAG = this.getClass().getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		if (DEALDROID_ENABLE.getAction().equals(intent.getAction())) {
			final Site site = Site.valueOf(intent.getExtras().getString("site"));

			if (site != null) {
				if (getNumSitesEnabled(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)) == 1) {
					enable(context);
				} else {
					checkSites(context, site);
				}
			}

		} else if (DEALDROID_DISABLE.getAction().equals(intent.getAction())) {
			final Site site = Site.valueOf(intent.getExtras().getString("site"));
			if (site != null) {
				disableSite(context, site);
			}

		} else if (BOOT_INTENT.getAction().equals(intent.getAction())
				|| DEALDROID_START.getAction().equals(intent.getAction())) {
			disable(context);
			enable(context);

		} else if (DEALDROID_STOP.getAction().equals(intent.getAction())) {
			disable(context);

		} else if (DEALDROID_RESTART.getAction().equals(intent.getAction())) {
			disable(context);
			enable(context);

		} else if (DEALDROID_UPDATE.getAction().equals(intent.getAction())) {
			checkSites(context, Site.values());
		}

	}

	/**
	 * @param site
	 */
	private void disableSite(final Context context, final Site site) {
		Log.d(LOG_TAG, "Deleting data for site: " + site.name());
		final Database db = new Database(context);
		try {
			db.open();
			db.delete(site);
		} finally {
			db.close();
		}
		if (getNumSitesEnabled(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)) == 0) {
			Log.d(LOG_TAG, "Checking for all sites disabled.  Disabling alarm..");
			disable(context);
		}
	}

	/**
	 * Checks the given sites for new items (only if the network is up).
	 * 
	 * @param context
	 */
	private void checkSites(final Context context, final Site... sites) {

		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null && info.isAvailable()) {
			final Database db = new Database(context);
			final Map<Site, Item> sitesToCheck = new EnumMap<Site, Item>(Site.class);

			try {
				db.open();
				final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				for (Site site : sites) {
					if (isEnabled(prefs, site)) {
						final Item oldItem = db.getCurrentItem(site);
						if (oldItem == null || oldItem.getExpiration() == null || oldItem.getExpiration().before(new Date())) {
							Log.d(LOG_TAG, "Skipping update for " + site.name() + " (expiration: " + oldItem.getExpiration().getTime() + ")");
						} else {
							sitesToCheck.put(site, oldItem);

						}
					}
				}
			} finally {
				db.close();
			}
			if (sitesToCheck.size() > 0) {
				final Thread checker = new SiteCheckerThread(context, sitesToCheck);
				checker.setDaemon(true);
				checker.start();
			}
		}
	}

	/**
	 * @param context
	 */
	private synchronized void enable(final Context context) {

		final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		if (getNumSitesEnabled(prefs) > 0) {
			Log.i(LOG_TAG, "Starting DealDroid updater..");

			final Interval interval = Interval.valueOf(prefs.getString(CHECK_INTERVAL, Interval.I_2_MINUTES.getName()));

			final int mode = shouldKeepPhoneAwake(context) ? AlarmManager.ELAPSED_REALTIME_WAKEUP : AlarmManager.ELAPSED_REALTIME;
			getAlarmManager(context).setRepeating(mode, 0, interval.getMillis(), getSiteCheckerIntent(context));
		} else {
			Log.i(LOG_TAG, "Not starting updater (no sites enabled)");
		}
	}

	/**
	 * @param context
	 */
	private synchronized void disable(final Context context) {
		Log.i(LOG_TAG, "Stopping DealDroid updater..");
		getAlarmManager(context).cancel(getSiteCheckerIntent(context));
	}

	/**
	 * @param context
	 * @return
	 */
	private static AlarmManager getAlarmManager(final Context context) {
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * @param context
	 * @return
	 */
	private static PendingIntent getSiteCheckerIntent(final Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(DEALDROID_UPDATE.getAction()), 0);
	}

	/**
	 * @param context
	 * @return
	 */
	private static boolean shouldKeepPhoneAwake(final Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(KEEP_AWAKE, false);
	}

	/**
	 * @author shade
	 * 
	 */
	private static class SiteCheckerThread extends Thread {

		private final Context context;
		private final Database database;
		private final SharedPreferences preferences;
		private final WakeLock wakeLock;
		private final DefaultHttpClient httpClient = new DefaultHttpClient();

		private final Map<Site, Item> sitesToCheck;

		private final String LOG_TAG = this.getClass().getSimpleName();

		SiteCheckerThread(final Context context, final Map<Site, Item> sitesToCheck) {
			this.context = context;
			this.sitesToCheck = sitesToCheck;
			this.database = new Database(context);
			this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			this.httpClient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 5000);
			this.httpClient.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, 5000);

			HttpClientGzipSupport.enableCompression(httpClient);

			final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DealDroid");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {

			// Get the WakeLock, open the Database, do our job and clean up.
			if (wakeLock != null) {
				wakeLock.acquire();
			}

			try {
				database.open();
				checkSites();
			} finally {
				database.close();
				if (wakeLock != null) {
					wakeLock.release();
				}
			}
		}

		/**
		 * @param context
		 */
		private void checkSites() {

			Log.d(LOG_TAG, "Updating sites: " + sitesToCheck.keySet().toString());
			
			for (Map.Entry<Site, Item> entry : sitesToCheck.entrySet()) {

				final Site site = entry.getKey();
				final Item oldItem = entry.getValue();

				try {

					final HttpGet req = new HttpGet(site.getUrl().toURI());

					// Make sure we bypass caches
					req.addHeader("Cache-Control", "no-cache");
					req.addHeader("Pragma", "no-cache");

					// Be as nice as possible to the remote server
					if (oldItem != null) {
						final Date lastModified = oldItem.getTimestamp();
						if (lastModified != null) {
							req.addHeader("If-Modified-Since", Utils.formatRFC822Date(lastModified));
						}
					}

					final HttpResponse response = httpClient.execute(req);

					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

						final FeedHandler handler = site.getHandler().newInstance();

						final InputStream in = response.getEntity().getContent();
						Xml.parse(in, site.getEncoding(), handler);
						in.close();

						notify(site, handler.getCurrentItem());

					} else {
						Log.e(LOG_TAG, "HTTP request for " + site.name() + " failed: " + response.getStatusLine().toString());
					}

				} catch (Throwable e) {

					Log.e(LOG_TAG, e.getMessage(), e);
				}
			}

		}

		/**
		 * @param site
		 * @param item
		 */
		private void notify(final Site site, final Item item) {

			if (item != null && item.getTitle() != null) {

				if (database.updateStateIfNotCurrent(site, item)) {

					Log.i(LOG_TAG, "Creating new notification for " + site.name());
					((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(site.ordinal(), createNotification(site, item));

				} 
				
			} else if (item == null) {
				Log.e(LOG_TAG, "Item was null!");
			} else {
				Log.e(LOG_TAG, "Incomplete item object, not notifying.");
			}
		}

		/**
		 * @param site
		 * @param item
		 * @param context
		 * @return
		 */
		private Notification createNotification(final Site site, final Item item) {

			final Notification notification = new Notification(site.getDrawable(), item.getTitle(), System.currentTimeMillis());

			final Uri link = site.applyAffiliation(item.getLink());

			final Intent i;

			// Use the built-in ItemViewer if we have a template for it..
			if (Utils.hasSiteAsset(context, site)) {
				i = new Intent(context, ItemViewer.class);
			} else {
				i = new Intent(Intent.ACTION_VIEW);
			}
			i.setData(link);
			i.putExtra("site", site.name());

			final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, 0);

			final String summary;
			if (item.getSalePrice() != null && item.getSavings() != null) {
				summary = "$" + item.getSalePrice() + " (" + item.getSavings() + "% Off! Regularly: $" + item.getRetailPrice() + ")";
			} else if (item.getSalePrice() != null && item.getShortDescription() != null) {
				summary = item.getSalePrice() + " - " + item.getShortDescription();
			} else if (item.getSalePrice() != null) {
				summary = "$" + item.getSalePrice() + " - " + site.getName();
			} else {
				summary = null;
			}

			if (summary == null) {
				notification.setLatestEventInfo(context, site.getName(), item.getTitle(), contentIntent);
			} else {
				notification.setLatestEventInfo(context, item.getTitle(), summary, contentIntent);
			}

			notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

			// Notification options
			if (preferences.getBoolean(NOTIFY_VIBRATE, false)) {
				notification.vibrate = new long[] { 100, 250, 100, 500 };
			}

			final String ringtone = preferences.getString(NOTIFY_RINGTONE, "");
			if (!ringtone.equals("")) {
				notification.sound = Uri.parse(ringtone);
			}

			if (preferences.getBoolean(NOTIFY_LED, false)) {
				notification.ledARGB = 0xFFFF5171;
				notification.ledOnMS = 500;
				notification.ledOffMS = 500;
				notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
			}

			return notification;

		}

	}

}
