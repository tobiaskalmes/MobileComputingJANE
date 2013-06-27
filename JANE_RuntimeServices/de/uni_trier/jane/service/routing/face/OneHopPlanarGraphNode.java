package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an concrete implementation of AbstractPlanarGraphNode. It uses
 * one hop neighbor information to construct a PlanarGraphNode.
 * @author Stefan Peters
 * 
 */
public class OneHopPlanarGraphNode extends AbstractPlanarGraphNode {

	/**
	 * The constructor
	 * @param startNode A NetworkNode representing the center of neighbor information
	 * @param currentNode The current node
	 * @param allNeighbors All neighbors of startNode
	 * @param planarizer The used planarizer
	 * @param neighborDiscoveryService The NeighborDiscoveryService to gain acces to needed informations
	 */
	public OneHopPlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode,
			Planarizer planarizer,NeighborDiscoveryService_sync neighborDiscoveryService) {
		super(startNode, currentNode, planarizer,neighborDiscoveryService);
	}

	public PlanarGraphNode[] getAdjacentNodes() {
		if(neighborDiscoveryService.getNeighborDiscoveryData(currentNode.getAddress()).getHopDistance()==1)
			return null; // No data available
		if(adjacentNodes==null) {
			NetworkNode[] neighs= getAllOneHopNeighbors();
			neighs=planarizer.stdPlanarizer(currentNode,neighs);
			PlanarGraphNode[] pNeighs= new PlanarGraphNode[neighs.length];
			for(int i=0;i<neighs.length;i++){
				pNeighs[i]= new OneHopPlanarGraphNode(startNode,neighs[i], planarizer,neighborDiscoveryService);
			}
			adjacentNodes=pNeighs;
			return pNeighs;
		}else {
			return adjacentNodes;
		}
	}

	public boolean isStopNode() {
		return neighborDiscoveryService.getNeighborDiscoveryData(currentNode.getAddress()).getHopDistance()==1;
	}

	
	public NetworkNode[] getAllOneHopNeighbors() {
		if(currentNode.isOneHopNeighbor())
			return null;
		if(allOneHopNeighbors==null) {
			Vector vector= new Vector();
			NeighborDiscoveryData[] datas=null;
			datas=neighborDiscoveryService.getNeighborDiscoveryData(NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
			for(int i=0;i<datas.length;i++) {
				vector.add(new NetworkNodeImpl(datas[i].getSender(),LocationData.getPosition(datas[i]),true));
			}
			allOneHopNeighbors=(NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
		}
		return allOneHopNeighbors;
	}

}
