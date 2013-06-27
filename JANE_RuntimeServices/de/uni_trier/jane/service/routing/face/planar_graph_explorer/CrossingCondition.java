package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.io.Serializable;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;

/**
 * Use this interface in order to describe the condition used by the planar
 * graph explorer in order to decide if an edge crossing the line connecting
 * source and destination node has to be traversed or not. A simple
 * implementation may always return true (crossing heuristic) or it may always
 * return false (no-crossing heuristic).
 */
public interface CrossingCondition extends Serializable{

	/**
	 * Make transition into the next state.
	 * 
	 * @param sourceNode
	 *            the source node of the planar graph routing
	 * @param destinationNode
	 *            the destination node of the planar graph routing
	 * @param neighbors
	 *            the neighbor nodes of the node performing planar graph routing
	 *            locally at the moment (Note, that this is not necessarily the
	 *            currentNode)
	 * @param currentNode
	 *            the current node of face exploration
	 * @param nextNodeCrossing
	 *            the next hop when crossing the line
	 * @param nextNodeNotCrossing
	 *            the next hop when not crossing the line
	 * @return the next state of this condition
	 */
	public CrossingCondition nextEdge(NetworkNode sourceNode,
			NetworkNode destinationNode, NetworkNode[] neighbors,
			PlanarGraphNode currentNode, PlanarGraphNode nextNodeCrossing,
			PlanarGraphNode nextNodeNotCrossing);

	/**
	 * Check whether the edge crossing the remaining straight line connecting
	 * source and destination node has to be traversed or omitted.
	 * 
	 * @return true if the crossing edge has to be used
	 */
	public boolean isSatisfied();

}
