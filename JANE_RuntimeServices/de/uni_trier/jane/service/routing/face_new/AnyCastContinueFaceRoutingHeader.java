package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;

/**
 * This is one of two anycast face routing headers. Each header represents one state of face routing.
 * This header is for continueing face routing until destination is reached.
 * @author Stefan Peters
 *
 */
public class AnyCastContinueFaceRoutingHeader extends AnyCastFaceRoutingHeader {

	private static final long serialVersionUID = -4800492781925076028L;

	/**
	 * Constructor	
	 * @param sender The address of the sending node
	 * @param senderPosition The position of the sending node
	 * @param location 
	 * @param countHops A flag to decide whether to count hops or not
	 * @param traceRoute A flog to decide whether to trace route or not
	 * @param routingID The ServiceID of the routing algorithm
	 */
	public AnyCastContinueFaceRoutingHeader(Address sender, Position senderPosition,
			GeographicLocation location, boolean countHops,
			boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition, location, countHops,
				traceRoute, routingID);
	}

	/**
	 * Constructor to copy a header
	 * @param header The header to copy
	 */
	public AnyCastContinueFaceRoutingHeader(AnyCastFaceRoutingHeader header) {
		super(header);
	}

	public LinkLayerInfo copy() {
		return new AnyCastContinueFaceRoutingHeader(this);
	}

	public void handle(RoutingTaskHandler handler,
			AbstractRoutingAlgorithm algorithm) {
		FaceRouting faceRouting=(FaceRouting) algorithm;
		
		if(deliverMessage(faceRouting)) { // destination reached
			handler.deliverMessage(this);
			return;
		}
		RoutingStep step=handleContinue(faceRouting);
		FaceRoutingHeader header=this;
		if(step==null) {
			handler.dropMessage(this);
			return;
		}
		if(step.isResumeGreedy()&& faceRouting.getGreedyID()!=null) {
			//continue greedy
			LocationRoutingAlgorithm_Sync recoveryStrategy = faceRouting.getAnycastRecovery();
			handler.delegateMessage(recoveryStrategy
							.getLocationRoutingHeader(this),this);
			return;
		}
		handler.forwardAsUnicast(header,step.getNode().getAddress());
	}

}
