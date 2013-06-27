package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.io.Serializable;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This condition will notify the current face exploration that it can be
 * finished at the current node and greedy routing can be applied once again.
 */
public interface ResumeGreedyCondition extends Serializable{

	/**
	 * Make transition into the next state.
	 * 
	 * @param startNode
	 *            the start node where planar graph exploration was started
	 * @param destinationNode
	 *            the final destination node
	 * @param currentNode
	 *            the current node of face exploration traversal is being
	 *            applied locally at the moment. Note, that this are not
	 *            necessarily the neighbor nodes of the current node of face
	 *            traversal.
	 * @return the next state of this condition
	 */
	public ResumeGreedyCondition nextNode(NetworkNode startNode,
			NetworkNode destinationNode, NetworkNode greedyFailureNode,
			PlanarGraphNode currentNode, NetworkNode[] neighbors);

	/**
	 * Check if planar graph exploration can be terminated and greedy routing
	 * has to be resumed
	 * 
	 * @return true if face exploration can be terminated
	 */
	public boolean isSatisfied();

	public Shape getShape();
	
}
