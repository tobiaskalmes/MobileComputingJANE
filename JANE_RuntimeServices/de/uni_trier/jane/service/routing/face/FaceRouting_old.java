/* 29.09.04 erste Realisierung der Anwendung zum Testen des PlanarGraphExplorers
 * 03.10.04 Frage: Wie erfï¿½hrt ein Dummy User den Startpunkt und den Zielpunkt?
 * ...
 * 19.10.04 sollte bald fertig sein 
 * So ganz blick ich hier noch nicht durch.
 * 21.10.04 bei crossing, muss ich da clockwise und counterclockwise vertauschen
 * 
 * 1. Diagramm malen von dem Beispiel Greedy Routing
 * 2. danach ï¿½berlegen, da die gewï¿½nschten Daten vom Startpunkt und Zielpunkt im Header sind
 * 3. Wo setze ich die ganzen Conditions? In der Klasse mit der Mainmethode und ï¿½bergib sie an diese Klasse oder
 * ï¿½bergebe ich dieser Klasse ein objekt vom Typ PlanarGraphExplorer
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
 * 20.10.04 OneHopPlanarizerListener funktioniert, dafï¿½r lï¿½uft jetzt das packet im kreis
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
import de.uni_trier.jane.service.location_directory.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.service.routing.unicast.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a face routing startegy based on position information
 * of all one-hop neighbors or knowledge of multi hop neighbors
 */
