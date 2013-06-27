package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;

public class RANDOMMetric implements LocalRoutingMetric {

	public ContinuousDistribution continuousDistribution;
	
	public RANDOMMetric(ContinuousDistribution continuousDistribution) {
		this.continuousDistribution = continuousDistribution;
	}
	
	public double calculate(Position sender, Position destination,
			Position receiver) {
		
		double ts = destination.distance(sender);
		double tr = destination.distance(receiver);
		
		if(tr < ts) {
			return continuousDistribution.getNext();
		}

		return Double.POSITIVE_INFINITY;

	}

	public String toString() {
		return "RANDOM";
	}

}
