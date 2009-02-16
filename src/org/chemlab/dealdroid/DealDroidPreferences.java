package org.chemlab.dealdroid;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

/**
 * @author shade
 * @version $Id$
 */
public class DealDroidPreferences extends PreferenceActivity {

	public static final String PREFS_NAME = "org.chemlab.dealdroid_preferences";
	
	public static final String ENABLED = "enabled_";
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
        final Intent si = new Intent(this, DealDroidService.class);
        startService(si);
        
		setPreferenceScreen(createPreferences());
	}

	/**
	 * @return
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
		
		return root;
	}
	
}
