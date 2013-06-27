/*****************************************************************************
 * 
 * MessageTransportProtocolMessage.java
 * 
 * $Id: MessageTransportProtocolMessage.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.transport;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

public abstract class MessageTransportProtocolMessage implements RoutingData {

    private MessageID messageID;
    
    
    /**
     * 
     * Constructor for class <code>MessageTransportProtocolMessage</code>
     * @param messageID
     */
    public MessageTransportProtocolMessage(MessageID messageID) {
        this.messageID=messageID;
    }


    public void handle(RoutingHeader routingHeader, SignalListener signalListener) {
        handleInternal(routingHeader,(TransportLayerDataReceiver)signalListener);
        
    }
    
    protected abstract void handleInternal(RoutingHeader routingHeader, TransportLayerDataReceiver receiver);


    public Dispatchable copy() {
        return this;
    }

    public Class getReceiverServiceClass() {
        return TransportLayerDataReceiver.class;
    }

    public int getSize() {
     
        return messageID.getCodingSize();
    }

    
    public MessageID getMessageID() {
        return messageID;
    }

}
