package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;

/**
 * This header is one of four headers for face routing on dominating set. Each header represents a 
 * different state of face routing on dominating set. This header is for continueing face routing on dominating set.
 * The state and the header is only changed in two cases:
 * 	1: A node is reached with destination as one hop neighbor. The header changes to DominatingSetEndRoutingHeader.
 * 	2: The packet was transmitted to a node formerly in dominating set but currently not, this could happen, if mobility is used. The header changes to DominatingSetEnter RoutingHeader.
 * 
 * @author Stefan Peters
 *
 */
public class DominatingSetContinueRoutingHeader extends DominatingSetFaceRoutingHeader {

	private static final long serialVersionUID = 3047871048731992868L;
	
	/**
	 * The constructor to copy the header
	 * @param header
	 */
	public DominatingSetContinueRoutingHeader(DominatingSetFaceRoutingHeader header) {
		super(header);
	}
	
	public LinkLayerInfo copy() {
		return new DominatingSetContinueRoutingHeader(this);
	}

	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {
		//first check whether destination is a oneHopNeighbor
		FaceRouting faceRouting=(FaceRouting) algorithm;
		PlanarizerService planarizerService=faceRouting.getPlanarizerService();
		PlanarGraphNode current=planarizerService.getPlanarGraphNode();
		RoutingStep step;
		DominatingSetFaceRoutingHeader header=this;
		if(destinationIsOneHopNeighbor(faceRouting)){ // destination is one hop neighbor
			
			step=handleEnd(faceRouting);
			header=new DominatingSetEndRoutingHeader(this);
		}else if(current==null){ // node is not in dominating set anymore
			step=handleEnter(faceRouting);
			header=new DominatingSetEnterRoutingHeader(this);
		}else { // destination is not a one hop neighbor
			step=handleNext(faceRouting);
		}
		if(step==null) {
			handler.dropMessage(this);
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
