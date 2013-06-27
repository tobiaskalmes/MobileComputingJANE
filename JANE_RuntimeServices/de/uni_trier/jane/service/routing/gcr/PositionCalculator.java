package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;

public class PositionCalculator {

	protected Position getPosition(NeighborDiscoveryData data, double delta) {
		Position position = LocationData.getPosition(data);
		if(position == null) {
			return null;
		}
		Position direction = SpeedData.getSpeed(data);
		if(direction == null || delta <= 0) {
			return position;
		}
		return position.add(direction.scale(delta));
	}
	
	protected double calculateProgress(Position source, Position destination, Position current) {
		Position ts = destination.sub(source);
		Position tr = destination.sub(current);
		
		double dp = ts.mult(tr);

		return dp;
	}


}
