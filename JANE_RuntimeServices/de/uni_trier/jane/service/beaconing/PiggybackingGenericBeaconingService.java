/*****************************************************************************
* 
* PiggybackingGenericBeaconingService.java
* 
* $Id: PiggybackingGenericBeaconingService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.service.beaconing;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.beaconing.events.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayer.LinkLayerStub;
import de.uni_trier.jane.service.network.link_layer.extended.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;



/**
 * This class is a generic beaconing service implementation. Override the abstract methods in order
 * to determine the time and period of beacon message transmissions and cleanups.
 */
public abstract class PiggybackingGenericBeaconingService extends GenericBeaconingService implements  LinkLayerExtended {

	private class AddressedBroadcastHandler implements AddressedBroadcastCallbackHandler {

        private AddressedBroadcastCallbackHandler callbackHandler;

        public AddressedBroadcastHandler(AddressedBroadcastCallbackHandler callbackHandler) {
            this.callbackHandler=callbackHandler;
        }

        public void notifyAddressedBroadcastFailed(Address receiver, LinkLayerMessage message) {
            callbackHandler.notifyAddressedBroadcastFailed(receiver, unpackMessage(message));
        }

        public void notifyAddressedBroadcastProcessed(Address[] receivers, LinkLayerMessage message) {
            callbackHandler.notifyAddressedBroadcastProcessed(receivers, unpackMessage(message));
        }

        public void notifyAddressedBroadcastSuccess(Address receiver, LinkLayerMessage message) {
            callbackHandler.notifyAddressedBroadcastSuccess(receiver, unpackMessage(message));
        }

        public void notifyAddressedBroadcastSuccess(Address[] receivers, LinkLayerMessage message) {
            callbackHandler.notifyAddressedBroadcastSuccess(receivers, unpackMessage(message));
        }

        public void notifyAddressedBroadcastTimeout(Address[] receivers, Address[] failedReceivers, Address[] timeoutReceivers, LinkLayerMessage message) {
            callbackHandler.notifyAddressedBroadcastTimeout(receivers, failedReceivers, timeoutReceivers, unpackMessage(message));
        }

 
    }

    private final class UnicastHandler implements UnicastCallbackHandler {
        private final UnicastCallbackHandler handler;

        private UnicastHandler(UnicastCallbackHandler handler) {
            super();
            this.handler = handler;
        }

        public void notifyUnicastUndefined(Address receiver,
                LinkLayerMessage message) {
            handler.notifyUnicastUndefined(receiver,unpackMessage(message));
        
        }

        public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
            handler.notifyUnicastLost(receiver,unpackMessage(message));
        
        }

        public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
            handler.notifyUnicastReceived(receiver,unpackMessage(message));
        
        }

        public void notifyUnicastProcessed(Address receiver,
                LinkLayerMessage message) {
            handler.notifyUnicastProcessed(receiver,unpackMessage(message));
        
        }
    }

    public static class PiggyBackingMessage implements LinkLayerMessage {

        private LinkLayerMessage message;
        private DefaultDataMap dataMap;
        private boolean promisc;

        public PiggyBackingMessage(LinkLayerMessage message, DefaultDataMap dataMap, boolean promisc) {
            this.message=message;
            this.dataMap=dataMap;
            this.promisc=promisc;
        }

        public void handle(LinkLayerInfo info, SignalListener listener) {
            ((PiggybackingGenericBeaconingService)listener).receivePiggybackingMessage(info,message,dataMap,promisc);

        }

        public Dispatchable copy() {

            return this;
        }


        public Class getReceiverServiceClass() {

            return PiggybackingGenericBeaconingService.class;
        }

        public int getSize() {
            return message.getSize()+dataMap.getSize();
        }

        public Shape getShape() {
            return message.getShape();
        }
        
        public LinkLayerMessage getMessage() {
            return message;
        }



    }

    public static final ServiceReference REFERENCE = new ServiceReference("beaconing") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
			if(!serviceUnit.hasService(BeaconingService.class)) {
				RandomBeaconingService.createInstance(serviceUnit);
			}
			return serviceUnit.getService(BeaconingService.class);
		}
	};

    

	
    private LinkLayerExtended_async linkLayerExtended;
    //private LinkLayer_sync linkLayer_sync;
    private static final double BEACON_DISPLAY_DELTA = 0.5;

    
    
    // initialized on startup
    
    

    


    //private LinkLayerStub linkLayerFacade;

	

    /**
     * Construct a new generic beaconing service.
     * @param linkLayerService the ID of the link layer service used to send the broadcast messages
     */
    public PiggybackingGenericBeaconingService(ServiceID ownServiceID, ServiceID linkLayerService) {
        super(ownServiceID,linkLayerService);

    }

    /**
     * 
     * TODO: comment method 
     * @param info
     * @param message
     * @param dataMap
     * @param promisc 
     */
    public void receivePiggybackingMessage(LinkLayerInfo info, LinkLayerMessage message, DefaultDataMap dataMap, boolean promisc) {
        if (info.getReceiver().equals(linkLayerAddress)||promisc ){
            runtimeOperatingSystem.sendSignal(new MessageReceiveSignal(info,message));
        }else{
           // runtimeOperatingSystem.write("fine!");
        }
        handleBeaconMessage(info,dataMap);
        
    }


    


    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
    	super.start(runtimeOperatingSystem);
    	
        runtimeOperatingSystem.registerAccessListener(LinkLayer_sync.class);
        runtimeOperatingSystem.registerSignalListener(LinkLayer_async.class);
    	if (runtimeOperatingSystem.serviceSatisfies(linkLayerService,LinkLayerExtended_async.class)){
            runtimeOperatingSystem.registerSignalListener(LinkLayerExtended_async.class);
            //runtimeOperatingSystem.registerSignalListener(LinkLayerExtended_sync.class);
    	    linkLayerExtended =(LinkLayerExtended_async)  runtimeOperatingSystem.getSignalListenerStub(linkLayerService,LinkLayerExtended_async.class);
            linkLayer_async=linkLayerExtended;
        }else{
            linkLayer_async=(LinkLayer_async)runtimeOperatingSystem.getSignalListenerStub(linkLayerService,LinkLayer_async.class);    
        }
    	
       
        if (linkLayerExtended==null){
            linkLayer_async.setPromiscuous(true);
        }    
        

    	
    }

  
	




