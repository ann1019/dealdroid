package org.chemlab.dealdroidapp;

import static org.chemlab.dealdroidapp.Intents.DEALDROID_DISABLE;
import static org.chemlab.dealdroidapp.Intents.DEALDROID_ENABLE;
import static org.chemlab.dealdroidapp.Intents.DEALDROID_RESTART;
import static org.chemlab.dealdroidapp.Intents.DEALDROID_START;
import static org.chemlab.dealdroidapp.Intents.DEALDROID_STOP;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.util.Log;

/**
 * The preferences panel.
 * 
 * @author shade
 * @version $Id$
 */
public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
		
	public static final String PREFS_NAME = "org.chemlab.dealdroidapp_preferences";
	
	public static final String APP_ENABLED = "app_enabled";
	
	public static final String SITE_ENABLED = "enabled_";
	
	public static final String NOTIFY_VIBRATE = "notify_vibrate";
	
	public static final String NOTIFY_LED = "notify_lights";
	
	public static final String NOTIFY_RINGTONE = "notify_ringtone";
		
	public static final String CHECK_INTERVAL = "check_interval";
	
	private PreferenceScreen preferenceScreen;

	private final String LOG_TAG = this.getClass().getSimpleName();
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		final boolean enabled = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(APP_ENABLED, true);
		
		if (preferenceScreen == null) {        
			preferenceScreen = createPreferences();
		}
		
		if (savedInstanceState == null && enabled) {
			final Intent si = new Intent(DEALDROID_START.getIntent());
			sendBroadcast(si);
		}
		
		setPreferenceScreen(preferenceScreen);
		
	}

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		Log.d(LOG_TAG, "Pref changed: " + key);

		// If a site is toggled, just check right away
		if (key != null) {

			final boolean enabled = sharedPreferences.getBoolean(APP_ENABLED, true);

			if (key.startsWith(SITE_ENABLED)) {

				final Site site = Site.valueOf(key.substring(SITE_ENABLED.length()));

				final Intent intent = sharedPreferences.getBoolean(key, false) ? DEALDROID_ENABLE.getIntent() : DEALDROID_DISABLE.getIntent();
				intent.putExtra("site", site.toString());

				sendBroadcast(intent);

			} else if (enabled && (key.equals(CHECK_INTERVAL))) {
					
				final Intent reschedule = DEALDROID_RESTART.getIntent();
				sendBroadcast(reschedule);
				
			} else if (key.equals(APP_ENABLED)) {

				final Intent toggle = enabled ? DEALDROID_START.getIntent() : DEALDROID_STOP.getIntent();
				sendBroadcast(toggle);

			}
		}
	}


	/**
	 * Dynamically creates the PreferenceScreen.  I didn't want this in XML because
	 * it needs to have dynamic options from the DealSite enum.
	 * 
	 * @return the preference screen
	 */
	private PreferenceScreen createPreferences() {

		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		
		root.setTitle(R.string.app_name);
		
		final PreferenceCategory dd = new PreferenceCategory(this);
		dd.setTitle(R.string.app_name);
		root.addPreference(dd);
		
		final CheckBoxPreference enabled = new CheckBoxPreference(this);
		enabled.setKey(APP_ENABLED);
		enabled.setTitle(R.string.auto_check);
		enabled.setSummary(R.string.auto_check_summary);
		enabled.setDefaultValue(true);
		dd.addPreference(enabled);
		
		final PreferenceScreen sites = getPreferenceManager().createPreferenceScreen(this);
		sites.setTitle(R.string.manage_sites);
		sites.setSummary(R.string.manage_sites_summary);
		
		dd.addPreference(sites);
		
		final PreferenceCategory feeds = new PreferenceCategory(this);
		feeds.setTitle(R.string.sites_and_feeds);
		sites.addPreference(feeds);
		
		for (Site site : Site.values()) {

			final CheckBoxPreference toggle = new CheckBoxPreference(this);
			toggle.setKey(SITE_ENABLED + site.toString());
			toggle.setTitle(site.getName());
			toggle.setSummary("Category: " + site.getCategory() + " (" + site.getSite().toString() + ")");
			toggle.setDefaultValue(site.isEnabledByDefault());
			
			feeds.addPreference(toggle);

		}
		
		// Notification Prefs
		final PreferenceCategory notify = new PreferenceCategory(this);
		notify.setTitle(R.string.notification_options);
		root.addPreference(notify);
		
		final CheckBoxPreference vibrate = new CheckBoxPreference(this);
		vibrate.setKey(NOTIFY_VIBRATE);
		vibrate.setTitle(R.string.notify_vibrate);
		vibrate.setSummary(R.string.notify_vibrate_summary);
		vibrate.setDefaultValue(true);
		
		final CheckBoxPreference led = new CheckBoxPreference(this);
		led.setKey(NOTIFY_LED);
		led.setTitle(R.string.notify_led);
		led.setSummary(R.string.notify_led_summary);
		led.setDefaultValue(true);
		
		final RingtonePreference ring = new RingtonePreference(this);
		ring.setKey(NOTIFY_RINGTONE);
		ring.setTitle(R.string.notify_ringtone);
		ring.setSummary(R.string.notify_ringtone_summary);
		ring.setRingtoneType(RingtoneManager.TYPE_NOTIFICATION);
		
		final ListPreference interval = new ListPreference(this);
		interval.setKey(CHECK_INTERVAL);
		interval.setTitle(R.string.update_interval);
		interval.setSummary(R.string.update_interval_summary);
		
		final List<String> intervals = new ArrayList<String>();
		final List<String> iValues = new ArrayList<String>();
		for (Interval i : Interval.values()) {
			iValues.add(i.name());
			intervals.add(i.getName());
		}
		interval.setEntries(intervals.toArray(new String[intervals.size()]));
		interval.setEntryValues(iValues.toArray(new String[iValues.size()]));
		interval.setDefaultValue(Interval.I_10_MINUTES.name());
		
		notify.addPreference(vibrate);
		notify.addPreference(led);
		notify.addPreference(ring);
		notify.addPreference(interval);
		
		// About Link
		final PreferenceScreen about = getPreferenceManager().createPreferenceScreen(this);
		about.setIntent(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse("http://dealdroid.googlecode.com")));
		about.setTitle(R.string.about);
		root.addPreference(about);
		
		return root;
	}
	
	/**
	 * @param preferences
	 * @param site
	 * @return if the site is enabled 
	 */
	public static boolean isEnabled(final SharedPreferences preferences, final Site site) {
		return preferences.getBoolean(SITE_ENABLED + site.toString(), false);
	}
	
	/**
	 * @param preferences
	 * @return the number of sites enabled
	 */
	public static int getNumSitesEnabled(final SharedPreferences preferences) {
		int si = 0;
		for (Site site : Site.values()) {
			if (isEnabled(preferences, site)) {
				si++;
			}
		}
		return si;
	}

}
