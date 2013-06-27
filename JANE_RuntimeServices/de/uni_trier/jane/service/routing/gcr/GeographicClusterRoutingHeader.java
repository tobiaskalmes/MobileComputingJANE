package de.uni_trier.jane.service.routing.gcr;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.routing.greedy.PositionUnicastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

public abstract class GeographicClusterRoutingHeader extends PositionBasedRecoveryHeader {

	

    public GeographicClusterRoutingHeader(Address sourceAddress, Position sourcePosition, Address destinationAddress, Position destinationPosition) {
		super(sourceAddress, destinationAddress, false, false, destinationPosition, sourcePosition, sourceAddress, sourcePosition);
	}

	public GeographicClusterRoutingHeader(GeographicClusterRoutingHeader other) {
		super(other);
	}

	/**
     * Constructor for class <code>GeographicClusterRoutingHeader</code>
     *
     * @param positionbasedRoutingHeader
	 * @param greedyFailureAddress 
	 * @param greeedyFailurePosition 
     */
    public GeographicClusterRoutingHeader(PositionbasedRoutingHeader positionbasedRoutingHeader, Address greedyFailureAddress, Position greeedyFailurePosition) {
        super(positionbasedRoutingHeader,greedyFailureAddress,greeedyFailurePosition);
    }

    public ServiceID getRoutingAlgorithmID() {
		return GeographicClusterRoutingServiceImpl.SERVICE_ID;
	}

//	public abstract void handle(RoutingTaskHandler handler, GeographicClusterRoutingAlgorithm algorithm);

	protected NetworkNode getDestinationClusterAsNetworkNode(GeographicClusterRoutingAlgorithm algorithm) {
		Cluster destinationCluster = getDestinationCluster(algorithm);
		// TODO Geometrieberechnungen auf der Basis von doubles sind nicht exakt. Damit ist es möglich, dass
		// für zwei Geraden, welche sich nur in einem Endpunkt schneiden, der Schnitt nicht erkannt werden kann.
		// die minimale Verschiebung des Zielknotens verhindert diesen Fehler.
		Position position = destinationCluster.getCenter();
		position = position.add(1.0, 1.0, 0.0);
		return new ClusterNetworkNodeImpl(destinationCluster.getAddress(), position, getReceiver(),getReceiverPosition());
	}

	protected PlanarGraphNode getCurrentClusterAsPlanarGraphNode(GeographicClusterRoutingAlgorithm algorithm) {
		return algorithm.getClusterPlanarizerService().getPlanarGraphNode();
	}

	protected Cluster getCurrentCluster(GeographicClusterRoutingAlgorithm algorithm) {
		return algorithm.getClusterDiscoveryService().getHostCluster();
	}
	
	protected NetworkNode[] getNeighborNetworkNodes(GeographicClusterRoutingAlgorithm algorithm) {
		PlanarGraphNode source = getCurrentClusterAsPlanarGraphNode(algorithm);
		return source.getAllOneHopNeighbors();
	}

	protected NetworkNode getGreedyFailureNetworkNode() {
		Address greedyFailureAddress = getGreedyFailureAddress();
		Position greedyFailurePosition = getGreedyFailurePosition();
		return new NetworkNodeImpl(greedyFailureAddress, greedyFailurePosition);
	}

	protected NetworkNode getDestinationNetworkNode() {
		return new NetworkNodeImpl(destinationAddress, receiverPosition);
	}

	protected NetworkNode getSourceClusterAsNetworkNode(GeographicClusterRoutingAlgorithm algorithm) {
		Cluster sourceCluster = algorithm.getClusterDiscoveryService().getClusterMap().getCluster(getSenderPosition());
		return new NetworkNodeImpl(sourceCluster.getAddress(), sourceCluster.getCenter());
	}
    
    /**
     * TODO Comment method
     * @param greedyFailureNetworkNode
     * @param geographicClusterRoutingAlgorithm 
     * @return
     */
    public NetworkNode getCluster( GeographicClusterRoutingAlgorithm geographicClusterRoutingAlgorithm) {
        //Cluster cluster=geographicClusterRoutingAlgorithm.getClusterDiscoveryService().getClusterMap().getCluster(greedyFailureNetworkNode.getPosition());
        //return new NetworkNodeImpl(cluster.getAddress(), cluster.getCenter());
        return getGreedyFailureNetworkNode();
    }

