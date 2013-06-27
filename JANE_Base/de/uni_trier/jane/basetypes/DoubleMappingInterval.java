/*
 * Created on 29.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.basetypes;

/**
 * @author daniel
 *
 *	This interface describes a Double Mapping in a specific time Interval
 */
public interface DoubleMappingInterval {
	
	/**
	 * Get the infimum time.
	 * @return the infimum 
	 */
	public double getInfimum();

	/**
	 * Get the supremum time
	 * @return the supremum
	 */
	public double getSupremum();

	/**
	 * Get the double for the given time.
	 * @param time the current time
	 * @return the double
	 */
	public double getValue(double time);


}
