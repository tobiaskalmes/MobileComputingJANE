package de.uni_trier.jane.service.routing.dijkstra;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;

/**
 * The weight function is used by the global knowledge graph in order to determine the
 * costs of an edge between two neighbor nodes. An implementation should return positive
 * infinity for al node pairs which are not connected. For instance, a simple implementation
 * of this interface may return 1 if there is an edge and infinity if there is no edge (hop
 * count metric).
 */
public interface WeightFunction {

    /**
     * Determine the weight of the edge between the source and destination node.
     * @param source the source node
     * @param destination the destination node
     * @param globalKnowledge the global known network graph
     * @return the weight between source and destiantion. Return positive infinity if
     * there is no edge between the two nodes.
     */
    public double getWeight(DeviceID source, DeviceID destination, GlobalKnowledge globalKnowledge);

}
