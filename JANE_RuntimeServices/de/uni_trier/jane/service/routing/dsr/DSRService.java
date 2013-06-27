/*****************************************************************************
 * 
 * DSRService.java
 * 
 * $Id: DSRService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
 *  
 * Copyright (C) 2005-2006 Alexander Hoehfeld
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
package de.uni_trier.jane.service.routing.dsr; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.GeographicLocationManager;
import de.uni_trier.jane.service.locationManager.LocationManager_sync;
import de.uni_trier.jane.service.locationManager.basetypes.EllipseGeographicLocation;
import de.uni_trier.jane.service.locationManager.basetypes.Location;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.DefaultRoutingService;
import de.uni_trier.jane.service.routing.RoutingAlgorithm;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.service.routing.RoutingService;
import de.uni_trier.jane.service.routing.RoutingService_sync;
import de.uni_trier.jane.service.routing.RoutingTaskHandler;
import de.uni_trier.jane.service.routing.geocast.LocationFloodingHeader;
import de.uni_trier.jane.service.routing.geocast.LocationFloodingRoutingAlgorithm;
import de.uni_trier.jane.service.routing.greedy.PendingMessageEntry;
import de.uni_trier.jane.service.routing.transport.routecache.Route;
import de.uni_trier.jane.service.routing.transport.routecache.RouteCache;
import de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync;
import de.uni_trier.jane.service.routing.transport.routecache.SimpleRouteCache;
import de.uni_trier.jane.service.routing.unicast.UnicastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * A simple implementation of the DSR (Dynamic Source Routing) algorithm using location based flooding for route discovery
 * This implementation is bidirectional; unidirectional links are (currently) not covered.
 * 
 * TODO: Exponential timeouts for retries if e.g. network is clustered. Timeouts for retries only, if delivery is NOT guaranteed!!!
 * TODO: Implement unidirectional links.
 */
public class DSRService implements RuntimeService, RoutingAlgorithm, UnicastRoutingAlgorithm_Sync
{
    /************************************************************************************************
     * Class Attributes                                                                             *
     ************************************************************************************************/

    /**
     * The <code>RouteCache</code>
     */
    private RouteCache routeCache;
    
    /**
     * Accessor to the synchronized route cache methods
     */
    private RouteCache_sync routeCacheSync;
    
    /**
     * The <code>ServiceID</code> of the <code>RouteCache</code>
     */
    private ServiceID routeCacheID;
    
    /**
     * The route request timout
     */
    private static final double ROUTE_REQUEST_TIMEOUT = 1;

    private static final int ROUTE_REQUEST_RETRIES = 3;
    
    /**
     * The unique <code>ServiceID</code> of the location DSR routing service
     */
    public static ServiceID SERVICE_ID = new EndpointClassID(DSRService.class.getName());
    
    /**
     * The <code>RuntimeOperatingSystem</code>
     */
    protected RuntimeOperatingSystem  runtimeOperatingSystem;
    
    /**
     * A <code>HashMap</code> containing the sent, but pending route requests
     */
    protected HashMap sendBuffer;
    
    /**
     * The <code>RoutingService</code>
     */
    protected RoutingService routingService;
    
    /**
     * The <code>ServiceID</code> of the routing service
     */
    private ServiceID routingServiceID;
    
    /**
     * The target <code>Location</code> beyond which flooding will cease
     */
    private Location targetLocation;
    
    /**
     * Accessor for synchronized calls to the location mananger
     */
    private LocationManager_sync defaultLocationManager;

	private Address ownAddress;

    /************************************************************************************************
     * Constructors and Instantiators                                                               *
     ************************************************************************************************/
    
