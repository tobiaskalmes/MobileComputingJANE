/*****************************************************************************
 * 
 * LocationDataDisseminationService.java
 * 
 * $Id: LocationDataDisseminationService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This service utilized the discovery service in order to dissminate
 * the current device location to all nearby network nodes.
 */
public class LocationDataDisseminationService implements RuntimeService, PositioningListener {

	/**
	 * The CVS version number of this class.
	 */
	public static final String VERSION = "$Id: LocationDataDisseminationService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $";
	
    private ServiceID neighborDiscoveryService;
    private ServiceID locationSystem;
    private boolean includeSpeedVector;

    // initialized on startup
    protected RuntimeOperatingSystem operatingSystem;
    protected NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;

    /**
	 * Insert this service into the given service unit. by default the serviec will
	 * not dissmeinate speed vectors.
	 * @param serviceUnit the service unit, where this service has to be inserted
	 */
    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, false);
    }
    
    /**
	 * Insert this service into the given service unit.
	 * @param serviceUnit the service unit, where this service has to be inserted
	 * @param includeSpeedVector if <code>true</code> the current device speed and direction
	 * will disseminated as well
	 */
    public static void createInstance(ServiceUnit serviceUnit, boolean includeSpeedVector) {
    	if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryService = serviceUnit.getService(NeighborDiscoveryService_sync.class);
    	ServiceID positioningService = serviceUnit.getService(PositioningService.class);
    	Service locationDataDisseminationService = new LocationDataDisseminationService(
    			neighborDiscoveryService, positioningService, includeSpeedVector);
    	serviceUnit.addService(locationDataDisseminationService);
    }
    
    /**
     * Construct a new location dissemination service.
     * @param neighborDiscoveryID the ID of the neighbor discovery service used to disseminate the positioning data
     * @param positioningID the ID of the service used to determine the positioning data
     * @param includeSpeedVector if <code>true</code> the current device speed and direction will disseminated as well
     */
    public LocationDataDisseminationService(ServiceID neighborDiscoveryID,
    		ServiceID positioningID, boolean includeSpeedVector) {
    	this.neighborDiscoveryService = neighborDiscoveryID;
    	this.locationSystem = positioningID;
    	this.includeSpeedVector = includeSpeedVector;
    }

    public ServiceID getServiceID() {
    	return null;
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
    	this.operatingSystem = runtimeOperatingSystem;
    	neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(operatingSystem, neighborDiscoveryService);
    	operatingSystem.registerAtService(locationSystem, PositioningService.class);
    	PositioningService.PositioningServiceFassade positioningService=new PositioningService.PositioningServiceFassade(locationSystem,runtimeOperatingSystem);
    	updatePositioningData(positioningService.getPositioningData());
    }

    public void finish() {
    	// ignore
    }

    public Shape getShape() {
        return null;
    }

    public void updatePositioningData(PositioningData info) {
        Data data = new LocationData(info.getPosition());
        neighborDiscoveryServiceStub.setOwnData(data);
        if(includeSpeedVector) {
        	data = new SpeedData(info.getDirection());
        	neighborDiscoveryServiceStub.setOwnData(data);
        }
    }

	public void getParameters(Parameters parameters) {
		parameters.addParameter("includeSpeedVector", includeSpeedVector);
	}

}
