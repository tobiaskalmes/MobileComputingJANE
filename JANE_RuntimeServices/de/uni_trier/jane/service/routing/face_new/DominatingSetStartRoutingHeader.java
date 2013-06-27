package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.planarizer.PlanarizerService;
import de.uni_trier.jane.service.routing.AbstractRoutingAlgorithm;
import de.uni_trier.jane.service.routing.RoutingTaskHandler;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * This header is one of four headers for face routing on dominating set. Each header represents a  
 * different state of face routing on dominating set. This header represents the state, that face routing has yet started,
 * because a node in the domianting set has been reached. In this state two cases are reachable
 * 1) The current node is in the dominating set. The header changes to DominatingSetContinueRoutingHeader.
 * 2) The current node is not currently in the dominating set, but was in the domainating set when the packet was transmitted. This could happen during mobility. The 
 *    Header changes to DominatingSetEnterRoutingHeader.
 * @author Stefan Peters
 *
 */
public class DominatingSetStartRoutingHeader extends DominatingSetFaceRoutingHeader {

	private static final long serialVersionUID = 3047871048731992868L;

	/**
	 * The constructor
	 * @param header The former header of a different state
	 */
	public DominatingSetStartRoutingHeader(DominatingSetFaceRoutingHeader header) {
		super(header);
	}
	
	/**
	 * The constructor the create the header for the first state
	 * @param sender The address of sending node
	 * @param senderPosition The position of sending node
	 * @param receiver The address of the receiving node
	 * @param receiverPosition The position of the receiving node
	 * @param countHops A flag to decide whether to count the hops or not
	 * @param traceRoute A flag to decide whether to trace the route or not
	 * @param routingID The id of the routing service
	 */
	public DominatingSetStartRoutingHeader(Address sender, Position senderPosition, Address receiver, Position receiverPosition, boolean countHops, boolean traceRoute, ServiceID routingID) {
		super(sender,senderPosition,receiver,receiverPosition,countHops,traceRoute,routingID);
	}

	/**
     * Constructor for class <code>DominatingSetStartRoutingHeader</code>
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param greedyHeader
     * @param routingID
     */
    public DominatingSetStartRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, PositionbasedRoutingHeader greedyHeader, ServiceID routingID) {
        super(greedyFailureNode,greedyFailurePosition,greedyHeader,routingID);
    }

    public LinkLayerInfo copy() {
		return new DominatingSetStartRoutingHeader(this);
	}


	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {
		//check whether node is in dominating set, or not.
		FaceRouting faceRouting=(FaceRouting) algorithm;
		PlanarizerService planarizerService=faceRouting.getPlanarizerService();
		PlanarGraphNode current=planarizerService.getPlanarGraphNode();
		setGreedyFailureNode(current);
		selectNextHopAction(handler, faceRouting, current);
		
	}

}
