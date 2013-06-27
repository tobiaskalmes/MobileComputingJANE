package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.planarizer.PlanarizerService;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.RoutingStep;
import de.uni_trier.jane.service.routing.greedy.PositionUnicastRoutingAlgorithm_Sync;

/**
 * This is one of two states anycast face routing could reach. This state is the starting state.
 * The header is automatically changed to AnyCastContinueFaceRoutingHeader.
 * @author Stefan Peters
 *
 */
public class AnyCastStartFaceRoutingHeader extends AnyCastFaceRoutingHeader {
	
	private static final long serialVersionUID = 8853990097698253727L;

	/**
	 * The constructor to create the starting header
	 * @param sender The address of the sender
	 * @param senderPosition The position of the sender
	 * @param location The geographic location this message should be transferred to
	 * @param countHops A flag to decide to count the hops or not
	 * @param traceRoute A flag to decide to trace the route or not
	 * @param routingID The id of the routing service
	 */
	public AnyCastStartFaceRoutingHeader(Address sender, Position senderPosition,
			GeographicLocation location, boolean countHops,
			boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition, location, countHops,
				traceRoute, routingID);
	}

	/**
	 * Constructor
	 * @param header An existing header
	 */
	public AnyCastStartFaceRoutingHeader(AnyCastStartFaceRoutingHeader header) {
		super(header);
	}

	/**
     * 
     * Constructor for class <code>AnyCastStartFaceRoutingHeader</code>
     *
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param routingHeader
     * @param location
     * @param routingID
	 */
    public AnyCastStartFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader routingHeader, GeographicLocation location, ServiceID routingID) {
        super(greedyFailureNode,greedyFailurePosition,routingHeader,location,routingID);
    }

    public LinkLayerInfo copy() {
		return new AnyCastStartFaceRoutingHeader(this);
	}

	public void handle(RoutingTaskHandler handler,
			AbstractRoutingAlgorithm algorithm) {
		FaceRouting faceRouting=(FaceRouting) algorithm;
		PlanarizerService planarizerService=faceRouting.getPlanarizerService();
		PlanarGraphNode current=planarizerService.getPlanarGraphNode();
		setGreedyFailureNode(current);
		RoutingStep step=handleStart(faceRouting);
		if(deliverMessage(faceRouting)) { // destination reached
			handler.deliverMessage(this);
			return;
		}
		if(step==null) {
			handler.dropMessage(this);
			return;
		}
		FaceRoutingHeader header=new AnyCastContinueFaceRoutingHeader(this);
		if(step.isResumeGreedy()&& faceRouting.getGreedyID()!=null) {
			LocationRoutingAlgorithm_Sync recoveryStrategy = faceRouting.getAnycastRecovery();
			handler.delegateMessage(recoveryStrategy
							.getLocationRoutingHeader(this),this);
			return;
		}
		
		handler.forwardAsUnicast(header,step.getNode().getAddress());

	}


}
