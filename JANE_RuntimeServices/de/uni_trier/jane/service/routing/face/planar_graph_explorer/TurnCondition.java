package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.io.Serializable;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;

/**
 * In each traversal step the planar graph explorer asks the turn condition, if
 * the current face exploration has to be interrupted and continued in the
 * opposite direction. A simple implementation may always return false. A more
 * elaborate implemetnation may limit face exploration to a certain searchable
 * area, while the area is increased each time it is hit by the face
 * exploration.
 */
public interface TurnCondition  extends Serializable{

	/**
	 * Make transition into the next state.
	 * 
	 * @param sourceNode
	 *            the source node of the planar graph routing
	 * @param destinationNode
	 *            the destinaton node of the planar graph routing (address may
	 *            be null)
	 * @param currentNode
	 *            the current node testing for the next hop
	 * @param nextNode
	 *            the currently selected next hop node
	 * @return the next state of this condition
	 */
	public TurnCondition nextEdge(NetworkNode sourceNode,
			NetworkNode destinationNode, PlanarGraphNode currentNode,
			PlanarGraphNode nextNode);

	/**
	 * Check whether the current face exploration has to be be interrupted and
	 * performed in the opposite direction.
	 * 
	 * @return true if face exploration has to be performed in the opposite
	 *         direction
	 */
	public boolean isSatisfied();

}
