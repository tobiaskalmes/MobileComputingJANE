package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;

public class MFRBRMetric implements LocalRoutingMetric {

	public double calculate(Position sender, Position destination,
			Position receiver) {
		
		Position ts = destination.sub(sender);
		Position tr = destination.sub(receiver);
		
		double dp = ts.mult(tr);

		if(dp >= 0) {
			return dp;
		}

		return Double.POSITIVE_INFINITY;

	}

	public String toString() {
		return "MFRBR";
	}

}
