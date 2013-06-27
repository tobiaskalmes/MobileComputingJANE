 /* Created on 14.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.winfra;

import java.util.HashMap;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceBS;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceBSO;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author christian.hiedels
 *
 * This global Service maintains the List of Base Stations and forwards Messages between them
 */
public class WinfraBaseStationNetwork implements GlobalService, InterfaceBSO {

	// stores the device id of a basestation and its operating system to be able to generate stubs
	private HashMap baseStationSystems;	// <deviceID / runtimeOperatingSystem>

    // Runtime operating system.
	private GlobalOperatingSystem globalOperatingSystem;
	// The Stub to a BaseStation
	InterfaceBS.BSStub bs;
	// The ServiceID
	private ServiceID serviceID;
	//protected static final ServiceID SERVICE_ID = new EndpointClassID( "Winfra Base Station Organizer" );
	
	public WinfraBaseStationNetwork() {
		this.serviceID = new EndpointClassID(WinfraBaseStationNetwork.class.getName());
		baseStationSystems = new HashMap();
	}
	
    public static void createInstance(ServiceUnit serviceUnit) {
    	serviceUnit.addService(new WinfraBaseStationNetwork(), false);	// no visualisation
    }
    
	public void start(GlobalOperatingSystem globalOperatingSystem) {
		this.globalOperatingSystem = globalOperatingSystem;
	}

	public ServiceID getServiceID() {
		return serviceID;
	}

	public void finish() {
	}

	/**
	 * This Service does not need to be visualised. 
	 */
	public Shape getShape() {
		return null;
	}

	public void getParameters(Parameters parameters) {
		// nop
	}
	
	/**
	 * Store RuntimeOperatingSystems of Devices that are running a BaseStation Service in a List
	 */
	public void registerBaseStation( RuntimeOperatingSystem ros ) {
		if( !baseStationSystems.containsKey( ros.getDeviceID() )) {
			baseStationSystems.put( ros.getDeviceID(), ros );
			//globalOperatingSystem.write("Added a new BaseStation: "+ros.getDeviceID());
		}
	}
	
	/**
	 * Send a Message to a target BaseStation
	 * @param message The Message
	 */
	public void distributeMessage( LinkLayerMessage llm, DeviceID targetBS, DeviceID finalReceiver ) {
		RuntimeOperatingSystem ros = (RuntimeOperatingSystem)baseStationSystems.get( targetBS );
		ServiceID serviceID = ros.getServiceID();
		bs = new InterfaceBS.BSStub( ros, serviceID );
		bs.receiveMessageFromBSO( llm, finalReceiver );
		globalOperatingSystem.write("Distributed the Message ("+llm+") to BaseStation: "+ros.getDeviceID());
	}
}
