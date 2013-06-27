/*****************************************************************************
 * 
 * LinkLayerExtended_async.java
 * 
 * $Id: LinkLayerExtended_async.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 *  
 * Copyright (C) 2002-2006 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.extended;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.*;

public interface LinkLayerExtended_async extends LinkLayer_async{
    
    /**
     * 
     * TODO Comment method
     * @param message
     * @param configuration
     * @param callbackHandler
     */
    public void sendBroadcast(LinkLayerMessage message,
            LinkLayerConfiguration configuration,
            BroadcastCallbackHandler callbackHandler);
    
    
    /**
     * 
     * TODO Comment method
     * @param receiver
     * @param message
     * @param configuration
     * @param callbackHandler
     */
    public void sendUnicast(Address receiver, 
            LinkLayerMessage message, 
            LinkLayerConfiguration configuration,
            UnicastCallbackHandler callbackHandler);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver. 
     * All other neighbors will recieve this message if the delivery is successfull.
     * @param receiver  the network <code>Address</code> of the receiver
     * @param message   the message to send
     *
     */
    public void sendAddressedBroadcast(
            Address receiver,
            LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver and
     * send the message delivery status asynchrone to the callback handler 
     * All other neighbors will recieve this message if the delivery is successfull.
     * @param receiver  the <code>LinkLayerAddress</code> of the receiver
     * @param configuration  use the given configuration
     * @param message   the message to send
     * @param callbackHandler the callback handler
     */
    public void sendAddressedBroadcast(Address receiver,
            LinkLayerMessage message, 
            LinkLayerConfiguration configuration,
            UnicastCallbackHandler callbackHandler) ;
    
    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver. 
     * All other neighbors will recieve this message if the delivery is successfull.
     * @param receivers  the network <code>Address</code> of the receiver
     * @param message   the message to send
     *
     */
    public void sendAddressedBroadcast(
            Address[] receivers,
            LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> reliable to the given receivers and
     * send the message delivery status asynchrone to the callback handler 
     * All other neighbors will recieve this message if the delivery is successfull.
     * @param receivers  the <code>Address</code>es of the receivers
     * @param message   the message to send
     * @param configuration use the given linklayer configuration
     * @param callbackHandler the callback handler
     */
    public void sendAddressedBroadcast(
            Address[] receivers, 
            LinkLayerMessage message,
            LinkLayerConfiguration configuration,
            AddressedBroadcastCallbackHandler callbackHandler);
    
    
    /**
     * Send the given <code>LinkLayerMessage</code> reliable to the given receivers and
     * send the message delivery status asynchrone to the callback handler 
     * @param receivers  the <code>Address</code>es of the receivers
     * @param configuration use the given timeout for failure signaling
     * @param message   the message to send
     * @param callbackHandler the callback handler
     */
    public void sendAddressedMulticast(
            Address[] receivers, 
            LinkLayerMessage message,
            LinkLayerConfiguration configuration,
            AddressedBroadcastCallbackHandler callbackHandler);

}
