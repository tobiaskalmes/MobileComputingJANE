/*****************************************************************************
 * 
 * GeoFloodingRoutingAlgorithm.java
 * 
 * $Id: GeoFloodingRoutingAlgorithm.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.geocast;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.positioning.PositioningService;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

import java.util.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class GeoFloodingRoutingAlgorithm implements RoutingAlgorithm,
        RuntimeService,
        LocationRoutingAlgorithm_Sync,
        DelegationRoutingAlgorithm_Sync{

    public static final ServiceID SERVICE_ID=new EndpointClassID(GeoFloodingRoutingAlgorithm.class.getName());
    
    
    //Constructor parameters
    private ServiceID positioningServiceID;
   
    
    //Initialized by constructor
    private Set messageSet;
    private double MESSAGE_STORE_DELTA = 60;
    private Set targetSet;
    
    
    //Initialized in start()
    private RuntimeOperatingSystem operatingSystem;
    private PositioningService.PositioningServiceStub positioningService;
    //private LinkLayerAddress address;



    
    public static ServiceID createInstance(ServiceUnit serviceUnit){
        return createInstance(serviceUnit,60);
    }

    public static ServiceID createInstance(ServiceUnit serviceUnit,double messageStoreDelta){

        if (!serviceUnit.hasService(PositioningService.class)){
            throw new IllegalStateException("ServiceUnit must contain a PositioningService to create this RoutingAlgorithm");
        }
        ServiceID positining=serviceUnit.getService(PositioningService.class);
        return createInstance(serviceUnit,positining,messageStoreDelta);
        
    }
    
    /**
     * TODO: comment method 
     * @param serviceUnit
     * @param linkLayer
     * @param positining
     * @param messageStoreDelta
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID positining, double messageStoreDelta) {
        return serviceUnit.addService(new GeoFloodingRoutingAlgorithm(positining,messageStoreDelta));
    }
    


   


    /**
     * Constructor for class <code>GeoFloodingRoutingAlgorithm</code>
     *
     * @param positioningServiceID
     * @param linkLayerServiceID
     * @param message_store_delta
     */
    public GeoFloodingRoutingAlgorithm(
            ServiceID positioningServiceID,
            double message_store_delta) {
        super();
        this.positioningServiceID = positioningServiceID;
        MESSAGE_STORE_DELTA = message_store_delta;
        messageSet=new LinkedHashSet();
        targetSet=new LinkedHashSet();
    }
    
    
    public void handleStartRoutingRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        GeoFloodingHeader header=(GeoFloodingHeader)routingHeader;
        if (!header.hasMessageID()){
            throw new IllegalStateException("Header must have a unique message id");
        }
        handleMessageReceivedRequest(handler,header,null);
    }

    //
    public void handleMessageReceivedRequest(RoutingTaskHandler replyHandle,
            RoutingHeader routingHeader, Address sender) {
        final GeoFloodingHeader header=(GeoFloodingHeader)routingHeader;
        final MessageID messageID=header.getMessageID();
        if (!messageSet.contains(messageID)&&
                ((GeographicLocation)header.getTargetLocation()).isInside(positioningService.getPositioningData().getPosition())){
            replyHandle.createOpenTask();
            replyHandle.deliverMessage(header);
            replyHandle.forwardAsBroadcast(routingHeader);
            replyHandle.finishOpenTask();
            
            messageSet.add(messageID);
            targetSet.add(header.getTargetLocation());
            operatingSystem.setTimeout(new ServiceTimeout(MESSAGE_STORE_DELTA) {
                public void handle() {
                    messageSet.remove(messageID);

                }
            });
            
        }else{
            replyHandle.ignoreMessage(header);
            
        }
        
    }
    
    //
    public void handleMessageDelegateRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        //ignore
        //TODO:
        
        handleStartRoutingRequest(handler,routingHeader);

    }

    //
    public void handleUnicastErrorRequest(RoutingTaskHandler replyHandle,
            RoutingHeader header, Address receiver) {
        // ignore no unicast used

    }
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }
    
    //
    public void handleMessageForwardProcessed(RoutingHeader routingHeader) {
        final GeoFloodingHeader header=(GeoFloodingHeader)routingHeader;
        targetSet.remove(header.getTargetLocation());

    }

    //
    public void start(RuntimeOperatingSystem operatingSystem) {
        this.operatingSystem=operatingSystem;
        operatingSystem.registerSignalListener(RoutingAlgorithm.class);
        operatingSystem.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
        operatingSystem.registerAccessListener(DelegationRoutingAlgorithm_Sync.class);
        positioningService=new PositioningService.PositioningServiceStub(operatingSystem,positioningServiceID);
        

    }

    //
    public ServiceID getServiceID() {
        return SERVICE_ID;
    }

    //
    public void finish() {
        // TODO Auto-generated method stub

    }

    //
    public Shape getShape() {
        ShapeCollection shapeCollection=new ShapeCollection();
        Iterator iterator=targetSet.iterator();
        while (iterator.hasNext()) {
            GeographicTarget element = (GeographicTarget) iterator.next();
            shapeCollection.addShape(element.getShape(operatingSystem.getDeviceID(),Color.GREEN,false));
            
        }
        return shapeCollection;
    }

    //
    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub

    }

    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location, ServiceID delegationService) {

        return new GeoFloodingHeader((GeographicLocation)location,false,false);
    }

    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location) {
        return new GeoFloodingHeader((GeographicLocation)location,false,false);
    }
    
    public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header) {
     
        return new GeoFloodingHeader(header,(GeographicLocation) header.getTargetLocation());
    }

    
    //
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {
         return new GeoFloodingHeader(otherRoutingHeader,(GeographicLocation) location);
    }
    
    public RoutingHeader getDelegationRoutingHeader(RoutingHeader routingHeaderWithDelegationData) {
        if (routingHeaderWithDelegationData instanceof LocationBasedRoutingHeader) {
            
            LocationBasedRoutingHeader header = (LocationBasedRoutingHeader) routingHeaderWithDelegationData;
            return new GeoFloodingHeader(header,(GeographicLocation) header.getTargetLocation());
            
        }
        return null;
    }

}
