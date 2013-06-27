/*****************************************************************************
 * 
 * FaceRoutingHeader.java
 * 
 * $Id: FaceRoutingHeader.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.face; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.positionbased.*;

public class FaceRoutingHeader extends GenericFaceRoutingHeader implements PositionbasedRoutingHeader {
    
    private Position receiverPosition;
    
    private StartCondition startCondition;
    private CrossingCondition crossingCondition;
    private TurnCondition turnCondition;
    private FinishCondition finishCondition;
	private BreakCondition breakCondition;
	private ResumeGreedyCondition resumeGreedyCondition;
	

	// TODO zum testen!!!
    public FaceRoutingHeader(Address sender, Position senderPosition, Address receiver, Position receiverPosition, boolean countHops, boolean traceRoute, ServiceID routingID, CrossingCondition crossingCondition, StartCondition startCondition, ResumeGreedyCondition resumeGreedyCondition) {
        super(sender, senderPosition, receiver, countHops, traceRoute, routingID);
        this.receiverPosition = receiverPosition;
        this.startCondition = startCondition;
        this.crossingCondition = crossingCondition;
        this.turnCondition = new SimpleTurnCondition();
        this.finishCondition = new SimpleFinishCondition(receiver);
        this.breakCondition = new BreakConditionImpl();
        this.resumeGreedyCondition = resumeGreedyCondition;
    }

    /**
     * Constructor for class <code>FaceRoutingHeader</code>
     * @param sender
     * @param senderPosition
     * @param receiver
     * @param countHops
     * @param traceRoute
     * @param routingID
     */
    public FaceRoutingHeader(Address sender, Position senderPosition, Address receiver, Position receiverPosition, boolean countHops, boolean traceRoute, ServiceID routingID,
    		StartCondition startCondition, CrossingCondition crossingCondition, TurnCondition turnCondition, FinishCondition finishCondition, BreakCondition breakCondition, ResumeGreedyCondition resumeGreedyCondition) {
        super(sender, senderPosition, receiver, countHops, traceRoute, routingID);
        this.receiverPosition = receiverPosition;
        this.startCondition = startCondition;
        this.crossingCondition = crossingCondition;
        this.turnCondition = turnCondition;
        this.finishCondition = finishCondition;
        this.breakCondition = breakCondition;
        this.resumeGreedyCondition = resumeGreedyCondition;
    }


    /**
     * Constructor for class <code>FaceRoutingHeader</code>
     * @param header
     */
    public FaceRoutingHeader(FaceRoutingHeader header) {
        super((GenericFaceRoutingHeader)header);
        this.receiverPosition = header.receiverPosition;
        this.startCondition = header.startCondition;
        this.crossingCondition = header.crossingCondition;
        this.turnCondition = header.turnCondition;
        this.finishCondition = header.finishCondition;
        this.breakCondition = header.breakCondition;
        this.resumeGreedyCondition = header.resumeGreedyCondition;
    }




    public FaceRoutingHeader(PositionbasedRoutingHeader positionbasedRoutingHeader, ServiceID service_id) {
        super(positionbasedRoutingHeader,service_id);
        receiverPosition=positionbasedRoutingHeader.getReceiverPosition();
        breakCondition = new BreakConditionImpl();
    }

    public boolean hasGreedyFailureNode(){
    	try{
    		getGreedyFailureNode();
    		return true;
    	}catch(IllegalAccessError e){
    		return false;
    	}
    }


    public void setReceiverPosition(Position receiverPosition) {
        this.receiverPosition = receiverPosition;
    }


    public Position getReceiverPosition() {
        if (receiverPosition==null) throw new IllegalAccessError("This routing header does not provide hop count information");
        return receiverPosition;
    }







    public boolean hasReceiverPosition() {
        return receiverPosition!=null;
    }
    public LinkLayerInfo copy() {
     
        return new FaceRoutingHeader(this);
    }



    public FinishCondition getFinishCondition() {
    	return finishCondition;
    }


	public BreakCondition getBreakCondition() {
		return breakCondition;
	}


	public void setStartCondition(StartCondition startCondition) {
		this.startCondition = startCondition;
	}


	public void setCrossingCondition(CrossingCondition crossingCondition) {
		this.crossingCondition = crossingCondition;
	}


	public void setTurnCondition(TurnCondition turnCondition) {
		this.turnCondition = turnCondition;
	}


	public void setFinishCondition(FinishCondition finishCondition) {
		this.finishCondition = finishCondition;
	}


	public void setBreakCondition(BreakCondition breakCondition) {
		this.breakCondition = breakCondition;
	}


	public void setResumeGreedyCondition(ResumeGreedyCondition resumeGreedyCondition) {
		this.resumeGreedyCondition = resumeGreedyCondition;
	}

	public CrossingCondition getCrossingCondition() {
		return crossingCondition;
	}

	public ResumeGreedyCondition getResumeGreedyCondition() {
		return resumeGreedyCondition;
	}

	public StartCondition getStartCondition() {
		return startCondition;
	}

	public TurnCondition getTurnCondition() {
		return turnCondition;
	}



















    


}
