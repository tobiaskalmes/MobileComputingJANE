package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;

public class GEDIRMetric implements LocalRoutingMetric {

	public double calculate(Position sender, Position destination, Position receiver) {
		
		double rt = receiver.distance(destination);
		double st = sender.distance(destination);

		return rt * st;

	}

	public String toString() {
		return "GEDIR";
	}

}
