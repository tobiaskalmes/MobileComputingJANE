package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.io.Serializable;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;

/**
 * A class implementing this interface is always asked when planar graph
 * traversal is invoked at the first node in order to determine if traversal has
 * to be done in clockwise or counterclockwise direction. A simple
 * implementation may always return true for instance.
 */
public interface StartCondition extends Serializable{

	/**
	 * Make transition into the next state.
	 * 
	 * @param startNode
	 *            the start node of face routing
	 * @param cwNode
	 *            the node in clockwise direction
	 * @param ccwNode
	 *            the node in counterclockwise direction
	 * @param destinationNode
	 *            the destination node (address may be null)
	 * @param neighbors
	 *            the one-hop (and possibly two-hop) neighbor nodes of the
	 *            source node
	 * @return the next state of this condition
	 */
	public StartCondition nextEdge(PlanarGraphNode startNode,
			PlanarGraphNode cwNode, PlanarGraphNode ccwNode,
			NetworkNode destinationNode, NetworkNode[] neighbors);

	/**
	 * Check whether the first node has to be determined in clockwise or
	 * counterclockwise direction from the line connecting source and
	 * destination node.
	 * 
	 * @return true if the first node has to be searched in clockwise direction
	 */
	public boolean isClockwise();

}
