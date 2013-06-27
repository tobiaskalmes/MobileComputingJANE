/*****************************************************************************
 * 
 * GeoFloodingRoutingAlgorithm.java
 * 
 * $Id: SimpleFloodingRoutingAlgorithm.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.flooding;

import java.util.LinkedHashSet;
import java.util.Set;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.ServiceTimeout;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.MessageID;
import de.uni_trier.jane.service.routing.RoutingAlgorithm;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.service.routing.RoutingTaskHandler;
import de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * A Routing Algorithm using Simple Flooding  
 * @author Daniel Görgen
 **/

public class SimpleFloodingRoutingAlgorithm implements RoutingAlgorithm, RuntimeService,FloodingRoutingAlgorithm_sync{

    public static final ServiceID SERVICE_ID = new EndpointClassID(SimpleFloodingRoutingAlgorithm.class.getName());
    
    //Initialized by constructor
    private Set 	messageSet;
    private double 	messageStoreDelta = 60;
    
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
        return serviceUnit.addService(new SimpleFloodingRoutingAlgorithm(messageStoreDelta));
    }

    /**
     * Constructor for class <code>GeoFloodingRoutingAlgorithm</code>
     * @param messageStoreDelta the initial delta of the message store
     */
    public SimpleFloodingRoutingAlgorithm(double messageStoreDelta) 
    {
        super();

        this.messageStoreDelta 	= messageStoreDelta;
        this.messageSet			= new LinkedHashSet();
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleStartRoutingRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleStartRoutingRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) 
    {
        handleMessageReceivedRequest(handler, routingHeader, null);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageReceivedRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.basetypes.Address)
     */
    public void handleMessageReceivedRequest(RoutingTaskHandler taskHandler, RoutingHeader routingHeader, Address sender) 
    {
        final MessageID messageID = routingHeader.getMessageID();
        
        if (!messageSet.contains(messageID))
        {
            taskHandler.createOpenTask();

            taskHandler.deliverMessage(routingHeader);
            taskHandler.forwardAsBroadcast(routingHeader);
            
            taskHandler.finishOpenTask();
            
            messageSet.add(messageID);
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
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageDelegateRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleMessageDelegateRequest(RoutingTaskHandler taskHandler, RoutingHeader routingHeader) 
    {
        handleStartRoutingRequest(taskHandler, routingHeader);
    }

    //
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleUnicastErrorRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.basetypes.Address)
     */
    public void handleUnicastErrorRequest(RoutingTaskHandler taskHandler, RoutingHeader header, Address receiver) 
    {
        // ignore no unicast used
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageForwardProcessed(de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleMessageForwardProcessed(RoutingHeader routingHeader) 
    {
        //ignore
    }
    
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.RuntimeService#start(de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem operatingSystem) 
    {
        this.operatingSystem = operatingSystem;
        operatingSystem.registerSignalListener(RoutingAlgorithm.class);
        operatingSystem.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
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
        return null;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
     */
    public void getParameters(Parameters parameters) 
    {
    	// ignore
    }
    
    public RoutingHeader getRoutingHeader(int maxHopCount) 
    {
        SimpleFloodingRoutingHeader routingHeader = new SimpleFloodingRoutingHeader();
        routingHeader.setMaxHops(maxHopCount);
        return routingHeader;
    }

    
    

}
