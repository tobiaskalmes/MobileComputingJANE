
/*****************************************************************************
* 
* $Id: FaceRouting.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.location_directory.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.gg.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class realize face routing using headers to implement to routing
 * algorithm, so each message knows how it has to be routed
 * 
 * @author Stefan Peters
 */
public class FaceRouting extends AbstractPositionBasedRoutingAlgorithm
		implements FaceRoutingAlgorithm, LocationRoutingAlgorithm_Sync {

	private ServiceID SERVICE_ID = new EndpointClassID(
			FaceRouting.class.getName());

    
    
    
    
    
    /**
     * Creating a face routing instance
     * 
     * @param serviceUnit
     *            The service unit
     * @param startConditionFactory
     *            The factory to create the start condition
     * @param turnConditionFactory
     *            The factory to create the turn condition
     * @param crossingConditionFactory
     *            The factory to create the crossing condition
     * @param resumeGreedyConditionFactory
     *            The factory to create the resume greedy condition
     * @param finishConditionFactory
     *            The factory to create the finish condition
     * @param breakConditionFactory
     *            The factory to create the break condition
     * @param headerFactory
     *            The factory to craete the header for face routing
     * @param enterHandler
     *            The enter handler is only needed by face routing on dominating
     *            set. It is used to decide, when to enter Facerouting, e.g. Routing starts in a node
     *            not in dominating set.
     * @param endHandler
     *            The end handler is only needed by face routing on dominating
     *            set. It is used to decide when to end Facerouting, e.g. Endnode is 
     *            not in dominating set..
     * @param stepSelector
     *            The step selector chooses the next routing step
     * @param greedyID 
     *            The Greedy recovery
     * 
     * @return the ID of the created face routing service                   
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit,
            StartConditionFactory startConditionFactory,
            CrossingConditionFactory crossingConditionFactory,
            TurnConditionFactory turnConditionFactory,
            ResumeGreedyConditionFactory resumeGreedyConditionFactory,
            BreakConditionFactory breakConditionFactory,
            FinishConditionFactory finishConditionFactory,
            
            FaceRoutingHeaderFactory headerFactory, EnterHandler enterHandler,
            EndHandler endHandler, StepSelector stepSelector, ServiceID greedyID ) {
        return createInstance(serviceUnit,startConditionFactory,crossingConditionFactory,turnConditionFactory,resumeGreedyConditionFactory,
                breakConditionFactory,finishConditionFactory, headerFactory,enterHandler,endHandler,stepSelector,
                greedyID,new EndpointClassID(FaceRouting.class.getName()));
    }
    
    
    
    
    
	/**
	 * Creating a face routing instance
	 * 
	 * @param serviceUnit
	 *            The service unit
	 * @param startConditionFactory
	 *            The factory to create the start condition
	 * @param turnConditionFactory
	 *            The factory to create the turn condition
	 * @param crossingConditionFactory
	 *            The factory to create the crossing condition
	 * @param resumeGreedyConditionFactory
	 *            The factory to create the resume greedy condition
	 * @param finishConditionFactory
	 *            The factory to create the finish condition
	 * @param breakConditionFactory
	 *            The factory to create the break condition
	 * @param headerFactory
	 *            The factory to craete the header for face routing
	 * @param enterHandler
	 *            The enter handler is only needed by face routing on dominating
	 *            set. It is used to decide, when to enter Facerouting, e.g. Routing starts in a node
	 *            not in dominating set.
	 * @param endHandler
	 *            The end handler is only needed by face routing on dominating
	 *            set. It is used to decide when to end Facerouting, e.g. Endnode is 
	 *            not in dominating set..
	 * @param stepSelector
	 *            The step selector chooses the next routing step
     * @param greedyID 
     *            The Greedy recovery
     * @param faceRoutingID
     *             The predifined ID of the created face routing service
     * @return the ID of the created face routing service                   
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
            TurnConditionFactory turnConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
            BreakConditionFactory breakConditionFactory,
			FinishConditionFactory finishConditionFactory,
			
			FaceRoutingHeaderFactory headerFactory, EnterHandler enterHandler,
			EndHandler endHandler, StepSelector stepSelector, ServiceID greedyID,
            ServiceID faceRoutingID) {
		ServiceID locationServiceID=null;
		ServiceID planarizerServiceID;
		if (serviceUnit.hasService(PlanarizerService.class)) {
			planarizerServiceID = serviceUnit
					.getService(PlanarizerService.class);
		} else {
			planarizerServiceID = GabrielGraphPlanarizerService.createInstance(
					serviceUnit, false);
		}
		if (serviceUnit.hasService(LocationDirectoryService.class)) {
			locationServiceID = serviceUnit
					.getService(LocationDirectoryService.class);
		} else {
			// SimulationLocationDirectoryService.createInstance(serviceUnit);
			// SimulationPositioningSystem.createInstance(serviceUnit);
			
			//locationServiceID = serviceUnit
			//		.getService(LocationDirectoryService.class);
		}
        if (!serviceUnit.hasService(LocationDataDisseminationService.class)){
            LocationDataDisseminationService.createInstance(serviceUnit);
        }
		
		// if(serviceUnit.hasService(GreedyRoutingAlgorithm.class)) {
		// greedyID=serviceUnit.getService(GreedyRoutingAlgorithm.class);
		// }
		ServiceID neighborDiscoveryID = serviceUnit
				.getService(NeighborDiscoveryService.class);
		FaceRouting face = new FaceRouting(locationServiceID,
				planarizerServiceID, neighborDiscoveryID, greedyID,
				finishConditionFactory, breakConditionFactory,
				startConditionFactory, turnConditionFactory,
				crossingConditionFactory, resumeGreedyConditionFactory,
				headerFactory, enterHandler, endHandler, stepSelector, faceRoutingID);
		return serviceUnit.addService(face);
	}
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param greedyRoutingID
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit,ServiceID greedyRoutingID) {
        return createInstance(serviceUnit, greedyRoutingID, new EndpointClassID(FaceRouting.class.getName()));
              
        
    }
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param greedyRoutingID
     * @param faceRoutingID
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID greedyRoutingID, ServiceID faceRoutingID) {
        return createInstance(
                serviceUnit,
                AngleStartCondition.FACTORY,
                AngleCrossingCondition.FACTORY,
                SimpleTurnCondition.FACTORY, SimpleResumeGreedyCondition.FACTORY,//ResumeGreedySoonest.FACTORY,// ResumeGreedySoonest.FACTORY,
                BreakConditionImpl.FACTORY, SimpleFinishCondition.FACTORY,
                new NormalFaceRoutingHeaderFactory(false,false),
                null,null,new HopCountWeightCondition(),
                greedyRoutingID,
                faceRoutingID);
    }

	private FinishConditionFactory finishConditionFactory;

	private BreakConditionFactory breakConditionFactory;

	private StartConditionFactory startConditionFactory;

	private CrossingConditionFactory crossingConditionFactory;

	private ResumeGreedyConditionFactory resumeGreedyConditionFactory;

	private TurnConditionFactory turnConditionFactory;

	private FaceRoutingHeaderFactory headerFactory;

	private ServiceID planarizerServiceID;

	private StepSelector stepSelector;

	private PlanarizerService planarizer;

	private ServiceID greedyID;

	private PlanarGraphExplorer planarGraphExplorer;

	private EnterHandler enterHandler;

	private EndHandler endHandler;

	/**
	 * The constructor for face routing
	 * 
	 * @param locationServiceID
	 *            The id of the location service
	 * @param planarizerServiceID
	 *            The id of a planarizer service
	 * @param neighborDiscoveryID
	 *            The id of a neighbor discovery service
	 * @param greedyID
	 *            The id of greedy routing, or null if not available
	 * @param finishConditionFactory
	 *            The factory to create a finish condition
	 * @param breakConditionFactory
	 *            The factory to create a break condition
	 * @param startConditionFactory
	 *            The factory to create a start condition
	 * @param turnConditionFactory
	 *            The factory to create a turn condition
	 * @param crossingConditionFactory
	 *            The factory to create a crossing condition
	 * @param resumeGreedyConditionFactory
	 *            The factory to create a resume greedy condition
	 * @param headerFactory
	 *            The factory to create the needed header
	 * @param enterHandler
	 *            The enter handler is only needed by face routing on dominating
	 *            set
	 * @param endHandler
	 *            The end handler is only needed by face routing on dominating
	 *            set
	 * @param stepSelector
	 *            The step selector chooses the next routing step
	 * @param faceRoutingID 
	 */
	public FaceRouting(ServiceID locationServiceID,
			ServiceID planarizerServiceID, ServiceID neighborDiscoveryID,
			ServiceID greedyID, FinishConditionFactory finishConditionFactory,
			BreakConditionFactory breakConditionFactory,
			StartConditionFactory startConditionFactory,
			TurnConditionFactory turnConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			FaceRoutingHeaderFactory headerFactory, EnterHandler enterHandler,
			EndHandler endHandler, StepSelector stepSelector, ServiceID faceRoutingID) {
		super(locationServiceID, neighborDiscoveryID);
        SERVICE_ID=faceRoutingID;
		this.finishConditionFactory = finishConditionFactory;
		this.breakConditionFactory = breakConditionFactory;
		this.startConditionFactory = startConditionFactory;
		this.crossingConditionFactory = crossingConditionFactory;
		this.resumeGreedyConditionFactory = resumeGreedyConditionFactory;
		this.turnConditionFactory = turnConditionFactory;
		this.headerFactory = headerFactory;
		this.planarizerServiceID = planarizerServiceID;
		this.greedyID = greedyID;
		this.planarGraphExplorer = new PlanarGraphExplorer();
		this.endHandler = endHandler;
		this.enterHandler = enterHandler;
		this.stepSelector = stepSelector;
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore

	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		// TODO Auto-generated method stub
		super.start(runtimeOperatingSystem);
		runtimeOperatingSystem.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
	}
	public Shape getShape() {
//		ShapeCollection shapeCollection = new ShapeCollection();
//		shapeCollection.addShape(new EllipseShape(new Extent(10, 10),
//				Color.GREY, true));
//		return shapeCollection;
//      was soll das auf 0 ein grauer Kreis malen  ???
        return null;
	}

	public void getParameters(Parameters parameters) {
		// ignore
	}

	public PositionbasedRoutingHeader getPositionBasedHeader(
			Address destinationAddress, Position destinationPosition) {
		AbstractFaceRoutingHeader header = null;
		Address ownAddress = getOwnAddress();
		Position ownPosition = getOwnPosition();
		header = headerFactory.createHeader(ownAddress, ownPosition,
				destinationAddress, destinationPosition, SERVICE_ID);
		return header;
	}
    
    public PositionbasedRoutingHeader getPositionBasedHeader(
            PositionbasedRoutingHeader positionbasedRoutingHeader) {
        Address ownAddress = getOwnAddress();
        Position ownPosition = getOwnPosition();
        AbstractFaceRoutingHeader header;
//        if (positionbasedRoutingHeader instanceof LocationBasedRoutingHeader){
//            header = headerFactory.createAnycastGreedyFailureHeader(ownAddress, ownPosition,    
//                    (LocationBasedRoutingHeader)positionbasedRoutingHeader, SERVICE_ID);
//        }else{
            header = headerFactory.createGreedyFailureHeader(ownAddress, ownPosition,    
                positionbasedRoutingHeader, SERVICE_ID);
        //}
        return header;
    }

    /**
     * 
     * TODO Comment method
     * @param positionbasedRoutingHeader
     * @param lastSenderAddress
     * @param lastSenderPosition
     * @return
     */
//	public PositionbasedRoutingHeader getPositionBasedHeader(
//			PositionbasedRoutingHeader positionbasedRoutingHeader,
//            Address lastSenderAddress, Position lastSenderPosition) {
//        Address ownAddress = getOwnAddress();
//        Position ownPosition = getOwnPosition();
//        AbstractFaceRoutingHeader header = headerFactory.createGreedyFailureHeader(ownAddress, ownPosition,
//                lastSenderAddress,lastSenderPosition,
//                positionbasedRoutingHeader, SERVICE_ID);
//        return header;
////		return getPositionBasedHeader(positionbasedRoutingHeader.getReceiver(),
////				positionbasedRoutingHeader.getReceiverPosition());
//	}

	public RoutingHeader getUnicastHeader(Address destination) {
		AbstractFaceRoutingHeader header = null;
		Address ownAddress = getOwnAddress();
		Position ownPosition = getOwnPosition();
		header = headerFactory.createHeader(ownAddress, ownPosition,
				destination, null, SERVICE_ID);
		return header;
	}

	public LocationBasedRoutingHeader getLocationRoutingHeader(Location location) {
		GeographicLocation geographicLocation = (GeographicLocation) location;
		Address ownAddress = getOwnAddress();
		Position ownPosition = getOwnPosition();
		return headerFactory.createAnyCastHeader(ownAddress, ownPosition,
				geographicLocation, SERVICE_ID);
	}

	public LocationBasedRoutingHeader getLocationRoutingHeader(
			LocationBasedRoutingHeader locationHeader) {

        return getLocationRoutingHeader(locationHeader,locationHeader.getTargetLocation());
	}
    
    //
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {
        Address ownAddress = getOwnAddress();
        Position ownPosition = getOwnPosition();
        return headerFactory.createAnycastGreedyFailureHeader(ownAddress, ownPosition,otherRoutingHeader,location, SERVICE_ID);
    }

	public PlanarGraphExplorer getPlanarGraphExplorer() {
		return planarGraphExplorer;
	}

	public StepSelector getNextNodeSelector() {
		return stepSelector;
	}

	public PlanarizerService getPlanarizerService() {
		if (planarizer == null) {
			planarizer = (PlanarizerService) runtimeOperatingSystem
					.getAccessListenerStub(planarizerServiceID,
							PlanarizerService.class);
		}
		return planarizer;
	}

	public StartCondition createStartCondition() {
		return startConditionFactory.createStartCondition();
	}

	public CrossingCondition createCrossingCondition() {
		return crossingConditionFactory.createCrossingCondition();
	}

	public TurnCondition createTurnCondition() {
		return turnConditionFactory.createTurnCondition();
	}

	public ResumeGreedyCondition createResumeGreedyCondition() {
		return resumeGreedyConditionFactory.createResumeGreedyCondition();
	}

	public BreakCondition createBreakCondition() {
		return breakConditionFactory.createBreakCondition();
	}

	public FinishCondition createFinishCondition(Address destination) {
		return finishConditionFactory.createFinishCondition(destination);
	}

	public ServiceID getGreedyID() {
		return greedyID;
	}

	public PositionUnicastRoutingAlgorithm_Sync getRecovery() {
		return (PositionUnicastRoutingAlgorithm_Sync) runtimeOperatingSystem
				.getAccessListenerStub(greedyID,
						PositionUnicastRoutingAlgorithm_Sync.class);
	}
    
    /**
     * TODO Comment method
     * @return
     */
    public LocationRoutingAlgorithm_Sync getAnycastRecovery() {

        return (LocationRoutingAlgorithm_Sync) runtimeOperatingSystem
        .getAccessListenerStub(greedyID,
                LocationRoutingAlgorithm_Sync.class);
    }

	public EndHandler getEndHandler() {
		return endHandler;
	}

	public EnterHandler getEnterHandler() {
		return enterHandler;
	}

	public NetworkNode getNetworkNode() {
		return new NetworkNodeImpl(getOwnAddress(), getOwnPosition(), false);
	}







}
