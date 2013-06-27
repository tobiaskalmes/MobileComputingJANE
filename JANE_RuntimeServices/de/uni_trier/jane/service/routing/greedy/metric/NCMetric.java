package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;

public class NCMetric implements LocalRoutingMetric {
	
	public double calculate(Position sender, Position destination, Position receiver) {

		double sr = sender.distance(receiver);
		double st = sender.distance(destination);
		double rt = receiver.distance(destination);

		if(rt < st){
			return sr;
		}

		return Double.POSITIVE_INFINITY;

	}
	
	public String toString() {
		return "NC";
	}

}