public class FaceRouting_old implements RuntimeService,
		UnicastRoutingAlgorithm,
		PositionUnicastRoutingAlgorithm_Sync,
		LocationDirectoryEntryReplyHandler {

	
	// TODO TEST
	private boolean ENERGY_TEST = false;
	
	public static final ServiceID SERVICE_ID = new EndpointClassID(
			FaceRouting_old.class.getName());


	/**
	 * Creates an instance of Facerouting
	 * @param serviceUnit The service unit
	 * @param conditions All conditions needed by face routing
	 * @param udgRadius The radius of the udg graph
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,
			Conditions conditions,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory) {
		return createInstance(serviceUnit, startConditionFactory, crossingConditionFactory, resumeGreedyConditionFactory, conditions, null, false);
	}
	

	/**
	 * Creates an instance of Facerouting
	 * @param serviceUnit The service unit
	 * @param conditions The conditions needed by face routing
	 * @param udgRadius The radius of the udg graph
	 * @param resumeGreedyServiceID The serviceID of greedy routing
	 * 
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
	 * Creates an instance of face routing
	 * @param serviceUnit The service unit
	 * @param conditions The conditions needed by face routing
	 * @param udgRadius The radius of the udg graph
	 * @param resumeGreedyServiceID The serviceID of greedy routing
	 * @param debug to set debug enabled, so more console output is created
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			Conditions conditions,
			ServiceID resumeGreedyServiceID, boolean debug) {
		//		LinkLayer linkLayerService = (LinkLayer) serviceUnit
		//		.getService(LinkLayer.class);
		ServiceID locationDirectoryService = serviceUnit
				.getService(LocationDirectoryService.class);
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
		
		//Creating face routing service
		Service faceRouting = new FaceRouting_old(
				startConditionFactory,
				turnCondition, breakCondition, crossingConditionFactory,
				resumeGreedyConditionFactory, weightCondition,
				dominatingSetStartCondition,
				locationDirectoryService,
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
	

	private ServiceID locationServiceID;

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

    private PositionUnicastRoutingAlgorithm_Sync greedyAlgorith;

	private EnergyStatusProviderServiceStub energyStatusProviderServiceStub;

//	private Shape resumeGreedyConditionShape;
//	private double resumeGreedyConditionShapeCreation;
	
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
	 * @param locationServiceID
	 * @param routingServiceID
	 * @param neighborDiscoveryServiceID
	 * @param recoveryHeaderFactory
	 * @param debug
	 * @param udgDominatingSetID
	 * @param energyServiceID
	 * @param planarizerServiceID
	 */
	public FaceRouting_old(
			StartConditionFactory startConditionFactory, 
			TurnCondition turnCondition,
			BreakCondition breakCondition, 
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			StepSelector weightCondition,
			DominatingSetStartCondition dominatingSetStartCondition,
			ServiceID locationServiceID,
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
		this.locationServiceID = locationServiceID;
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
			FaceRoutingHeader faceRoutingHeader) {
	    replyHandle.delegateMessage(greedyAlgorith.
	            getPositionBasedHeader(faceRoutingHeader),faceRoutingHeader);
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
					(LinkLayerAddress) neighborDiscoveryData[i].getSender(),
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

//		double time = operatingSystem.getTime();
//		if(time < resumeGreedyConditionShapeCreation + 1.0) {
//			return resumeGreedyConditionShape;
//		}
		
		return null;
//		return resumeGreedyConditionShape;
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

    public void handleLocationDataReply(LocationDirectoryEntry info) {
        
        Position destinationPosition = info.getPosition();
        Address destinationAddress=info.getAddress();
        List list = (List)destinationPendingMessageListMap.remove(destinationAddress);
        if(list != null) {
            
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                 PendingMessageEntry pendingMessageEntry = (PendingMessageEntry)iterator.next();
                 FaceRoutingHeader header=(FaceRoutingHeader) pendingMessageEntry.getRoutingHeader();
                 header.setReceiverPosition(destinationPosition);  
//                 if(!header.hasGreedyFailureNode()){
//                	 Address ownAddress=neighborDiscoveryServiceStub.getOwnAddress();
//                     LocationData ownLocationData = (LocationData) neighborDiscoveryServiceStub
//     				.getNeighborDiscoveryData(address).getDataMap().getData(
//     						LocationData.DATA_ID); //locationSystemInfo.getPosition();
//                     Position ownPosition = ownLocationData.getPosition();
//                	 header.setGreedyFailureNode(new NetworkNodeImpl(ownAddress,ownPosition,false));
//                 }
                 handleStartUnicastRequest(pendingMessageEntry.getRoutingTaskHandler(),header);
                //handleNextHop(pendingMessageEntry.getRoutingTaskHandler(), header);
            }
        }
    }

    
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
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
		FaceRoutingHeader faceRoutingHeader = (FaceRoutingHeader) header;
		//TODO eigentlich nicht notwendig
		FinishCondition finishCondition=faceRoutingHeader.getFinishCondition();
		if (faceRoutingHeader.getReceiver().equals(address)) {
			//angekommen
		    replyHandle.deliverMessage(header);

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
			handleNextHop(replyHandle, faceRoutingHeader, sender);
			return;
		}
		LocationData senderLocation = (LocationData) neighborDiscoveryServiceStub
				.getNeighborDiscoveryData(sender).getDataMap().getData(
						LocationData.DATA_ID);
		Position lastPosition = senderLocation.getPosition();
		//this.sender = sender;
		sender = (LinkLayerAddress) ((FaceRoutingHeader) header).getLastSenderAddress();
		lastPosition = ((FaceRoutingHeader) header).getLastSenderPosition();
		handleNextHop(replyHandle, faceRoutingHeader, sender);

	}

	/**
	 * This method is used to start facerouting
	 * 
	 * @param replyHandle
	 *            The message id
	 * @param header
	 *            The message header
	 */
	private void handleNextHop(RoutingTaskHandler replyHandle, FaceRoutingHeader header) {
		if(debug){
			System.out.println("Starte routing");
		}
		handleNextHop(replyHandle,header,null);
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
								(LinkLayerAddress) neighborDiscoveryData[i]
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
							(LinkLayerAddress) neighborDiscoveryData[i]
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
			FaceRoutingHeader header, Address sender) {
		LocationData ownLocationData = (LocationData) neighborDiscoveryServiceStub
				.getNeighborDiscoveryData(address).getDataMap().getData(
						LocationData.DATA_ID); //locationSystemInfo.getPosition();
		Position ownPosition = ownLocationData.getPosition();
		NetworkNode destination = new NetworkNodeImpl(header.getReceiver(), 
				header.getReceiverPosition());
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
		
		if(ENERGY_TEST) {
//			startCondition = new EnergyStartCondition(neighborDiscoveryServiceStub);
			crossingCondition = new EnergyCrossingCondition(neighborDiscoveryServiceStub);
		}
		
		TurnCondition turnCondition = header.getTurnCondition();
		ResumeGreedyCondition resumeGreedyCondition = header.getResumeGreedyCondition();
		PlanarGraphExplorer explorer = new PlanarGraphExplorer();

//		resumeGreedyConditionShape = resumeGreedyCondition.getShape();
//		resumeGreedyConditionShapeCreation = operatingSystem.getTime();
		
		RoutingStep[] steps=null;
		
		//TODO ersetzen
		NetworkNode previousNode;
		if(sender!=null){
			//previousNode = planarServiceStub.getPlanarGraphNode(new NetworkNodeImpl(sender, lastPosition));
			previousNode=node.getAdjacentNode(sender);
			
            // Mobilitaet ist boese!
            if (previousNode==null){
            	NeighborDiscoveryData data = neighborDiscoveryServiceStub.getNeighborDiscoveryData(sender);
            	Position senderPosition = null;
            	if(data != null) {
                	senderPosition = LocationData.getPosition(data);
            	}
            	if(senderPosition == null) {
            		senderPosition = header.getLastSenderPosition();
            	}
            	previousNode = new NetworkNodeImpl(sender, senderPosition);
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
//System.out.println("DROP2");
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
		replyHandle.forwardAsUnicast(header, (LinkLayerAddress) next.getNode().getAddress());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.routing.UnicastStartService#handleStartUnicastRequest(de.uni_trier.jane.signaling.ReplyHandle,
	 *      de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress)
	 */
	//TODO wird diese Methode noch benoetigt?
	public void handleStartUnicastRequest(RoutingTaskHandler replyHandle,
			LinkLayerAddress destination) {
//		visited = true;
        handleStartRoutingRequest(replyHandle,getUnicastHeader(destination));
	}
	//
    public void handleStartRoutingRequest(RoutingTaskHandler handle,
            RoutingHeader routingHeader) {
        
//        visited=true;
        FaceRoutingHeader greedyRoutingHeader = (FaceRoutingHeader)routingHeader;
        if (greedyRoutingHeader.hasReceiverPosition()){
            handleNextHop(handle,greedyRoutingHeader);
            return;
        }
        Address destination=routingHeader.getReceiver();
        List pendingMessageList = (List)destinationPendingMessageListMap.get(destination);
        
        // we are requested about the destination for the first time
        if(pendingMessageList == null) {
            pendingMessageList = new ArrayList();
            destinationPendingMessageListMap.put(destination, pendingMessageList);
            
            // This is the NEW location directory entry request
            ListenerID listenerID = operatingSystem.registerOneShotListener(
                    FaceRouting_old.this,
                    LocationDirectoryEntryReplyHandler.class);
            operatingSystem.sendSignal(locationServiceID,
                    new LocationDirectoryService.LocationDirectoryEntryRequest(
                            destination, listenerID));
            
            /* TODO Did I translate this OLD request correctly? -- Christoph Lange
            RequestHandlerPair requestHandlerPair = new LocationDirectoryService.
                LocationDirectoryEntryRequestHandlerPair(destination, this);
            operatingSystem.sendRequest(locationServiceID, requestHandlerPair);
            */
        }

        pendingMessageList.add(new PendingMessageEntry(handle,routingHeader));
        
       

    }

    //TODO wird diese Methode noch benoetigt?
	public void handleStartUnicastRequest(RoutingTaskHandler replyHandle,
            FaceRoutingHeader faceRoutingHeader) {
//		visited = true;
		Address sourceAddress = address;
		FinishCondition finishCondition=faceRoutingHeader.getFinishCondition();
		LocationData ownLocationData = (LocationData) neighborDiscoveryServiceStub
				.getNeighborDiscoveryData(address).getDataMap().getData(
						LocationData.DATA_ID); //locationSystemInfo.getPosition();
		Position ownPosition = ownLocationData.getPosition();
//		FaceRoutingHeader header = new FaceRoutingHeader(sourceAddress,
//				ownPosition, destination, destinationPosition, SERVICE_ID);
		Position sourcePosition = ownPosition;
        faceRoutingHeader.setGreedyFailureNode(new NetworkNodeImpl(sourceAddress,sourcePosition));
		handleNextHop(replyHandle, faceRoutingHeader);
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

		DefaultRoutingHeader defaultRoutingHeader = (DefaultRoutingHeader)header;
		Address previous = defaultRoutingHeader.getPreviousNode();
		if(previous == null) {
			handleNextHop(replyHandle, (FaceRoutingHeader)header);
		}
		else {
			handleNextHop(replyHandle, (FaceRoutingHeader)header, previous);
		}
//	    replyHandle.dropMessage(header);
//System.out.println("Error behandelt");

	}
	//
    public void handleMessageForwardProcessed(RoutingHeader header) {
    	//ignore
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.RuntimeService#start(de.uni_trier.jane.service.RuntimeOperatingSystem)
	 */
	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		operatingSystem = runtimeOperatingSystem;
		operatingSystem.registerSignalListener(UnicastRoutingAlgorithm.class);
		operatingSystem.registerAccessListener(PositionUnicastRoutingAlgorithm_Sync.class);
		
		operatingSystem.registerAtService(locationServiceID,
				LocationDirectoryService.class);

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
                    greedyAlgorith=(PositionUnicastRoutingAlgorithm_Sync)operatingSystem.
    				    getAccessListenerStub(greedyID,PositionUnicastRoutingAlgorithm_Sync.class);
                }
            });
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.service.routing.greedy.RecoveryStrategy#startRecovery(de.uni_trier.jane.signaling.ReplyHandle,
	 *      de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress,
	 *      de.uni_trier.ubi.appsim.kernel.basetype.Position)
	 */
