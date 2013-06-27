package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;

public class EnergyCrossingCondition implements CrossingCondition {

	private boolean satisfied;
	
	private NeighborDiscoveryService_sync neighborDiscoveryService;
	
	public EnergyCrossingCondition(NeighborDiscoveryService_sync neighborDiscoveryService) {
		this.neighborDiscoveryService = neighborDiscoveryService;
		satisfied = false;
	}
	
	public CrossingCondition nextEdge(NetworkNode sourceNode,
			NetworkNode destinationNode, NetworkNode[] neighbors,
			PlanarGraphNode currentNode, PlanarGraphNode nextNodeCrossing,
			PlanarGraphNode nextNodeNotCrossing) {

		NeighborDiscoveryData crossingData = neighborDiscoveryService.getNeighborDiscoveryData(nextNodeCrossing.getAddress());
		NeighborDiscoveryData noCrossingData = neighborDiscoveryService.getNeighborDiscoveryData(nextNodeNotCrossing.getAddress());
		double crossingEnergy = EnergyStatusData.getRemainingJoule(crossingData);
		double noCrossingEnergy = EnergyStatusData.getRemainingJoule(noCrossingData);
		satisfied = noCrossingEnergy < crossingEnergy;

		return this;
	}

	public boolean isSatisfied() {
		return satisfied;
	}

}
