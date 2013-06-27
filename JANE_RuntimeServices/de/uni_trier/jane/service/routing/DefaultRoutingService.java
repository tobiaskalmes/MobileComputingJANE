/****************************************************************************
* 
* $Id: DefaultRoutingService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
package de.uni_trier.jane.service.routing;

import java.util.*;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.ServiceTimeout;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.network.link_layer.BroadcastCallbackHandler;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfoImplementation;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_sync;
import de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler;
import de.uni_trier.jane.service.network.link_layer.extended.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.events.MessageReceiveEvent;
import de.uni_trier.jane.service.routing.logging.LocalRoutingLogService;
import de.uni_trier.jane.service.routing.logging.loop_checker.LoopChecker;
import de.uni_trier.jane.service.routing.logging.loop_checker.LoopCheckerFactory;
import de.uni_trier.jane.service.routing.messages.MessageDeliverService;
import de.uni_trier.jane.service.routing.unicast.UnicastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.parametrized.parameters.InitializationContext;
import de.uni_trier.jane.simulation.parametrized.parameters.Parameter;
import de.uni_trier.jane.simulation.parametrized.parameters.service.ServiceElement;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * This class implements a default routing service for messages.
 * It delegates and manages the message routing tasks for routing algorithms  
 */
public class DefaultRoutingService implements RuntimeService, RoutingService, RoutingService_sync, MessageDeliverService 
{

	/**
     * @author goergen
     *
     * TODO comment class
     */
    private  final class MyLocalRoutingLogService implements
            LocalRoutingLogService {
        Set routingLogs=new LinkedHashSet();;

        public void addLoggingAlgorithm(ServiceID routingAlgorithmToLog) {
           //ignore
        }

        public void finish() {
            //ignore
        }

        public void getParameters(Parameters parameters) {
            //ignore
        }

        public ServiceID getServiceID() {
            //ignore
            return null;
        }

        public Shape getShape() {
            //ignore
            return null;
        }

        public void logDelegateMessage(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logDelegateMessage(routingAlgorithmID, messageID, routingHeader);    
            }
            
        }

