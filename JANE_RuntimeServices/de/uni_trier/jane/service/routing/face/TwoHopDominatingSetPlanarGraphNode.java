package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an concrete implementation of AbstractPlanarGraphNode. It uses
 * dominating set with two hop neighbor information.
 * @author Stefan Peters
 */
public class TwoHopDominatingSetPlanarGraphNode extends TwoHopPlanarGraphNode {

	/**
	 * The constructor
	 * 
	 * @param startNode
	 *            The center node of neigborinformation
	 * @param currentNode
	 *            The current node is the startNode or one of its one or two hop
	 *            neighbors, if two hop neighborinformation is available
	 * @param allNeighbors
	 *            All available neighbors of the startNode
	 * @param allDominatingSetNeighbors
	 *            All dominating set neighbors
	 * @param planarizer
	 *            The used planarizer
	 */
	public TwoHopDominatingSetPlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode, Planarizer planarizer,
			NeighborDiscoveryService_sync neighborDiscoveryService) {
		super(startNode, currentNode, planarizer,
				neighborDiscoveryService);
	}


	public PlanarGraphNode[] getAdjacentNodes() {
		if (adjacentNodes == null) {
			NetworkNode[] neighs = getAllOneHopDominatingSetNeighbors();
			neighs = planarizer.stdPlanarizer(currentNode, neighs);
			PlanarGraphNode[] pNeighs = new PlanarGraphNode[neighs.length];
			for (int i = 0; i < neighs.length; i++) {
				pNeighs[i] = new TwoHopDominatingSetPlanarGraphNode(startNode,
						neighs[i],
						planarizer, neighborDiscoveryService);
			}
			adjacentNodes = pNeighs;
			return pNeighs;
		} else {
			return adjacentNodes;
		}
	}

	// Returns all one hop neighbors in the dominating set
	//TODO falls das Performance Problem an der PlanarGraphNode Implementierung liegt,
	// kann der Fehler nur in dieser Methode sein, da der Rest so ziemlich identisch ist.
	private NetworkNode[] getAllOneHopDominatingSetNeighbors() {
		NetworkNode[] nodes=getAllOneHopNeighbors();
		Vector vector=new Vector();
		for(int i=0;i<nodes.length;i++) {
			if(DominatingSetData.fromNeighborDiscoveryData(neighborDiscoveryService.getNeighborDiscoveryData(nodes[i].getAddress())).isMember()) {
				vector.add(nodes[i]);
			}
		}
		return (NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
	}


}
