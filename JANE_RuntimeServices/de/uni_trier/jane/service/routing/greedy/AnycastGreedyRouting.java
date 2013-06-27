/*****************************************************************************
 * 
 * GreedyRouting.java
 * 
 * $Id: AnycastGreedyRouting.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.neighbor_discovery.filter.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.*;
import de.uni_trier.jane.service.routing.greedy.metric.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * This class implements a traditional greedy routing startegy based on 
 * position information of all one-hop neighbors.
 *  @deprecated use @see GreedyRoutingImplementation
 */
public class AnycastGreedyRouting implements RuntimeService, 
	RoutingAlgorithm,
    LocationRoutingAlgorithm_Sync,
	PositioningListener{

  
	public static final ServiceID SERVICE_ID = new EndpointClassID(AnycastGreedyRouting.class.getName());
	
    // initialized in constructor
    private ServiceID neighborDiscoveryServiceID;
    private ServiceID recoveryServiceID;
    
    
    // initialized on startup
    private Address address;
    private RuntimeOperatingSystem operatingService;
    private NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;

    // updated periodically
    private PositioningData locationSystemInfo;

	private ServiceID linkLayerServiceID;

	private LocalRoutingMetric weightFunction;

    private LocationRoutingAlgorithm_Sync recoveryStrategy;

    private ServiceID positioningServiceID;

    private Shape lastLocation;

    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @return
     * @deprecated use @see GreedyRoutingImplementation
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
    	return createInstance(serviceUnit,(ServiceID)null);
    }
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param metricInterface
     * @return
     * @deprecated use @see GreedyRoutingImplementation
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, LocalRoutingMetric metricInterface) {
        return createInstance(serviceUnit,null,metricInterface);
    }
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param recoveryServiceID
     * @deprecated use @see GreedyRoutingImplementation
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit,ServiceID recoveryServiceID){
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
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param recoveryServiceID
     * @param metricInterface
     * @deprecated use @see GreedyRoutingImplementation
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID recoveryServiceID,LocalRoutingMetric metricInterface) {
    	ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
    	
    	
    	if(!serviceUnit.hasService(RoutingService.class)) {
    		DefaultRoutingService.createInstance(serviceUnit);
    	}
    	
    	
    	if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryService = serviceUnit.getService(NeighborDiscoveryService_sync.class);
    	
    	if(!serviceUnit.hasService(LocationDataDisseminationService.class)) {
    		LocationDataDisseminationService.createInstance(serviceUnit);
    	}
        ServiceID positioningServiceID = serviceUnit.getService(PositioningService.class);
    	Service greedyRouting = new AnycastGreedyRouting(
    			linkLayerService,
				positioningServiceID,
				neighborDiscoveryService, recoveryServiceID,metricInterface);
		return serviceUnit.addService(greedyRouting);
    }
    
    /**
     * 
     * Constructor for class <code>AnycastGreedyRouting</code>
     * @param linkLayerServiceID
     * @param positioningServiceID
     * @param neighborDiscoveryServiceID
     * @deprecated use @see GreedyRoutingImplementation
     */
    public AnycastGreedyRouting(ServiceID linkLayerServiceID,ServiceID positioningServiceID, ServiceID neighborDiscoveryServiceID) {
    	this(linkLayerServiceID, positioningServiceID, neighborDiscoveryServiceID, null,null);
    }

    /**
     * 
     * Constructor for class <code>AnycastGreedyRouting</code>
     * @param linkLayerServiceID
     * @param neighborDiscoveryServiceID
     * @param recoveryServiceID
     * @param metricInterface
     * @deprecated use @see GreedyRoutingImplementation
     */
    public AnycastGreedyRouting(ServiceID linkLayerServiceID, ServiceID positioningServiceID,ServiceID neighborDiscoveryServiceID, 
            ServiceID recoveryServiceID,LocalRoutingMetric metricInterface) {
    	
    	this.linkLayerServiceID=linkLayerServiceID;
    	this.positioningServiceID=positioningServiceID;
    	
    	this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
    	this.recoveryServiceID = recoveryServiceID;
    	this.weightFunction=metricInterface;
        
    }


	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

    public void start(RuntimeOperatingSystem operatingSystem) {
    	this.operatingService = operatingSystem;
    	operatingSystem.registerSignalListener(RoutingAlgorithm.class);
        
    	operatingService.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
        operatingSystem.registerAtService(positioningServiceID,PositioningService.class);
    	neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(operatingSystem, neighborDiscoveryServiceID);
    	if(!neighborDiscoveryServiceStub.getNeighborDiscoveryProperties().isNotifyAboutOwnChanges()) {
    		throw new ServiceException("Greedy routing requires own data to be stored in the neighbor discovery service.");
    	}
        neighborDiscoveryServiceStub.addUnicastErrorProvider(operatingSystem.getServiceIDs(RoutingService.class)[0]);
    	LinkLayer.LinkLayerStub facade=new LinkLayer.LinkLayerStub(operatingSystem,linkLayerServiceID);
    	address=facade.getLinkLayerProperties().getLinkLayerAddress();
        locationSystemInfo=((PositioningService)operatingService
                .getAccessListenerStub(positioningServiceID,PositioningService.class))
                    .getPositioningData();
    	if (recoveryServiceID!=null){
    	    operatingSystem.setTimeout(new ServiceTimeout(0) {
    	        public void handle() {
    	            recoveryStrategy=(LocationRoutingAlgorithm_Sync)operatingService.
    	                getAccessListenerStub(recoveryServiceID,LocationRoutingAlgorithm_Sync.class);
              
            }
    	    });
        }
    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        return lastLocation;
    }

	public void getParameters(Parameters parameters) {
		parameters.addParameter("metric", weightFunction.toString());
		// TODO: Greedy-RoutingTyp bekannt geben.
	}


    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }
	
    //
    public void handleStartRoutingRequest(RoutingTaskHandler handle,
            RoutingHeader routingHeader) {
        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)routingHeader;

        handleNextHop(handle, greedyRoutingHeader);
        

    }

    public void handleMessageReceivedRequest(RoutingTaskHandler replyHandle, RoutingHeader header, Address sender) {
        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)header;
        handleNextHop(replyHandle, greedyRoutingHeader);
    }

    public void handleUnicastErrorRequest(RoutingTaskHandler replyHandle, RoutingHeader header, Address receiver) {
        lastLocation=null;

        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)header;
        handleNextHop(replyHandle, greedyRoutingHeader);

    }
    
    //
    public void handleMessageForwardProcessed(RoutingHeader header) {
        lastLocation=null;

    }


    //
    public void handleMessageDelegateRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        GreedyRoutingHeader greedyRoutingHeader = (GreedyRoutingHeader)routingHeader;
        handleNextHop(handler, greedyRoutingHeader);

    }




	public void updatePositioningData(PositioningData info) {
        locationSystemInfo = info;
	}

	public void removePositioningData() {
        locationSystemInfo = null;
	}

    // This method creates the next routing action, which can be delivering, dropping, or
    // unicast forwarding.
    private void handleNextHop(RoutingTaskHandler replyHandle, GreedyRoutingHeader greedyRoutingHeader) {
    	
    	
         GeographicLocation location=(GeographicLocation)greedyRoutingHeader.getTargetLocation();
         
        // the message arrived at its destination
        if(location.isInside(locationSystemInfo.getPosition())) {
            if (greedyRoutingHeader.hasDelegationData()){
                DelegationRoutingAlgorithm_Sync delegateService=(DelegationRoutingAlgorithm_Sync)
                    operatingService.getAccessListenerStub(greedyRoutingHeader.getDelegationData().getDelegationServiceID(),DelegationRoutingAlgorithm_Sync.class);
                replyHandle.delegateMessage(delegateService.getDelegationRoutingHeader(greedyRoutingHeader),greedyRoutingHeader);
            }else{
                replyHandle.deliverMessage(greedyRoutingHeader);
            }
            return;
        }

        // determine own location information from the neighbor discovery service
        Address nextHopAddress = null;
        LocationData ownLocationData = (LocationData)neighborDiscoveryServiceStub.
			getNeighborDiscoveryData(address).getDataMap().getData(LocationData.DATA_ID);
        
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
	            double distance = weightFunction.calculate(ownPosition, destinationPosition, neighborPosition); //neighborPosition.distance(destinationPosition);
	            if(distance < currentDistance) {
	            	nextHopAddress = neighbors[i].getSender(); // TODO: cast nur zum testen
	            	currentDistance = distance;
	            }
	        }
	        
        }
        
        // perform forwarding or dropping action
        if(nextHopAddress != null) {
            lastLocation=location.getShape(null,Color.GREEN,false);
            replyHandle.forwardAsUnicast(greedyRoutingHeader,nextHopAddress);
            
        }
        else {
            
            // we have a recovery strategy and will pass the message handling to it
            if(recoveryServiceID != null) {
                  replyHandle.delegateMessage(
                          recoveryStrategy.getLocationRoutingHeader(
                                  greedyRoutingHeader
                          ),
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

    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location){   
         return new GreedyRoutingHeader((GeographicLocation)location,false,false);
    }
    
//    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location,ServiceID delegationService){   
//        return new GreedyRoutingHeader((GeographicLocation)location,delegationService,false,false);
//    }
    
    
    public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header) {
        
        return new GreedyRoutingHeader(header);
    }
    

        
    
        
        
        
        

    // the routing header containing of the destination address and position
    public static class GreedyRoutingHeader extends DefaultRoutingHeader implements LocationBasedRoutingHeader,PositionbasedRoutingHeader {

        private static final Shape SHAPE = new EllipseShape(Position.NULL_POSITION, new Extent(4, 4), Color.BLUE, true);


        private GeographicLocation location;
        
        


 
        
        /**
         * Constructor for class <code>GreedyRoutingHeader</code>
         * @param location
         * @param countHops
         * @param traceRoute
         */
        public GreedyRoutingHeader(GeographicLocation location,  boolean countHops, boolean traceRoute) {
            super(null,null, countHops, traceRoute);
            this.location=location;
            setReceiverPosition(location.getCenterPosition());
            
        }
        
        /**
         * 
         * Constructor for class <code>GreedyRoutingHeader</code>
         * @param header
         * @param location
         */
        public GreedyRoutingHeader(DefaultRoutingHeader header, GeographicLocation location) {
            super(header);
            this.location=location;
        }

        /**
         * Constructor for class <code>GreedyRoutingHeader</code>
         * @param header
         */
        protected GreedyRoutingHeader(GreedyRoutingHeader header) {
            super(header);
            location=header.location;
            
        }
        
        public GreedyRoutingHeader(LocationBasedRoutingHeader header) {
            super((DefaultRoutingHeader)header);
            location=(GeographicLocation)header.getTargetLocation();
            
        }

        public Location getTargetLocation() {         
            return location;
        }


 



//        public Position getDestinationPosition() {
//            return location.getCenterPosition();
//        }




        public int getCodingSize() {
            int size=location.getCodingSize();

            return size;
        }



        public ServiceID getRoutingAlgorithmID() {
            return SERVICE_ID;
        }

        public LinkLayerInfo copy() {
            return this;
        }



        public Shape getShape() {
            return SHAPE;
        }
        

        

    }
    
    //
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {
        // TODO Auto-generated method stub
        return null;
    }

}
