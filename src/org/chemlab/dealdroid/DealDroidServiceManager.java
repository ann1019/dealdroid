package org.chemlab.dealdroid;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
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

	private static final String BOOT_INTENT = "android.intent.action.BOOT_COMPLETED";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		if (BOOT_INTENT.equals(intent.getAction())) {
			final ComponentName comp = new ComponentName(context, DealDroidService.class);
			final ComponentName service = context.startService(new Intent().setComponent(comp));

			if (service == null) {
				Log.e(DealDroidService.DEALDROID, "Could not start service! " + comp.toString());
			}

		}

	}

}
