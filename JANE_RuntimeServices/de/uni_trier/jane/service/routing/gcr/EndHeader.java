package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;

public class EndHeader extends GeographicClusterRoutingHeader {

//	public EndHeader(Address sourceAddress, Position sourcePosition,
//			Address destinationAddress, Position destinationPosition) {
//		super(sourceAddress, sourcePosition, destinationAddress, destinationPosition);
//	}

	public EndHeader(GeographicClusterRoutingHeader other) {
		super(other);
	}

	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {
		
		GeographicClusterRoutingAlgorithm geographicClusterRoutingAlgorithm = (GeographicClusterRoutingAlgorithm)algorithm;
		
		// check if the final destination node was reached
		Address ownAddress = geographicClusterRoutingAlgorithm.getNeighborDiscoveryService().getOwnAddress();
		if(ownAddress.equals(getReceiver())) {
			handler.deliverMessage(this);
			return;
		}
		
		NeighborDiscoveryData nextNode = findDestinationClusterCandidateNode(geographicClusterRoutingAlgorithm);
		forwardIfPossible(handler, this, nextNode);
	}

	public LinkLayerInfo copy() {
		return this;
	}

}
