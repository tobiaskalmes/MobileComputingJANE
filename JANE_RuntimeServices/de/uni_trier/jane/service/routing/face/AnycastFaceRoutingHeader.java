/*****************************************************************************
 * 
 * AnycastFaceRoutingHeader.java
 * 
 * $Id: AnycastFaceRoutingHeader.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

public class AnycastFaceRoutingHeader extends GenericFaceRoutingHeader implements LocationBasedRoutingHeader {

    private GeographicLocation geographicTarget;

    private StartCondition startCondition;
    private CrossingCondition crossingCondition;
    private TurnCondition turnCondition;
    private FinishCondition finishCondition;
	private BreakCondition breakCondition;
	private ResumeGreedyCondition resumeGreedyCondition;

    /**
     * Constructor for class <code>AnycastFaceRoutingHeader</code>
     * @param sender
     * @param senderPosition
     * @param receiver
     * @param countHops
     * @param traceRoute
     * @param routingID
     */
    public AnycastFaceRoutingHeader(Address sender, Position senderPosition, GeographicLocation geographicTarget, boolean countHops, boolean traceRoute, ServiceID routingID) {
        super(sender, senderPosition, null, countHops, traceRoute, routingID);
        this.geographicTarget=geographicTarget;
        this.startCondition = new AngleStartCondition();
        this.crossingCondition = new AngleCrossingCondition();
        this.turnCondition = new SimpleTurnCondition();
        this.finishCondition = new LocationFinishCondition(geographicTarget);
        this.breakCondition = new BreakConditionImpl();
        this.resumeGreedyCondition = new SimpleResumeGreedyCondition();
    }

    public AnycastFaceRoutingHeader(Address sender, Position senderPosition, GeographicLocation geographicTarget, boolean countHops, boolean traceRoute, ServiceID routingID, CrossingCondition crossingCondition, StartCondition startCondition) {
        super(sender, senderPosition, null, countHops, traceRoute, routingID);
        this.geographicTarget = geographicTarget;
        this.startCondition = startCondition;
        this.crossingCondition = crossingCondition;
        this.turnCondition = new SimpleTurnCondition();
        this.finishCondition = new LocationFinishCondition(geographicTarget);
        this.breakCondition = new BreakConditionImpl();
        this.resumeGreedyCondition = new SimpleResumeGreedyCondition();
    }
    
    public AnycastFaceRoutingHeader(GeographicLocation geographicTarget,  boolean countHops, boolean traceRoute,ServiceID routingID) {
        this(null, null, geographicTarget, countHops, traceRoute, routingID);
        
    }
    
    /**
     * 
     * Constructor for class <code>AnycastFaceRoutingHeader</code>
     * @param header
     * @param routingID
     * @param delegationService
     */
    public AnycastFaceRoutingHeader(LocationBasedRoutingHeader header, ServiceID routingID) {
        this((DefaultRoutingHeader)header,(GeographicLocation) header.getTargetLocation(),routingID);

    }
    
    /**
     * Constructor for class <code>AnycastFaceRoutingHeader</code>
     */
     public AnycastFaceRoutingHeader(DefaultRoutingHeader defaultRoutingHeader,GeographicLocation geographicTarget, ServiceID faceRoutingID) {
        super(defaultRoutingHeader,faceRoutingID);
        this.geographicTarget=geographicTarget;  
        
        this.startCondition = new AngleStartCondition();
        this.crossingCondition = new AngleCrossingCondition();
        this.turnCondition = new SimpleTurnCondition();
        this.finishCondition = new LocationFinishCondition(geographicTarget);
        this.breakCondition = new BreakConditionImpl();
        this.resumeGreedyCondition = new SimpleResumeGreedyCondition();
    }

//    public AnycastFaceRoutingHeader(GeographicTarget geographicTarget, ServiceID  routingID){
//        this(geographicTarget,null,routingID);
//    }



    private AnycastFaceRoutingHeader(AnycastFaceRoutingHeader header) {
        super(header);
        this.geographicTarget=header.geographicTarget;
        this.startCondition = header.startCondition;
        this.crossingCondition = header.crossingCondition;
        this.turnCondition = header.turnCondition;
        this.finishCondition = header.finishCondition;
        this.breakCondition = header.breakCondition;
        this.resumeGreedyCondition = header.resumeGreedyCondition;
    }
    
    //
    public Position getReceiverPosition() {
     
        return geographicTarget.getCenterPosition();
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




    
    













    public int getCodingSize() {
        int size=super.getCodingSize();
        
        if (geographicTarget!=null){
            size+=geographicTarget.getCodingSize();
        }
        return size;
    }



    public LinkLayerInfo copy() {
     
        return new AnycastFaceRoutingHeader(this);
    }


    
    /**
     * @return Returns the destinationPosition.
     */
    public Position getDestinationPosition() {
        return geographicTarget.getCenterPosition();
    }
    
    /**
     * @return Returns the geographic target.
     */
    public Location getTargetLocation() {
        return geographicTarget;
    }

}
