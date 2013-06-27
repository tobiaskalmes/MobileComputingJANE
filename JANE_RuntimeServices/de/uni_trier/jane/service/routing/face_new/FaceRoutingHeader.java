package de.uni_trier.jane.service.routing.face_new;

import java.util.Vector;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationData;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.service.routing.face.NetworkNodeImpl;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.PlanarGraphExplorer;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.RoutingStep;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.StepSelector;

/**
 * An abstract face routing header to provide all informations needed by normal face routing.
 * Every implementation of this class represents a different state. Their are two known implementation of this class:
 * StartFaceRouting and ContinueFaceRouting.
 * @author Stefan Peters
 *
 */
public abstract class FaceRoutingHeader extends AbstractFaceRoutingHeader {

	private static final long serialVersionUID = 1688811451677399531L;

	/**
	 * The constructor
	 * @param sender The sender address
	 * @param senderPosition The sender position
	 * @param receiver The receiver address
	 * @param receiverPosition The receiver position
	 * @param countHops A flog to decide whether to count the hops or not
	 * @param traceRoute A flag to decide whether to trace the rout or not
	 * @param routingID The id of the routing service
	 */
	public FaceRoutingHeader(Address sender, Position senderPosition,
			Address receiver, Position receiverPosition, boolean countHops,
			boolean traceRoute, ServiceID routingID) {
		super(sender, senderPosition, receiver, receiverPosition, countHops,
				traceRoute, routingID);
	}

	/**
	 * The constructor
	 * @param header The former header of a different state
	 */
	public FaceRoutingHeader(FaceRoutingHeader header) {
		super(header);
	}

