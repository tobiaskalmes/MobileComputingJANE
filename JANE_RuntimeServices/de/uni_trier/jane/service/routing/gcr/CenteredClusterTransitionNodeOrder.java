package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public class CenteredClusterTransitionNodeOrder extends PositionCalculator implements ClusterTransitionNodeOrder {

	public boolean isLessThan(NeighborDiscoveryData currentNode,
			NeighborDiscoveryData nextNode, Cluster currentCluster,
			Cluster nextCluster) {
		
		Position currentPosition = LocationData.getPosition(currentNode);
		Position nextPosition = LocationData.getPosition(nextNode);
		
		if(currentPosition == null || nextPosition == null) {
			return false;
		}
		
		boolean currentInside = nextCluster.isInside(currentPosition);
		boolean nextInside = nextCluster.isInside(nextPosition);
		
		if(nextInside && !currentInside) {
			return true;
		}

		if(!nextInside && currentInside) {
			return false;
		}

//		return nextCluster.getCenter().distance(nextPosition) < nextCluster.getCenter().distance(currentPosition);

		double d1 = nextCluster.getCenter().distance(nextPosition);
		double d2 = nextCluster.getCenter().distance(currentPosition);
		
		return d1 < d2;

		
	}

}
