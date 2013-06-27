/*****************************************************************************
 * 
 * FaceRoutingHeader.java
 * 
 * $Id: AbstractFaceRoutingHeader.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.service.routing.face_new; 

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.positionbased.*;

/**
 * This is an abstract implementation of a face routing header, its used by normal facerouting
 * and face routing with dominating set. All needed conditions used by face routing are stored 
 * in this class.
 * @author Stefan Peters
 *
 */
public abstract class AbstractFaceRoutingHeader extends GenericFaceRoutingHeader implements PositionbasedRoutingHeader {
    
	private static final long serialVersionUID = 1L;

	//protected Position receiverPosition;
	protected StartCondition startCondition;
	protected FinishCondition finishCondition;
	protected BreakCondition breakCondition;
	protected TurnCondition turnCondition;
	protected ResumeGreedyCondition resumeGreedyCondition;
	protected CrossingCondition crossingCondition;
    
	/**
	 * Constructor	
	 * @param sender The address of the sending node
	 * @param senderPosition The position of the sending node
	 * @param receiver The address of the receiver, if available
	 * @param receiverPosition The position of receiver
	 * @param countHops A flag to decide whether to count hops or not
	 * @param traceRoute A flog to decide whether to trace route or not
	 * @param routingID The ServiceID of the routing algorithm
	 */
	public AbstractFaceRoutingHeader(Address sender, Position senderPosition, Address receiver, Position receiverPosition, boolean countHops, boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition, receiver, countHops, traceRoute, routingID);
		setReceiverPosition(receiverPosition);
	}

	/**
	 * Constructor to copy a header
	 * TODO : Conditions need a copy method
	 * @param header The header to copy
	 */
	public AbstractFaceRoutingHeader(AbstractFaceRoutingHeader header) {
		super(header);
		receiverPosition=header.getReceiverPosition();
		startCondition=header.startCondition;
		finishCondition=header.finishCondition;
		breakCondition=header.breakCondition;
		turnCondition=header.turnCondition;
		resumeGreedyCondition=header.resumeGreedyCondition;
		crossingCondition=header.crossingCondition;
	}


    /**
     * Constructor for class <code>AbstractFaceRoutingHeader</code>
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param positionbasedRoutingHeader
     * @param routingID
     */
    public AbstractFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, PositionbasedRoutingHeader positionbasedRoutingHeader, ServiceID routingID) {
        super (greedyFailureNode,greedyFailurePosition,positionbasedRoutingHeader,routingID);
    }

    /**
     * Constructor for class <code>AbstractFaceRoutingHeader</code>
     *
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param routingHeader
     * @param target
     * @param routingID
     */
    public AbstractFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader routingHeader, Position target, ServiceID routingID) {
        super(greedyFailureNode,greedyFailurePosition,routingHeader,target,routingID);
    }

    /**
	 * 
	 * @return Returns true, if a greedy failure node is available, false if not.
	 */
    public boolean hasGreedyFailureNode(){
    	try{
    		getGreedyFailureNode();
    		return true;
    	}catch(IllegalAccessError e){
    		return false;
    	}
    }


//    public void setReceiverPosition(Position receiverPosition) {
//        this.receiverPosition = receiverPosition;
//    }
//
//
//    public Position getReceiverPosition() {
//        if (receiverPosition==null) throw new IllegalAccessError("This routing header does not provide receiver position information");
//        return receiverPosition;
//    }
//
//    public boolean hasReceiverPosition() {
//        return receiverPosition!=null;
//    }
    
    public abstract LinkLayerInfo copy();
    
    protected int getStepIndex(RoutingStep[] steps, NetworkNode destination, NeighborDiscoveryData[] neighborData, Address bestNeighbor) {

    	//Ueberarbeiten
    	if(steps.length==2) {
    		return 1;
    	}
    	int step=0;
    	Map indexMap = new HashMap();
    	for(int i=1; i<steps.length; i++) {// ignore first step, because it is the current node
			indexMap.put(steps[i].getNode().getAddress(), new Integer(i));
		}
    	if(indexMap.containsKey(bestNeighbor))//bestNeighbor is one of step nodes
    		return ((Integer)indexMap.get(bestNeighbor)).intValue();
    	//chosing a RoutingStep nearest to bestNeighbor
    	Position pos=null;
    	for(int i=0;i<neighborData.length;i++) {
    		if(neighborData[i].getSender().equals(bestNeighbor)) {
    			pos=LocationData.getPosition(neighborData[i]);
    			break;
    		}
    	}
    	if(pos!=null) {
    		Address last=null;
    		double distance=Double.MAX_VALUE;
    		for(int i=1;i<steps.length;i++) {
    			NetworkNode node=steps[i].getNode();
    			double d=node.getPosition().distance(pos);
    			if(distance>d) {
    				distance=d;
    				last=node.getAddress();
    			}
    		}
    		if(last!=null) {
    			return ((Integer)indexMap.get(last)).intValue();
    		}
    		//return 
    	}else {
    		Set set=new HashSet();
    		for(int i=0;i<neighborData.length;i++) {
    			if(neighborData[i].getHopDistance()==1)
    				set.add(neighborData[i].getSender());
    		}
    		for(int i=steps.length-1;i>=0;i--) {
    			if(set.contains(steps[i].getNode().getAddress()))
    				return i;
    		}
    	}
    	return step;
    	/*
		int maxStep = 0;
		
		// TODO Was machen wenn bestNeighbor kein Node aus den steps ist?
		Map indexMap = new HashMap();
		List neighborList = new LinkedList();
		NeighborDiscoveryData currentNeighbor = null;
		
		for(int i=0; i<steps.length; i++) {
			indexMap.put(steps[i].getNode().getAddress(), new Integer(i));
		}
		
		for(int i=0; i<neighborData.length; i++) {
			if(neighborData[i].getHopDistance() == 0) {
				currentNeighbor = neighborData[i];
			}
			else if(neighborData[i].getHopDistance() == 1 && indexMap.containsKey(neighborData[i].getSender())) {
				Integer index = (Integer)indexMap.get(neighborData[i].getSender());
				maxStep = Math.max(maxStep, index.intValue());
				neighborList.add(neighborData[i]);
			}
		}


		NeighborDiscoveryData[] neighbors = (NeighborDiscoveryData[])neighborList.toArray(new NeighborDiscoveryData[neighborList.size()]);
		
		Address address = bestNeighbor;
		//TODO was hier machen?
		// bestNeighbor muss nicht unbedingt in steps liegen
		if(address != null) {
			Integer index = (Integer)indexMap.get(address);
//			System.out.println(steps.length + " " + index);
			if(index!=null)
					return index.intValue();
			else {
				//TODO Zwischenloesung, am besten knoten suchen der am naechsten zu best neighbor liegt
				for(int i=0;i<steps.length;i++) {
					if(steps[i].getNode().isStopNode()) {
						return i;
					}
				}
				return steps.length;
			}
		}

		return maxStep;
		*/

	}
}
