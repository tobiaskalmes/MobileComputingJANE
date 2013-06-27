package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.util.*;

/**
 * TODO comment
 * @author Hannes Frey
 *
 */
public class GabrielGraphClusterPlanarizer extends GenericClusterPlanarizer implements NeighborDiscoveryListener {

	// initialized in constructor
	private ServiceID neighborDiscoveryID;
	
	// set to null whenever new computation is needed
	private PlanarGraphNode planarGraphNode;
	
	// initialized on startup
	private NeighborDiscoveryService neighborDiscoveryService;
	private NeighborDiscoveryService_sync neighborDiscoveryService_sync;
	private Address ownAddress;
	
	/**
	 * TODO comment
	 * @param serviceUnit
	 */
	public static void createInstance(ServiceUnit serviceUnit) {
		if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
			OneHopNeighborDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);
		if(!serviceUnit.hasService(ClusterDiscoveryService.class)) {
			OneHopClusterDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID clusterDiscoveryID = serviceUnit.getService(ClusterDiscoveryService.class);
		serviceUnit.addService(new GabrielGraphClusterPlanarizer(clusterDiscoveryID, neighborDiscoveryID));
	}

	/**
	 * TODO comment
	 * @param clusterDiscoveryID
	 * @param neighborDiscoveryID
	 */
	public GabrielGraphClusterPlanarizer(ServiceID clusterDiscoveryID, ServiceID neighborDiscoveryID) {
		super(clusterDiscoveryID, neighborDiscoveryID);
		this.neighborDiscoveryID = neighborDiscoveryID;
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		super.start(runtimeOperatingSystem);
		neighborDiscoveryService = (NeighborDiscoveryService)runtimeOperatingSystem.getSignalListenerStub(neighborDiscoveryID, NeighborDiscoveryService.class);
		runtimeOperatingSystem.registerAtService(neighborDiscoveryID, NeighborDiscoveryService.class);
		neighborDiscoveryService_sync = (NeighborDiscoveryService_sync)runtimeOperatingSystem.getAccessListenerStub(neighborDiscoveryID, NeighborDiscoveryService_sync.class);
		ownAddress = neighborDiscoveryService_sync.getOwnAddress();
		
		// TODO Cluster-Discovery-Dienst abfragen da dieser schon am laufen ist!
		
	}

	public PlanarGraphNode getPlanarGraphNode() {
		if(planarGraphNode == null && clusterDiscoveryService.hasValidHostData()) {
			
			// determine all incoming edges
			Cluster u = clusterDiscoveryService.getHostCluster();
			Set incoming = new LinkedHashSet();
			NeighborDiscoveryData[] data = neighborDiscoveryService_sync.getNeighborDiscoveryData();
			for(int i=0; i<data.length; i++) {
				IncomingEdges incomingEdges = (IncomingEdges)data[i].getDataMap().getData(IncomingEdges.DATA_ID);
				if(incomingEdges != null && incomingEdges.getHostCluster().equals(u.getAddress())) {
					incoming.addAll(incomingEdges.getOneHopClusterSet());
				}
			}

			// determine all outgoing edges
			OutgoingEdges outgoingEdges = (OutgoingEdges) neighborDiscoveryService_sync
					.getNeighborDiscoveryData(ownAddress).getDataMap().getData(OutgoingEdges.DATA_ID);
			Collection outgoing;
			if(outgoingEdges == null) {
				outgoing = new LinkedHashSet();
			}
			else {
				outgoing = outgoingEdges.getOneHopClusterSet();
			}

			// compute intersection between incoming and outgoing into an array of addresses
			incoming.retainAll(outgoing);
			Address[] addresses = (Address[])incoming.toArray(new Address[incoming.size()]);

			// construct the node data structure
			Map outgoingMap = new HashMap();
			Cluster[] neighbors = clusterDiscoveryService.getClustersFromClusterAddresses(addresses);
			for(int i=0; i<neighbors.length; i++) {
				Cluster v = neighbors[i];
				ClusterPlanarGraphNode node = new ClusterPlanarGraphNode(v);
				outgoingMap.put(node.getAddress(), node);
			}
			planarGraphNode = new ClusterPlanarGraphNode(u, outgoingMap, getAllNeighborNodes());
			
		}
		
		return planarGraphNode;
	}

