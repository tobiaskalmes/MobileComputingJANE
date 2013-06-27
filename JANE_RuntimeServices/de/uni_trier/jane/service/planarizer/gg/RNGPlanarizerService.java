/*
 * @author Stefan Peters
 * Created on 17.05.2005
 */
package de.uni_trier.jane.service.planarizer.gg;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.unit.*;

/**
 * This Class is an concrete realisation of a Planarizer Service with one hop or two hop neighbor information
 * @author Stefan Peters
 */
public class RNGPlanarizerService extends AbstractPlanarizerService {

	/**
	 * This static method creates an PlanarizerService 
	 * @param serviceUnit The serviceUnit of the simulation
	 */
	public static void createInstance(ServiceUnit serviceUnit){
		ServiceID neighborDiscoveryServiceID=null;
		if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)){
			OneHopNeighborDiscoveryService.createInstance(serviceUnit,true,false);
		}
		neighborDiscoveryServiceID=serviceUnit.getService(NeighborDiscoveryService_sync.class);
		Service planarizerService=new RNGPlanarizerService(neighborDiscoveryServiceID);
		serviceUnit.addService(planarizerService);
	}

	public RNGPlanarizerService(ServiceID neighborDiscoveryServiceID) {
		super(neighborDiscoveryServiceID);
	}

	public PlanarGraphNode getPlanarGraphNode() {
		NetworkNode currentNode = getCurrentNetworkNode();
		NetworkNode[] neighbors = getNetworkNodes();
		return new OneHopPlanarGraphNode(currentNode, currentNode, new RNGPlanarizer(),neighborDiscoveryService);
	}


}
