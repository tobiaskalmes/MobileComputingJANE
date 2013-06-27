/*****************************************************************************
 * 
 * MessageReceiveEvent.java
 * 
 * $Id: MessageReceiveEvent.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.events;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.routing.DefaultRoutingHeader;

public class MessageReceiveEvent extends ServiceEvent {

    private DefaultRoutingHeader header;

    public MessageReceiveEvent(DefaultRoutingHeader header) {
        this.header=header;
    }

    /**
     * TemplateConstructor for class <code>MessageReceiveEvent</code>
     *
     * @param eventSenderID
     * @param eventSenderClass
     */
    public MessageReceiveEvent(ServiceID eventSenderID, Class eventSenderClass) {
        super(eventSenderID, eventSenderClass);
    }
    
    public DefaultRoutingHeader getRoutingHeader() {
        return header;
    }
    
    

}
