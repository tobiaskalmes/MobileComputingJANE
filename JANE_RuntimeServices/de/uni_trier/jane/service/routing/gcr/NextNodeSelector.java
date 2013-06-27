package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public interface NextNodeSelector {

	NeighborDiscoveryData selectNextNode(Cluster sourceCluster, Cluster destinationCluster,
			Cluster currentCluster, Cluster nextCluster,
			NetworkNode sourceNode, NetworkNode destinationNode,
			NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors);

}
