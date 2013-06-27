/*****************************************************************************
* 
* $Id: ClusterPlanarGraphNode.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public class ClusterPlanarGraphNode implements PlanarGraphNode {

	private Cluster cluster;
	private Map planarNeighbors;
	private NetworkNode[] neighbors;
	private ClusterPlanarGraphNode relay;
	
	public ClusterPlanarGraphNode(Cluster cluster) {
		this.cluster = cluster;
		planarNeighbors = null;
		relay = null;
	}

	public ClusterPlanarGraphNode(Cluster cluster, ClusterPlanarGraphNode relay) {
		this.cluster = cluster;
		this.relay = relay;
		planarNeighbors = null;
		neighbors = null;
	}


	public ClusterPlanarGraphNode(Cluster cluster, Map planarNeighbors, NetworkNode[] neighbors) {
		this.cluster = cluster;
		this.planarNeighbors = planarNeighbors;
		this.neighbors = neighbors;
		relay = null;
	}

	public PlanarGraphNode[] getAdjacentNodes() {
		if(planarNeighbors == null) {
			return null;
		}
		Collection values = planarNeighbors.values();
		return (PlanarGraphNode[])values.toArray(new PlanarGraphNode[values.size()]);
	}

	public boolean hasAdjacentNodes() {
		if(planarNeighbors == null) {
			return false;
		}
		return planarNeighbors.size() > 0;
	}

	public PlanarGraphNode getAdjacentNode(Address address) {
		if(planarNeighbors == null) {
			return null;
		}
		return (PlanarGraphNode)planarNeighbors.get(address);
	}

	public NetworkNode[] getAllOneHopNeighbors() {
		return neighbors;
	}

	public boolean isStopNode() {
		return planarNeighbors == null;
	}

	public Address getAddress() {
		return cluster.getAddress();
	}

	public Position getPosition() {
		return cluster.getCenter();
	}

	public boolean isOneHopNeighbor() {
		return true;
	}

	public boolean isVirtual() {
		return relay != null;
	}

	public NetworkNode getRelayNode() {
		return relay;
	}
	

}