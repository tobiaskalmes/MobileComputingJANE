/*****************************************************************************
 * 
 * LinklayerExtended.java
 * 
 * $Id: LinkLayerExtended_Plugin.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.routing.MessageID;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.io.IOException;
import java.util.*;

import javax.security.auth.callback.*;

public class LinkLayerExtended_Plugin implements LinkLayerExtended_async{
    private static class LLExtededACK implements LinkLayerMessage {

        private MessageID messageID;

        public LLExtededACK(MessageID messageID) {
            this.messageID=messageID;
        }

        public void handle(LinkLayerInfo info, SignalListener listener) {
            ((LinkLayerExtended_Plugin)listener).handleACKMessage(messageID,info.getSender());
            
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return LinkLayerExtended_Plugin.class;
        }

        public int getSize() {
            return messageID.getCodingSize();
        }

        public Shape getShape() {
            // TODO Auto-generated method stub
            return null;
        }

 

    }

    private class MessageObject {

        private Set leftReceivers;
        private Set nackReceivers;
        private AddressedBroadcastCallbackHandler callbackHandler;
        private LinkLayerMessage message;
        private MessageID messageID;
        private ServiceTimeout timeout;
        private int retryCount;
        private boolean broadcast;
        
        private Address[] receivers;
        
        private LinkLayerConfiguration configuration;
        

        /**
         * 
         * Constructor for class <code>MessageObject</code>
         * @param receivers
         * @param message
         * @param messageID
         * @param callbackHandler
         * @param broadcast
         * @param configuration
         */
        public MessageObject(Address[] receivers, 
                LinkLayerMessage message, 
                final MessageID messageID, 
                AddressedBroadcastCallbackHandler callbackHandler,
                boolean broadcast,
                LinkLayerConfiguration configuration) {
            this.receivers=receivers;
            this.broadcast=broadcast;
            retryCount=configuration.getRetries();
            
            leftReceivers=new HashSet(Arrays.asList(receivers));
            nackReceivers=new HashSet();
            if (callbackHandler==null){
                callbackHandler=new AddressedBroadcastCallbackHandler() {
                
                    public void notifyAddressedBroadcastTimeout(Address[] receivers,
                            Address[] failedReceivers, Address[] timeoutReceivers,
                            LinkLayerMessage message) {/*ignore*/}
                
                    public void notifyAddressedBroadcastFailed(Address receiver,
                            LinkLayerMessage message) {/*ignore*/}
                
                    public void notifyAddressedBroadcastSuccess(Address receiver,
                            LinkLayerMessage message) {/*ignore*/}
                
                    public void notifyAddressedBroadcastSuccess(Address[] receivers,
                            LinkLayerMessage message) {/*ignore*/}
                
                    public void notifyAddressedBroadcastProcessed(Address[] receivers,
                            LinkLayerMessage message) {/*ignore*/}
                
                };
            }
            this.callbackHandler=callbackHandler;
            
            this.message=message;
            this.messageID=messageID;
            this.configuration=configuration;
            timeout=new ServiceTimeout(configuration.getTimeout()/(configuration.getRetries()+1)){
                public void handle() {
                    if (map.containsKey(messageID)){
                        ((MessageObject)map.get(messageID)).handleTimeout();
                    }
                };
            };
            os.setTimeout(timeout);
            
            send();
          }
        
        private void send(){
            if (!leftReceivers.isEmpty()){
                linkLayer.sendBroadcast(new LLExtendedMessage(message,leftReceivers,receivers,broadcast,messageID,configuration.getTimeout()));
            }
        }
        
        public void handleACK(Address sender) {
            leftReceivers.remove(sender);
            callbackHandler.notifyAddressedBroadcastSuccess(sender,message);
            if (leftReceivers.isEmpty()){
                os.removeTimeout(timeout);
                //Address[] receivers = (Address[])totalReceivers.toArray(new Address[receiverSet.size()]);
                callbackHandler.notifyAddressedBroadcastProcessed(receivers,message);
                callbackHandler.notifyAddressedBroadcastSuccess(receivers,message);
                map.remove(messageID);
            }
        }
        
        /**
         * TODO Comment method
         * @param receiver
         */
        public void handleNACK(Address receiver) {
            nackReceivers.add(receiver);
            
        }
        
        
        public void handleTimeout() {
            
            if (--retryCount>=0){
                os.setTimeout(timeout);
                nackReceivers.clear();
                send();

            }else{
                Address[] timeoutReceivers=(Address[])leftReceivers.toArray(new Address[leftReceivers.size()]);
                leftReceivers.removeAll(Arrays.asList(receivers));
                callbackHandler.notifyAddressedBroadcastProcessed(receivers,message);
            
                callbackHandler.notifyAddressedBroadcastTimeout(
                        (Address[])leftReceivers.toArray(new Address[leftReceivers.size()]),
                        (Address[])nackReceivers.toArray(new Address[nackReceivers.size()]),
                        timeoutReceivers,
                        message);
                map.remove(messageID);
            }
        }





    }

    private  static class LLExtendedMessage implements LinkLayerMessage {

        private LinkLayerMessage message;
        private MessageID messageID;
        private boolean broadcast;
        private Set ackReceivers;
        private Address[] receivers;
        private double timeOut;
        

        /**
         * 
         * Constructor for class <code>LLExtendedMessage</code>
         *
         * @param message
         * @param receivers
         * @param receivers 
         * @param unicastReceiver
         * @param broadcast
         * @param messageID
         */
        public LLExtendedMessage(LinkLayerMessage message, Set ackReceivers, Address[] receivers, boolean broadcast,MessageID messageID, double timeOut) {
            this.message=message;
            this.messageID=messageID;
            this.ackReceivers=ackReceivers;
            this.receivers=receivers;
            this.timeOut=timeOut;
            this.broadcast=broadcast;
        }

        public void handle(LinkLayerInfo info, SignalListener listener) {
            ((LinkLayerExtended_Plugin)listener).handleLLExtendedMessage(info,ackReceivers,receivers,broadcast,messageID,message,timeOut);
            

        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return LinkLayerExtended_Plugin.class;
        }

        public int getSize() {
            return message.getSize()+messageID.getCodingSize()+receivers[0].getCodingSize()*receivers.length+ackReceivers.size();
        }

        public Shape getShape() {
            return message.getShape();
        }

    }

    

    private LinkLayer linkLayer;
    private RuntimeOperatingSystem os;
    private long sequenceNumber;
    protected HashMap map;
    private Set cache;
    private LinkLayerConfiguration defaultConfiguration;
    
    
    
    /**
     * 
     * Constructor for class <code>LinkLayerExtended_Plugin</code>
     * @param linkLayer
     * @param os
     * @param defaultConfiguration
     */
    public LinkLayerExtended_Plugin(LinkLayer linkLayer, RuntimeOperatingSystem os, LinkLayerConfiguration defaultConfiguration) {
        super();
        this.linkLayer = linkLayer;
        this.os = os;
        map=new HashMap();
        this.defaultConfiguration=defaultConfiguration;
        os.registerAtService(os.getServiceID(),this, LinkLayerExtended.class);
        
        cache=new HashSet();
    }

    

    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message) {
        sendAddressedBroadcast(new Address[]{receiver},message, defaultConfiguration,null);
    }

    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, final UnicastCallbackHandler callbackHandler) {
        sendAddressedBroadcast(new Address[]{receiver},message, configuration,new AddressedBroadcastCallbackHandler(){
        
            public void notifyAddressedBroadcastTimeout(Address[] receivers, Address[] failedReceivers, Address[] timeoutReceivers, LinkLayerMessage message) {
                callbackHandler.notifyUnicastUndefined(timeoutReceivers[0],message);
            }
        
            public void notifyAddressedBroadcastFailed(Address receiver, LinkLayerMessage message) {
                callbackHandler.notifyUnicastLost(receiver,message);
            }
        
            public void notifyAddressedBroadcastSuccess(Address receiver, LinkLayerMessage message) {
                callbackHandler.notifyUnicastReceived(receiver,message);
            }
        
            public void notifyAddressedBroadcastSuccess(Address[] receivers, LinkLayerMessage message) {/*ignore*/}
        
            public void notifyAddressedBroadcastProcessed(Address[] receivers, LinkLayerMessage message) {
                callbackHandler.notifyUnicastProcessed(receivers[0],message);
            }
        
        });
    }
    
    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message) {
        sendAddressedBroadcast(receivers,message,defaultConfiguration,null);
        
    }
    
