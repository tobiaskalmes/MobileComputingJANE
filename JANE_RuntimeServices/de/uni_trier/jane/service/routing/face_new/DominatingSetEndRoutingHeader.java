package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;

/**
 * This header is one of four headers for face routing on dominating set. Each header represents a  
 * different state of face routing on dominating set. This header represents the state, that destination is
 * a one hop neighbor. In this state two cases are reachable:
 * 	1: Destinaion was formerly a one hop neighbor, but currently, if mobility is used, it is not a neighbor.
 * 		In this case the header changes to DominatingSetEnterRoutingHeader to start a new dominating set face routing with old destination position information.
 *  2: Destination is currently a one hop neighbor, so the packet can be transmitted to destination, and this routing task has finished.
 * @author Stefan Peters
 *
 */
public class DominatingSetEndRoutingHeader extends DominatingSetFaceRoutingHeader {

	private static final long serialVersionUID = 3047871048731992868L;

	/**
	 * Constructor to copy the header
	 * @param header The DominatingSetFaceRoutingHeader
	 */
	public DominatingSetEndRoutingHeader(DominatingSetFaceRoutingHeader header) {
		super(header);
	}

	public LinkLayerInfo copy() {
		return new DominatingSetEndRoutingHeader(this);
	}


	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {
		FaceRouting faceRouting=(FaceRouting) algorithm;
		if(faceRouting.getNetworkNode().getAddress().equals(destinationAddress)) { // destination reached
			handler.deliverMessage(this);
			return;
		}
		RoutingStep step;
		DominatingSetFaceRoutingHeader header=this;
		
		if(destinationIsOneHopNeighbor(faceRouting)) {
			step=handleEnd(faceRouting);
		}else {
			step=handleEnter(faceRouting);
			header=new DominatingSetEnterRoutingHeader(this);
		}
		
		if(step==null) {
			handler.dropMessage(this);
			return;
		}
		if(step.isResumeGreedy() && faceRouting.getGreedyID()!=null) {
			PositionUnicastRoutingAlgorithm_Sync recoveryStrategy = faceRouting.getRecovery();
			handler.delegateMessage(recoveryStrategy
							.getPositionBasedHeader(header),this);
			return;
		}
		handler.forwardAsUnicast(header,step.getNode().getAddress());
		
	}

}
