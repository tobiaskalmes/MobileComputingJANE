/*****************************************************************************
 * 
 * NeighborDiscoveryDataEventNew.java
 * 
 * $Id: NeighborDiscoveryDataEventNew.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.neighbor_discovery.events; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * A <code>ServiceEvent</code> propagated by a <code>NeighborDiscoveryService</code> when a <code>Data</code> object 
 * has been received for the first time from a neighbor device. 
 * 
 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService
 * 
 * Eventtemplate super class:
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEvent
 * 
 * other eventtemplates of the same type 
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventUpdate
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventDelete
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventNew
 * 
 * corresponding events:
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEvent
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventDelete
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventUpdate
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventNew
 */
public class NeighborDiscoveryDataEventNew extends NeighborDiscoveryDataEvent {

    /**
     * Constructor for class <code>NeighborDiscoveryDataEventNew</code>
     * @param data
     * @param sender
     */
    public NeighborDiscoveryDataEventNew(Data data, Address sender) {
        super(data, sender);
    }

    /**
     * TemplateConstructor for class <code>NeighborDiscoveryDataEventNew</code>
     * @param eventSenderID         the serviceID of the neighbor discovery service
     * @param eventSenderClass      the class of the neighbordiscovery service
     * @param dataID                the data ID of the corresponding <code>Data</code>
     * @param sender                the <code>Address</code> of the device propagating the corresponding data
     */
    public NeighborDiscoveryDataEventNew(ServiceID eventSenderID,
            Class eventSenderClass, DataID dataID, Address sender) {
        super(eventSenderID, eventSenderClass, dataID, sender);
    }

    /**
     * TemplateConstructor for class <code>NeighborDiscoveryDataEventNew</code>
     * @param dataID                the data ID of the corresponding <code>Data</code>
     * @param sender                the <code>Address</code> of the device propagating the corresponding data
     */
    public NeighborDiscoveryDataEventNew(DataID dataID, Address sender) {
        super(dataID, sender);
    }
    
    public Class getReceiverServiceClass() {
        return NeighborDiscoveryDataEventNewHandler.class;
    }
    
   public void handle(SignalListener listener) {
       ((NeighborDiscoveryDataEventNewHandler)listener).handleNewData(data,sender);
   }
    
    

}
