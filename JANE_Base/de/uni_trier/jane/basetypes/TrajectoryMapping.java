/*****************************************************************************
 * 
 * TrajectoryMapping.java
 * 
 * $Id: TrajectoryMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen  and Johannes K. Lehnert
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
 * This interface describes a mapping from double to <code>Trajectory</code>.
 */
public interface TrajectoryMapping {

	/**
	 * Get the minimum trajectory consisting of minimum direction and minimum
	 * position.
	 * @return the minimum trajectory
	 */
	public Trajectory getInfimum();

	/**
	 * Get the maximum trajectory consisting of maximum direction and maximum
	 * position.
	 * @return the maximum trajectory
	 */
	public Trajectory getSupremum();

	/**
	 * Get the trajectory value for the given time.
	 * @param time the time
	 * @return the value
	 */
	public Trajectory getValue(double time);

}
