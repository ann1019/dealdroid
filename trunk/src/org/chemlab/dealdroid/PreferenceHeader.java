package org.chemlab.dealdroid;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.widget.TextView;

/**
 * @author shade
 *
 */
public class PreferenceHeader extends Preference {

	final String headerText;
	
	/**
	 * @param context
	 */
	public PreferenceHeader(Context context, String headerText) {
		super(context);
		this.headerText = headerText;
		setWidgetLayoutResource(R.layout.prefheader);
	}

	/* (non-Javadoc)
	 * @see android.preference.Preference#onBindView(android.view.View)
	 */
	@Override
	protected void onBindView(View view) {
		
		super.onBindView(view);
		
		 // Set our custom views inside the layout
        final TextView myTextView = (TextView) view.findViewById(R.id.prefheader_view);
        if (myTextView != null) {
            myTextView.setText(headerText);
        }

	}

}
