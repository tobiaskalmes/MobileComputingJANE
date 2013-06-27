package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public interface ClusterTransitionNodeOrder {

	public boolean isLessThan(NeighborDiscoveryData currentNode, NeighborDiscoveryData nextNode, Cluster currentCluster, Cluster nextCluster);

}
