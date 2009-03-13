package org.chemlab.dealdroid;

/**
 * @author shade
 * @version $Id$
 */
public enum Interval {
	I_30_SECONDS("30 Seconds", 30000), 
	I_1_MINUTE("1 Minute", 60000),
	I_2_MINUTES("2 Minutes", 120000), 
	I_5_MINUTES("5 Minutes", 300000), 
	I_10_MINUTES("10 Minutes", 60000);

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
