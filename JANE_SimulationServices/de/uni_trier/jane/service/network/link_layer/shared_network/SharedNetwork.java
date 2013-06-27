/*******************************************************************************
 * 
 * SharedNetwork.java
 * 
 * $Id: SharedNetwork.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 * 
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K.
 * Lehnert
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 ******************************************************************************/
package de.uni_trier.jane.service.network.link_layer.shared_network;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.collision_free.*;
import de.uni_trier.jane.service.network.link_layer.global.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a IEEE802.11 like network.
 * It realize the concurrent media access by emulating the sequest to send/clear to send
 * protocol used in IEEE802.11. Thus, devices are only  able to receive one unicast from 
 * one device at a time. Hidden terminal boradcasts to the same device are possible at a 
 * time, but are discarted. Moreover, only one device in the direct neighborhood is able 
 * to send messages (media sense collision avoidance).   
 * Unicasts are a bit more reliable than in the origninal IEEE802.11, but can also fail when
 * RTS/CTS is not possible. I.e. when to devices are sending when comming into communication 
 * range.
 * This network implementation sends callbacks:
 *		<code>BroadcastObserver.BroadcastProcessedCallback</code>
 *		<code>UnicastObserver.UnicastProcessedCallback</code>
 *			The Message is send.
 *		<code>UnicastObserver.UnicastReceivedCallback</code>
 *			The unicast message is received correctly by the receiving device
 * 		<code>UnicastObserver.UnicastLostCallback</code>
 * 			The unicast message is lost 
 * 		<code>UnicastObserver.UnicastUndefinedCallback</code>
 * 			The unicast message is probably lost (last unicast acknowledge was lost) 
 * 
 * This network is implemented as a Global Service and with global knowledge. It needs 
 * device attach and detach events, enter and exit events.
 * To access this network from a devcice a lokal service proxy is needed, to pass messages 
 * to this network implementation. As default proxy implementation 
 * <code>GlobalNetworkLinkLayer</code> can be used.
 * Proxies can be implemented as <code>EvaluationService</code>. They have to register
 * themselves at this service be sending an <code>GlobalLinkLayer.RegisterDeviceSignal</code>,
 * providing their <code>LinkLayerAddress</code>.
 * From now on, messages can be send by using the following signals:
 * 	<code>GlobalLinkLayer.SendUnicastSignal</code>	
 * 		send a unicast message
 *  <code> GlobalLinkLayer.SendUnicastTask</code>
 * 		send a unicast message and register a <code>UnicastObserver</code> callback handler
 * 	<code>GlobalLinkLayer.SendBroadcastSignal</code>
 * 		send a broadcast message
 * 	<code>GlobalLinkLayer.SendBroadcastTask</code>
 * 		send a broadcast message and register a <code>BroadcastObserver</code> callback handler
 * 
 * 
 * @see BroadcastCallbackHandler.BroadcastProcessedCallback
 * @see UnicastCallbackHandler.UnicastProcessedCallback
 * @see UnicastCallbackHandler.UnicastReceivedCallback
 * @see UnicastCallbackHandler.UnicastLostCallback
 * @see UnicastCallbackHandler.UnicastUndefinedCallback
 * @see GlobalLinkLayer.RegisterDeviceSignal
 * @see GlobalLinkLayer.SendUnicastSignal
 * @see GlobalLinkLayer.SendUnicastTask
 * @see GlobalLinkLayer.SendBroadcastSignal
 * @see GlobalLinkLayer.SendBroadcastTask
 *  
 */
