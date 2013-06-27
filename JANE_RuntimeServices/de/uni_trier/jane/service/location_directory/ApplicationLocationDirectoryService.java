/*****************************************************************************
 * 
 * ApplicationLocationDirectoryService.java
 * 
 * $Id: ApplicationLocationDirectoryService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.location_directory;


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.beaconing.DataMap;
import de.uni_trier.jane.service.event.EventListener;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationData;
import de.uni_trier.jane.service.network.link_layer.SimulationLinkLayerAddress;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.DefaultRoutingHeader;
import de.uni_trier.jane.service.routing.DefaultRoutingService;
import de.uni_trier.jane.service.routing.RoutingService;
import de.uni_trier.jane.service.routing.events.MessageReceiveEvent;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

public class ApplicationLocationDirectoryService implements
         RuntimeService, LocationDirectoryCache {

    
    protected static final double DEFAULT_CACHING_DELTA = 60;
    
    private TimeoutCache cache;

    private RuntimeOperatingSystem operatingSystem;

    private ServiceID routingService;
    
    public static ServiceID createInstance(ServiceUnit serviceUnit){
        ServiceID routingService;
        if (serviceUnit.hasService(RoutingService.class)){
            routingService=serviceUnit.getService(RoutingService.class);
        }else{
            routingService=DefaultRoutingService.createInstance(serviceUnit);
        }
        return serviceUnit.addService(new ApplicationLocationDirectoryService(routingService));
    }
    
    
    
    /**
     * Constructor for class <code>ApplicationLocationDirectoryService</code>
     *
     * @param service
     */
    public ApplicationLocationDirectoryService(ServiceID service) {
        routingService = service;
        cache=new TimeoutCache(1,50);
    }



    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.location_directory.LocationDirectoryCache#addLocationDirectoryEntry(de.uni_trier.jane.basetypes.Address, de.uni_trier.jane.basetypes.Position, double)
     */
    public void addLocationDirectoryEntry(Address address, Position position, double cachingDelta){


        cache.map(address,position,cachingDelta);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.location_directory.LocationDirectoryCache#requestLocationDirectoryEntry(de.uni_trier.jane.basetypes.Address, de.uni_trier.jane.basetypes.ListenerID, double)
     */
    public void requestLocationDirectoryEntry(Address address,
            ListenerID listener, double timeout) {
        requestLocationDirectoryEntry(address,listener);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.location_directory.LocationDirectoryCache#requestLocationDirectoryEntry(de.uni_trier.jane.basetypes.Address, de.uni_trier.jane.basetypes.ListenerID)
     */
    public void requestLocationDirectoryEntry(Address address,
            ListenerID listener) {
        LocationDirectoryEntry info;
        if (cache.hasKey(address)){
            info=new LocationDirectoryEntry(address,(Position)cache.get(address));
            
        }else{
            info=new LocationDirectoryEntry(address,null);
        }
        operatingSystem.sendSignal(listener,
                new LocationDirectoryEntryReplyHandler.Reply(info));

    }



    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        operatingSystem.registerSignalListener(LocationDirectoryCache.class);
        cache.init(runtimeOperatingSystem);
        operatingSystem.registerEventListener(new MessageReceiveEvent(routingService,null), 
                new EventListener() {
                
                    public void handle(ServiceEvent event) {
                        DefaultRoutingHeader header = ((MessageReceiveEvent)event).getRoutingHeader();
                        if (header.hasSenderPosition()){
                            addLocationDirectoryEntry(header.getSender(),header.getSenderPosition(),DEFAULT_CACHING_DELTA);
                        }
                
                    }
                
                });
        
        if (operatingSystem.hasService(NeighborDiscoveryService.class)){
            ServiceID[] serviceIDs=operatingSystem.getServiceIDs(NeighborDiscoveryService.class);
            for (int i=0;i<serviceIDs.length;i++){
                operatingSystem.registerAtService(serviceIDs[i], new NeighborDiscoveryListener() {
                
                    public void removeNeighborData(Address linkLayerAddress) {
                        //ignore
                
                    }
                
                    public void updateNeighborData(NeighborDiscoveryData neighborData) {
                        setNeighborData(neighborData);
                
                    }
                
                    public void setNeighborData(NeighborDiscoveryData neighborData) {
                        DataMap dataMap=neighborData.getDataMap();
                        if (dataMap.hasData(LocationData.DATA_ID)){
                            LocationData data=(LocationData)dataMap.getData(LocationData.DATA_ID);
                            addLocationDirectoryEntry(neighborData.getSender(),data.getPosition(),DEFAULT_CACHING_DELTA);
                        }
                
                    }
                
                }, NeighborDiscoveryService.class);
            }
        }
        
    }

    public ServiceID getServiceID() {
        // TODO Auto-generated method stub
        return null;
    }

    public void finish() {
        // TODO Auto-generated method stub
        
    }

    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub
        
    }
}
