/* 29.09.04 erste Realisierung der Anwendung zum Testen des PlanarGraphExplorers
 * 03.10.04 Frage: Wie erfährt ein Dummy User den Startpunkt und den Zielpunkt?
 * ...
 * 19.10.04 sollte bald fertig sein 
 * So ganz blick ich hier noch nicht durch.
 * 21.10.04 bei crossing, muss ich da clockwise und counterclockwise vertauschen
 * 
 * 1. Diagramm malen von dem Beispiel Greedy Routing
 * 2. danach überlegen, da die gewünschten Daten vom Startpunkt und Zielpunkt im Header sind
 * 3. Wo setze ich die ganzen Conditions? In der Klasse mit der Mainmethode und übergib sie an diese Klasse oder
 * übergebe ich dieser Klasse ein objekt vom Typ PlanarGraphExplorer
 * 
 * 
 * Erfolge:
 * 
 * 19.10.04 Startpaket senden funktioniert
 * 
 * 
 * Test:
 * 
 * 20.10.04 OneHopPlanarizerListener funktioniert nicht!!!!!!!!!!!!!!!!!
 * 20.10.04 OneHopPlanarizerListener funktioniert, dafür läuft jetzt das packet im kreis
 * 20.10.04 clockwise funktioniert nicht, hab ich wohl vertauscht
 * 20.01.05 nach Umzug auf neue Plattform sind Position gleich null
 * 
 */

package de.uni_trier.jane.service.routing.face;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.dominating_set.*;
import de.uni_trier.jane.service.energy.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a face routing startegy based on position information
 * of all one-hop neighbors or knowledge of multi hop neighbors
 */
