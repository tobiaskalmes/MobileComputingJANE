/*****************************************************************************
 * 
 * ConstantIntegerMapping.java
 * 
 * $Id: ConstantIntegerMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class implements the <code>IntegerMapping</code> interface. It constantly
 * maps a time value to one <code>int</code> value.
 */
public class ConstantIntegerMapping implements IntegerMapping {

	private final static String VERSION = "$Id: ConstantIntegerMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private int i;

	/**
	 * Construct a new <code>ConstantIntegerMapping</code> object.
	 * @param i the constant value
	 */
	public ConstantIntegerMapping(int i) {
		this.i = i;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerMapping#getMaximum()
	 */
	public int getMaximum() {
		return i;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerMapping#getMinimum()
	 */
	public int getMinimum() {
		return i;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.IntegerMapping#getValue(double)
	 */
	public int getValue(double time) {
		return i;
	}

}