	public void setNeighborData(NeighborDiscoveryData neighborData) {
		planarGraphNode = null;
		if(!neighborData.getSender().equals(ownAddress)) {
			setIncomingEdges();
		}
	}

	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		planarGraphNode = null;
		if(!neighborData.getSender().equals(ownAddress)) {
			setIncomingEdges();
		}
	}

	public void removeNeighborData(Address linkLayerAddress) {
		planarGraphNode = null;
		setIncomingEdges();
	}

	public void handleEnterCluster(Cluster hostCluster, Cluster[] adjacentClusters) {
		planarGraphNode = null;
		setOutgoingEdges();
	}

	public void handleUpdateCluster(Cluster hostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
		planarGraphNode = null;
		setOutgoingEdges();
	}

	public void handleChangeCluster(Cluster oldHostCluster, Cluster newHostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
		planarGraphNode = null;
		setOutgoingEdges();
	}

	public void handleLeaveCluster(Cluster hostCluster, Cluster[] adjacentClusters) {
		planarGraphNode = null;
		setOutgoingEdges();
	}

	// TODO: required Bits richtig bestimmen
	private void setIncomingEdges() {
		if(clusterDiscoveryService.hasValidHostData()) {
			Set incoming = new LinkedHashSet();
			Address ownCluster = clusterDiscoveryService.getHostCluster().getAddress();
			NeighborDiscoveryData[] data = neighborDiscoveryService_sync.getNeighborDiscoveryData();
			for(int i=0; i<data.length; i++) {
				OutgoingEdges outgoingEdges = (OutgoingEdges)data[i].getDataMap().getData(OutgoingEdges.DATA_ID);
				if(outgoingEdges != null) {
					if(outgoingEdges.contains(ownCluster)) {
						incoming.add(outgoingEdges.getHostCluster());
					}
				}
			}
			IncomingEdges ownIncomingEdges = new IncomingEdges(ownCluster, incoming, 18);
			neighborDiscoveryService.setOwnData(ownIncomingEdges);
		}
		else {
			neighborDiscoveryService.removeOwnData(IncomingEdges.DATA_ID);
		}
	}
	
	// TODO: required Bits richtig bestimmen
	private void setOutgoingEdges() {
		if(clusterDiscoveryService.hasValidHostData()) {
			Set outgoing = new LinkedHashSet();
			Address ownCluster = clusterDiscoveryService.getHostCluster().getAddress();
			Cluster u = clusterDiscoveryService.getHostCluster();
			Cluster[] neighbors = clusterDiscoveryService.getClustersReachableFromHost();
			for(int i=0; i<neighbors.length; i++) {
				Cluster v = neighbors[i];
				if(isGabrielGraphEdge(u, v, neighbors)) {
					outgoing.add(v.getAddress());
				}
			}
			OutgoingEdges ownOutgoingEdges = new OutgoingEdges(ownCluster, outgoing, 18);
			neighborDiscoveryService.setOwnData(ownOutgoingEdges);
		}
		else {
			neighborDiscoveryService.removeOwnData(OutgoingEdges.DATA_ID);
		}
	}
	
	private boolean isGabrielGraphEdge(Cluster u, Cluster v, Cluster[] neighbors) {
        for(int i=0; i<neighbors.length; i++){
        	Cluster w = neighbors[i];
            if(!u.equals(w) && !v.equals(w)) {
            	if(GeometryCalculations.isInCircle(u.getCenter(), v.getCenter(), w.getCenter(), true)) {
            		return false;
            	}
            }
        }
        return true;
	}
	
	private static class OutgoingEdges extends OneHopClusterTable {
		
		public static final DataID DATA_ID = new ClassDataID(OutgoingEdges.class);
		
		public OutgoingEdges(Address hostCluster, Set oneHopClusterSet, int requiredBits) {
			super(hostCluster, oneHopClusterSet, requiredBits);
		}

		public DataID getDataID() {
			return DATA_ID;
		}

	}

	private static class IncomingEdges extends OneHopClusterTable {
		
		public static final DataID DATA_ID = new ClassDataID(IncomingEdges.class);
		
		public IncomingEdges(Address hostCluster, Set oneHopClusterSet, int requiredBits) {
			super(hostCluster, oneHopClusterSet, requiredBits);
		}

		public DataID getDataID() {
			return DATA_ID;
		}

	}

}
