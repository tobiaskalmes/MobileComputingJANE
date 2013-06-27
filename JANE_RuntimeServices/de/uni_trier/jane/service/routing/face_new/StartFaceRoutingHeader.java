package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.planarizer.PlanarizerService;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.RoutingStep;
import de.uni_trier.jane.service.routing.greedy.PositionUnicastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * This is one of two states normal face routing could reach. This state is the starting state.
 * The header is automatically changed to ContinueFaceRoutingHeader.
 * @author Stefan Peters
 *
 */
public class StartFaceRoutingHeader extends FaceRoutingHeader {
	
	private static final long serialVersionUID = 8853990097698253727L;

	/**
	 * The constructor to create the starting header
	 * @param sender The address of the sender
	 * @param senderPosition The position of the sender
	 * @param receiver The address of the receiver
	 * @param receiverPosition The position of the receiver
	 * @param countHops A flag to decide to count the hops or not
	 * @param traceRoute A flag to decide to trace the route or not
	 * @param routingID The id of the routing service
	 */
	public StartFaceRoutingHeader(Address sender, Position senderPosition,
			Address receiver, Position receiverPosition, boolean countHops,
			boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition, receiver, receiverPosition, countHops,
				traceRoute, routingID);
	}

	/**
	 * Constructor
	 * @param header An existing header
	 */
	public StartFaceRoutingHeader(FaceRoutingHeader header) {
		super(header);
	}

	/**
     * 
     * Constructor for class <code>StartFaceRoutingHeader</code>
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param positionbasedRoutingHeader
     * @param routingID
	 */
    public StartFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, DefaultRoutingHeader positionbasedRoutingHeader, Position receiverPosition, ServiceID routingID) {
        super (greedyFailureNode,greedyFailurePosition,positionbasedRoutingHeader, receiverPosition, routingID);
    }

    public LinkLayerInfo copy() {
		return new StartFaceRoutingHeader(this);
	}

	public void handle(RoutingTaskHandler handler,
			AbstractRoutingAlgorithm algorithm) {
		FaceRouting faceRouting=(FaceRouting) algorithm;
		PlanarizerService planarizerService=faceRouting.getPlanarizerService();
		PlanarGraphNode current=planarizerService.getPlanarGraphNode();
		setGreedyFailureNode(current);
		RoutingStep step=handleStart(faceRouting);
		if(step==null) {
			handler.dropMessage(this);
			return;
		}
		FaceRoutingHeader header=new ContinueFaceRoutingHeader(this);
		if(step.isResumeGreedy()&& faceRouting.getGreedyID()!=null) {
			PositionUnicastRoutingAlgorithm_Sync recoveryStrategy = faceRouting.getRecovery();
			handler.delegateMessage(recoveryStrategy
							.getPositionBasedHeader(this),this);
			return;
		}
		
		handler.forwardAsUnicast(header,step.getNode().getAddress());

	}

}
