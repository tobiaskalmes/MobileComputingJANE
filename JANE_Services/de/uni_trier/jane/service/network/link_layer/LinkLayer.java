/*****************************************************************************
 * 
 * LinkLayer.java
 * 
 * $Id: LinkLayer.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

/**
 * This interface represents a LinkLayerService. These services realize network communication 
 * at the  networks link layer level.
 * Messages can be send within the network by using the following Signals:
 * <code>LinkLayer.SendUnicastSignal</code>
 * 		Sends a unicast message to the device with the given <code>LinkLayerMessage</code>.
 * <code>LinkLayer.SendUnicastTask</code>
 * 		Sends a unicast message to the device with the given <code>LinkLayerMessage</code>. 
 * 		and registers the callback handler UnicastObserver to retreive message delivery status
 * <code>LinkLayer.SendBroadcastSignal</code>
 * 		Sends a broadcast message to all neighboring devices.
 * <code>LinkLayer.SendBroadcastTask</code>
 * 		Sends a broadcast message to all neighboring devices.
 * 		and registers the callback handler BroadcastObserver to retreive message delivery status
 * 
 * All messages must be of Type <code>LinkLayerMessage</code>.
 * The <code>LinkLayerInfo</code> passed to the LinkLayerMessage handler is implementation dependend.
 * As default, LinkLayerInfoImplementation should be used. This info can be extended to give the receiver
 * additional, implementation dependend information. 
 * 
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayerAddress
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayerMessage
 * @see de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler
 * @see de.uni_trier.jane.service.network.link_layer.BroadcastCallbackHandler
 */
// TODO umbenennen in LinkLayerService
public interface LinkLayer extends LinkLayer_async, LinkLayer_sync{

	
    /**
     * Returns the <code>LinkLayerProperties</code> of this LinkLayer
     * @return	the <code>LinkLayerProperties</code>
     * @deprecated use LinkLayer_sync methods
     */
    //public LinkLayerProperties getLinkLayerProperties();
    
    
//    public static class LinkLayerFacade{
//    	/**
//    	 * @author Daniel G?rgen
//    	 *
//    	 * To change this generated comment go to 
//    	 * Window>Preferences>Java>Code Generation>Code and Comments
//    	 */
//    	private static final class LinkLayerPropertiesAccess implements
//    			ListenerAccess {
//
//
//    		/* (non-Javadoc)
//    		 * @see de.uni_trier.jane.service.ListenerAccess#handle(de.uni_trier.jane.basetypes.ServiceID, de.uni_trier.jane.service.Service)
//    		 */
//    		public Object handle(ServiceID sender, Service service) {
//    			return ((LinkLayer)service).getLinkLayerProperties();
//    			
//    		}
//
//    		/* (non-Javadoc)
//    		 * @see de.uni_trier.jane.basetypes.Dispatchable#copy()
//    		 */
//    		public Dispatchable copy() {
//    			return this;
//    		}
//
//    		/* (non-Javadoc)
//    		 * @see de.uni_trier.jane.basetypes.Dispatchable#getReceiverServiceClass()
//    		 */
//    		public Class getReceiverServiceClass() {
//    			return LinkLayer.class;
//    		}
//
//    	}
//    	
//    	
//    	private ServiceID linklayerServiceID;
//    	private RuntimeOperatingSystem operatingSystem;
//    	
//		/**
//		 * @param linklayerServiceID
//		 * @param operatingSystem
//		 */
//		public LinkLayerFacade(ServiceID linklayerServiceID,
//				RuntimeOperatingSystem operatingSystem) {
//			this.linklayerServiceID = linklayerServiceID;
//			this.operatingSystem = operatingSystem;
//		}
//		public LinkLayerProperties getLinkLayerProperties(){
//			return (LinkLayerProperties)operatingSystem.accessSynchronous(linklayerServiceID, new LinkLayerPropertiesAccess());
//		}
//        
//       public void sendUnicast(LinkLayerAddress receiver,LinkLayerMessage message) {
//            operatingSystem.sendSignal(linklayerServiceID,new SendUnicastSignal(receiver,message));
//        }
//       
//       public void sendBroadcast(LinkLayerMessage message) {
//           operatingSystem.sendSignal(linklayerServiceID,new SendBroadcastSignal(message));
//       }
//       
//       /**
//     	* TODO: comment method 
//     	*
//     	* @return
//     	*/
//       public LinkLayerAddress getLinkLayerAddress() {
//
//           return getLinkLayerProperties().getLinkLayerAddress();
//       }
//
//    }
    
    
    /**
     * Send the given <code>LinkLayerMessage</code> as broadcast
     * @param message	the message to send
     */
 //   public void sendBroadcast(LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as broadcast and
     * send the message delivery status to the callback handler accessible with given <code>TaskHandle</code> 
     * @param message	the message to send
     * @param handle	the TaskHandle
     */
    //public void sendBroadcast(LinkLayerMessage message, TaskHandle handle);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver
     * @param receiver	the <code>LinkLayerAddress</code> of the receiver
     * @param message	the message to send
     */
    //public void sendUnicast(LinkLayerAddress receiver, LinkLayerMessage message);
    
