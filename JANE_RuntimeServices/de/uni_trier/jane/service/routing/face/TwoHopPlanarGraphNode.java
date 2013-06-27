package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an concrete implementation of AbstractPlanarGraphNode. It uses
 * two hop neighbor information.
 * 
 * @author Stefan Peters
 * 
 */
public  class TwoHopPlanarGraphNode extends AbstractPlanarGraphNode {
	
	private static final long serialVersionUID = 1117808204447521535L;


	/**
	 * The constructor
	 * 
	 * @param startNode
	 *            The center node of neighborinformation
	 * @param currentNode
	 *            The current node
	 * @param allNeighbors
	 *            All known neighbors of startNode
	 * @param planarizer
	 *            The used planarizer
	 * @param neighborDiscoveryService
	 *            The NeighborDiscoveryService to gain needed information
	 */
	public TwoHopPlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode, Planarizer planarizer,
			NeighborDiscoveryService_sync neighborDiscoveryService) {
		super(startNode, currentNode, planarizer, neighborDiscoveryService);
	}

	public boolean isStopNode() {
		return neighborDiscoveryService.getNeighborDiscoveryData(
				currentNode.getAddress()).getHopDistance() == 2;
	}

	public PlanarGraphNode[] getAdjacentNodes() {
		if (neighborDiscoveryService.getNeighborDiscoveryData(
				currentNode.getAddress()).getHopDistance() == 2)
			return null; // no data available
		if (adjacentNodes == null) {
			NetworkNode[] neighs = getAllOneHopNeighbors();
			neighs = planarizer.stdPlanarizer(currentNode, neighs);
			PlanarGraphNode[] pNeighs = new PlanarGraphNode[neighs.length];
			for (int i = 0; i < neighs.length; i++) {
				pNeighs[i] = new TwoHopPlanarGraphNode(startNode, neighs[i],
						planarizer, neighborDiscoveryService);
			}
			adjacentNodes = pNeighs; 
			return pNeighs;
		} else {
			return adjacentNodes;
		}
	}

	
	public NetworkNode[] getAllOneHopNeighbors() {
		
		
		Address[] addresses=neighborDiscoveryService.getNeighborNodes(currentNode.getAddress());
		Set set = new HashSet();
		
		// Adrian, 20.10.2006, java 1.3
		//for( Address address : addresses )
		for( int i=0; i<addresses.length; i++ )
		{
			set.add( addresses[i] );
		}
		NeighborDiscoveryData[] datas=neighborDiscoveryService.getNeighborDiscoveryData();
		Vector vector = new Vector();
		
		// Adrian, 20.10.2006, java 1.3
		//for( NeighborDiscoveryData data : datas )
		for( int i=0; i<datas.length; i++ )
		{
			if(set.contains( datas[i].getSender() )){
				vector.add(new NetworkNodeImpl( datas[i].getSender(),LocationData.getPosition(datas[i]),true) );
			}
		}
		// Adrian, 20.10.2006, java 1.3: added cast (NetworkNode[])
		return (NetworkNode[]) vector.toArray( new NetworkNode[vector.size()] );
		
		/*
		Address currentAddress = currentNode.getAddress();
		// das wird kompliziert
		if (currentAddress.equals(startNode.getAddress())) {
			Vector vector = new Vector();
			NeighborDiscoveryData[] datas = null;
			datas = neighborDiscoveryService
					.getNeighborDiscoveryData(NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
			for (int i = 0; i < datas.length; i++) {
				vector.add(new NetworkNodeImpl(datas[i].getSender(),LocationData.getPosition(datas[i]), true));
			}
			allOneHopNeighbors = (NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
		} else {
			// get all one hop neighbors of this one hop neighbor
			Vector vector = new Vector();
			NeighborDiscoveryData[] datas = neighborDiscoveryService
					.getNeighborDiscoveryData();
			Address[] addresses=neighborDiscoveryService.getNeighborNodes(currentAddress);
			//TODO herumhacken an der falschen Stelle, meines erachtens liegt das Problem beim NeighbordiscoveryService
			// oder ich habe die Methode getNeighborNodes falsch verstanden
			if(addresses==null) {
				//Debug code
				//NeighborDiscoveryData[] datas = null;
				datas = neighborDiscoveryService
						.getNeighborDiscoveryData(NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
				for(int i=0;i<datas.length;i++) {
					if(datas[i].getSender().equals(currentAddress)) {
						System.err.println(currentAddress +  " : Is one hop neighbor");
					}
				}
				return new NetworkNode[0];
			}else {
				System.err.println("Es gibt doch noch infos");
			}
				
			Set set=new HashSet();
			for(int i=0;i<addresses.length;i++) {
				set.add(addresses[i]);
			}
			for(int i=0;i<datas.length;i++) {
				if(set.contains(datas[i].getSender())) {
					Position pos=LocationData.getPosition(datas[i]);
					NetworkNode node=new NetworkNodeImpl(datas[i].getSender(),pos,datas[i].getHopDistance()==1);
					vector.add(node);
				}
			}
			allOneHopNeighbors=(NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
		}
		return allOneHopNeighbors;*/
	}
}
