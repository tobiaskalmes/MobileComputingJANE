/*
 * Wird noch nicht benoetigt
 * Created on 23.09.2004
 *
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * This is simple. It only returns false
 * @author Stefan Peters
 *
 */
public class SimpleTurnCondition implements TurnCondition {

    public static final TurnConditionFactory FACTORY = new TurnConditionFactory() {
		public TurnCondition createTurnCondition() {
			return new SimpleTurnCondition();
		}
    };

    public boolean checkCondition(NetworkNode sourceNode,
            NetworkNode destinationNode, PlanarGraphNode currentNode,
            PlanarGraphNode nextNode) {
        return false;
    }

	public TurnCondition nextEdge(NetworkNode sourceNode, NetworkNode destinationNode, PlanarGraphNode currentNode, PlanarGraphNode nextNode) {
		return this;
	}

	public boolean isSatisfied() {
		return false;
	}

}
