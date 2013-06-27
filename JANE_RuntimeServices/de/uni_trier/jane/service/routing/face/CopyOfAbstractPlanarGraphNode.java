package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an abstract implementation of PlanarGraphNode with one or two hop
 * information. 
 * 
 * @author Hannes Frey, Stefan Peters
 */
public abstract class CopyOfAbstractPlanarGraphNode implements PlanarGraphNode {

	protected NetworkNode startNode;

	protected NetworkNode currentNode;

	protected NetworkNode[] allNeighbors;

	protected Planarizer planarizer;

	protected PlanarGraphNode[] adjacentNodes;

	protected NetworkNode[] allOneHopNeighbors;

	protected NeighborDiscoveryService_sync neighborDiscoveryService;

	/**
	 * Construct a new <code>SimplePlanarGraphNode</code> object.
	 * 
	 * @param startNode
	 *            The startNode is the node you have neighbor information
	 * @param currentNode
	 *            The currentNode could be the startNode, or one of its
	 *            neighbors, or one if its neighbors neighbors. In the last case
	 *            the method isStopNode() will return true
	 * @param allNeighbors
	 *            All known one and two hop neighbors
	 * @param planarizer
	 *            The used planarizer
	 */
	public CopyOfAbstractPlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode, NetworkNode[] allNeighbors,
			Planarizer planarizer,
			NeighborDiscoveryService_sync neighborDiscoveryService) {
		this.startNode = startNode;
		this.currentNode = currentNode;
		this.allNeighbors = allNeighbors;
		this.planarizer = planarizer;
		this.neighborDiscoveryService = neighborDiscoveryService;
	}

	public boolean hasAdjacentNodes() {
		return getAdjacentNodes().length > 0;
	}

	public NetworkNode[] getAllOneHopNeighbors() {
		if (allOneHopNeighbors == null) {
			Vector v = new Vector();
			if (currentNode.getAddress().equals(startNode.getAddress())) {
				for (int i = 0; i < allNeighbors.length; i++) {
					NetworkNode net = allNeighbors[i];
					if (net.isOneHopNeighbor()) {
						v.add(net);
					}
				}
			} else {
				Address[] neighbors = neighborDiscoveryService
						.getNeighborNodes(currentNode.getAddress());
				if (neighbors != null) {
					for (int i = 0; i < neighbors.length; i++) {
						NeighborDiscoveryData data = neighborDiscoveryService
								.getNeighborDiscoveryData(neighbors[i]);
						Position position = LocationData.getPosition(data);
						NetworkNode networkNode = new NetworkNodeImpl(data
								.getSender(), position,
								data.getHopDistance() == 1);
						v.add(networkNode);
					}
				}
			}
			NetworkNode[] net = new NetworkNode[v.size()];
			for (int i = 0; i < v.size(); i++) {
				net[i] = (NetworkNode) v.get(i);
			}
			allOneHopNeighbors=net;
			return net;
		} else {
			return allOneHopNeighbors;
		}
	}

	public Address getAddress() {
		return currentNode.getAddress();
	}

	public Position getPosition() {
		return currentNode.getPosition();
	}

	public boolean isOneHopNeighbor() {
		return currentNode.isOneHopNeighbor();
	}

	public String toString() {
		return currentNode.getAddress().toString();
	}

	public boolean isVirtual() {
		return false;
	}

	public NetworkNode getRelayNode() {
		return this;
	}

	/**
	 * 
	 * @return Returns an array of all neighbors
	 */
	protected NetworkNode[] getAllNeighbors() {
		Map map = new HashMap();
		for (int i = 0; i < allNeighbors.length; i++) {
			map.put(allNeighbors[i].getAddress(), allNeighbors[i]);
		}
		map.put(currentNode.getAddress(), currentNode);
		return (NetworkNode[]) map.values()
				.toArray(new NetworkNode[map.size()]);
	}
}
