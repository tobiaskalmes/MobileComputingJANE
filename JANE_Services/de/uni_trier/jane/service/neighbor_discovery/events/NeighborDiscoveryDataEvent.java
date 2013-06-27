/*****************************************************************************
 * 
 * NeighborDiscoveryDataEvent.java
 * 
 * $Id: NeighborDiscoveryDataEvent.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
import de.uni_trier.jane.service.event.ServiceEvent;

/**
 * This <code>ServiceEvent</code> class is propagated by a <code>NeighborDiscoveryService</code> 
 * (e.g. @see de.uni_trier.jane.service.neighbor_discovery.ServiceGenericNeighborDiscoveryService)  when a received <code>Data</code> has been changed
 * (<code> NeighborDiscoveryDataEventUpdate</code>), newly received (<code> NeighborDiscoveryDataEventNew</code>) or removed  
 * (<code> NeighborDiscoveryDataEventDelete</code>). 
 * 
 * Use the template constructure to register the event for a specific data ID or sender address. To enable also Data matching, 
 * the reflection depth of the OperatingSystems Eventmanager must be changed (@see SimulationParameters, PlatformParameters).
 */
public class NeighborDiscoveryDataEvent extends ServiceEvent {


    
    private DataID dataID;
    protected Data data;
    protected Address sender;

    /**
     * SenderConstructor for class <code>NeighborDiscoveryDataEvent</code>
     * @param sender 
     * @param data 
     */
    protected NeighborDiscoveryDataEvent(Data data, Address sender) {
        super();
        dataID=data.getDataID();
        this.data=data;
        this.sender=sender;
    }

    /**
     * TemplateConstructor for class <code>NeighborDiscoveryDataEvent</code>
     * @param eventSenderID         the serviceID of the neighbor discovery service
     * @param eventSenderClass      the class of the neighbordiscovery service
     * @param dataID                the data ID of the corresponding <code>Data</code>
     * @param sender                the <code>Address</code> of the device propagating the corresponding data
     */
    public NeighborDiscoveryDataEvent(ServiceID eventSenderID,
            Class eventSenderClass,
            DataID dataID, Address sender) {
        super(eventSenderID, eventSenderClass);
        this.dataID=dataID;
        this.sender=sender;
    }
    
    /**
     * TemplateConstructor for class <code>NeighborDiscoveryDataEvent</code>
     * @param dataID                the data ID of the corresponding <code>Data</code>
     * @param sender                the <code>Address</code> of the device propagating the corresponding data
     */
    public NeighborDiscoveryDataEvent(DataID dataID, Address sender) {
        this.dataID=dataID;
        this.sender=sender;

    }
    
    /**
     * @return Returns the data.
     */
    public Data getData() {
        return data;
    }
    
    /**
     * @return Returns the sender.
     */
    public Address getNeighborAddress() {
        return sender;
    }
    

}
