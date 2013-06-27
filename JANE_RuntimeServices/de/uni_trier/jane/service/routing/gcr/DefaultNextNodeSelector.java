package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;

public class DefaultNextNodeSelector extends PositionCalculator implements NextNodeSelector {

	private double udgRadius; // = 100.0; // TODO test
	private double transmissionTime = 1.0; // TODO test
	
	
	public DefaultNextNodeSelector(double udgRadius) {
		this.udgRadius = udgRadius;
	}
	
	public NeighborDiscoveryData selectNextNode(Cluster sourceCluster, Cluster destinationCluster, Cluster currentCluster, Cluster nextCluster, NetworkNode sourceNode, NetworkNode destinationNode, NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors) {

		Position currentPosition = getPosition(currentNode, transmissionTime);

		NeighborDiscoveryData result = null;
		double dist = Double.POSITIVE_INFINITY;
		int hops = Integer.MAX_VALUE;
		for(int i=0; i<neighbors.length; i++) {
			
			NeighborDiscoveryData neighbor = neighbors[i];
			
			// return the destination node if it is reachable
			if(neighbor.getSender().equals(destinationNode.getAddress())) {
				Position destinationPosition = getPosition(neighbor, transmissionTime);
				if(currentPosition == null || destinationPosition == null || currentPosition.distance(destinationPosition) < udgRadius) {
					return neighbor;
				}
			}

			Position neighborPosition = getPosition(neighbor, transmissionTime);
			
			if(neighborPosition != null) {
				
				if(currentPosition == null || currentPosition.distance(neighborPosition) < udgRadius) {
					ReachableClusterTable table = ReachableClusterTable.fromNeighborDiscoveryData(neighbor);
					int h = table.getHopDistance(nextCluster.getAddress());
					double d = calculateProgress(currentCluster.getCenter(), nextCluster.getCenter(), neighborPosition);
					if(h < hops || (h == hops && d < dist)) {
						result = neighbor;
						dist = d;
						hops = h;
					}
				}

			}
		}
		return result;
	}

}
