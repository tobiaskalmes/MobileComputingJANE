package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.util.*;

public class AngleCrossingCondition implements CrossingCondition {

	public static final CrossingConditionFactory FACTORY = new CrossingConditionFactory() {
		public CrossingCondition createCrossingCondition() {
			return new AngleCrossingCondition();
		}
		public String toString() {
			return "AngleCrossing";
		}
	};
	
	private boolean satisfied = false;

	public AngleCrossingCondition() {
		satisfied = false;
	}

	private AngleCrossingCondition(boolean satisfied) {
		this.satisfied = satisfied;
	}

	public CrossingCondition nextEdge(NetworkNode sourceNode,
			NetworkNode destinationNode, NetworkNode[] neighbors,
			PlanarGraphNode currentNode, PlanarGraphNode nextNodeCrossing,
			PlanarGraphNode nextNodeNotCrossing) {
		
		Position cwPos = nextNodeCrossing.getPosition();
		Position ccwPos = nextNodeNotCrossing.getPosition();
		Position destPos = destinationNode.getPosition();
		Position startPos = currentNode.getPosition();

		double cwa = GeometryCalculations.getAngle(cwPos, startPos, destPos);
		double ccwa = GeometryCalculations.getAngle(ccwPos, startPos, destPos);

		return new AngleCrossingCondition(cwa < ccwa);
	}

	public boolean isSatisfied() {
		return satisfied;
	}

}
