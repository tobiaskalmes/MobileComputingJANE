/*****************************************************************************
 * 
 * DoubleArray.java
 * 
 * $Id: DoubleArray.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class implements the <code>DoubleVector</code> interface by using an array
 * of <code>double</code> values.
 */
public class DoubleArray implements DoubleVector {

    
	private final static String VERSION = "$Id: DoubleArray.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	protected double[] values;
	private double min;
	private double max;

	/**
	 * Construct a new <code>DoubleArray</code> object.
	 * @param values the array of <code>double</code> values
	 */
	public DoubleArray(double[] values) {
		this.values = values;
		min = Double.MIN_VALUE;
		max = Double.MAX_VALUE;
		for(int i=0; i<values.length; i++) {
			min = Math.min(min, values[i]);
			max = Math.max(max, values[i]);
		}
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleVector#get(int)
	 */
	public double get(int i) {
		return values[i];
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleVector#iterator()
	 */
	public DoubleIterator iterator() {
		return new Iterator();
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleVector#size()
	 */
	public int size() {
		return values.length;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleVector#getMaximum()
	 */
	public double getMaximum() {
		return max;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.DoubleVector#getMinimum()
	 */
	public double getMinimum() {
		return min;
	}

	private class Iterator implements DoubleIterator {
		protected int current;
		public boolean hasNext() {
			return current < values.length;
		}
		public double next() {
			return values[current++];
		}
	}

}
