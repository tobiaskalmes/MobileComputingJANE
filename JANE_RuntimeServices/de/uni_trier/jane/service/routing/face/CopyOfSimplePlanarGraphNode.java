/*
 * funktioniert
 */
package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;

/**
 * This class is an implementation of PlanarGraphNode with two hop information
 * @author Hannes Frey
 */
public class CopyOfSimplePlanarGraphNode implements PlanarGraphNode {

    private NetworkNode startNode;
    private NetworkNode currentNode;
    private NetworkNode[] allNeighbors;
    //private double udgRadius;
    private Planarizer planarizer;
    
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
	 * @param udgRadius
	 *            The sending radius
	 */
	public CopyOfSimplePlanarGraphNode(NetworkNode startNode,
			NetworkNode currentNode, NetworkNode[] allNeighbors,
			 Planarizer planarizer) {
		this.startNode = startNode;
		this.currentNode = currentNode;
		this.allNeighbors = allNeighbors;
		//this.udgRadius = udgRadius;
		this.planarizer = planarizer;
	}
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.PlanarGraphNode#getAdjacentNodes()
	 */
    public PlanarGraphNode[] getAdjacentNodes() {
        NetworkNode[] neighs= getAllOneHopNeighbors();
        neighs=planarizer.stdPlanarizer(currentNode,neighs);
        /*if(currentNode.getAddress().toString().equalsIgnoreCase("13")){
        	System.err.println("da");
        }*/
        PlanarGraphNode[] pNeighs= new PlanarGraphNode[neighs.length];
        /*
         * Creating a new List og neighbors for the requested nodes including the current Node;
         */
        for(int i=0;i<neighs.length;i++){
        	pNeighs[i]= new CopyOfSimplePlanarGraphNode(startNode,neighs[i],getAllNeighbors(), planarizer);
        }
        return pNeighs;
    }

	public boolean hasAdjacentNodes() {
		return getAdjacentNodes().length > 0;
	}


    /**
	 * @return
	 */
	private NetworkNode[] getAllNeighbors() { // TODO: wozu benötigt man die HashMap?
		Map map= new HashMap();
		for(int i=0;i<allNeighbors.length;i++){
			map.put(allNeighbors[i].getAddress(),allNeighbors[i]);
		}
		map.put(currentNode.getAddress(),currentNode);
		Collection c=map.values();
		Iterator iterator=c.iterator();
		NetworkNode[] nodes= new NetworkNode[c.size()];
		int i=0;
		while(iterator.hasNext()){
			NetworkNode node=(NetworkNode) iterator.next();
			nodes[i]=node;
			i++;
		}
		return nodes;
	}

	/* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.PlanarGraphNode#getAdjacentNode(de.uni_trier.ubi.appsim.kernel.basetype.Address)
     */
    public PlanarGraphNode getAdjacentNode(Address address) {
        NetworkNode[] neighs= getAllOneHopNeighbors();
        /*
         * Creating a new List og neighbors for the requested nodes including the current Node;
         */
        NetworkNode[] allNeighs=new NetworkNode[allNeighbors.length+1];
        for(int j=0;j<allNeighbors.length;j++){
        	allNeighs[j]=allNeighbors[j];
        }
        allNeighs[allNeighbors.length]=currentNode;
        for(int i=0;i<neighs.length;i++){
        	if(neighs[i].getAddress().equals(address)){
        		
        		return new CopyOfSimplePlanarGraphNode(startNode,neighs[i],allNeighs, planarizer);
        	}
        }
        return null;
    }

    public NetworkNode[] getAllOneHopNeighbors() {
        // für alle Knoten aus allOneAndTwoHopNeighbors die Knoten bestimmen,
        // welche einen Abstand zu currentNode kleinergleich udgRadius haben.
    	Vector v= new Vector();
    	for(int i=0;i<allNeighbors.length;i++){
    		NetworkNode net=allNeighbors[i];
    		if(net.isOneHopNeighbor()){
    			v.add(net);
    		}
    	}
    	NetworkNode[] net= new NetworkNode[v.size()];
    	for(int i=0;i<v.size();i++){
    		net[i]=(NetworkNode) v.get(i);
    	}
        return net;
    }

    public boolean isStopNode() {
    	
    	// TODO test
    	//if(true)
    		return !startNode.getAddress().equals(currentNode.getAddress());
    	
    	// Fehler gefunden
//    	if(startNode.getPosition().distance(currentNode.getPosition()) > udgRadius)
//    		return true;
//    	for(int i=0;i<allNeighbors.length;i++){
//    		if(!isOneHopNeighbor(allNeighbors[i])){
//    			//System.err.println("Kein StopNode");
//    			return false;
//    		}
//    	}
//    	return true;
    	//return startNode.getPosition().distance(currentNode.getPosition()) > udgRadius;
        
    }

//    /**
//	 * @param node
//	 * @return
//	 */
//	private boolean isOneHopNeighbor(NetworkNode node) {
//		NetworkNode[] nodes=getAllOneHopNeighbors();
//		for(int i =0;i<nodes.length;i++) {
//			if(nodes[i].getAddress().equals(node.getAddress()))
//				return true;
//		}
//		return false;
//	}

	public Address getAddress() {
        return currentNode.getAddress();
    }

    public Position getPosition() {
        return currentNode.getPosition();
    }

    public boolean isOneHopNeighbor() {
    	return currentNode.isOneHopNeighbor();
        //return !isStopNode();
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
}
