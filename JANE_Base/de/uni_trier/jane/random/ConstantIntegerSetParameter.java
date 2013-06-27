/*****************************************************************************
 * 
 * ConstantIntegerSetParameter.java
 * 
 * $Id: ConstantIntegerSetParameter.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This is a time independent implementation of <code>IntegerSetParameter</code>.
 */
public class ConstantIntegerSetParameter implements IntegerSetParameter {

	private final static String VERSION = "$Id: ConstantIntegerSetParameter.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private IntegerVector integerSet;

	/**
	 * Construct a <code>ConstantIntegerSetParameter</code> object.
	 * @param integerSet the constant integer set
	 */
	public ConstantIntegerSetParameter(IntegerVector integerSet) {
		this.integerSet = integerSet;
	}

	/**
	 * @see de.uni_trier.jane.random.IntegerSetParameter#getValue(double)
	 */
	public IntegerVector getValue(double t) {
		return integerSet;
	}

	/**
	 * @see de.uni_trier.jane.random.IntegerSetParameter#getDomain()
	 */
	public IntegerVector getDomain() {
		return integerSet;
	}

}
