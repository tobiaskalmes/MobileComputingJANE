/*
 * Created on 20.03.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * This class is an implementation of <CODE>WeightCondtion<CODE>. It minimizes the hopcount
 * of face routing.
 * @author Stefan Peters
 */
public class HopCountWeightCondition implements StepSelector {

	public int getStepIndex(RoutingStep[] steps, NetworkNode destination, NeighborDiscoveryData[] neighborDiscoveryDatas) {
		return steps.length-1;
	}

	public boolean useReachableNodesOnly() {
		return true;
	}

	public Address getBestNeighbor(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors, NetworkNode destinationNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
