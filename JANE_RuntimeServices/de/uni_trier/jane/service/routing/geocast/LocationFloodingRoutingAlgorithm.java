/*****************************************************************************
 * 
 * GeoFloodingRoutingAlgorithm.java
 * 
 * $Id: LocationFloodingRoutingAlgorithm.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.GeographicTarget;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.ServiceTimeout;
import de.uni_trier.jane.service.locationManager.LocationManager_sync;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.flooding.SimpleFloodingRoutingAlgorithm;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * A simple Flooding Routing Algorithm using geo-information for flood-limiting 
 * @author Daniel Görgen
 **/

public class LocationFloodingRoutingAlgorithm implements RoutingAlgorithm, RuntimeService, LocationRoutingAlgorithm_Sync,DelegationRoutingAlgorithm_Sync
{
    public static final ServiceID SERVICE_ID = new EndpointClassID(LocationFloodingRoutingAlgorithm.class.getName());
    
    //Initialized by constructor
    private Set 	messageSet;
    private double 	messageStoreDelta = 60;
    private Set 	targetSet;
    
    //Initialized in start()
    private RuntimeOperatingSystem operatingSystem;

    /**
     * Creates an instance of the <code>SimpleFloodingRoutingAlgorithm</code> 
     * @param serviceUnit the <code>ServiceUnit</code>
     * @return the serviceID of the service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit)
    {
        return createInstance(serviceUnit, 60);
    }
    
    /**
     * Creates an instance of the <code>SimpleFloodingRoutingAlgorithm</code> 
     * @param serviceUnit the <code>ServiceUnit</code>
     * @param messageStoreDelta the delta of the message store
     * @return the serviceID of the service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, double messageStoreDelta) 
    {
        return serviceUnit.addService(new LocationFloodingRoutingAlgorithm(messageStoreDelta));
    }

    /**
     * Constructor for class <code>GeoFloodingRoutingAlgorithm</code>
     *
     * @param messageStoreDelta
     */
    public LocationFloodingRoutingAlgorithm(double messageStoreDelta) 
    {
        this.messageStoreDelta 	= messageStoreDelta;
        this.messageSet			= new LinkedHashSet();
        this.targetSet			= new LinkedHashSet();
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleStartRoutingRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleStartRoutingRequest(RoutingTaskHandler taskHandler, RoutingHeader routingHeader) 
    {
        handleMessageReceivedRequest(taskHandler, routingHeader,null);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageReceivedRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.basetypes.Address)
     */
    public void handleMessageReceivedRequest(RoutingTaskHandler taskHandler, RoutingHeader routingHeader, Address sender) 
    {
        final LocationFloodingHeader header	   		 = (LocationFloodingHeader)routingHeader;
        final MessageID 			 messageID 		 = header.getMessageID();
        LocationManager_sync         locationManager = getLocationManager(header.getTargetLocation());
        
        if (locationManager == null) 
        	taskHandler.dropMessage(routingHeader);
        
        if (!messageSet.contains(messageID) && locationManager.locatedAt(header.getTargetLocation()))
        {
            if (header.hasDelegationService())
            {
                LocationRoutingAlgorithm_Sync locationRoutingAlgorithm_Sync = (LocationRoutingAlgorithm_Sync)operatingSystem.getAccessListenerStub(header.getDelegationService(),LocationRoutingAlgorithm_Sync.class);
                taskHandler.delegateMessage(locationRoutingAlgorithm_Sync.getLocationRoutingHeader(header),header);
            }
            taskHandler.deliverMessage(header);
            taskHandler.forwardAsBroadcast(routingHeader);
            
            messageSet.add(messageID);
            targetSet .add(header.getTargetLocation());
            
            operatingSystem.setTimeout(new ServiceTimeout(messageStoreDelta) 
            {
                public void handle() 
                {
                    messageSet.remove(messageID);
                }
            });
            
        }
        else
            taskHandler.ignoreMessage(routingHeader);
    }
    
    /**
     * Returns the (synchronized) location manager for the specified location
     * @param location the <code>Location</code>
     * @return the location manager
     */
    private LocationManager_sync getLocationManager(Location location) 
    {
        final Class locationManagerClass = location.getLocationManagerClass();
        
        if (!operatingSystem.hasService(locationManagerClass))
            return null;
        
        ServiceID locationManagerID = operatingSystem.getServiceIDs(locationManagerClass)[0];
        LocationManager_sync locationManager = (LocationManager_sync)operatingSystem.getAccessListenerStub(locationManagerID, LocationManager_sync.class);
        return locationManager;
    }

    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageDelegateRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleMessageDelegateRequest(RoutingTaskHandler taskHandler, RoutingHeader routingHeader) 
    {
        handleStartRoutingRequest(taskHandler, routingHeader);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleUnicastErrorRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.basetypes.Address)
     */
    public void handleUnicastErrorRequest(RoutingTaskHandler taskHandler, RoutingHeader header, Address receiver) 
    {
        // ignore no unicast used
    }
    
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageForwardProcessed(de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleMessageForwardProcessed(RoutingHeader routingHeader) 
    {
        final LocationFloodingHeader header=(LocationFloodingHeader)routingHeader;
        targetSet.remove(header.getTargetLocation());
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.RuntimeService#start(de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem operatingSystem) 
    {
        this.operatingSystem = operatingSystem;
        operatingSystem.registerSignalListener(RoutingAlgorithm.class);
        operatingSystem.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
        operatingSystem.registerAccessListener(DelegationRoutingAlgorithm_Sync.class);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getServiceID()
     */
    public ServiceID getServiceID() 
    {
        return SERVICE_ID;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#finish()
     */
    public void finish() 
    {
    	// ignore
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getShape()
     */
    public Shape getShape() 
    {
        ShapeCollection shapeCollection = new ShapeCollection();
        Iterator iterator = targetSet.iterator();
        while (iterator.hasNext()) 
        {
            GeographicTarget element = (GeographicTarget) iterator.next();
            shapeCollection.addShape(element.getShape(operatingSystem.getDeviceID(), Color.GREEN,false));
        }
        return shapeCollection;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
     */
    public void getParameters(Parameters parameters) 
    {
    	// ignore
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync#getLocationRoutingHeader(de.uni_trier.jane.service.locationManager.basetypes.Location)
     */
    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location) 
    {
        return new LocationFloodingHeader(location,false,false);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync#getLocationRoutingHeader(de.uni_trier.jane.service.routing.LocationBasedRoutingHeader)
     */
    public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header) 
    {
        return new LocationFloodingHeader((DefaultRoutingHeader)header,(GeographicLocation)header.getTargetLocation(),null);
    }
    
    // Werden die folgenden Methoden auch irgendwo verwendet?
    
    /**
     * Returns the <code>LocationBasedRoutingHeader</code> for the current location
     * @param location the current <code>Location</code>
     * @param delegationService the <code>ServiceID</code> of the delegate service
     * @return the <code>LocationBasedRoutingHeader</code>
     */
    public LocationBasedRoutingHeader getLocationRoutingHeader(Location location, ServiceID delegationService) 
    {
        return new LocationFloodingHeader(location, delegationService, false, false);
    }
    
    
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {

        return new LocationFloodingHeader((DefaultRoutingHeader)otherRoutingHeader,(GeographicLocation)location,null);
    }
    
    /**
     * Returns the <code>LocationBasedRoutingHeader</code> derived from the specified header
     * @param header the <code>LocationBasedRoutingHeader</code> where to derive data from
     * @param delegationService the <code>ServiceID</code> of the delegate service
     * @return the <code>LocationBasedRoutingHeader</code>
     */
    public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header, ServiceID delegationService) 
    {
        return new LocationFloodingHeader((DefaultRoutingHeader)header,(GeographicLocation)header.getTargetLocation(),delegationService);
    }

    public RoutingHeader getDelegationRoutingHeader(RoutingHeader routingHeaderWithDelegationData) {
        if (routingHeaderWithDelegationData instanceof LocationBasedRoutingHeader){
            return getLocationRoutingHeader((LocationBasedRoutingHeader)routingHeaderWithDelegationData,(ServiceID)null);
        }
        throw new IllegalStateException("cannot create delegation header");
        //return null;
        
    }
}
 