        public void logDeliverMessage(RoutingHeader header) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logDeliverMessage(header);
            }
        }

        public void logDropMessage(RoutingHeader header) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logDropMessage(header);
            }
        }

        public void logForwardBroadcast(MessageID messageID, RoutingHeader header) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logForwardBroadcast(messageID, header);
            }
        }

        public void logForwardError(MessageID messageID, RoutingHeader header, Address receiver) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                   service.logForwardError(messageID, header, receiver);
            }
        }

        public void logForwardMulticast(MessageID messageID, RoutingHeader header, Address[] receivers) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logForwardMulticast(messageID, header, receivers);
            }
        }

        public void logForwardUnicast(MessageID messageID, RoutingHeader header, Address receiver) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logForwardUnicast(messageID, header, receiver);
            }
        }

        public void logIgnoreMessage(RoutingHeader header) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logIgnoreMessage(header);
            }
        }

        public void logLoopMessage(RoutingHeader header, int loopLength) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logLoopMessage(header, loopLength);
            }
        }

        public void logMessageReceived(MessageID messageID, RoutingHeader header, Address sender) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logMessageReceived(messageID, header, sender);
            }
        }

        public void logStart(RoutingHeader header) {
            Iterator iterator=routingLogs.iterator();
            while (iterator.hasNext()) {
                LocalRoutingLogService service = (LocalRoutingLogService) iterator.next();
                service.logStart(header);
            }
        }

        /**
         * TODO Comment method
         * @param serviceID
         */
        public void addLogger(ServiceID routingLogID) {
            routingLogs.add(operatingService.getSignalListenerStub(routingLogID, LocalRoutingLogService.class));
            
        }
        

    }

    public static final ServiceID SERVICE_ID = new EndpointClassID(DefaultRoutingService.class.getName());
	
	public static final ServiceElement SERVICE_ELEMENT = new ServiceElement("defaultRoutingService") 
	{
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) 
		{
			LoopCheckerFactory loopCheckerFactory = (LoopCheckerFactory)LoopCheckerFactory.LOOP_CHECKER_FACTORY.getValue(initializationContext, serviceUnit);
			DefaultRoutingService.createInstance(serviceUnit, false, -1, loopCheckerFactory);
		}
		
		public Parameter[] getParameters() 
		{
			return new Parameter[] { LoopCheckerFactory.LOOP_CHECKER_FACTORY };
		}
	};
	
    /**
     * Initialized in Constructor
     */ 
	protected 	Address 				address;
	private 	ServiceID 				linkLayerServiceID;
	
	private 	LoopCheckerFactory  	loopCheckerFactory;
	
    private 	ServiceID 				routingLogID;
    private 	boolean 				traceAllMessages;
    //protected 	LocalRoutingLogService 	routingLog;
    private 	int 					nextSequenceNumber;
    
    private MyLocalRoutingLogService routingLog;

    /**
     * Initialized on startup
     */ 
    protected 	RuntimeOperatingSystem operatingService;

    private 	int 					maxHopCount;

    private 	LinkLayer_async 		linkLayerFacade;
    private 	LinkLayerExtended_async linkLayerExtended;
    
    private 	ContinuousDistribution 	messageProcessingTime;

    public LinkLayerConfiguration defaultConfiguration=new LinkLayerConfiguration(-1,-1);

    /**
     * Creates an instance of the <code>DefaultRoutingService</code>
     * @param serviceUnit the <code>ServiceUnit</code> to add the service to
     * @return the <code>ServiceID</code> of the created service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) 
    {
    	return createInstance(serviceUnit, false);
    }

    /**
     * Creates an instance of the <code>DefaultRoutingService</code>
     * @param serviceUnit the <code>ServiceUnit</code> to add the service to
     * @param traceAllMessages <code>true</code> if a routing log is to be used
     * @return the <code>ServiceID</code> of the created service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean traceAllMessages) 
    {
        return createInstance(serviceUnit, traceAllMessages, -1,  null);
    }
    
    /**
     * Creates an instance of the <code>DefaultRoutingService</code>
      * @param serviceUnit the <code>ServiceUnit</code> to add the service to
     * @param traceAllMessages <code>true</code> if a routing log is to be used
     * @param maxHopCount the maximum hop count
     * @return the <code>ServiceID</code> of the created service
     * 
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean traceAllMessages, int maxHopCount, LoopCheckerFactory loopCheckerFactory) 
    {
        ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
        
        ContinuousDistribution messageProcessingTime =new DistributionCreator.ContinuousDeterministicDistribution(0);// serviceUnit.getDistributionCreator().getContinuousUniformDistribution(0.01, 0.0000001);
       
         //ContinuousDistribution messageProcessingTime=new ConstantDistribution(0);
        ServiceID routingLogID = null;
//        
//        if (serviceUnit.hasService(LocalRoutingLogService.class))
//            routingLogID= serviceUnit.getService(LocalRoutingLogService.class);
        
        Service defaultRoutingService = new DefaultRoutingService(linkLayerService, routingLogID, traceAllMessages, maxHopCount, loopCheckerFactory, messageProcessingTime);
        return serviceUnit.addService(defaultRoutingService);
    }
	
    /**
     * Constructor for class <code>DefaultRoutingService</code>
     * @param linkLayerServiceID the <code>ServiceID</code> of the link layer
     * @param routingLogID the <code>ServiceID</code> of the routing log
     * @param traceAllMessages <code>true</code> if all messages should be traced
     * @param maxHopCount the maxmimum hop count for messages
     * @param loopCheckerFactory the <code>LoopCheckerFactory</code>
     * @param messageProcessingTime the message processing time
     */
    public DefaultRoutingService(ServiceID linkLayerServiceID, ServiceID routingLogID, boolean traceAllMessages, int maxHopCount, LoopCheckerFactory loopCheckerFactory, ContinuousDistribution messageProcessingTime) 
    {
    	this.linkLayerServiceID 	= linkLayerServiceID;
        this.routingLogID 			= routingLogID;
        this.messageProcessingTime	= messageProcessingTime;
        this.traceAllMessages 		= traceAllMessages;
        this.nextSequenceNumber 	= 0;
        this.maxHopCount			= maxHopCount;
        this.loopCheckerFactory 	= loopCheckerFactory;
    }
    
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getServiceID()
	 */
	public ServiceID getServiceID() 
	{
		return SERVICE_ID;
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.RuntimeService#start(de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) 
    {
    	this.operatingService = runtimeOperatingSystem;
    	operatingService.registerSignalListener(RoutingService.class);
        operatingService.registerAccessListener(RoutingService_sync.class);
        if (operatingService.serviceSatisfies(linkLayerServiceID, LinkLayerExtended.class))
        {
            linkLayerExtended=(LinkLayerExtended_async)operatingService.getSignalListenerStub(linkLayerServiceID,LinkLayerExtended_async.class );
            linkLayerFacade = linkLayerExtended;
        }
        else
            linkLayerFacade=(LinkLayer_async)operatingService.getSignalListenerStub(linkLayerServiceID,LinkLayer_async.class );

        LinkLayer_sync linkLayer_sync = (LinkLayer_sync)operatingService.getAccessListenerStub(linkLayerServiceID, LinkLayer_sync.class);
		//linkLayerFacade = new LinkLayer.LinkLayerStub(runtimeOperatingSystem, linkLayerServiceID);
		address = linkLayer_sync.getLinkLayerProperties().getLinkLayerAddress();
    	operatingService.registerAtService(linkLayerServiceID, LinkLayer.class);
    	
        routingLog=new MyLocalRoutingLogService();
        ServiceID[] serviceIDs=operatingService.getServiceIDs(LocalRoutingLogService.class);
    	if(serviceIDs != null) 
    	{
            
    	    for (int i=0;i<serviceIDs.length;i++){
    	        routingLog.addLogger(serviceIDs[i]);
            }
            
//        	routingLog = new LocalRoutingLogServiceStub(runtimeOperatingSystem, routingLogID);            
//        	if(routingLog.requiresMessageTrace()) {
//        		traceAllMessages = true;
//        	}
    	}
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#finish()
     */
    public void finish() 
    {
        // ignore
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getShape()
     */
    public Shape getShape() 
    {
        return null;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingService#startUnicast(de.uni_trier.jane.basetypes.ServiceID, de.uni_trier.jane.service.routing.RoutingData, de.uni_trier.jane.basetypes.Address)
     */
    public void startUnicast(ServiceID unicastRoutingAlgorithmID, RoutingData payload, Address destination) 
    {
        UnicastRoutingAlgorithm_Sync unicastRoutingAlgorithm = (UnicastRoutingAlgorithm_Sync) operatingService.getAccessListenerStub(unicastRoutingAlgorithmID, UnicastRoutingAlgorithm_Sync.class);
        startRoutingTask(unicastRoutingAlgorithm.getUnicastHeader(destination), payload);
//        
//    	MessageID messageID = new MessageID(address, nextSequenceNumber);
//    	nextSequenceNumber++;
//    	RoutingTaskHandler routingAlgorithmReplyHandler = new DefaultRoutingAlgorithmReplyHandler(messageID, payload);
//        
//        
//        UnicastRoutingAlgorithm algorithm=(UnicastRoutingAlgorithm)
//        	operatingService.getSignalListenerStub(unicastRoutingAlgorithmID,UnicastRoutingAlgorithm.class);
//        algorithm.handleStartUnicastRequest(routingAlgorithmReplyHandler,destination);
//        
//        if(routingLog != null) {
//        	// TODO logStartUnicast!!!
//            routingLog.logStart(messageID);
//        }
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingService#startRoutingTask(de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.service.routing.RoutingData, de.uni_trier.jane.service.routing.RoutingServiceCallback)
     */
    public void startRoutingTask(RoutingHeader routingHeader, RoutingData routingData, RoutingServiceCallback callback) 
    {
        DefaultRoutingHeader header = (DefaultRoutingHeader)routingHeader;
        header.setLinkLayerInfo(new LinkLayerInfoImplementation(address, null, false, -1));
        // each message has to be traced
        if(traceAllMessages) 
        	header.setTraceRoute();
        
        if(loopCheckerFactory != null) 
        {
        	LoopChecker loopChecker = loopCheckerFactory.getLoopChecker();
        	loopChecker.reset(address);
        	header.setLoopChecker(loopChecker);
        }
        
        MessageID messageID = new MessageID(address, nextSequenceNumber);
        header.setMessageID(messageID);
        header.setSourceAddress(address);
        
        if (maxHopCount> 0 && !header.hasMaxHopCount())
            header.setMaxHops(maxHopCount);
        
        nextSequenceNumber++;
        RoutingTaskHandler routingAlgorithmReplyHandler = new DefaultRoutingAlgorithmReplyHandler(messageID, routingData,callback);
        
        RoutingAlgorithm algorithm=getRoutingAlgorithm(routingHeader.getRoutingAlgorithmID());
        algorithm.handleStartRoutingRequest(routingAlgorithmReplyHandler,routingHeader);

        Signal signal = new LocalRoutingLogService.LogStartSignal(header);
        operatingService.sendSignal(signal);
        
        routingLog.logStart(header);
        
        if (callback!=null){
            callback.routingStarted(header);
        }
            
        

     
        
    }
    //
    public void startRoutingTask(RoutingHeader routingHeader, RoutingData routingData) {
        startRoutingTask(routingHeader,routingData,null);
    }
    
    //
    public Address getOwnAddress() 
    {
        return address;
    }



    protected void handleMessageReceived(LinkLayerInfo info, RoutingHeader routingHeader, RoutingData payload) 
    {
        DefaultRoutingHeader header = (DefaultRoutingHeader) routingHeader;
        header.setLinkLayerInfo(info);
     
        if (!address.equals(info.getReceiver()))
        {
            if (routingHeader.isPromiscousMessage())
            {
                Signal signal = new RoutingData.DeliverMessageSignal(header, payload);
                operatingService.sendSignal(signal);
            }
            if (routingHeader.isPromiscousHeader())
            {
            	RoutingAlgorithm algorithm=getRoutingAlgorithm(header.getRoutingAlgorithmID());
            	algorithm.handlePromiscousHeader(routingHeader);
            }
            return;
        }
       
        Address messageSender = info.getSender();
        

        //header.setPreviousNode(messageSender);
        operatingService.sendEvent(new MessageReceiveEvent(header));
        RoutingTaskHandler handler = new DefaultRoutingAlgorithmReplyHandler(header.getMessageID(), payload);
        if (header.traceRoute()){
            header.addHop(address);
        }
        if (header.hasHopCount()){
            header.nextHop();
        }


        // notify routing logs about received messages
    	Signal signal = new LocalRoutingLogService.LogMessageReceivedSignal(header.getMessageID(), header, messageSender);
    	operatingService.sendSignal(signal);

        // check for loop and terminate if required
        LoopChecker loopChecker = header.getLoopChecker();
        if(loopChecker != null) {
        	loopChecker.addNode(address);
        	if(loopChecker.checkForLoop()) {
        		int loopLength = loopChecker.getLoopLength();
        		signal = new LocalRoutingLogService.LogLoopMessageSignal(header, loopLength);
        		operatingService.sendSignal(signal);
            	return;
        	}
        }
//else {
//throw new NullPointerException("TEST!!!");       	
//}
        
        
        
    	RoutingAlgorithm algorithm=getRoutingAlgorithm(header.getRoutingAlgorithmID());
    	algorithm.handleMessageReceivedRequest(handler,header,messageSender);
    	
        if(routingLog != null) {
            routingLog.logMessageReceived(header.getMessageID(), header, messageSender);
        }
    }


    /**
     * 
     * TODO: comment method 
     * @param routingAlgorithmID
     * @return
     */
    protected RoutingAlgorithm getRoutingAlgorithm(ServiceID routingAlgorithmID) {
        return (RoutingAlgorithm)operatingService.getSignalListenerStub(routingAlgorithmID,RoutingAlgorithm.class);
        
    }
    
    

    /**
     * 
     * @author goergen
     *
     * TODO comment class
     */
    private class DefaultRoutingAlgorithmReplyHandler implements RoutingTaskHandler {
        private class MessageHandler implements UnicastCallbackHandler,BroadcastCallbackHandler, AddressedBroadcastCallbackHandler{
            
            public void notifyBroadcastProcessed(LinkLayerMessage message) {
                 notifyMessageForwarded(message);
            }
            
            public void notifyUnicastProcessed(Address receiver, LinkLayerMessage message) {
                notifyMessageForwarded(message);
            }

            public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {/* ignore */}

            public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
                notifyUnicastError(receiver, message);
            }

            public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message) {
                notifyUnicastError(receiver, message);
            }

            public void notifyAddressedBroadcastProcessed(Address[] receivers, LinkLayerMessage message) {
                notifyMessageForwarded(message);
            }

            public void notifyAddressedBroadcastSuccess(Address[] receivers, LinkLayerMessage message) {/* ignore */}

            public void notifyAddressedBroadcastSuccess(Address receiver, LinkLayerMessage message) {/* ignore */}

            public void notifyAddressedBroadcastFailed(Address receiver, LinkLayerMessage message) {/* ignore */}

            public void notifyAddressedBroadcastTimeout(Address[] receivers, Address[] failedReceivers, Address[] timeoutReceivers, LinkLayerMessage message) {
//                List failed=Arrays.asList(failedReceivers);
//                failed.addAll(Arrays.asList(timeoutReceivers));
                notifyAddressedBroadcastError(receivers,failedReceivers,timeoutReceivers,message);
                
            }
        }
        
        private MessageHandler messageHandler=new MessageHandler();

        private boolean open;
    	private MessageID messageID;
    	private RoutingData routingData;
        private RoutingServiceCallback callback;
        private int casts;
        private int delegates;

        private boolean delivered;

        

        /**
         * 
         * Constructor for class <code>DefaultRoutingAlgorithmReplyHandler</code>
         * @param messageID
         * @param routingData
         */
		public DefaultRoutingAlgorithmReplyHandler(MessageID messageID,
				RoutingData routingData) {
            this(messageID,routingData,null);
			
		}



        /**
         * 
         * Constructor for class <code>DefaultRoutingAlgorithmReplyHandler</code>
         * @param messageID
         * @param routingData
         * @param callback
         */
		public DefaultRoutingAlgorithmReplyHandler(MessageID messageID, RoutingData routingData, RoutingServiceCallback callback) {
            this.messageID = messageID;
            this.routingData = routingData;
            this.callback=callback;
            
        }

        public void dropMessage(RoutingHeader routingHeader) {
            checkHeader(routingHeader);
            dropMessageInternal(routingHeader,routingData,callback);
		}



        public void ignoreMessage(RoutingHeader routingHeader) {
        	checkHeader(routingHeader);
        	Signal signal = new LocalRoutingLogService.LogIgnoreMessageSignal(routingHeader);
        	operatingService.sendSignal(signal);
	        if(routingLog != null) {
	            routingLog.logIgnoreMessage(routingHeader);
	        }
            if (callback!=null){
                callback.ignoreLocally(routingHeader);
            }
            operatingService.sendSignal(new RoutingServiceListener.
                       IgnoreMessageSignal(routingHeader,routingData));
	        
		}

		/**
         * TODO Comment method
         * @param routingHeader
         */
        private void checkHeader(RoutingHeader routingHeader) {
            if (routingHeader.isPromiscousMessage()&&!delivered){
                DefaultRoutingHeader header=(DefaultRoutingHeader)routingHeader;
                delivered=true;
                Signal signal = new RoutingData.DeliverMessageSignal(header,routingData);
                operatingService.sendSignal(signal);
                
            }
            
        }



        public void deliverMessage(RoutingHeader routingHeader) {
            
            DefaultRoutingHeader header=(DefaultRoutingHeader)routingHeader;
            
            //if (deliverMessage){
            if (!delivered){
                header.setDestinationAddress(address);
                Signal signal = new RoutingData.DeliverMessageSignal(header,routingData);
                operatingService.sendSignal(signal);
            }else{
                operatingService.write("Auslieferung ohne korrekte Destination address erfolgt");
            }
            delivered=true;
            //}else{
            //    operatingService.sendSignal(new RoutingServiceListener.DelegateDeliverMessageSignal(header,routingData));
            //}

            Signal signal = new LocalRoutingLogService.LogDeliverMessageSignal(routingHeader);
            operatingService.sendSignal(signal);
	        if(routingLog != null) {
	            routingLog.logDeliverMessage(routingHeader);
	        }
            if (callback!=null){
                
                callback.messageProcessed(header);
                callback.deliverLocally(routingHeader);
            }
	        
		}

		public void forwardAsUnicast(final RoutingHeader routingHeader, final Address receiver) {
            double pTime=messageProcessingTime.getNext();
            if (pTime>0){
                operatingService.setTimeout(new ServiceTimeout(pTime){
                    public void handle() {
                        forwardAsUnicast_internal(routingHeader,receiver);
                    }
                });
            }else{
                forwardAsUnicast_internal(routingHeader,receiver);
            }
        }
        public void forwardAsUnicast_internal(RoutingHeader routingHeader, Address receiver) {
		    DefaultRoutingHeader header=(DefaultRoutingHeader)routingHeader;
            if (header.hasMaxHopCount()&&header.hopCountReached()){
                dropMessageInternal(routingHeader,routingData,callback);
                return;
            }
            checkHeader(routingHeader);
            casts++;
            UnicastRoutingServiceMessage message = new UnicastRoutingServiceMessage( header, routingData);
            if (linkLayerExtended!=null&&(routingHeader.isPromiscousMessage()||routingHeader.isPromiscousHeader())){
            	linkLayerExtended.sendAddressedBroadcast(receiver,message,defaultConfiguration,messageHandler);
            }else{
            	linkLayerFacade.sendUnicast(receiver, message, messageHandler);
            }
            //

            Signal signal = new LocalRoutingLogService.LogForwardUnicastSignal(messageID, message.getHeader(), receiver);
            operatingService.sendSignal(signal);
	        if(routingLog != null) {
	            routingLog.logForwardUnicast(messageID, message.getHeader(), receiver);
	        }
	        
	        
	        
		}
        
        public void forwardAsAddressedMulticast(final RoutingHeader routingHeader, final Address[] receivers) {
            double pTime=messageProcessingTime.getNext();
            if (pTime>0){
                operatingService.setTimeout(new ServiceTimeout(pTime){
                    public void handle() {
                        forwardAsAddressedMulticast_internal(routingHeader,receivers);
                    }
                });
            }else{
                forwardAsAddressedMulticast_internal(routingHeader,receivers);
            }
        }    
        public void forwardAsAddressedMulticast_internal(RoutingHeader routingHeader, Address[] receivers) {
            DefaultRoutingHeader header=(DefaultRoutingHeader)routingHeader;
            if (header.hasMaxHopCount()&&header.hopCountReached()){
                dropMessageInternal(routingHeader,routingData,callback);
                return;
            }
            checkHeader(routingHeader);
            casts++;
            if (linkLayerExtended==null){
                throw new IllegalStateException("The given linklayer does not provide extended functionality");
            }
            UnicastRoutingServiceMessage message = new UnicastRoutingServiceMessage( header, routingData);
            
            if ((routingHeader.isPromiscousMessage()||routingHeader.isPromiscousHeader())){
            	linkLayerExtended.sendAddressedBroadcast(receivers,message,defaultConfiguration,messageHandler);
            }else{
            	linkLayerExtended.sendAddressedMulticast(receivers, message,defaultConfiguration,messageHandler);
            }
            
            //linkLayerFacade.sendUnicast(receiver, message, messageHandler);   
            if(routingLog != null) {
                routingLog.logForwardMulticast(messageID, message.getHeader(), receivers);
            }
        }
        
        
        

		public void forwardAsBroadcast(final RoutingHeader routingHeader) {
            double pTime=messageProcessingTime.getNext();
            if (pTime>0){
                operatingService.setTimeout(new ServiceTimeout(pTime){
                    public void handle() {
                        forwardAsBroadcast_internal(routingHeader);
                    }
                });
            }else{
                forwardAsBroadcast_internal(routingHeader);
            }
        }
        public void forwardAsBroadcast_internal(RoutingHeader routingHeader) {
            DefaultRoutingHeader header=(DefaultRoutingHeader)routingHeader;
            if (header.hasMaxHopCount()&&header.hopCountReached()){
                dropMessageInternal(routingHeader,routingData,callback);
                return;
            }
            checkHeader(routingHeader);
            casts++;
            UnicastRoutingServiceMessage message = new UnicastRoutingServiceMessage( header, routingData);
            
            linkLayerFacade.sendBroadcast(message,messageHandler);
            

            Signal signal = new LocalRoutingLogService.LogForwardBroadcastSignal(messageID, header);
            operatingService.sendSignal(signal);
	        if(routingLog != null) {
	            routingLog.logForwardBroadcast(messageID, header);
	        }
	        
	        
		}
		
		//
        public void delegateMessage(RoutingHeader newRoutingHeader,RoutingHeader oldRoutingHeader) {
        	
        	DefaultRoutingHeader h = (DefaultRoutingHeader)newRoutingHeader;
        	LoopChecker loopChecker = h.getLoopChecker();
        	if(loopChecker != null) {
        		loopChecker.reset(address);
        	}
        	
        	Signal signal = new LocalRoutingLogService.LogDelagateMessageSignal(operatingService.getCallingServiceID(),messageID, newRoutingHeader);
        	operatingService.sendSignal(signal);
            operatingService.sendSignal(new RoutingServiceListener.
                    DelegateMessageSignal(newRoutingHeader,oldRoutingHeader,routingData));
            if(routingLog != null) {
                routingLog.logDelegateMessage(operatingService.getCallingServiceID(),messageID, newRoutingHeader);
            }
            getRoutingAlgorithm(newRoutingHeader.getRoutingAlgorithmID()).handleMessageDelegateRequest(this,newRoutingHeader);
            delegates++;
        }
        
        //
        public void createOpenTask() {
            open=true;
        }
        
        
        //
        public void finishOpenTask() {
            if (open){
                operatingService.finishListener(this);
            }
        }
        
        
        //
        public void resetOpenTask() {
            open=false;
        }

        
        private void notifyMessageForwarded(LinkLayerMessage message) {
            casts--;
            if (casts<0) throw new IllegalStateException("");
            if (casts==0){
                UnicastRoutingServiceMessage routingServiceMessage = (UnicastRoutingServiceMessage)message;
            
                RoutingHeader header = routingServiceMessage.getHeader();
                RoutingAlgorithm algorithm=getRoutingAlgorithm(header.getRoutingAlgorithmID());
            
                algorithm.handleMessageForwardProcessed(header);
            
            if(routingLog != null) {
                //routingLog.logForwardProcessed(messageID, header);
            }
                if (callback!=null){
                    callback.messageProcessed(header);
                }
            }
            
        }
        
        private void notifyAddressedBroadcastError(Address[] successReceivers, Address[] failedReceivers, Address[] timeoutReceivers, LinkLayerMessage message) {
            UnicastRoutingServiceMessage routingServiceMessage = (UnicastRoutingServiceMessage)message;
            MessageID messageID = routingServiceMessage.getHeader().getMessageID();
            RoutingHeader header = routingServiceMessage.getHeader();
            RoutingData data = routingServiceMessage.getPayload();
            ServiceID routingAlgorithmID = header.getRoutingAlgorithmID();
            RoutingTaskHandler handler = new DefaultRoutingAlgorithmReplyHandler(messageID, data);
            RoutingAlgorithmExtended algorithm=(RoutingAlgorithmExtended)
                operatingService.getSignalListenerStub(routingAlgorithmID,RoutingAlgorithmExtended.class);
            algorithm.handleAddressedBroadcastErrorRequest(handler,header,successReceivers,failedReceivers,timeoutReceivers);
            for (int i=0;i<failedReceivers.length;i++){
                Signal signal = new LocalRoutingLogService.LogForwardErrorSignal(messageID, header, failedReceivers[i]);
                operatingService.sendSignal(signal);
            }
            for (int i=0;i<timeoutReceivers.length;i++){
                Signal signal = new LocalRoutingLogService.LogForwardErrorSignal(messageID, header, timeoutReceivers[i]);
                operatingService.sendSignal(signal);
            }
        }

        private void notifyUnicastError(Address receiver, LinkLayerMessage message) {
            UnicastRoutingServiceMessage routingServiceMessage = (UnicastRoutingServiceMessage)message;
            MessageID messageID = routingServiceMessage.getHeader().getMessageID();
            RoutingHeader header = routingServiceMessage.getHeader();
            RoutingData data = routingServiceMessage.getPayload();
            ServiceID routingAlgorithmID = header.getRoutingAlgorithmID();
            RoutingTaskHandler handler = new DefaultRoutingAlgorithmReplyHandler(messageID, data);
            RoutingAlgorithm algorithm=getRoutingAlgorithm(routingAlgorithmID);
          //  operatingService.sendSignal(new UnicastErrorSignal(receiver));
            algorithm.handleUnicastErrorRequest(handler,header,receiver);

            Signal signal = new LocalRoutingLogService.LogForwardErrorSignal(messageID, header, receiver);
            operatingService.sendSignal(signal);
            if(routingLog != null) {
                routingLog.logForwardError(messageID, header, receiver);
            }
        }
        

 
    	
    }

    /**
     * 
     * @author goergen
     *
     * TODO comment class
     */
    private static class UnicastRoutingServiceMessage implements LinkLayerMessage {

        private DefaultRoutingHeader header;
        private RoutingData payload;
        /**
         * 
         * Constructor for class <code>UnicastRoutingServiceMessage</code>
         * @param messageID
         * @param header
         * @param payload
         */
        public UnicastRoutingServiceMessage( DefaultRoutingHeader header, RoutingData payload) {

            this.header = header;
            this.payload = payload;
        }

        public void handle(LinkLayerInfo info, SignalListener listener) {
            DefaultRoutingService routingService = (DefaultRoutingService)listener;
            routingService.handleMessageReceived(info, header, payload);
        }

        public Dispatchable copy() {

            DefaultRoutingHeader headerCopy = (DefaultRoutingHeader)header.copy();
            RoutingData dataCopy = (RoutingData) payload.copy();

            // we can return "this" since no real copy was performed
            if(headerCopy == header && dataCopy == payload) {
                return this;
            }

            return new UnicastRoutingServiceMessage(headerCopy, dataCopy);

        }

        public int getSize() {
            return header.getSize() + payload.getSize();
        }

        public Shape getShape() {
            ShapeCollection collection=new ShapeCollection();
            Shape shape=header.getShape();
            if (shape!=null){
                collection.addShape(shape);
            }
            shape=payload.getShape();
            if (shape!=null){
                collection.addShape(shape);
            }
            return collection;
        }

        public Class getReceiverServiceClass() {
            return DefaultRoutingService.class;
        }

        /**
         * @return Returns the header.
         */
        public RoutingHeader getHeader() {
            return header;
        }

        /**
         * @return Returns the payload.
         */
        public RoutingData getPayload() {
            return payload;
        }



		
    }


	public void getParameters(Parameters parameters) {

		parameters.addParameter("loopCheckerFactory", loopCheckerFactory);
		
		// TODO fill all parameters
		
	}

    /**
     * TODO Comment method
     * @param routingHeader
     * @param routingData 
     * @param callback 
     */
    protected void dropMessageInternal(RoutingHeader routingHeader,RoutingData routingData, RoutingServiceCallback callback) {
    	
    	Signal signal = new LocalRoutingLogService.LogDropMessageSignal(routingHeader);
    	operatingService.sendSignal(signal);
        if(routingLog != null) {
            routingLog.logDropMessage(routingHeader);
        }
        if (callback!=null){
            callback.dropLocally(routingHeader);
        }
        operatingService.sendSignal(new RoutingServiceListener.
                DropMessageSignal(routingHeader,routingData));
    }

}
