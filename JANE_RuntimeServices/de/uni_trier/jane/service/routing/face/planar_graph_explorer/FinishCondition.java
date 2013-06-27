package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.io.Serializable;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;

/**
 * This condition will notify the current face exploration that it can be
 * finished at the current node. The standard implementation will finish face
 * exploration if the final destination has been reached.
 */
public interface FinishCondition extends Serializable{

	/**
	 * Make transition into the next state.
	 * 
	 * @param currentNode
	 *            the current node of face exploration
	 * @param neighbors
	 *            the one-hop and (possibly two-hop) neighbors of the node where
	 *            face traversal is being applied locally at the moment. Note,
	 *            that this are not necessarily the neighbor nodes of the
	 *            current node of face traversal.
	 * @return the next state of this condition
	 */
	public FinishCondition nextNode(PlanarGraphNode currentNode,
			NetworkNode[] neighbors);

	/**
	 * Check if planar graph exploration can be terminated as successful. This
	 * will normally be the case when the final destination has been reached.
	 * 
	 * @return true if face exploration can be terminated
	 */
	public boolean isSatisfied();

}