    /**
     * Send the given <code>LinkLayerMessage</code> as unicast to the given receiver and
     * send the message delivery status to the callback handler accessible with given <code>TaskHandle</code>
     * @param receiver	the <code>LinkLayerAddress</code> of the receiver
     * @param message	the message to send
     * @param handle	the TaskHandle
     */
    //public void sendUnicast(LinkLayerAddress receiver, LinkLayerMessage message, TaskHandle handle);

    
    /**
     *	This signal is used to send a unicast to the given receiver device.
     *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
     * 	@see LinkLayer 
     */
//    public static final class SendUnicastSignal implements Signal, ServiceTask {
//        private LinkLayerMessage message;
//        private LinkLayerAddress receiver;
//        
//        /**
//         * Constructor for class <code>SendUnicastSignal</code>
//         * @param receiver	the reciever of the unicast message
//         * @param message	the message to be send
//         */
//        public SendUnicastSignal(LinkLayerAddress receiver, LinkLayerMessage message) {
//            this.message = message;
//            this.receiver=receiver;
//        }
//        
//        
//        public void handle(SignalListener service) {
//            LinkLayer linkLayer = (LinkLayer)service;
//            linkLayer.sendUnicast(receiver,message);
//        }
//
//        
//        public void handle(ServiceID sender, Service service, TaskHandle handle) {
//            LinkLayer linkLayer = (LinkLayer)service;
//            linkLayer.sendUnicast(receiver,message, handle);
//        }
//        
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.Dispatchable#copy()
//         */
//        public Dispatchable copy() {
//        	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
//            if(messageCopy == message) {
//                return this;
//            }
//            return new SendUnicastSignal(receiver,messageCopy);
//        }
//
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
//         */
//        public Class getReceiverServiceClass() {
//            return LinkLayer.class;
//        }
//        
//    }
//
//    /**
//     *	This <code>TaskCallbackPair</code> is used to send a unicast to the given receiver device and
//     * 	to register the given callback handler for receiving message delivery status information
//     *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
//     * 	@see LinkLayer 
//     * 	@see UnicastCallbackHandler
//     */
//    public static final class SendUnicastTask implements TaskCallbackPair {
//
//        private ServiceTask task;
//        private CallbackHandler handler;
//        
//        /**
//         * Constructor for class <code>SendUnicastTask</code>
//         * @param receiver	The receiver of the message	
//         * @param message	The message to send as unicast
//         * @param observer	The callback handler 
//         */
//        public SendUnicastTask(LinkLayerAddress receiver, LinkLayerMessage message, UnicastCallbackHandler observer) {
//            task = new SendUnicastSignal(receiver,message);
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
//    
//    /**
//     *	This signal is used to send a broadcast to all neighboring devices.
//     *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
//     * 	@see LinkLayer 
//     */
//    public static final class SendBroadcastSignal implements Signal, ServiceTask {
//
//        private LinkLayerMessage message;
//
//        /**
//         * Constructor for class <code>SendBroadcastSignal</code>
//         * @param message	the message to be send as broadcast
//         */
//        public SendBroadcastSignal(LinkLayerMessage message) {
//            this.message = message;
//        }
//        
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.Signal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
//         */
//        public void handle(SignalListener service) {
//            LinkLayer linkLayer = (LinkLayer)service;
//            linkLayer.sendBroadcast(message);
//        }
//        
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.ServiceTask#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service, de.uni_trier.ssds.service.TaskHandle)
//         */
//        public void handle(ServiceID sender, Service service, TaskHandle handle) {
//            LinkLayer linkLayer = (LinkLayer)service;
//            linkLayer.sendBroadcast(message, handle);
//        }
//        
//        /*
//         *  (non-Javadoc)
//         * @see de.uni_trier.ssds.service.Dispatchable#copy()
//         */
//        public Dispatchable copy() {
//        	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
//            if(messageCopy == message) {
//                return this;
//            }
//            return new SendBroadcastSignal(messageCopy);
//        }
//
//        /* (non-Javadoc)
//         * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
//         */
//        public Class getReceiverServiceClass() {
//            return LinkLayer.class;
//        }
//        
//    }
//
//    /**
//     *	This <code>TaskCallbackPair</code> is used to send a broadcast to all neighboring devices and
//     * 	to register the given callback handler for receiving message delivery status information
//     *	This signal must be sended to a service implementing the <code>LinkLayer</code> interface
//     * 	@see LinkLayer 
//     * 	@see BroadcastCallbackHandler
//     */
//    public static final class SendBroadcastTask implements TaskCallbackPair {
//
//        private ServiceTask task;
//        private CallbackHandler handler;
//
//        /**
//         * Constructor for class <code>SendBroadcastSignal</code>
//         * @param message	the message to be send as broadcast
//         * @param observer	The callback handler 
//         */
//        public SendBroadcastTask(LinkLayerMessage message, BroadcastCallbackHandler observer) {
//            task = new SendBroadcastSignal(message);
//            handler = observer;
//        }
//
//        /* (non-Javadoc)
//         * @see de.uni_trier.ssds.service.SignalCallbackPair#getSignal()
//         */
//        public ServiceTask getServiceTask() {
//            return task;
//        }
//
//        /* (non-Javadoc)
//         * @see de.uni_trier.ssds.service.SignalCallbackPair#getCallbackHandler()
//         */
//        public CallbackHandler getCallbackHandler() {
//            return handler;
//        }
//
//    }

