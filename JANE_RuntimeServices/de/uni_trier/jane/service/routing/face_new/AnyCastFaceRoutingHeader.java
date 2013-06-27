package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * An abstract Version of an anycast Facerouting algorithm
 * @author Stefan Peters
 *
 */
public abstract class AnyCastFaceRoutingHeader extends FaceRoutingHeader implements LocationBasedRoutingHeader {
	
	private GeographicLocation geographicLocation;
	
	/**
	 * 
	 * @param sender The address of the sender
	 * @param senderPosition The position of the sender
	 * @param geographicLocation The geographic location this message schould be transferred to
	 * @param countHops A flag, whether to count hops or not
	 * @param traceRoute The flag, whether to trace route or not
	 * @param routingID The id of the routing service
	 */
	public AnyCastFaceRoutingHeader(Address sender, Position senderPosition,
			GeographicLocation geographicLocation, boolean countHops,
			boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition,null, geographicLocation.getCenterPosition(), countHops,
				traceRoute, routingID);
		this.geographicLocation=geographicLocation;
	}
	
	/**
	 * 
	 * @param header
	 */
	public AnyCastFaceRoutingHeader(AnyCastFaceRoutingHeader header) {
		super(header);
		this.geographicLocation=header.geographicLocation;
	}

	/**
     * Constructor for class <code>AnyCastFaceRoutingHeader</code>
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param locationBasedRoutingHeader
     * @param routingID
     */
    public AnyCastFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader routingHeader, GeographicLocation location, ServiceID routingID) {
        super(greedyFailureNode,greedyFailurePosition,routingHeader, location.getCenterPosition() ,routingID);
        geographicLocation=location;
    }
    
    //
    public Position getReceiverPosition() {

        return geographicLocation.getCenterPosition();
    }

    /**
	 * 
	 * @return Returns the destination area
	 */
	public GeographicLocation getGeographicLocation() {
		return geographicLocation;
	}

	protected boolean deliverMessage(FaceRouting algorithm) {
		NetworkNode node=algorithm.getNetworkNode();
		return geographicLocation.isInside(node.getPosition());
	}

	public Location getTargetLocation() {
		return geographicLocation;
	}
	
}
