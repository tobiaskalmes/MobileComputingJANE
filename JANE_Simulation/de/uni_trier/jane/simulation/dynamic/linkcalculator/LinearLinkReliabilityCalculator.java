/*****************************************************************************
 * 
 * LinearLinkReliabilityCalculator.java
 * 
 * $Id: LinearLinkReliabilityCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.linkcalculator;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.*;

/**
 * This class implements a linear increasing link reliability calculator. 
 * Link realiability is 0 when the other device is out of range and increases 
 * linear to 1 from distance sending range to distance 0.  
 */
public class LinearLinkReliabilityCalculator
	implements LinkReliabilityCalculator {


	
	

	private class LinearDistanceMapping implements DoubleMapping {

		private DoubleMapping doubleMapping;

		private double sendingRadius;

		/**
		 * 
		 * @param doubleMapping
		 * @param sendingRadius
		 */
		public LinearDistanceMapping(DoubleMapping doubleMapping, double sendingRadius) {
			this.doubleMapping=doubleMapping;
			this.sendingRadius=sendingRadius;
			
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getInfimum()
		 */
		public double getInfimum() {
			
			double value= 1-doubleMapping.getSupremum()/sendingRadius;
			if (value <0) return 0;
			else return value;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getSupremum()
		 */
		public double getSupremum() {
			// TODO Auto-generated method stub
			double value= 1-doubleMapping.getInfimum()/sendingRadius;
			if (value>1) return 1;
			else return value;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMapping#getValue(double)
		 */
		public double getValue(double time) {
			// TODO Auto-generated method stub
			final double value=doubleMapping.getValue(time);
			if (value>sendingRadius) return 0;
			if (value==0.0) return 1;
			if (value<0) throw new IllegalStateException("DistanceMapping must always be greater 0");
			return 1-value/sendingRadius;
		}

	}
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkReliabilityCalculator#getLinkReliability(double, double, double, double, double)
	 */
	public DoubleMappingInterval getLinkReliability(double sendingRadius, double startTime, double endTime, DistanceMapping distanceMapping) {
//		if (startDistance>endDistance)
//			throw new IllegalArgumentException("Startdistance must be lower than enddistance");
//		
//		if (sendingRadius<endDistance)
//			throw new IllegalArgumentException("Enddistance must be lower than sending radius");
//		if (startDistance<0)
//			throw new IllegalArgumentException("startDistance must be greater 0");
		if (startTime>endTime)
			throw new IllegalArgumentException("endTime must be greater than startTime");
		if (startTime<0)
			throw new IllegalArgumentException("startTime must be greater 0");
		return new DoubleMappingIntervalImplementation(new LinearDistanceMapping(distanceMapping,sendingRadius),startTime,endTime);
	}

}