    public static class LinkLayerStub implements LinkLayer_async,LinkLayer_sync{
        protected RuntimeOperatingSystem operatingSystem;
        protected ServiceID LinkLayerServiceID;
        private LinkLayer_async linkLayer_async;
        private LinkLayer_sync linkLayer_sync;
        public LinkLayerStub(
            RuntimeOperatingSystem operatingSystem,
            ServiceID LinkLayerServiceID) {
            this.operatingSystem = operatingSystem;
            this.LinkLayerServiceID = LinkLayerServiceID;
            linkLayer_async=(LinkLayer_async)operatingSystem.getSignalListenerStub(LinkLayerServiceID, LinkLayer_async.class);
            linkLayer_sync=(LinkLayer_sync)operatingSystem.getAccessListenerStub(LinkLayerServiceID,LinkLayer_sync.class);
        }
        
        /**
         * register for message reception
         */
        public void registerAtService() {
            operatingSystem.registerAtService(
                LinkLayerServiceID,
                LinkLayer.class);
        }

        /**
         * 
         * unregister for message reception
         */
        public void unregisterAtService() {
            operatingSystem.unregisterAtService(
                LinkLayerServiceID,
                LinkLayer.class);
        }

//        private static final class GetLinkLayerPropertiesSyncAccess
//            implements
//                ListenerAccess {
//            public GetLinkLayerPropertiesSyncAccess() {
//            }
//
//            public Dispatchable copy() {
//                return this;
//            }
//
//            public Class getReceiverServiceClass() {
//                return LinkLayer.class;
//            }
//
//            public Object handle(SignalListener service) {
//                return ((LinkLayer) service).getLinkLayerProperties();
//            }
//
//        }
        public LinkLayerProperties getLinkLayerProperties() {
            return linkLayer_sync.getLinkLayerProperties();
//            return (LinkLayerProperties) operatingSystem.accessSynchronous(
//                LinkLayerServiceID,
//                new GetLinkLayerPropertiesSyncAccess());
        }
        
