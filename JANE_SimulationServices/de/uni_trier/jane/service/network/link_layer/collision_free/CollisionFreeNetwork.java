/*****************************************************************************
 * 
 * CollisionFreeNetwork.java
 * 
 * $Id: CollisionFreeNetwork.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.collision_free;

import java.awt.geom.Arc2D.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;



import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.global.*;
import de.uni_trier.jane.service.network.link_layer.global.randomReject.AllRecieveDecider;
import de.uni_trier.jane.service.network.link_layer.global.randomReject.ReceiveDecider;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.global_knowledge.DeviceListener;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.global_knowledge.LinkListener;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.ArrowShape;
import de.uni_trier.jane.visualization.shapes.EllipseShape;
import de.uni_trier.jane.visualization.shapes.MovingShape;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * This is a simple implementation of the <code>SimulationNetwork</code> interface.
 * There are no packet collisions and the transmission rate does not change, if
 * the network is unter high load. The link reliability is assumed to be 100%.
 * Thus, no message will get lost.
 */
// TODO: überprüfen, ob der Fall Knoten nicht erreichbar korrekt behandelt wird.
public class CollisionFreeNetwork implements  GlobalLinkLayer_sync,GlobalLinkLayer, GlobalService {

	private static final IntegerParameter DATA_RATE = new IntegerParameter("dataRate", 1024);
	private static final BooleanParameter VISUALIZE_MESSAGES = new BooleanParameter("visualizeMessages", true);
	private static final BooleanParameter VISUALIZE_SENDING_RADIUS = new BooleanParameter("visualizeSendingRadius", false);
	private static final BooleanParameter VISUALIZE_COMMUNICATION_LINKS = new BooleanParameter("visualizeCommunicationLinks", true);

	public static final IdentifiedServiceElement INITIALIZATION_ELEMENT = new IdentifiedServiceElement("collisionFreeNetwork") {

		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			int dr = DATA_RATE.getValue(initializationContext);
			boolean vm = VISUALIZE_MESSAGES.getValue(initializationContext);
			boolean vsr = VISUALIZE_SENDING_RADIUS.getValue(initializationContext);
			boolean vcl = VISUALIZE_COMMUNICATION_LINKS.getValue(initializationContext);
			serviceUnit.addService(new CollisionFreeNetwork(ownServiceID, dr, new AllRecieveDecider(), vm, true, vsr, vcl));
		}
		
