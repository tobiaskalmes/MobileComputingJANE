/*****************************************************************************
 * 
 * TransportLayerServiceIpmlementation.java
 * 
 * $Id: TransportLayerServiceIpmlementation.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.messageTransport;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.MessageReceiveSignal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.transport.*;
import de.uni_trier.jane.service.routing.unicast.UnicastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.unit.ServiceUnit;

import de.uni_trier.jane.visualization.shapes.Shape;

import java.util.*;

public class TransportLayerServiceIpmlementation implements RuntimeService, TransportLayerService, RoutingServiceListener, TransportLayerDataReceiver {
    
    
    
    
    private List cache;
    private static int maxSize=100;
    
    private ServiceID serviceID;
    
    private ServiceID defaultRoutingServiceID;
    private RoutingService routingService;
    private Address address;
    private Map pendingEndToEnd;
    
    
    //DefaultConfiguration
//    private double defaultTimeout=10;
//    private int defaultRetries=0;

    private RuntimeOperatingSystem operatingSystem;
    private ServiceID defaultUnicastService;
    private TransportHeader defaultHeader=new TransportHeader(null,10,1,null,null);
    
    
    
    
    /**
     * 
     * Constructor for class <code>TransportLayerServiceIpmlementation</code>
     *
     * @param defaultRoutingServiceID
     * @param defaultUnicastService
     * @param cacheSize
     * @param defaultRetry
     * @param defaultTimeout
     */
    public TransportLayerServiceIpmlementation(
            ServiceID defaultRoutingServiceID, ServiceID defaultUnicastService,
            int cacheSize, int defaultRetry, double defaultTimeout) {
        
        
        this.defaultRoutingServiceID=defaultRoutingServiceID;
        maxSize=cacheSize;
        defaultHeader=new TransportHeader(null,defaultTimeout,defaultRetry,null,null);
        serviceID= new StackedClassID(getClass().getName(),defaultRoutingServiceID);
        this.defaultUnicastService=defaultUnicastService;
        pendingEndToEnd=new HashMap();
        cache=new ArrayList();
    }
    

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        routingService=(RoutingService)runtimeOperatingSystem.getSignalListenerStub(defaultRoutingServiceID,RoutingService.class);
        
        RoutingService_sync routingService_sync = (RoutingService_sync)runtimeOperatingSystem.getAccessListenerStub(defaultRoutingServiceID,RoutingService_sync.class);
        address=routingService_sync.getOwnAddress();
        runtimeOperatingSystem.registerAtService(defaultRoutingServiceID,RoutingService.class);
    }
    
    
    public void startTransportTask(TransportHeader transportHeader, RoutingData data) {
        startTransportTask(transportHeader,data,null);
    }
    
    public void startTransportTask(TransportHeader transportHeader, RoutingData data, MessageStatusHandler statusHandler) {
        transportHeader.initDefault(defaultHeader);
        TransportLayerData2 transportLayerdata = new TransportLayerData2(data,transportHeader);
        routingService.startRoutingTask(transportHeader.getRoutingHeader(),transportLayerdata,
                new MessageObject(transportHeader,transportLayerdata,statusHandler));
    }
    

    
