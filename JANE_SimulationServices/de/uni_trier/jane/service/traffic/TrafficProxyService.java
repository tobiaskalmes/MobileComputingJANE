/*****************************************************************************
 * 
 * TrafficProxyService.java
 * 
 * $Id: TrafficProxyService.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.traffic;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.address_device_map.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.*;
import de.uni_trier.jane.service.routing.unicast.*;
import de.uni_trier.jane.service.traffic.anycast.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements the proxy service listening for traffic actions
 * send by a global service. The global service will use the public defined
 * service id in order to send its traffic actions to the proxy service.
 */
public class TrafficProxyService implements SimulationService {

	/**
	 * The service id used by this service.
	 */
	public static ServiceID SERVICE_ID = new EndpointClassID(TrafficProxyService.class.getName());

	// initialized in constructor
	private ServiceID addressDeviceMapServiceID;
	private ServiceID routingServiceID;
	private ServiceID routingAlgorithmID;

	// intialized on startup
	private AddressDeviceMapService.AddressDeviceMapFassade addressDeviceMapServiceStub;
	private RoutingService routingServiceStub;

	private SimulationOperatingSystem operatingService;

    private ServiceID anycastRoutingAlgorithm;

	public static ServiceID createInstance(ServiceUnit serviceUnit) {
        ServiceID unicast=null;
		if (serviceUnit.hasService(UnicastRoutingAlgorithm_Sync.class)){
            unicast = serviceUnit.getService(UnicastRoutingAlgorithm_Sync.class);
        }
        ServiceID anycast=null;
        if (serviceUnit.hasService(LocationRoutingAlgorithm_Sync.class)){
            anycast=serviceUnit.getService(LocationRoutingAlgorithm_Sync.class);
        }
        
		return createInstance(serviceUnit,unicast,anycast);
	}
    
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID routingAlgorithmID, ServiceID anycastRoutingAlgorithm) {
        ServiceID addressDeviceMapServiceID = serviceUnit.getService(LocalAddressDeviceMapService.class);
        if(!serviceUnit.hasService(RoutingService.class)) {
            DefaultRoutingService.createInstance(serviceUnit);
        }
        ServiceID routingServiceID = serviceUnit.getService(RoutingService.class);
        TrafficProxyService trafficProxy = new TrafficProxyService(addressDeviceMapServiceID, routingServiceID, routingAlgorithmID,anycastRoutingAlgorithm);
        return serviceUnit.addService(trafficProxy);
    }
	
	/**
	 * @param routingServiceID
	 * @param anycastRoutingAlgorithm 
	 */
	public TrafficProxyService(ServiceID addressDeviceMapServiceID,
			ServiceID routingServiceID, ServiceID routingAlgorithmID, ServiceID anycastRoutingAlgorithm) {
		this.addressDeviceMapServiceID = addressDeviceMapServiceID;
		this.routingServiceID = routingServiceID;
		this.routingAlgorithmID = routingAlgorithmID;
        this.anycastRoutingAlgorithm=anycastRoutingAlgorithm;
	}
	
	public void start(SimulationOperatingSystem simulationOperatingSystem) {
        addressDeviceMapServiceStub = new AddressDeviceMapService.AddressDeviceMapFassade(addressDeviceMapServiceID, simulationOperatingSystem);
        routingServiceStub=(RoutingService)simulationOperatingSystem.getSignalListenerStub(
                routingServiceID,
                RoutingService.class
        );
        operatingService=simulationOperatingSystem;
		//routingServiceStub = new RoutingServiceStub(simulationOperatingSystem, routingServiceID);
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {
		return null;
	}

	public void getParameters(Parameters parameters) {
		// ignore
	}

	public void startUnicast(DeviceID receiver, int payload) {
		Address receiverAddress = addressDeviceMapServiceStub.getAddress(receiver);
	    RoutingData data = new Payload(payload);
	    routingServiceStub.startUnicast(routingAlgorithmID, data, receiverAddress);
	}

	public void startRoutingTask(RoutingHeader header, int payload) {
		RoutingData data = new Payload(payload);
		routingServiceStub.startRoutingTask(header,data);
	}
	
	public void startAnyCast(GeographicLocation location,int payload) {
		LocationRoutingAlgorithm_Sync locationRoutingAlgorithm_Sync=(LocationRoutingAlgorithm_Sync)operatingService.getAccessListenerStub(anycastRoutingAlgorithm,LocationRoutingAlgorithm_Sync.class);
		startRoutingTask(locationRoutingAlgorithm_Sync.getLocationRoutingHeader(location),payload);
	}
	
	private static class Payload implements RoutingData {

	    private int size;

        public Payload(int size) {
            this.size = size;
        }

        public Dispatchable copy() {
            return this;
        }

        public int getSize() {
            return size;
        }

        //
        public void handle(RoutingHeader routingHeader,
                SignalListener signalListener) {
            ((TrafficProxyService)signalListener).receive(routingHeader);
        }

        public Class getReceiverServiceClass() {
            return TrafficProxyService.class;
        }
        //
        public Shape getShape() {
            return null;
        }
        
	    
	}

    /**
     * TODO: comment method 
     * @param routingHeader
     */
    public void receive(RoutingHeader routingHeader) {
        // TODO Auto-generated method stub
        
    }

}
