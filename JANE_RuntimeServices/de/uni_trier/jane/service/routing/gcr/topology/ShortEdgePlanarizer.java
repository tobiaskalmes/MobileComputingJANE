package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.unit.*;

public class ShortEdgePlanarizer extends GenericClusterPlanarizer {

	private PlanarGraphNode planarGraphNode;
	private Cluster[] adjacentClusters;
	
	public static void createInstance(ServiceUnit serviceUnit) {
//		if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
//			OneHopNeighborDiscoveryService.createInstance(serviceUnit);
//		}
//		ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);
//		if(!serviceUnit.hasService(ClusterDiscoveryService.class)) {
//			ClusterTopologyService.createInstance(serviceUnit);
//		}
//		ServiceID clusterDiscoveryID = serviceUnit.getService(ClusterDiscoveryService.class);
//		serviceUnit.addService(new ShortEdgePlanarizer(serviceUnit));
		new ShortEdgePlanarizer(serviceUnit);
	}

	public ShortEdgePlanarizer(ServiceUnit serviceUnit) {
		super(serviceUnit);
	}

	public ShortEdgePlanarizer(ServiceID clusterDiscoveryID, ServiceID neighborDiscoveryID) {
		super(clusterDiscoveryID, neighborDiscoveryID);
	}

	public PlanarGraphNode getPlanarGraphNode() {
		if(planarGraphNode == null && adjacentClusters != null) {
			Map map = new HashMap();
			Cluster hostCluster = clusterDiscoveryService.getHostCluster();
			for(int i=0; i<adjacentClusters.length; i++) {
				Cluster neighbor = adjacentClusters[i];
				if(hostCluster.getDistance(neighbor) == 1) {
					PlanarGraphNode node = new ClusterPlanarGraphNode(neighbor);
					map.put(node.getAddress(), node);
				}
			}

			// create the planar graph node
			planarGraphNode = new ClusterPlanarGraphNode(hostCluster, map, getAllNeighborNodes());
			
		}
		return planarGraphNode;
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		super.start(runtimeOperatingSystem);
		reset(clusterDiscoveryService.getClustersReachableFromHost());
	}

	public void handleEnterCluster(Cluster hostCluster, Cluster[] adjacentClusters) {
		reset(adjacentClusters);
	}

	public void handleUpdateCluster(Cluster hostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
		reset(newAdjacentClusters);
	}

	public void handleChangeCluster(Cluster oldHostCluster, Cluster newHostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
		reset(newAdjacentClusters);
	}

	public void handleLeaveCluster(Cluster hostCluster, Cluster[] adjacentClusters) {
		reset(null);
	}
	
	private void reset(Cluster[] adjacentClusters) {
		planarGraphNode = null;
		this.adjacentClusters = adjacentClusters;
	}
	
}