        public void setLinkLayerProperties(LinkLayerProperties properties) {
            linkLayer_sync.setLinkLayerProperties(properties);
        }

//        private static final class SendBroadcastSignal implements Signal {
//            private LinkLayerMessage message;
//            public SendBroadcastSignal(LinkLayerMessage message) {
//                this.message = message;
//            }
//
//            public Dispatchable copy() {
//                return this;
//            }
//
//            public Class getReceiverServiceClass() {
//                return LinkLayer.class;
//            }
//
//            public void handle(SignalListener service) {
//                ((LinkLayer) service).sendBroadcast(message);
//            }
//
//        }
        public void sendBroadcast(LinkLayerMessage message) {
            linkLayer_async.sendBroadcast(message);
//            operatingSystem.sendSignal(
//                LinkLayerServiceID,
//                new SendBroadcastSignal(message));
        }

//        private static final class SendBroadcastTask implements ServiceTask {
//            private LinkLayerMessage message;
//            public SendBroadcastTask(LinkLayerMessage message) {
//                this.message = message;
//            }
//
//            public Dispatchable copy() {
//                return this;
//            }
//
//            public Class getReceiverServiceClass() {
//                return LinkLayer.class;
//            }
//
//            public void handle(
//                ServiceID serviceID,
//                Service service,
//                TaskHandle taskHandle) {
//                ((LinkLayer) service).sendBroadcast(message, taskHandle);
//            }
//
//        }
        public void sendBroadcast(
            LinkLayerMessage message,
            BroadcastCallbackHandler callbackHandler) {
            linkLayer_async.sendBroadcast(message,callbackHandler);
//            return operatingSystem.startTask(
//                LinkLayerServiceID,
//                new DefaultTaskCallbackPair(
//                    new SendBroadcastTask(message),
//                    callbackHandler));
        }

//        private static final class SendUnicastSignal implements Signal {
//            private LinkLayerAddress receiver;
//            private LinkLayerMessage message;
//            public SendUnicastSignal(
//                LinkLayerAddress receiver,
//                LinkLayerMessage message) {
//                this.receiver = receiver;
//                this.message = message;
//            }
//
//            public Dispatchable copy() {
//                return this;
//            }
//
//            public Class getReceiverServiceClass() {
//                return LinkLayer.class;
//            }
//
//            public void handle(SignalListener service) {
//                ((LinkLayer) service).sendUnicast(receiver, message);
//            }
//
//        }
        public void sendUnicast(
            Address receiver,
            LinkLayerMessage message) {
            linkLayer_async.sendUnicast(receiver,message);
//            operatingSystem.sendSignal(
//                LinkLayerServiceID,
//                new SendUnicastSignal(receiver, message));
        }

//        private static final class SendUnicastTask implements ServiceTask {
//            private LinkLayerAddress receiver;
//            private LinkLayerMessage message;
//            public SendUnicastTask(
//                LinkLayerAddress receiver,
//                LinkLayerMessage message) {
//                this.receiver = receiver;
//                this.message = message;
//            }
//
//            public Dispatchable copy() {
//                return this;
//            }
//
//            public Class getReceiverServiceClass() {
//                return LinkLayer.class;
//            }
//
//            public void handle(
//                ServiceID serviceID,
//                Service service,
//                TaskHandle taskHandle) {
//                ((LinkLayer) service)
//                    .sendUnicast(receiver, message, taskHandle);
//            }
//
//        }
        public void sendUnicast(
            Address receiver,
            LinkLayerMessage message,
            UnicastCallbackHandler callbackHandler) {
            linkLayer_async.sendUnicast(receiver,message,callbackHandler);
//            return operatingSystem.startTask(
//                LinkLayerServiceID,
//                new DefaultTaskCallbackPair(new SendUnicastTask(
//                    receiver,
//                    message), callbackHandler));
        }

        /**
         * 
         *
         * @return
         * @deprecated use getNetworkAddress instead        
         */         
        public LinkLayerAddress getLinkLayerAddress() {
            return (LinkLayerAddress) linkLayer_sync.getNetworkAddress();
        }
        
        public Address getNetworkAddress() {        
            return linkLayer_sync.getNetworkAddress();
        }

        public void setPromiscuous(boolean promiscuous) {
            linkLayer_async.setPromiscuous(promiscuous);
            
        }

    }}
