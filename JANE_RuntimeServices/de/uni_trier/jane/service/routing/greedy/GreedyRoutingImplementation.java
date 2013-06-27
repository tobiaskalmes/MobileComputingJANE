/*****************************************************************************
 * 
 * GreedyRouting.java
 * 
 * $Id: GreedyRoutingImplementation.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.routing.greedy;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.GeographicLocation;
import de.uni_trier.jane.service.locationManager.basetypes.Location;
import de.uni_trier.jane.service.location_directory.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.neighbor_discovery.filter.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.face.FaceUsingRoutingAlgorithm;
import de.uni_trier.jane.service.routing.greedy.metric.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * This class implements a traditional greedy routing startegy based on 
 * position information of all one-hop neighbors.
 */
public class GreedyRoutingImplementation implements RuntimeService, 
	RoutingAlgorithm,
	PositionUnicastRoutingAlgorithm_Sync,
    LocationRoutingAlgorithm_Sync,
	LocationDirectoryEntryReplyHandler,
    FaceUsingRoutingAlgorithm{


	public static final ServiceID SERVICE_ID = new EndpointClassID(GreedyRoutingImplementation.class.getName());
	
    // initialized in constructor
    private ServiceID locationServiceID;
    
    private ServiceID neighborDiscoveryServiceID;
    private ServiceID defaultRecoveryServiceID;
    private Map destinationPendingMessageListMap;
    
    // initialized on startup
    private Address address;
    private RuntimeOperatingSystem operatingService;
    private NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;

    // updated periodically
    

	private ServiceID linkLayerServiceID;

	private LocalRoutingMetric defaultWeightFunction;

    private PositionUnicastRoutingAlgorithm_Sync recoveryStrategy;

    private boolean finalDelivery;

    private boolean useFaceRecovery;

    public static ServiceID createInstance(ServiceUnit serviceUnit,boolean finalDelivery) {
        return createInstance(serviceUnit,new LocalRoutingMetric(){

            public double calculate(Position sender, Position destination, Position receiver) {
                double distance=receiver.distance(destination);
                if(distance < sender.distance(destination)){
                    return distance;
                }
                return Double.POSITIVE_INFINITY;
            }
        },finalDelivery);
        
    }
    /**
     * TODO Comment method
     * @param serviceUnit
     * @param metric
     * @param b
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, LocalRoutingMetric metric, boolean finalDelivery) {

        return createInstance(serviceUnit,null,metric,finalDelivery);
    }

    public static ServiceID createInstance(ServiceUnit serviceUnit) {
    	return createInstance(serviceUnit,new LocalRoutingMetric(){

			public double calculate(Position sender, Position destination, Position receiver) {
				double distance=receiver.distance(destination);
				if(distance < sender.distance(destination)){
					return distance;
				}
				return Double.POSITIVE_INFINITY;
			}
    		
    	});
    }
    
    public static ServiceID createInstance(ServiceUnit serviceUnit, LocalRoutingMetric metricInterface) {
    	return createInstance(serviceUnit,null,metricInterface);
    }
    
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID recoveryServiceID){
    	return createInstance(serviceUnit,recoveryServiceID,new LocalRoutingMetric(){

			public double calculate(Position sender, Position destination, Position receiver) {
				double distance=receiver.distance(destination);
				if(distance < sender.distance(destination)){
					return distance;
				}
				return Double.POSITIVE_INFINITY;
			}
    		
    	});
    }
    public static ServiceID createInstance(
            ServiceUnit serviceUnit,
            ServiceID recoveryServiceID,
            LocalRoutingMetric routingMetric) {
        return createInstance(serviceUnit,recoveryServiceID,routingMetric,false);
    }
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param recoveryServiceID
     * @param routingMetric
     * @param finalDelivery
     * @return
     */
    public static ServiceID createInstance(
    		ServiceUnit serviceUnit,
    		ServiceID recoveryServiceID,
    		LocalRoutingMetric routingMetric,boolean finalDelivery) {
    	ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
        ServiceID locationDirectoryService=null;
        if (serviceUnit.hasService(LocationDirectoryService.class)){
            locationDirectoryService= serviceUnit.getService(LocationDirectoryService.class);   
        }
    	 
    	
    	if(!serviceUnit.hasService(RoutingService.class)) {
    		DefaultRoutingService.createInstance(serviceUnit);
    	}
    	ServiceID routingService = serviceUnit.getService(RoutingService.class);
    	
    	if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryService = serviceUnit.getService(NeighborDiscoveryService_sync.class);
    	
    	if(!serviceUnit.hasService(LocationDataDisseminationService.class)) {
    		LocationDataDisseminationService.createInstance(serviceUnit);
    	}
    	Service greedyRouting = new GreedyRoutingImplementation(
    			linkLayerService,
				locationDirectoryService, routingService,
				neighborDiscoveryService, recoveryServiceID,routingMetric,finalDelivery);
		return serviceUnit.addService(greedyRouting);
    }
    
    public GreedyRoutingImplementation(ServiceID linkLayerServiceID, ServiceID locationServiceID, ServiceID routingServiceID, ServiceID neighborDiscoveryServiceID) {
    	this(linkLayerServiceID, locationServiceID, routingServiceID, neighborDiscoveryServiceID, null,null,false);
    }

    /**
     * 
     * Constructor for class <code>GreedyRoutingImplementation</code>
     * @param linkLayerServiceID
     * @param locationServiceID
     * @param routingServiceID
     * @param neighborDiscoveryServiceID
     * @param recoveryServiceID
     * @param metricInterface
     * @param finalDelivery
     */
    public GreedyRoutingImplementation(ServiceID linkLayerServiceID, ServiceID locationServiceID,
    		ServiceID routingServiceID, ServiceID neighborDiscoveryServiceID, ServiceID recoveryServiceID, LocalRoutingMetric metricInterface, boolean finalDelivery) {
    	
    	this.linkLayerServiceID=linkLayerServiceID;
    	this.locationServiceID = locationServiceID;
    	
    	this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
    	this.defaultRecoveryServiceID = recoveryServiceID;
    	this.defaultWeightFunction=metricInterface;
        destinationPendingMessageListMap = new HashMap();
        this.finalDelivery=finalDelivery;
        useFaceRecovery(true);
    }


	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

    public void start(RuntimeOperatingSystem operatingSystem) {
    	this.operatingService = operatingSystem;
    	operatingSystem.registerSignalListener(RoutingAlgorithm.class);
        operatingSystem.registerSignalListener(FaceUsingRoutingAlgorithm.class);
    	operatingService.registerAccessListener(PositionUnicastRoutingAlgorithm_Sync.class);
        operatingService.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
    	
    	neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(operatingSystem, neighborDiscoveryServiceID);
    	if(!neighborDiscoveryServiceStub.getNeighborDiscoveryProperties().isNotifyAboutOwnChanges()) {
    		throw new ServiceException("Greedy routing requires own data to be stored in the neighbor discovery service.");
    	}
    	LinkLayer.LinkLayerStub facade=new LinkLayer.LinkLayerStub(operatingSystem,linkLayerServiceID);
    	address=facade.getLinkLayerProperties().getLinkLayerAddress();
        if (locationServiceID!=null)
            operatingSystem.registerAtService(locationServiceID, LocationDirectoryService.class);
        if (defaultRecoveryServiceID!=null){
            operatingSystem.setTimeout(new ServiceTimeout(0) {
                public void handle() {
                    recoveryStrategy=(PositionUnicastRoutingAlgorithm_Sync)operatingService.
                    getAccessListenerStub(defaultRecoveryServiceID,PositionUnicastRoutingAlgorithm_Sync.class);
                }
            });
        }
    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        return null;
    }

	public void getParameters(Parameters parameters) {
        parameters.addParameter("default metric", defaultWeightFunction.toString());
        if (defaultRecoveryServiceID!=null)
            parameters.addParameter("default recovery",defaultRecoveryServiceID.toString());
        else{
            parameters.addParameter("default recovery","none");
        }
	}

//    public void handleStartUnicastRequest(RoutingTaskHandler replyHandle, Address destination) {
//        handleStartRoutingRequest(replyHandle,getUnicastHeader(destination));  
//    }
    
    //
    
    public void useFaceRecovery(boolean recovery) {
        if (defaultRecoveryServiceID!=null){
            useFaceRecovery=recovery;
        }
        
    }
    public void handleStartRoutingRequest(RoutingTaskHandler handle,
            RoutingHeader routingHeader) {

        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)routingHeader;
        
        
        
        //////for face recovery
        if (greedyRoutingHeader.useFaceRecovery()){
            LocationData ownLocationData = (LocationData)neighborDiscoveryServiceStub.
                getNeighborDiscoveryData(address).getDataMap().getData(LocationData.DATA_ID);
            greedyRoutingHeader.setSourcePosition(ownLocationData.getPosition());
            greedyRoutingHeader.setSourceAddress(address);
            //////for face recovery
            if (!greedyRoutingHeader.hasRecoveryStrategy()){
                greedyRoutingHeader.setRecoveryStrategy(defaultRecoveryServiceID);
            }
        }
        
        if (!greedyRoutingHeader.hasRoutingMetric()){
            greedyRoutingHeader.setRoutingMetric(defaultWeightFunction);
        }
        
        
        
        if (greedyRoutingHeader.hasReceiverPosition()){
            handleNextHop(handle,greedyRoutingHeader);
            return;
        }
        if (!routingHeader.hasReceiver()){
            handle.dropMessage(routingHeader);
            return;
        }
        if (locationServiceID==null){
            throw new IllegalAccessError("No Locationdirectory instanciated");
        }
        Address destination=routingHeader.getReceiver();
        List pendingMessageList = (List)destinationPendingMessageListMap.get(destination);
        
        // we are requested about the destination for the first time
        if(pendingMessageList == null) {
            pendingMessageList = new ArrayList();
            destinationPendingMessageListMap.put(destination, pendingMessageList);

            // This is the NEW location directory entry request
            ListenerID listenerID = operatingService.registerOneShotListener(
                    GreedyRoutingImplementation.this,
                    LocationDirectoryEntryReplyHandler.class);
            operatingService.sendSignal(locationServiceID,
                    new LocationDirectoryService.LocationDirectoryEntryRequest(
                            destination, listenerID));
            
            /* TODO Did I translate this OLD request correctly? -- Christoph Lange
            RequestHandlerPair requestHandlerPair = new LocationDirectoryService.
                LocationDirectoryEntryRequestHandlerPair(destination, this);
            operatingService.sendRequest(locationServiceID, requestHandlerPair);
            */
        }

        pendingMessageList.add(new PendingMessageEntry(handle,routingHeader));
        

    }
    
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }

    public void handleMessageReceivedRequest(RoutingTaskHandler replyHandle, RoutingHeader header, Address sender) {
        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)header;
        handleNextHop(replyHandle, greedyRoutingHeader);
    }

    public void handleUnicastErrorRequest(RoutingTaskHandler replyHandle, RoutingHeader header, Address receiver) {

        // the neighbor is not reachable, so it will be removed by hand and try the next best one
// TODO        neighborDiscoveryInfo.removeNeighbor(receiver);
        
        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)header;
        handleNextHop(replyHandle, greedyRoutingHeader);

    }
    
    //
    public void handleMessageForwardProcessed(RoutingHeader header) {
        //ignore

    }


    //
    public void handleMessageDelegateRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        
        handleNextHop(handler, (GreedyRoutingHeader) routingHeader);

    }


    public void handleLocationDataReply(LocationDirectoryEntry info) {
        
        Position destinationPosition = info.getPosition();
        Address destinationAddress=info.getAddress();
        List list = (List)destinationPendingMessageListMap.remove(destinationAddress);
        if(list != null) {
            
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                 PendingMessageEntry pendingMessageEntry = (PendingMessageEntry)iterator.next();
                 GreedyRoutingHeader header=(GreedyRoutingHeader) pendingMessageEntry.getRoutingHeader();
                 if (destinationPosition!=null){
                     header.setReceiverPosition(destinationPosition);
                     handleNextHop(pendingMessageEntry.getRoutingTaskHandler(), header);
                 }else{
                     pendingMessageEntry.getRoutingTaskHandler().dropMessage(header); 
                 }
            }
        }
    }



    // This method creates the next routing action, which can be delivering, dropping, or
    // unicast forwarding.
    private void handleNextHop(RoutingTaskHandler replyHandle, GreedyRoutingHeader greedyRoutingHeader) {
    	

        
        
    	// TODO: Hier ist ein Beispiel wie man an die Energiedaten der Nachbarn heran kommt. Das Beispiel darf jederzeit wieder gel�scht werden!
//    	NeighborDiscoveryData[] test = neighborDiscoveryServiceStub.getNeighborDiscoveryData(
//    			new TypedDataFilter(1, 1, EnergyStatusData.DATA_ID, true));
//    	for(int i=0; i<test.length; i++) {
//    		NeighborDiscoveryData neighbor = test[i];
//    		EnergyStatusData energyStatusData = (EnergyStatusData)neighbor.getDataMap().getData(EnergyStatusData.DATA_ID);
//    		operatingService.write("### " + address + " <- " + neighbor.getSender() + " : " + energyStatusData.getRemainingJoule());
//    	}
    	// TODO: Ende des Beispiels
    	
        //GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)header;

        // the message arrived at its destination
        LocationData ownLocationData = (LocationData)neighborDiscoveryServiceStub.
        getNeighborDiscoveryData(address).getDataMap().getData(LocationData.DATA_ID);
        if(greedyRoutingHeader.destinationReached(address,ownLocationData)) {
            replyHandle.deliverMessage(greedyRoutingHeader);
            
            return;
        }
        if (greedyRoutingHeader.finalDelivery()&&
                neighborDiscoveryServiceStub.hasNeighborDiscoveryData(greedyRoutingHeader.getReceiver())){
            replyHandle.forwardAsUnicast(greedyRoutingHeader,greedyRoutingHeader.getReceiver());
            return;
                
       }

        // determine own location information from the neighbor discovery service
        Address nextHopAddress = null;

        
        // determine previous sender (null for the first node)
        Address previousNode = greedyRoutingHeader.getPreviousNode();
        
        // if we have a own location information then determine possible next hop
        if(ownLocationData != null) {

	       	// determine all one hop neighbors having valid location data
	        NeighborDiscoveryData[] neighbors = neighborDiscoveryServiceStub.
				getNeighborDiscoveryData(TypedDataFilter.OPEN_ONE_HOP_NEIGHBOR_LOCATION_FILTER);
	
	        // search for the next hop node
	        Position ownPosition = ownLocationData.getPosition();
	        Position destinationPosition = greedyRoutingHeader.getReceiverPosition();
	        double currentDistance = Double.POSITIVE_INFINITY; //ownPosition.distance(destinationPosition);
	        int n = neighbors.length;
	        for(int i=0; i<n; i++) {
	           	LocationData locationData = (LocationData)neighbors[i].getDataMap().getData(LocationData.DATA_ID);
	            Position neighborPosition = locationData.getPosition();
	            double distance = greedyRoutingHeader.getRoutingMetric().calculate(ownPosition, destinationPosition, neighborPosition); 
	            if(distance == currentDistance && previousNode != null) {
	            	if(previousNode.equals(neighbors[i].getSender())) {
	            		nextHopAddress = neighbors[i].getSender();
	            	}
	            }
	            else if(distance < currentDistance) {
	            	nextHopAddress = neighbors[i].getSender(); // TODO: cast nur zum testen
	            	currentDistance = distance;
	            }
	        }
	        
        }
        
        // drop the message if the message is sent to the previous sender
        if(previousNode != null) {
            if(previousNode.equals(nextHopAddress)) {
            	nextHopAddress = null;
            }
        }
        
        // perform forwarding or dropping action
        if(nextHopAddress != null) {
            replyHandle.forwardAsUnicast(greedyRoutingHeader,nextHopAddress);
            
        }
        else {
            
            // we have a recovery strategy and will pass the message handling to it
            if(greedyRoutingHeader.hasRecoveryStrategy()&&useFaceRecovery) {
                
                  replyHandle.delegateMessage(
                          greedyRoutingHeader.getRecoveryStrategyHeader(operatingService),
                          //recoveryStrategy.getPositionBasedHeader(greedyRoutingHeader),
                          greedyRoutingHeader
                   );
                  
//                replyHandle.delegateMessage(new GeographicRoutingHeader())
//                ServiceSignal recoverySignal = new RecoveryStrategy.StartRecoverySignal(replyHandle, greedyRoutingHeader.getDestinationAddress(),
//                        greedyRoutingHeader.getDestinationPosition());
//                operatingService.sendSignal(recoveryServiceID, recoverySignal);
            }
            
            // there is no recovery strategy available so we simply drop the message
            else {
                replyHandle.dropMessage(greedyRoutingHeader);
                
            }
            
        }

    }

    /**
     * TODO Comment method
     * @param header
     * @return
     */
    private GreedyRoutingHeader init(GreedyRoutingHeader header) {
        if (useFaceRecovery){
            header.setUseFaceRecovery(useFaceRecovery);
            header.setRecoveryStrategy(defaultRecoveryServiceID);
        }
        header.setFinalDelivery(finalDelivery);
        header.setRoutingMetric(defaultWeightFunction);
        return header;
    }
        
    public RoutingHeader getUnicastHeader(Address destination){
        return init(new GreedyRoutingHeader(null,destination,null,false,false));
    }
        

    public PositionbasedRoutingHeader getPositionBasedHeader(Address destinationAddress,Position destinationPosition){
        return init(new GreedyRoutingHeader(null,destinationAddress,destinationPosition,false,false));
    }
        
        
    public PositionbasedRoutingHeader getPositionBasedHeader(PositionbasedRoutingHeader positionbasedRoutingHeader) {
        return init(new GreedyRoutingHeader(positionbasedRoutingHeader));
    }
    
    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location) {
        return (LocationBasedRoutingHeader) init(new AnycastGreedyRoutingHeader((GeographicLocation) location,false,false));
    }
    
    public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header) {
        return (LocationBasedRoutingHeader) init(new AnycastGreedyRoutingHeader(header));
    }
    
    //
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {

        return  (LocationBasedRoutingHeader) init(new AnycastGreedyRoutingHeader(otherRoutingHeader,location));
    }
    
        
        

    // the routing header containing of the destination address and position
    public static class GreedyRoutingHeader extends DefaultRoutingHeader
            implements PositionbasedRoutingHeader {

        private static final Shape SHAPE = new EllipseShape(
                Position.NULL_POSITION, new Extent(4, 4), Color.BLUE, true);

        // private Position receiverPosition;
        private static class RoutingData implements DelegationData{
            protected LocalRoutingMetric routingMetric;
            protected boolean finalDelivery;

            protected ServiceID recoveryServiceID;
            protected boolean useFaceRecovery=true;
            public int getCodingSize() {
                return (8+/*routingServiceID*/+8/*recoverStrategy*/ + 8/*routingMetric*/) * 8 + 2/*flags*/;
            }
            public ServiceID getDelegationServiceID() {
                return SERVICE_ID;
            }
        }
        
        protected RoutingData data=new RoutingData();
        
        /**
         * 
         * Constructor for class <code>GreedyRoutingHeader</code>
         * @param sender
         * @param receiver
         * @param receiverPosition
         * @param countHops
         * @param traceRoute
         */
        public GreedyRoutingHeader(Address sender, Address receiver,
                Position receiverPosition, boolean countHops, boolean traceRoute) {
            super(sender, receiver, countHops, traceRoute);
            this.receiverPosition = receiverPosition;
            
        }
        
        







        /**
         * Constructor for class <code>GreedyRoutingHeader</code>
         * 
         * @param header
         */
        protected GreedyRoutingHeader(GreedyRoutingHeader header) {
            super(header);
            data=header.data;//no copy requiered!
        }

        /**
         * 
         * Constructor for class <code>GreedyRoutingHeader</code>
         * @param delagationRoutingHeader
         */
        protected GreedyRoutingHeader(
                RoutingHeader delagationRoutingHeader) {
            super((DefaultRoutingHeader) delagationRoutingHeader);
            data=(RoutingData)delagationRoutingHeader.getDelegationData();
            //receiverPosition = positionbasedRoutingHeader.getReceiverPosition();
            
        }

        /**
         * TODO Comment method
         * @param operatingService
         * @return
         */
        public RoutingHeader getRecoveryStrategyHeader(RuntimeOperatingSystem operatingService) {
            PositionbasedRoutingHeader header = ((PositionUnicastRoutingAlgorithm_Sync)operatingService.getAccessListenerStub(data.recoveryServiceID,PositionUnicastRoutingAlgorithm_Sync.class))
                .getPositionBasedHeader(this);
            header.setDelegationData(data);
            return header;
        }

        /**
         * TODO Comment method
         * 
         * @return
         */
        public boolean finalDelivery() {
            return data.finalDelivery;
        }
        
        /**
         * TODO Comment method
         * @param finalDelivery
         */
        public void setFinalDelivery(boolean finalDelivery) {
            data.finalDelivery=finalDelivery;
            
        }

        /**
         * TODO Comment method
         * 
         * @param address
         * @param ownLocationData
         * @return
         */
        public boolean destinationReached(Address address,
                LocationData ownLocationData) {
            return address.equals(getReceiver());
        }

        /**
         * 
         * TODO Comment method
         * @param localRoutingMetric
         */
        public void setRoutingMetric(LocalRoutingMetric localRoutingMetric) {
            data.routingMetric = localRoutingMetric;

        }

        /**
         * TODO Comment method
         * 
         * @return
         */
        public boolean hasRoutingMetric() {
            return data.routingMetric != null;
        }
        
        /**
         * @return Returns the routingMetric.
         */
        public LocalRoutingMetric getRoutingMetric() {
            return data.routingMetric;
        }

        /**
         * 
         * TODO Comment method
         * @param recoveryServiceID
         */
        public void setRecoveryStrategy(ServiceID recoveryServiceID) {
            data.recoveryServiceID=recoveryServiceID;
            

        }

        /**
         * TODO Comment method
         * 
         * @return
         */
        public boolean hasRecoveryStrategy() {
            return data.recoveryServiceID!=null;
        }

        /**
         * TODO Comment method
         * 
         * @return
         */
        public boolean useFaceRecovery() {
            return data.useFaceRecovery;
        }
        
        /**
         * TODO Comment method
         * @param useFaceRecovery
         */
        public void setUseFaceRecovery(boolean useFaceRecovery) {
            data.useFaceRecovery=useFaceRecovery;
            
        }




        public ServiceID getRoutingAlgorithmID() {
            return SERVICE_ID;
        }

        public LinkLayerInfo copy() {
            return new GreedyRoutingHeader(this);
        }

        public Shape getShape() {
            return SHAPE;
        }

        public int getCodingSize() {
            return data.getCodingSize()-(8*8)/*count routingserviceID only once*/;
        }


    }


    public static class AnycastGreedyRoutingHeader extends GreedyRoutingHeader implements LocationBasedRoutingHeader{
        
        private GeographicLocation location;

        /**
         * 
         * Constructor for class <code>AnycastGreedyRoutingHeader</code>
         * @param geographicLocation
         * @param countHops
         * @param traceRoute
         */
        public AnycastGreedyRoutingHeader(GeographicLocation geographicLocation, boolean countHops, boolean traceRoute) {
            super(null,null, geographicLocation.getCenterPosition(), countHops, traceRoute);
            this.location=geographicLocation;
        }

        /**
         * Constructor for class <code>AnycastGreedyRoutingHeader</code>
         * @param header
         */
        protected AnycastGreedyRoutingHeader(AnycastGreedyRoutingHeader header) {
            super(header);
            location=header.location;
        }

        /**
         * Constructor for class <code>AnycastGreedyRoutingHeader</code>
         * @param locationBasedRoutingHeader
         */
        public AnycastGreedyRoutingHeader(LocationBasedRoutingHeader locationBasedRoutingHeader) {
            super(locationBasedRoutingHeader);
            location=(GeographicLocation) locationBasedRoutingHeader.getTargetLocation();
        }

        /**
         * Constructor for class <code>AnycastGreedyRoutingHeader</code>
         *
         * @param otherRoutingHeader
         * @param location2
         */
        public AnycastGreedyRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {
            super(otherRoutingHeader);
            location=(GeographicLocation) location;
            
        }

        public Location getTargetLocation() {
            return location;
        }
        
        public boolean destinationReached(Address address, LocationData locationData) {
            return location.isInside(locationData.getPosition());
        }
        
        public boolean finalDelivery() {
            return false;
        }
        
        public RoutingHeader getRecoveryStrategyHeader(RuntimeOperatingSystem operatingSystem) {
            RoutingHeader header=((LocationRoutingAlgorithm_Sync)operatingSystem.getAccessListenerStub(data.recoveryServiceID,LocationRoutingAlgorithm_Sync.class))
                    .getLocationRoutingHeader(this);
            header.setDelegationData(data);
            return header;
        }
        public int getCodingSize() {
         
            return super.getCodingSize()+location.getCodingSize();
        }
        public LinkLayerInfo copy() {
            
            return new AnycastGreedyRoutingHeader(this);
        }
    }
 

}
