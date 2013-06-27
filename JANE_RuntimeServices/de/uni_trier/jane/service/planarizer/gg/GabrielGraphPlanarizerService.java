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
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

/**
 * This Class is an concrete realisation of a Planarizer Service with one hop or two hop neighbor information
 * using gabriel graph as planar graph.
 * @author Stefan Peters
 */
public class GabrielGraphPlanarizerService extends AbstractPlanarizerService {

	private static final BooleanParameter USE_TWO_HOP = new BooleanParameter("useTwoHop", false);
	public static final ServiceElement SERVICE_ELEMENT = new ServiceElement("gabrielGraph") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			boolean useTwoHop = USE_TWO_HOP.getValue(initializationContext);
			GabrielGraphPlanarizerService.createInstance(serviceUnit, useTwoHop);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { USE_TWO_HOP };
		}
	};

	/**
	 * This static method creates an PlanarizerService 
	 * @param serviceUnit The serviceUnit of the simulation
	 * @param twoHop 
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,boolean twoHop){
		ServiceID neighborDiscoveryServiceID=null;
		if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)){
			OneHopNeighborDiscoveryService.createInstance(serviceUnit,true,false);
		}
		neighborDiscoveryServiceID=serviceUnit.getService(NeighborDiscoveryService_sync.class);
		Service planarizerService=new GabrielGraphPlanarizerService(neighborDiscoveryServiceID,twoHop);
		serviceUnit.addService(planarizerService);
		return SERVICE_ID;
	}

	private boolean twoHop;

	/**
	 * 
	 * @param neighborDiscoveryServiceID The id of a neighbor discovery service
	 * @param twoHop A flag, to decide whether to use one hop or two hop planar graph nodes
	 */
	public GabrielGraphPlanarizerService(ServiceID neighborDiscoveryServiceID,boolean twoHop) {
		super(neighborDiscoveryServiceID);
		this.twoHop=twoHop;
	}

	public PlanarGraphNode getPlanarGraphNode() {
		NetworkNode currentNode = getCurrentNetworkNode();
		
		NetworkNode[] neighbors = getNetworkNodes();
		//System.err.println();
		if(twoHop) {
			return new TwoHopPlanarGraphNode(currentNode, currentNode, new GGPlanarizer(),neighborDiscoveryService);
		}
		return new OneHopPlanarGraphNode(currentNode, currentNode, new GGPlanarizer(),neighborDiscoveryService);
	}

}
