/*****************************************************************************
 * 
 * PositionMappingTrajectory.java
 * 
 * $Id: PositionMappingTrajectory.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This implementation of the <code>TrajectoryMapping</code> interface uses
 * <code>PositionMapping</code> objects to determine the current position and
 * direction.
 */
public class PositionMappingTrajectory implements TrajectoryMapping {

	private final static String VERSION = "$Id: PositionMappingTrajectory.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private PositionMapping position;
	private PositionMapping direction;
	
	/**
	 * Construct a newe <code>PositionMappingTrajectory</code> object.
	 * @param position the mapping to positions
	 * @param direction the maping to directions
	 */
	public PositionMappingTrajectory(PositionMapping position, PositionMapping direction) {
		this.position = position;
		this.direction = direction;
	}
	
	/**
	 * @see de.uni_trier.jane.basetypes.TrajectoryMapping#getInfimum()
	 */
	public Trajectory getInfimum() {
		return new Trajectory(position.getInfimum(), direction.getInfimum());
	}

	/**
	 * @see de.uni_trier.jane.basetypes.TrajectoryMapping#getSupremum()
	 */
	public Trajectory getSupremum() {
		return new Trajectory(position.getSupremum(), direction.getSupremum());
	}

	/**
	 * @see de.uni_trier.jane.basetypes.TrajectoryMapping#getValue(double)
	 */
	public Trajectory getValue(double time) {
		return new Trajectory(position.getValue(time), direction.getValue(time));
	}

}