		public Parameter[] getParameters() {
			return new Parameter[] { DATA_RATE, VISUALIZE_MESSAGES, VISUALIZE_SENDING_RADIUS, VISUALIZE_COMMUNICATION_LINKS};
		}

	};
	
	public final static String VERSION = "$Id: CollisionFreeNetwork.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $";

	private static final Shape DEFAULT_MESSAGE_SHAPE = new RectangleShape( new Extent(4,4), Color.GREY, true);

	private double dataRate;
	protected NetworkGraph graph;
	private MessageProcessor processor;
	private MessageQueue queue;

	private ReceiveScheduler scheduler;
	private Set addressSet;

    protected GlobalOperatingSystem operatingSystem;

    private HashMap deviceIDInfoMap;

    private ServiceID serviceID;

    private HashMap addressInfoMap;

    private boolean visualizeMessages;

    private boolean visualizeSendingRadius;

    private boolean visualizeCommunicationLinks;

    private boolean messagesByService;

    protected LinkLayerObserverGlobalStub linkLayerObserver;
    
    public ReceiveDecider receiveDecider;

    /**
     * Creates an instance of a <code>CollisionFreeNetwork</code>
     * @param serviceUnit the service unit
     */
    public static void createInstance(ServiceUnit serviceUnit) 
    {
    	createInstance(serviceUnit, 10240*8);
    }

    /**
     * Creates an instance of a <code>CollisionFreeNetwork</code>
     * @param serviceUnit the service unit
     * @param dataRate the data rate
     */
    public static void createInstance(ServiceUnit serviceUnit, double dataRate) 
    {
    	createInstance(serviceUnit, dataRate, true, true, true);
    }

    /**
     * Creates an instance of a <code>CollisionFreeNetwork</code>
     * @param serviceUnit the service unit
     * @param dataRate the data rate
     * @param visualizeMessages <code>true</code> if messages are to be visualized
     * @param messagesByService
     * @param visualizeSendingRadius <code>true</code> if the sending ranges of devices are to be visualized
     * @param visualizeCommunicationLinks <code>true</code> if the communication links are to be visualized
     */
    public static void createInstance(ServiceUnit serviceUnit, double dataRate, boolean visualizeMessages, boolean visualizeSendingRadius,boolean visualizeCommunicationLinks) 
    {
    	createInstance(serviceUnit, dataRate, visualizeMessages, true, visualizeSendingRadius, visualizeCommunicationLinks, false);
    }

    /**
     * Creates an instance of a <code>CollisionFreeNetwork</code>
     * @param serviceUnit the service unit
     * @param dataRate the data rate
     * @param visualizeMessages <code>true</code> if messages are to be visualized
     * @param visualizeSendingRadius <code>true</code> if the sending ranges of devices are to be visualized
     * @param visualizeCommunicationLinks <code>true</code> if the communication links are to be visualized
	 * @param useUDGLinkLayer
     */
    public static void createInstance(ServiceUnit serviceUnit, double dataRate, boolean visualizeMessages, boolean messagesByService, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, boolean useUDGLinkLayer) 
    {
        createInstance(serviceUnit,new EndpointClassID(CollisionFreeNetwork.class.getName()),
                dataRate, new AllRecieveDecider(),
                visualizeMessages,messagesByService, visualizeSendingRadius, visualizeCommunicationLinks,useUDGLinkLayer);
        
    }
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param dataRate
     * @param receiveDecider
     * @param visualizeMessages
     * @param messagesByService
     * @param visualizeSendingRadius
     * @param visualizeCommunicationLinks
     * @param useUDGLinkLayer
     */
    public static void createInstance(ServiceUnit serviceUnit, double dataRate, ReceiveDecider receiveDecider,boolean visualizeMessages, boolean messagesByService, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, boolean useUDGLinkLayer) 
    {
        createInstance(serviceUnit,new EndpointClassID(CollisionFreeNetwork.class.getName()),
                dataRate,receiveDecider,
                visualizeMessages,messagesByService, visualizeSendingRadius, visualizeCommunicationLinks,useUDGLinkLayer);
       
    }
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param serviceID
     * @param dataRate
     * @param receiveDecider
     * @param visualizeMessages
     * @param messagesByService
     * @param visualizeSendingRadius
     * @param visualizeCommunicationLinks
     * @param useUDGLinkLayer
     */
    public static void createInstance(ServiceUnit serviceUnit, ServiceID serviceID, double dataRate, ReceiveDecider receiveDecider,boolean visualizeMessages, boolean messagesByService, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, boolean useUDGLinkLayer) 
    {
        serviceUnit.addService(new CollisionFreeNetwork(serviceID, dataRate, receiveDecider, visualizeMessages,messagesByService, visualizeSendingRadius, visualizeCommunicationLinks));
        if(useUDGLinkLayer) 
            UDGLinkLayerProxy.createFactory(serviceUnit);
        else 
            GlobalNetworkLinkLayerExtended.createFactory(serviceUnit);
    }
    
	/**
	 * Construct a new <code>CollisionFreeNetwork</code> object.
	 * @param dataRate the transmission rate in Bits per second. 
	 */
