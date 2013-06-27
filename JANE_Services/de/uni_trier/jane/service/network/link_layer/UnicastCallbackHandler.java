/*****************************************************************************
 * 
 * UnicastObserver.java
 * 
 * $Id: UnicastCallbackHandler.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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


/**
 * This interface represents the <code>CallbackHandler</code> used by the <code>LinkLayer</code>
 * implementations to inform the sender about unicast message delivery status.
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 */
public interface UnicastCallbackHandler extends UnicastObserver {
  
//	/**
//	 * Uses this ServiceCallback to inform a message sending service about the status of its message
//	 * send by a unicast to the @see LinkLayerAddress receiver. 
//	 * Send this callback when the message has been processed and completely put on the medium
//	 * @see LinkLayer 
//	 */
//    public static class UnicastProcessedCallback implements ServiceCallback {
//
//		private LinkLayerMessage message;
//		private LinkLayerAddress receiver;
//
//		/**
//		 * Constructor for class <code>UnicastProcessedCallback</code>
//		 * @param receiver	the message receiver
//		 * @param message	the message
//		 */
//		public UnicastProcessedCallback(LinkLayerAddress receiver,
//				LinkLayerMessage message) {
//			this.message = message;
//			this.receiver = receiver;
//		}
//
//		public Dispatchable copy() {
//			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
//			if (messageCopy == message) {
//				return this;
//			}
//			return new UnicastProcessedCallback(receiver, messageCopy);
//		}
//
//		public Class getReceiverServiceClass() {
//			return UnicastCallbackHandler.class;
//		}
//
//		public void handle(CallbackHandler handler) {
//			((UnicastCallbackHandler) handler).notifyUnicastProcessed(receiver, message);
//		}
//	}
//    
//	/**
//	 * Uses this ServiceCallback to inform a message sending service about the status of its message
//	 * send by a unicast to the @see LinkLayerAddress receiver.
//	 * Send this callback when the message has been received correctly.
//	 * @see LinkLayer 
//	 */    
//    public static class UnicastReceivedCallback implements ServiceCallback {
//
//		private LinkLayerMessage message;
//
//		private LinkLayerAddress receiver;
//
//		/** 
//		 * Constructor for class <code>UnicastReceivedCallback</code>
//		 * @param receiver	the message receiver
//		 * @param message	the message
//		 */
//		public UnicastReceivedCallback(LinkLayerAddress receiver,
//				LinkLayerMessage message) {
//			this.message = message;
//			this.receiver = receiver;
//
//		}
//
//		public Dispatchable copy() {
//			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
//			if (messageCopy == message) {
//				return this;
//			}
//			return new UnicastReceivedCallback(receiver, messageCopy);
//		}
//
//		public Class getReceiverServiceClass() {
//			return UnicastCallbackHandler.class;
//		}
//
//		public void handle(CallbackHandler handler) {
//			((UnicastCallbackHandler) handler)
//					.notifyUnicastReceived(receiver, message);
//		}
//
//	}    
//
//    /**
//	 * Uses this ServiceCallback to inform a message sending service about the status of its message
//	 * send by a unicast to the @see LinkLayerAddress receiver. 
//	 * Send this callback when the message has been lost.
//	 * @see LinkLayer 
//	 */   
//    public static class UnicastLostCallback implements  ServiceCallback{
//
//        private LinkLayerMessage message;
//        private LinkLayerAddress receiver;
//
//        /**
//         * Constructor for class <code>UnicastLostCallback</code>
//		 * @param receiver	the message receiver
//		 * @param message	the message
//         */
//        public UnicastLostCallback(LinkLayerAddress receiver, LinkLayerMessage message) {
//            this.message=message;
//            this.receiver=receiver;
//            
//        }
//
//        public Dispatchable copy() {
//        	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
//            if (messageCopy==message){
//                return this;
//            }
//            return new UnicastLostCallback(receiver,messageCopy);
//        }
//        
//        public Class getReceiverServiceClass() {
//            return UnicastCallbackHandler.class;
//        }
//
//        public void handle(CallbackHandler handler) {
//            ((UnicastCallbackHandler)handler).notifyUnicastLost(receiver,message);    
//        }
//    }    
//
//    /**
//	 * Uses this ServiceCallback to inform a message sending service about the status of its message
//	 * send by a unicast to the @see LinkLayerAddress receiver. 
//	 * Send this callback when the message status is unknown. Probably the message has been lost or
//	 * the message has been received.
//	 * @see LinkLayer 
//	 */   
//    public static class UnicastUndefinedCallback implements ServiceCallback {
//        
//		private LinkLayerAddress receiver;
//		private LinkLayerMessage message;
//
//		/**
//		 * Constructor for class <code>UnicastUndefinedCallback</code>
//		 * @param receiver	the message receiver
//		 * @param message	the message
//		 */
//		public UnicastUndefinedCallback(LinkLayerAddress receiver,
//				LinkLayerMessage message) {
//			this.receiver = receiver;
//			this.message = message;
//		}
//
//		public Dispatchable copy() {
//			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
//			if (messageCopy == message) {
//				return this;
//			}
//			return new UnicastUndefinedCallback(receiver, messageCopy);
//		}
//
//		public Class getReceiverServiceClass() {
//			return UnicastCallbackHandler.class;
//		}
//
//		public void handle(CallbackHandler handler) {
//			((UnicastCallbackHandler) handler).notifyUnicastUndefined(receiver,
//					message);
//
//		}
//
//	} 

}
