/*****************************************************************************
 * 
 * LinkReliabilityCalculator.java
 * 
 * $Id: LinkReliabilityCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
 * Classes implementing this interface calculates the link reliability between two devices e.g. using the given distance mapping
 */
public interface LinkReliabilityCalculator {
    /**
     * Calculate the link reliability mapping at the given time intervall for a device with the given sending radius sending to a device 
     * with a distance given by the distance mapping  
     * @param sendingRadius			the sending radius of the sending device
     * @param startTime				interval beginning
     * @param endTime				interval end
     * @param distanceMapping		DistanceMapping for the given time interval maps the time to the distance between sender and receiver 
     * @return						the link reliability mapping
     */
	public DoubleMappingInterval getLinkReliability(double sendingRadius,double startTime,double endTime, DistanceMapping distanceMapping);

}
