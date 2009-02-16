package org.chemlab.dealdroid;
import java.net.URLConnection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.chemlab.dealdroid.rss.RSSHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import android.widget.Toast;

/**
 * @author shade
 * @version $Id$
 */
public class RSSCheckerService extends Service {

	private Timer timer;

	public final Map<DealSite, Item> RESULTS = new EnumMap<DealSite, Item>(DealSite.class);

	private static final long updateInterval = 60000;


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		final SharedPreferences preferences = getSharedPreferences(DealDroidPreferences.PREFS_NAME, MODE_PRIVATE);
		preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.startsWith(DealDroidPreferences.ENABLED)) {
					stopService();
					startService();
				}
				
			}
			
		});
		
		Toast.makeText(this, "DealDroid service started.", Toast.LENGTH_SHORT).show();
		
		startService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService();
		Toast.makeText(this, "DealDroid service stopped.", Toast.LENGTH_SHORT).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 
	 */
	private void startService() {
		
		if (timer != null) {
			timer.cancel();
		}

		timer = new Timer();
		
		final SharedPreferences preferences = getSharedPreferences(DealDroidPreferences.PREFS_NAME, MODE_PRIVATE);
		timer.scheduleAtFixedRate(new RSSCheckerTask(preferences, (NotificationManager) getSystemService(NOTIFICATION_SERVICE)), 0, updateInterval);

	}

	/**
	 * 
	 */
	private void stopService() {
		if (timer != null) {
			timer.cancel();
		}			
	}

	/**
	 * @author shade
	 */
	private final class RSSCheckerTask extends TimerTask {

		private boolean isActive = false;
		
		private final SharedPreferences preferences;
		
		private final NotificationManager notificationManager;
		
		public RSSCheckerTask(SharedPreferences preferences, NotificationManager notificationManager) {
			this.preferences = preferences;
			this.notificationManager = notificationManager;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {

			if (!isActive) {
				try {
					isActive = true;
					for (DealSite site : DealSite.values()) {

						Log.i(this.getClass().getName(), "Handling " + site);

						if (preferences.getBoolean(DealDroidPreferences.ENABLED + site.toString(), false)) {
							try {

								final Item item = new Item();

								final URLConnection conn = site.getUrl().openConnection();
								conn.setConnectTimeout(10000);
								conn.setReadTimeout(60000);

								Xml.parse(conn.getInputStream(), Encoding.UTF_8, new RSSHandler(item));

								if (item.getTitle() != null) {
									notify(site, item);
								}

							} catch (Exception e) {
								Log.e(this.getClass().getName(), e.getMessage());
							}
						} else {
							Log.i(this.getClass().getName(), "Skipping " + site + " (disabled)");
						}
					}
				} finally {
					isActive = false;
				}

			} else {
				Log.i(this.getClass().getName(), "Task already running.");
			}
		}

		/**
		 * @param key
		 * @param item
		 */
		private void notify(final DealSite key, final Item item) {

			if (item != null) {
				final Item previousItem = RESULTS.get(key);
				if (previousItem == null || !previousItem.equals(item)) {

					Log.d(this.getClass().getName(), "Creating new notification.");

					RESULTS.put(key, item);

					Notification notification = new Notification(key.getDrawable(), item.getTitle(), System.currentTimeMillis());
					PendingIntent contentIntent = PendingIntent.getActivity(RSSCheckerService.this, 0, new Intent(Intent.ACTION_VIEW, item.getLink()), 0);
					notification.setLatestEventInfo(RSSCheckerService.this, item.getTitle(), item.getPrice(), contentIntent);
					notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
					notificationManager.notify(key.ordinal(), notification);

				} else {

					Log.d(this.getClass().getName(), "Not creating notification.");
				}
			}
		}

	}
}