    /**
     * Creates an instance of <code>SimpleLocationDSR</code>
     * @param serviceUnit the <code>ServiceUnit</code>
     * @param location an example location
     * @return the <code>ServiceID</code> of the service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, Location location)
    {
        if (location != null && !serviceUnit.hasService(location.getLocationManagerClass()))
            location.createLocationManagerService(serviceUnit);
        
        initializeLocationFloodingService(serviceUnit);
        
        ServiceID routeCacheID      = initializeRouteCache      (serviceUnit);
        ServiceID routingServiceID  = initializeRoutingService  (serviceUnit);
        
        return serviceUnit.addService(new DSRService(routingServiceID, routeCacheID, location));
    }

    /**
     * Creates an instance of <code>SimpleLocationDSR</code>
     * @param serviceUnit the <code>ServiceUnit</code>
     * @param unicastService <code>true</code> if the service is an unicast service
     * @return the <code>ServiceID</code> of the service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean unicastService)
    {
        if (unicastService)
        {
            if (!serviceUnit.hasService(GeographicLocationManager.class))
                GeographicLocationManager.createInstance(serviceUnit);
            
            return createInstance(serviceUnit, new EllipseGeographicLocation(new Position(0,0),500));
        }
        
        return createInstance(serviceUnit, null);
    }
    
    /**
     * Creates an instance of <code>SimpleLocationDSR</code>
     * @param serviceUnit the <code>ServiceUnit</code>    
     * @return the <code>ServiceID</code> of the service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit)
    {
        return createInstance(serviceUnit,false);
    }

    /**
     * Constructor for class <code>SimpleLocationDSR</code>
     *
     * @param routingServiceID the <code>ServiceID</code> of the routing service
     * @param targetLocation the <code>Location</code>
     */
    public DSRService(ServiceID routingServiceID, ServiceID routeCacheID, Location targetLocation) 
    {
        setRouteCacheID     (routeCacheID);
        setRoutingServiceID (routingServiceID);
        setTargetLocation   (targetLocation);
        
        sendBuffer        = new HashMap();
    }
    
    /************************************************************************************************
     * Utility Methods                                                                              *
     ************************************************************************************************/
    
    /**
     * If the <code>LocationFloodingRoutingAlgorithm</code> is not available for the provided <code>ServiceUnit</code> create an instance of it
     * @param serviceUnit the <code>ServiceUnit</code>
     * @return the <code>ServiceID</code> of the <code>LocationFloodingRoutingAlgorithm</code>
     */
    private static ServiceID initializeLocationFloodingService(ServiceUnit serviceUnit)
    {
        return (serviceUnit.hasService(LocationFloodingRoutingAlgorithm.class) ? serviceUnit.getService(LocationFloodingRoutingAlgorithm.class) : LocationFloodingRoutingAlgorithm.createInstance(serviceUnit));
    }
    
    /**
     * If no <code>RoutingService</code> is available for the provided <code>ServiceUnit</code> create an instance of the default implementation
     * @param serviceUnit the <code>ServiceUnit</code>
     * @return the <code>ServiceID</code> of the <code>RoutingService</code>
     */
    private static ServiceID initializeRoutingService(ServiceUnit serviceUnit)
    {
    	// TODO: DefaultRoutingService erweitern, damit er den ExtendedLinkLayer nutzt, um AdressedBroadcasts zuzulassen!
    	
        return (serviceUnit.hasService(RoutingService.class) ? serviceUnit.getService(RoutingService.class) : DefaultRoutingService.createInstance(serviceUnit));
    }
    
    /**
     * If no <code>RouteCache</code> is available for the provided <code>ServiceUnit</code> create an instance of the SimpleRouteCache
     * @param serviceUnit the <code>ServiceUnit</code>
     * @return the <code>ServiceID</code> of the used <code>RouteCache</code>
     */
    private static ServiceID initializeRouteCache(ServiceUnit serviceUnit)
    {
        return (serviceUnit.hasService(RouteCache.class) ? serviceUnit.getService(RouteCache.class) : SimpleRouteCache.createInstance(serviceUnit));
    }
    
    /**
     * Checks if the routing header's target location is available and if not, sets it to the current location 
     * @param dsrRoutingHeader the <code>RoutingHeader</code>
     * @return <code>true</code> if the routing headers target location has been set (if it was not already), <code>false</code> otherwise.
     */
    private boolean checkLocation(DSRRoutingHeader dsrRoutingHeader) 
    {
        if (dsrRoutingHeader.getTargetLocation() == null)
        {
            Location location = defaultLocationManager.getCurrentLocation(targetLocation);
            
            if (location == null)
                return false;
            
            dsrRoutingHeader.setTargetLocation(location);
        }
        
        LocationManager_sync locationManager = getLocationManager(dsrRoutingHeader.getTargetLocation());
        
        if (locationManager == null)
            return false;
        
        if (!locationManager.locatedAt(dsrRoutingHeader.getTargetLocation()))
            return false;
        
        return true;
    }
    
