/*****************************************************************************
 * 
 * MessageTransportLayer.java
 * 
 * $Id: MessageTransportLayer.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.messageTransport; 


import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.*;
import de.uni_trier.jane.service.routing.messages.MessageDeliverService;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;
import de.uni_trier.jane.service.routing.multicast.MulticastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.transport.*;
import de.uni_trier.jane.service.routing.unicast.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.*;

public class MessageTransportLayer implements RuntimeService ,RoutingServiceListener, TransportService,TransportLayerDataReceiver{
     
    
    

    private double defaultTimeout;
    private RoutingService routingService;
    private UnicastRoutingAlgorithm_Sync unicastRoutingAlgorithm_Sync;
    private LocationRoutingAlgorithm_Sync anyCastRoutingRoutingAlgorithm_Sync;
    private LocationRoutingAlgorithm_Sync locationFloodingRoutingAlgorithm_Sync;
    private MulticastRoutingAlgorithm_Sync multicastRoutingAlgorithm_Sync;
    private RuntimeOperatingSystem operatingSystem;
    private Map pendingUnicasts;
    private ServiceID routingServiceID;
    private ServiceID unicastRoutingAlgorithmID;
    private ServiceID anyCastRoutingRoutingAlgorithmID;
    private ServiceID locationFloodingRoutingAlgorithmID;
    private Address address;
    private Position position;
    private ServiceID multicastRoutingServiceID;
    
    public static ServiceID createInstance(ServiceUnit serviceUnit,ServiceID unicastRoutingAlgorithmID,
            ServiceID anyCastRoutingRoutingAlgorithmID,
            ServiceID locationFloodingRoutingAlgorithmID, ServiceID multicastRoutingServiceID){
        ServiceID routingServiceID;
        if (serviceUnit.hasService(RoutingService.class)){
            routingServiceID=serviceUnit.getService(RoutingService.class);
        }else{
            routingServiceID=DefaultRoutingService.createInstance(serviceUnit);
        }
        return serviceUnit.addService(new MessageTransportLayer(routingServiceID,
                unicastRoutingAlgorithmID,
                anyCastRoutingRoutingAlgorithmID,
                locationFloodingRoutingAlgorithmID,
                multicastRoutingServiceID,
                60));
    }
    
    /**
     * 
     * Constructor for class <code>MessageTransportLayer</code>
     * @param routingServiceID
     * @param unicastRoutingAlgorithmID
     * @param anyCastRoutingRoutingAlgorithmID
     * @param locationFloodingRoutingAlgorithmID
     * @param multicastRoutingServiceID 
     * @param defaultTimeout
     */
    public MessageTransportLayer(ServiceID routingServiceID, 
            ServiceID unicastRoutingAlgorithmID,
            ServiceID anyCastRoutingRoutingAlgorithmID,
            ServiceID locationFloodingRoutingAlgorithmID,
            
            ServiceID multicastRoutingServiceID, double defaultTimeout) {
        this.routingServiceID=routingServiceID;
        this.unicastRoutingAlgorithmID=unicastRoutingAlgorithmID;
        this.anyCastRoutingRoutingAlgorithmID=anyCastRoutingRoutingAlgorithmID;
        this.locationFloodingRoutingAlgorithmID=locationFloodingRoutingAlgorithmID;
        this.multicastRoutingServiceID=multicastRoutingServiceID;
        
        this.defaultTimeout=defaultTimeout;
        pendingUnicasts=new HashMap();
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        operatingSystem.registerSignalListener(TransportService.class);
        routingService=(RoutingService)operatingSystem.getSignalListenerStub(routingServiceID,RoutingService.class);
        unicastRoutingAlgorithm_Sync=(UnicastRoutingAlgorithm_Sync)operatingSystem.getAccessListenerStub(unicastRoutingAlgorithmID,UnicastRoutingAlgorithm_Sync.class);
        locationFloodingRoutingAlgorithm_Sync=(LocationRoutingAlgorithm_Sync) operatingSystem.getAccessListenerStub(locationFloodingRoutingAlgorithmID,LocationRoutingAlgorithm_Sync.class);
        anyCastRoutingRoutingAlgorithm_Sync=(LocationRoutingAlgorithm_Sync) operatingSystem.getAccessListenerStub(anyCastRoutingRoutingAlgorithmID,LocationRoutingAlgorithm_Sync.class);
        
        multicastRoutingAlgorithm_Sync=(MulticastRoutingAlgorithm_Sync)operatingSystem.getAccessListenerStub(multicastRoutingServiceID,MulticastRoutingAlgorithm_Sync.class);
        //unicastRoutingAlgorithm_Sync=(UnicastRoutingAlgorithm_Sync)operatingSystem.getAccessListenerStub(algorithmID,UnicastRoutingAlgorithm_Sync.class);
        operatingSystem.registerAtService(routingServiceID,RoutingService.class);
        RoutingService_sync routingServiceSync=(RoutingService_sync)
            operatingSystem.getAccessListenerStub(routingServiceID,RoutingService_sync.class);
        address=routingServiceSync.getOwnAddress();
        
//TODO: Dirty quickFix to add sender positions!        
        if (operatingSystem.hasService(PositioningService.class)){
            ServiceID posService=operatingSystem.getServiceIDs(PositioningService.class)[0];
            operatingSystem.registerAtService(posService,
                    new PositioningListener() {
                    
                        public void removePositioningData() {
                            position=null;
                    
                        }
                    
                        public void updatePositioningData(PositioningData info) {
                            position=info.getPosition();
                    
                        }
                    
                    },
                    PositioningService.class);
            
        }
//End of quickFix
        

    }
    
    //
    public void sendUnicast(Address receiver,RoutingData data){
        sendUnicast(receiver,data,defaultTimeout,null);
    }

    //
    public void sendUnicast(Address receiver,RoutingData data, UnicastStatusHandler callbackHandler){
        sendUnicast(receiver,data,defaultTimeout,callbackHandler);  
    }
    
    //
    public void sendUnicast(Address receiver, RoutingData data, double timeout, UnicastStatusHandler callbackHandler) {
       // routingAlgorithm_Sync.getUnicastHeader(receiver);
        if (timeout<=0) timeout=defaultTimeout;
        routingService.startRoutingTask(createHeader(unicastRoutingAlgorithm_Sync.getUnicastHeader(receiver)),
                new UnicastTransportLayerData(data),new UnicastMessageObject(timeout,receiver,data,callbackHandler));    
    }
    
    private RoutingHeader createHeader(RoutingHeader routingHeader){
//TODO: Dirty quickFix
        if (position==null) return routingHeader;
        DefaultRoutingHeader dRoutingHeader=(DefaultRoutingHeader)routingHeader;
        dRoutingHeader.setSourcePosition(position);
        return dRoutingHeader;
    }
    
    
    
    public void sendLocationAnycast(Location location, RoutingData data, double timeout, UnicastStatusHandler unicastStatusHandler) {
        if (timeout<=0) timeout=defaultTimeout;
        routingService.startRoutingTask(createHeader(anyCastRoutingRoutingAlgorithm_Sync.getLocationRoutingHeader(location)),
                new UnicastTransportLayerData(data),new UnicastMessageObject(timeout,null,data,unicastStatusHandler));
        
    }
    
    public void sendLocationUnicast(Location location, Address receiver, RoutingData data, double timeout, UnicastStatusHandler callbackHandler) {
        if (timeout<=0) timeout=defaultTimeout;
        routingService.startRoutingTask(createHeader(anyCastRoutingRoutingAlgorithm_Sync.getLocationRoutingHeader(location)),
                new LocationTransportLayerData(data,receiver,timeout/2),new UnicastMessageObject(timeout,receiver,data,callbackHandler));
        
    }
    
    public void sendLocationBroadcast(Location location, RoutingData data) {
        routingService.startRoutingTask(createHeader(locationFloodingRoutingAlgorithm_Sync.getLocationRoutingHeader(location)),new BroadcastTransportLayerData(data));
        
    }
    
    
    public void sendMulticast(MulticastGroupID groupID, final RoutingData data, final BroadcastStatusHandler broadcastStatusHandler) {
        routingService.startRoutingTask(createHeader(multicastRoutingAlgorithm_Sync.getMulticastRoutingHeader(groupID)),new BroadcastTransportLayerData(data),
                new RoutingServiceCallback() {
                
                    public void deliverLocally(RoutingHeader routingHeader) {
                        // TODO Auto-generated method stub
                
                    }
                
                    public void ignoreLocally(RoutingHeader routingHeader) {
                        broadcastStatusHandler.notifyBroadcastFailure(routingHeader,data);
                        operatingSystem.finishListener(broadcastStatusHandler);
                
                    }
                
                    public void dropLocally(RoutingHeader routingHeader) {
                        broadcastStatusHandler.notifyBroadcastFailure(routingHeader,data);
                        operatingSystem.finishListener(broadcastStatusHandler);
                
                    }
                
                    public void messageProcessed(RoutingHeader routingHeader) {
                        broadcastStatusHandler.notifyBroadcastProcessed(routingHeader,data);
                        operatingSystem.finishListener(broadcastStatusHandler);
                
                    }
                
                    public void routingStarted(RoutingHeader routingHeader) {
                        // TODO Auto-generated method stub
                
                    }
                
                });
        
    }
    
    public void sendLocationBroadcast(Location location, final RoutingData data, final BroadcastStatusHandler broadcastStatusHandler) {
        
        routingService.startRoutingTask(createHeader(locationFloodingRoutingAlgorithm_Sync.getLocationRoutingHeader(location)),
                new BroadcastTransportLayerData(data),
                new RoutingServiceCallback() {
                
                    public void deliverLocally(RoutingHeader routingHeader) {
                        //broadcastStatusHandler.notifyBroadcastProcessed(routingHeader,data);
                        //operatingSystem.finishListener(broadcastStatusHandler);
                
                    }
                
                    public void ignoreLocally(RoutingHeader routingHeader) {
                        broadcastStatusHandler.notifyBroadcastFailure(routingHeader,data);
                        operatingSystem.finishListener(broadcastStatusHandler);
                    }
                
                    public void dropLocally(RoutingHeader routingHeader) {
                        broadcastStatusHandler.notifyBroadcastFailure(routingHeader,data);
                        operatingSystem.finishListener(broadcastStatusHandler);
                    }
                
                    public void messageProcessed(RoutingHeader routingHeader) {
                        broadcastStatusHandler.notifyBroadcastProcessed(routingHeader,data);
                        operatingSystem.finishListener(broadcastStatusHandler);
                    }
                
                    public void routingStarted(RoutingHeader routingHeader) {
                        // TODO Auto-generated method stub
                
                    }
                
                });
        
    }
    
    public void sendLocationCast(Location location, RoutingData data, double timeout, UnicastStatusHandler unicastStatusHandler) {
        routingService.startRoutingTask(createHeader(anyCastRoutingRoutingAlgorithm_Sync.getLocationRoutingHeader(location)),
                new LocationCastTransportLayerData(data));
        
    }
    
    public void receiveLoactionCastTransportLayerData(LocationBasedRoutingHeader routingHeader, RoutingData data) {
        sendLocationBroadcast(routingHeader.getTargetLocation(),data);
        routingService.startRoutingTask(createHeader(unicastRoutingAlgorithm_Sync.getUnicastHeader(routingHeader.getSender())), 
               new MessageTransportAck(routingHeader.getMessageID())); 
        
    }
    
    
    public void receiveBroadcastTransportLayerData(RoutingHeader routingHeader, RoutingData data) {
        operatingSystem.sendSignal(new RoutingData.DeliverMessageSignal((DefaultRoutingHeader)routingHeader,data));
        
    }

    public void receiveTransportLayerData(RoutingHeader routingHeader, RoutingData data) {
        routingService.startRoutingTask(createHeader(unicastRoutingAlgorithm_Sync.getUnicastHeader(routingHeader.getSender())), 
                new MessageTransportAck(routingHeader.getMessageID()));      
        operatingSystem.sendSignal(new RoutingData.DeliverMessageSignal((DefaultRoutingHeader)routingHeader,data));
        
    }
    
    public void receiveTransportLayerData(final RoutingHeader originalRoutingHeader, RoutingData data, Address receiver, double timeout) {
        sendUnicast(receiver,data,timeout,new UnicastStatusHandler() {
        
            public void notifyUnicastLost(RoutingHeader routingHeader, RoutingData data) {
                routingService.startRoutingTask(unicastRoutingAlgorithm_Sync.getUnicastHeader(originalRoutingHeader.getSender()), 
                        new MessageTransportNack(originalRoutingHeader.getMessageID())); 
        
            }
        
            public void notifyUnicastTimeout(RoutingHeader routingHeader,
                    RoutingData data, double timeout) {
                routingService.startRoutingTask(unicastRoutingAlgorithm_Sync.getUnicastHeader(originalRoutingHeader.getSender()), 
                        new MessageTransportNack(originalRoutingHeader.getMessageID())); 
        
            }
        
            public void notifyUnicastReceived(RoutingHeader routingHeader,
                    RoutingData data) {
                routingService.startRoutingTask(unicastRoutingAlgorithm_Sync.getUnicastHeader(originalRoutingHeader.getSender()), 
                        new MessageTransportAck(originalRoutingHeader.getMessageID())); 
        
            }
        
            public void notifyUnicastProcessed(RoutingHeader routingHeader, RoutingData data) {/*ignore*/}
        
        });
        
    }
    
    
    public void handleAck(RoutingHeader ackRoutingHeader, MessageTransportProtocolMessage data) {
       
        MessageTransportProtocolMessage protocolMessage = (MessageTransportProtocolMessage) data;
        UnicastMessageObject unicastMessageObject=(UnicastMessageObject)pendingUnicasts.get(protocolMessage.getMessageID());
        if (unicastMessageObject!=null){
            unicastMessageObject.handleAck(ackRoutingHeader);
        }        
    }
    
    public void handleNack(RoutingHeader nackRoutingHeader,MessageTransportProtocolMessage data) {
        MessageTransportProtocolMessage protocolMessage = (MessageTransportProtocolMessage) data;
        UnicastMessageObject unicastMessageObject=(UnicastMessageObject)pendingUnicasts.get(protocolMessage.getMessageID());
        if (unicastMessageObject!=null){
            unicastMessageObject.handleNack(nackRoutingHeader);
        }
        
    }

    
    public void handleDropMessage(RoutingHeader routingHeader, RoutingData routingData) {
        if (address.equals(routingHeader.getSender())) return;
        if (!(routingData instanceof UnicastTransportLayerData)) return;
        routingService.startRoutingTask(createHeader(unicastRoutingAlgorithm_Sync.getUnicastHeader(routingHeader.getSender())), 
                new MessageTransportNack(routingHeader.getMessageID()));
        
    }
    
    public void handleIgnoreMessage(RoutingHeader header, RoutingData routingData) {
        //ignore
    }
    
    public void handleDelegateMessage(RoutingHeader routingHeader, RoutingHeader oldRoutingHeader, RoutingData routingData) {
        //ignore
    }
    

    public ServiceID getServiceID() {
        // TODO Auto-generated method stub
        return null;
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
    
    private class UnicastMessageObject implements RoutingServiceCallback {
        
        private double timeout;
        private Address receiver;
        private RoutingData data;
        private UnicastStatusHandler callbackHandler;
        private RoutingHeader routingHeader;
        


        public UnicastMessageObject(double timeout, Address receiver, RoutingData data, UnicastStatusHandler callbackHandler) {
            this.timeout=timeout;
            this.receiver=receiver;
            this.data=data;
            this.callbackHandler=callbackHandler;
        }

        public void handleNack(RoutingHeader nackRoutingHeader) {
            pendingUnicasts.remove(routingHeader.getMessageID());
            if (callbackHandler!=null){
                callbackHandler.notifyUnicastLost(routingHeader,data);
            }   
        }
        
        public void handleAck(RoutingHeader ackRoutingHeader) {
            pendingUnicasts.remove(routingHeader.getMessageID());
            if (callbackHandler!=null){
                callbackHandler.notifyUnicastReceived(routingHeader,data);
            }   
            
        }

        public void deliverLocally(RoutingHeader routingHeader) {
            callbackHandler.notifyUnicastReceived(routingHeader,data);
            operatingSystem.finishListener(this);
            
        }
    
        public void ignoreLocally(RoutingHeader routingHeader) {
            //mmh was passiert hier!?!
            callbackHandler.notifyUnicastLost(routingHeader,data);
            operatingSystem.finishListener(this);
            
        }
    
        public void dropLocally(RoutingHeader routingHeader) {
            operatingSystem.finishListener(this);
            if (callbackHandler!=null){
                callbackHandler.notifyUnicastLost(routingHeader,data);
            }
            
        }
    
        public void messageProcessed(final RoutingHeader routingHeader) {
            operatingSystem.finishListener(this);
            this.routingHeader=routingHeader;
            pendingUnicasts.put(routingHeader.getMessageID(),this);
            operatingSystem.setTimeout(new ServiceTimeout(timeout) {
                public void handle() {
                    handleTimeout(routingHeader);
                }
            });
            
            if (callbackHandler!=null){
                callbackHandler.notifyUnicastProcessed(routingHeader,data);
            }
            
        }
        
        private void handleTimeout(RoutingHeader routingHeader) {
            if (pendingUnicasts.containsKey(routingHeader.getMessageID())){
                if (callbackHandler!=null){
                    callbackHandler.notifyUnicastTimeout(routingHeader,data,timeout);
                }
                pendingUnicasts.remove(routingHeader.getMessageID());
            }
            
        }
    
        public void routingStarted(RoutingHeader routingHeader) {
            
            
        }
    
    
    }

}
