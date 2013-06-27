package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.util.*;

public class ClockwiseStartCondition implements StartCondition {

	public static final StartConditionFactory FACTORY = new StartConditionFactory() {
		public StartCondition createStartCondition() {
			return new ClockwiseStartCondition();
		}
		public String toString() {
			return "ClockwiseStart";
		}
	};

	public StartCondition nextEdge(PlanarGraphNode startNode, PlanarGraphNode cwNode, PlanarGraphNode ccwNode, NetworkNode destinationNode, NetworkNode[] neighbors) {
		return this;
	}

	public boolean isClockwise() {
		return true;
	}

}