	// TODO Achtung: kann null zurückgeben!
	protected NeighborDiscoveryData findCandidateNode(GeographicClusterRoutingAlgorithm algorithm, Cluster senderCluster, Cluster nextCluster) {
		
		// determine required information
		ClusterDiscoveryService clusterDiscoveryService = algorithm.getClusterDiscoveryService();
//		Cluster currentCluster = clusterDiscoveryService.getHostCluster();
		Map candidateMap = new HashMap();
		ClusterTransitionNodeOrder clusterNodeOrder = algorithm.getClusterNodeOrder();
		NeighborDiscoveryService_sync neighborDiscoveryService = algorithm.getNeighborDiscoveryService();
		NeighborDiscoveryData ownData = getOwnData(algorithm);
		
		// when the current node is able to reach the next cluster C in one hop we may only select a node which is
		// able to reach C and which provides progress towards this cluster.
		if(clusterDiscoveryService.isReachableFromNode(nextCluster.getAddress())) {
			
System.out.println(algorithm.getNeighborDiscoveryService().getOwnAddress() + ":" + senderCluster + "->" + nextCluster);

			Address[] candidateAddresses = clusterDiscoveryService.getOneHopGatewayNodes(nextCluster.getAddress());
			addToCandidateMap(candidateMap, ownData, candidateAddresses, senderCluster, nextCluster,
					clusterNodeOrder, neighborDiscoveryService);
		}

		// when the current node is not able to reach the next cluster C in one hop we are allowed to consider all
		// nodes which are able to reach C in one hop or all nodes which are able to reach C in two hops and which
		// provide progress towards this cluster
		else {
			Address[] candidateAddresses = clusterDiscoveryService.getOneHopGatewayNodes(nextCluster.getAddress());
			for(int i=0; i<candidateAddresses.length; i++) {
				NeighborDiscoveryData candidateData = neighborDiscoveryService.getNeighborDiscoveryData(candidateAddresses[i]);
				candidateMap.put(candidateData.getSender(), candidateData);
			}
			candidateAddresses = clusterDiscoveryService.getTwoHopGatewayNodes(nextCluster.getAddress());
			addToCandidateMap(candidateMap, ownData, candidateAddresses, senderCluster, nextCluster,
					clusterNodeOrder, neighborDiscoveryService);
		}

		// the current node is not allowed as candidate node
		candidateMap.remove(ownData.getSender());
		
		// create candidate nodes and return null if the set is empty
		Collection values = candidateMap.values();
		NeighborDiscoveryData[] candidates = (NeighborDiscoveryData[])values.toArray(new NeighborDiscoveryData[values.size()]);

		// select and return the best node out of the candidate nodes
		Cluster destinationCluster = getDestinationCluster(algorithm);
		Cluster sourceCluster = getSourceCluster(algorithm);
		NetworkNode sourceNode = getSourceNode();
		NetworkNode destinationNode = getDestinationNode();
		NextNodeSelector nextNodeSelector = algorithm.getNextNodeSelector();
		return nextNodeSelector.selectNextNode(sourceCluster, destinationCluster, senderCluster, nextCluster, sourceNode,
				destinationNode, ownData, candidates);

	}

