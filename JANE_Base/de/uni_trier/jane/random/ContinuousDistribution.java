/*****************************************************************************
 * 
 * ContinuousDistribution.java
 * 
 * $Id: ContinuousDistribution.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

/**
 * This interface describes a general continuous distribution. Its domain is
 * a subset of double. The distribution is allowed to depend on the current
 * time.
 */
public interface ContinuousDistribution {

	/**
	 * Get the infimum of the domain.
	 * @return the infimum
	 */
	public double getInfimum();

	/**
	 * Get the supremum of the domain.
	 * @return the supremum
	 */
	public double getSupremum();

	/**
	 * Get the infimum of the domain at time t.
	 * @param t
	 * @return the infimum
	 * @deprecated
	 */
	public double getInfimum(double t);

	/**
	 * Get the supremum of the domain at time t.
	 * @param t
	 * @return the supremum
	 * @deprecated
	 */
	public double getSupremum(double t);

	/**
	 * Get the next value. The current time is t.
	 * @param t the current time
	 * @return the next value
	 * @deprecated
	 */
	public double getNext(double t);

	public double getNext();

}
