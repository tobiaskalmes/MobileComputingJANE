package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.util.*;

public class DIRMetric implements LocalRoutingMetric {

	public double calculate(Position sender, Position destination,
			Position receiver) {
		
		double angle = GeometryCalculations.getAngle(receiver, sender, destination);
		
		return angle;

	}
	
	public String toString() {
		return "DIR";
	}

}
