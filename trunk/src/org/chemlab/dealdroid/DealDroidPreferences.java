package org.chemlab.dealdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

/**
 * The preferences panel.
 * 
 * @author shade
 * @version $Id$
 */
public class DealDroidPreferences extends PreferenceActivity {
		
	public static final String PREFS_NAME = "org.chemlab.dealdroid_preferences";
	
	public static final String ENABLED = "enabled_";
	
	public static final String NOTIFY_VIBRATE = "notify_vibrate";
	
	public static final String NOTIFY_LED = "notify_lights";
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
        final Intent si = new Intent(DealDroidServiceManager.DEALDROID_START);
        sendBroadcast(si);
                
		setPreferenceScreen(createPreferences());
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
		
		for (DealSite site : DealSite.values()) {
			
			final PreferenceCategory category = new PreferenceCategory(this);
			category.setTitle(site.getName() + " Options");
			root.addPreference(category);
			
			final CheckBoxPreference toggle = new CheckBoxPreference(this);
			toggle.setKey(ENABLED + site.toString());
			toggle.setTitle(R.string.enable_notifications);
			toggle.setSummary(R.string.enable_notifications_summary);
			
			category.addPreference(toggle);
				
		}
		
		// Notification Prefs
		final PreferenceCategory notify = new PreferenceCategory(this);
		notify.setTitle(R.string.notification_options);
		root.addPreference(notify);
		
		final CheckBoxPreference vibrate = new CheckBoxPreference(this);
		vibrate.setKey(NOTIFY_VIBRATE);
		vibrate.setTitle(R.string.notify_vibrate);
		vibrate.setSummary(R.string.notify_vibrate_summary);
		
		final CheckBoxPreference led = new CheckBoxPreference(this);
		led.setKey(NOTIFY_LED);
		led.setTitle(R.string.notify_led);
		led.setSummary(R.string.notify_led_summary);
		
		notify.addPreference(vibrate);
		notify.addPreference(led);
		
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
	public static boolean isEnabled(final SharedPreferences preferences, final DealSite site) {
		return preferences.getBoolean(ENABLED + site.toString(), false);
	}
	
	/**
	 * @param preferences
	 * @return if any site is enabled
	 */
	public static boolean isAnySiteEnabled(final SharedPreferences preferences) {
		boolean ret = false;
		for (DealSite site : DealSite.values()) {
			if (isEnabled(preferences, site)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

}
