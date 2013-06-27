/*****************************************************************************
 * 
 * ProbabilityVector.java
 * 
 * $Id: ProbabilityVector.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.random;

import de.uni_trier.jane.basetypes.*;

/**
 * This class is a wrapper for a <code>DoubleVector</code>. It tests if the vector
 * (p1,...,pn) has the property with pi>=0 for all i and p1+...+pn=1.0.
 */
public class ProbabilityVector {

	private final static String VERSION = "$Id: ProbabilityVector.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private DoubleVector doubleVector;
	private static final double EPSILON = 0.001;

	/**
	 * Construct a new <code>ProbabilityVector</code> object.
	 * @param doubleVector the vector of double values
	 */
	public ProbabilityVector(DoubleVector doubleVector) {
		double sum = 0;
		for(int i=0; i<doubleVector.size(); i++) {
			if(doubleVector.get(i) < 0) {
				throw new IllegalArgumentException("the given doubleSet conains negative values.");
			}
			sum += doubleVector.get(i);
		}
		if(Math.abs(sum-1.0) >= EPSILON) {
			throw new IllegalArgumentException("the given doubleSet does not sum to 1.");
		}
		this.doubleVector = doubleVector;
	}

	/**
	 * Get the size of this vector.
	 * @return the size
	 */
	public int getSize() {
		return doubleVector.size();
	}
	
	/**
	 * Get the ith component.
	 * @param i the index
	 * @return the value
	 */
	public double getValue(int i) {
		return doubleVector.get(i);
	}

}