public class AnycastFaceRouting implements RuntimeService,
		RoutingAlgorithm,
		LocationRoutingAlgorithm_Sync,
		PositioningListener{

	public static final ServiceID SERVICE_ID = new EndpointClassID(
			AnycastFaceRouting.class.getName());


	/**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param conditions
     * @param startConditionFactory
     * @param crossingConditionFactory
     * @param udgRadius
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,
			Conditions conditions,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory) {
		return createInstance(serviceUnit, startConditionFactory, crossingConditionFactory, resumeGreedyConditionFactory, conditions,null,false);
	}
	

	/**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param conditions
     * @param startConditionFactory
     * @param crossingConditionFactory
     * @param udgRadius
     * @param resumeGreedyServiceID
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,
			Conditions conditions,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			ServiceID resumeGreedyServiceID) {
		return createInstance(serviceUnit, startConditionFactory, crossingConditionFactory, resumeGreedyConditionFactory, conditions, resumeGreedyServiceID, false);
	}

	/**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param startConditionFactory
     * @param crossingConditionFactory
     * @param conditions
     * @param udgRadius
     * @param resumeGreedyServiceID
     * @param debug
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			Conditions conditions,
			ServiceID resumeGreedyServiceID, boolean debug) {
		//		LinkLayer linkLayerService = (LinkLayer) serviceUnit
		//		.getService(LinkLayer.class);
		ServiceID positioningService = serviceUnit
				.getService(PositioningService.class);
		if (!serviceUnit.hasService(RoutingService.class)) {
			DefaultRoutingService.createInstance(serviceUnit);
		}
		//ServiceID routingService = serviceUnit.getService(RoutingService.class);
		if (!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
			OneHopNeighborDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID neighborDiscoveryService = serviceUnit
				.getService(NeighborDiscoveryService_sync.class);

		if (!serviceUnit.hasService(LocationDataDisseminationService.class)) {
			LocationDataDisseminationService.createInstance(serviceUnit);
		}
		//ServiceID greedyID = resumeGreedyHeaderFactory;
		ServiceID udgDominatingSetID = null;
		if (serviceUnit.hasService(UDGDominatingSetService.class)) {
			udgDominatingSetID = serviceUnit
					.getService(UDGDominatingSetService.class);
		}
		if(!serviceUnit.hasService(PlanarizerService.class)){
			GabrielGraphPlanarizerService.createInstance(serviceUnit,false);
		}
		
		if (serviceUnit.hasService(UDGDominatingSetService.class)) {
			udgDominatingSetID = serviceUnit
					.getService(UDGDominatingSetService.class);
		}
		ServiceID energyServiceID=null;
		if(serviceUnit.hasService(EnergyStatusProviderService.class)){
			energyServiceID = serviceUnit.getService(EnergyStatusProviderService.class);
		}
		ServiceID planarizerServiceID=serviceUnit.getService(PlanarizerService.class);
//		StartCondition startCondition = conditions.getStartCondition();
		TurnCondition turnCondition = conditions.getTurnCondition();
		BreakCondition breakCondition = conditions.getBreakCondition();
//		CrossingCondition crossingCondition = conditions.getCrossingCondition();
//		ResumeGreedyConditionFactory resumeGreedyConditionFactory = conditions.getResumeGreedyCondition();
		StepSelector weightCondition = conditions.getWeightCondition();
		DominatingSetStartCondition dominatingSetStartCondition = conditions.getDominatingSetStartCondition();
		Service faceRouting = new AnycastFaceRouting(
				startConditionFactory,
				turnCondition, breakCondition, crossingConditionFactory,
				resumeGreedyConditionFactory, weightCondition,
				dominatingSetStartCondition,
                positioningService,
				neighborDiscoveryService, 
				resumeGreedyServiceID, debug, udgDominatingSetID,energyServiceID,planarizerServiceID);
		return serviceUnit.addService(faceRouting);

	}

	private StartConditionFactory startConditionFactory;
	private CrossingConditionFactory crossingConditionFactory;
	private ResumeGreedyConditionFactory resumeGreedyConditionFactory;

	private Address address;


	// Initialized in Constructor
	private Map destinationPendingMessageListMap;



	private ServiceID greedyID;
	

    private ServiceID positioningServiceID;

	private NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;



	private ServiceID neighborDiscoveryServiceID;

	//initilized on startup
	private RuntimeOperatingSystem operatingSystem;



	private boolean debug;
	
	private ServiceID planarizerServiceID;

	private ServiceID udgDominatingSetID;

	private StepSelector weightCondition;

	//private DominatingSetStartCondition dominatingSetStartCondition;
	
	private ServiceID energyStatusProviderServiceID;

	private PlanarizerServiceStub planarServiceStub;

    private LocationRoutingAlgorithm_Sync greedyAlgorith;

	private EnergyStatusProviderServiceStub energyStatusProviderServiceStub;
    private PositioningData info;
    private PositioningData positionInfo;

	/**
	 * 
	 * Constructor for class <code>FaceRouting</code>
	 *
	 * @param startCondition
	 * @param finishConditon
	 * @param turnCondition
	 * @param breakCondition
	 * @param crossingCondition
	 * @param resumeGreedyCondition
	 * @param weightCondition
	 * @param dominatingSetStartCondition
	 * @param udgRadius
	 * @param address
	 * @param positioningServiceID
	 * @param routingServiceID
	 * @param neighborDiscoveryServiceID
	 * @param recoveryHeaderFactory
	 * @param debug
	 * @param udgDominatingSetID
	 * @param energyServiceID
	 * @param planarizerServiceID
	 */
	public AnycastFaceRouting(
			StartConditionFactory startConditionFactory, 
			TurnCondition turnCondition,
			BreakCondition breakCondition, 
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			StepSelector weightCondition,
			DominatingSetStartCondition dominatingSetStartCondition,
			ServiceID positioningServiceID, 
			ServiceID neighborDiscoveryServiceID, 
			ServiceID resumeGreedyServiceID,
			boolean debug, 
			ServiceID udgDominatingSetID,
			ServiceID energyServiceID,
			ServiceID planarizerServiceID) {
		this.startConditionFactory = startConditionFactory;
//		this.breakCondition = breakCondition;
//		this.turnCondition = turnCondition;
		this.crossingConditionFactory = crossingConditionFactory;
		this.resumeGreedyConditionFactory = resumeGreedyConditionFactory;
		this.positioningServiceID = positioningServiceID;
		//this.routingServiceID = routingServiceID;
		this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
		this.greedyID=resumeGreedyServiceID;
		
		this.debug = debug;
		this.udgDominatingSetID = udgDominatingSetID;
		this.weightCondition = weightCondition;
		//this.dominatingSetStartCondition = dominatingSetStartCondition;
		this.energyStatusProviderServiceID=energyServiceID;
		this.planarizerServiceID=planarizerServiceID;
		destinationPendingMessageListMap = new HashMap();
	}

	
    
	private void continueGreedy(RoutingTaskHandler replyHandle,
			AnycastFaceRoutingHeader header) {

       replyHandle.delegateMessage(greedyAlgorith.
       		getLocationRoutingHeader(header),header);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.Service#finish()
	 */
	public void finish() {
		// ignore

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.ubi.appsim.service.Service#getGlobalShape()
	 */
	/*
	 * public Shape getGlobalShape() { return globalShape; }
	 */

	/**
	 * @param neighborData
	 * @return
	 */
	private NetworkNode[] getNetworkNodes(
			NeighborDiscoveryData[] neighborDiscoveryData) {
		NetworkNode[] nodes = new NetworkNode[neighborDiscoveryData.length];

		int j = 0;

		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			nodes[j] = new NetworkNodeImpl(
					neighborDiscoveryData[i].getSender(),
					((LocationData) neighborDiscoveryData[i].getDataMap()
							.getData(LocationData.DATA_ID)).getPosition(),
					neighborDiscoveryData[i].getHopDistance() == 1);
			j++;
		}
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.Service#getServiceID()
	 */
	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.Service#getShape()
	 */
	public Shape getShape() {

		return null;
/*		
		Shape number;
		if (visited) {
			number = new TextShape(address.toString(), new Rectangle(
					new Extent(15, 15)), Color.RED);
		} else {
			number = new TextShape(address.toString(), new Rectangle(
					new Extent(5, 5)), Color.BLUE);
		}
		return number;
*/
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageReceivedRequest(de.uni_trier.jane.signaling.ReplyHandle,
	 *      de.uni_trier.jane.service.routing.RoutingHeader,
	 *      de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress)
	 */
	public void handleMessageReceivedRequest(RoutingTaskHandler replyHandle,
			RoutingHeader header, Address sender) {
		//		 empfängt ein Knoten auch die eigene Sendung?
		if (sender.equals(address)) {
			return;
		}

//		visited = true;
		AnycastFaceRoutingHeader faceRoutingHeader = (AnycastFaceRoutingHeader) header;
		if (((GeographicTarget)faceRoutingHeader.getTargetLocation()).isInside(info.getPosition())) {
			//angekommen
			if (header.hasDelegationData()){
				DelegationRoutingAlgorithm_Sync other=(DelegationRoutingAlgorithm_Sync)
					operatingSystem.getAccessListenerStub(header.getDelegationData().getDelegationServiceID(),DelegationRoutingAlgorithm_Sync.class);
				replyHandle.delegateMessage(other.getDelegationRoutingHeader(faceRoutingHeader),faceRoutingHeader);
			}else{
				replyHandle.deliverMessage(header);
			}

			return;
		}
		if (faceRoutingHeader.isStartRouting()) {
			// the packet was send from a node not in the dominating set, so the
			// routing starts in this node;
			Position sourcePosition = ((LocationData) neighborDiscoveryServiceStub
					.getNeighborDiscoveryData(address).getDataMap().getData(
							LocationData.DATA_ID)).getPosition();
			NetworkNode greedyFailureNode=faceRoutingHeader.getGreedyFailureNode();
			faceRoutingHeader.setLastSenderAddress(address);
			faceRoutingHeader.setLastSenderPosition(sourcePosition);
			faceRoutingHeader.setGreedyFailureNode(greedyFailureNode);
			handleNextHop(replyHandle, faceRoutingHeader);
			return;
		}

		if (!neighborDiscoveryServiceStub.hasNeighborDiscoveryData(sender)) {
			//The node who had sent this message is not in my known
			// neighborhood
			if (debug) {
				operatingSystem.write(address
						+ " : Using position information given by the header");
			}
			handleNextHop(replyHandle, faceRoutingHeader, sender,
					faceRoutingHeader.getLastSenderPosition());
			return;
		}
		LocationData senderLocation = (LocationData) neighborDiscoveryServiceStub
				.getNeighborDiscoveryData(sender).getDataMap().getData(
						LocationData.DATA_ID);
		Position lastPosition = senderLocation.getPosition();
		//this.sender = sender;
		sender = ((AnycastFaceRoutingHeader) header).getLastSenderAddress();
		lastPosition = ((AnycastFaceRoutingHeader) header).getLastSenderPosition();
		handleNextHop(replyHandle, faceRoutingHeader, sender, lastPosition);

	}

	/**
	 * This method is used to start facerouting
	 * 
	 * @param replyHandle
	 *            The message id
	 * @param header
	 *            The message header
	 */
	private void handleNextHop(RoutingTaskHandler replyHandle, AnycastFaceRoutingHeader header) {
		handleNextHop(replyHandle,header,null,null);
	}

	/**
	 * @return
	 */
	private NetworkNode[] getAllDominatingSetOneHopNeighbors(
			NeighborDiscoveryData[] neighborDiscoveryData) {
		NetworkNode[] neighbors = null;
		Vector v = new Vector();
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			//if (!neighborDiscoveryData[i].getSender().equals(address)) {
			if (neighborDiscoveryData[i].getDataMap().hasData(
					DominatingSetData.DATA_ID)) {
				if (((DominatingSetData) neighborDiscoveryData[i].getDataMap()
						.getData(DominatingSetData.DATA_ID)).isMember()) {
					if (neighborDiscoveryData[i].getHopDistance() == 1) {
						NetworkNode node = new NetworkNodeImpl(
								 neighborDiscoveryData[i]
										.getSender(),
								((LocationData) neighborDiscoveryData[i]
										.getDataMap().getData(
												LocationData.DATA_ID))
										.getPosition(),
								neighborDiscoveryData[i].getHopDistance() == 1);
						v.add(node);
					}
				}
			}

			//}
		}
		if (v.size() != 0) {
			neighbors = new NetworkNode[v.size()];
			for (int i = 0; i < v.size(); i++) {
				neighbors[i] = (NetworkNode) v.get(i);
			}
		}
		return neighbors;
	}

	/**
	 * @param neighborDiscoveryData
	 * @return
	 */
	private NetworkNode[] getDominatingSetNeighbors(
			NeighborDiscoveryData[] neighborDiscoveryData) {
		NetworkNode[] neighbors = null;
		Vector v = new Vector();
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			//if (!neighborDiscoveryData[i].getSender().equals(address)) {
			if (neighborDiscoveryData[i].getDataMap().hasData(
					DominatingSetData.DATA_ID)) {
				if (((DominatingSetData) neighborDiscoveryData[i].getDataMap()
						.getData(DominatingSetData.DATA_ID)).isMember()) {
					NetworkNode node = new NetworkNodeImpl(
							 neighborDiscoveryData[i]
									.getSender(),
							((LocationData) neighborDiscoveryData[i]
									.getDataMap().getData(LocationData.DATA_ID))
									.getPosition(), neighborDiscoveryData[i]
									.getHopDistance() == 1);
					v.add(node);
				}
				//}

			}
		}
		if (v.size() != 0) {
			neighbors = new NetworkNode[v.size()];
			for (int i = 0; i < v.size(); i++) {
				neighbors[i] = (NetworkNode) v.get(i);
			}
		}
		return neighbors;
	}

	/**
	 * @param current
	 * @param neighborDiscoveryData
	 * @return
	 */
	private boolean isInDominatingSet(NetworkNode current,
			NeighborDiscoveryData[] neighborDiscoveryData) {
		//boolean isIn=false;
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			if (neighborDiscoveryData[i].getSender().toString().equals(
					current.getAddress().toString())) {
				if (neighborDiscoveryData[i].getDataMap().hasData(
						DominatingSetData.DATA_ID))
					return ((DominatingSetData) neighborDiscoveryData[i]
							.getDataMap().getData(DominatingSetData.DATA_ID))
							.isMember();
				else
					return false;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.ubi.appsim.service.neighbor_discovery.NeighborDiscoveryListener#handleNeighborDiscoveryInfo(de.uni_trier.ubi.appsim.service.neighbor_discovery.NeighborDiscoveryInfo)
	 */
	/*
	 * public void handleNeighborDiscoveryInfo(NeighborDiscoveryInfo info) {
	 * neighborDiscoveryInfo = info; }
	 */

	/**
	 * This method is used to continue Facerouting
	 * 
	 * @param replyHandle
	 *            The messageID
	 * @param header
	 *            The routing header
	 * @param lastPosition
	 *            The position of the sender
	 * @param sender2
	 *            The senders address
	 */
	private void handleNextHop(RoutingTaskHandler replyHandle,
			AnycastFaceRoutingHeader header, Address sender,
			Position lastPosition) {
		LocationData ownLocationData = (LocationData) neighborDiscoveryServiceStub
				.getNeighborDiscoveryData(address).getDataMap().getData(
						LocationData.DATA_ID); //locationSystemInfo.getPosition();
		Position ownPosition = ownLocationData.getPosition();
        
        
//        if (finishCondition.checkCondition(current,null/*?*/)){
//            
//        }
		NetworkNode destination = new NetworkNodeImpl(new SimulationLinkLayerAddress(-1),
			 header.getDestinationPosition());
		NetworkNode source = header.getGreedyFailureNode();
//            new NetworkNodeImpl(header.getLastSenderAddress(),
//				header.getLastSenderPosition());
		NetworkNode current = new NetworkNodeImpl(address, ownPosition);
		NetworkNode[] neighbors = null;
		PlanarGraphNode node = null;
		if (udgDominatingSetID != null) {
			// SUing Dominating set information
			neighbors = getNetworkNodes(neighborDiscoveryServiceStub
					.getNeighborDiscoveryData());
			if (debug) {
				operatingSystem.write(writeNeighbors(neighbors));
			}
			neighbors = getDominatingSetNeighbors(neighborDiscoveryServiceStub
					.getNeighborDiscoveryData());
			if (debug) {
				operatingSystem.write("DominatingSet: "
						+ writeNeighbors(neighbors));
			}
//			neighbors = Planarizer.stdPlanarizer(current, neighbors);
			if (debug) {
				operatingSystem.write(writeNeighbors(neighbors));
			}
			node= (OneHopDominatingSetPlanarGraphNode) planarServiceStub.getPlanarGraphNode();
		} else {
			// Using all neighbor information
			neighbors = getNetworkNodes(neighborDiscoveryServiceStub
					.getNeighborDiscoveryData());
			if (debug) {
				operatingSystem.write(writeNeighbors(neighbors));
			}
//			neighbors = Planarizer.stdPlanarizer(current, neighbors);
			if (debug) {
				operatingSystem.write(writeNeighbors(neighbors));
			}
			node=planarServiceStub.getPlanarGraphNode();
		}
		Position lastIntersection = header.getLastIntersection();
		FinishCondition finishCondition=header.getFinishCondition();
		BreakCondition breakCondition = header.getBreakCondition();
		StartCondition startCondition = header.getStartCondition();
		CrossingCondition crossingCondition = header.getCrossingCondition();
		TurnCondition turnCondition = header.getTurnCondition();
		ResumeGreedyCondition resumeGreedyCondition = header.getResumeGreedyCondition();
		PlanarGraphExplorer explorer = new PlanarGraphExplorer();

		RoutingStep[] steps=null;
		
		//TODO ersetzen
		PlanarGraphNode previousNode;
		if(sender!=null){
			//previousNode = planarServiceStub.getPlanarGraphNode(new NetworkNodeImpl(sender, lastPosition));
			previousNode=node.getAdjacentNode(sender);
            if (previousNode==null){
                //TODO:
                replyHandle.dropMessage(header);
                return;
            }
			steps = explorer.continueExploration(node,
					getNetworkNodes(neighborDiscoveryServiceStub
							.getNeighborDiscoveryData()), previousNode, source,
					destination,header.getGreedyFailureNode(), lastIntersection, header.isClockwise(), startCondition,
					crossingCondition, turnCondition, finishCondition,
					breakCondition, resumeGreedyCondition);
		}else{
			steps = explorer.startExploration(node,
					getNetworkNodes(neighborDiscoveryServiceStub
							.getNeighborDiscoveryData()), destination,source, startCondition,
							crossingCondition, turnCondition, finishCondition,
							breakCondition, resumeGreedyCondition);
		}
		
		if (debug) {
			operatingSystem.write(writeRoutingSteps(steps));
		}
		int step = weightCondition.getStepIndex(steps,destination,neighborDiscoveryServiceStub
				.getNeighborDiscoveryData());
		RoutingStep next = steps[step];
		//		start greedy routing
		if (steps[0].isResumeGreedy() && greedyID != null && step==0) {
			continueGreedy(replyHandle, header);
			return;
		}
		
		// message is dropped
		if(next.isBreaked() || step == 0) {
			replyHandle.dropMessage(header);
			return;
		}

		// TODO : wozu ist der folgende jetzt auskommentierte Code gut???
//		if (finishCondition.checkCondition(node, node
//				.getAllOneHopNeighbors())) {
//			NetworkNode senderNode = steps[step].getNode();
//			header.setLastIntersection(next.getLastIntersection());
//			header.setLastSenderAddress(senderNode.getAddress());
//			header.setLastSenderPosition(senderNode.getPosition());
//			header.setClockwise(next.isClockwise());
//			if (debug) {
//				operatingSystem.write(header.toString());
//			}
//			replyHandle.forwardAsUnicast(header, (LinkLayerAddress) destination.getAddress());
//			return;
//		}
//		if (debug) {
//
//			operatingSystem.write("Ausgewaehlter Knoten");
//			operatingSystem.write(next.getNode().getAddress().toString());
//		}
		NetworkNode senderNode = steps[step - 1].getNode();
		header.setLastIntersection(next.getLastIntersection());
		header.setLastSenderAddress(senderNode.getAddress());
		header.setLastSenderPosition(senderNode.getPosition());
		header.setClockwise(next.isClockwise());
		
		header.setStartCondition(next.getStartCondition());
		header.setCrossingCondition(next.getCrossingCondition());
		header.setTurnCondition(next.getTurnCondition());
		header.setFinishCondition(next.getFinishCondition());
		header.setBreakCondition(next.getBreakCondition());
		header.setResumeGreedyCondition(next.getResumeGreedyCondition());
		
		if (debug) {
			operatingSystem.write(header.toString());
		}
		replyHandle.forwardAsUnicast(header,  next.getNode().getAddress());
	}


    public void handleStartRoutingRequest(RoutingTaskHandler handle,
            RoutingHeader routingHeader) {
        
        AnycastFaceRoutingHeader faceRoutingHeader=(AnycastFaceRoutingHeader)routingHeader;
        

        handleNextHop(handle,faceRoutingHeader);
            
            
        

    }



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleUnicastErrorRequest(de.uni_trier.jane.signaling.ReplyHandle,
	 *      de.uni_trier.jane.service.routing.RoutingHeader,
	 *      de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress)
	 */
	public void handleUnicastErrorRequest(RoutingTaskHandler replyHandle,
			RoutingHeader header, Address receiver) {
	    
	    replyHandle.dropMessage(header);

	}
	//
    public void handleMessageForwardProcessed(RoutingHeader header) {
    	//ignore
    }




	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		operatingSystem = runtimeOperatingSystem;
		operatingSystem.registerSignalListener(RoutingAlgorithm.class);
		operatingSystem.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
		
		operatingSystem.registerAtService(positioningServiceID,
				PositioningService.class);
		//		operatingSystem.registerAtService(neighborDiscoveryServiceID,
		//				NeighborDiscoveryService.class);

		neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(
				runtimeOperatingSystem, neighborDiscoveryServiceID);
		address=neighborDiscoveryServiceStub.getOwnAddress();
		planarServiceStub= new PlanarizerServiceStub(operatingSystem,planarizerServiceID);
		planarServiceStub.register();
		if(energyStatusProviderServiceID!=null){
			energyStatusProviderServiceStub=new EnergyStatusProviderServiceStub(runtimeOperatingSystem, energyStatusProviderServiceID);
		}
        if (greedyID!=null){
            operatingSystem.setTimeout(new ServiceTimeout(0) {
                public void handle() {
                    greedyAlgorith=(LocationRoutingAlgorithm_Sync)operatingSystem.
    				    getAccessListenerStub(greedyID,LocationRoutingAlgorithm_Sync.class);
                }
            });
        }
		
		//operatingSystem.registerAtService(routingServiceID,RoutingService.class);
		//operatingSystem.registerAtService(greedyID,GreedyRoutingAlgorithm.class);
	}


	
	//
    public void handleMessageDelegateRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        if (debug) {
			System.err.println("Start recovery");
		}
        //GeographicDelegateConfiguration geoRoutingConfiguration = (GeographicDelegateConfiguration)routingHeader;
        AnycastFaceRoutingHeader faceRoutingHeader=(AnycastFaceRoutingHeader)routingHeader;
        faceRoutingHeader.setGreedyFailureNode(new NetworkNodeImpl(address,info.getPosition()));
               // faceRoutingHeader.setGreedyFailureNode(new NetworkNodeImpl(sourceAddress,sourcePosition));
        handleNextHop(handler,faceRoutingHeader);
		//handleStartRoutingRequest(handler,routingHeader); 
//		        (LinkLayerAddress)geoRoutingConfiguration.getDestinationAddress(),
//				geoRoutingConfiguration.getDestinationPosition());


    }
    
    
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }

	/**
	 * @param neighbors
	 * @return Returns a String representing the neighbors
	 */
	private String writeNeighbors(NetworkNode[] neighbors) {
		String str = "Nachbarn:\n";
		if (neighbors == null) {
			return "";
		}
		for (int i = 0; i < neighbors.length; i++) {
			str += neighbors[i].getAddress() + "; ";
		}
		return str;

	}

	private String writeRoutingSteps(RoutingStep[] steps) {
		String str = "RoutingSteps:\n";
		str += "i\t\tAddress\t\tbreaked\t\tclockwise\tfaceChanged\tfinished\tresumeGreedy\tturned\t\tstopNode\n";
		for (int i = 0; i < steps.length; i++) {
			Address a = steps[i].getNode().getAddress();
			str += i + ".\t\t" + a.toString();
			str += "\t\t" + steps[i].isBreaked();
			str += "\t\t" + steps[i].isClockwise();
			str += "\t\t" + steps[i].isFaceChanged();
//			str += "\t\t" + steps[i].isFinished();
			str += "\t\t" + steps[i].isResumeGreedy();
//			str += "\t\t" + steps[i].isTurned();
//			str += "\t\t" + steps[i].getNode().isStopNode();
			str += "\n";
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		//ignore

	}
	


	
    


    public void updatePositioningData(PositioningData info) {
        this.info=info;
        
    }

    public void removePositioningData() {
        // TODO Auto-generated method stub
        
    }

    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location) {
        
        return new AnycastFaceRoutingHeader((GeographicLocation)location,false,false,SERVICE_ID);
    }
    

    public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header) {
        return new AnycastFaceRoutingHeader(header,SERVICE_ID);
    }
    
    //
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader routingHeader, Location location) {
        return new AnycastFaceRoutingHeader((DefaultRoutingHeader)routingHeader,(GeographicLocation)location,SERVICE_ID);
        
    }
    




    






}