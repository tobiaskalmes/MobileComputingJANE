package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public class NeighborDiscoveryBase {

	private static final ServiceIDParameter NEIGHBOR_DISCOVERY_ID = new ServiceIDParameter("neighborDiscoveryID");

	public static final ServiceID getInstance(ServiceUnit serviceUnit, InitializationContext initializationContext) {
		ServiceID neighborDiscoveryID = NEIGHBOR_DISCOVERY_ID.getValue(initializationContext);
    	if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	return neighborDiscoveryID;
	}
	
}