//	public void startRecovery(ReplyHandle replyHandle,
//			LinkLayerAddress destinationAddress, Position destinationPosition) {
//	    
//	    
//		if (debug) {
//			System.err.println("Start recovery");
//			System.err.println("deprecated");
//		}
////		handleStartUnicastRequest(replyHandle, destinationAddress,
////				destinationPosition);
//
//	}
	
	//
    public void handleMessageDelegateRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        if (debug) {
			System.err.println("Start recovery");
		}
        
		handleStartUnicastRequest(handler,(FaceRoutingHeader) routingHeader); 


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
//			str += "\t\t" + steps[i].isBreaked();
			str += "\t\t" + steps[i].isClockwise();
			str += "\t\t" + steps[i].isFaceChanged();
//			str += "\t\t" + steps[i].isFinished();
//			str += "\t\t" + steps[i].isResumeGreedy();
//			str += "\t\t" + steps[i].isTurned();
//			str += "\t\t" + steps[i].getNode().isStopNode();
			str += "\n";
		}
		return str;
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("startConditionFactory", startConditionFactory);
		parameters.addParameter("crossingConditionFactory", crossingConditionFactory);

		// TODO include all parameters!

	}

    public RoutingHeader getUnicastHeader(Address destination) {
    	FaceRoutingHeader header = new FaceRoutingHeader(null, null, destination, null, false,
				false, SERVICE_ID,
				crossingConditionFactory.createCrossingCondition(),
				startConditionFactory.createStartCondition(), resumeGreedyConditionFactory.createResumeGreedyCondition());
    	header.setMaxHops(100);
    	return header;
	}

    public PositionbasedRoutingHeader getPositionBasedHeader(Address destinationAddress,Position destinationPosition){
    	
        return new FaceRoutingHeader(null,null,destinationAddress,destinationPosition,false,false,SERVICE_ID,
				crossingConditionFactory.createCrossingCondition(),
				startConditionFactory.createStartCondition(), resumeGreedyConditionFactory.createResumeGreedyCondition());
    }

    public PositionbasedRoutingHeader getPositionBasedHeader(PositionbasedRoutingHeader positionbasedRoutingHeader) {
    	Address destinationAddress = positionbasedRoutingHeader.getReceiver();
    	Position destinationPosition = positionbasedRoutingHeader.getReceiverPosition();
    	MessageID messageID = positionbasedRoutingHeader.getMessageID();
    	FaceRoutingHeader newHeader = new FaceRoutingHeader(null,null,destinationAddress,destinationPosition,false,false,SERVICE_ID,
				crossingConditionFactory.createCrossingCondition(),
				startConditionFactory.createStartCondition(), resumeGreedyConditionFactory.createResumeGreedyCondition());
    	newHeader.setMessageID(messageID);
    	
    	return newHeader;
//        return new FaceRoutingHeader(positionbasedRoutingHeader,SERVICE_ID);
    }

}