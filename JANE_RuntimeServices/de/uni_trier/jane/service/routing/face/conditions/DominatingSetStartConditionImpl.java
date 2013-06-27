/*
 * @author Stefan Peters
 * Created on 23.05.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;


/**
 * This Condtion is used, if routing with dominating set neighbors starts in a node, which is not in a dominating set.
 * @author Stefan Peters
 */
public class DominatingSetStartConditionImpl implements DominatingSetStartCondition {

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.planar_graph_explorer.DominatingSetStartCondition#chooseStartNode(de.uni_trier.jane.service.planarizer.NetworkNode[], de.uni_trier.jane.service.planarizer.NetworkNode)
	 */
	public NetworkNode chooseStartNode(
			NetworkNode[] dominatingSetNeighbors,
			Position destination) {
		//Simple Implementation, send to neighbor with the shortest
		// distance to destination
		NetworkNode chosen = dominatingSetNeighbors[0];
		for (int i = 1; i < dominatingSetNeighbors.length; i++) {
			if (dominatingSetNeighbors[i].getPosition().distance(
					destination) < chosen.getPosition()
					.distance(destination)) {
				chosen = dominatingSetNeighbors[i];
			}
		}
		return chosen;
	}
}