//    public void sendEndToMiddleEnd(RoutingData data, RoutingHeader routingHeader, double timeout, int retries, MessageStatusHandler statusHandler){
//        
//    }
    
    public void handleDelegateMessage(RoutingHeader routingHeader, RoutingHeader oldRoutingHeader, RoutingData routingData) {
        if (routingData instanceof TransportLayerData2){
            TransportLayerData2 data=(TransportLayerData2)routingData;
            if (data.getMiddleEnd()!=null&&oldRoutingHeader.getRoutingAlgorithmID().equals(data.getMiddleEnd())){
                sendAck(routingHeader,data);
            }
        }
        
    }
    
    
    
    
    public void handleDropMessage(RoutingHeader routingHeader, RoutingData routingData) {
        //send a NACK
        sendNack(routingHeader, routingData);
        //repropagate DROP 
        
        
        
    }


    /**
     * TODO: comment method 
     * @param routingHeader
     * @param routingData
     */
    private void sendNack(RoutingHeader routingHeader, RoutingData routingData) {
        if (routingData instanceof TransportLayerData2 ){
            TransportLayerData2 data=(TransportLayerData2)routingData;
            if (data.getMiddleEnd()!=null&&!data.getMiddleEnd().equals(routingHeader.getRoutingAlgorithmID())) return;
            RoutingHeader replyHeader=getReplyHeader(routingHeader,data);
            routingService.startRoutingTask(routingHeader,new MessageTransportNack(routingHeader.getMessageID()));
        }
    }

    public void handleIgnoreMessage(RoutingHeader header, RoutingData routingData) {
        //also NACK?
        sendNack(header,routingData);
        //repropagate Ignore
    
    }
    
    public void receiveTransportLayerData(RoutingHeader routingHeader, TransportLayerData2 data) {
        if (routingHeader.hasReceiver()&&address.equals(routingHeader.getReceiver())&&
                (data.getMiddleEnd()==null||data.getMiddleEnd().equals(routingHeader.getRoutingAlgorithmID()))){
            sendAck(routingHeader,data);
        }
        if (!cache.contains(routingHeader.getMessageID())){
            operatingSystem.sendSignal(new RoutingData.DeliverMessageSignal((DefaultRoutingHeader)routingHeader,data.getData()));
            cache.add(routingHeader.getMessageID());
            if (cache.size()>maxSize){
                cache.remove(0);
            }
        }else{
            //drop
            //operatingSystem.write("drop");
        }
        
    }

    
    
    private void sendAck(RoutingHeader routingHeader, TransportLayerData2 data) {
        RoutingHeader replyHeader = getReplyHeader(routingHeader, data);
        
        //try reply
        routingService.startRoutingTask(replyHeader,new MessageTransportAck(routingHeader.getMessageID()));
    }


    /**
     * TODO: comment method 
     * @param routingHeader
     * @param data
     * @return
     */
    private RoutingHeader getReplyHeader(RoutingHeader routingHeader, TransportLayerData2 data) {
        ServiceID replyService=data.getReplyService();
        if (replyService==null){
            replyService=routingHeader.getRoutingAlgorithmID();
        }
        RoutingHeader replyHeader=null;
        if (operatingSystem.serviceSatisfies(replyService, ReplyableRoutingAlgorithm_Sync.class)){
            ReplyableRoutingAlgorithm_Sync ura=(ReplyableRoutingAlgorithm_Sync)
            operatingSystem.getAccessListenerStub(routingHeader.getRoutingAlgorithmID(), ReplyableRoutingAlgorithm_Sync.class);
            replyHeader=ura.getHeaderForReply(routingHeader);
        }
        
        //use the sending service
        if (replyHeader==null){
            if (!operatingSystem.serviceSatisfies(replyService, UnicastRoutingAlgorithm_Sync.class)){
                replyService=defaultUnicastService;
            }
            UnicastRoutingAlgorithm_Sync ura=(UnicastRoutingAlgorithm_Sync)
                operatingSystem.getAccessListenerStub(replyService, UnicastRoutingAlgorithm_Sync.class);
            replyHeader=ura.getUnicastHeader(routingHeader.getSender());
        }
        return replyHeader;
    }
    

    public void handleNack(RoutingHeader nackRoutingHeader, MessageTransportProtocolMessage data) {
        MessageObject messageObject=(MessageObject)pendingEndToEnd.get(data.getMessageID());
        if (messageObject!=null){
            messageObject.handleNack(nackRoutingHeader);
        }        
    }


    public void handleAck(RoutingHeader ackRoutingHeader, MessageTransportProtocolMessage data) {
        MessageObject messageObject=(MessageObject)pendingEndToEnd.get(data.getMessageID());
        if (messageObject!=null){
            messageObject.handleAck(ackRoutingHeader);
        }
        
    }


    public Address getOwnAddress(){
        return address;
    }
    
    public ServiceID getServiceID() {
        return serviceID;
    }

    public void finish() {
        // TODO Auto-generated method stub

    }

    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub

    }

    