//	public CollisionFreeNetwork(double dataRate) 
//    {
//	    this(new EndpointClassID(CollisionFreeNetwork.class.getName()), dataRate,true,true,true,true);
//	}
	
	/**
     * Constructor for class <code>CollisionFreeNetwork</code>
     *
     * @param dataRate the data rate
     * @param visualizeMessages <code>true</code> if messages are to be visualized
     * @param messagesByService
     * @param visualizeSendingRadius <code>true</code> if the sending ranges of devices are to be visualized
     * @param visualizeCommunicationLinks <code>true</code> if the communication links are to be visualized
	 */
    public CollisionFreeNetwork(ServiceID serviceID, 
            double dataRate, ReceiveDecider receiveDecider, 
            boolean visualizeMessages, boolean messagesByService, boolean visualizeSendingRadius,boolean visualizeCommunicationLinks) 
    {
        this.receiveDecider=receiveDecider;
        this.visualizeMessages=visualizeMessages;
        this.messagesByService=messagesByService;
        this.visualizeSendingRadius=visualizeSendingRadius;
        this.visualizeCommunicationLinks=visualizeCommunicationLinks;
	    deviceIDInfoMap=new HashMap();
	    addressInfoMap=new HashMap();
		this.dataRate = dataRate;	
		this.serviceID=serviceID;
		graph = new NetworkGraph();
		processor = new MessageProcessor();
		queue = new MessageQueue();
		addressSet = new LinkedHashSet();
    }
	


	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	private void notifyAttach(DeviceID sender, DeviceID receiver) {
		graph.addEdge(sender, receiver);
		
	}

	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	private void notifyDetach(DeviceID sender, DeviceID receiver) {

		graph.removeEdge(sender, receiver);
		processor.removeReceiver(sender, receiver);
	}

	

	/**
	 * Method deliver.
	 * @param sender
	 */
	protected void deliver(DeviceID sender) {
		processor.deliver(sender);
		if(!queue.isEmpty(sender)) {
			queue.remove(sender).handle();
		}
	}

	/**
     * 
     * TODO Comment method
     * @param sender
     * @param message
     * @param unicastCallbackHandler
	 */
	protected void deliverInternalUnicast(DeviceID sender, LinkLayerMessage message, UnicastCallbackHandler unicastCallbackHandler) {
	    DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
	    operatingSystem.sendSignal(deviceInfo.getDeviceID(),deviceInfo.getServiceID(),
	            new GlobalLinkLayerMessageReceiver.ReceiveSignal(createLinklayerInfo(deviceInfo,deviceInfo,true),message));
	    if (unicastCallbackHandler!=null){
            linkLayerObserver.notifyUnicastProcessed(sender,deviceInfo.getLinkLayerAddress(),message);
            linkLayerObserver.notifyUnicastReceived(sender,deviceInfo.getLinkLayerAddress(),message);
            unicastCallbackHandler.notifyUnicastProcessed(deviceInfo.getLinkLayerAddress(),message);
            unicastCallbackHandler.notifyUnicastReceived(deviceInfo.getLinkLayerAddress(),message);
	        operatingSystem.finishListener(deviceInfo.getDeviceID(),unicastCallbackHandler);
	    }	        
	}

    /**
     * 
     * TODO: comment method 
     * @param sender
     * @param receiver
     * @param unicast
     * @return
     */
    protected LinkLayerInfoImplementation createLinklayerInfo(DeviceInfo sender, DeviceInfo receiver, boolean unicast) {
        double signalStrength=java.lang.Double.MAX_VALUE;
        if (!sender.equals(receiver)){
            GlobalKnowledge gk=operatingSystem.getGlobalKnowledge();
            signalStrength=1/gk.getTrajectory(sender.getDeviceID()).getPosition().distance(gk.getTrajectory(receiver.getDeviceID()).getPosition());
        }
        return new LinkLayerInfoImplementation(sender.getLinkLayerAddress(),receiver.getLinkLayerAddress(),unicast, signalStrength);
    }
    protected void deliverInternalBroadcast(DeviceID sender, LinkLayerMessage message) {
        DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
        operatingSystem.sendSignal(deviceInfo.getDeviceID(),deviceInfo.getServiceID(),
                new GlobalLinkLayerMessageReceiver.ReceiveSignal(createLinklayerInfo(deviceInfo,deviceInfo,false),message));
        linkLayerObserver.notifyBroadcastProcessed(deviceInfo.getDeviceID(),message);
        
    }

	






	


