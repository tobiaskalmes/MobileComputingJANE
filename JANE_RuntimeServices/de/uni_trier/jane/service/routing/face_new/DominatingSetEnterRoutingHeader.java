package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.planarizer.PlanarizerService;
import de.uni_trier.jane.service.routing.AbstractRoutingAlgorithm;
import de.uni_trier.jane.service.routing.RoutingTaskHandler;

/**
 * This header is one of four headers for face routing on dominating set. Each header represents a  
 * different state of face routing on dominating set. This header represents the state, that face routing has not started,
 * because a node in the domianting set has not reached, in this case three states are reachable:
 * 1. current node is a dominating set node, so face routing is started and the header changes to DominatingSetContinueRoutingHeader
 * 2. current node is not in the dominating set. In this case two states are reachable:
 * 		a: destination is a one hop neighbor, so the header changes to DominatingSetEndRoutingHeader
 * 		b: destination is not a one hop neighbor, so the packet is transferred to a neighbor in the dominating set
 * @author Stefan Peters
 *
 */
public class DominatingSetEnterRoutingHeader extends DominatingSetFaceRoutingHeader {

	private static final long serialVersionUID = 3047871048731992868L;
	
	/**
	 * The constructor
	 * @param header The former header of a different state
	 */
	public DominatingSetEnterRoutingHeader(DominatingSetFaceRoutingHeader header) {
		super(header);
	}

	public LinkLayerInfo copy() {
		return new DominatingSetEnterRoutingHeader(this);
	}


	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {
		FaceRouting faceRouting=(FaceRouting) algorithm;
		PlanarizerService planarizerService=faceRouting.getPlanarizerService();
		PlanarGraphNode current=planarizerService.getPlanarGraphNode();
		selectNextHopAction(handler, faceRouting, current);
	}

}
