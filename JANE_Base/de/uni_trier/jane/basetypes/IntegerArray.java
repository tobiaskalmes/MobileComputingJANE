/*****************************************************************************
 * 
 * IntegerArray.java
 * 
 * $Id: IntegerArray.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class implements the <code>IntegerVector</code> interface by using an
 * int array.
 */
public class IntegerArray implements IntegerVector {

	private final static String VERSION = "$Id: IntegerArray.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private int[] values;
	private int min;
	private int max;

	/**
	 * Construct a new <code>IntegerArray</code> object.
	 * @param values the int array
	 */
	public IntegerArray(int[] values) {
		this.values = values;
		min = Integer.MIN_VALUE;
		max = Integer.MAX_VALUE;
		for(int i=0; i<values.length; i++) {
			min = Math.min(min, values[i]);
			max = Math.max(max, values[i]);
		}
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#get(int)
	 */
	public int get(int i) {
		return values[i];
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#iterator()
	 */
	public IntegerIterator iterator() {
		return new Iterator();
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#size()
	 */
	public int size() {
		return values.length;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#getMaximum()
	 */
	public int getMaximum() {
		return max;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#getMinimum()
	 */
	public int getMinimum() {
		return min;
	}

	private class Iterator implements IntegerIterator {
		public int current;
		public boolean hasNext() {
			return current < values.length;
		}
		public int next() {
			return values[current++];
		}
	}

}
