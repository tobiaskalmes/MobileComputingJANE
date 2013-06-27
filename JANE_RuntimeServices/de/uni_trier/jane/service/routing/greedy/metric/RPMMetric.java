package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;

public class RPMMetric implements LocalRoutingMetric {

	public ContinuousDistribution continuousDistribution;
	
	public RPMMetric(ContinuousDistribution continuousDistribution) {
		this.continuousDistribution = continuousDistribution;
	}
	
	public double calculate(Position sender, Position destination,
			Position receiver) {
		
		Position ts = destination.sub(sender);
		Position tr = destination.sub(receiver);
		
		double dp = ts.mult(tr);
		double len = ts.lengthnosqrt();

		if(0 <= dp && dp <= len) {
			return continuousDistribution.getNext();
		}

		return Double.POSITIVE_INFINITY;

	}

	public String toString() {
		return "RPM";
	}

}
