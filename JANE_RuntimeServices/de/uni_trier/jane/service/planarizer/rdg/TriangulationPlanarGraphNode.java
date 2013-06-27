/**
 * 
 */
package de.uni_trier.jane.service.planarizer.rdg;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;

/**
 * 
 * 
 * @author Stefan Peters
 *
 */
public class TriangulationPlanarGraphNode implements PlanarGraphNode {
	
	private PartialTriangulation triangulation;
	private NetworkNode currentNode;
	private NetworkNode startNode;
	private boolean start;
	private Vector neighbors;
	
	public TriangulationPlanarGraphNode(PartialTriangulation triangulation,NetworkNode startNode,NetworkNode currentNode){
		this.triangulation=triangulation;
		this.currentNode=currentNode;
		this.startNode=startNode;
		neighbors=new Vector();
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.PlanarGraphNode#getAdjacentNodes()
	 */
	public PlanarGraphNode[] getAdjacentNodes() {
		computeNodes();
		return (PlanarGraphNode[])neighbors.toArray(new PlanarGraphNode[neighbors.size()]);
	}
	
	public boolean hasAdjacentNodes() {
		return getAdjacentNodes().length > 0;
	}
	

	private void computeNodes() {
		if(start)
			return;
		NetworkEdge[] edges=triangulation.getEdges();
		
		for(int i=0;i<edges.length;i++){
			if(edges[i].getNodeA().getAddress().equals(currentNode.getAddress())){
				neighbors.add(new TriangulationPlanarGraphNode(triangulation,startNode,edges[i].getNodeB()));
			}else if(edges[i].getNodeB().getAddress().equals(currentNode.getAddress())){
				neighbors.add(new TriangulationPlanarGraphNode(triangulation,startNode,edges[i].getNodeA()));
			}
		}
		start=true;
		
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.PlanarGraphNode#getAdjacentNode(de.uni_trier.jane.service.network.link_layer.LinkLayerAddress)
	 */
	public PlanarGraphNode getAdjacentNode(Address address) {
		computeNodes();
		ListIterator iterator=neighbors.listIterator();
		while(iterator.hasNext()){
			PlanarGraphNode node = (PlanarGraphNode)iterator.next();
			if(node.getAddress().equals(address)){
				return node;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.PlanarGraphNode#getAllOneHopNeighbors()
	 */
	public NetworkNode[] getAllOneHopNeighbors() {
		computeNodes();
		return getAdjacentNodes();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.PlanarGraphNode#isStopNode()
	 */
	public boolean isStopNode() {
		return isOneHopNeighbor();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.NetworkNode#getAddress()
	 */
	public Address getAddress() {
		return currentNode.getAddress();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.NetworkNode#getPosition()
	 */
	public Position getPosition() {
		return currentNode.getPosition();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.NetworkNode#isOneHopNeighbor()
	 */
	public boolean isOneHopNeighbor() {
		return !currentNode.getAddress().equals(startNode.getAddress());
	}

	public boolean isVirtual() {
		return false;
	}

	public NetworkNode getRelayNode() {
		return this;
	}

}
