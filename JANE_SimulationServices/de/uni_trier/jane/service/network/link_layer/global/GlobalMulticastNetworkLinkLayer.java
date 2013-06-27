/*****************************************************************************
 * 
 * GlobalNetworkLinkLayer.java
 * 
 * $Id: GlobalMulticastNetworkLinkLayer.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.global;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.MessageID;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a proxy service for a GlobalLinkLayer implementation
 * All link layer signals are passed to the GlobalLinkLayer
 * This service must be started on each device to use the GlobalLinkLayer implementation
 * @see de.uni_trier.jane.service.network.link_layer.global.GlobalLinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.collision_free.CollisionFreeNetwork
 * @see de.uni_trier.jane.service.network.link_layer.shared_network.SharedNetwork
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler
 * @see de.uni_trier.jane.service.network.link_layer.BroadcastCallbackHandler
 */
// TODO der Namen sollte klar ausdr�cken, dass es sich hierbei um einen lokalen Proxy f�r GlobaleLinkLayer handelt.
public class GlobalMulticastNetworkLinkLayer extends GlobalNetworkLinkLayerProxy implements MulticastLinkLayer,SimulationService,GlobalLinkLayerMessageReceiver {

	/**
	 * @author Daniel G�rgen
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	public class MulticastEntry {

		private MessageID messageID;
		private HashSet receiverSet;
		private ListenerID handle;
		private LinkLayerMessage message;
		private ServiceTimeout multicastTimeout;
		private HashSet realReceiverSet;

		/**
		 * @param messageID
		 * @param receivers
		 * @param handle
		 * @param message
		 * @param multicastTimeout
		 */
		public MulticastEntry(MessageID messageID, Address[] receivers, ListenerID handle, LinkLayerMessage message, ServiceTimeout multicastTimeout) {
			this.messageID=messageID;
			receiverSet=new HashSet(Arrays.asList(receivers));
			realReceiverSet=new HashSet();
			this.handle=handle;
			this.message=message;
			this.multicastTimeout=multicastTimeout;
		}
		
		public void receiveACK(Address receiver){
			realReceiverSet.add(receiver);
			if (realReceiverSet.size()==receiverSet.size()){
				
				operatingService.removeTimeout(multicastTimeout);
				sendCallback();
				
			}
		}
		
		/**
		 * 
		 */
		private void sendCallback() {
			if (handle!=null){
				Address[] receivers=
					(Address[])realReceiverSet.toArray(new Address[realReceiverSet.size()]);
				receiverSet.removeAll(realReceiverSet);
				Address[] nonReceivers=
					(Address[])receiverSet.toArray(new Address[receiverSet.size()]);
				operatingService.sendSignal(handle,new MulticastCallbackHandler.MulticastReceivedCallback(
						receivers,nonReceivers,message));
			}
			
		}

