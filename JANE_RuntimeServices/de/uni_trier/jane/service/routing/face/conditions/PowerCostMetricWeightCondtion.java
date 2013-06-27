/*
 * @author Stefan Peters
 * Created on 22.04.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * @author Stefan Peters
 */
public class PowerCostMetricWeightCondtion implements StepSelector {

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.planar_graph_explorer.WeightCondition#weightFunction(de.uni_trier.jane.service.routing.planar_graph_explorer.RoutingStep[], de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData[])
	 */
	public int getStepIndex(RoutingStep[] steps, NetworkNode destination,
			NeighborDiscoveryData[] neighborData) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean useReachableNodesOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	public Address getBestNeighbor(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors, NetworkNode destinationNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
