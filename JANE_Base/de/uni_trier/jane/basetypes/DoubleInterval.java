/*****************************************************************************
 * 
 * DoubleInterval.java
 * 
 * $Id: DoubleInterval.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class implements a interval.
 */
public class DoubleInterval {

	private final static String VERSION = "$Id: DoubleInterval.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private double first;
	private double last;

	/**
	 * Construct a new <code>DoubleInterval</code> object.
	 * @param first the first value
	 * @param last the last value
	 */
	public DoubleInterval(double first, double last) {
		if(first > last) {
			throw new IllegalArgumentException("the first value has to be less or equal the last value");
		}
		this.first = first;
		this.last = last;
	}

	/**
	 * Get the first value.
	 * @return the first value
	 */
	public double getFirst() {
		return first;
	}

	/**
	 * Get the last value.
	 * @return the last value
	 */
	public double getLast() {
		return last;
	}

}