		public void receiveTimeout(){
			sendCallback();
		}

	}
    //private LinkLayerAddress linkLayerAddress;
    //private ServiceID globalNetworkServiceID;
    //protected SimulationOperatingSystem operatingService;
    //private StackedClassID serviceID;
	private int multicastTimestamp;
	private static final double MULTICAST_TIMEOUT_DELTA = 1;
	private Map multicastMap;

    public static void createInstance(ServiceUnit serviceUnit) {
    	DeviceID deviceID = serviceUnit.getDeviceID();
    	Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);
    	createInstance(serviceUnit, linkLayerAddress);
    }

    public static void createInstance(ServiceUnit serviceUnit, Address linkLayerAddress) {
    	ServiceID globalLinkLayerService = serviceUnit.getService(GlobalLinkLayer.class);
		GlobalMulticastNetworkLinkLayer linkLayerProxy = new GlobalMulticastNetworkLinkLayer(
				linkLayerAddress, globalLinkLayerService);
		serviceUnit.addService(linkLayerProxy);
    }
    
    /**
     * Cosntructor for class GlobalNetworkLinkLayer
     * @param linkLayerAddress			the linkLayerAddress for this device
     * @param globalNetworkServiceID	the serviceID of the GlobalLinkLayer 
     */
    public GlobalMulticastNetworkLinkLayer(Address linkLayerAddress, ServiceID globalNetworkServiceID) {
        super(new StackedClassID(GlobalMulticastNetworkLinkLayer.class.getName(),globalNetworkServiceID),linkLayerAddress,globalNetworkServiceID);

        multicastMap = new HashMap();
    }

 
    
    private boolean visualize() {
        
        return operatingService.isVisualized(operatingService.getCallingServiceID());
    }

 
    







	/* (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.MulticastLinkLayer#sendMulticast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress[], de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
	 */
	public void sendMulticast(Address[] receivers, LinkLayerMessage message) {
		sendMulticast(receivers,message,null);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.MulticastLinkLayer#sendMulticast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress[], de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage, de.uni_trier.jane.signaling.TaskHandle)
	 */
	public void sendMulticast(final Address[] receivers, LinkLayerMessage message, final ListenerID handle) {
	    
		final MessageID messageID=new MessageID(operatingService.getDeviceID(),multicastTimestamp++);
		MulticastMessageContainer messageContainer=new MulticastMessageContainer(receivers,message,messageID);
        globalLinkLayer.sendBroadcast(messageContainer,visualize(),new BroadcastCallbackHandler() {
					
					public void notifyBroadcastProcessed(
							LinkLayerMessage message) {
						if (handle!=null){
							operatingService.sendSignal(handle,
									new MulticastCallbackHandler.MulticastProcessedCallback(receivers,message));
						}
					}

					
				});
		ServiceTimeout multicastTimeout=new ServiceTimeout(MULTICAST_TIMEOUT_DELTA) {
			public void handle() {
				((MulticastEntry)multicastMap.remove(messageID)).receiveTimeout();
			}
		};
		operatingService.setTimeout(multicastTimeout);
		multicastMap.put(messageID,new MulticastEntry(messageID,receivers,handle,message,multicastTimeout));
		
		
	}
    


    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayerMessageReceiver#deliverMessage(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
     */
    public void deliverMessage(LinkLayerInfo info, LinkLayerMessage linkLayerMessage) {
    	if (linkLayerMessage instanceof MulticastMessage){
    		linkLayerMessage.handle(info,this);	
    	}else{
    		operatingService.sendSignal(new MessageReceiveSignal(info,linkLayerMessage));
    	}
    }


	/**
     * 
     * TODO: comment method 
     * @param info
     * @param receivers
     * @param message
     * @param messageID
	 */
	public void receiveMulticastContainer(final LinkLayerInfo info,final Address[] receivers, final LinkLayerMessage message, MessageID messageID) {
		boolean contains=false;
		for (int i=0;i<receivers.length;i++){
			if (receivers[i].equals(linkLayerAddress)){
				contains = true;
				continue;
			}
		}
		if (!contains) return;
		MulticastMessageACK messageACK=new MulticastMessageACK(messageID);
		globalLinkLayer.sendUnicast(info.getSender(),messageACK,false, new UnicastCallbackHandler() {
					/* (non-Javadoc)
					 * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastProcessed(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
					 */
					public void notifyUnicastProcessed(
							Address receiver, LinkLayerMessage message) {/*ignore*/}

					/* (non-Javadoc)
					 * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastReceived(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
					 */
					public void notifyUnicastReceived(
							Address receiver, LinkLayerMessage otherMessage) {
						operatingService.sendSignal(new MessageReceiveSignal(
								new MulticastLinkLayerInfoImplementation(receiver,linkLayerAddress,receivers,info.getSignalStrength()),message));
						
						
					}

					/* (non-Javadoc)
					 * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastLost(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
					 */
					public void notifyUnicastLost(Address receiver,
							LinkLayerMessage message) {
						// TODO Handle that?!? repaet sending?!? deliver message?!?
					}

					/* (non-Javadoc)
					 * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastUndefined(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
					 */
					public void notifyUnicastUndefined(
							Address receiver, LinkLayerMessage message) {
						// TODO Handle that?!?
					}

				}); 
				
		
	}

	/**
	 * @param sender
	 * @param messageID
	 */
	public void receiveMulticastACK(Address sender, MessageID messageID) {
		if (multicastMap.containsKey(messageID)){
			((MulticastEntry)multicastMap.get(messageID)).receiveACK(sender);
		}else{
			//TODO: use debug mode
			operatingService.write("Delayed ACK received from device "+sender);
		}
		
	}





}
