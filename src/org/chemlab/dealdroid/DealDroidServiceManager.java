package org.chemlab.dealdroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This will start the DealDroid service on bootup.
 * 
 * @author shade
 * @version $Id$
 */
public class DealDroidServiceManager extends BroadcastReceiver {

	public static final String BOOT_INTENT = "android.intent.action.BOOT_COMPLETED";
	
	public static final String DEALDROID_START = "org.chemlab.dealdroid.DEALDROID_START";
	
	public static final String DEALDROID_STOP = "org.chemlab.dealdroid.DEALDROID_STOP";
	
	private static final long UPDATE_INTERVAL = 120000;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (BOOT_INTENT.equals(intent.getAction()) || DEALDROID_START.equals(intent.getAction())) {
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
		getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP, 0, UPDATE_INTERVAL, getSiteCheckerIntent(context));	
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
		return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	/**
	 * @param context
	 * @return
	 */
	private PendingIntent getSiteCheckerIntent(final Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(DealDroidSiteChecker.INTENT_CHECK_SITES), 0);
	}
}
