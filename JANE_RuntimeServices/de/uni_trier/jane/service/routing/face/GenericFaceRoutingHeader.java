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
package de.uni_trier.jane.service.routing.face;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.EllipseShape;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * This is the header for facerouting packets.
 * 
 * @author Stefan Peters
 *  
 */
public abstract class GenericFaceRoutingHeader extends DefaultRoutingHeader {

	private static final Shape SHAPE = new EllipseShape(Position.NULL_POSITION,
			new Extent(4, 4), Color.PINK, true);

	private boolean clockwise;



	private boolean faceChanged;

	private boolean finished;
	private boolean startRouting=false;

	private Position lastIntersection;

	private ServiceID routingID;

	//private Position senderPosition;
	


	private boolean turned;
	private NetworkNode greedyFailureNode;

    private Address lastSenderAddress;

    private Position lastSenderPosition;


    
    
    

	/**
     * Constructor for class <code>GenericFaceRoutingHeader</code>
     * @param sender
     * @param receiver
     * @param countHops
     * @param traceRoute
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
        faceChanged=header.faceChanged;
        finished=header.finished;
        greedyFailureNode=header.greedyFailureNode;
        lastIntersection=header.lastIntersection;
        routingID=header.routingID;
        startRouting=header.startRouting;
        lastSenderAddress=header.lastSenderAddress;
        lastSenderPosition=header.lastSenderPosition;
        
        //senderPosition = header.senderPosition;
        
    }



	public GenericFaceRoutingHeader(PositionbasedRoutingHeader positionbasedRoutingHeader, ServiceID service_id) {
	    super((DefaultRoutingHeader) positionbasedRoutingHeader);
//        if (positionbasedRoutingHeader.hasSenderPosition()){
//            setSpositionbasedRoutingHeader.getSenderPosition();
//        }
        routingID=service_id;
    }
    public GenericFaceRoutingHeader(DefaultRoutingHeader header, ServiceID service_id) {
        super(header);
        routingID=service_id;
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
		return FaceRouting_old.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.routing.RoutingHeader#getRoutingAlgorithmID()
	 */
	public ServiceID getRoutingAlgorithmID() {
		return routingID;
	}

	/**
	 * 
	 * @return Returns the sender position
	 */
//	public Position getSenderPosition() {
//        if (senderPosition==null)throw new IllegalAccessError("This routing header does not provide sender position");
//		return senderPosition;
//	}
//    
//    public boolean hasSenderPosition() {
//        
//        return senderPosition!=null;
//    }
    
    

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
	 * @return Returns the faceChanged.
	 */
	public boolean isFaceChanged() {
		return faceChanged;
	}

	/**
	 * @return Returns the finished.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @return Returns the turned.
	 */
	public boolean isTurned() {
		return turned;
	}

	/**
	 * @param clockwise
	 *            The clockwise to set.
	 */
	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
	}

	/**
	 * @param faceChanged
	 *            The faceChanged to set.
	 */
	public void setFaceChanged(boolean faceChanged) {
		this.faceChanged = faceChanged;
	}

	/**
	 * @param finished
	 *            The finished to set.
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/**
	 * 
	 * @param lastIntersection
	 *            Sets a new lastIntersection
	 */
	public void setLastIntersection(Position lastIntersection) {
		this.lastIntersection = lastIntersection;
	}

//	/**
//	 * 
//	 * @param senderPosition
//	 *            Set the a sender position
//	 */
//	public void setSenderPosition(Position senderPosition) {
//		this.senderPosition = senderPosition;
//	}

	/**
	 * @param turned
	 *            The turned to set.
	 */
	public void setTurned(boolean turned) {
		this.turned = turned;
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


	public String toString() {
		String str = "FaceRoutingHeader\n";
		str += "SourceAddress : " + getSender()+ "\n";
//		str += "SenderPosition: " + senderPosition + "\n";
		str += "LastInstersection: " + lastIntersection + "\n";
		str += "Clockwise: " + clockwise + "\n";
		str += "FaceChanged: " + faceChanged + "\n";
		str += "Turned: " + turned + "\n";
		str += "Finished: " + finished + "\n";
		return str;
	}

	
	public boolean isStartRouting() {
		return startRouting;
	}
	public void setStartRouting(boolean startRouting) {
		this.startRouting = startRouting;
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