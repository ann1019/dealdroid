package org.chemlab.dealdroidapp;

/**
 * @author shade
 * @version $Id$
 */
public enum Interval {
	I_30_SECONDS("30 Seconds", 30000), 
	I_1_MINUTE("1 Minute", 60000),
	I_2_MINUTES("2 Minutes", 120000), 
	I_5_MINUTES("5 Minutes", 300000), 
	I_10_MINUTES("10 Minutes", 600000),
	I_30_MINUTES("30 Minutes", 1800000),
	I_1_HOUR("1 Hour", 3600000);

	private final String name;

	private final int millis;

	Interval(final String name, final int millis) {
		this.name = name;
		this.millis = millis;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the millis
	 */
	public int getMillis() {
		return millis;
	}
}
