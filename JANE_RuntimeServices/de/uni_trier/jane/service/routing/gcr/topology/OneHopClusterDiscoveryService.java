/*****************************************************************************
 * 
 * OneHopClusterDiscoveryService.java
 * 
 * $Id: OneHopClusterDiscoveryService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.routing.gcr.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This service uses a neighbor discovery service in order to determine all
 * geographical clusters which are reachable from all nodes in the geographical
 * cluster the node running this service is located in.
 * 
 * @author Hannes Frey
 */
public class OneHopClusterDiscoveryService implements RuntimeService, PositioningListener,
	NeighborDiscoveryListener, ClusterDiscoveryService {


	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Service Identification
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final String SERVICE_ID_STRING = "ONE_HOP_CLUSTER_DISCOVERY";

	/**
	 * The CVS version number of this class.
	 */
	public static final String VERSION = "$Id: OneHopClusterDiscoveryService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $";

	/**
	 * The default service ID when no ID is passed by to the constructor
	 */
	public static final ServiceID DEFAULT_SERVICE_ID = new EndpointClassID(SERVICE_ID_STRING);


	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Service creation
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final ServiceIDParameter OWN_SERVICE_ID = new ServiceIDParameter("ownServiceID",
			SERVICE_ID_STRING, "The ID of this service.");

	private static final BooleanParameter SHOW_ADJACENT_CLUSTERS = new BooleanParameter(
			"showAdjacentClusters", true,
			"Show a link from the node's assigned cluster to it's reachable ones.");

	private static final BooleanParameter SHOW_NODES_OWN_CLUSTER = new BooleanParameter(
			"showNodesOwnCluster", false,
			"Show a link from the node to it's currently assigned cluster.");
	
	private static final BooleanParameter SHOW_NODES_ONE_HOP_CLUSTERS = new BooleanParameter(
			"showNodesOneHopClusters", false,
			"Show a link from the node to the clusters it is able to reach in one hop.");
	
	private static final BooleanParameter SHOW_NODES_TWO_HOP_CLUSTERS = new BooleanParameter(
			"showNodesTwoHopClusters", false,
			"Show a link from the node to the clusters it is able to reach in two hops.");

	private static final ColorParameter ADJACENT_CLUSTERS_COLOR = new ColorParameter(
			"adjacentClusterColor", Color.RED);

	private static final ColorParameter NODES_OWN_CLUSTER_COLOR = new ColorParameter(
			"nodesOwnClusterColor", Color.BLUE);

	private static final ColorParameter NODES_ONE_HOP_CLUSTERS_COLOR = new ColorParameter(
			"nodesOneHopClustersColor", Color.DARKGREY);

	private static final ColorParameter NODES_TWO_HOP_CLUSTERS_COLOR = new ColorParameter(
			"nodesTwoHopClustersColor", Color.LIGHTGREY);

	/**
	 * The service element in order to create the service form an initialization context.
	 * @see InitializationContext
	 */
	public static final ServiceElement SERVICE_ELEMENT = new ServiceElement("oneHop",
			"This service determines all geographical clusters which are reachable from all nodes " +
			"in the geographical cluster the node running this service is located in.") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			ServiceID ownServiceID = OWN_SERVICE_ID.getValue(initializationContext);
			boolean showAdjacentClusters = SHOW_ADJACENT_CLUSTERS.getValue(initializationContext);
			boolean showNodesOwnCluster = SHOW_NODES_OWN_CLUSTER.getValue(initializationContext);
			boolean showNodesOneHopClusters = SHOW_NODES_ONE_HOP_CLUSTERS.getValue(initializationContext);
			boolean showNodesTwoHopClusters = SHOW_NODES_TWO_HOP_CLUSTERS.getValue(initializationContext);
			Color adjacentClustersColor = ADJACENT_CLUSTERS_COLOR.getValue(initializationContext);
			Color nodesOwnClusterColor = NODES_OWN_CLUSTER_COLOR.getValue(initializationContext);
			Color nodesOneHopClustersColor = NODES_ONE_HOP_CLUSTERS_COLOR.getValue(initializationContext);
			Color nodesTwoHopClustersColor = NODES_TWO_HOP_CLUSTERS_COLOR.getValue(initializationContext);
			OneHopClusterDiscoveryService.createInstance(serviceUnit, ownServiceID,
					showAdjacentClusters, adjacentClustersColor, 
					showNodesOwnCluster, nodesOwnClusterColor,
					showNodesOneHopClusters, nodesOneHopClustersColor,
					showNodesTwoHopClusters, nodesTwoHopClustersColor);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { OWN_SERVICE_ID, SHOW_ADJACENT_CLUSTERS, ADJACENT_CLUSTERS_COLOR,
					SHOW_NODES_OWN_CLUSTER, NODES_OWN_CLUSTER_COLOR,
					SHOW_NODES_ONE_HOP_CLUSTERS, NODES_ONE_HOP_CLUSTERS_COLOR,
					SHOW_NODES_TWO_HOP_CLUSTERS, NODES_TWO_HOP_CLUSTERS_COLOR };
		}
	};

	/**
	 * Default static service creation method.
	 * @param serviceUnit the service unit where this service is added to
	 */
	public static final void createInstance(ServiceUnit serviceUnit) {
		createInstance(serviceUnit, null, true, Color.RED, false, Color.BLUE, false,
				Color.DARKGREY, false, Color.LIGHTGREY);
	}

	/**
	 * Static service creation.
	 * @param serviceUnit the service unit where this service and the required ones are inserted.
	 * @param ownServiceID the ID of this service. If <code>null</code> a default service ID will be used.
	 * @param showAdjacentClusters show links from the own cluster to its adjacent ones
	 * @param adjacentClustersColor the color if used
	 * @param showNodesOwnCluster show link from this node to its own cluster
	 * @param nodesOwnClusterColor the color if used
	 * @param showNodesOneHopClusters show a link from this node to the clusters it is able to reach by a one
	 * hop neighbor directly
	 * @param nodesOneHopClustersColor the color if used
	 * @param showNodesTwoHopClusters show a link from this node to the clusters it is able to reach by a one
	 * @param nodesTwoHopClustersColor the color if used
	 * hop neighbor which is connected to that cluster
	 */
	public static final void createInstance(ServiceUnit serviceUnit, ServiceID ownServiceID,
			boolean showAdjacentClusters, Color adjacentClustersColor,
			boolean showNodesOwnCluster, Color nodesOwnClusterColor,
			boolean showNodesOneHopClusters, Color nodesOneHopClustersColor,
			boolean showNodesTwoHopClusters, Color nodesTwoHopClustersColor) {
		ServiceID clusterMapID = serviceUnit.getService(ClusterMapService.class);
		ServiceID positioningID = serviceUnit.getService(PositioningService.class);
		if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
			OneHopNeighborDiscoveryService.createInstance(serviceUnit, true,false);
		}
		ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);
		serviceUnit.addService(new OneHopClusterDiscoveryService(ownServiceID, clusterMapID, positioningID,
				neighborDiscoveryID, showAdjacentClusters, adjacentClustersColor,
				showNodesOwnCluster, nodesOwnClusterColor,
				showNodesOneHopClusters, nodesOneHopClustersColor,
				showNodesTwoHopClusters, nodesTwoHopClustersColor));
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Members
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	// the services own service ID
	private ServiceID ownServiceID;

	// the services known to this one
	private ServiceID neighborDiscoveryID;
	private ServiceID positioningID;
	private ServiceID clusterMapID;

	// the utilized service stubs
	private NeighborDiscoveryService neighborDiscoveryService;
	private ClusterMapService clusterMapService;

	// the operating system of this device
	private RuntimeOperatingSystem operatingSystem;

	// used to check and to announce if own neighbor discovery data has changed
	private long clockValue;
	private Map neighborClock;

	// used for visualization
	private boolean showAdjacentClusters;
	private boolean showNodesOwnCluster;
	private boolean showNodesOneHopClusters;
	private boolean showNodesTwoHopClusters;
	private Color adjacentClustersColor;
	private Color nodesOwnClusterColor;
	private Color nodesOneHopClustersColor;
	private Color nodesTwoHopClustersColor;

	// the address of the device running this service
	private Address ownAddress;
	
	// the utilized partitioning of the plane (e.g. an infinite mesh of hexagons)
	private ClusterMap clusterMap;
	
	// the cluster this node is assigned to
	private Address ownCluster;

	// all clusters which are reachable from the cluster this node is assigned to (excluding the assigned cluster)
	private SymmetricMapping reachableFromOwnCluster;

	// the map assigning each one hop neighbor to its cluster
	private SymmetricMapping neighborsOwnCluster;
	
	// all clusters a one hop neighbor is connected to (excluding its assigned one)
	private SymmetricMapping neighborsOneHopClusters;

	// all clusters a one hop neighbor is able to reach in two hops (excluding its assigned one)
	private SymmetricMapping neighborsTwoHopClusters;

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructor
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Construct an instance of this service.
	 * @param clusterMapID the ID of the service providing the partitioning of the plane
	 * @param positioningID the ID of the service providing information about the own location
	 * @param neighborDiscoveryID the ID of the service used to disseminate information to nearby devices
	 */
	public OneHopClusterDiscoveryService(ServiceID clusterMapID,
			ServiceID positioningID, ServiceID neighborDiscoveryID) {
		this(null, clusterMapID, positioningID, neighborDiscoveryID, true, Color.RED,
				false, Color.BLUE, false, Color.GREY, false, Color.LIGHTGREY);
	}
	
	/**
	 * Construct an instance of this service.
	 * @param ownServiceID the ID of this service. If <code>null</code> a default service ID will be used
	 * @param clusterMapID the ID of the service providing the partitioning of the plane
	 * @param positioningID the ID of the service providing information about the own location
	 * @param neighborDiscoveryID the ID of the service used to disseminate information to nearby devices
	 * @param showAdjacentClusters show links from the own cluster to its adjacent ones
	 * @param adjacentClustersColor the color if used
	 * @param showNodesOwnCluster show link from this node to its own cluster
	 * @param nodesOwnClusterColor the color if used
	 * @param showNodesOneHopClusters show a link from this node to the clusters it is able to reach by a one
	 * hop neighbor directly
	 * @param nodesOneHopClustersColor the color if used
	 * @param showNodesTwoHopClusters show a link from this node to the clusters it is able to reach by a one
	 * @param nodesTwoHopClustersColor the color if used
	 * hop neighbor which is connected to that cluster
	 */
	public OneHopClusterDiscoveryService(ServiceID ownServiceID, ServiceID clusterMapID,
			ServiceID positioningID, ServiceID neighborDiscoveryID,
			boolean showAdjacentClusters, Color adjacentClustersColor,
			boolean showNodesOwnCluster, Color nodesOwnClusterColor,
			boolean showNodesOneHopClusters, Color nodesOneHopClustersColor,
			boolean showNodesTwoHopClusters, Color nodesTwoHopClustersColor) {
		this.ownServiceID = ownServiceID;
		if(this.ownServiceID == null) {
			this.ownServiceID = DEFAULT_SERVICE_ID;
		}
		this.clusterMapID = clusterMapID;
		this.positioningID = positioningID;
		this.neighborDiscoveryID = neighborDiscoveryID;
		this.showAdjacentClusters = showAdjacentClusters;
		this.showNodesOwnCluster = showNodesOwnCluster;
		this.showNodesOneHopClusters = showNodesOneHopClusters;
		this.showNodesTwoHopClusters = showNodesTwoHopClusters;
		this.adjacentClustersColor = adjacentClustersColor;
		this.nodesOwnClusterColor = nodesOwnClusterColor;
		this.nodesOneHopClustersColor = nodesOneHopClustersColor;
		this.nodesTwoHopClustersColor = nodesTwoHopClustersColor;
		reachableFromOwnCluster = new SymmetricMapping();
		neighborsOwnCluster = new SymmetricMapping();
		neighborsOneHopClusters = new SymmetricMapping();
		neighborsTwoHopClusters = new SymmetricMapping();
		neighborClock = new HashMap();
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Runtime service handler
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void start(RuntimeOperatingSystem operatingSystem) {
		
		// store link to OS
		this.operatingSystem = operatingSystem;

		// register as service listener
		operatingSystem.registerAtService(neighborDiscoveryID, NeighborDiscoveryService.class);
		operatingSystem.registerAtService(positioningID, PositioningService.class);

		// export own interfaces
		operatingSystem.registerAccessListener(ClusterDiscoveryService.class);

		// get own address
		NeighborDiscoveryService_sync tmpStub = (NeighborDiscoveryService_sync)operatingSystem.getAccessListenerStub(
				neighborDiscoveryID, NeighborDiscoveryService_sync.class);
		ownAddress = tmpStub.getOwnAddress();

		// get service stubs
		neighborDiscoveryService = (NeighborDiscoveryService)operatingSystem.getSignalListenerStub(
				neighborDiscoveryID, NeighborDiscoveryService.class);
		clusterMapService = (ClusterMapService)operatingSystem.getAccessListenerStub(
				clusterMapID, ClusterMapService.class);
		clusterMap = clusterMapService.getClusterMap();

		// determine own cluster if the positioning service is already running
		if(operatingSystem.hasService(positioningID)) {
			PositioningService positioningService = (PositioningService)operatingSystem.getAccessListenerStub(
					positioningID, PositioningService.class);
			handlePositioningData(positioningService.getPositioningData());
		}

	}

	public ServiceID getServiceID() {
		return ownServiceID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {

		// the node is currently not assigned to any cluster
		if(ownCluster == null) {
			return null;
		}

		// create all desired edges
		ShapeCollection result = new ShapeCollection();

		if(showAdjacentClusters) {
			Cluster cluster = clusterMap.getCluster(ownCluster);
			Iterator iterator = reachableFromOwnCluster.getValues().iterator();
			while (iterator.hasNext()) {
				Cluster oneHopCluster = clusterMap.getCluster((Address) iterator.next());
				result.addShape(new LineShape(cluster.getCenter(), oneHopCluster.getCenter(), adjacentClustersColor));
			}
		}
		
		if(showNodesTwoHopClusters) {
			Iterator iterator = neighborsOneHopClusters.getValues().iterator();
			while (iterator.hasNext()) {
				Cluster oneHopCluster = clusterMap.getCluster((Address) iterator.next());
				result.addShape(new LineShape(ownAddress, oneHopCluster.getCenter(), nodesTwoHopClustersColor));
			}
		}

		if(showNodesOneHopClusters) {
			Iterator iterator = neighborsOwnCluster.getValues().iterator();
			while (iterator.hasNext()) {
				Cluster oneHopCluster = clusterMap.getCluster((Address) iterator.next());
				result.addShape(new LineShape(ownAddress, oneHopCluster.getCenter(), nodesOneHopClustersColor));
			}
		}

		if(showNodesOwnCluster) {
			Cluster hostCluster = clusterMap.getCluster(ownCluster);
			result.addShape(new LineShape(ownAddress, hostCluster.getCenter(), nodesOwnClusterColor));
		}
		
		return result;
		
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("ownServiceID", ownServiceID);
		parameters.addParameter("clusterMapID", clusterMapID);
		parameters.addParameter("positioningID", positioningID);
		parameters.addParameter("neighborDiscoveryID", neighborDiscoveryID);
		parameters.addParameter("showAdjacentClusters", showAdjacentClusters);
		parameters.addParameter("adjacentClustersColor", adjacentClustersColor);
		parameters.addParameter("showNodesOwnCluster", showNodesOwnCluster);
		parameters.addParameter("nodesOwnClusterColor", nodesOwnClusterColor);
		parameters.addParameter("showNodesOneHopClusters", showNodesOneHopClusters);
		parameters.addParameter("nodesOneHopClustersColor", nodesOneHopClustersColor);
		parameters.addParameter("showNodesTwoHopClusters", showNodesTwoHopClusters);
		parameters.addParameter("nodesTwoHopClustersColor", nodesTwoHopClustersColor);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Positioning Listener Handler
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void updatePositioningData(PositioningData data) {
		handlePositioningData(data);
	}

	// Handle the case when the node entered a new cluster or is currently assigned to no cluster.
	private void handlePositioningData(PositioningData data) {

		// get position of this node
		Position position = data.getPosition();

		// a node with no position has no cluster assignment
		if(position == null) {

			// remove cluster assignment only if the node was previously assigned to one
			if(ownCluster != null) {

				// notify listeners about the removed cluster assignment
				Cluster hostCluster = clusterMap.getCluster(ownCluster);
				Cluster[] adjacentClusters = createClusterArray(reachableFromOwnCluster.getValues());
				Signal signal = new ClusterDiscoveryListener.LeaveClusterSignal(hostCluster, adjacentClusters);
				operatingSystem.sendSignal(signal);

				// remove own cluster assignment
				ownCluster = null;

				// remove all clusters reachable from the own cluster
				reachableFromOwnCluster.clear();

				// remove neighbor discovery data
				neighborDiscoveryService.removeOwnData(ReachableClusterTable.DATA_ID);

			}
			
		}
		
		// in case of existent location data check if we are in a new cluster
		else {

			// get currently assigned cluster
			Address newOwnCluster = clusterMap.getCluster(position).getAddress();
			
			// we are in a new cluster
			if(!newOwnCluster.equals(ownCluster)) {

				// we are in a cluster for the first time
				if(ownCluster == null) {

					// set new cluster assignment
					enterCluster(newOwnCluster);

					// notify listeners about the entered cluster
					Cluster hostCluster = clusterMap.getCluster(ownCluster);
					Cluster[] adjacentClusters = createClusterArray(reachableFromOwnCluster.getValues());
					final Signal signal = new ClusterDiscoveryListener.EnterClusterSignal(hostCluster, adjacentClusters);
					operatingSystem.sendSignal(signal);

				}
				
				// we changed from one cluster to another
				else {

					// remember old state
					Cluster oldHostCluster = clusterMap.getCluster(ownCluster);
					Cluster[] oldAdjacentClusters = createClusterArray(reachableFromOwnCluster.getValues());

					// remove all clusters reachable from the old cluster
					reachableFromOwnCluster.clear();

					// set new cluster assignment
					enterCluster(newOwnCluster);

					// notify listeners about the changed cluster assignment
					Cluster newHostCluster = clusterMap.getCluster(ownCluster);
					Cluster[] newAdjacentClusters = createClusterArray(reachableFromOwnCluster.getValues());
					Signal signal = new ClusterDiscoveryListener.ChangeClusterSignal(
							oldHostCluster, newHostCluster, oldAdjacentClusters, newAdjacentClusters);
					operatingSystem.sendSignal(signal);

				}

				// set new neigbor discovery data
				setNeighborDiscoveryData();

			}

		}

	}

	// Store the new cluster assignment of this node. This includes the host cluster
	// address and all adjacent clusters the host is able to reach.
	private void enterCluster(Address newOwnCluster) {
		
		// set new cluster assignment
		ownCluster = newOwnCluster;

		// add clusters reachable from all nodes located in the new cluster
		Iterator iterator = neighborsOwnCluster.getKeys(newOwnCluster).iterator();
		while (iterator.hasNext()) {
			Address neighbor = (Address) iterator.next();
			Set reachableSet = neighborsOneHopClusters.getValues(neighbor);
			reachableFromOwnCluster.putKey(neighbor, reachableSet, null, null);
		}
		
		// add all clusters reachable from this node excluding its own one
		Set reachableSet = new LinkedHashSet(neighborsOwnCluster.getValues());
		reachableSet.remove(newOwnCluster);
		reachableFromOwnCluster.putKey(ownAddress, reachableSet, null, null);
		
	}
	
	// Possibly set new neigbor discovery data
	private void setNeighborDiscoveryData() {

		// create copy of data to be sent
		Set oneHopClusterSet = createSendableSetCopy(ownCluster, neighborsOwnCluster.getValues());
		Set twoHopClusterSet = createSendableSetCopy(ownCluster, neighborsOneHopClusters.getValues());

		// set next neighbor discovery data
		clockValue++;
		ReachableClusterTable table = new ReachableClusterTable(clockValue, ownCluster, oneHopClusterSet,
				twoHopClusterSet, 2 * clusterMap.getRequiredBits());
		neighborDiscoveryService.setOwnData(table);

	}

	// Create a copy of the cluster set which conains only those clusters which can be coded
	// in a bit string consisting of the cluster maps specified required bits only.
	private Set createSendableSetCopy(Address referenceCluster, Set clusterSet) {
		Set setCopy = new LinkedHashSet();
		Iterator iterator = clusterSet.iterator();
		while (iterator.hasNext()) {
			Address cluster = (Address) iterator.next();
			if(!cluster.equals(referenceCluster) && clusterMap.isInReach(referenceCluster, cluster)) {
				setCopy.add(cluster);
			}
		}
		return setCopy;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Neighbor Discovery Listener Handler
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setNeighborData(NeighborDiscoveryData neighborData) {
		handleNeighborDiscoveryData(neighborData);
	}

	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		handleNeighborDiscoveryData(neighborData);
	}

	public void removeNeighborData(Address address) {
		handleRemoveNeighbor(address);
	}
	
	// Handle received neighbor discovery data.
	private void handleNeighborDiscoveryData(NeighborDiscoveryData data) {

		// get basic neighbor information
		Address neighbor = data.getSender();
		int hopDistance = data.getHopDistance();

		// handle only data from a one hop neighbor
		if(hopDistance == 1) {

			// get neighbor data
			ReachableClusterTable table = ReachableClusterTable.fromNeighborDiscoveryData(data);
			
			// neighbor with no vaild data will be removed
			if (table == null) {
				handleRemoveNeighbor(neighbor);
			}
			
			// update valid neighbor data
			else {

				// store the neighbors new clock value
				Long newClockValue = new Long(table.getClockValue());
				Long oldClockValue = (Long)neighborClock.put(neighbor, newClockValue);

				// update only if data has changed
				if(!newClockValue.equals(oldClockValue)) {

					// remember old state
					Set oldAdjacentClusters = reachableFromOwnCluster.getValues();

					// used to remember changes
					boolean setNewData = false;
					boolean notifyChanged = false;
					Set removedClusters = new LinkedHashSet();
					
					// get neighbors current cluster
					Address neighborsCluster = table.getHostCluster();

					// possibly update the neighbors cluster assignment
					setNewData |= neighborsOwnCluster.putKey(neighbor, neighborsCluster, null, removedClusters);
					
					// update clusters reachable from the neighbor node
					Set reachableClusters = table.getOneHopClusterSet();
					setNewData |= neighborsOneHopClusters.putKey(neighbor, reachableClusters, null, null);
					reachableClusters = table.getTwoHopClusterSet();
					neighborsTwoHopClusters.putKey(neighbor, reachableClusters, null, null);

					// handle neighbor changed its cluster
					notifyChanged |= reachableFromOwnCluster.removeFromKey(ownAddress, removedClusters, null);

					// handle neighbor inside this cluster
					if (neighborsCluster.equals(ownCluster)) {
						reachableClusters = table.getOneHopClusterSet();
						notifyChanged |= reachableFromOwnCluster.putKey(neighbor, reachableClusters, null, null);
					}
					
					// handle neighbor outside this cluster
					else {
						notifyChanged |= reachableFromOwnCluster.removeKey(neighbor, null);
						notifyChanged |= reachableFromOwnCluster.addToKey(ownAddress, neighborsCluster, null);
					}

					// possibly notify all listeners
					if(notifyChanged) {
						notifyChanged(oldAdjacentClusters);
					}
					
					// possibly set new neighbor discovery data
					if(setNewData) {
						setNeighborDiscoveryData();
					}

				}

			}

		}
		
		// remove this node if this is not the current node or a one hop neighbor
		else if(hopDistance > 1) {
			handleRemoveNeighbor(neighbor);
		}

	}
	
	// posssibly notify all listeners if the adjacent clusters have changed
	private void notifyChanged(Set oldAdjacentClusters) {
		Set newAdjacentClusters = reachableFromOwnCluster.getValues();
		Cluster hostCluster = clusterMap.getCluster(ownCluster);
		Cluster[] oldAdjacent = createClusterArray(oldAdjacentClusters);
		Cluster[] newAdjacent = createClusterArray(newAdjacentClusters);
		Signal signal = new ClusterDiscoveryListener.UpdateClusterSignal(hostCluster, oldAdjacent, newAdjacent);
		operatingSystem.sendSignal(signal);
	}

	// remove all information stored for the given neighbor
	private void handleRemoveNeighbor(Address neighbor) {
		
		// remember old state
		Set oldAdjacentClusters = reachableFromOwnCluster.getValues();

		// used to store any changes
		boolean setNewData = false;
		boolean notifyChanged = false;
		Set possiblyRemoved = new LinkedHashSet();
		
		// remove the neighbors cluster assignment
		setNewData |= neighborsOwnCluster.removeKey(neighbor, possiblyRemoved);
		
		// remove clusters reachable from the neighbor in one hop
		setNewData |= neighborsOneHopClusters.removeKey(neighbor, null);
		
		// remove clusters reachable from the neighbor in two hops
		neighborsTwoHopClusters.removeKey(neighbor, null);
		
		// possibly remove clusters reachable from this cluster
		notifyChanged |= reachableFromOwnCluster.removeKey(neighbor, null);
		notifyChanged |= reachableFromOwnCluster.removeFromKey(ownAddress, possiblyRemoved, null);

		// remove neighbors clock value
		neighborClock.remove(neighbor);

		// possibly notify all listeners
		if(notifyChanged) {
			notifyChanged(oldAdjacentClusters);
		}
		
		// possibly set new neighbor discovery data
		if(setNewData) {
			setNeighborDiscoveryData();
		}

	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Cluster discovery service handler
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Cluster getHostCluster() {
		if(ownCluster == null) {
			return null;
		}
		return clusterMap.getCluster(ownCluster);
	}

	public ClusterMap getClusterMap() {
		return clusterMap;
	}

	public Cluster[] getClustersReachableFromHost() {
		if(ownCluster == null) {
			return null;
		}
		return createClusterArray(reachableFromOwnCluster.getValues());
	}

	public Cluster[] getClusterReachableFromNode(Address node) {
		if(neighborsOneHopClusters.hasKey(node)) {
			return createClusterArray(neighborsOneHopClusters.getValues(node));
		}
		if(neighborsOwnCluster.hasKey(node)) {
			return new Cluster[0]; 
		}
		return null;
	}
	
	public Cluster[] getClustersFromClusterAddresses(Address[] addresses) {
		Cluster[] clusters = new Cluster[addresses.length];
		for(int i=0; i<addresses.length; i++) {
			clusters[i] = clusterMap.getCluster(addresses[i]);
		}
		return clusters;
	}

	public boolean hasValidHostData() {
		return ownCluster != null;
	}

	public boolean isReachableFromHost(Cluster cluster) {
		return reachableFromOwnCluster.hasValue(cluster.getAddress());
	}

	public Cluster[] getClusterReachableFromNode() {
		Set set = new LinkedHashSet(neighborsOwnCluster.getValues());
		set.remove(ownCluster);
		return createClusterArray(set);
	}

	public boolean hasData(Address address) {
		return neighborsOwnCluster.hasKey(address);
	}

	public Cluster getNodesCluster(Address w) {
		if(hasData(w)) {
			Address cluster = (Address)neighborsOwnCluster.getValues(w).iterator().next();
			return clusterMap.getCluster(cluster);
		}
		return null;
	}

	public Cluster getClusterFromAddress(Address address) {
		return clusterMap.getCluster(address);
	}

	public boolean isInHostCluster(Address node) {
		if(node.equals(ownAddress)) {
			return true;
		}
		if(ownCluster == null) {
			throw new IllegalStateException("The host cluster is not known.");
		}
		Set set = neighborsOwnCluster.getKeys(ownCluster);
		if(set != null) {
			return set.contains(node);
		}
		return false;
	}

	public Address[] getOneHopGatewayNodes(Address cluster) {
		List result = new ArrayList();
		addAll(result, neighborsOneHopClusters.getKeys(cluster));
		addAll(result, neighborsOwnCluster.getKeys(cluster));
		if(neighborsOwnCluster.hasValue(cluster)) {
			result.add(ownAddress);
		}
		return (Address[])result.toArray(new Address[result.size()]);
	}

	public Address[] getTwoHopGatewayNodes(Address cluster) {
		List result = new ArrayList();
		addAll(result, neighborsTwoHopClusters.getKeys(cluster));
		addAll(result, neighborsOneHopClusters.getKeys(cluster));
		addAll(result, neighborsOwnCluster.getKeys(cluster));
		if(neighborsOneHopClusters.hasValue(cluster)) {
			result.add(ownAddress);
		}
		return (Address[])result.toArray(new Address[result.size()]);
	}

	public boolean isReachableFromNode(Address cluster) {
		return neighborsOwnCluster.hasValue(cluster) || cluster.equals(ownCluster);
	}

	// add a possibly null initialized set to the given list
	private void addAll(List list, Set set) {
		if(set != null) {
			list.addAll(set);
		}
	}
	
	// create an array of clusters from a set of cluster addresses
	private Cluster[] createClusterArray(Set clusterAddresses) {
		Set clusters = new LinkedHashSet();
		Iterator iterator = clusterAddresses.iterator();
		while(iterator.hasNext()) {
			Address address = (Address)iterator.next();
			clusters.add(clusterMap.getCluster(address));
		}
		return (Cluster[])clusters.toArray(new Cluster[clusters.size()]);
	}

}