public class SharedNetwork implements GlobalService,GlobalLinkLayer,GlobalLinkLayer_sync {
    
    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, 10240*8);
    }

    public static void createInstance(ServiceUnit serviceUnit, double dataRate) {
    	createInstance(serviceUnit, dataRate,true,true,true);
    }
    
    public static void createInstance(ServiceUnit serviceUnit, double dataRate,boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks){
        createInstance(serviceUnit,dataRate,false,visualizeMessages,visualizeSendingRadius,visualizeCommunicationLinks);
    }
    
    public static void createInstance(ServiceUnit serviceUnit, double dataRate,boolean promiscous,boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks){
        serviceUnit.addService(new SharedNetwork(dataRate,promiscous,visualizeMessages,visualizeSendingRadius,visualizeCommunicationLinks));
        GlobalNetworkLinkLayerExtended.createFactory(serviceUnit);    
    }

  
    
    /**
     * This MessageQueueEntry stores a broadcast send request until it is possible to send the message
	 */
	private class BroadcastMessageQueueEntry implements MessageQueueEntry {

		private DeviceID sender;
		private LinkLayerMessage message;
		private BroadcastCallbackHandler handle;

		/**
		 * Constructor for class BroadcastMessageQueueEntry
		 * @param sender	deviceID of sender device
		 * @param message	the message to send
		 * @param handle	the TaskHandle for callbacks
		 */
		public BroadcastMessageQueueEntry(DeviceID sender, LinkLayerMessage message, BroadcastCallbackHandler handle) {
			this.sender=sender;
			this.message=message;
			this.handle=handle;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.WLANNetwork.MessageQueueEntry#handle()
		 */
		public void handle() {
			MessageProcessor senderProcessor=(MessageProcessor)addressMessageProcessorMap.get(sender);
			senderProcessor.sendBroadcast(message,handle);
		}
	}
	
	 /**
     * This MessageQueueEntry stores a unicast send request until it is possible to send the message
	 */
	private class UnicastMessageQueueEntry implements MessageQueueEntry {

	    private DeviceID sender;
		private DeviceID receiver;
		private LinkLayerMessage message;
		private UnicastCallbackHandler handle;
		

		/**
		 * Constructor of class UnicastMessageQueueEntry
		 * @param sender	the sending device
		 * @param receiver	the receiving device
		 * @param message	the message to send
		 * @param handle	the  TaskHandle for callbacks
		 */
		public UnicastMessageQueueEntry(DeviceID sender,DeviceID receiver, LinkLayerMessage message, UnicastCallbackHandler handle) {
			this.sender=sender;
			this.receiver=receiver;
			this.message=message;
			this.handle=handle;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.WLANNetwork.MessageQueueEntry#handle()
		 */
		public void handle() {
			MessageProcessor senderProcessor=(MessageProcessor)addressMessageProcessorMap.get(sender);
			senderProcessor.sendUnicast(receiver,message,handle);
		}

	}
	
	/**
	 * This is the supercall for all MessageQueueEntrys. A MessageQueueEntry stores a send request until it is possible to send the message
	 */
	private interface MessageQueueEntry {

		/**
		 *	This method is called when it is possible to prepare the message sending 
		 */
		void handle();
	}
	
	
	/**
	 * The MessageQueue stores MessageQueueEntries containing send request until it is possible to prepare the message send process.
	 * This queue represents the queues on all active devices 
	 */
	private class MessageQueue {

		private HashMap senderMap;

		/**
		 * Constructor for class MessageQueue
		 */
		public MessageQueue() {
			senderMap=new HashMap();
		}

		/**
		 * Checks wether the queue of device sender is empty
		 * @param sender	the deviceID 
		 * @return	true, if queue on device sender is empty
		 */
		public boolean isEmpty(DeviceID sender) {
			return !senderMap.containsKey(sender);
		}

		/**
		 * Adds a MessageQueueEntry to device sender
		 * @param sender	the deviceID 
		 * @param entry		the MessageQueueEntry to add
		 */
		public void add(DeviceID sender,MessageQueueEntry entry) {
			Vector messageQueue;
			if (senderMap.containsKey(sender)){
				messageQueue=(Vector)senderMap.remove(sender);
			}else {
				messageQueue=new Vector();
			}
			messageQueue.add(entry);
			senderMap.put(sender,messageQueue);
		}
		
		/**
		 * Returns the first MessageQueueEntry from the queu of device sender and removes it.
		 * @param sender	the deviceID
		 * @return	the first MessageQueueEntry
		 */
		public MessageQueueEntry removeFirstEntry(DeviceID sender){
			if (senderMap.containsKey(sender)){
				Vector messageQueue=(Vector)senderMap.get(sender);
				MessageQueueEntry entry=(MessageQueueEntry)messageQueue.elementAt(0);
				messageQueue.removeElementAt(0);
				if (messageQueue.isEmpty()){
					senderMap.remove(sender);
				}
				return entry;
			}
			throw new IllegalArgumentException("No Messages left in Queue for this Device");
		}

		/**
		 * Returns the size of the queue on device sender
		 * @param sender	the deviceID
		 * @return	the size of the devices queue
		 */
		public int getSize(DeviceID sender) {
			if (!senderMap.containsKey(sender)) return 0;
			return ((Vector)senderMap.get(sender)).size();
		}

	}
	
	/**
	 *	This class stores the NetworkGraph of all known devices 
	 */
	private class NetworkGraph {
		private HashMap senderMap;
		
		/**
		 *Constructor for class NetworkGraph 
		 */
		public NetworkGraph() {
			senderMap=new HashMap();
		}
		
		/**
		 *adds a new device to the graph 
		 * @param sender the deviceID to add
		 */
		protected void add(DeviceID sender){
			LinkedHashSet receiverMap=new LinkedHashSet();
			senderMap.put(sender,receiverMap);
			
		}
		
		/**
		 * Removes an device from the graph
		 * @param address	the device to remove
		 */
		public void remove(DeviceID address) {
			senderMap.remove(address);
		}
		
		/**
		 * Adds an edge from sender to receiver to the graph
		 * @param sender	senderDevice
		 * @param receiver	receiverDevice
		 */
		protected void add(DeviceID sender, DeviceID receiver){
			if (senderMap.containsKey(sender)){
				((LinkedHashSet)senderMap.get(sender)).add(receiver);
			} else throw new IllegalStateException("Sender does not exist!");
		}
		
		/**
		 * Removes an edge from sender to receiver from the graph
		 * @param sender	senderDevice
		 * @param receiver	receiverDevice
		 */
		protected void remove(DeviceID sender, DeviceID receiver){
			if (senderMap.containsKey(sender)){
				LinkedHashSet receiverMap=(LinkedHashSet)senderMap.get(sender);
				receiverMap.remove(receiver);
			}else throw new IllegalStateException("Sender does not exist!");
		}
			
		/**
		 * Checks, if the graph contains an edge from sender to receiver 
		 * @param sender	the sender device
		 * @param receiver	the receiver device
		 * @return	true if an edge exists
		 */
		protected boolean isConnected(DeviceID sender, DeviceID receiver) {
			if (senderMap.containsKey(sender)){
				LinkedHashSet receiverMap=(LinkedHashSet)senderMap.get(sender);
				return receiverMap.contains(receiver);
			}
			return false;
		}	
	}
	
	/**
	 * The physikal layer package size. Needed to determine wether the last uncast acknowledge is lost
	 */
	final double VIRTUAL_PACKET_SIZE = 100;
	
	/**
	 * The maximum length of the message queues
	 */
	final int MAX_MESSAGE_QUEUE_LENGTH=100;
	
	
	
	
	protected HashMap addressMessageProcessorMap;
	private MessageQueue messageQueue;

	private double dataRate;
	private NetworkGraph networkGraph;
	private NetworkStatistic networkStatistic;
   
    private GlobalOperatingSystem operatingSystem;
    protected HashMap deviceIDInfoMap;
    protected HashMap linkLayerAddressInfoMap;
    private EndpointClassID serviceID;

    private boolean visualizeMessages;

    private boolean visualizeSendingRadius;

    private boolean visualizeCommunicationLinks;

	private GlobalTimestampCreator timestampCreator;

    private boolean promisc;
	
	

	/**
	 * Creates a new SharedNetwork with the given datarate
	 * @param dataRate 	the network datarate
	 */
	public SharedNetwork(double dataRate) {
		this(dataRate,false,new EmptyNetworkStatistic(),true,true,true);
		
	}
	
	/**
	 * Creates a new SharedNetwork with the given datarate
	 * @param dataRate 	the network datarate
     * @param promiscous 
	 * @param visualizeMessages 		show message shapes
	 * @param visualizeSendingRadius 	show devices sending radius
	 * @param visualizeCommunicationLinks	show links beteen devices 
2 
	 */
	public SharedNetwork(double dataRate, boolean promiscous,boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks) {
		this(dataRate,promiscous,new EmptyNetworkStatistic(),visualizeMessages,visualizeSendingRadius,visualizeCommunicationLinks);
		
	}
	
	
	/**
	 * Creates a new SharedNetwork with the given datarate and a network statistic object
	 * @param dataRate					the network datarate
	 * @param promiscous 
	 * @param networkStatistic			statistic object to collect network statistic.
	 * @param visualizeMessages 		show message shapes
	 * @param visualizeSendingRadius 	show devices sending radius
	 * @param visualizeCommunicationLinks	show links beteen devices
	 */
	public SharedNetwork(double dataRate, boolean promiscous, NetworkStatistic networkStatistic,boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks) {
        promisc=promiscous;
	    this.visualizeMessages=visualizeMessages;
	    this.visualizeSendingRadius=visualizeSendingRadius;
	    this.visualizeCommunicationLinks=visualizeCommunicationLinks;
		this.networkStatistic=networkStatistic;
		this.dataRate=dataRate;
		addressMessageProcessorMap=new HashMap();
		networkGraph=new NetworkGraph();
		messageQueue=new MessageQueue();
		deviceIDInfoMap=new HashMap();
		linkLayerAddressInfoMap=new HashMap();
		serviceID=new EndpointClassID(getClass().getName());
		timestampCreator=new GlobalTimestampCreator();
	}


	/**
	 * Is called, when a device enters the simulation
	 * @param address	the deviceID of the device
	 */
	protected void notifyEnter(DeviceID address) {
		MessageProcessor messageProcessor=new MessageProcessor(address,dataRate,operatingSystem,this,timestampCreator,networkStatistic,promisc);
		networkGraph.add(address);
		addressMessageProcessorMap.put(address,messageProcessor);
	}

	/**
	 * Is called, when a device leaves the simulation
	 * @param address	the deviceID of the device
	 */
	protected void notifyExit(DeviceID address) {
		MessageProcessor messageProcessor=(MessageProcessor)addressMessageProcessorMap.remove(address);
		messageProcessor.notifyExit();
		networkGraph.remove(address);
	}

	/**
	 * Is called, when device receiver comes into communication range of device sender
	 * @param sender		sending device
	 * @param receiver		receiving device
	 */
	protected void notifyAttach(
	        DeviceID sender,
	        DeviceID receiver) {
		networkGraph.add(sender,receiver);
		((MessageProcessor)addressMessageProcessorMap.get(sender)).notifyAttach(receiver,(MessageProcessor)addressMessageProcessorMap.get(receiver));
	}

	/**
	 * Is called, when device receiver comes ou of communication range of device sender
	 * @param sender		sending device
	 * @param receiver		receiving device
	 */ 
	protected void notifyDetach(DeviceID sender, DeviceID receiver) {
		networkGraph.remove(sender,receiver);
		((MessageProcessor)addressMessageProcessorMap.get(sender)).notifyDetach(receiver);

	}



    /**
     * Returns the deviceInfo containing the deviceID, the proxy serviceID for this deviceand the corresponding LinkLayerAddress
     * for a given deviceID
     * @param address	the deviceID
     * @return	the deviceInfo
     */
	DeviceInfo getDeviceInfo(DeviceID address){
	    return (DeviceInfo)deviceIDInfoMap.get(address);
	}

	/**
	 * Is called when the last message has been send by device sender
	 * @param sender	the deviceID of the senders device
	 */
	public void sendFinished(DeviceID sender) {
		if (!messageQueue.isEmpty(sender)){	
			messageQueue.removeFirstEntry(sender).handle();
		}
	}

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.GlobalService#start(de.uni_trier.ssds.service.GlobalOperatingSystem)
     */
    public void start(final GlobalOperatingSystem operatingSystem) {
        this.operatingSystem=operatingSystem;
        
        operatingSystem.registerSignalListener(GlobalLinkLayer.class);
        operatingSystem.registerAccessListener(GlobalLinkLayer_sync.class);
        GlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
        globalKnowledge.addDeviceListener(new DeviceListener() {

            public void enter(DeviceID deviceID) {
               
                notifyEnter(deviceID);
            }

            public void exit(DeviceID deviceID) {
                
                notifyExit(deviceID);
                DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(deviceID);
                linkLayerAddressInfoMap.remove(deviceInfo.getLinkLayerAddress());
                deviceIDInfoMap.remove(deviceID);
            }
            
            public void changeTrack(DeviceID deviceID,
                    TrajectoryMapping trajectoryMapping, boolean suspended) {/*ignore*/}
        });
        globalKnowledge.addLinkListener(new LinkListener() {
            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.globalKnowledge.LinkListener#handleDetach(de.uni_trier.ssds.service.SimulationDeviceID, de.uni_trier.ssds.service.SimulationDeviceID)
             */
            public void handleDetach(DeviceID sender, DeviceID receiver) {
                
                notifyDetach(sender,receiver);
            }

            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.globalKnowledge.LinkListener#handleAttach(de.uni_trier.ssds.service.SimulationDeviceID, de.uni_trier.ssds.service.SimulationDeviceID)
             */
            public void handleAttach(DeviceID sender, DeviceID receiver) {
                notifyAttach(sender,receiver);
            }
        });
    
        DeviceIDIterator iterator=globalKnowledge.getNodes().iterator();
        while (iterator.hasNext()){
            notifyEnter(iterator.next());
        }
        iterator=globalKnowledge.getNodes().iterator();
        while (iterator.hasNext()){
            DeviceID deviceID=iterator.next();
            DeviceIDIterator deviceIDIterator=globalKnowledge.getConnected(deviceID);
            while (deviceIDIterator.hasNext()){
                notifyAttach(deviceID,deviceIDIterator.next());
            }
        }
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
        //SimulationGlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
        //TODO: remove listeners from global knowledge!
        //globalKnowledge.
        
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
        GlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
        ShapeCollection shape=new ShapeCollection();
        Iterator iterator=addressMessageProcessorMap.keySet().iterator();
        while(iterator.hasNext()){
            DeviceID deviceID=(DeviceID)iterator.next();
            if (visualizeSendingRadius){
                double radius=globalKnowledge.getSendingRadius(deviceID);
                shape.addShape(new EllipseShape(deviceID,new Extent(radius*2,radius*2),Color.BLUE,false),Position.NULL_POSITION);
            }
            if (visualizeCommunicationLinks){
                DeviceIDIterator neighborIterator=globalKnowledge.getConnected(deviceID);
                while (neighborIterator.hasNext()){
//TODO Demonstrator fasr visualization fix                    
//                    shape.addShape(new ArrowShape(deviceID,neighborIterator.next(),Color.LIGHTGREY,4),Position.NULL_POSITION);
                	shape.addShape(new LineShape(deviceID,neighborIterator.next(),Color.LIGHTGREY),Position.NULL_POSITION);
                }
            }
            shape.addShape(((MessageProcessor)addressMessageProcessorMap.get(deviceID)).getShape(),Position.NULL_POSITION);
        }
        return shape;
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendBroadcast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.LinkLayerMessage)
     */
    public void sendBroadcast(LinkLayerMessage message, boolean visualize) {
        sendBroadcast(message,visualize,null);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendBroadcast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.LinkLayerMessage, de.uni_trier.ssds.service.TaskHandle)
     */
    public void sendBroadcast(LinkLayerMessage message, boolean visualize, BroadcastCallbackHandler handle) {
        
        DeviceID sender=operatingSystem.getCallingDeviceID();
    	if (addressMessageProcessorMap.containsKey(sender)){
			MessageProcessor senderProcessor=(MessageProcessor)addressMessageProcessorMap.get(sender);
			if (messageQueue.isEmpty(sender)&&senderProcessor.isFree()){
				senderProcessor.sendBroadcast(message,handle);
			}else{
				if (messageQueue.getSize(sender)>MAX_MESSAGE_QUEUE_LENGTH) {
                    operatingSystem.write(senderProcessor.medium.getReserveQueue().prioReserveList.firstKey().toString());
					throw new IllegalStateException ("Network message queue full: "+ MAX_MESSAGE_QUEUE_LENGTH);
        
                }
				messageQueue.add(sender,new BroadcastMessageQueueEntry(sender,message,handle));
			}
		}else throw new IllegalArgumentException("Sender does not exist!");

        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendUnicast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.LinkLayerMessage)
     */
    public void sendUnicast(Address receiver, LinkLayerMessage message, boolean visualize) {
        sendUnicast(receiver,message,visualize,null);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#sendUnicast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.LinkLayerMessage, de.uni_trier.ssds.service.TaskHandle)
     */
    public void sendUnicast(Address receiverAddress, LinkLayerMessage message, boolean visualize, UnicastCallbackHandler handle) {
        
        if (!linkLayerAddressInfoMap.containsKey(receiverAddress)){
            //TODO: 
            throw new IllegalAccessError("Unknown receiver");
        }
        DeviceID sender=operatingSystem.getCallingDeviceID();
        DeviceID receiver=((DeviceInfo)linkLayerAddressInfoMap.get(receiverAddress)).getDeviceID();
    	if (addressMessageProcessorMap.containsKey(sender)){
			
			MessageProcessor senderProcessor=(MessageProcessor)addressMessageProcessorMap.get(sender);
			if (messageQueue.isEmpty(sender)&&senderProcessor.isFree()){
				senderProcessor.sendUnicast(receiver,message,handle);
			}else{
				if (messageQueue.getSize(sender)>MAX_MESSAGE_QUEUE_LENGTH) 
                    throw new IllegalStateException("Network message queue full: "+MAX_MESSAGE_QUEUE_LENGTH);
				messageQueue.add(sender,new UnicastMessageQueueEntry(sender,receiver,message,handle));
			}
		}else throw new IllegalArgumentException("Sender does not exist!");

        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayer#registerDevice(de.uni_trier.ssds.service.DeviceID, de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.ServiceID)
     */
    public void registerDevice(Address linkLayerAddress) {
        DeviceID deviceID = operatingSystem.getCallingDeviceID();
        ServiceID senderServiceID=operatingSystem.getCallingServiceID();
        DeviceInfo info=new DeviceInfo(deviceID,linkLayerAddress,senderServiceID);
        deviceIDInfoMap.put(deviceID,info);
        linkLayerAddressInfoMap.put(linkLayerAddress,info);
    }
    
    
    public LinkLayerProperties getLinkLayerProperties() {
        DeviceInfo info=(DeviceInfo) deviceIDInfoMap.get(operatingSystem.getCallingDeviceID());
        MessageProcessor messageProcessor=(MessageProcessor)addressMessageProcessorMap.get(operatingSystem.getCallingDeviceID());
        if (messageProcessor!=null&&info!=null){
            return new LinkLayerProperties(info.getLinkLayerAddress(),messageProcessor.isPromiscuous(),-1, Double.MAX_VALUE);
        }
        throw new IllegalAccessError("Device does not exists: "+operatingSystem.getCallingDeviceID());
    }
    public Address getNetworkAddress() {
        DeviceInfo info=(DeviceInfo) deviceIDInfoMap.get(operatingSystem.getCallingDeviceID());
        if (info!=null){
            return info.getLinkLayerAddress();
        }
        throw new IllegalAccessError("Device does not exists: "+operatingSystem.getCallingDeviceID());
    }
    public void setPromiscuous(boolean promiscuous) {
        MessageProcessor messageProcessor=(MessageProcessor)addressMessageProcessorMap.get(operatingSystem.getCallingDeviceID());
        if (messageProcessor!=null){
            messageProcessor.setPromiscuous(promiscuous);
        }else{
            throw new IllegalAccessError("Device does not exists: "+operatingSystem.getCallingDeviceID());
        }
        
    }
    
    public void setLinkLayerProperties(LinkLayerProperties props) {
        throw new IllegalAccessError("this linkLayer does not provide changing linklayer properties");
    }

    
    
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
    public void getParameters(Parameters parameters) {
        parameters.addParameter("dataRate", dataRate);
        parameters.addParameter("promiscuousMode",promisc);
        parameters.addParameter("visualizeMessages", visualizeMessages);
        
        parameters.addParameter("visualizeSendingRadius", visualizeSendingRadius);
        parameters.addParameter("visualizeCommunicationLinks", visualizeCommunicationLinks);
    }



}
