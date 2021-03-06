package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

public class BeforeCrossingCondition implements CrossingCondition {

	public static final CrossingConditionFactory FACTORY = new CrossingConditionFactory() {
		public CrossingCondition createCrossingCondition() {
			return new BeforeCrossingCondition();
		}
		public String toString() {
			return "BeforeCrossing";
		}
	};
	
	public CrossingCondition nextEdge(NetworkNode sourceNode,
			NetworkNode destinationNode, NetworkNode[] neighbors,
			PlanarGraphNode currentNode, PlanarGraphNode nextNodeCrossing,
			PlanarGraphNode nextNodeNotCrossing) {
		return this;
	}

	public boolean isSatisfied() {
		return false;
	}

}
