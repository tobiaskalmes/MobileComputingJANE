package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.energy.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This service utilizes a neighbor discovery service in order to dissminate
 * the current remaining energy of this device to all nearby network nodes.
 */
public class EnergyDataDisseminationService implements RuntimeService {

	// intitialized in constructor
    private ServiceID neighborDiscoveryServiceID;
    private ServiceID energyStatusProviderServiceID;
    private double updateDelta;

    // initialized on startup
    private RuntimeOperatingSystem operatingSystem;
    private NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;
    private EnergyStatusProviderServiceStub energyStatusProviderServiceStub;

    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, 1.0);
    }
    
    public static void createInstance(ServiceUnit serviceUnit, double updateDelta) {
    	if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryServiceID = serviceUnit.getService(NeighborDiscoveryService_sync.class);
    	ServiceID energyStatusProviderServiceID = serviceUnit.getService(EnergyStatusProviderService.class);
    	Service service = new EnergyDataDisseminationService(neighborDiscoveryServiceID, energyStatusProviderServiceID, updateDelta);
    	serviceUnit.addService(service);
    }
    
    /**
     * Construct a new energy status dissemination service.
     * @param neighborDiscoveryService the neighbor discovery service used to disseminate the energy value
     * @param energyStatusProviderServiceID the service used to determine the current energy
     * @param updateDelta the update delta for writing the current data to the neighbor discovery service
     */
    public EnergyDataDisseminationService(ServiceID neighborDiscoveryService, ServiceID energyStatusProviderServiceID, double updateDelta) {
    	this.neighborDiscoveryServiceID = neighborDiscoveryService;
    	this.energyStatusProviderServiceID = energyStatusProviderServiceID;
    	this.updateDelta = updateDelta;
    }

    public ServiceID getServiceID() {
    	return null;
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
    	this.operatingSystem = runtimeOperatingSystem;
    	neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(runtimeOperatingSystem, neighborDiscoveryServiceID);
    	energyStatusProviderServiceStub = new EnergyStatusProviderServiceStub(runtimeOperatingSystem, energyStatusProviderServiceID);
    	update();
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

	protected void update() {
		double remainingJoule = energyStatusProviderServiceStub.getEnergyStatus().getRemainingJoule();
		Data data = new EnergyStatusData(remainingJoule);
		neighborDiscoveryServiceStub.setOwnData(data);
		operatingSystem.setTimeout(new ServiceTimeout(updateDelta) {
			public void handle() {
				update();
			}});
	}

}
