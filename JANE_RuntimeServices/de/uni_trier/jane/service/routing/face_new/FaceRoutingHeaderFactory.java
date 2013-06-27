package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * An interface describing the method a header factory for face routing have to
 * provide
 * 
 * @author Stefan Peters
 * 
 */
public interface FaceRoutingHeaderFactory {

	/**
	 * Creates a new Header
	 * 
	 * @param sender
	 *            The sender address
	 * @param senderPosition
	 *            The sender position
	 * @param receiver
	 *            The receiver address
	 * @param receiverPosition
	 *            The receiver position
	 * @param routingID
	 *            The id of the routing servce
	 * @return Returns a new Header
	 */
	public AbstractFaceRoutingHeader createHeader(Address sender,
			Position senderPosition, Address receiver,
			Position receiverPosition, ServiceID routingID);

	/**
	 * Creates a new Header for anycast
	 * 
	 * @param sender
	 *            The sender address
	 * @param senderPosition
	 *            The sender position
	 * @param location
	 *            The geographic location this message should be transferred to
	 * @param routingID
	 *            The id of the routing service
	 * @return Returns a new Header
	 */
	public LocationBasedRoutingHeader createAnyCastHeader(Address sender,
			Position senderPosition, GeographicLocation location,
			ServiceID routingID);

    /**
     * 
     * TODO Comment method
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param greedyHeader
     * @param service_id
     * @return
     */
    public AbstractFaceRoutingHeader createGreedyFailureHeader(
            Address greedyFailureNode,
            Position greedyFailurePosition,
            PositionbasedRoutingHeader greedyHeader, 
            ServiceID service_id);

    /**
     * 
     * TODO: comment method 
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param otherRoutingHeader
     * @param location
     * @param service_id
     * @return
     */
    public LocationBasedRoutingHeader createAnycastGreedyFailureHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader otherRoutingHeader, Location location, ServiceID service_id);
}
