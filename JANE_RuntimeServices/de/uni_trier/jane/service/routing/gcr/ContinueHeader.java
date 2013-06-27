package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.routing.greedy.PositionUnicastRoutingAlgorithm_Sync;

public class ContinueHeader extends GeographicClusterRoutingHeader {

	protected PlanarGraphNode previousNode;
	protected Address nextClusterAddress;
	private Position lastIntersection;
	private boolean clockwise;
	private StartCondition startCondition;
	private CrossingCondition crossingCondition;
	private TurnCondition turnCondition;
	private ResumeGreedyCondition resumeGreedyCondition;
	private BreakCondition breakCondition;
	private FinishCondition finishCondition;
	
	public ContinueHeader(GeographicClusterRoutingHeader header, Address sourceAddress, Position sourcePosition,
			Address destinationAddress, Position destinationPosition,
			PlanarGraphNode previousNode, Address nextClusterAddress,
			Position lastIntersection, boolean clockwise,
			StartCondition startCondition, CrossingCondition crossingCondition,
			TurnCondition turnCondition, ResumeGreedyCondition resumeGreedyCondition,
			BreakCondition breakCondition, FinishCondition finishCondition) {
		super(header);
		this.previousNode = previousNode;
		this.nextClusterAddress = nextClusterAddress;
		this.lastIntersection = lastIntersection;
		this.clockwise = clockwise;
		this.startCondition = startCondition;
		this.crossingCondition = crossingCondition;
		this.turnCondition = turnCondition;
		this.resumeGreedyCondition = resumeGreedyCondition;
		this.breakCondition = breakCondition;
		this.finishCondition = finishCondition;
	}

	public ContinueHeader(ContinueHeader other) {
		super(other);
		this.previousNode = other.previousNode;
		this.nextClusterAddress = other.nextClusterAddress;
		this.lastIntersection = other.lastIntersection;
		this.clockwise = other.clockwise;
		this.startCondition = other.startCondition;
		this.crossingCondition = other.crossingCondition;
		this.turnCondition = other.turnCondition;
		this.resumeGreedyCondition = other.resumeGreedyCondition;
		this.breakCondition = other.breakCondition;
		this.finishCondition = other.finishCondition;
	}

	public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {

		GeographicClusterRoutingAlgorithm geographicClusterRoutingAlgorithm = (GeographicClusterRoutingAlgorithm)algorithm;
		
		// check if the final destination node was reached
		Address ownAddress = geographicClusterRoutingAlgorithm.getNeighborDiscoveryService().getOwnAddress();
		if(ownAddress.equals(getReceiver())) {
			handler.deliverMessage(this);
			return;
		}

		Cluster currentCluster = getCurrentCluster(geographicClusterRoutingAlgorithm);
		Cluster destinationCluster = getDestinationCluster(geographicClusterRoutingAlgorithm);
		
		// we reached the final destination cluster
		if (currentCluster.getAddress().equals(destinationCluster.getAddress())) {

			// determine the next hop node and forward message
			NeighborDiscoveryData nextNode = findDestinationClusterCandidateNode(geographicClusterRoutingAlgorithm);
			GeographicClusterRoutingHeader header = new EndHeader(this);
			forwardIfPossible(handler, header, nextNode);

		}

		else {

			// check if we reached the next cluster
			if(currentCluster.getAddress().equals(nextClusterAddress)) {

				// find the next step in face exploration
				RoutingStep[] steps = geographicClusterRoutingAlgorithm.getPlanarGraphExplorer().continueExploration(
						getCurrentClusterAsPlanarGraphNode(geographicClusterRoutingAlgorithm),
						getNeighborNetworkNodes(geographicClusterRoutingAlgorithm),
						previousNode,
						getSourceClusterAsNetworkNode(geographicClusterRoutingAlgorithm),
						getDestinationClusterAsNetworkNode(geographicClusterRoutingAlgorithm),
                        getCluster(geographicClusterRoutingAlgorithm),
						
						lastIntersection,
						clockwise,
						startCondition,
						crossingCondition,
						turnCondition,
						finishCondition,
						breakCondition,
						resumeGreedyCondition);
				RoutingStep step = steps[steps.length - 1];
				
				// we are done if planar graph routing was breaked
				if(step.isBreaked()) {
					handler.dropMessage(this);
				}else if(step.isResumeGreedy()){
                    super.handleGreedy(geographicClusterRoutingAlgorithm,handler);

                }

				// do the next routing step
				else {
					
					// get the next cluster address
					Address nextClusterAddress;
					if(step.getNode().isVirtual()) {
						nextClusterAddress = step.getNode().getRelayNode().getAddress();
					}
					else {
						nextClusterAddress = step.getNode().getAddress();
					}
					ClusterDiscoveryService clusterDiscoveryService = geographicClusterRoutingAlgorithm.getClusterDiscoveryService();
					Cluster nextCluster = clusterDiscoveryService.getClusterFromAddress(nextClusterAddress);

					// get the next hop node and forward the message if possible
					NeighborDiscoveryData nextNode = findCandidateNode(geographicClusterRoutingAlgorithm, currentCluster, nextCluster);
					RoutingStep previousStep = steps[steps.length - 2];
					GeographicClusterRoutingHeader header = createHeader(handler, step, previousStep);
					forwardIfPossible(handler, header, nextNode);

				}

			}
			
			// we are on the way into the next cluster
			else {

				// get the next cluster and forward the message to the best node regarding this cluster
				Cluster nextCluster = geographicClusterRoutingAlgorithm.getClusterDiscoveryService().getClusterMap().getCluster(
						this.nextClusterAddress);
				Cluster senderCluster = geographicClusterRoutingAlgorithm.getClusterDiscoveryService().getClusterFromAddress(previousNode.getAddress());
				forwardOrRestart(handler, geographicClusterRoutingAlgorithm, senderCluster, nextCluster, this);

			}

		}

	}



    public LinkLayerInfo copy() {
		return new ContinueHeader(this);
	}

}