    /**
     * Returns an accessor for synchronized calls to the location manager
     * @param location the current <code>Location</code>
     * @return an accessor for synchronized calls to the location manager
     */
    private LocationManager_sync getLocationManager(Location location) 
    {
        final Class locationManagerClass = location.getLocationManagerClass();
        
        if (locationManagerClass.isInstance(defaultLocationManager))
            return defaultLocationManager;
        
        if (!runtimeOperatingSystem.hasService(locationManagerClass))
            return null;
        
        ServiceID locationManagerID = runtimeOperatingSystem.getServiceIDs(locationManagerClass)[0];
        LocationManager_sync locationManager=(LocationManager_sync)runtimeOperatingSystem.getAccessListenerStub(locationManagerID,LocationManager_sync.class);
        return locationManager;
    }
    
    /**
     * Reverses the route provided as argument 
     * @param route the original route list
     * @return the reversed route list
     */
    private List reverseRoute(List route) 
    {
        List reverseRoute = new ArrayList();
        Iterator iterator = route.iterator();
        while (iterator.hasNext()) 
            reverseRoute.add(0, iterator.next());
        
        reverseRoute.remove(0);
        return reverseRoute;
    }
    
    /**
     * Initializes a new route discovery to the receiver of the current message
     * @param handler the <code>RoutingTaskHandler</code>
     * @param routingHeader the <code>DSRRoutingHeader</code>
     */
    private void initializeRouteDiscovery(RoutingTaskHandler handler, DSRRoutingHeader routingHeader)
    {
        Address destination = routingHeader.getReceiver();
        
        RouteDiscoveryEntry entry;
        if (sendBuffer.containsKey(destination))
            entry = (RouteDiscoveryEntry)sendBuffer.get(destination);
        else
        {
            ServiceTimeout timeout = new DSRDiscoveryTimeout(this, destination, ROUTE_REQUEST_TIMEOUT);
             entry = new RouteDiscoveryEntry(timeout);
            sendBuffer.put(destination, entry);
            routingService.startRoutingTask(new LocationFloodingHeader(routingHeader.getTargetLocation(), false, true), new DSRRouteDiscoveryMessage(destination));
            
            runtimeOperatingSystem.setTimeout(timeout);
        }
        entry.add(new PendingMessageEntry(handler, routingHeader,ROUTE_REQUEST_RETRIES));
    }
    
    /**
     * Caches the specified route
     * @param receiver the receiver of the route discovery
     * @param route the <code>Route</code>
     * TODO Cache the sub-routes as well
     */
    private void cacheRoute(Address receiver, Route route)
    {
    	routeCache.addRoute(receiver, route);
    }
    
    /************************************************************************************************
     * Setters & Getters                                                                            *
     ************************************************************************************************/
    
    /**
     * Returns the target location
     * @return the target location
     */
    public Location getTargetLocation()
    {
        return targetLocation;
    }
    
    /**
     * Sets the target location
     * @param targetLocation the target location
     */
    public void setTargetLocation(Location targetLocation)
    {
        this.targetLocation = targetLocation;
    }
    
    /**
     * Returns the <code>ServiceID</code> of the <code>RoutingService</code>
     * @return the <code>ServiceID</code> of the <code>RoutingService</code>
     */
    public ServiceID getRoutingServiceID()
    {
        return routingServiceID;
    }
    
    /**
     * Sets the <code>ServiceID</code> of the <code>RoutingService</code>
     * @param routingServiceID the <code>ServiceID</code> of the <code>RoutingService</code>
     */
    public void setRoutingServiceID(ServiceID routingServiceID)
    {
        this.routingServiceID = routingServiceID;
    }
    
    /**
     * Returns the <code>ServiceID</code> of the <code>RouteCache</code>
     * @return the <code>ServiceID</code> of the <code>RouteCache</code>
     */
    public ServiceID getRouteCacheID()
    {
        return routeCacheID;
    }
    
