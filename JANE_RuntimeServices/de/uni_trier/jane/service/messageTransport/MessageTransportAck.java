/*****************************************************************************
 * 
 * MessageTransportAck.java
 * 
 * $Id: MessageTransportAck.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.messageTransport; 

import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.transport.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;


public class MessageTransportAck extends MessageTransportProtocolMessage{
    /**
     * 
     */
    private static final long serialVersionUID = -1055124756704128382L;
    
    
    /**
     * Constructor for class <code>MessageTransportAck</code>
     *
     * @param messageID
     */
    public MessageTransportAck(MessageID messageID) {
        super(messageID);
    }
    
    
    protected void handleInternal(RoutingHeader routingHeader, TransportLayerDataReceiver receiver) {
        receiver.handleAck(routingHeader,this);
        
    }

    public Shape getShape() {
        return new TextShape("ACK",new Rectangle(0,0,5,5),Color.BLUE);
    }
}