//    // remove the receiver of a lost unicast transmission
//    private void handleUnicastError(Address address) {
//        deviceTimestampMap.remove(address);
//        notifyRemoved(address);
//    }
//
//    // notify all beacon listeners, that a neighbor has been removed
//    private void notifyRemoved(Address address) {
//        Signal signal = new BeaconingListener.RemoveNeighborSignal(address);
//        runtimeOperatingSystem.sendSignal(signal);
//    }





    
    private LinkLayerMessage createMessage(LinkLayerMessage message,boolean promisc) {
        beaconingTimer.reset();
        return new PiggyBackingMessage(message,dataMap,promisc);
    }
    //
    public void sendBroadcast(LinkLayerMessage message, final BroadcastCallbackHandler callbackHandler) {
        
        linkLayer_async.sendBroadcast(createMessage(message,true), new BroadcastCallbackHandler() {
            
            public void notifyBroadcastProcessed(LinkLayerMessage message) {
                callbackHandler.notifyBroadcastProcessed(unpackMessage(message));
        
            }
        
        });
    }
    
    public void sendBroadcast(LinkLayerMessage message, LinkLayerConfiguration configuration, final BroadcastCallbackHandler callbackHandler) {
        linkLayerExtended.sendBroadcast(createMessage(message,true),configuration, new BroadcastCallbackHandler() {
            
            public void notifyBroadcastProcessed(LinkLayerMessage message) {
                callbackHandler.notifyBroadcastProcessed(unpackMessage(message));
        
            }
        
        });
        
    }



    //
    public void sendBroadcast(LinkLayerMessage message) {
        linkLayer_async.sendBroadcast(createMessage(message,true));
    }

    //
    public void sendUnicast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, final UnicastCallbackHandler callbackHandler) {
        if (linkLayerExtended!=null){
            linkLayerExtended.sendAddressedBroadcast(receiver,createMessage(message,false),configuration,new UnicastHandler(callbackHandler));
        }else{
            linkLayer_async.sendUnicast(receiver, createMessage(message,false), new UnicastHandler(callbackHandler));    
        }

        beaconingTimer.reset();
    }
    
    
    public void sendUnicast(Address receiver, LinkLayerMessage message, UnicastCallbackHandler callbackHandler) {
        sendUnicast(receiver,message, new LinkLayerConfiguration(-1,-1),callbackHandler);
    }

   

    //
    public void sendUnicast(Address receiver, LinkLayerMessage message) {
        if (linkLayerExtended!=null){
            linkLayerExtended.sendAddressedBroadcast(receiver,createMessage(message,false));
        }else{
            linkLayer_async.sendUnicast(receiver, createMessage(message,false));
        }
    }
   
    
    public void sendAddressedBroadcast(Address receiver,  LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        linkLayerExtended.sendAddressedBroadcast(receiver, createMessage(message,true), configuration, new UnicastHandler(callbackHandler));
    }

    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message) {
        linkLayerExtended.sendAddressedBroadcast(receiver, createMessage(message,true));
    }

    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        linkLayerExtended.sendAddressedBroadcast(receivers,  createMessage(message,true), configuration, new AddressedBroadcastHandler(callbackHandler));
    }

    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message) {
        linkLayerExtended.sendAddressedBroadcast(receivers, createMessage(message,true));
    }

    public void sendAddressedMulticast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        linkLayerExtended.sendAddressedBroadcast(receivers, createMessage(message,false), configuration, new AddressedBroadcastHandler(callbackHandler));
    }

    private LinkLayerMessage unpackMessage(LinkLayerMessage message){
        if (message instanceof PiggyBackingMessage) {
            return ((PiggyBackingMessage) message).getMessage();
            
        }
        return message;
    }

    //
    public LinkLayerProperties getLinkLayerProperties() {
        return linkLayer_sync.getLinkLayerProperties();
    }

    //
    public Address getNetworkAddress() {
        return linkLayerAddress;
    }

}
