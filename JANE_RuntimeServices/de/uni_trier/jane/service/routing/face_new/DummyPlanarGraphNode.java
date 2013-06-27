package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;

/**
 * A dummy PlanarGraphNode, it only contains information of the current node.
 * @author Stefan Peters
 *
 */
public class DummyPlanarGraphNode implements PlanarGraphNode {

	NetworkNode node;
	
	/**
	 * The constructor
	 * @param node The current node
	 */
	public DummyPlanarGraphNode(NetworkNode node) {
		this.node=node;
	}
	
	
	public PlanarGraphNode[] getAdjacentNodes() {
		return null;
	}

	public PlanarGraphNode getAdjacentNode(Address address) {
		return null;
	}

	public NetworkNode[] getAllOneHopNeighbors() {
		return null;
	}

	public boolean isStopNode() {
		return false;
	}

	public Address getAddress() {
		return node.getAddress();
	}

	public Position getPosition() {
		return node.getPosition();
	}

	public boolean isOneHopNeighbor() {
		return true;
	}

	public boolean hasAdjacentNodes() {
		return false;
	}

	public boolean isVirtual() {
		return false;
	}

	public NetworkNode getRelayNode() {
		return null;
	}

}
