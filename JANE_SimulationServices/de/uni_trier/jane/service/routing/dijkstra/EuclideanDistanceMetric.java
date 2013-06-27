package de.uni_trier.jane.service.routing.dijkstra;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;

/**
 * This weight implemtation uses the Euclidean distance between two nodes
 * as the weight of an edge. The weight is infinite if there is no edge
 * between source and destiantion.
 */
public class EuclideanDistanceMetric implements WeightFunction {

	public double getWeight(DeviceID source, DeviceID destination, GlobalKnowledge globalKnowledge) {
	    if(globalKnowledge.isConnected(source, destination)) {
			Position position1 = globalKnowledge.getTrajectory(source).getPosition();
			Position position2 = globalKnowledge.getTrajectory(destination).getPosition();
			return position1.distance(position2);
	    }
	    return Double.POSITIVE_INFINITY;
	}

	public String toString() {
		return "Euclidean";
	}

}
