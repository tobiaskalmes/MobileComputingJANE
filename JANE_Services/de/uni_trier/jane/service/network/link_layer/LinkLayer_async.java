/*****************************************************************************
 * 
 * LinkLayer_sync.java
 * 
 * $Id: LinkLayer_async.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer; 



import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author goergen
 *
 * TODO comment class
 */
public interface LinkLayer_async extends SignalListener {

    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver
     * @param receiver  the network <code>Address</code> of the receiver
     * @param message   the message to send
     *
     */
    public void sendUnicast(
            Address receiver,
            LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver and
     * send the message delivery status asynchrone to the callback handler 
     * @param receiver  the <code>LinkLayerAddress</code> of the receiver
     * @param message   the message to send
     * @param callbackHandler the callback handler
     */
    public void sendUnicast(
            Address receiver,
            LinkLayerMessage message,
            UnicastCallbackHandler callbackHandler);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as broadcast
     * @param message   the message to send
     */
    public void sendBroadcast(LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as broadcast and
     * send the message delivery status asynchrone to the callback handler  
     * @param message   the message to send
     * @param callbackHandler the callbackHandler
     */
    public void sendBroadcast(
            LinkLayerMessage message,
            BroadcastCallbackHandler callbackHandler);
    
    
    
    /**
     * Turns the promiscuous mode of the linkLayer on or of 
     * @param promiscuous
     */
    public void setPromiscuous(boolean promiscuous);
    
    
    
}
