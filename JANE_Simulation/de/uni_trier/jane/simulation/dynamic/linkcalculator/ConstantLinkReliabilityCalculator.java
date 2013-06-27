/*****************************************************************************
 * 
 * ConstantLinkReliabilityCalculator.java
 * 
 * $Id: ConstantLinkReliabilityCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.linkcalculator;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.*;

/**
 * Implementation for a constant link reliability calculator. The link reliability returned is 1 when device are within sending range, and 0 else.
 */
public class ConstantLinkReliabilityCalculator
	implements LinkReliabilityCalculator {

	/**
	 * 
	 */
	private class ConstantLinkReliability implements DoubleMapping {

		private double sendingRadius;

		private DistanceMapping distanceMapping;

		/**
		 * 
		 * @param distanceMapping
		 * @param sendingRadius
		 */
		public ConstantLinkReliability(DistanceMapping distanceMapping, double sendingRadius) {
			this.distanceMapping=distanceMapping;
			this.sendingRadius=sendingRadius;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getInfimum()
		 */
		public double getInfimum() {
			
			return 0;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getSupremum()
		 */
		public double getSupremum() {
			
			return 1;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getValue(double)
		 */
		public double getValue(double time) {
			if (distanceMapping.getValue(time)<0)
				throw new IllegalStateException("DistanceMapping must be greater or equal 0");
			
			if (distanceMapping.getValue(time)>sendingRadius)
				return 0;
			else return 1;
			
		}

	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkReliabilityCalculator#getLinkReliability(double, double, double, de.uni_trier.ubi.appsim.kernel.dynamic.DistanceMapping)
	 */
	public DoubleMappingInterval getLinkReliability(
		double sendingRadius,
		double startTime,
		double endTime,
		DistanceMapping distanceMapping) {
		
		return new DoubleMappingIntervalImplementation(new ConstantLinkReliability(distanceMapping, sendingRadius),startTime,endTime);
	}

	
}
