/*****************************************************************************
 * 
 * LocationFinishCondition.java
 * 
 * $Id: LocationFinishCondition.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.routing.face.conditions; 

import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

public class LocationFinishCondition implements FinishCondition {

	private boolean satisfied;
    
    private GeographicLocation targetLocation;

    public LocationFinishCondition(GeographicLocation targetLocation) {
        this.targetLocation=targetLocation;
        satisfied = false;
    }

    public LocationFinishCondition(boolean satisfied, GeographicLocation location) {
		// TODO Auto-generated constructor stub
		this.satisfied = satisfied;
		targetLocation = location;
	}

	public boolean checkCondition(PlanarGraphNode currentNode,
            NetworkNode[] neighbors) {
        return targetLocation.isInside(currentNode.getPosition());
    }

	public FinishCondition nextNode(PlanarGraphNode currentNode, NetworkNode[] neighbors) {
		return new LocationFinishCondition(checkCondition(currentNode, neighbors), targetLocation);
	}

	public boolean isSatisfied() {
		return satisfied;
	}



}
