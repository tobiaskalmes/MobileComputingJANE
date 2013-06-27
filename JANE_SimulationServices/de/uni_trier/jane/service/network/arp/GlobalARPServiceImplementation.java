/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.arp;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.arp.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GlobalARPServiceImplementation implements 
		GlobalARPService, GlobalService{

	private static ServiceID serviceID;
	
	
	private HashMap deviceIDToAdressMappingMap;
    private GlobalOperatingSystem operatingSystem;
	
	/**
	 * 
	 */
	public GlobalARPServiceImplementation(ServiceID transportLayerServiceID) {
		serviceID=new StackedClassID(getClass().getName(),transportLayerServiceID);
	}
 
	public void propagateARPMapping( AddressMapping addressMapping) {
		deviceIDToAdressMappingMap.put(operatingSystem.getCallingDeviceID(),addressMapping);
		operatingSystem.sendSignal(new ARPClient.UpdateAddressMappingsSignal(new ARPAddressMappings(deviceIDToAdressMappingMap)));		
	}


	public ServiceID getServiceID() {
		return serviceID;
	}


	public void finish() {
		// TODO Auto-generated method stub
		
	}



	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}
   
    public void start(GlobalOperatingSystem globalOperatingSystem) {
        operatingSystem=globalOperatingSystem;
        
    }

 

    public void deregisterDevice() {
        deviceIDToAdressMappingMap.remove(operatingSystem.getCallingDeviceID());
        operatingSystem.sendSignal(new ARPClient.UpdateAddressMappingsSignal(new ARPAddressMappings(deviceIDToAdressMappingMap)));
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}
}
