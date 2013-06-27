package de.uni_trier.jane.service.routing.face;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an abstract implementation of PlanarGraphNode with one or two hop
 * information. 
 * 
 * @author Hannes Frey, Stefan Peters
 */
public abstract class AbstractPlanarGraphNode implements PlanarGraphNode {

	protected NetworkNode startNode;

	protected NetworkNode currentNode;

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
	public AbstractPlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode,
			Planarizer planarizer,
			NeighborDiscoveryService_sync neighborDiscoveryService) {
		this.startNode = startNode;
		this.currentNode = currentNode;
		this.planarizer = planarizer;
		this.neighborDiscoveryService = neighborDiscoveryService;
	}

	public boolean hasAdjacentNodes() {
		return getAdjacentNodes().length > 0;
	}
	
	public abstract NetworkNode[] getAllOneHopNeighbors();

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

	public PlanarGraphNode getAdjacentNode(Address address) {
		PlanarGraphNode[] nodes=getAdjacentNodes();
		if(nodes==null)
			return null;
		for(int i=0;i<nodes.length;i++) {
			if(nodes[i].getAddress().equals(address)) {
				return nodes[i];
			}
		}
		return null;
	}
	
}