//    private abstract class MessageObject implements RoutingServiceCallback{
//        protected double timeout;
//        protected RoutingData data;
//        protected MessageStatusHandler callbackHandler;
//        protected RoutingHeader routingHeader;
//        
//        public MessageObject(double timeout, RoutingData data, MessageStatusHandler callbackHandler) {
//            this.timeout=timeout;
//            this.data=data;
//            this.callbackHandler=callbackHandler;
//        }
//        
//        public abstract void handleNack(RoutingHeader nackRoutingHeader);
//        public abstract void handleAck(RoutingHeader ackRoutingHeader);
//        
//    }
    
    
    
    private class MessageObject implements RoutingServiceCallback{
        private int retry;
        private ServiceTimeout timeout;
        protected TransportLayerData2 data;
        protected MessageStatusHandler callbackHandler;
        protected RoutingHeader routingHeader;
        public MessageObject(TransportHeader transportHeader, TransportLayerData2 transportLayerdata, MessageStatusHandler statusHandler) {
            this.data=transportLayerdata;
            this.routingHeader=transportHeader.getRoutingHeader();
            retry=transportHeader.getRetries();
            this.callbackHandler=statusHandler;
            timeout=new ServiceTimeout(transportHeader.getTimeout()) {
                public void handle() {
                    handleTimeout(routingHeader);
                }
            };
            operatingSystem.setTimeout(timeout);
            
        }

        

        public void handleNack(RoutingHeader nackRoutingHeader) {
            if (retry>0){
                routingService.startRoutingTask(routingHeader,data,this);
                retry--;
            }else{
                finishMessage();
                if (callbackHandler!=null){
                    callbackHandler.notifyMessageLost(routingHeader,data);
                }
            }   
        }
        
        private void finishMessage() {
            pendingEndToEnd.remove(routingHeader.getMessageID());
            operatingSystem.removeTimeout(timeout);
            
        }

        public void handleAck(RoutingHeader ackRoutingHeader) {
            finishMessage();
            if (callbackHandler!=null){
                callbackHandler.notifyMessageReceived(routingHeader,data);
            }   
            
        }

        public void deliverLocally(RoutingHeader routingHeader) {
            callbackHandler.notifyMessageReceived(routingHeader,data);
            finishMessage();
            
            
        }
    
        public void ignoreLocally(RoutingHeader routingHeader) {
            //mmh was passiert hier!?!
            callbackHandler.notifyMessageLost(routingHeader,data);
            finishMessage();
            
            
        }
    
        public void dropLocally(RoutingHeader routingHeader) {
            //operatingSystem.
            //handleNack(routingHeader);
            finishMessage();
            
            
        }
    
        public void messageProcessed(final RoutingHeader routingHeader) {
            pendingEndToEnd.put(routingHeader.getMessageID(),this);

            if (callbackHandler!=null){
                callbackHandler.notifyMessageProcessed(routingHeader,data);
            }
            
        }
        
        private void handleTimeout(RoutingHeader routingHeader) {
            if (pendingEndToEnd.containsKey(routingHeader.getMessageID())){
                if (callbackHandler!=null){
                    callbackHandler.notifyMessageTimeout(routingHeader,data.getData(),timeout.getDelta());
                }
                pendingEndToEnd.remove(routingHeader.getMessageID());
            }
            
        }
    
        public void routingStarted(RoutingHeader routingHeader) {
            
            
        }
    
    
    }


    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
        
        ServiceID unicastRouting=serviceUnit.getService(UnicastRoutingAlgorithm_Sync.class);
        if (unicastRouting==null){
            throw new IllegalStateException("No Unicast routing defined");
        }
        return createInstance(serviceUnit,unicastRouting);
    }


    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @param unicastRouting
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID unicastRouting) {

        return createInstance(serviceUnit,unicastRouting,100,0,10.0);
    }

    /**
     * 
     * Creates a TransportLayerFramworkService 
     * @param serviceUnit
     * @param unicastRouting
     * @param maxCacheSize
     * @param defaultMaxRetries
     * @param defaultTimeout
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, ServiceID unicastRouting, int maxCacheSize, int defaultMaxRetries, double defaultTimeout) {
        ServiceID defaultRoutingService=serviceUnit.getService(RoutingService.class);
        if (defaultRoutingService==null){
            defaultRoutingService=DefaultRoutingService.createInstance(serviceUnit);
        }
        return serviceUnit.addService(new TransportLayerServiceIpmlementation(defaultRoutingService,unicastRouting,maxCacheSize,defaultMaxRetries,defaultTimeout));
    }






   




    
}
