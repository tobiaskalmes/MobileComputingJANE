/*****************************************************************************
 * 
 * IntegerSetParameter.java
 * 
 * $Id: IntegerSetParameter.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This interface is used as a parameter for <code>DiscreteWeightedDistribution</code>.
 */
public interface IntegerSetParameter {

	/**
	 * Get the integer set at time t.
	 * @param t the time of interest
	 * @return the integer set
	 */
	public IntegerVector getValue(double t);

	/**
	 * Get the union of the integer sets for all times t.
	 * @return the union over all integer sets
	 */
	public IntegerVector getDomain();

}
