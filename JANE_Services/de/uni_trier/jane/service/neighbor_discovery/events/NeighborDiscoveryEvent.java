/*****************************************************************************
 * 
 * NeighborDiscoveryDataEvent.java
 * 
 * $Id: NeighborDiscoveryEvent.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;

/**
 * This <code>ServiceEvent</code> class is propagated by a <code>NeighborDiscoveryService</code> 
 * (e.g. @see de.uni_trier.jane.service.neighbor_discovery.ServiceGenericNeighborDiscoveryService)  when a received <code>Data</code> 
 * object from a neighboring device has been changed
 * (<code> NeighborDiscoveryDataEventUpdate</code>), newly received (<code> NeighborDiscoveryDataEventNew</code>) or removed  
 * (<code> NeighborDiscoveryDataEventDelete</code>). 
 * 
 * Uses this template to register for all possible derivations of this event.
 * Use the template constructor to register the event for a specific data ID or sender address. To enable also Data matching, 
 * the reflection depth of the OperatingSystems Eventmanager must be changed (@see SimulationParameters, PlatformParameters).
 */
public class NeighborDiscoveryEvent extends ServiceEvent {

    private Address address;
    private NeighborDiscoveryData data;

    /**
     * 
     * Constructor for class <code>NeighborDiscoveryEvent</code>
     * @param data 
     */
    public NeighborDiscoveryEvent(NeighborDiscoveryData data) {
        super();
        this.address=data.getSender();
        this.data=data;
    }
    
    
    /**
     * 
     * TemplateConstructor for class <code>NeighborDiscoveryEvent</code>
     * @param address   match the NeighbordiscoveryData sender <code>address</code>
     */
    public NeighborDiscoveryEvent(Address address ) {
        this.address=address;
    }

    /**
     * 
     * TemplateConstructor for class <code>NeighborDiscoveryEvent</code>
     * @param eventSenderID             the service ID of the <code>NeighborDiscovceryService</code> 
     * @param eventSenderClass          the class of the <code>NeighborDiscoveryService</code>
     * @param address                   match the NeighbordiscoveryData sender <code>address</code>
     */
    public NeighborDiscoveryEvent(ServiceID eventSenderID,
            Class eventSenderClass,
            Address address) {
        super(eventSenderID, eventSenderClass);
        this.address=address;
    }
    
    /**
     * @return Returns the address.
     */
    public Address getNeighborAddress() {
        return address;
    }
    
    /**
     * @return Returns the data.
     */
    public NeighborDiscoveryData getNeighborDiscoveryData() {
        return data;
    }
    

    
    
    
    
}