//	public SendingVisualizationInfo getSendingVisualizationInfo(LinkLayerAddress address) {
//		return new SendingVisualizationInfo(processor.isBusy(address), scheduler.getProgress(address), processor.getReceiverSet(address), processor.getMessageShape(address));
//	}

	private void handleEntry(DeviceID sender, MessageQueueEntry entry) {
		// [TRICKY 2002-12-09 jkl] bypassing the queue is only allowed if the queue
		//                         is empty *and* the processor is idle.
		
		if (queue.isEmpty(sender)) {
			if (processor.isBusy(sender)) {
				queue.add(sender, entry);
			} else {
				entry.handle();
			}
		} else {
			queue.add(sender, entry);
		}
	}

	private static class NetworkGraph {
		private HashMap senderReceiverSetMap;
		public NetworkGraph() {
			senderReceiverSetMap = new HashMap();
		}
		public void addEdge(DeviceID sender, DeviceID receiver) {
			LinkedHashSet receiverSet = (LinkedHashSet)senderReceiverSetMap.get(sender);
			if(receiverSet == null) {
				receiverSet = new LinkedHashSet();
				senderReceiverSetMap.put(sender, receiverSet);
			}
			receiverSet.add(receiver);
		}
		public void removeEdge(DeviceID sender, DeviceID receiver) {
			LinkedHashSet receiverSet = (LinkedHashSet)senderReceiverSetMap.get(sender);
			if(receiverSet != null) {
				receiverSet.remove(receiver);
			}
		}
		public boolean isConnected(DeviceID sender, DeviceID receiver) {
			LinkedHashSet receiverSet = (LinkedHashSet)senderReceiverSetMap.get(sender);
			if(receiverSet != null) {
				return receiverSet.contains(receiver);
			}
			else {
				return false;
			}
		}
		public DeviceID[] getReceiverSet(DeviceID sender) {
			DeviceID [] result;
			LinkedHashSet receiverSet = (LinkedHashSet)senderReceiverSetMap.get(sender);
			if(receiverSet != null) {
			    result=(DeviceID[])receiverSet.toArray(new DeviceID[receiverSet.size()]);
				
			}else{
			    result=new DeviceID[0];
			}
			return result;
		}
	}



	private class MessageProcessor {
		private HashMap senderPendingMessageMap;
        
		public MessageProcessor() {
			senderPendingMessageMap = new HashMap();
		}
		public Shape getMessageShape(DeviceID sender) {
			PendingMessage pm = (PendingMessage)senderPendingMessageMap.get(sender);
			if(pm == null) {
				return null;
			}
			else {
				return pm.getMessageShape();
			}
		}
		public DeviceID[] getReceiverSet(DeviceID sender) {
			PendingMessage pm = (PendingMessage)senderPendingMessageMap.get(sender);
			if(pm == null) {
				return null;
			}
			else {
				return pm.getReceiverset();
			}
		}
		public void setUnicastMessage(DeviceID sender, LinkLayerMessage message,boolean visualize, DeviceID receiver, UnicastCallbackHandler handle) {
			PendingUnicastMessage pendingUnicastMessage = new PendingUnicastMessage(message, visualize, sender,receiver, handle);
			if(!graph.isConnected(sender, receiver)) {
				pendingUnicastMessage.removeReceiver(receiver);
			}
			senderPendingMessageMap.put(sender, pendingUnicastMessage);
		}
		public void setBroadcastMessage(DeviceID sender, LinkLayerMessage message,boolean visualize, DeviceID[] receivers, BroadcastCallbackHandler handle) {
			senderPendingMessageMap.put(sender, new PendingBroadcastMessage(message,  visualize, sender,receivers,handle));
		}
		public boolean isBusy(DeviceID sender) {
			return senderPendingMessageMap.get(sender) != null;
		}
		public void removeReceiver(DeviceID sender, DeviceID receiver) {
			PendingMessage pendingMessage = ((PendingMessage)senderPendingMessageMap.get(sender));
			if(pendingMessage != null) {
				pendingMessage.removeReceiver(receiver);
			}
		}
		public void deliver(DeviceID sender) {
			PendingMessage pendingMessage = (PendingMessage)senderPendingMessageMap.remove(sender);
			if(pendingMessage != null) {
				
				pendingMessage.deliver(sender);
			}
		}
		public void remove(DeviceID address) {
			senderPendingMessageMap.remove(address);
		}
		private abstract class PendingMessage {
			protected LinkLayerMessage message;
			
            private DeviceID sender;
            private Shape shape;
            private boolean visualize;
            
			public PendingMessage(LinkLayerMessage message, boolean visualize,DeviceID sender) {
				this.message = message;
			
				this.sender=sender;
                this.visualize=visualize;
                this.shape=message.getShape();
			}
			public Shape getMessageShape() {
                if (!visualize) return null;
                if (shape==null){
                    return DEFAULT_MESSAGE_SHAPE;
                }
				return shape;
			}
			public abstract void removeReceiver(DeviceID receiver);
			public abstract void deliver(DeviceID sender);
			public abstract DeviceID[] getReceiverset();
		}
		private class PendingUnicastMessage extends PendingMessage {
			private DeviceID receiver;
			
            private boolean failed;

            private UnicastCallbackHandler handle;
			
			public PendingUnicastMessage(LinkLayerMessage message, boolean visualize, DeviceID sender, DeviceID receiver, UnicastCallbackHandler handle) {
				super(message,visualize, sender);
				this.receiver = receiver;
				this.handle=handle;
				failed=false;
			}
			public void removeReceiver(DeviceID receiver) {
				if(this.receiver != null) {
					if(this.receiver.equals(receiver)) {
						failed=true;
					}
				}
			}
			public void deliver(DeviceID sender) {
			    DeviceInfo receiverInfo=(DeviceInfo)deviceIDInfoMap.get(receiver);
			    DeviceInfo senderInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
                LinkLayerInfoImplementation info = createLinklayerInfo(senderInfo,receiverInfo,true);
                failed=failed&!receiveDecider.receive(info,operatingSystem.getGlobalKnowledge());
				if(failed) {
                    linkLayerObserver.notifyUnicastProcessed(sender,receiverInfo.getLinkLayerAddress(),message);
                    linkLayerObserver.notifyUnicastLost(sender,receiverInfo.getLinkLayerAddress(),message);
					if(handle!=null) {
                        handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),message);
                        handle.notifyUnicastLost(receiverInfo.getLinkLayerAddress(),message);
                        
//                        operatingSystem.sendCallback(handle,new  UnicastCallbackHandler.UnicastProcessedCallback(receiverInfo.getLinkLayerAddress(),message));
//                        operatingSystem.sendCallback(handle,new  UnicastCallbackHandler.UnicastLostCallback(receiverInfo.getLinkLayerAddress(),message));
					    operatingSystem.finishListener(senderInfo.getDeviceID(),handle);
					}
				}
				else {
				    operatingSystem.sendSignal(receiver,receiverInfo.getServiceID(),
				            new GlobalLinkLayerMessageReceiver.ReceiveSignal(info,message));
					
                    linkLayerObserver.notifyUnicastProcessed(sender,receiverInfo.getLinkLayerAddress(),message);
                    linkLayerObserver.notifyUnicastReceived(sender,receiverInfo.getLinkLayerAddress(),message);
					if(handle!=null) {
                        handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),message);
                        handle.notifyUnicastReceived(receiverInfo.getLinkLayerAddress(),message);
//                        operatingSystem.sendCallback(handle,new  UnicastCallbackHandler.UnicastProcessedCallback(receiverInfo.getLinkLayerAddress(),message));
//                        operatingSystem.sendCallback(handle,new  UnicastCallbackHandler.UnicastReceivedCallback(receiverInfo.getLinkLayerAddress(),message));
					    operatingSystem.finishListener(senderInfo.getDeviceID(),handle);
					}
				}
			}
			public DeviceID[] getReceiverset() {
				
				return new DeviceID[]{receiver};
			}
		}
		private class PendingBroadcastMessage extends PendingMessage {
			private List receivers;
            private BroadcastCallbackHandler handle;
			public PendingBroadcastMessage(LinkLayerMessage message,boolean visualize, DeviceID sender,DeviceID[] receivers, BroadcastCallbackHandler handle) {
				super(message,visualize,sender);
				this.receivers=new ArrayList(Arrays.asList(receivers));
                this.handle=handle;
			}
			public void removeReceiver(DeviceID receiver) {
				receivers.remove(receiver);
			}
			public void deliver(DeviceID sender) {
				Iterator it = receivers.iterator();
				DeviceInfo senderInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
				while(it.hasNext()) {
				    DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(it.next());
                    LinkLayerInfoImplementation info = createLinklayerInfo(senderInfo,deviceInfo,false);
                    if (receiveDecider.receive(info,operatingSystem.getGlobalKnowledge())){
				        operatingSystem.sendSignal(deviceInfo.getDeviceID(),deviceInfo.getServiceID(),
				            new GlobalLinkLayerMessageReceiver.ReceiveSignal(info,message));
                    }
					
				}
                linkLayerObserver.notifyBroadcastProcessed(sender,message);
				if(handle!=null) {
                    handle.notifyBroadcastProcessed(message);
				    operatingSystem.finishListener(senderInfo.getDeviceID(),handle);
				}
			}
			public DeviceID[] getReceiverset() {
				return (DeviceID[])receivers.toArray(new DeviceID[receivers.size()]);
			}
		}
	}

	private static class MessageQueue {
		private HashMap senderEntryListMap;
		public MessageQueue() {
			senderEntryListMap = new HashMap();
		}
		public boolean isEmpty(DeviceID sender) {
			LinkedList messageList = (LinkedList)senderEntryListMap.get(sender);
			if(messageList == null) {
				return true;
			}
			else {
				return messageList.isEmpty();
			}
		}
		public void add(DeviceID sender, MessageQueueEntry entry) {
			
			LinkedList messageList = (LinkedList)senderEntryListMap.get(sender);
			
			if(messageList == null) {
				messageList = new LinkedList();
				senderEntryListMap.put(sender, messageList);
			}
			//if (messageList.size()>1000) throw new IllegalStateException("Message Buffer greater 1000");
			messageList.addLast(entry);
		}
		public MessageQueueEntry remove(DeviceID sender) {
			LinkedList messageList = (LinkedList)senderEntryListMap.get(sender);
			if(messageList == null || messageList.isEmpty()) {
				throw new IllegalArgumentException("The sender has no pending messages.");
			}
			return (MessageQueueEntry)messageList.removeFirst();
		}
		public void clean(DeviceID address) {
			senderEntryListMap.remove(address);
		}
	}

	private abstract class MessageQueueEntry {
		protected DeviceID sender;
		protected LinkLayerMessage message;
		
        protected boolean visualize;
		public MessageQueueEntry(DeviceID sender, LinkLayerMessage message, boolean visualize) {
			this.sender = sender;
			this.message = message;
            this.visualize=visualize;
			
            
		}
		public abstract void handle();
	}

	private class BroadcastMessageQueueEntry extends MessageQueueEntry {
		private BroadcastCallbackHandler handle;
        /**
         * 
         * Constructor for class <code>BroadcastMessageQueueEntry</code>
         * @param sender
         * @param message
         * @param visualize
         * @param handle
         */
        public BroadcastMessageQueueEntry(DeviceID sender, LinkLayerMessage message, boolean visualize, BroadcastCallbackHandler handle) {
			super(sender, message, visualize);
            this.handle=handle;
		}
		public void handle() {
			processor.setBroadcastMessage(sender, message, visualize, graph.getReceiverSet(sender), handle);
			scheduler.add(message.getSize()/dataRate,1.0, sender);
		}
	}

	private class UnicastMessageQueueEntry extends MessageQueueEntry {
		private DeviceID receiver;
        private UnicastCallbackHandler handle;
        
        /**
         * 
         * Constructor for class <code>UnicastMessageQueueEntry</code>
         * @param sender
         * @param message
         * @param visualize
         * @param receiver
         * @param handle
         */
		public UnicastMessageQueueEntry(DeviceID sender, LinkLayerMessage message,boolean visualize, DeviceID receiver, UnicastCallbackHandler handle) {
			super(sender, message,visualize);
			this.receiver = receiver;
            this.handle=handle;
		}
		
		public void handle() {
//			DeviceID address = null;
//			if(graph.isConnected(sender, receiver)) {
//				address = receiver;
//			}
//			else {
//System.out.println("TODO: Empfänger nicht erreichbar!"); // TODO address = null führt zu NullPointerException
//			}
			// even if the receiver is not reachable the processor needs to handle the message
			processor.setUnicastMessage(sender, message, visualize, receiver, handle);
			scheduler.add(message.getSize()/dataRate,1.0, sender);
		}
		
	}

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#registerDevice(de.uni_trier.ssds.service.DeviceID, de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.ServiceID)
     */
    public void registerDevice(Address linkLayerAddress) {
        DeviceID deviceID=operatingSystem.getCallingDeviceID();
        ServiceID sender=operatingSystem.getCallingServiceID();
        DeviceInfo deviceInfo=new DeviceInfo(deviceID,linkLayerAddress,sender);
        
        deviceIDInfoMap.put(deviceID,deviceInfo);
        addressInfoMap.put(linkLayerAddress,deviceInfo);
       
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendBroadcast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
     */
    public void sendBroadcast(LinkLayerMessage message, boolean visualize) {
        
        sendBroadcast(message,visualize,null);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendBroadcast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage, de.uni_trier.ssds.service.TaskHandle)
     */
    public void sendBroadcast(LinkLayerMessage message, boolean visualize, BroadcastCallbackHandler handle) {
		// broadcast messages are received by the sender!
		// => send an internal unicast message to the sender
        
        
		if(message.getSize()<=0) throw new IllegalStateException("message size must be greater than 0");
//		if (!addressInfoMap.containsKey(senderAddress)){
//		    throw new IllegalStateException("ClientService must register itself first");
//		}
		DeviceID sender=operatingSystem.getCallingDeviceID();//((DeviceInfo)addressInfoMap.get(senderAddress)).getDeviceID();
		
		
		scheduler.addInternalBroadcast(sender, message);
		// and a broadcast message to all other receivers
		BroadcastMessageQueueEntry entry = new BroadcastMessageQueueEntry(sender, message,!messagesByService||visualize, handle);		
		handleEntry(sender, entry);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendUnicast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
     */
    public void sendUnicast(Address receiver, LinkLayerMessage message, boolean visualize) {
        sendUnicast(receiver,message,visualize,null);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendUnicast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage, de.uni_trier.ssds.service.TaskHandle)
     */
    public void sendUnicast(Address receiverAddress, LinkLayerMessage message, boolean visualize, UnicastCallbackHandler handle) {
    	if(message.getSize()<=0) throw new IllegalStateException("message size must be greater than 0");
//    	if (!addressInfoMap.containsKey(senderAddress)){
//		    throw new IllegalStateException("ClientService must register itself first");
//		}
    	if (!addressInfoMap.containsKey(receiverAddress)){
		    throw new IllegalStateException("ReceiverAddress does not exist");
		}
		DeviceID sender=operatingSystem.getCallingDeviceID();//((DeviceInfo)addressInfoMap.get(senderAddress)).getDeviceID();
		DeviceID receiver=((DeviceInfo)addressInfoMap.get(receiverAddress)).getDeviceID();
		if (sender.equals(receiver)) {
			scheduler.addInternalUnicast(sender, message,handle); 
		} else {
            if (!graph.isConnected(sender,receiver)){
                if (handle!=null){
                    handle.notifyUnicastProcessed(receiverAddress,message);
                    handle.notifyUnicastLost(receiverAddress,message);
                }
                return;
            }
			MessageQueueEntry entry = new UnicastMessageQueueEntry(sender, message,!messagesByService||visualize, receiver, handle);
			handleEntry(sender, entry); // adds it to the queue or handles it directly
		}
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.GlobalService#start(de.uni_trier.ssds.service.GlobalOperatingSystem)
     */
    public void start(GlobalOperatingSystem operatingSystem) {
        this.operatingSystem=operatingSystem;
        operatingSystem.registerSignalListener(GlobalLinkLayer.class);
        operatingSystem.registerAccessListener(GlobalLinkLayer_sync.class);
        GlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
        linkLayerObserver=new LinkLayerObserverGlobalStub(operatingSystem);
        
        globalKnowledge.addDeviceListener(new DeviceListener() {

            public void enter(DeviceID deviceID) {
                addressSet.add(deviceID);
            }

            public void exit(DeviceID deviceID) {
           		addressSet.remove(deviceID);
           		processor.remove(deviceID);
           		queue.clean(deviceID);
                Address address=((DeviceInfo)deviceIDInfoMap.get(deviceID)).getLinkLayerAddress();
                deviceIDInfoMap.remove(deviceID);
                addressInfoMap.remove(address);
            }
            
            public void changeTrack(DeviceID deviceID,
                    TrajectoryMapping trajectoryMapping, boolean suspended) {/*ignore*/}
        });
        globalKnowledge.addLinkListener(new LinkListener() {
            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.globalKnowledge.LinkListener#handleDetach(de.uni_trier.ssds.service.SimulationDeviceID, de.uni_trier.ssds.service.SimulationDeviceID)
             */
            public void handleDetach(DeviceID sender,
                    DeviceID receiver) {
                notifyDetach(sender,receiver);
            }

            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.globalKnowledge.LinkListener#handleAttach(de.uni_trier.ssds.service.SimulationDeviceID, de.uni_trier.ssds.service.SimulationDeviceID)
             */
            public void handleAttach(DeviceID sender,
                    DeviceID receiver) {
                notifyAttach(sender,receiver);
            }
        });
        
        DeviceIDIterator iterator=globalKnowledge.getNodes().iterator();
        while (iterator.hasNext()){
            addressSet.add(iterator.next());
        }
        iterator=globalKnowledge.getNodes().iterator();
        while (iterator.hasNext()){
            DeviceID deviceID=iterator.next();
            DeviceIDIterator deviceIDIterator=globalKnowledge.getConnected(deviceID);
            while (deviceIDIterator.hasNext()){
                notifyAttach(deviceID,deviceIDIterator.next());
            }
        }
        
		scheduler=new ReceiveScheduler(operatingSystem);
		scheduler.initialize(new ReceiveListener() {
			public void notifyFinished(DeviceID sender) {
				deliver(sender);
			}
		}, new InternalReceiveListener() {
			

            public void notifyBroadcastFinished(DeviceID sender, LinkLayerMessage message) {
                deliverInternalBroadcast(sender, message);
                
            }

            public void notifyUnicastFinished(DeviceID sender, LinkLayerMessage message, UnicastCallbackHandler handle) {
                deliverInternalUnicast(sender,message,handle);
                
            }
		});
 
    }

   

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getServiceID()
     */
    public ServiceID getServiceID() {
        return serviceID;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#finish()
     */
    public void finish() {
        // TODO Auto-generated method stub
        
    }
    
    public void visualize(boolean messages, boolean sendingRadii, boolean links) {
        visualizeCommunicationLinks=links;
        visualizeMessages=messages;
        visualizeSendingRadius=sendingRadii;
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getShape()
     */
    public Shape getShape() {
        if (!visualizeCommunicationLinks&&!visualizeMessages&&!visualizeSendingRadius) return null;
        Iterator iterator=addressSet.iterator();
        ShapeCollection shape=new ShapeCollection();
        GlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
        
        while (iterator.hasNext()){
            SimulationDeviceID deviceID=(SimulationDeviceID)iterator.next();
            if (visualizeSendingRadius){
                double radius=globalKnowledge.getSendingRadius(deviceID);
                shape.addShape(new EllipseShape(deviceID,new Extent(radius*2,radius*2),Color.LIGHTBLUE,false),Position.NULL_POSITION);
                //shape.addShape(new RelativeEllipseShape(deviceID,new Extent(radius*2,radius*2),Color.BLUE,false),Position.NULL_POSITION);
            }
            if (visualizeCommunicationLinks){
                DeviceIDIterator neighborIterator=globalKnowledge.getConnected(deviceID);
                while (neighborIterator.hasNext()){
                    
                    shape.addShape(new ArrowShape(neighborIterator.next(),deviceID,Color.LIGHTGREY,4),Position.NULL_POSITION);
                }
            }
           
            //optimize visualization relevant parts a little bit:
            if (processor.isBusy(deviceID)&&visualizeMessages){
                Position ownerPosition=globalKnowledge.getTrajectory(deviceID).getPosition();
                DeviceID[] deviceIDs=processor.getReceiverSet(deviceID);
                SimulationDeviceID simdeviceid;
                Shape messageShape = processor.getMessageShape(deviceID);
                for (int i=0;i<deviceIDs.length;i++){
        	        //MovingShape is a container for ordinary shapes
                	

                	if(messageShape != null) {
                        simdeviceid = (SimulationDeviceID)(deviceIDs[i]);
                        MovingShape movingShape=new MovingShape(
                                messageShape,deviceID,simdeviceid,scheduler.getProgress(deviceID));
        	        	
        	        	//MovingShape provides a progress interpolation method
        	        	//simulating the message travelling along a path in the
        	        	//network graph:
        	        	//messageShape.setProgressInfo(deviceID,simdeviceid,scheduler.getProgress(deviceID));
            	        shape.addShape(movingShape);
        	        }
                }
            }
        }
    	//return new SendingVisualizationInfo(processor.isBusy(address), scheduler.getProgress(address), processor.getReceiverSet(address), processor.getMessageShape(address));
        return shape;
    }	

    
    public LinkLayerProperties getLinkLayerProperties() {
        DeviceInfo info=(DeviceInfo) deviceIDInfoMap.get(operatingSystem.getCallingDeviceID());
        return new LinkLayerProperties(info.getLinkLayerAddress(),false,1,java.lang.Double.MAX_VALUE);
    }
    
    public void setLinkLayerProperties(LinkLayerProperties props) {
        throw new IllegalAccessError("this linkLayer does not provide changing linklayer properties");
    }
    
    public void setPromiscuous(boolean promiscuous) {
        throw new IllegalAccessError("this linkLayer does not provide promiscuous mode");
    }
    
    public Address getNetworkAddress() {
        return ((DeviceInfo) deviceIDInfoMap.get(operatingSystem.getCallingDeviceID())).getLinkLayerAddress();
    }
    
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
    public void getParameters(Parameters parameters) {
    	parameters.addParameter("dataRate", dataRate);
    	parameters.addParameter("visualizeMessages", visualizeMessages);
    	parameters.addParameter("messagesByService", messagesByService);
    	parameters.addParameter("visualizeSendingRadius", visualizeSendingRadius);
    	parameters.addParameter("visualizeCommunicationLinks", visualizeCommunicationLinks);
	}
}