	protected NeighborDiscoveryData findDestinationClusterCandidateNode(GeographicClusterRoutingAlgorithm algorithm) {

		// get all required data
		Cluster destinationCluster = getDestinationCluster(algorithm);
		DestinationClusterNodeOrder destinationClusterNodeOrder = algorithm.getDestinationClusterNodeOrder();
		
		// determine candidate nodes
		Map candidateMap = new HashMap();
		NeighborDiscoveryService_sync neighborDiscoveryService = algorithm.getNeighborDiscoveryService();
		NeighborDiscoveryData ownData = neighborDiscoveryService.getNeighborDiscoveryData(neighborDiscoveryService.getOwnAddress());
		
		NetworkNode[] candidateNodes = getNeighborNetworkNodes(algorithm); // TODO Achtung dies funktioniert nur solange planare Cluster-Knoten die Netzwerkknoten als alle erreichbaren Ein-Hop-Nachbarn zurückliefert
		for(int i=0; i<candidateNodes.length; i++) {
			NeighborDiscoveryData candidateData = neighborDiscoveryService.getNeighborDiscoveryData(candidateNodes[i].getAddress());
			if(candidateData != null) {
				if(destinationClusterNodeOrder.isLessThan(ownData, candidateData, getDestinationNetworkNode(), destinationCluster)) {
					candidateMap.put(candidateData.getSender(), candidateData);
				}
			}
		}
		
		candidateMap.remove(ownData.getSender());
		Collection values = candidateMap.values();
		NeighborDiscoveryData[] candidates = (NeighborDiscoveryData[])values.toArray(new NeighborDiscoveryData[values.size()]);

		// select best node
		Cluster sourceCluster = getSourceCluster(algorithm);
		NetworkNode sourceNode = getSourceNode();
		NetworkNode destinationNode = getDestinationNode();
		DestinationClusterNextNodeSelector nextNodeSelector = algorithm.getDestinationClusterNextNodeSelector();
		return nextNodeSelector.selectNextNode(sourceCluster, destinationCluster, sourceNode, destinationNode, ownData, candidates);

	}

	// Determine all neighbor nodes which are less than the current node regarding the given cluster node order
	// and which have valid neighbor discovery data.
	private void addToCandidateMap(Map candidateMap, NeighborDiscoveryData ownData, Address[] candidateAddresses,
			Cluster currentCluster, Cluster nextCluster, ClusterTransitionNodeOrder clusterNodeOrder,
			NeighborDiscoveryService_sync neighborDiscoveryService) {
		for(int i=0; i<candidateAddresses.length; i++) {
			NeighborDiscoveryData candidateData = neighborDiscoveryService.getNeighborDiscoveryData(
					candidateAddresses[i]);
			if(candidateData != null) {
				if(clusterNodeOrder.isLessThan(ownData, candidateData, currentCluster, nextCluster)) {
					candidateMap.put(candidateData.getSender(), candidateData);
				}
			}
		}
	}

	protected void startForwarding(RoutingTaskHandler handler, GeographicClusterRoutingAlgorithm algorithm) {
		
		// create conditions
		StartCondition startCondition = algorithm.createStartCondition();
		CrossingCondition crossingCondition = algorithm.createCrossingCondition();
		TurnCondition turnCondition = algorithm.createTurnCondition();
		ResumeGreedyCondition resumeGreedyCondition = algorithm.createResumeGreedyCondition();
		BreakCondition breakCondition = algorithm.createBreakCondition();
		Cluster destinationCluster = getDestinationCluster(algorithm);
		FinishCondition finishCondition = algorithm.createFinishCondition(destinationCluster.getAddress());

		// do the first step in face exploration
		RoutingStep[] steps = algorithm.getPlanarGraphExplorer().startExploration(
				getCurrentClusterAsPlanarGraphNode(algorithm),
				getNeighborNetworkNodes(algorithm),
				getDestinationClusterAsNetworkNode(algorithm),
				getCluster(algorithm),
				startCondition,
				crossingCondition,
				turnCondition,
				finishCondition,
				breakCondition,
				resumeGreedyCondition);
		RoutingStep step = steps[steps.length - 1];
		
		// we are done when planar graph routing was breaked
        if(step.isResumeGreedy()){
            handleGreedy(algorithm,handler);
		}else if(step.isBreaked()) {
            handler.dropMessage(this);
        }else {    

		// do the next routing step
		
			
			// get the current and the next cluster
			ClusterDiscoveryService clusterDiscoveryService = algorithm.getClusterDiscoveryService();
			Cluster senderCluster = clusterDiscoveryService.getHostCluster();
			Cluster nextCluster = clusterDiscoveryService.getClusterFromAddress(step.getNode().getAddress());
			
			// determine the next hop node
			NeighborDiscoveryData nextNode = findCandidateNode(algorithm, senderCluster, nextCluster);

			// forward message if possible
			RoutingStep previousStep = steps[steps.length - 2];
			GeographicClusterRoutingHeader header = createHeader(handler, step, previousStep);
			forwardIfPossible(handler, header, nextNode);
			
		}

	}

