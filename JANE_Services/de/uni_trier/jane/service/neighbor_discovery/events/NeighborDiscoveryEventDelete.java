/*****************************************************************************
 * 
 * NeighborDiscoveryEventNew.java
 * 
 * $Id: NeighborDiscoveryEventDelete.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;


/**
 * A <code>ServiceEvent</code> propagated by a <code>NeighborDiscoveryService</code> when a neighbor device has been removed. (e.g. left sending range) 
 * 
 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService
 * 
 * Eventtemplate super class:
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEvent
 * 
 * other eventtemplates of the same type
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEvent
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventDelete
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventUpdate
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventNew 
 * 
 * corresponding events:
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEvent
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventUpdate
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventDelete
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventNew
 * 
 * 
 */
public class NeighborDiscoveryEventDelete extends NeighborDiscoveryEvent {

    /**
     * Constructor for class <code>NeighborDiscoveryEventNew</code>
     * @param data 
     */
    public NeighborDiscoveryEventDelete(NeighborDiscoveryData data) {
        super(data);
        
    }
    
    

    /**
     * TemplateConstructor for class <code>NeighborDiscoveryEventDelete</code>
     * @param address   match the NeighbordiscoveryData sender <code>address</code>
     */
    public NeighborDiscoveryEventDelete(Address address) {
        super(address);
    }



    /**
     * TemplateConstructor for class <code>NeighborDiscoveryEvent</code>
     * @param eventSenderID             the service ID of the <code>NeighborDiscovceryService</code> 
     * @param eventSenderClass          the class of the <code>NeighborDiscoveryService</code>
     * @param address                   match the NeighbordiscoveryData sender <code>address</code>
     */
    public NeighborDiscoveryEventDelete(ServiceID eventSenderID,
            Class eventSenderClass, Address address) {
        super(eventSenderID, eventSenderClass, address);
    }

}
