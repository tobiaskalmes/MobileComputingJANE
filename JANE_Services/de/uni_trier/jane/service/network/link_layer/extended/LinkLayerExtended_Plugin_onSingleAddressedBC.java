/*****************************************************************************
 * 
 * LinklayerExtended.java
 * 
 * $Id: LinkLayerExtended_Plugin_onSingleAddressedBC.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import java.util.*;


public class LinkLayerExtended_Plugin_onSingleAddressedBC implements LinkLayerExtended_async{
    private class LLExtededACK implements LinkLayerMessage {

        private MessageID messageID;

        public LLExtededACK(MessageID messageID) {
            this.messageID=messageID;

        }

        public void handle(LinkLayerInfo info, SignalListener listener) {
            ((LinkLayerExtended_Plugin_onSingleAddressedBC)listener).handleACKMessage(messageID,info.getSender());
            
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return LinkLayerExtended_Plugin_onSingleAddressedBC.class;
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

        private Set receiverSet;
        private Set totalReceivers;
        private Set nackReceivers;
        private AddressedBroadcastCallbackHandler callbackHandler;
        private LinkLayerMessage message;
        private MessageID messageID;
        private ServiceTimeout timeout;
        private boolean retry;
        private boolean broadcast;
        private LinkLayerConfiguration configuration;
        private boolean finished;
        

        public MessageObject(Set receiverSet, 
                Set success, Set failed, LinkLayerMessage message, 
                LinkLayerConfiguration configuration,
                MessageID messageID, 
                ServiceTimeout timeout, 
                AddressedBroadcastCallbackHandler callbackHandler,
                boolean broadcast,
                boolean retry ) {
            this.receiverSet=receiverSet;
            totalReceivers=success;
            nackReceivers=failed;
            this.callbackHandler=callbackHandler;
            this.configuration=configuration;
            this.message=message;
            this.messageID=messageID;
            this.timeout=timeout;
            //this.retry=retry;
            retry=false;

          }
        
        
        public void handleACK(Address sender) {
            totalReceivers.add(sender);
            nackReceivers.remove(sender);
            receiverSet.remove(sender);

                
            
            callbackHandler.notifyAddressedBroadcastSuccess(sender,message);
            checkFinished();
        }


        /**
         * TODO Comment method
         */
        private boolean checkFinished() {
            if (receiverSet.isEmpty()){
                if (finished) 
                    return true;
                finished=true;
                os.removeTimeout(timeout);
                Address[] receivers = (Address[])totalReceivers.toArray(new Address[totalReceivers.size()]);
                callbackHandler.notifyAddressedBroadcastProcessed(receivers,message);
                if (nackReceivers.isEmpty()){
                    callbackHandler.notifyAddressedBroadcastSuccess(receivers,message);
                }else{
                    callbackHandler.notifyAddressedBroadcastTimeout(receivers,
                            (Address[])nackReceivers.toArray(new Address[nackReceivers.size()]),
                            new Address[0],message);
                }
                return true;
            }
            return false;
        }
        
        /**
         * TODO Comment method
         * @param receiver
         */
        public void handleNACK(Address receiver) {
            nackReceivers.add(receiver);
            
            if (!checkFinished()){
                sendAddressedBroadcast(receiverSet,totalReceivers,nackReceivers,message,configuration,callbackHandler,broadcast,false);
            }
            
        }
        
        
        public void handleTimeout() {
            Address[] receivers = (Address[])receiverSet.toArray(new Address[receiverSet.size()]);
            receiverSet.removeAll(totalReceivers);
            receiverSet.removeAll(nackReceivers);
//            if (retry){
//                //todo: korrekte receiver menge für callback
//                sendAddressedBroadcast((Address[])receiverSet.toArray(new Address[receiverSet.size()]),message,configuration,callbackHandler,broadcast,false);
//            }else{
                callbackHandler.notifyAddressedBroadcastProcessed(receivers,message);
            
                callbackHandler.notifyAddressedBroadcastTimeout(
                        (Address[])totalReceivers.toArray(new Address[totalReceivers.size()]),
                        (Address[])nackReceivers.toArray(new Address[nackReceivers.size()]),
                        (Address[])receiverSet.toArray(new Address[receiverSet.size()]),
                        message);
                map.remove(messageID);
//            }
        }





    }

    private  class LLExtendedMessage implements LinkLayerMessage {

        private LinkLayerMessage message;
        private MessageID messageID;
        private boolean broadcast;
        private Set receivers;
        private Address unicastReceiver;
        private Set ackReceivers;

        /**
         * 
         * Constructor for class <code>LLExtendedMessage</code>
         *
         * @param message
         * @param receivers
         * @param ackReceivers 
         * @param unicastReceiver
         * @param broadcast
         * @param messageID
         */
        public LLExtendedMessage(LinkLayerMessage message, Set receivers,  Set ackReceivers, Address unicastReceiver, boolean broadcast,MessageID messageID) {
            this.message=message;
            this.messageID=messageID;
            this.receivers=receivers;
            this.ackReceivers=ackReceivers;
            this.unicastReceiver=unicastReceiver;
            this.broadcast=broadcast;
        }



        public void handle(LinkLayerInfo info, SignalListener listener) {
            ((LinkLayerExtended_Plugin_onSingleAddressedBC)listener).handleLLExtendedMessage(info,receivers,ackReceivers,unicastReceiver,broadcast,messageID,message);
            

        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return LinkLayerExtended_Plugin_onSingleAddressedBC.class;
        }

        public int getSize() {

            return message.getSize()+messageID.getCodingSize()+(receivers.size()+1)*(unicastReceiver.getCodingSize()+1);
        }

        public Shape getShape() {
            return message.getShape();
        }

    }

    private LinkLayerExtended linkLayer;
    private RuntimeOperatingSystem os;
    private long sequenceNumber;
    protected HashMap map;
    private LinkLayerConfiguration defaultConfiguration;
    
    
    
    /**
     * Constructor for class <code>LinkLayerExtended_Plugin_onSingleAddressedBC</code>
     *
     * @param linkLayer
     * @param os
     */
    public LinkLayerExtended_Plugin_onSingleAddressedBC(LinkLayerExtended linkLayer, RuntimeOperatingSystem os, LinkLayerConfiguration defaultConfiguration) {
        super();
        this.linkLayer = linkLayer;
        this.defaultConfiguration=defaultConfiguration;
        this.os = os;
        map=new HashMap();
        os.registerAtService(os.getServiceID(),this, LinkLayerExtended.class);
    }

    

    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message) {
        throw new IllegalStateException("Single addressed broadcast must be provided by the original link layer");
        
    }

    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, final UnicastCallbackHandler callbackHandler) {
        throw new IllegalStateException("Single addressed broadcast must be provided by the original link layer");
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
        sendAddressedBroadcast(new HashSet(Arrays.asList(receivers)), new HashSet(), new HashSet(),message,configuration,callbackHandler,true,true);
    }
    public void sendAddressedMulticast(final Address[] receivers, LinkLayerMessage message,LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        sendAddressedBroadcast(new HashSet(Arrays.asList(receivers)), new HashSet(), new HashSet(),message,configuration,callbackHandler,false,true);
    }
    
    
    
    private void sendAddressedBroadcast(Set receivers, Set success, Set failed, LinkLayerMessage message, LinkLayerConfiguration configuration,final AddressedBroadcastCallbackHandler callbackHandler, boolean broadcast, boolean retry) {
        Address firstReceiver=(Address) receivers.iterator().next();
        if (receivers.size()==1&&failed.size()==0){
            singleReceiver(firstReceiver,success, message,configuration, callbackHandler,broadcast);
            return;
        }
        Set receiverSet=new HashSet(receivers);
        
        receiverSet.remove(firstReceiver);
        HashSet ackReceivers = new HashSet(receiverSet);
        receiverSet.addAll(failed);
        receiverSet.addAll(success);
        
        
        
        final MessageID messageID=new MessageID(linkLayer.getNetworkAddress(),sequenceNumber++);
        

        
        linkLayer.sendAddressedBroadcast(firstReceiver,new LLExtendedMessage(message,receiverSet,ackReceivers,firstReceiver,broadcast,messageID),configuration,
                new UnicastCallbackHandler(){
            public void notifyUnicastProcessed(Address receiver, LinkLayerMessage message) {
                
          
            }
            public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
                if(callbackHandler!=null)
                    callbackHandler.notifyAddressedBroadcastFailed(receiver,message);
                if (map.containsKey(messageID)){
                    MessageObject mob=(MessageObject)map.get(messageID);
                    mob.handleNACK(receiver);
                }
                
            }
            public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
                if(callbackHandler!=null)
                    callbackHandler.notifyAddressedBroadcastSuccess(receiver,message);
                if (map.containsKey(messageID)){
                    MessageObject mob=(MessageObject)map.get(messageID);
                    mob.handleACK(receiver);
                }
                
            }
            public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message) {
                /*ignore*/
                notifyUnicastLost(receiver,message);
            }
        });
        
        
        ServiceTimeout timeout=new ServiceTimeout(configuration.getTimeout()){
            public void handle() {
                ((MessageObject)map.get(messageID)).handleTimeout();
            };
        };
        map.put(messageID,new MessageObject(new HashSet(receivers),success,failed,message,configuration,messageID,timeout,callbackHandler,broadcast,retry));
        os.setTimeout(timeout);
            
                
    }



    /**
     * 
     * TODO Comment method
     * @param receiver
     * @param success
     * @param message
     * @param configuration
     * @param callbackHandler
     * @param broadcast
     */
    private void singleReceiver(final Address receiver, final Set success, LinkLayerMessage message, LinkLayerConfiguration configuration,final AddressedBroadcastCallbackHandler callbackHandler,boolean broadcast) {
        
        UnicastCallbackHandler handler = new UnicastCallbackHandler() {
        
            public void notifyUnicastUndefined(Address receiver,
                    LinkLayerMessage message) {
                if (callbackHandler!=null){
                    callbackHandler.notifyAddressedBroadcastTimeout(
                            (Address[]) success.toArray(new Address[success.size()]),new Address[0],new Address[]{receiver},message);
                }
            }
        
            public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
                if (callbackHandler!=null){
                    callbackHandler.notifyAddressedBroadcastFailed(receiver,message);
                    callbackHandler.notifyAddressedBroadcastTimeout(
                            (Address[]) success.toArray(new Address[success.size()]),new Address[]{receiver},new Address[0],message);
                }
            }
        
            public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
                if (callbackHandler!=null){
                    callbackHandler.notifyAddressedBroadcastSuccess(receiver,message);
                    success.add(receiver);
                    callbackHandler.notifyAddressedBroadcastSuccess(
                            (Address[]) success.toArray(new Address[success.size()]),message);
                }
            }
        
            public void notifyUnicastProcessed(Address receiver,
                    LinkLayerMessage message) {
                if (callbackHandler!=null){
                    callbackHandler.notifyAddressedBroadcastProcessed(new Address[]{receiver},message);
                }
            }
        };
        if (broadcast){
            linkLayer.sendAddressedBroadcast(receiver,message,configuration,handler);
        }else{
            linkLayer.sendUnicast(receiver,message,configuration,handler);
        }
        return;
    }
    

 

    public void sendUnicast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
        
    }
    public void sendUnicast(Address receiver, LinkLayerMessage message) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
    }

    public void sendUnicast(Address receiver, LinkLayerMessage message, UnicastCallbackHandler callbackHandler) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
    }

    public void sendBroadcast(LinkLayerMessage message, LinkLayerConfiguration configuration, BroadcastCallbackHandler callbackHandler) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
    }
    public void sendBroadcast(LinkLayerMessage message) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");    }

    public void sendBroadcast(LinkLayerMessage message, BroadcastCallbackHandler callbackHandler) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");    }

    public void setPromiscuous(boolean promiscuous) {
        throw new IllegalAccessError("Method mus be realized by LinkLayer");
        
    }

    /**
     * TODO Comment method
     * @param info
     * @param receivers 
     * @param ackReceivers 
     * @param unicastReceiver 
     * @param broadcast 
     * @param messageID 
     * @param message 
     */
    protected void handleLLExtendedMessage(LinkLayerInfo info, Set receivers, Set ackReceivers, Address unicastReceiver, boolean broadcast, MessageID messageID, LinkLayerMessage message) {
        Address receiver;
        if (receivers.contains(linkLayer.getNetworkAddress())){
            receiver=linkLayer.getNetworkAddress();
        }else{
            receiver=unicastReceiver;
        }
        Signal signal=new MessageReceiveSignal(new LinklayerInfoExtended(
                    broadcast,true,
                    (Address[])receivers.toArray(new Address[receivers.size()]),
                    info.getSender(),receiver,info.getSignalStrength()),message);
        if (broadcast||receivers.contains(linkLayer.getNetworkAddress())||unicastReceiver.equals(linkLayer.getNetworkAddress())){
            os.sendSignal(signal);
            
        }
        if (ackReceivers.contains(linkLayer.getNetworkAddress())){
            linkLayer.sendUnicast(info.getSender(),new LLExtededACK(messageID));
//            ,new UnicastCallbackHandler() {
//            
//                public void notifyUnicastUndefined(Address receiver,
//                        LinkLayerMessage message) {
//                    os.write("undefined");
//            
//                }
//            
//                public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
//                    os.write("lost");
//            
//                }
//            
//                public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
//                    // TODO Auto-generated method stub
//            
//                }
//            
//                public void notifyUnicastProcessed(Address receiver,
//                        LinkLayerMessage message) {
//                    // TODO Auto-generated method stub
//            
//                }
//            
//            });
        }
    }
    
    /**
     * TODO Comment method
     * @param messageID
     * @param sender
     */
    public void handleACKMessage(MessageID messageID, Address sender) {
        MessageObject object=(MessageObject)map.get(messageID);
        if (object!=null)
            object.handleACK(sender);
        
    }


    
    

}