    /**
     * Sets the <code>ServiceID</code> of the <code>RouteCache</code>
     * @param routeCacheID the <code>ServiceID</code> of the <code>RouteCache</code>
     */
    public void setRouteCacheID(ServiceID routeCacheID)
    {
        this.routeCacheID = routeCacheID;
    }
    
    /**
     * Sets the <code>RuntimeOperatingSystem</code>
     * @param runtimeOperatingSystem the <code>RuntimeOperatingSystem</code> to set
     */
    public void setRuntimeOperatingSystem(RuntimeOperatingSystem runtimeOperatingSystem)
    {
        this.runtimeOperatingSystem = runtimeOperatingSystem;
    }
    
    /**
     * Returns the <code>RuntimeOperatingSystem</code>
     * @return the <code>RuntimeOperatingSystem</code>
     */
    public RuntimeOperatingSystem getRuntimeOperatingSystem()
    {
        return runtimeOperatingSystem;
    }
    
    /************************************************************************************************
     * Implemented or Inherited Methods                                                             *
     ************************************************************************************************/

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.RuntimeService#start(de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) 
    {
        setRuntimeOperatingSystem(runtimeOperatingSystem);
        
        runtimeOperatingSystem.registerSignalListener(RoutingAlgorithm.class);
        if (targetLocation != null)
            runtimeOperatingSystem.registerAccessListener(UnicastRoutingAlgorithm_Sync.class);
       
        routingService = (RoutingService)runtimeOperatingSystem.getSignalListenerStub(routingServiceID, RoutingService.class);
        RoutingService_sync routingServiceSync = (RoutingService_sync)
        	runtimeOperatingSystem.getAccessListenerStub(routingServiceID, RoutingService_sync.class);
        ownAddress=routingServiceSync.getOwnAddress();
        runtimeOperatingSystem.registerAtService(routingServiceID, RoutingService.class);
        if (targetLocation != null)
            defaultLocationManager = getLocationManager(targetLocation);
        
        routeCache       =(RouteCache)       runtimeOperatingSystem.getSignalListenerStub(routeCacheID, RouteCache.class);
        routeCacheSync   =(RouteCache_sync)  runtimeOperatingSystem.getAccessListenerStub(routeCacheID, RouteCache_sync.class);
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
    public void finish() { } 

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
    public void getParameters(Parameters parameters) { } 
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.unicast.UnicastRoutingAlgorithm_Sync#getUnicastHeader(de.uni_trier.jane.basetypes.Address)
     */
    public RoutingHeader getUnicastHeader(Address destination) 
    {
        return new DSRRoutingHeader(destination, null);
    }
    
    /************************************************************************************************
     * Handler Methods                                                                              *
     ************************************************************************************************/

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleStartRoutingRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleStartRoutingRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) 
    {
        final DSRRoutingHeader dsrRoutingHeader = (DSRRoutingHeader) routingHeader;
                
        // drop the request if the current location is out of the target locations bounds or it doesnt have a receiver
        
        if (!checkLocation(dsrRoutingHeader) || !dsrRoutingHeader.hasReceiver())
        {
            handler.dropMessage(dsrRoutingHeader);
            return;
        }
        Route route;
        if (dsrRoutingHeader.hasValidRoute())
        	route = new Route(dsrRoutingHeader.getRoute());
        else
        {
        	route = (Route) routeCacheSync.getRoute(dsrRoutingHeader.getReceiver());
            if (route!=null)
            {
                List list = route.getRoute();
                list.add(0, ownAddress);
                if (dsrRoutingHeader.hasRoute())
                {
                    Route oldRoute = new Route(dsrRoutingHeader.getRoute());
                    route = oldRoute.repair(list);
                }
                else
                    route = new Route(list);
            }
        }
	    
	    if (route == null)
	        initializeRouteDiscovery(handler, dsrRoutingHeader);
	    else
	    {
	        dsrRoutingHeader.setRoute(route.getRoute());
	        handleNextHop(handler, dsrRoutingHeader);
	    }
    }
    
