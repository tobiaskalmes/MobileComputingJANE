package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

public class SimpleFinishCondition implements FinishCondition {

	public static final FinishConditionFactory FACTORY = new FinishConditionFactory() {

		public FinishCondition createFinishCondition(Address destination) {
			return new SimpleFinishCondition(destination);
		}
		public String toString() {
			return "SimpleFinishCondition";
		}
		
	};

	private Address destination;
	
	private boolean satisfied;
	
	public SimpleFinishCondition(Address destination) {
		this.destination = destination;
		satisfied = false;
	}

	public SimpleFinishCondition(Address destination, boolean satisfied) {
		this.destination = destination;
		this.satisfied = satisfied;
	}

	public boolean checkCondition(PlanarGraphNode currentNode, NetworkNode[] neighbors) {
		satisfied = currentNode.getAddress().equals(destination);
		return isSatisfied();
	}

	public FinishCondition nextNode(PlanarGraphNode currentNode, NetworkNode[] neighbors) {
		boolean result = currentNode.getAddress().equals(destination);
		return new SimpleFinishCondition(destination, result);
	}

	public boolean isSatisfied() {
		return satisfied;
	}

}
