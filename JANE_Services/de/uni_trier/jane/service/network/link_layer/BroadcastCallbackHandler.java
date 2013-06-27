/*****************************************************************************
 * 
 * BroadcastObserver.java
 * 
 * $Id: BroadcastCallbackHandler.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
import de.uni_trier.jane.signaling.*;

/**
 * This interface represents the <code>CallbackHandler</code> used by the <code>LinkLayer</code>
 * implementations to inform the sender about broadcast message delivery status.
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 */
public interface BroadcastCallbackHandler extends BroadcastObserver {

//	/**
//	 * Uses this ServiceCallback to inform a message sending service about the status of its message
//	 * send by a broadcast. 
//	 * Send this callback when the message has been processed and completely put on the medium
//	 * @see LinkLayer 
//	 */
//    public static class BroadcastProcessedCallback implements ServiceCallback {
//
//    	private LinkLayerMessage message;
//
//    	/**
//    	 * Constructor for class <code>BroadcastProcessedCallback</code>
//    	 * @param message	the send message
//    	 */
//		public BroadcastProcessedCallback(LinkLayerMessage message) {
//			this.message = message;
//		}
//		
//        public void handle(CallbackHandler handler) {
//            ((BroadcastCallbackHandler) handler).notifyBroadcastProcessed(message);
//        }
//
//        public Dispatchable copy() {
//        	LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
//			if (messageCopy == message) {
//				return this;
//			}
//			return new BroadcastProcessedCallback(messageCopy);
//        }
//
//        public Class getReceiverServiceClass() {
//            return BroadcastCallbackHandler.class;
//        }
//        
//    }
    
}