    /**
     * Handle for next hops
     * @param handler the <code>RoutingTaskHandler</code>
     * @param dsrRoutingHeader the <code>RoutingHeader</code>
     */
    protected void handleNextHop(RoutingTaskHandler handler, DSRRoutingHeader dsrRoutingHeader) 
    {
        if (dsrRoutingHeader.hasNextHop())
            handler.forwardAsUnicast(dsrRoutingHeader, dsrRoutingHeader.getNextHop());
        else
            handler.deliverMessage(dsrRoutingHeader);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageReceivedRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.service.network.link_layer.LinkLayerAddress)
     */
    public void handleMessageReceivedRequest(RoutingTaskHandler handler, RoutingHeader header, Address sender) 
    {
        handleNextHop(handler, (DSRRoutingHeader)header);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleUnicastErrorRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.service.network.link_layer.LinkLayerAddress)
     */
    public void handleUnicastErrorRequest(RoutingTaskHandler handler, RoutingHeader header, Address receiver) 
    {
    	DSRRoutingHeader routingHeader = (DSRRoutingHeader) header;
    	
    	Location location = routingHeader.getTargetLocation();

    	List brokenRoute = header.getRoute();
    	
//    	int index = header.getHopCount();
    	
    	List brokenLink = new ArrayList();
    	brokenLink.add(ownAddress);
    	brokenLink.add(receiver);
        int i=brokenRoute.indexOf(ownAddress);
        brokenRoute=brokenRoute.subList(0,i+1);
    	
//    	while(brokenRoute.size() > index + 1)
//    	brokenRoute.remove(index + 1);
        
    	Iterator targets= routeCacheSync.removeBrokenLink(receiver).iterator();
    	
// 		Route route=routeCacheSync.getRoute(receiver);
    	
        while (targets.hasNext())
        {
            targetFailure((Address)targets.next());
        }
        
//    	TODO: lokal link break
        
        if (brokenRoute.size() > 1)
        {
            routingService.startRoutingTask(new DSRRoutingHeader(null, location, reverseRoute(brokenRoute),true,true), new DSRRouteErrorMessage(brokenLink));
        }
        if (routingHeader.isProtocolMessage())
            handler.dropMessage(header);
        else
        {
            routingHeader.invalidRoute();
            handleStartRoutingRequest(handler,header);
        }
    }
    
    /**
     * Handle for the <code>AdvancedRouteErrorMessage</code>
     * Called on the originator node of the message after a route error has been encountered
     * Cleans up the cache of no longer valid links. The send buffer is not affected since
     * for its entries a route discovery is still pending. 
     * @param route the broken route <code>List</code>
     */
    public void handleRouteError(List brokenLink) 
    {
    	Iterator targets = routeCacheSync.removeBrokenLink(new Route(brokenLink)).iterator();
        while (targets.hasNext())
            targetFailure((Address)targets.next());
    }

