package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;

public class RelayHeader extends ContinueHeader {

	private Address relayCluster;

	public RelayHeader(
			GeographicClusterRoutingHeader header, Address sourceAddress,
			Position sourcePosition,
			Address destinationAddress,
			Position destinationPosition,
			PlanarGraphNode previousNode,
			Address relayCluster,
			Address nextCluster,
			Position lastIntersection,
			boolean clockwise,
			StartCondition startCondition,
			CrossingCondition crossingCondition,
			TurnCondition turnCondition,
			ResumeGreedyCondition resumeGreedyCondition,
			BreakCondition breakCondition,
			FinishCondition finishCondition) {
		super(header,sourceAddress, sourcePosition, destinationAddress, destinationPosition,
				previousNode, nextCluster, lastIntersection, clockwise, startCondition,
				crossingCondition, turnCondition, resumeGreedyCondition,
				breakCondition, finishCondition);
		this.relayCluster = relayCluster;
	}
	
	public RelayHeader(RelayHeader other) {
		super(other);
		this.relayCluster = other.relayCluster;
	}

	public RelayHeader(ContinueHeader other, Address relayCluster) {
		super(other);
		this.relayCluster = relayCluster;
	}

	public LinkLayerInfo copy() {
		return new RelayHeader(this);
	}

	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {

		GeographicClusterRoutingAlgorithm geographicClusterRoutingAlgorithm = (GeographicClusterRoutingAlgorithm)algorithm;
		
		// check if we arrived at the destination node
		Address ownAddress = geographicClusterRoutingAlgorithm.getNeighborDiscoveryService().getOwnAddress();
		if(ownAddress.equals(getReceiver())) {
			handler.deliverMessage(this);
			return;
		}

		// get the sender and the next cluster
		Cluster senderCluster;
		Cluster nextCluster;
		GeographicClusterRoutingHeader header;
		Cluster currentCluster = getCurrentCluster(geographicClusterRoutingAlgorithm);
		if(currentCluster.getAddress().equals(relayCluster)) {
			senderCluster = currentCluster;
			nextCluster = geographicClusterRoutingAlgorithm.getClusterDiscoveryService().getClusterMap().getCluster(this.nextClusterAddress);
			header = new ContinueHeader(this);
		}
		else {
			senderCluster = geographicClusterRoutingAlgorithm.getClusterDiscoveryService().getClusterFromAddress(previousNode.getAddress());
			nextCluster = geographicClusterRoutingAlgorithm.getClusterDiscoveryService().getClusterMap().getCluster(relayCluster);
			header = this;
		}


		// forward the message to the best node regarding this cluster
		forwardOrRestart(handler, geographicClusterRoutingAlgorithm, senderCluster, nextCluster, header);

	}

}
