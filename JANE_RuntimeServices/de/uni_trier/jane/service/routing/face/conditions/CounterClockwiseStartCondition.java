package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

public class CounterClockwiseStartCondition implements StartCondition {

	public static final StartConditionFactory FACTORY = new StartConditionFactory() {
		public StartCondition createStartCondition() {
			return new CounterClockwiseStartCondition();
		}
		public String toString() {
			return "CounterClockwiseStart";
		}
	};

	public StartCondition nextEdge(PlanarGraphNode startNode, PlanarGraphNode cwNode, PlanarGraphNode ccwNode, NetworkNode destinationNode, NetworkNode[] neighbors) {
		return this;
	}

	public boolean isClockwise() {
		return false;
	}

}
