/*
 * @author Stefan Peters
 * Created on 17.05.2005
 */
package de.uni_trier.jane.service.planarizer.gg;

import java.util.Vector;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.dominating_set.UDGDominatingSetService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.DominatingSetData;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.DominatingSetDataDisseminationService;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationData;
import de.uni_trier.jane.service.planarizer.AbstractPlanarizerService;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.face.NetworkNodeImpl;
import de.uni_trier.jane.service.routing.face.OneHopDominatingSetPlanarGraphNode;
import de.uni_trier.jane.service.routing.face.TwoHopDominatingSetPlanarGraphNode;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.parametrized.parameters.InitializationContext;
import de.uni_trier.jane.simulation.parametrized.parameters.Parameter;
import de.uni_trier.jane.simulation.parametrized.parameters.base.BooleanParameter;
import de.uni_trier.jane.simulation.parametrized.parameters.service.ServiceElement;

/**
 * This Class is an concrete realisation of a Planarizer Service with domianting set neighbor information 
 * @author Stefan Peters
 */
public class DominatingSetPlanarizerService extends AbstractPlanarizerService {

	
	private static final BooleanParameter USE_TWO_HOP = new BooleanParameter("useTwoHop", false);
	public static final ServiceElement SERVICE_ELEMENT = new ServiceElement("dominatingSetGabrielGraph") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			boolean useTwoHop = USE_TWO_HOP.getValue(initializationContext);
			DominatingSetPlanarizerService.createInstance(serviceUnit, useTwoHop);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { USE_TWO_HOP };
		}
	};


	
	boolean twoHop;
	
	// Creates an planarizer service with dominating set information
	private DominatingSetPlanarizerService(ServiceID neighborDiscoveryServiceID,boolean twoHop) {
		super(neighborDiscoveryServiceID);
		this.twoHop=twoHop;
	}
	/**
	 * This static method creates an PlanarizerService with Dominating Set neighborinformation
	 * @param serviceUnit The serviceUnit of the simulation
	 * @param twoHop A flag to decide whether to use a PlanarGraphNode implementation with one or two Hop
	 * 					neighbor information
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,boolean twoHop){
		ServiceID neighborDiscoveryServiceID=null;
		if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)){
			OneHopNeighborDiscoveryService.createInstance(serviceUnit,true,false);
		}
		if(!serviceUnit.hasService(UDGDominatingSetService.class)){
			UDGDominatingSetService.createInstance(serviceUnit);
		}
		if(!serviceUnit.hasService(DominatingSetDataDisseminationService.class)){
			DominatingSetDataDisseminationService.createInstance(serviceUnit);
		}
		neighborDiscoveryServiceID=serviceUnit.getService(NeighborDiscoveryService_sync.class);
		Service planarizerService=new DominatingSetPlanarizerService(neighborDiscoveryServiceID,twoHop);
		serviceUnit.addService(planarizerService);
		return planarizerService.getServiceID();
	}

	private boolean isInDominatingSet() {
		NeighborDiscoveryData data = neighborDiscoveryService.getNeighborDiscoveryData(getCurrentNetworkNode().getAddress());
		DominatingSetData dominatingSetData = DominatingSetData.fromNeighborDiscoveryData(data);
		if(dominatingSetData != null) {
			return dominatingSetData.isMember();
		}
		return false;
	}

	public PlanarGraphNode getPlanarGraphNode() {

		if(!isInDominatingSet()) {
			return null;
		}
		NetworkNode currentNode = getCurrentNetworkNode();
		//NetworkNode[] neighbors = getNetworkNodes();
		//NetworkNode[] dominatingSetNeighbors=getDominatingSetNeighbors(neighborDiscoveryService.getNeighborDiscoveryData());
		if(twoHop) {
			return new TwoHopDominatingSetPlanarGraphNode(currentNode,currentNode, new GGPlanarizer(),neighborDiscoveryService); // TODO: new GGPlanarizer(); das ist kaputt!!!
		}
		return new OneHopDominatingSetPlanarGraphNode(currentNode,currentNode, new GGPlanarizer(),neighborDiscoveryService); // TODO: new GGPlanarizer(); das ist kaputt!!!
	}
	
	// extracts all neighbors, which are in a Domianting Set, from the neighborlist.
	private NetworkNode[] getDominatingSetNeighbors(
			NeighborDiscoveryData[] neighborDiscoveryData) {
		NetworkNode[] neighbors = new NetworkNode[0];
		Vector v = new Vector();
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			//if (!neighborDiscoveryData[i].getSender().equals(address)) {
			if (neighborDiscoveryData[i].getDataMap().hasData(
					DominatingSetData.DATA_ID)) {
				if (((DominatingSetData) neighborDiscoveryData[i].getDataMap()
						.getData(DominatingSetData.DATA_ID)).isMember()) {
					NetworkNode node = new NetworkNodeImpl(
							 neighborDiscoveryData[i]
									.getSender(),
							((LocationData) neighborDiscoveryData[i]
									.getDataMap().getData(LocationData.DATA_ID))
									.getPosition(), neighborDiscoveryData[i]
									.getHopDistance() == 1);
					v.add(node);
				}
				//}

			}
		}
		if (v.size() != 0) {
			neighbors = new NetworkNode[v.size()];
			for (int i = 0; i < v.size(); i++) {
				neighbors[i] = (NetworkNode) v.get(i);
			}
		}
		return neighbors;
	}

}
