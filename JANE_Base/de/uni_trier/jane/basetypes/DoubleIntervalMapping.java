/*****************************************************************************
 * 
 * DoubleIntervalMapping.java
 * 
 * $Id: DoubleIntervalMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This interface describes a mpapping form <code>double</code> to <cod>DoubleInterval</code>.
 */
public interface DoubleIntervalMapping {

	/**
	 * Get the infimum interval.
	 * @return the infimum 
	 */
	public DoubleInterval getInfimum();

	/**
	 * Get the supremum interval.
	 * @return the supremum
	 */
	public DoubleInterval getSupremum();

	/**
	 * Get the interval for the given time.
	 * @param time the current time
	 * @return the interval
	 */
	public DoubleInterval getValue(double time);

}

