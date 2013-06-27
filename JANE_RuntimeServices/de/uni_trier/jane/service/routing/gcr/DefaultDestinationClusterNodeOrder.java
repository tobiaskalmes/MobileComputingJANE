package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public class DefaultDestinationClusterNodeOrder implements
		DestinationClusterNodeOrder {

	public boolean isLessThan(NeighborDiscoveryData currentNode,
			NeighborDiscoveryData nextNode, NetworkNode destinationNode,
			Cluster destinationCluster) {

		Position currentPosition = LocationData.getPosition(currentNode);
		Position nextPosition = LocationData.getPosition(nextNode);
		
		if(currentPosition == null || nextPosition == null) {
			return false;
		}
		
		return nextPosition.distance(destinationNode.getPosition()) < currentPosition.distance(destinationNode.getPosition());

	}

}
