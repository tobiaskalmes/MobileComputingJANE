package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public class DefaultDestinationClusterNextNodeSelector implements DestinationClusterNextNodeSelector {

	public NeighborDiscoveryData selectNextNode(Cluster sourceCluster, Cluster destinationCluster, NetworkNode sourceNode, NetworkNode destinationNode, NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors) {

		// TODO implement correctly: the destination node could not be reachable!?
		for(int i=0; i<neighbors.length; i++) {
			NeighborDiscoveryData neighbor = neighbors[i];
			if(neighbor.getSender().equals(destinationNode.getAddress())) {
				return neighbor;
			}
		}
		return null;
	}

}
