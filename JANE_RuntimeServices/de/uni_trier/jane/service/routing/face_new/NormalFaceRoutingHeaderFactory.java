package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * This factory creates a new FaceRoutingHeader needed by normal face routing algorithm
 * @author Stefan Peters
 *
 */
public class NormalFaceRoutingHeaderFactory implements FaceRoutingHeaderFactory {

	private boolean countHops;
	private boolean traceRoute;
	
	/**
	 * Creates a new Factory
	 * @param countHops A flag, whether number of hops are counted or not
	 * @param traceRoute A flag, whether route is ttraced or not
	 * @param neighborDiscoveryServiceID The ServiceID of a neighbor discovery service
	 * @param planarizerServiceID The ServiceID of a planarizer service
	 * @param greedyID The ServiceID of a greedy routing service
	 * @param stepSelector A StepSelector to choose next routing step
	 */
	public NormalFaceRoutingHeaderFactory(boolean countHops,
			boolean traceRoute) {
		this.countHops=countHops;
		this.traceRoute=traceRoute;
	}
	
	public AbstractFaceRoutingHeader createHeader(Address sender,
			Position senderPosition, Address receiver,
			Position receiverPosition, ServiceID routingID) {
		return new StartFaceRoutingHeader(sender,senderPosition,receiver,receiverPosition,
				countHops,traceRoute,routingID);
	}
    
    public AbstractFaceRoutingHeader createGreedyFailureHeader(Address sender, Position senderPosition, 
            //Address lastSender, Position lastSenderPosition,
            PositionbasedRoutingHeader positionbasedRoutingHeader, ServiceID routingID) {

        AbstractFaceRoutingHeader header = new StartFaceRoutingHeader(sender,senderPosition,(DefaultRoutingHeader) positionbasedRoutingHeader, positionbasedRoutingHeader.getReceiverPosition(),routingID);
        
        return header;
    }
    

	public LocationBasedRoutingHeader createAnyCastHeader(Address sender, Position senderPosition, GeographicLocation location, ServiceID routingID) {
		return new AnyCastStartFaceRoutingHeader(sender,senderPosition,location,countHops,traceRoute,routingID);
	}
    
    
    public LocationBasedRoutingHeader createAnycastGreedyFailureHeader(Address sender, Position senderPosition, RoutingHeader routingHeader, Location location, ServiceID routingID) {
     
        return new AnyCastStartFaceRoutingHeader(sender,senderPosition,routingHeader,(GeographicLocation) location,routingID);
    }

}