    /**
     * Called if the target is unreachable 
     * @param address the address of the target
     */
    public void targetFailure(Address target) 
    {
       //TODO was?
        Location location = null;
        if (sendBuffer.containsKey(target))
        {
            RouteDiscoveryEntry entry 	 = (RouteDiscoveryEntry)sendBuffer.get(target);
            Iterator 			iterator = entry.iterator();
            
            while(iterator.hasNext())
            {
                PendingMessageEntry messageEntry = (PendingMessageEntry)iterator.next();
                if (messageEntry.isRetryMessage())
                {
                    messageEntry.retry();
                    location = ((DSRRoutingHeader) messageEntry.getRoutingHeader()).getTargetLocation();
                }
                else
                {
                    messageEntry.getRoutingTaskHandler().dropMessage(messageEntry.getRoutingHeader());
                    iterator.remove();
                }
            }
            if (entry.isEmpty())
                sendBuffer.remove(target);
            else
            {
                routingService.startRoutingTask(new LocationFloodingHeader(location, false, true), new DSRRouteDiscoveryMessage(target));
                runtimeOperatingSystem.removeTimeout(entry.getTimeout());
                ServiceTimeout timeout = new DSRDiscoveryTimeout(this, target, ROUTE_REQUEST_TIMEOUT);
                entry.setTimeout(timeout);
                runtimeOperatingSystem.setTimeout(timeout);
            }
        }
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageForwardProcessed(de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleMessageForwardProcessed(RoutingHeader header) 
    { 
        // ignore
    } 

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handleMessageDelegateRequest(de.uni_trier.jane.service.routing.RoutingTaskHandler, de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handleMessageDelegateRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) 
    {
    	handleStartRoutingRequest(handler,routingHeader);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingAlgorithm#handlePromiscousMessage(de.uni_trier.jane.service.routing.RoutingHeader)
     */
    public void handlePromiscousHeader(RoutingHeader routingHeader) 
    {
    	//??? was mach ich denn hier nu überhaupt?
    	
    	
    	// ignore, we are only intested in promiscuous messages
    	//NO! also routed messages!
        //Das ist komplett falsch. Wenn ich auf A eine Nachricht von B nach C mithöre dann ist die Route 
        // SXXXBCXXXXT. also speichere ich mir BCXXXXXT (bidirectionale Verbindung vorrausgesetzt.
//    	DSRRoutingHeader header=(DSRRoutingHeader)routingHeader;
//    	List route =header.getRoute();
//        Address receiver = (Address)route.get(0);
//        List reverseRoute = reverseRoute(route);
//        
//        routeCache.addRoute(receiver, new Route(reverseRoute));
    	
    }
    
    /**
     * Handle for the <code>AdvancedRouteDiscoveryMessage</code>
     * Called on the destination node of the message after a route here was successfully found
     * @param route the discovered route as <code>List</code>
     * @param location the <code>Location</code>
     */
    public void handleRouteDiscovery(List route, Location location) 
    {
        List reverseRoute = reverseRoute(route);
        // Cache reverse route for bidirectional caching; for unidirectional, a route back to the originator has to be found by another flooding
        cacheRoute((Address)route.get(0), new Route(reverseRoute));
        reverseRoute.add(0,ownAddress);
        routingService.startRoutingTask(new DSRRoutingHeader(null, location, reverseRoute,true,true), new DSRRouteReplyMessage());
    }

    /**
     * Handle for the <code>RouteReplyMessage</code>
     * Called on the originator node of the message after a <code>Route</code> has been successfully discovered
     * @param route the discovered route as <code>List</code>
     * @param replyReceiver 
     */
    public void handleRouteReply(RoutingHeader routeReplyHeader) 
    {
        List 	route		  = routeReplyHeader.getRoute();
        Address replyReceiver = routeReplyHeader.getReceiver();
        Address receiver 	  = (Address) route.get(0);
        List 	reverseRoute  = reverseRoute (route);
        reverseRoute		  = getSubRoute(reverseRoute,routeReplyHeader.getLinkLayerInfo().getSender());
        
        if (reverseRoute.size()>1)
            routeCache.addRoute(receiver, new Route(reverseRoute));
        
        if(replyReceiver.equals(ownAddress))
        {
            reverseRoute.add(0,ownAddress);
            RouteDiscoveryEntry set = (RouteDiscoveryEntry) sendBuffer.remove(receiver);
		    if (set != null)
		    {
                runtimeOperatingSystem.removeTimeout(set.getTimeout());
		        Iterator iterator = set.iterator();
		        while (iterator.hasNext()) 
		        {
		            PendingMessageEntry	element       = (PendingMessageEntry)iterator.next();
		            DSRRoutingHeader 	routingHeader = (DSRRoutingHeader)   element .getRoutingHeader();
		            Route 				newRoute	  = new Route(routingHeader.getRoute()).repair(reverseRoute);
		            routingHeader.setRoute(newRoute.getRoute());
		            handleNextHop(element.getRoutingTaskHandler(), routingHeader);
		        }
		    }
        }
    }

    /**
     * Returns a sub route from the route with the specified neighbor   
     * @param the original route
     * @return the sub route
     */
    private List getSubRoute(List route, Address neighbor) 
    {
        int i = route.indexOf(ownAddress);
        if (i < 0)
        {
            i = route.indexOf(neighbor);
        }
        else
        {
            i ++;
        }
        return route.subList(i,route.size()); 
    }
}
