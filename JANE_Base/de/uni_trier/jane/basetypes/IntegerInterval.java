/*****************************************************************************
 * 
 * IntegerInterval.java
 * 
 * $Id: IntegerInterval.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class implements the <code>IntegerVector</code> interface. It contains
 * all values between a given min and max value.
 */
public class IntegerInterval implements IntegerVector {

	private final static String VERSION = "$Id: IntegerInterval.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private int first;
	private int last;
	
	/**
	 * Costruct a new <code>IntegerInterval</code> object.
	 * @param i the first or last value
	 * @param j the first or last value
	 */
	public IntegerInterval(int i, int j) {
		first = Math.min(i, j);
		last = Math.max(i, j);
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#get(int)
	 */
	public int get(int i) {
		return first + i;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#getMaximum()
	 */
	public int getMaximum() {
		return last;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerVector#getMinimum()
	 */
	public int getMinimum() {
		return first;
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
		return last - first + 1;
	}

	private class Iterator implements IntegerIterator {
		private int current;
		public Iterator() {
			current = first;
		}
		public boolean hasNext() {
			return current <= last;
		}

		public int next() {
			int result = current;
			current++;
			return result;
		}
	}

}
