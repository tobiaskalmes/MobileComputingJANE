/*****************************************************************************
 * 
 * DiscreteDistribution.java
 * 
 * $Id: DiscreteDistribution.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This interface describes a general discrete distribution. The domain of the
 * distribution is a subset of int. A distribution implementing this interface
 * is allowed to depend on the time.
 */
public interface DiscreteDistribution {

	/**
	 * Get the domain of this distribution.
	 * @return a subset of int
	 */
	public IntegerVector getDomain();

	/**
	 * Get the domain of this distribution at time t.
	 * @param t the time
	 * @return a subset of int
	 */
	public IntegerVector getDomain(double t);

	/**
	 * Get the next value for the current time t.
	 * @param t the current time
	 * @return the next value
	 */
	public int getNext(double t);

}
