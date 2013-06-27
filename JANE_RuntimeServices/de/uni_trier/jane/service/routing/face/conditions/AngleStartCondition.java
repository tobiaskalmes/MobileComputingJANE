package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.util.*;

public class AngleStartCondition implements StartCondition {

	public transient static final StartConditionFactory FACTORY = new StartConditionFactory() {
		public StartCondition createStartCondition() {
			return new AngleStartCondition();
		}
		public String toString() {
			return "AngleStart";
		}
	};
	
	private boolean clockwise;

	public AngleStartCondition() {
		clockwise = false;
	}

	private AngleStartCondition(boolean clockwise) {
		this.clockwise = clockwise;
	}

	public boolean isClockwise(PlanarGraphNode startNode,
			PlanarGraphNode cwNode, PlanarGraphNode ccwNode,
			NetworkNode destinationNode, NetworkNode[] neighbors) {

		Position cwPos = cwNode.getPosition();
		Position ccwPos = ccwNode.getPosition();
		Position destPos = destinationNode.getPosition();
		Position startPos = startNode.getPosition();

		double cwa = GeometryCalculations.getCWAngle(cwPos, startPos, destPos);
		double ccwa = GeometryCalculations.getCCWAngle(ccwPos, startPos, destPos);
		return cwa < ccwa;
	}

	public StartCondition nextEdge(PlanarGraphNode startNode, PlanarGraphNode cwNode, PlanarGraphNode ccwNode, NetworkNode destinationNode, NetworkNode[] neighbors) {
		boolean result = isClockwise(startNode, cwNode, ccwNode, destinationNode, neighbors);
		return new AngleStartCondition(result);
	}

	public boolean isClockwise() {
		return clockwise;
	}

}
