package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * This is one of two normal face routing headers. Each header represents one state of face routing.
 * This header is for continueing face routing until destination is reached.
 * @author Stefan Peters
 *
 */
public class ContinueFaceRoutingHeader extends FaceRoutingHeader {

	private static final long serialVersionUID = -4800492781925076028L;

	/**
	 * Constructor	
	 * @param sender The address of the sending node
	 * @param senderPosition The position of the sending node
	 * @param receiver The address of the receiver, if available
	 * @param receiverPosition The position of receiver
	 * @param countHops A flag to decide whether to count hops or not
	 * @param traceRoute A flog to decide whether to trace route or not
	 * @param routingID The ServiceID of the routing algorithm
	 */
	public ContinueFaceRoutingHeader(Address sender, Position senderPosition,
			Address receiver, Position receiverPosition, boolean countHops,
			boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition, receiver, receiverPosition, countHops,
				traceRoute, routingID);
	}

	/**
	 * Constructor to copy a header
	 * @param header The header to copy
	 */
	public ContinueFaceRoutingHeader(FaceRoutingHeader header) {
		super(header);
	}

	/**
     * Constructor for class <code>ContinueFaceRoutingHeader</code>
     * @param sender
     * @param senderPosition
     * @param positionbasedRoutingHeader
     * @param routingID
     */
    public ContinueFaceRoutingHeader(Address sender, Position senderPosition, PositionbasedRoutingHeader positionbasedRoutingHeader,Position receiverPosition, ServiceID routingID) {
        super(sender,senderPosition,positionbasedRoutingHeader,receiverPosition,routingID);
    }

    public LinkLayerInfo copy() {
		return new ContinueFaceRoutingHeader(this);
	}

	public void handle(RoutingTaskHandler handler,
			AbstractRoutingAlgorithm algorithm) {
		FaceRouting faceRouting=(FaceRouting) algorithm;
		
		if(deliverMessage(faceRouting)) { // destination reached
			handler.deliverMessage(this);
			//System.err.println("deliver message to " + receiverPosition);
			return;
		}
		RoutingStep step=handleContinue(faceRouting);
		FaceRoutingHeader header=this;
		if(step==null) {
			handler.dropMessage(this);
			//System.err.println("dropped message");
			return;
		}
		if(step.isResumeGreedy()&& faceRouting.getGreedyID()!=null) {
			//continue greedy
			PositionUnicastRoutingAlgorithm_Sync recoveryStrategy = faceRouting.getRecovery();
			handler.delegateMessage(recoveryStrategy
							.getPositionBasedHeader(this),this);
			return;
		}
		handler.forwardAsUnicast(header,step.getNode().getAddress());
	}

}
