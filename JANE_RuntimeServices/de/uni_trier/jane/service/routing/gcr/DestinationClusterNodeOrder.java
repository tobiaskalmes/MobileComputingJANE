package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public interface DestinationClusterNodeOrder {

	public boolean isLessThan(NeighborDiscoveryData currentNode, NeighborDiscoveryData nextNode, NetworkNode destinationNode, Cluster destinationCluster);

}
