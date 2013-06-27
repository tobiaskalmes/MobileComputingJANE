/*****************************************************************************
 * 
 * FaceRoutingHeader.java
 * 
 * $Id: GenericFaceRoutingHeader.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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

/*
 * Created on 29.09.2004
 *
 */
package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This is the header for facerouting packets.
 * 
 * @author Stefan Peters
 *  
 */
public abstract class GenericFaceRoutingHeader extends DefaultPositionBasedHeader {

	private transient static final Shape SHAPE = new EllipseShape(Position.NULL_POSITION,
			new Extent(10, 10), Color.PINK, true);

	private boolean clockwise;

	private Position lastIntersection;

	private ServiceID routingID;

	private NetworkNode greedyFailureNode;

    private Address lastSenderAddress;

    private Position lastSenderPosition;

	/**
     * Constructor for class <code>GenericFaceRoutingHeader</code>
     * @param sender The sender address
     * @param receiver The receiver address
     * @param countHops A flag to decide to count the hops or not
     * @param traceRoute A flag to decide to trace the route or not
     */
    public GenericFaceRoutingHeader(Address sender, Position senderPosition, Address receiver, boolean countHops, boolean traceRoute,  ServiceID routingID) {
        super(sender, receiver, countHops, traceRoute);
        this.routingID=routingID;
        lastIntersection=senderPosition;
        setSourcePosition(senderPosition);
        
    }
    /**
     * Constructor for class <code>GenericFaceRoutingHeader</code>
     * @param header
     */
    public GenericFaceRoutingHeader(GenericFaceRoutingHeader header) {
        super(header);
        clockwise=header.clockwise;
        greedyFailureNode=header.greedyFailureNode;
        lastIntersection=header.lastIntersection;
        routingID=header.routingID;
        lastSenderAddress=header.lastSenderAddress;
        lastSenderPosition=header.lastSenderPosition;
        
        //senderPosition = header.senderPosition;
        
    }

	public GenericFaceRoutingHeader(PositionbasedRoutingHeader positionbasedRoutingHeader, ServiceID service_id) {
	    super((DefaultPositionBasedHeader) positionbasedRoutingHeader);
//        if (positionbasedRoutingHeader.hasSenderPosition()){
//            setSpositionbasedRoutingHeader.getSenderPosition();
//        }
        routingID=service_id;
    }
    public GenericFaceRoutingHeader(DefaultPositionBasedHeader header, ServiceID service_id) {
        super(header);
        routingID=service_id;
    }
    
    /**
     * 
     * Constructor for class <code>GenericFaceRoutingHeader</code>
     *
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param positionbasedRoutingHeader
     * @param routingID
     */
    public GenericFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, PositionbasedRoutingHeader positionbasedRoutingHeader, ServiceID routingID) {
        super(positionbasedRoutingHeader);
        this.greedyFailureNode=new NetworkNodeImpl(greedyFailureNode,greedyFailurePosition);
        this.routingID=routingID;
    }
    /**
     * Constructor for class <code>GenericFaceRoutingHeader</code>
     *
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param routingHeader
     * @param target
     * @param routingID
     */
    public GenericFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader routingHeader, Position target, ServiceID routingID) {
        super(routingHeader,target);
        this.greedyFailureNode=new NetworkNodeImpl(greedyFailureNode,greedyFailurePosition);
        this.routingID=routingID;
    }
    /**
	 * @return Returns the lastIntersection.
	 */
	public Position getLastIntersection() {
		return lastIntersection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.ubi.appsim.service.routing.RoutingHeader#getRoutingAlgorithmClass()
	 */
	public Class getRoutingAlgorithmClass() {
		return FaceRouting.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.routing.RoutingHeader#getRoutingAlgorithmID()
	 */
	public ServiceID getRoutingAlgorithmID() {
		return routingID;
	} 

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.ubi.appsim.service.routing.RoutingHeader#getShape()
	 */
	public Shape getShape() {
		return SHAPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.ubi.appsim.service.routing.RoutingHeader#getSize()
	 */
	public int getCodingSize(){
		//TODO was kommt dahin?
		return 10;//?
	}

	/**
	 * @return Returns the clockwise.
	 */
	public boolean isClockwise() {
		return clockwise;
	}

	/**
	 * @param clockwise
	 *            The clockwise to set.
	 */
	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
	}
	
	/**
	 * 
	 * @param lastIntersection
	 *            Sets a new lastIntersection
	 */
	public void setLastIntersection(Position lastIntersection) {
		this.lastIntersection = lastIntersection;
	}
    
    /**
     * @return Returns the greedyFailureNode.
     */
    public NetworkNode getGreedyFailureNode() {
        if (greedyFailureNode==null){
            return new NetworkNodeImpl(getSender(),getSenderPosition());
        }
        return greedyFailureNode;
    }
    /**
     * @param greedyFailureNode The greedyFailureNode to set.
     */
    public void setGreedyFailureNode(NetworkNode greedyFailureNode) {
        this.greedyFailureNode = greedyFailureNode;
    }
  
    public Address getLastSenderAddress() {
        return lastSenderAddress;
    }
    
    public void setLastSenderAddress(Address lastSenderAddress) {
        this.lastSenderAddress = lastSenderAddress;
    }
    
    public Position getLastSenderPosition(){
        return lastSenderPosition;
    }
    
    public void setLastSenderPosition(Position lastSenderPosition) {
        this.lastSenderPosition = lastSenderPosition;
    }

    
}