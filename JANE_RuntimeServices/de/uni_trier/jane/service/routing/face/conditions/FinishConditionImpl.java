/* 
 * Funktioniert
 * Created on 23.09.2004
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * An implementation of the FinishCondition. If the destination is in sending radius, it will return true,
 * else it will return false.
 * @author Stefan Peters
 *
 */
public class FinishConditionImpl implements FinishCondition {

	private boolean satisfied;

	private Address destination;
	
	/**
	 * The Constructur
	 * @param destination The node the packet shall be sent
	 */
	public FinishConditionImpl(Address destination){
		this.destination=destination;
		satisfied = false;
	}
	
	
	private FinishConditionImpl(Address destination, boolean satisfied) {
		this.destination = destination;
		this.satisfied = satisfied;
	}


	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.FinishCondition#checkCondition(de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.PlanarGraphNode, de.uni_trier.ubi.appsim.kernel.basetype.NetworkNode[])
	 */
	public boolean checkCondition(PlanarGraphNode currentNode,
			NetworkNode[] neighbors) {
		if(neighbors==null)
			return false;
		for(int i=0;i<neighbors.length;i++){
		    if(neighbors[i].getAddress().equals(destination)){
		        return true;
		    }
		}
		return false;
	}
	
	public FinishCondition nextNode(PlanarGraphNode currentNode, NetworkNode[] neighbors) {
		return new FinishConditionImpl(destination, checkCondition(currentNode, neighbors));
	}
	
	public boolean isSatisfied() {
		return satisfied;
	}

}
