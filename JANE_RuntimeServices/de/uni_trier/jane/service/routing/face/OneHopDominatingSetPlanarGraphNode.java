package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an concrete implementation of AbstractPlanarGraphNode. It uses
 * dominating set with one hop neighbor information.
 * 
 * @author Stefan Peters
 * 
 */
public class OneHopDominatingSetPlanarGraphNode extends OneHopPlanarGraphNode {

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
	public OneHopDominatingSetPlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode, Planarizer planarizer,
			NeighborDiscoveryService_sync neighborDiscoveryService) {
		super(startNode, currentNode, planarizer,
				neighborDiscoveryService);
	}

	public PlanarGraphNode[] getAdjacentNodes() {
		if (adjacentNodes == null) {
			NetworkNode[] neighs = getAllOneHopDominatingSetNeighbors();
			neighs = planarizer.stdPlanarizer(currentNode, neighs);
			/*
			 * if(currentNode.getAddress().toString().equalsIgnoreCase("13")){
			 * System.err.println("da"); }
			 */
			PlanarGraphNode[] pNeighs = new PlanarGraphNode[neighs.length];
			/*
			 * Creating a new List of neighbors for the requested nodes
			 * including the current Node;
			 */
			for (int i = 0; i < neighs.length; i++) {
				pNeighs[i] = new OneHopDominatingSetPlanarGraphNode(startNode,
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
	private NetworkNode[] getAllOneHopDominatingSetNeighbors() {
		NeighborDiscoveryData[] datas=neighborDiscoveryService.getNeighborDiscoveryData(NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
		Vector vector =new Vector();
		for(int i=0;i<datas.length;i++) {
			if(DominatingSetData.fromNeighborDiscoveryData(datas[i]).isMember()) {
				Position pos=LocationData.getPosition(datas[i]);
				vector.add(new NetworkNodeImpl(datas[i].getSender(),pos,datas[i].getHopDistance()==1));
			}
		}
		return (NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
	}

}