//    public void sendAddressedBroadcast(final Address[] receivers, LinkLayerMessage message, final UnicastCallbackHandler callbackHandler){
//        linkLayer.sendBroadcast(new LLExtendedMessage(message,new MessageID(linkLayer.getNetworkAddress(),sequenceNumber++)),new BroadcastCallbackHandler(){
//            public void notifyBroadcastProcessed(LinkLayerMessage message) {
//                callbackHandler.notifyUnicastProcessed(receivers[0],message);
//            }
//        });
//    }
    
    public void sendAddressedBroadcast(final Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, final AddressedBroadcastCallbackHandler callbackHandler) {
        sendAddressedBroadcast(receivers,message,configuration,callbackHandler,true);
    }
    
    
    
    private void sendAddressedBroadcast(final Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, final AddressedBroadcastCallbackHandler callbackHandler, boolean broadcast) {
//        if (receivers.length==1){
//            singleReceiver(receivers[0], timeoutDelta, message, callbackHandler,broadcast);
//            return;
//        }
        if (receivers==null||receivers.length==0){

            callbackHandler.notifyAddressedBroadcastProcessed(receivers,message);
            return;
        }
        if (configuration==null){
            configuration=defaultConfiguration;
        }else{
            configuration=configuration.setDefaults(defaultConfiguration);
        }

        final MessageID messageID=new MessageID(linkLayer.getNetworkAddress(),sequenceNumber++);
        
        
        //Set receiverSet2=new HashSet(receiverSet);
        map.put(messageID,new MessageObject(receivers,message,messageID,callbackHandler,broadcast,configuration));
            
            
                
    }



    /**
     * TODO Comment method
     * @param receivers
     * @param timeoutDelta
     * @param message
     * @param callbackHandler
     */
