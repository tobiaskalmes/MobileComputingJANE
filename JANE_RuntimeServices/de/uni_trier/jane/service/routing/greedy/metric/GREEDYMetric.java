/*
 * @author Stefan Peters
 * Created on 15.04.2005
 */
package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;

/**
 * @author Stefan Peters
 */
public class GREEDYMetric implements LocalRoutingMetric {
	public double calculate(Position sender, Position destination, Position receiver) {
		double distance=receiver.distance(destination);
		if(distance < sender.distance(destination)){
			return distance;
		}
		return Double.POSITIVE_INFINITY;
	}

	public String toString() {
		return "GREEDY";
	}

}