	protected void forwardOrRestart(RoutingTaskHandler handler, GeographicClusterRoutingAlgorithm algorithm,
			Cluster senderCluster, Cluster nextCluster, GeographicClusterRoutingHeader header) {
		
		// determine the next hop node
		NeighborDiscoveryData nextNode = findCandidateNode(algorithm, senderCluster, nextCluster);
		
		// forward when the next cluster is reachable
		if(nextNode != null) {
			handler.forwardAsUnicast(header, nextNode.getSender());
		}
		
		// restart planar graph routing when the next cluster is no more reachable (e.g. due to mobility)
		else {
			startForwarding(handler, algorithm);
		}
		
	}
	
	protected void forwardIfPossible(RoutingTaskHandler handler, GeographicClusterRoutingHeader header, NeighborDiscoveryData nextNode) {
		if(nextNode != null) {
			handler.forwardAsUnicast(header, nextNode.getSender());
		}
		else {
			handler.dropMessage(this);
		}
	}
	
	protected GeographicClusterRoutingHeader createHeader(RoutingTaskHandler handler, RoutingStep step, RoutingStep previousStep) {
		GeographicClusterRoutingHeader header;
		if(step.getNode().isVirtual()) {
			header = new RelayHeader(
                    this,
					getSender(),
					getSenderPosition(),
					getReceiver(),
					getReceiverPosition(),
					previousStep.getNode(),
					step.getNode().getRelayNode().getAddress(),
					step.getNode().getAddress(),
					step.getLastIntersection(),
					step.isClockwise(),
					step.getStartCondition(),
					step.getCrossingCondition(),
					step.getTurnCondition(),
					step.getResumeGreedyCondition(),
					step.getBreakCondition(),
					step.getFinishCondition());
		}
		else {
			header = new ContinueHeader(
                    this,
					getSender(),
					getSenderPosition(),
					getReceiver(),
					getReceiverPosition(),
					previousStep.getNode(),
					step.getNode().getAddress(),
					step.getLastIntersection(),
					step.isClockwise(),
					step.getStartCondition(),
					step.getCrossingCondition(),
					step.getTurnCondition(),
					step.getResumeGreedyCondition(),
					step.getBreakCondition(),
					step.getFinishCondition());
		}
		header.setMessageID(getMessageID());
		header.setLoopChecker(getLoopChecker());
		return header;
//		handler.forwardAsUnicast(header, nextNode.getSender());
	}


	protected Cluster getDestinationCluster(GeographicClusterRoutingAlgorithm algorithm) {
		return algorithm.getClusterDiscoveryService().getClusterMap().getCluster(getReceiverPosition());
	}

	private Cluster getSourceCluster(GeographicClusterRoutingAlgorithm algorithm) {
		return algorithm.getClusterDiscoveryService().getClusterMap().getCluster(getSenderPosition());
	}
	
	private NetworkNode getSourceNode() {
		return new NetworkNodeImpl(getSender(), getSenderPosition());
	}
	private NetworkNode getDestinationNode() {
		return new NetworkNodeImpl(getReceiver(), getReceiverPosition());
	}

	private NeighborDiscoveryData getOwnData(GeographicClusterRoutingAlgorithm algorithm) {
		NeighborDiscoveryService_sync neighborDiscoveryService = algorithm.getNeighborDiscoveryService();
		return neighborDiscoveryService.getNeighborDiscoveryData(neighborDiscoveryService.getOwnAddress());
	}

    /**
     * TODO Comment method
     * @param geographicClusterRoutingAlgorithm 
     * @param handler 
     */
    public void handleGreedy(GeographicClusterRoutingAlgorithm geographicClusterRoutingAlgorithm, RoutingTaskHandler handler) {
        if (getGreedyFailureAddress().equals(geographicClusterRoutingAlgorithm.getOwnAddress())&&localGreedyFailure){ 
            handler.dropMessage(this);
            return;
        }
        PositionUnicastRoutingAlgorithm_Sync recoveryStrategy = geographicClusterRoutingAlgorithm.getRecovery();
        handler.delegateMessage(recoveryStrategy
                        .getPositionBasedHeader(this),this);
        return;
        
    }
	
}
