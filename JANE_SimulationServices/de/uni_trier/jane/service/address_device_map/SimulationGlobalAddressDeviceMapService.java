/*
 * Created on 20.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.address_device_map;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationGlobalAddressDeviceMapService implements GlobalService, GlobalAddressDeviceMapService {

	private static final ServiceID SERVICE_ID = new EndpointClassID(SimulationGlobalAddressDeviceMapService.class.getName());

	private Map addressDeviceMap;
	private Map deviceAddressMap;

	public static void createInstance(ServiceUnit serviceUnit) {
		serviceUnit.addService(new SimulationGlobalAddressDeviceMapService());
		serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				SimulationAddressDeviceMapService.createInstance(serviceUnit);
			}
		});
	}
	
	public SimulationGlobalAddressDeviceMapService() {
		addressDeviceMap = new HashMap();
		deviceAddressMap = new HashMap();
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
		// ignore
	}
	
	public void finish() {
		// ignore
	}
	
	public ServiceID getServiceID() {
		return SERVICE_ID;
	}
	
	public Shape getShape() {
		return null;
	}

	public void put(DeviceID deviceID, Address linkLayerAddress) {
		deviceAddressMap.put(deviceID, linkLayerAddress);
		addressDeviceMap.put(linkLayerAddress, deviceID);
	}

	public void remove(DeviceID deviceID) {
		addressDeviceMap.remove(deviceAddressMap.remove(deviceID));
	}
	
	public DeviceID getDeviceID(Address linkLayerAddress) {
		return (DeviceID)addressDeviceMap.get(linkLayerAddress);
	}
	
	public Address getLinkLayerAddress(DeviceID deviceID) {
		return (Address)deviceAddressMap.get(deviceID);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}
	
}