	/**
     * 
     * Constructor for class <code>FaceRoutingHeader</code>
     *
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param routingHeader
     * @param target
     * @param routingID
	 */
    public FaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, RoutingHeader routingHeader, Position target, ServiceID routingID) {
        super (greedyFailureNode,greedyFailurePosition,routingHeader, target,routingID);
    }

    /**
	 * This method is called, when face routing is started at source node
	 * @param faceRouting A wrapper to needed information provided by an implementation of the interface FaceRouting
	 * @return Returns chosen routing step
	 */
	protected RoutingStep handleStart(FaceRouting faceRouting) {
		PlanarGraphExplorer explorer = faceRouting.getPlanarGraphExplorer();
		setConditions(faceRouting);
		PlanarGraphNode current = faceRouting.getPlanarizerService()
				.getPlanarGraphNode();
		setSourceAddress(current.getAddress());
		setSourcePosition(current.getPosition());
		NetworkNode destination = new NetworkNodeImpl(destinationAddress,
				getReceiverPosition(), false);
		RoutingStep[] steps = explorer.startExploration(current, current
				.getAllOneHopNeighbors(), destination, getGreedyFailureNode(),
				startCondition, crossingCondition, turnCondition,
				finishCondition, breakCondition, resumeGreedyCondition);
		if (steps[steps.length - 1].isBreaked())
			return null;
        if (steps[steps.length - 1].isResumeGreedy()){
            return steps[steps.length - 1]; 
        }
		StepSelector selector = faceRouting.getNextNodeSelector();
		Address bestNeighbor=selector.getBestNeighbor(faceRouting.getNeighborDiscoveryService().getNeighborDiscoveryData(current.getAddress()),faceRouting.getNeighborDiscoveryService().getNeighborDiscoveryData(),destination);
		//int index=selector.getStepIndex(steps,destination,algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData());
		int index=getStepIndex(steps,destination,faceRouting.getNeighborDiscoveryService().getNeighborDiscoveryData(),bestNeighbor);
		setConditions(steps[index], steps[index-1].getNode());
		return steps[index];
	}

	
	
	
	/**
	 * This method is called to continue face routing untill destination is reached
	 * @param faceRouting A wrapper to needed information provided by an implementation of the interface FaceRouting
	 * @return Returns The chosen routing step
	 */
	protected RoutingStep handleContinue(FaceRouting faceRouting) {
        setConditions(faceRouting);
		PlanarGraphExplorer explorer=faceRouting.getPlanarGraphExplorer();
		PlanarGraphNode current=faceRouting.getPlanarizerService().getPlanarGraphNode();
		
		NetworkNode destination=new NetworkNodeImpl(destinationAddress,getReceiverPosition(),false);
		NetworkNode previousNode=new NetworkNodeImpl(getLastSenderAddress(),getLastSenderPosition(),true);
		NetworkNode sourceNode=new NetworkNodeImpl(getSender(),getSenderPosition());
		RoutingStep[] steps=explorer.continueExploration(current,current.getAllOneHopNeighbors(),previousNode,sourceNode,
				destination,getGreedyFailureNode(),getLastIntersection(),isClockwise(),
				startCondition,crossingCondition,turnCondition,finishCondition,breakCondition,resumeGreedyCondition);
		if(steps[steps.length-1].isBreaked())
			return null;
        if (steps[steps.length-1].isResumeGreedy()){
            return steps[steps.length-1];
        }
		StepSelector selector=faceRouting.getNextNodeSelector();
		Address bestNeighbor=selector.getBestNeighbor(faceRouting.getNeighborDiscoveryService().getNeighborDiscoveryData(current.getAddress()),faceRouting.getNeighborDiscoveryService().getNeighborDiscoveryData(),destination);
		//int index=selector.getStepIndex(steps,destination,algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData());
		int index=getStepIndex(steps,destination,faceRouting.getNeighborDiscoveryService().getNeighborDiscoveryData(),bestNeighbor);
		setConditions(steps[index],steps[index-1].getNode());
		return steps[index];
	}

	// Sets needed informations into the header so routing could continue 
	private void setConditions(RoutingStep step, PlanarGraphNode current) {
		super.startCondition = step.getStartCondition();
		super.finishCondition = step.getFinishCondition();
		super.breakCondition = step.getBreakCondition();
		super.turnCondition = step.getTurnCondition();
		super.resumeGreedyCondition = step.getResumeGreedyCondition();
		super.crossingCondition = step.getCrossingCondition();
		setLastSenderAddress(current.getAddress());
		setLastSenderPosition(current.getPosition());
		setLastIntersection(step.getLastIntersection());
		setClockwise(step.isClockwise());
	}

	// Sets needed informations into the header so routing could continue 
	private void setConditions(FaceRouting algorithm) {
		startCondition = algorithm.createStartCondition();
		finishCondition = algorithm.createFinishCondition(destinationAddress);
		breakCondition = algorithm.createBreakCondition();
		turnCondition = algorithm.createTurnCondition();
		resumeGreedyCondition = algorithm.createResumeGreedyCondition();
		crossingCondition = algorithm.createCrossingCondition();

	}
	
	
	/**
	 * 
	 * @param algorithm A wrapper to needed information provided by an implementation of the interface FaceRouting
	 * @return Returns all one hop neighbors of current node
	 */
	protected NetworkNode[] getOneHopNeighbors(FaceRouting algorithm) {
		NeighborDiscoveryService_sync neighborDiscoveryService = algorithm
				.getNeighborDiscoveryService();
		NeighborDiscoveryData[] neighborDiscoveryData = neighborDiscoveryService
				.getNeighborDiscoveryData(); 
		Vector vector = new Vector();
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			if (neighborDiscoveryData[i].getHopDistance() == 1)
				vector.add(new NetworkNodeImpl(
						(Address) neighborDiscoveryData[i].getSender(),
						((LocationData) neighborDiscoveryData[i].getDataMap()
								.getData(LocationData.DATA_ID)).getPosition(),
						neighborDiscoveryData[i].getHopDistance() == 1));
		}
		return (NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
	}
	
	protected boolean deliverMessage(FaceRouting algorithm) {
		return algorithm.getNetworkNode().getAddress().equals(destinationAddress);
	}

}
