package de.uni_trier.jane.service.neighbor_discovery.views;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;

public class LocationDataView {

	private NeighborDiscoveryService_sync neighborDiscoveryService;

	public LocationDataView(NeighborDiscoveryService_sync neighborDiscoveryService) {
		this.neighborDiscoveryService = neighborDiscoveryService;
	}

	public Position getPosition(Address address) {
		LocationData locationData =
			(LocationData)neighborDiscoveryService.getData(address, LocationData.DATA_ID);
		if(locationData != null) {
			return locationData.getPosition();
		}
		return null;
	}

	public boolean hasPosition(Address address) {
		return getPosition(address) != null;
	}

}
