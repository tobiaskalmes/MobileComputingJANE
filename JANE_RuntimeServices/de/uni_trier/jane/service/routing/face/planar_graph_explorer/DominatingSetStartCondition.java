/*
 * @author Stefan Peters
 * Created on 22.03.2005
 */
package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.NetworkNode;

/**
 * This condition is only used for start routing with dominating set.
 * @author Stefan Peters
 */
public interface DominatingSetStartCondition {
	
	/**
	 * If current node is not a dominating set node, use this condition to choose the startnode for facerouting
	 * @param dominatingSetNeighbors All oneHopNeighbors in the dominating set 
	 * @param destination The destination of routing
	 * @return The chosen Node
	 */
	public NetworkNode chooseStartNode(NetworkNode[] dominatingSetNeighbors,Position destination);
}
