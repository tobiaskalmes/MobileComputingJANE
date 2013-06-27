package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * A factory to generate the needed header when routing starts
 * @author Stefan Peters
 *
 */
public class DominatingSetFaceRoutingHeaderFactory implements
		FaceRoutingHeaderFactory {
	private boolean traceRoute;
	private boolean countHops;
	
	/**
	 * The constructor
	 * @param countHops A flag to decide whether to count the hops or not
	 * @param traceRoute A flag to decide whether to trace route or not
	 */
	public DominatingSetFaceRoutingHeaderFactory(boolean countHops,
			boolean traceRoute) {
		this.countHops = countHops;
		this.traceRoute = traceRoute;
	}

	public AbstractFaceRoutingHeader createHeader(Address sender,
			Position senderPosition, Address receiver,
			Position receiverPosition, ServiceID routingID) {
		return new DominatingSetStartRoutingHeader(sender,senderPosition,receiver,receiverPosition,countHops,traceRoute,routingID);
	}
    
    public AbstractFaceRoutingHeader createGreedyFailureHeader(Address greedyFailureNode, Position greedyFailurePosition, PositionbasedRoutingHeader greedyHeader, ServiceID routingID) {
        return new DominatingSetStartRoutingHeader(greedyFailureNode,greedyFailurePosition,greedyHeader,routingID);
    }


//
    public LocationBasedRoutingHeader createAnycastGreedyFailureHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader otherRoutingHeader, Location location, ServiceID service_id) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public LocationBasedRoutingHeader createAnyCastHeader(Address sender, Position senderPosition, GeographicLocation location, ServiceID routingID) {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
