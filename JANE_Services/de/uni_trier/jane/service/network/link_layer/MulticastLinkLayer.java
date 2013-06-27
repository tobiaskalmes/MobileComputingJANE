/*****************************************************************************
 * 
 * MulticastLinkLayer.java
 * 
 * $Id: MulticastLinkLayer.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MulticastLinkLayer extends LinkLayer {
    /**
     * TODO: comment class  
     * @author daniel
     **/

    public static class MulticastLinkLayerStub extends LinkLayerStub {

        /**
         * Constructor for class MulticastLinkLayerStub 
         *
         * @param operatingSystem
         * @param LinkLayerServiceID
         */
        public MulticastLinkLayerStub(RuntimeOperatingSystem operatingSystem, ServiceID LinkLayerServiceID) {
            super(operatingSystem, LinkLayerServiceID);

        }
        
        /**
         *	This signal is used to send a multicast to the given receiver devices.
         *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
         * 	@see LinkLayer 
         */
        public static final class SendMulticastSignal implements Signal{
            private LinkLayerMessage message;
            private LinkLayerAddress[] receivers;
            
            /**
             * Constructor for class <code>SendMulticastSignal</code>
             * @param receivers	the recievers of the multicast message
             * @param message	the message to be send
             */
            public SendMulticastSignal(LinkLayerAddress[] receivers, LinkLayerMessage message) {
                this.message = message;
                this.receivers=receivers;
                
            }
            
            public void handle(SignalListener service) {
                MulticastLinkLayer linkLayer = (MulticastLinkLayer)service;
                linkLayer.sendMulticast(receivers,message);
            }

            public Dispatchable copy() {
            	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
                if(messageCopy == message) {
                    return this;
                }
                return new SendMulticastSignal(receivers,messageCopy);
            }
            
            public Class getReceiverServiceClass() {
                return MulticastLinkLayer.class;
            }


            
        }

        /**
         * Send the given <code>LinkLayerMessage</code> as multicast to the given receivers
         * @param receivers	the <code>LinkLayerAddress</code>es of the receivers
         * @param message	the message to send
         */
        public void sendMulticast(LinkLayerAddress[] receivers, LinkLayerMessage message){
            operatingSystem.sendSignal(new SendMulticastSignal(receivers,message));
        }
        
        /**
         *	This signal is used to send a multicast to the given receiver devices.
         *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
         * 	@see LinkLayer 
         */
        public static final class SendMulticastTask implements Signal{
            private LinkLayerMessage message;
            private Address[] receivers;
            private ListenerID listenerID;
            
            /**
             * Constructor for class <code>SendMulticastSignal</code>
             * @param receivers	the recievers of the multicast message
             * @param message	the message to be send
             */
            public SendMulticastTask(Address[] receivers, LinkLayerMessage message,ListenerID listenerID) {
                this.message = message;
                this.receivers=receivers;
                this.listenerID=listenerID;
                
            }
            
            public void handle(SignalListener service) {
                MulticastLinkLayer linkLayer = (MulticastLinkLayer)service;
                linkLayer.sendMulticast(receivers,message,listenerID);
            }

            public Dispatchable copy() {
            	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
                if(messageCopy == message) {
                    return this;
                }
                return new SendMulticastTask(receivers,messageCopy,listenerID);
            }
            

            public Class getReceiverServiceClass() {
                return MulticastLinkLayer.class;
            }
        }
        
        /**
         * Send the given <code>LinkLayerMessage</code> as multicast to the given receivers and
         * send the message delivery status to the callback handler accessible with given <code>TaskHandle</code>
         * @param receivers	the <code>LinkLayerAddress</code>es of the receiverer
         * @param message	the message to send
         * @param handler	the the multicast callback handler
         */
        public void sendMulticast(Address[] receivers, LinkLayerMessage message, MulticastCallbackHandler handler){
            ListenerID listenerID=operatingSystem.registerSignalListener(handler,handler.getClass());
            operatingSystem.sendSignal(LinkLayerServiceID,new SendMulticastTask(receivers,message,listenerID));
        }


    }
    /**
     * Send the given <code>LinkLayerMessage</code> as multicast to the given receivers
     * @param receivers	the <code>LinkLayerAddress</code>es of the receivers
     * @param message	the message to send
     */
    public void sendMulticast(Address[] receivers, LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as multicast to the given receivers and
     * send the message delivery status to the callback handler accessible with given <code>TaskHandle</code>
     * @param receivers	the <code>LinkLayerAddress</code>es of the receiverer
     * @param message	the message to send
     * @param listenerID	the TaskHandle
     */
    public void sendMulticast(Address[] receivers, LinkLayerMessage message, ListenerID listenerID);
    
    
    /**
     *	This signal is used to send a multicast to the given receiver devices.
     *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
     * 	@see LinkLayer 
     */
    public static final class SendMulticastSignal implements Signal {
        private LinkLayerMessage message;
        private Address[] receivers;
        
        /**
         * Constructor for class <code>SendMulticastSignal</code>
         * @param receivers	the recievers of the multicast message
         * @param message	the message to be send
         */
        public SendMulticastSignal(Address[] receivers, LinkLayerMessage message) {
            this.message = message;
            this.receivers=receivers;
            
        }
        
        /*
         *  (non-Javadoc)
         * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
         */
        public void handle(SignalListener service) {
            MulticastLinkLayer linkLayer = (MulticastLinkLayer)service;
            linkLayer.sendMulticast(receivers,message);
        }


        
        /*
         *  (non-Javadoc)
         * @see de.uni_trier.ssds.service.Dispatchable#copy()
         */
        public Dispatchable copy() {
        	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
            if(messageCopy == message) {
                return this;
            }
            return new SendMulticastSignal(receivers,messageCopy);
        }

        /*
         *  (non-Javadoc)
         * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
         */
        public Class getReceiverServiceClass() {
            return MulticastLinkLayer.class;
        }
        
    }
//
//    /**
//     *	This <code>TaskCallbackPair</code> is used to send a multicast to the given receiver devices and
//     * 	to register the given callback handler for receiving message delivery status information
//     *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
//     * 	@see LinkLayer 
//     * 	@see MulticastCallbackHandler
//     */
//    public static final class SendMulticastTask implements TaskCallbackPair {
//        private ServiceTask task;
//        private CallbackHandler handler;
//        
//        /**
//         * Constructor for class <code>SendMulticastTask</code>
//         * @param receivers	The receivers of the message	
//         * @param message	The message to send as unicast
//         * @param observer	The callback handler 
//         */
//        public SendMulticastTask(LinkLayerAddress[] receivers, LinkLayerMessage message, MulticastCallbackHandler observer) {
//            task = new SendMulticastSignal(receivers,message);
//            handler = observer;
//        }
//
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.TaskCallbackPair#getServiceTask()
//         */
//        public ServiceTask getServiceTask() {
//            return task;
//        }
//        
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.TaskCallbackPair#getCallbackHandler()
//         */
//        public CallbackHandler getCallbackHandler() {
//            return handler;
//        }
//
//    }
}
