/*
 * Sollte funktionieren
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * An implementation of the break Condition. If an edge in the same face is used
 * twice in the same direction, it returns true, else it returns false. If a
 * face changes a new edge is stored.
 * 
 * @author Stefan Peters
 * 
 */
public class BreakConditionImpl implements BreakCondition {

	public static final BreakConditionFactory FACTORY = new BreakConditionFactory() {

		public BreakCondition createBreakCondition() {
			return new BreakConditionImpl();
		}
		
	};

	private Address current;

	private Address next;
	
	private boolean satisfied = false;
	

	public boolean checkCondition(PlanarGraphNode currentNode,
			PlanarGraphNode nextNode, boolean faceChanged) {

		// If the packet is in a new face, store the first edge in the new face
		if (faceChanged) {
			current = currentNode.getAddress();
			next = nextNode.getAddress();
			return false;
		}

		// Check whether an edge is used twice
		return nextNode.getAddress().equals(next)
				&& currentNode.getAddress().equals(current);

	}



	public BreakCondition nextState(PlanarGraphNode currentNode, PlanarGraphNode nextNode, boolean faceChanged) {
		BreakConditionImpl result = new BreakConditionImpl();
		
		// If the packet is in a new face, store the first edge in the new face
		if (faceChanged) {
			current = currentNode.getAddress();
			next = nextNode.getAddress();
			result.current = current;
			result.next = next;
		}
		else {

			// Check whether an edge is used twice
			if(nextNode.getAddress().equals(next)
					&& currentNode.getAddress().equals(current)) {
				result.satisfied = true;
			}

			result.current = this.current;
			result.next = this.next;
		}

		
		return result;
	}



	public boolean isSatisfied() {
		return satisfied;
	}

}