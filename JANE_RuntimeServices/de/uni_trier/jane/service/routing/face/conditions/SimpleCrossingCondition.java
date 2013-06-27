/*
 * Funktioniert
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * This class implments a simple crossing condition. It always returns the value
 * it was initialized with. 
 * 
 * @author Stefan Peters
 *
 */
public class SimpleCrossingCondition implements CrossingCondition {
    
    private boolean crossing;

    /**
     * The Constructor
     * @param crossing true, if to cross the line, false, if not
     */
    public SimpleCrossingCondition(boolean crossing){
        this.crossing=crossing;
    }

//    public boolean crossEdge(NetworkNode sourceNode,
//            NetworkNode destinationNode, NetworkNode[] neighbors,
//            PlanarGraphNode currentNode, PlanarGraphNode nextNodeCrossing,
//            PlanarGraphNode nextNodeNotCrossing) {
//        if(nextNodeNotCrossing==null){
//            return true;
//        }
//        return crossing;
//    }

	public CrossingCondition nextEdge(NetworkNode sourceNode, NetworkNode destinationNode, NetworkNode[] neighbors, PlanarGraphNode currentNode, PlanarGraphNode nextNodeCrossing, PlanarGraphNode nextNodeNotCrossing) {
		return new SimpleCrossingCondition(crossing);
	}

	public boolean isSatisfied() {
		return crossing;
	}

}
