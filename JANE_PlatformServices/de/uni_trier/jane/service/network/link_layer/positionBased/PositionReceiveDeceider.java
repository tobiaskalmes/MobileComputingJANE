/*****************************************************************************
 * 
 * PositionReceiveDeceider.java
 * 
 * $Id: PositionReceiveDeceider.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.network.link_layer.ReceiveDecider;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.positioning.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class PositionReceiveDeceider implements ReceiveDecider, PositioningListener {

    private Position myPosition;
    private double radius;
    private PositionBasedDataSerializer dataSerializer;
    private ServiceID positioningService;
    
    
    /**
     * Constructor for class <code>PositionReceiveDeceider</code>
     * @param serializer
     * @param service
     * @param radius
     */
    public PositionReceiveDeceider(PositionBasedDataSerializer serializer, double radius) {
        dataSerializer = serializer;
        
        this.radius = radius;
    }

    public boolean receive(InetAddress sender, byte[] data) {
        Position position=dataSerializer.getPosition(data);
        return position.distance(myPosition)<radius;
    }
    
    public void init(RuntimeOperatingSystem operatingSystem){
        ServiceID positioningService=operatingSystem.getServiceIDs(PositioningService.class)[0];
        operatingSystem.registerAtService(positioningService,this,PositioningService.class);
        PositioningService service=(PositioningService) operatingSystem.getAccessListenerStub(positioningService,PositioningService.class);
        updatePositioningData(service.getPositioningData());
        
    }

    public void updatePositioningData(PositioningData info) {
        myPosition=info.getPosition();
        dataSerializer.setPosition(myPosition);
        
    }

    public void removePositioningData() {
        // TODO Auto-generated method stub
        
    }
    



}
