package de.uni_trier.jane.service.routing.face_new;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

/**
 * This abstract class is the base for all dominating set face routing headers. All this headers
 * extends this class. This class contains four protected methods to decide next routing step.
 * @author Stefan Peters
 *
 */
public abstract class DominatingSetFaceRoutingHeader extends
		AbstractFaceRoutingHeader {
	
	
	/**
	 * Constructor
	 * @param header The former header of a different state
	 */
	public DominatingSetFaceRoutingHeader(DominatingSetFaceRoutingHeader header) {
		super(header);
	}
	
	/**
	 * Constructor
	 * @param sender The sender address
	 * @param senderPosition The sender position
	 * @param receiver The receiver address
	 * @param receiverPosition The receiver position
	 * @param countHops A flag whether to count the hops or not
	 * @param traceRoute A flag whether to trace the route or not
	 * @param routingID The id of the routing service
	 */
	public DominatingSetFaceRoutingHeader(Address sender, Position senderPosition, Address receiver, Position receiverPosition, boolean countHops, boolean traceRoute, ServiceID routingID){
		super(sender,senderPosition,receiver,receiverPosition,countHops,traceRoute,routingID);

	}
	
	/**
     * Constructor for class <code>DominatingSetFaceRoutingHeader</code>
     * @param greedyFailureNode
     * @param greedyFailurePosition
     * @param greedyHeader
     * @param routingID
     */
    public DominatingSetFaceRoutingHeader(Address greedyFailureNode, Position greedyFailurePosition, PositionbasedRoutingHeader greedyHeader, ServiceID routingID) {
        super(greedyFailureNode,greedyFailurePosition,greedyHeader,routingID);
    }

    /**
	 * This method checks whether a node is in the dominating set or not
	 * @param current The current node
	 * @param neighborDiscoveryData The neighbor discovery data provided by the neighbor discovery service
	 * @return Returns true, if current node is in dominating set, false if not. 
	 */
	protected boolean isInDominatingSet(NetworkNode current, NeighborDiscoveryService_sync neighborDiscoveryService) {
		NeighborDiscoveryData data = neighborDiscoveryService.getNeighborDiscoveryData(current.getAddress());
		DominatingSetData dominatingSetData = DominatingSetData.fromNeighborDiscoveryData(data);
		if(dominatingSetData != null) {
			return dominatingSetData.isMember();
		}
		return false;
	}
	
	/**
	 * This method checks whether the destination is a one hop neighbor or not.
	 * @param faceRouting The face routing wrapper to gain access to all needed data
	 * @return Returns true, if destination is a one hop neighbor, false if not.
	 */
	protected boolean destinationIsOneHopNeighbor(FaceRouting faceRouting) {
		NeighborDiscoveryService_sync neighborDiscoveryService = faceRouting.getNeighborDiscoveryService();
		NeighborDiscoveryData destinationData = neighborDiscoveryService.getNeighborDiscoveryData(destinationAddress);
		return destinationData!=null && destinationData.getHopDistance()==1;
	}
	
	/**
	 * This method is called by <code>DominatingSetContinueRoutingHeader</code> it continues
	 * face routing, only if the current node is currently in dominating set, otherwise the method
	 * handleEnter is called.
	 * @param algorithm gain access to all needed data provided by FaceRouting
	 * @return Returns the next routing step
	 */
	protected RoutingStep handleNext(FaceRouting algorithm) {
		PlanarGraphExplorer explorer=algorithm.getPlanarGraphExplorer();
		PlanarGraphNode current=algorithm.getPlanarizerService().getPlanarGraphNode();
		
		NetworkNode destination=new NetworkNodeImpl(destinationAddress,getReceiverPosition(),false);
		NetworkNode previousNode=new NetworkNodeImpl(getLastSenderAddress(),getLastSenderPosition(),true);
		NetworkNode sourceNode=new NetworkNodeImpl(getSender(),getSenderPosition());
		RoutingStep[] steps=explorer.continueExploration(current,current.getAllOneHopNeighbors(),previousNode,sourceNode,
				destination,getGreedyFailureNode(),getLastIntersection(),isClockwise(),
				startCondition,crossingCondition,turnCondition,finishCondition,breakCondition,resumeGreedyCondition);
		if(steps[steps.length-1].isBreaked())
			return null;
		StepSelector selector=algorithm.getNextNodeSelector();
		Address bestNeighbor=selector.getBestNeighbor(algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData(current.getAddress()),algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData(),destination);
		//int index=selector.getStepIndex(steps,destination,algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData());
		int index=getStepIndex(steps,destination,algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData(),bestNeighbor);
		if(index == 0) {
			return null;
		}
		setConditions(steps[index],steps[index-1].getNode());
		return steps[index];
	}
	
	/**
	 * This method is called everytime the current node is not in dominating set, this could happen
	 * by start time or during movement of the node. Its behavior can be described in the implementation
	 * of the interface EnterHandler.
	 * @param algorithm gain access to all needed data provided by FaceRouting
	 * @return Returns the next routing step
	 */
	protected RoutingStep handleEnter(FaceRouting algorithm) {
		// Select a dominatingSet node to route to
		//PlanarGraphNode current=algorithm.getPlanarizerService().getPlanarGraphNode();
		NetworkNode previous=new NetworkNodeImpl(getLastSenderAddress(),getLastSenderPosition(),true);
		NeighborDiscoveryData[] datas=algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData();
		NetworkNode destinationNode = new NetworkNodeImpl(getReceiver(), getReceiverPosition());
		NetworkNode next=algorithm.getEnterHandler().handleEnter(previous,algorithm.getNetworkNode(),datas, destinationNode);
		if(next==null)
			return null;
		DummyPlanarGraphNode node=new DummyPlanarGraphNode(next);
		RoutingStep step= new RoutingStep(node,getLastIntersection(),
				isClockwise(),false,false,false,finishCondition,resumeGreedyCondition,startCondition,crossingCondition,turnCondition,breakCondition);
		return step;
	}
	
	/**
	 * This method is called if destination node is currently a neighbor of the current node.
	 * Its behavior is described in the implementation of the interface EndHandler
	 * @param algorithm gain access to all needed data provided by FaceRouting
	 * @return Returns the next routing step
	 */
	protected RoutingStep handleEnd(FaceRouting algorithm) {
		//PlanarGraphNode current=algorithm.getPlanarizerService().getPlanarGraphNode();
		NeighborDiscoveryData[] datas=algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData();
		NetworkNode destination=new NetworkNodeImpl(getReceiver(),getReceiverPosition());
		NetworkNode previous=new NetworkNodeImpl(getLastSenderAddress(),getLastSenderPosition(),true);
		NetworkNode actuell=algorithm.getNetworkNode();
		NetworkNode next=algorithm.getEndHandler().handleEnd(previous,actuell,destination,datas);
		if(next==null)
			return null;
		DummyPlanarGraphNode node=new DummyPlanarGraphNode(next);
		RoutingStep step= new RoutingStep(node,getLastIntersection(),
				isClockwise(),false,false,false,finishCondition,resumeGreedyCondition,startCondition,crossingCondition,turnCondition,breakCondition);
		return step;
	}
	
	/**
	 * This method is called if FaceRouting is started in current node. This could happen if
	 * the start node is in dominating set, or the <code>DominatingSetEnterRoutingHeader</code>
	 * has found a node in the dominating set.
	 * @param algorithm gain access to all needed data provided by FaceRouting
	 * @return Returns the next routing step
	 */
	protected RoutingStep handleStart(FaceRouting algorithm) {
		PlanarGraphExplorer explorer=algorithm.getPlanarGraphExplorer();
		setConditions(algorithm);
		PlanarGraphNode current=algorithm.getPlanarizerService().getPlanarGraphNode();
		setSourceAddress(current.getAddress());
		setSourcePosition(current.getPosition());
		NetworkNode destination=new NetworkNodeImpl(destinationAddress,getReceiverPosition(),false);
		RoutingStep[] steps=explorer.startExploration(current,current.getAllOneHopNeighbors(),destination,getGreedyFailureNode(),
				startCondition,crossingCondition,turnCondition,finishCondition,breakCondition,resumeGreedyCondition);
		if(steps[steps.length-1].isBreaked())
			return null;
		StepSelector selector=algorithm.getNextNodeSelector();
		Address bestNeighbor=selector.getBestNeighbor(algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData(current.getAddress()),algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData(),destination);
		//int index=selector.getStepIndex(steps,destination,algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData());
		int index=getStepIndex(steps,destination,algorithm.getNeighborDiscoveryService().getNeighborDiscoveryData(),bestNeighbor);
		if(index == 0) {
			return null;
		}
		setConditions(steps[index],steps[index-1].getNode());
		return steps[index];
	}

	// stores the conditions of the current RoutingStep to the header
	private void setConditions(RoutingStep step,PlanarGraphNode current) {
		super.startCondition=step.getStartCondition();
		super.finishCondition=step.getFinishCondition();
		super.breakCondition=step.getBreakCondition();
		super.turnCondition=step.getTurnCondition();
		super.resumeGreedyCondition=step.getResumeGreedyCondition();
		super.crossingCondition=step.getCrossingCondition();
		setLastSenderAddress(current.getAddress());
		setLastSenderPosition(current.getPosition());
		setLastIntersection(step.getLastIntersection());
		setClockwise(step.isClockwise());
	}

	
	private void setConditions(FaceRouting algorithm) {
		startCondition=algorithm.createStartCondition();
		finishCondition=algorithm.createFinishCondition(destinationAddress);
		breakCondition=algorithm.createBreakCondition();
		turnCondition=algorithm.createTurnCondition();
		resumeGreedyCondition=algorithm.createResumeGreedyCondition();
		crossingCondition=algorithm.createCrossingCondition();
		
	}
	/**
	 * 
	 * @param algorithm gain access to all needed data provided by FaceRouting
	 * @return Returns all one hop neighbors of the current node
	 */
	protected NetworkNode[] getOneHopNeighbors(FaceRouting algorithm) {
		NeighborDiscoveryService_sync neighborDiscoveryService=algorithm.getNeighborDiscoveryService();
		NeighborDiscoveryData[] neighborDiscoveryData = neighborDiscoveryService.getNeighborDiscoveryData(); // TODO: Filter verwenden, welcher nur Knoten zulässt, die eine Positionsinformation besitzen?
		Vector vector=new Vector();
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			if(neighborDiscoveryData[i].getHopDistance()==1)
				vector.add(new NetworkNodeImpl((Address) neighborDiscoveryData[i]
				    .getSender(), ((LocationData) neighborDiscoveryData[i]
					.getDataMap().getData(LocationData.DATA_ID)).getPosition(),
					neighborDiscoveryData[i].getHopDistance() == 1));
		}
		return (NetworkNode[]) vector.toArray(new NetworkNode[vector.size()]);
	}
	
	/**
	 * This method is used to continue routing. It changes the header to current state
	 * and transmit the message.
	 * @param handler The RoutingTaskHandler to transfer the message
	 * @param faceRouting gain access to all needed data provided by FaceRouting
	 * @param current The current node
	 */
	protected void selectNextHopAction(RoutingTaskHandler handler, FaceRouting faceRouting, PlanarGraphNode current) {
		DominatingSetFaceRoutingHeader header;
		RoutingStep step;
		NetworkNode[] neighbors=getOneHopNeighbors(faceRouting);
		if(destinationIsOneHopNeighbor(faceRouting)) {
			step=handleEnd(faceRouting);
			header=new DominatingSetEndRoutingHeader(this);
		}else if(current!=null) { // Node is in Dominating Set	
			step=handleStart(faceRouting);
			header= new DominatingSetContinueRoutingHeader(this);
		}else { // node is not in dominating set
			step=handleEnter(faceRouting);
			header=new DominatingSetEnterRoutingHeader(this);
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
