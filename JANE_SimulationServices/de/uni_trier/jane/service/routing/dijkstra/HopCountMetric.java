/*******************************************************************************
 * 
 * HopCountMetric.java
 * 
 * $Id: HopCountMetric.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 * 
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K.
 * Lehnert
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 ******************************************************************************/
package de.uni_trier.jane.service.routing.dijkstra;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;


/**
 * This class implements the standard hop count metric, i.e. each existing edge has a weight
 * of 1.
 */
public class HopCountMetric implements WeightFunction {

	public double getWeight(DeviceID source, DeviceID destination, GlobalKnowledge globalKnowledge) {
		if(globalKnowledge.isConnected(source, destination)) {
			return 1.0;
		}
		
		return Double.POSITIVE_INFINITY;
		
	}
	
	public String toString() {
		return "HopCount";
	}

}
