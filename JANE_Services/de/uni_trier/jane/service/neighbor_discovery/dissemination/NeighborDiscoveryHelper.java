package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;

public class NeighborDiscoveryHelper {

	public static Position getPosition(NeighborDiscoveryData data) {
		LocationData locationData = (LocationData)data.getDataMap().getData(LocationData.DATA_ID);
		if(locationData != null) {
			return locationData.getPosition();
		}
		return null;
	}
	
}
