package org.chemlab.dealdroid;

import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import android.widget.Toast;

/**
 * @author shade
 * 
 */
public class RSSCheckerService extends Service {

	private Timer timer;

	public final Map<DealSite, Item> RESULTS = new EnumMap<DealSite, Item>(DealSite.class);

	private static final long updateInterval = 60000;

	private static Activity MAIN_ACTIVITY;

	private static NotificationManager mNM;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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

		timer.scheduleAtFixedRate(new RSSCheckerTask(), 0, updateInterval);

		Toast.makeText(this, "DealDroid service started.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 */
	private void stopService() {
		timer.cancel();
		Toast.makeText(this, "DealDroid service stopped.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * @param activity
	 */
	public static void setMainActivity(DealDroidActivity activity) {
		if (MAIN_ACTIVITY == null) {
			MAIN_ACTIVITY = activity;
		}
	}

	/**
	 * @author shade
	 */
	private final class RSSCheckerTask extends TimerTask {

		private boolean isActive = false;

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

						Log.i(this.getClass().getName(), "Fetching data for " + site);

						try {
							final Item item = new Item();
							Xml.parse(site.getUrl().openStream(), Encoding.UTF_8, new RSSHandler(item));

							if (item.getTitle() != null) {
								notify(site, item);
							}

						} catch (Exception e) {
							Log.e(this.getClass().getName(), e.getMessage());
						}
					}
				} finally {
					isActive = false;
				}
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
					mNM.notify(R.string.app_name, notification);

				} else {

					Log.d(this.getClass().getName(), "Not creating notification.");
				}
			}
		}

	}
}
