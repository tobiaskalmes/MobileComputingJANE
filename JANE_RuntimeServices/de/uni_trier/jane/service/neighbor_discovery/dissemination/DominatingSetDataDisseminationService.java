package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.dominating_set.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This service utilized a neighbor discovery service in order to dissminate
 * the current dominating set membership to all nearby network nodes.
 */
public class DominatingSetDataDisseminationService implements RuntimeService, DominatingSetListener {

	// intitialized in constructor
    private ServiceID neighborDiscoveryServiceID;
    private ServiceID dominatingSetServiceID;

    // initialized on startup
    private NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;
    private DominatingSetServiceStub dominatingSetServiceStub;

    public static void createInstance(ServiceUnit serviceUnit) {
    	if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryServiceID = serviceUnit.getService(NeighborDiscoveryService_sync.class);
    	ServiceID dominatingSetServiceID = serviceUnit.getService(DominatingSetService.class);
    	Service dominatingSetDataDissmeninationID = new DominatingSetDataDisseminationService(neighborDiscoveryServiceID, dominatingSetServiceID);
    	serviceUnit.addService(dominatingSetDataDissmeninationID);
    }
    
    /**
     * Construct a new location dissemination service.
     */
    public DominatingSetDataDisseminationService(ServiceID neighborDiscoveryService, ServiceID dominatingSetServiceID) {
    	this.neighborDiscoveryServiceID = neighborDiscoveryService;
    	this.dominatingSetServiceID = dominatingSetServiceID;
    }

    public ServiceID getServiceID() {
    	return null;
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
    	neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(runtimeOperatingSystem, neighborDiscoveryServiceID);
    	dominatingSetServiceStub = new DominatingSetServiceStub(runtimeOperatingSystem, dominatingSetServiceID);
    	updateMembership(dominatingSetServiceStub.isMember());
    	dominatingSetServiceStub.register();
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

	public void updateMembership(boolean membership) {
		DominatingSetData dominatingSetData = new DominatingSetData(membership);
		neighborDiscoveryServiceStub.setOwnData(dominatingSetData);
	}

}
