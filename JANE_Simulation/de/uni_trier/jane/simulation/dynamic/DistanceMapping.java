/*****************************************************************************
 * 
 * DistanceMapping.java
 * 
 * $Id: DistanceMapping.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic;

import de.uni_trier.jane.basetypes.*;


/**
 * Represents the distances between two devices at a given time. It maps doubles (time) to doubles (distances)  
 */
public class DistanceMapping implements DoubleMapping {

	private TrajectoryMapping trajectoryB;

	private TrajectoryMapping trajectoryA;

	/**
	 * Constructor for class <code>DistanceMapping</code> 
	 * @param trajectoryA	the trajectory mapping for device a
	 * @param trajectoryB 	the trajectory mapping for device b
	 */
	public DistanceMapping(TrajectoryMapping trajectoryA, TrajectoryMapping trajectoryB) {
		this.trajectoryA=trajectoryA;
		this.trajectoryB=trajectoryB;
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getInfimum()
	 */
	public double getInfimum() {
		// TODO calculate inf ?
		throw new IllegalStateException("Infimum is not calculated");
		//return trajectoryA.getInfimum().;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getSupremum()
	 */
	public double getSupremum() {
		// TODO calculate sup?
		throw new IllegalStateException("Supremum is not calculated");
		//return 0;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getValue(double)
	 */
	public double getValue(double time) {
		return trajectoryA.getValue(time).getPosition().distance(trajectoryB.getValue(time).getPosition());
	}

}