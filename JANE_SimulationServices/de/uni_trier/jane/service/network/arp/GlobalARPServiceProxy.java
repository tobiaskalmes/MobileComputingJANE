/*
 * Created on Dec 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.arp;

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
public class GlobalARPServiceProxy implements SimulationService, ARPService,ARPClient {

    private SimulationOperatingSystem operatingSystem;
    private ServiceID globalARPServiceID;
    private ServiceID serviceID;

    
    /**
     * 
     */
    public GlobalARPServiceProxy(ServiceID globalARPServiceID) {
        this.globalARPServiceID=globalARPServiceID;
        serviceID=new StackedClassID(getClass().getName(),globalARPServiceID);
        
    }
    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.EvaluationService#start(de.uni_trier.ssds.service.EvaluationOperatingSystem)
     */
    public void start(SimulationOperatingSystem evaluationOperatingSystem) {
        operatingSystem=evaluationOperatingSystem;
        operatingSystem.registerAtService(operatingSystem.getGlobalDeviceID(),globalARPServiceID,GlobalARPService.class);
        
    }



    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getServiceID()
     */
    public ServiceID getServiceID() {
        return serviceID;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#finish()
     */
    public void finish() {
        operatingSystem.sendSignal(operatingSystem.getGlobalDeviceID(),new GlobalARPService.DeregisterDevice());
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getShape()
     */
    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.arp.ARPService#register(de.uni_trier.ssds.service.network.arp.AddressMapping)
     */
    public void register(AddressMapping addressMapping) {
        operatingSystem.sendSignal(operatingSystem.getGlobalDeviceID(), new GlobalARPService.PropagateARPMappingSignal(addressMapping));
        
    }
    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.arp.ARPClient#updateKnownAddressMappings(de.uni_trier.ssds.service.network.arp.ARPAddressMappings)
     */
    public void updateKnownAddressMappings(ARPAddressMappings arpAddressMappings) {
        operatingSystem.sendSignal(new ARPClient.UpdateAddressMappingsSignal(arpAddressMappings));
        
    }
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}

}
