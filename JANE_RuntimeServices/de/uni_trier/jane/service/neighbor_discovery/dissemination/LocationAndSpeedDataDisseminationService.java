/*
 * Created on 19.05.2005
 */
package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.positioning.PositioningData;
import de.uni_trier.jane.service.positioning.PositioningService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationDataDisseminationService;
import de.uni_trier.jane.service.unit.ServiceUnit;

/**
 * This service utilized the discovery service in order to dissminate
 * the current device location and speed to all nearby network nodes.
 *
 * @author Christoph Lange
 * @see de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationDataDisseminationService
 */
// TODO diese funktionalität könnte mit der der Basisklasse zusammengefasst werden. Immer wenn der
// Positionierungsdienst keine Speedinformationen zur verfügung stellt wird diese von Hand berechnet,
// ansonsten verwendet man die vorhandene Geschwindigkeitsinformation.
public class LocationAndSpeedDataDisseminationService extends
		LocationDataDisseminationService {
    /**
     * position where I was at the last position update
     */
    private Position lastPosition = null;
    /**
     * time when my position was last updated
     */
    private double lastPositionTime = 0;
	/**
	 * @param neighborDiscoveryService
	 * @param locationSystem
	 */
	public LocationAndSpeedDataDisseminationService(
			ServiceID neighborDiscoveryService, ServiceID locationSystem) {
		super(neighborDiscoveryService, locationSystem, false);
	}
	
	/** 
     * Adds a new instance of this service to the given service unit.
     * Instantiates an One-hop neighbor discovery service, if no neighbor
     * discovery service is found in the service unit.
     * 
     * @see de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService
	 */
	public static void createInstance(ServiceUnit serviceUnit) {
    	if (!serviceUnit.hasService(NeighborDiscoveryService.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryService = serviceUnit.getService(NeighborDiscoveryService.class);
    	ServiceID positioningService = serviceUnit.getService(PositioningService.class);
    	Service locationAndSpeedDisseminationService = new LocationAndSpeedDataDisseminationService(neighborDiscoveryService, positioningService);
    	serviceUnit.addService(locationAndSpeedDisseminationService);
    }
	
    /** 
     * stores the position wrapped into the given positioning data. If this
     * method is not called for the first time, it also calculates the average 
     * speed between this call and the last one. 
     */
    public void updatePositioningData(PositioningData info) {
    	Position pos = info.getPosition();

    	/* Bestimme Geschwindigkeit */
    	double time = operatingSystem.getTime();
    	if (lastPosition != null) {
        	double timeDiff = time - lastPositionTime;
        	Position diff = pos.sub(lastPosition);
        	SpeedData data = new SpeedData(new Position(diff.getX() / timeDiff, diff.getY() / timeDiff));
            neighborDiscoveryServiceStub.setOwnData(data);
    	}
    	lastPosition = pos;
    	lastPositionTime = time;
        
        super.updatePositioningData(info);
    }
}