//    private void singleReceiver(final Address receiver, double timeoutDelta, LinkLayerMessage message, final AddressedBroadcastCallbackHandler callbackHandler,boolean broadcast) {
//        UnicastCallbackHandler handler = new UnicastCallbackHandler() {
//        
//            public void notifyUnicastUndefined(Address receiver,
//                    LinkLayerMessage message) {
//                if (callbackHandler!=null){
//                    callbackHandler.notifyAddressedBroadcastTimeout(new Address[0],new Address[0],new Address[]{receiver},message);
//                }
//            }
//        
//            public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
//                if (callbackHandler!=null){
//                    callbackHandler.notifyAddressedBroadcastFailed(receiver,message);
//                    callbackHandler.notifyAddressedBroadcastTimeout(new Address[0],new Address[]{receiver},new Address[0],message);
//                }
//            }
//        
//            public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
//                if (callbackHandler!=null){
//                    callbackHandler.notifyAddressedBroadcastSuccess(receiver,message);
//                    callbackHandler.notifyAddressedBroadcastSuccess(new Address[]{receiver},message);
//                }
//            }
//        
//            public void notifyUnicastProcessed(Address receiver,
//                    LinkLayerMessage message) {
//                if (callbackHandler!=null){
//                    callbackHandler.notifyAddressedBroadcastProcessed(new Address[]{receiver},message);
//                }
//            }
//        };
//        if (broadcast){
//            linkLayer.sendAddressedBroadcast(receiver,timeoutDelta,message,handler);
//        }else{
//            linkLayer.sendUnicast(receiver,message,handler);
//        }
//        return;
//    }
    
    public void sendAddressedMulticast(final Address[] receivers,  LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        sendAddressedBroadcast(receivers,message,configuration,callbackHandler,false);
    }
    
    
    public void sendBroadcast(LinkLayerMessage message, LinkLayerConfiguration configuration, BroadcastCallbackHandler callbackHandler) {
        sendBroadcast(message,callbackHandler);
        os.write("linklayer configuration ignored!");
        
    }
    
    public void sendUnicast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        sendUnicast(receiver,message,callbackHandler);
        os.write("linklayer configuration ignored!");
        
    }
 

    public void sendUnicast(Address receiver, LinkLayerMessage message) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
    }

    public void sendUnicast(Address receiver, LinkLayerMessage message, UnicastCallbackHandler callbackHandler) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");    }

    public void sendBroadcast(LinkLayerMessage message) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");    }

    public void sendBroadcast(LinkLayerMessage message, BroadcastCallbackHandler callbackHandler) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");    }

    public void setPromiscuous(boolean promiscuous) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
        
    }

    /**
     * 
     * TODO: comment method 
     * @param info
     * @param ackReceivers
     * @param receivers
     * @param broadcast
     * @param messageID
     * @param message
     */
    protected void handleLLExtendedMessage(LinkLayerInfo info, Set ackReceivers, Address[] receivers, boolean broadcast, final MessageID messageID, LinkLayerMessage message, double timeOut) {
        if (cache.contains(messageID)) return;
        cache.add(messageID);
        os.setTimeout(new ServiceTimeout(timeOut*2) {
            public void handle() {
                cache.remove(messageID);
            }
        
        });
        if (broadcast||ackReceivers.contains(linkLayer.getNetworkAddress())){
            Address receiver;
            if (ackReceivers.contains(linkLayer.getNetworkAddress())){
                receiver=linkLayer.getNetworkAddress();
            }else if (!ackReceivers.isEmpty()){
                receiver=(Address) ackReceivers.iterator().next();
            }else{
                receiver=null;
            }
            Signal signal=new MessageReceiveSignal(new LinklayerInfoExtended(
                    broadcast,true,
                    receivers,
                    info.getSender(),
                    receiver,
                    info.getSignalStrength()),message);
        
            os.sendSignal(signal);
            
        }
        if (ackReceivers.contains(linkLayer.getNetworkAddress())){
            linkLayer.sendUnicast(info.getSender(),new LLExtededACK(messageID));
        }
    }
    
    /**
     * TODO Comment method
     * @param messageID
     * @param sender
     */
    public void handleACKMessage(MessageID messageID, Address sender) {
        
        MessageObject mo = (MessageObject)map.get(messageID);
        if (mo!=null){
            mo.handleACK(sender);
        }
        
    }


    
    

}
