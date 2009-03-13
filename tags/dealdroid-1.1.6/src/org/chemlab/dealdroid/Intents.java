package org.chemlab.dealdroid;

import android.content.Intent;

/**
 * The various intents used by the application.
 * 
 * @author shade
 * @version $Id$
 */
public enum Intents {
	BOOT_INTENT("android.intent.action.BOOT_COMPLETED"),
	DEALDROID_START("org.chemlab.dealdroid.DEALDROID_START"),
	DEALDROID_STOP("org.chemlab.dealdroid.DEALDROID_STOP"),
	DEALDROID_RESTART("org.chemlab.dealdroid.DEALDROID_RESTART"),
	DEALDROID_UPDATE("org.chemlab.dealdroid.DEALDROID_UPDATE"),
	DEALDROID_ENABLE("org.chemlab.dealdroid.DEALDROID_ENABLE"),
	DEALDROID_DISABLE("org.chemlab.dealdroid.DEALDROID_DISABLE");

	private final String action;
	
	Intents(final String action) {
		this.action = action;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * @return
	 */
	public Intent getIntent() {
		return new Intent(action);
	}
}
