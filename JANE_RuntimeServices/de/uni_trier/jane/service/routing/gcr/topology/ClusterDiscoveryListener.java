package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.signaling.*;

public interface ClusterDiscoveryListener {

//	public void handleNewHostCluster(Address host, Address[] oldNeigborClusters, Address[] newNeighborClusters);

//	public void handleOwnClusterTableChanged(ReachableClusterTable reachableClusterTable);
//
//	public void handleNeighborClusterTableChanged(Address neighborAddress, ReachableClusterTable reachableClusterTable);

	public void handleEnterCluster(Cluster hostCluster, Cluster[] adjacentClusters);
	public void handleUpdateCluster(Cluster hostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters);
	public void handleChangeCluster(Cluster oldHostCluster, Cluster newHostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters);
	public void handleLeaveCluster(Cluster hostCluster, Cluster[] adjacentClusters);
	
//	public void handleOneHopClustersChanged(Address[] newClusters, Address[] addedClusters, Address[] removedClusters);
//	
//	public void handleOneHopClusterRemoved(Address[] clusterAddresses);

	public class EnterClusterSignal implements Signal {

		private Cluster cluster;
		private Cluster[] adjacentClusters;

		public EnterClusterSignal(Cluster cluster, Cluster[] adjacentClusters) {
			this.cluster = cluster;
			this.adjacentClusters = adjacentClusters;
		}

		public void handle(SignalListener listener) {
			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
			clusterDiscoveryListener.handleEnterCluster(cluster, adjacentClusters);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return ClusterDiscoveryListener.class;
		}
		
	}

	public class UpdateClusterSignal implements Signal {

		private Cluster hostCluster;
		private Cluster[] oldAdjacentClusters;
		private Cluster[] newAdjacentClusters;

		public UpdateClusterSignal(Cluster hostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
			this.hostCluster = hostCluster;
			this.oldAdjacentClusters = oldAdjacentClusters;
			this.newAdjacentClusters = newAdjacentClusters;
		}

		public void handle(SignalListener listener) {
			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
			clusterDiscoveryListener.handleUpdateCluster(hostCluster, oldAdjacentClusters, newAdjacentClusters);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return ClusterDiscoveryListener.class;
		}
		
	}

	public class ChangeClusterSignal implements Signal {

		private Cluster oldHostCluster;
		private Cluster newHostCluster;
		private Cluster[] oldAdjacentClusters;
		private Cluster[] newAdjacentClusters;

		public ChangeClusterSignal(Cluster oldHostCluster, Cluster newHostCluster, Cluster[] oldAdjacentClusters, Cluster[] newAdjacentClusters) {
			this.oldHostCluster = oldHostCluster;
			this.newHostCluster = newHostCluster;
			this.oldAdjacentClusters = oldAdjacentClusters;
			this.newAdjacentClusters = newAdjacentClusters;
		}

		public void handle(SignalListener listener) {
			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
			clusterDiscoveryListener.handleChangeCluster(oldHostCluster, newHostCluster, oldAdjacentClusters, newAdjacentClusters);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return ClusterDiscoveryListener.class;
		}
		
	}

	public class LeaveClusterSignal implements Signal {

		private Cluster hostCluster;
		private Cluster[] adjacentClusters;
		
		public LeaveClusterSignal(Cluster hostCluster, Cluster[] adjacentClusters) {
			this.hostCluster = hostCluster;
			this.adjacentClusters = adjacentClusters;
		}
		
		public void handle(SignalListener listener) {
			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
			clusterDiscoveryListener.handleLeaveCluster(hostCluster, adjacentClusters);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return ClusterDiscoveryListener.class;
		}
		
	}

//	public class OneHopClusterRemovedSignal implements Signal {
//
//		private Address[] clusterAddresses;
//
//		public OneHopClusterRemovedSignal(Address[] clusterAddresses) {
//			this.clusterAddresses = clusterAddresses;
//		}
//
//		public void handle(SignalListener listener) {
//			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
//			clusterDiscoveryListener.handleOneHopClusterRemoved(clusterAddresses);
//		}
//
//		public Dispatchable copy() {
//			return this;
//		}
//
//		public Class getReceiverServiceClass() {
//			return ClusterDiscoveryListener.class;
//		}
//		
//	}

	
//	public class OwnClusterTableChangedSignal implements Signal {
//
//		private static final long serialVersionUID = -2576078343454639150L;
//
//		private ReachableClusterTable table;
//
//		public OwnClusterTableChangedSignal(ReachableClusterTable table) {
//			this.table = table;
//		}
//
//		public void handle(SignalListener listener) {
//			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
//			clusterDiscoveryListener.handleOwnClusterTableChanged(table);
//		}
//
//		public Dispatchable copy() {
//			return this;
//		}
//
//		public Class getReceiverServiceClass() {
//			return ClusterDiscoveryListener.class;
//		}
//		
//	}
//
//	public class NeighborClusterTableChangedSignal implements Signal {
//
//		private static final long serialVersionUID = 2770268123304612550L;
//
//		private Address address;
//		private ReachableClusterTable table;
//
//		public NeighborClusterTableChangedSignal(Address address, ReachableClusterTable table) {
//			this.address = address;
//			this.table = table;
//		}
//
//		public void handle(SignalListener listener) {
//			ClusterDiscoveryListener clusterDiscoveryListener = (ClusterDiscoveryListener)listener;
//			clusterDiscoveryListener.handleNeighborClusterTableChanged(address, table);
//		}
//
//		public Dispatchable copy() {
//			return this;
//		}
//
//		public Class getReceiverServiceClass() {
//			return ClusterDiscoveryListener.class;
//		}
//		
//	}

}
