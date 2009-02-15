package org.chemlab.dealdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author shade
 * @version $Id$
 */
public class DealDroidActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        RSSCheckerService.setMainActivity(this);
        final Intent si = new Intent(this, RSSCheckerService.class);
        startService(si);
                
        setContentView(R.layout.main);

    }


}