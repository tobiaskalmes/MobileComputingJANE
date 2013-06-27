package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.io.Serializable;

import de.uni_trier.jane.service.planarizer.PlanarGraphNode;

/**
 * A break condition is used in order to notify the planar graph explorer that
 * face routing was not successful. Face exploration will stop if break
 * condition returns true. The standard implementation may return true if the
 * first edge of the current face traversal is being traversed twice in the same
 * direction.
 */
public interface BreakCondition extends Serializable{

	/**
	 * Make transition into the next state.
	 * 
	 * @param currentNode
	 *            the current node of planar graph traversal.
	 * @param nextNode
	 *            the next selected node of graph traversal
	 * @param faceChanged
	 *            true if face changes in this routing step
	 * @return the next state of this condition
	 */
	public BreakCondition nextState(PlanarGraphNode currentNode,
			PlanarGraphNode nextNode, boolean faceChanged);

	/**
	 * Check if planar graph traversal was not successful and has to be stopped.
	 * 
	 * @return true if planar graph traversal has to be finished as not
	 *         successful
	 */
	public boolean isSatisfied();

}
