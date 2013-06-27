/*****************************************************************************
 * 
 * UnicastObserver.java
 * 
 * $Id: MulticastCallbackHandler.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;

/**
 * This interface represents the <code>CallbackHandler</code> used by the <code>LinkLayer</code>
 * implementations to inform the sender about unicast message delivery status.
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 */
public interface MulticastCallbackHandler extends MulticastObserver {
  
	/**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a unicast to the @see LinkLayerAddress receiver. 
	 * Send this callback when the message has been processed and completely put on the medium
	 * @see LinkLayer 
	 */
    public static class MulticastProcessedCallback implements Signal {
		private LinkLayerMessage message;
		private Address[] receivers;

    	/** 
		 * Constructor for class <code>UnicastReceivedCallback</code>
		 * @param receivers	the message receivers
		 * @param message	the message
		 */
		public MulticastProcessedCallback(Address[] receivers,
				LinkLayerMessage message) {
			this.message = message;
			this.receivers = receivers;

		}

		public Dispatchable copy() {
			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
			if (messageCopy == message) {
				return this;
			}
			return new MulticastProcessedCallback(receivers, messageCopy);
		}

		public Class getReceiverServiceClass() {
			return MulticastCallbackHandler.class;
		}

		public void handle(SignalListener handler) {
			((MulticastCallbackHandler) handler)
					.notifyMulticastProcessed(receivers, message);
		}

	}
    
	/**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a unicast to the @see LinkLayerAddress receiver.
	 * Send this callback when the message has been received correctly.
	 * @see LinkLayer 
	 */    
    public static class MulticastReceivedCallback implements Signal {
		private LinkLayerMessage message;
		private Address[] receivers;
		private Address[] nonReceivers;

		/**
		 * Constructor for class <code>MulticastRecievedCallback</code>
		 * @param receivers			these devices received the multicast
		 * @param nonReceivers		these devices didnt receive the multicast
		 * @param message			the message
		 */
		public MulticastReceivedCallback(Address[] receivers,
		        Address[] nonReceivers,
				LinkLayerMessage message) {
			this.message = message;
			this.receivers = receivers;
			this.nonReceivers=nonReceivers;
		}

		public Dispatchable copy() {
			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
			if (messageCopy == message) {
				return this;
			}
			return new MulticastReceivedCallback(receivers,nonReceivers, messageCopy);
		}

		public Class getReceiverServiceClass() {
			return MulticastCallbackHandler.class;
		}

		public void handle(SignalListener handler) {
			((MulticastCallbackHandler) handler).notifyMulticastReceived(receivers,nonReceivers, message);
		}


	
	}    

  
 
  
}
