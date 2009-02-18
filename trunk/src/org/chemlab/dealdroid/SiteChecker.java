package org.chemlab.dealdroid;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.chemlab.dealdroid.Preferences.KEEP_AWAKE;
import static org.chemlab.dealdroid.Preferences.PREFS_NAME;
import static org.chemlab.dealdroid.Preferences.isEnabled;

import java.net.URLConnection;

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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
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

	public static final String DEALDROID_RESTART = "org.chemlab.dealdroid.DEALDROID_RESTART";
	
	public static final String DEALDROID_UPDATE = "org.chemlab.dealdroid.DEALDROID_UPDATE";
	
	public static final String DEALDROID_ENABLE = "org.chemlab.dealdroid.DEALDROID_ENABLE";
	
	public static final String DEALDROID_DISABLE = "org.chemlab.dealdroid.DEALDROID_DISABLE";
	
	private static final long UPDATE_INTERVAL = 180000;
	
	private WakeLock wakeLock;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
				
		final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DealDroid");
		
		if (DEALDROID_ENABLE.equals(intent.getAction())) {
			final Site site = Site.valueOf(intent.getExtras().getString("site"));
			if (site != null) {
				checkSites(context, site);
			}
			
		} else if (DEALDROID_DISABLE.equals(intent.getAction())) {
			final Site site = Site.valueOf(intent.getExtras().getString("site"));
			if (site != null) {
				disableSite(context, site);
			}
			
		} else if (BOOT_INTENT.equals(intent.getAction()) || DEALDROID_START.equals(intent.getAction())) {
			enable(context);
			
		} else if (DEALDROID_STOP.equals(intent.getAction())) {
			disable(context);
			
		} else if (DEALDROID_RESTART.equals(intent.getAction())) {
			disable(context);
			enable(context);
			
		} else if (DEALDROID_UPDATE.equals(intent.getAction())) {
			checkSites(context, Site.values());
		}
	}

	/**
	 * @param site
	 */
	private void disableSite(final Context context, final Site site) {
		Log.i(this.getClass().getSimpleName(), "Deleting data for site: " + site.toString());
		final Database db = new Database(context);
		db.open();
		db.delete(site);
		db.close();
	}
	
	/**
	 * Checks the given sites for new items (only if the network is up).
	 * 
	 * @param context
	 */
	private void checkSites(final Context context, final Site... sites) {
		
		final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = cm.getActiveNetworkInfo();
		
		if (info != null && info.isAvailable()) {
			final Thread checker = new SiteCheckerThread(context, sites);
			checker.start();
		}
	}
	
	/**
	 * @param context
	 */
	private synchronized void enable(final Context context) {
		Log.i(this.getClass().getSimpleName(), "Starting DealDroid updater..");
		
		final int mode = shouldKeepPhoneAwake(context) ? AlarmManager.ELAPSED_REALTIME_WAKEUP : AlarmManager.ELAPSED_REALTIME;
		getAlarmManager(context).setRepeating(mode, 0, UPDATE_INTERVAL,	getSiteCheckerIntent(context));
	}

	/**
	 * @param context
	 */
	private synchronized void disable(final Context context) {
		Log.i(this.getClass().getSimpleName(), "Stopping DealDroid updater..");
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
		return PendingIntent.getBroadcast(context, 0, new Intent(SiteChecker.DEALDROID_UPDATE), 0);
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
	private class SiteCheckerThread extends Thread {

		final Context context;
		final Database database;
		final SharedPreferences preferences;
		final Site[] sites;
		
		SiteCheckerThread(final Context context, Site... sites) {
			this.context = context;
			this.sites = sites;
			this.database = new Database(context);
			this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		}
		
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
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

			for (Site site : sites) {

				Log.d(this.getClass().getSimpleName(), "Handling " + site);

				if (isEnabled(preferences, site)) {

					try {

						final URLConnection conn = site.getUrl().openConnection();
						conn.setConnectTimeout(10000);
						conn.setReadTimeout(60000);

						final FeedHandler handler = site.getHandler().newInstance();
						
						Xml.parse(conn.getInputStream(), Encoding.UTF_8, handler);

						notify(site, handler.getCurrentItem());
						
						
					} catch (Exception e) {

						Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
					}

				} else {

					Log.d(this.getClass().getSimpleName(), "Skipping " + site + " (disabled)");

				}
			}

		}

		/**
		 * @param site
		 * @param item
		 */
		private synchronized void notify(final Site site, final Item item) {

			if (item != null && item.getTitle() != null) {

				if (database.updateStateIfNotCurrent(site, item)) {

					Log.i(this.getClass().getSimpleName(), "Creating new notification for " + site.name());
					((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(site.ordinal(), createNotification(site, item));

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
		private Notification createNotification(final Site site, final Item item) {

			final Notification notification = new Notification(site.getDrawable(), item.getTitle(), System
					.currentTimeMillis());
			final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(Intent.ACTION_VIEW,
					item.getLink()), 0);

			notification.setLatestEventInfo(context, item.getTitle(), item.getPrice(), contentIntent);

			notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

			// Notification options
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
}
