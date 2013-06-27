/*
 * Created on 20.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.address_device_map;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.address_device_map.GlobalAddressDeviceMapService.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationAddressDeviceMapService implements SimulationService, LocalAddressDeviceMapService {

	private static final ServiceID SERVICE_ID = new EndpointClassID(SimulationAddressDeviceMapService.class.getName());
	
	private ServiceID globalAddressDeviceMapServiceID;
	private ServiceID linkLayerServiceID;
	private SimulationOperatingSystem simulationOperatingSystem;
	private DeviceID deviceID;

    private GlobalAddressDeviceMap globalAddressDeviceMap;

	public static void createInstance(ServiceUnit serviceUnit) {
		ServiceID globalAddressDeviceMapServiceID = serviceUnit.getService(GlobalAddressDeviceMapService.class);
		ServiceID linkLayerServiceID = serviceUnit.getService(LinkLayer.class);
		serviceUnit.addService(new SimulationAddressDeviceMapService(globalAddressDeviceMapServiceID, linkLayerServiceID));
	}
	
	/**
	 * @param globalAddressDeviceMapServiceID
	 */
	public SimulationAddressDeviceMapService(ServiceID globalAddressDeviceMapServiceID, ServiceID linkLayerServiceID) {
		this.globalAddressDeviceMapServiceID = globalAddressDeviceMapServiceID;
		this.linkLayerServiceID= linkLayerServiceID;
	}
	
	public void start(SimulationOperatingSystem simulationOperatingSystem) {
		this.simulationOperatingSystem = simulationOperatingSystem;
		deviceID = simulationOperatingSystem.getDeviceID();
		LinkLayer.LinkLayerStub linkLayerFacade = new LinkLayer.LinkLayerStub(simulationOperatingSystem, linkLayerServiceID);
		Address address = linkLayerFacade.getLinkLayerProperties().getLinkLayerAddress();
		Signal signal = new GlobalAddressDeviceMapService.PutSignal(deviceID, address);
		simulationOperatingSystem.sendSignal(simulationOperatingSystem.getGlobalDeviceID(), globalAddressDeviceMapServiceID, signal);
		globalAddressDeviceMap=new GlobalAddressDeviceMapService.GlobalAddressDeviceMap(globalAddressDeviceMapServiceID,simulationOperatingSystem);
	}
	
	public void finish() {
		Signal signal = new GlobalAddressDeviceMapService.RemoveSignal(deviceID);
		simulationOperatingSystem.sendSignal(simulationOperatingSystem.getGlobalDeviceID(), globalAddressDeviceMapServiceID, signal);
	}
	
	public ServiceID getServiceID() {
		return SERVICE_ID;
	}
	
	public Shape getShape() {
		return null;
	}
	
	public DeviceID getDeviceID(Address linkLayerAddress) {
//		GlobalAddressDeviceMapService.GetDeviceIDAccess access =
//			new GlobalAddressDeviceMapService.GetDeviceIDAccess(linkLayerAddress);
//		simulationOperatingSystem.accessSynchronous(simulationOperatingSystem.getGlobalDeviceID(),
//				globalAddressDeviceMapServiceID, access);
//		return access.getDeviceID();
	    return globalAddressDeviceMap.getDeviceID(linkLayerAddress);
	}

	public Address getLinkLayerAddress(DeviceID deviceID) {
//		GlobalAddressDeviceMapService.GetLinkLayerAddressAccess access =
//			new GlobalAddressDeviceMapService.GetLinkLayerAddressAccess(deviceID);
//		return access.getLinkLayerAddress();
	    return globalAddressDeviceMap.getLinkLayerAddress(deviceID);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}
	
}
