/*****************************************************************************
 * 
 * LinearDoubleMapping.java
 * 
 * $Id: LinearDoubleMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.basetypes;

/**
 * This class maps <code>double</code> values from start time to end time
 * linearily between start vlaue and end value.
 */
public class LinearDoubleMapping implements DoubleMapping {

	private final static String VERSION = "$Id: LinearDoubleMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private double startTime;
	private double endTime;
	private double startValue;
	private double endValue;

	/**
	 * Construct a new <code>LinearDoubleMapping</code> object.
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param startValue the start value
	 * @param endValue the end value
	 */
	public LinearDoubleMapping(double startTime, double endTime, double startValue, double endValue) {
		if(startTime <= endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.startValue = startValue;
			this.endValue = endValue;
		}
		else {
			this.startTime = endTime;
			this.endTime = startTime;
			this.startValue = endValue;
			this.endValue = startValue;
		}
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleMapping#getInfimum()
	 */
	public double getInfimum() {
		return Math.min(startValue, endValue);
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleMapping#getSupremum()
	 */
	public double getSupremum() {
		return Math.max(startValue, endValue);
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleMapping#getValue(double)
	 */
	public double getValue(double time) {
		if(time < startTime) {
			return startValue;
		}
		else if(time > endTime) {
			return endValue;
		}
		else {
			double delta = endTime-startTime;
			if(delta == 0) {
				return endValue;
			}
			else {
				return startValue + (endValue-startValue)*((time-startTime)/delta);
			}
		}
	}

}

