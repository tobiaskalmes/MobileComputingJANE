package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.util.*;

public class VirtualEdgeClusterPlanarizer extends GenericClusterPlanarizer
		implements NeighborDiscoveryListener {

	// initialized in constructor
	private ServiceID neighborDiscoveryID;
	private ServiceID planarizerServiceID;

	// set to null if new computation required
	private PlanarGraphNode planarGraphNode;

	// initialized on startup
	private Address ownAddress;
	private PlanarizerService networkPlanarizerService;
	private NeighborDiscoveryService neighborDiscoveryService;
	private NeighborDiscoveryService_sync neighborDiscoveryService_sync;
	
	public static void createInstance(ServiceUnit serviceUnit) {
		if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
			OneHopNeighborDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);
		if(!serviceUnit.hasService(PlanarizerService.class)) {
			GabrielGraphPlanarizerService.createInstance(serviceUnit,false);
		}
		ServiceID planarizerServiceID = serviceUnit.getService(PlanarizerService.class);
		if(!serviceUnit.hasService(ClusterDiscoveryService.class)) {
			OneHopClusterDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID clusterDiscoveryID = serviceUnit.getService(ClusterDiscoveryService.class);
		serviceUnit.addService(new VirtualEdgeClusterPlanarizer(clusterDiscoveryID, neighborDiscoveryID, planarizerServiceID));
	}

	public VirtualEdgeClusterPlanarizer(ServiceID clusterDiscoveryID, ServiceID neighborDiscoveryID, ServiceID planarizerServiceID) {
		super(clusterDiscoveryID, neighborDiscoveryID);
		this.neighborDiscoveryID = neighborDiscoveryID;
		this.planarizerServiceID = planarizerServiceID;
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		super.start(runtimeOperatingSystem);
		neighborDiscoveryService = (NeighborDiscoveryService) runtimeOperatingSystem
				.getSignalListenerStub(neighborDiscoveryID,
						NeighborDiscoveryService.class);
		runtimeOperatingSystem.registerAtService(neighborDiscoveryID,
				NeighborDiscoveryService.class);
		neighborDiscoveryService_sync = (NeighborDiscoveryService_sync) runtimeOperatingSystem
				.getAccessListenerStub(neighborDiscoveryID,
						NeighborDiscoveryService_sync.class);
		networkPlanarizerService = (PlanarizerService)runtimeOperatingSystem.getAccessListenerStub(planarizerServiceID, PlanarizerService.class);
		ownAddress = neighborDiscoveryService_sync.getOwnAddress();
		
		// TODO Cluster-Discovery-Dienst abfragen da dieser schon am laufen ist!

	}

	public PlanarGraphNode getPlanarGraphNode() {

// TODO TEST
//planarGraphNode = null;
		
		if(planarGraphNode == null) {
			
			if(clusterDiscoveryService.hasValidHostData()) {
				
				Cluster host = clusterDiscoveryService.getHostCluster();
				
				NeighborDiscoveryData[] data = neighborDiscoveryService_sync.getNeighborDiscoveryData();
				Set explicit = new LinkedHashSet();
				Set implicitSame = new LinkedHashSet();
				Set implicitReverse = new LinkedHashSet();
				for(int i=0; i<data.length; i++) {
					if(clusterDiscoveryService.isInHostCluster(data[i].getSender())) {
						ExplicitEdges explicitEdges = (ExplicitEdges)data[i].getDataMap().getData(ExplicitEdges.DATA_ID);
						ImplicitSameEdges implicitSameEdges = (ImplicitSameEdges)data[i].getDataMap().getData(ImplicitSameEdges.DATA_ID);
						ImplicitReversEdges implicitReversEdges = (ImplicitReversEdges)data[i].getDataMap().getData(ImplicitReversEdges.DATA_ID);
						addTableData(explicitEdges, explicit);
						addTableData(implicitSameEdges, implicitSame);
						addTableData(implicitReversEdges, implicitReverse);
					}
				}
				
				Map outgoingMap = new HashMap();

				insertIntoMap(explicit, outgoingMap, false, false);
				insertIntoMap(implicitSame, outgoingMap, true, false);
				insertIntoMap(implicitReverse, outgoingMap, true, true);
				
				planarGraphNode = new ClusterPlanarGraphNode(host, outgoingMap, getAllNeighborNodes());

			}
			
		}
		return planarGraphNode;
	}

	private void insertIntoMap(Set clusterAddresses, Map map, boolean implicit, boolean reverse) {
		Address[] addresses = (Address[])clusterAddresses.toArray(new Address[clusterAddresses.size()]);
		Cluster[] neighbors = clusterDiscoveryService.getClustersFromClusterAddresses(addresses);
		for(int i=0; i<neighbors.length; i++) {
			Cluster v = neighbors[i];
			ClusterPlanarGraphNode node;
			Cluster relayCluster = getRelayCluster(v, reverse);
			ClusterPlanarGraphNode testDummy = new ClusterPlanarGraphNode(relayCluster);
			if(implicit) {
				node = new ClusterPlanarGraphNode(v, testDummy);
			}
			else {
				node = new ClusterPlanarGraphNode(v);
			}
			map.put(node.getAddress(), node);
		}
	}
	
	private Cluster getRelayCluster(Cluster d, boolean reverse) {
		Cluster c = clusterDiscoveryService.getHostCluster();
		Position center;
		Position cPos = c.getCenter();
		Position dPos = d.getCenter();
		if(reverse) {
			center = cPos.add(dPos.sub(cPos).scale(-1.0));
		}
		else {
			center = cPos.add(dPos.sub(cPos).scale(2.0));
		}
		return clusterDiscoveryService.getClusterMap().getCluster(center);
	}
	
	private void addTableData(OneHopClusterTable table, Set set) {
		if(table != null) {
			set.addAll(table.getOneHopClusterSet());
		}
	}
	
	
	public void handleEnterCluster(Cluster hostCluster, Cluster[] adjacentClusters) {
		setAllEdges();
	}

	public void handleUpdateCluster(Cluster hostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
		setAllEdges();
	}

	public void handleChangeCluster(Cluster oldHostCluster, Cluster newHostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
		setAllEdges();
	}

	public void handleLeaveCluster(Cluster hostCluster, Cluster[] adjacentClusters) {
		setAllEdges();
	}

	public void setNeighborData(NeighborDiscoveryData neighborData) {
		if(!neighborData.getSender().equals(ownAddress)) {
			setAllEdges();
		}
	}

	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		if(!neighborData.getSender().equals(ownAddress)) {
			setAllEdges();
		}
	}

	public void removeNeighborData(Address linkLayerAddress) {
		setAllEdges();
	}

	// v = current node
	// c = current cluster
	private void setAllEdges() {
		
		planarGraphNode = null;

		PlanarGraphNode planarGraphNode = networkPlanarizerService.getPlanarGraphNode();
		if(planarGraphNode != null && clusterDiscoveryService.hasValidHostData()) {
			Cluster c = clusterDiscoveryService.getHostCluster();
			
			Set ev = new LinkedHashSet();
			Set isv = new LinkedHashSet();
			Set irv = new LinkedHashSet();
			Set rv = new LinkedHashSet();

			PlanarGraphNode[] nodes = planarGraphNode.getAdjacentNodes();
			for(int i=0; i<nodes.length; i++) {
				Address w = nodes[i].getAddress();
				if(clusterDiscoveryService.hasData(w)) {
					Cluster e = clusterDiscoveryService.getNodesCluster(w);
					if(!c.getAddress().equals(e.getAddress())) {
						Cluster d = findMidCluster(w);
						if(d != null) {
							rv.add(e.getAddress());
							if(!clusterDiscoveryService.isReachableFromHost(d)) {
								isv.add(d.getAddress());
							}
						}
						else {
							ev.add(e.getAddress());
						}
					}
				}
			}

			Address[] allNeighbors = neighborDiscoveryService_sync.getNeighbors();
			for(int i=0; i<allNeighbors.length; i++) {
				Address w = allNeighbors[i];
				if(clusterDiscoveryService.hasData(w)) {
					Cluster e = clusterDiscoveryService.getNodesCluster(w);
					RemovedEdges rw = (RemovedEdges )neighborDiscoveryService_sync.getNeighborDiscoveryData(w).getDataMap().getData(RemovedEdges.DATA_ID);
					if(rw != null) {
						Address[] removed = rw.getOneHopClusters();
						for(int j=0; j<removed.length; j++) {
							Cluster d = clusterDiscoveryService.getClusterFromAddress(removed[j]);
							if(isMidOf(c, d, e)) {
								if(!clusterDiscoveryService.isReachableFromHost(d)) {
									irv.add(d.getAddress());
								}
							}
						}
					}
				}
			}

			neighborDiscoveryService.setOwnData(new ExplicitEdges(c.getAddress(), ev, 18));
			neighborDiscoveryService.setOwnData(new ImplicitSameEdges(c.getAddress(), isv, 6));
			neighborDiscoveryService.setOwnData(new ImplicitReversEdges(c.getAddress(), irv, 6));
			neighborDiscoveryService.setOwnData(new RemovedEdges(c.getAddress(), rv, 6));
			
		}
		else {
			neighborDiscoveryService.removeOwnData(ExplicitEdges.DATA_ID);
			neighborDiscoveryService.removeOwnData(ImplicitSameEdges.DATA_ID);
			neighborDiscoveryService.removeOwnData(ImplicitReversEdges.DATA_ID);
			neighborDiscoveryService.removeOwnData(RemovedEdges.DATA_ID);
		}
		
	}

	private Cluster findMidCluster(Address w) {
		Cluster c = clusterDiscoveryService.getHostCluster();
		Cluster[] hv = clusterDiscoveryService.getClusterReachableFromNode();
		Cluster e = clusterDiscoveryService.getNodesCluster(w);
		Cluster[] hw = clusterDiscoveryService.getClusterReachableFromNode(w);
		Set hvhw = new LinkedHashSet();
		addToSet(hvhw, hv);
		addToSet(hvhw, hw);
		Iterator iterator = hvhw.iterator();
		while(iterator.hasNext()) {
			Cluster d = (Cluster)iterator.next();
			if(isMidOf(d, c, e)) {
				return d;
			}
		}
		return null;
	}
	
	private void addToSet(Set set, Object[] elements) {
		for(int i=0; i<elements.length; i++) {
			set.add(elements[i]);
		}
	}
	
	private boolean isMidOf(Cluster d, Cluster c, Cluster e) {
		if(c.getAddress().equals(d.getAddress()) || e.getAddress().equals(c.getAddress()) || e.getAddress().equals(d.getAddress()) ) {
			return false;
		}
		return GeometryCalculations.checkIntersect(c.getCenter(), e.getCenter(), d.getCenter(), d.getCenter());
//		c.getCenter()
//		return c.getDistance(e) == 2 && d.getDistance(c) == 1 && d.getDistance(e) == 1;
	}

	private static class ExplicitEdges extends OneHopClusterTable {

		public static final DataID DATA_ID = new ClassDataID(
				ExplicitEdges.class);

		public ExplicitEdges(Address hostCluster, Set oneHopClusterSet,
				int requiredBits) {
			super(hostCluster, oneHopClusterSet, requiredBits);
		}

		public DataID getDataID() {
			return DATA_ID;
		}

	}

	private static class ImplicitSameEdges extends OneHopClusterTable {

		public static final DataID DATA_ID = new ClassDataID(
				ImplicitSameEdges.class);

		public ImplicitSameEdges(Address hostCluster, Set oneHopClusterSet,
				int requiredBits) {
			super(hostCluster, oneHopClusterSet, requiredBits);
		}

		public DataID getDataID() {
			return DATA_ID;
		}

	}

	private static class ImplicitReversEdges extends OneHopClusterTable {

		public static final DataID DATA_ID = new ClassDataID(
				ImplicitReversEdges.class);

		public ImplicitReversEdges(Address hostCluster, Set oneHopClusterSet,
				int requiredBits) {
			super(hostCluster, oneHopClusterSet, requiredBits);
		}

		public DataID getDataID() {
			return DATA_ID;
		}

	}

	private static class RemovedEdges extends OneHopClusterTable {

		public static final DataID DATA_ID = new ClassDataID(RemovedEdges.class);

		public RemovedEdges(Address hostCluster, Set oneHopClusterSet,
				int requiredBits) {
			super(hostCluster, oneHopClusterSet, requiredBits);
		}

		public DataID getDataID() {
			return DATA_ID;
		}

	}

}
