package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

public abstract class GenericClusterPlanarizer implements RuntimeService, ClusterPlanarizerService, ClusterDiscoveryListener {

	public static final ServiceID SERVICE_ID = new EndpointClassID(GenericClusterPlanarizer.class.getName());

	private static final boolean DEBUG_MODE = true;
	
	private ServiceID clusterDiscoveryID;
	private ServiceID neighborDiscoveryID;
	
	protected ClusterDiscoveryService clusterDiscoveryService;
	private NeighborDiscoveryService_sync neighborDiscoveryService;

	public GenericClusterPlanarizer(ServiceUnit serviceUnit) {
		if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
			OneHopNeighborDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);
		if(!serviceUnit.hasService(ClusterDiscoveryService.class)) {
			OneHopClusterDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID clusterDiscoveryID = serviceUnit.getService(ClusterDiscoveryService.class);
		this.clusterDiscoveryID = clusterDiscoveryID;
		this.neighborDiscoveryID = neighborDiscoveryID;
		serviceUnit.addService(this);
	}

	public GenericClusterPlanarizer(ServiceID clusterDiscoveryID, ServiceID neighborDiscoveryID) {
		this.clusterDiscoveryID = clusterDiscoveryID;
		this.neighborDiscoveryID = neighborDiscoveryID;
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		clusterDiscoveryService = (ClusterDiscoveryService)runtimeOperatingSystem
			.getAccessListenerStub(clusterDiscoveryID, ClusterDiscoveryService.class);
		neighborDiscoveryService = (NeighborDiscoveryService_sync)runtimeOperatingSystem
			.getAccessListenerStub(neighborDiscoveryID, NeighborDiscoveryService_sync.class);
		runtimeOperatingSystem.registerAtService(clusterDiscoveryID, ClusterDiscoveryService.class);
		
		runtimeOperatingSystem.registerAccessListener(ClusterPlanarizerService.class);
		
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {
		PlanarGraphNode hostCluster = getPlanarGraphNode();
		if(hostCluster == null) {
			return null;
		}
		ShapeCollection result = new ShapeCollection();
		Position from = hostCluster.getPosition();
		PlanarGraphNode[] planarGraphNodes = hostCluster.getAdjacentNodes();
		int len = planarGraphNodes.length;
		for(int i = 0; i < len; i++) {
			PlanarGraphNode planarGraphNode = planarGraphNodes[i];
			Position to = planarGraphNode.getPosition();
			Shape shape;
			if(DEBUG_MODE) {
				if(planarGraphNode.isVirtual()) {
					shape = new HalfLineShape(from, to, Color.GREEN);
				}
				else {
					shape = new HalfLineShape(from, to, Color.BLUE);
				}
			}
			else {
				shape = new LineShape(from, to, Color.RED);
			}
			result.addShape(shape);
		}
		return result;
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("clusterDiscoveryID", clusterDiscoveryID);
		parameters.addParameter("neighborDiscoveryID", neighborDiscoveryID);
	}

	// determine all known neighbor nodes
	protected NetworkNode[] getAllNeighborNodes() {
		List neighbors = new ArrayList();
		NeighborDiscoveryData[] data = neighborDiscoveryService.getNeighborDiscoveryData();
		for(int i=0; i<data.length; i++) {
			LocationData locationData = (LocationData)data[i].getDataMap().getData(LocationData.DATA_ID);
			if(locationData != null) {
				Address address = data[i].getSender();
				Position position = locationData.getPosition();
				boolean isOneHopNeighbor = data[i].getHopDistance() <= 1;
				neighbors.add(new NetworkNodeImpl(address, position, isOneHopNeighbor));
			}
		}
		return (NetworkNode[])neighbors.toArray(new NetworkNode[neighbors.size()]);
	}
	
}
