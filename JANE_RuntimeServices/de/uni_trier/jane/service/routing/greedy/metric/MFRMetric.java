package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;

public class MFRMetric implements LocalRoutingMetric {

	public double calculate(Position sender, Position destination,
			Position receiver) {
		
		Position ts = destination.sub(sender);
		Position tr = destination.sub(receiver);
		
		double dp = ts.mult(tr);
		double len = ts.lengthnosqrt();

		if(0 <= dp && dp <= len) {
			return dp;
		}

		return Double.POSITIVE_INFINITY;

	}

	public String toString() {
		return "MFR";
	}

}
