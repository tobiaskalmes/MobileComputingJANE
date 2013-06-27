/*****************************************************************************
 * 
 * NetworkNeighborManagerImpl.java
 * 
 * $Id: NetworkNeighborManagerImpl.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer.positionBased; 

import java.net.InetAddress;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationData;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationDataDisseminationService;
import de.uni_trier.jane.service.network.link_layer.PlatformLinkLayerAddress;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.positioning.PositioningData;
import de.uni_trier.jane.service.positioning.PositioningListener;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class NetworkNeighborManagerImpl implements NetworkNeighborManager, RuntimeService, PositioningListener {

    
    
    public static final ServiceID SERVICE_ID = new EndpointClassID(NetworkNeighborManagerImpl.class.getName());



    public static ServiceID createInstance(ServiceUnit serviceUnit,double virtualSendingRadius){
        ServiceID neighborDiscoveryService;
        if (serviceUnit.hasService(NeighborDiscoveryService_sync.class)){
            neighborDiscoveryService=serviceUnit.getService(NeighborDiscoveryService_sync.class);
        }else{
            neighborDiscoveryService=OneHopNeighborDiscoveryService.createInstance(serviceUnit);
        }
        
        if(!serviceUnit.hasService(LocationDataDisseminationService.class)) {
            LocationDataDisseminationService.createInstance(serviceUnit);
        }
        return serviceUnit.addService(new NetworkNeighborManagerImpl(neighborDiscoveryService,virtualSendingRadius));
    }
    
    public static ServiceID createInstance(ServiceUnit serviceUnit){
        return createInstance(serviceUnit,25);
    }
    
    
    
    private NeighborDiscoveryService_sync neighborDiscoveryService;
    private Position myPosition;
    private double virtualSendingRadius;
    private ListenerID neighborDiscoveryServiceID;

    
    
    /**
     * Constructor for class <code>NetworkNeighborManagerImpl</code>
     * @param neighborDiscoveryServiceID
     * @param virtualSendingRadius
     */
    public NetworkNeighborManagerImpl(ListenerID neighborDiscoveryServiceID, double virtualSendingRadius) {
        this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
        this.virtualSendingRadius = virtualSendingRadius;
    }
    
    public boolean isInReach(InetAddress address) {
        Address otherDevice=new PlatformLinkLayerAddress(address);
        if (myPosition!=null&&neighborDiscoveryService.hasNeighborDiscoveryData(otherDevice)){
            LocationData locationData=(LocationData)neighborDiscoveryService.getData(otherDevice,LocationData.DATA_ID);
            if (locationData!=null){
                if (locationData.getPosition().distance(myPosition)< virtualSendingRadius){
                    return true;
                }
            }
        }
        return false;
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        runtimeOperatingSystem.registerAccessListener(NetworkNeighborManager.class);
        neighborDiscoveryService=(NeighborDiscoveryService_sync)runtimeOperatingSystem.getAccessListenerStub(neighborDiscoveryServiceID, NeighborDiscoveryService_sync.class);
        
    }
    
    

    public void setVirtualSendingRadius(double virtualSendingRadius) {
        this.virtualSendingRadius = virtualSendingRadius;
    }

    public ServiceID getServiceID() {
        return SERVICE_ID;
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

    public void updatePositioningData(PositioningData info) {
        myPosition=info.getPosition();
        
    }

    public void removePositioningData() {
        // TODO Auto-generated method stub
        
    }





}
