package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public interface DestinationClusterNextNodeSelector {

	NeighborDiscoveryData selectNextNode(Cluster sourceCluster, Cluster destinationCluster,
			NetworkNode sourceNode, NetworkNode destinationNode,
			NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors);

}
