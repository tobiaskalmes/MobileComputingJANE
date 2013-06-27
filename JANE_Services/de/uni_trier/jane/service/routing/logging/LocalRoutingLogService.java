package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.signaling.*;

/**
 * A local routing log service is notified by the routing service about all routing actions.
 * Use this interface in order to log such routing events to a console or a file, for instance.
 */
public interface LocalRoutingLogService extends RoutingLogService, Service {

    /**
     * Log that a routing task was started
     * @param header the ID of the received message
     */
    public void logStart(RoutingHeader header);

    /**
     * Log a dropped message. 
     * @param header the header of the message to be dropped
     */
    public void logDropMessage(RoutingHeader header);

    public void logLoopMessage(RoutingHeader header, int loopLength);
    
    /**
     * Log an ignored message.
     * @param header the ID of the message to be ignored
     */
    public void logIgnoreMessage(RoutingHeader header);

    /**
     * Log a delivered message.
     * @param header the ID of the message to be forwarded
     */
    public void logDeliverMessage(RoutingHeader header);

    /**
     * Log that a message is sent by unicast.
     * @param messageID the ID of the message to be sent
     * @param header the new header of the routing message
     * @param receiver the next hop receiver
     */
    public void logForwardUnicast(MessageID messageID, RoutingHeader header, Address receiver);

    /**
     * Log that a message is sent by broadcast.
     * @param messageID the ID of the message to be sent
     * @param header the new header of the routing message
     */
    public void logForwardBroadcast(MessageID messageID, RoutingHeader header);

    /**
     * Log that there was an error during unicast message forwarding.
     * @param messageID the ID of the message to be forwarded
     * @param header the header of the routing message
     * @param receiver the unicast receiver
     */
    public void logForwardError(MessageID messageID, RoutingHeader header, Address receiver);

    /**
     * Log that a message was received on a device.
     * @param messageID the ID of the received message
     * @param header the header of the routing message
     * @param sender the message sender
     */
    public void logMessageReceived(MessageID messageID, RoutingHeader header, Address sender);

    /**
     * Log that a message has been delegated to another routing service
     * TODO Comment method
     * @param routingAlgorithmID    the delegating routing service
     * @param messageID             the ID of the delegated message
     * @param routingHeader         the new header of the delegated message
     */
    public void logDelegateMessage(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader);
    



    public void addLoggingAlgorithm(ServiceID routingAlgorithmToLog);


	////////////////////////////////////////
	//
	////////////////////////////////////////

	

	public class LogStartSignal implements Signal {
	    private RoutingHeader messageID;
	    
	    /**
	     * 
	     * Constructor for class <code>LogStartSignal</code>
	     * @param messageID
	     */
	    public LogStartSignal(RoutingHeader messageID) {
	        this.messageID = messageID;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener  service) {
	        ((LocalRoutingLogService) service).logStart(messageID);
	    }
	
	}
	
	
	
	public static final class LogDropMessageSignal
	    implements
	        Signal {
	    private RoutingHeader messageID;
	    /**
	     * 
	     * Constructor for class <code>LogDropMessageSignal</code>
	     * @param messageID
	     */
	    public LogDropMessageSignal(RoutingHeader messageID) {
	        this.messageID = messageID;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener  service) {
	        ((LocalRoutingLogService) service).logDropMessage(messageID);
	    }
	
	}

	
	public static final class LogLoopMessageSignal implements Signal {
		
		private RoutingHeader messageID;
		private int loopLength;
		
	    /**
	     * Constructor for class <code>LogLoopMessageSignal</code>
	     * @param messageID the ID of the looped message
	     * @param loopLength the length of the loop in hop count
	     */
	    public LogLoopMessageSignal(RoutingHeader messageID, int loopLength) {
	        this.messageID = messageID;
	        this.loopLength = loopLength;
	    }

	    public Dispatchable copy() {
	        return this;
	    }

	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }

	    public void handle(SignalListener  service) {
	        ((LocalRoutingLogService) service).logLoopMessage(messageID, loopLength);
	    }

	}


	
	public  static final class LogIgnoreMessageSignal
	    implements
	        Signal {
	    private RoutingHeader messageID;
	    
	    /**
	     * 
	     * Constructor for class <code>LogIgnoreMessageSignal</code>
	     * @param messageID
	     */
	    public LogIgnoreMessageSignal(RoutingHeader messageID) {
	        this.messageID = messageID;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener listener) {
	    
	        ((LocalRoutingLogService) listener).logIgnoreMessage(messageID);
	    }
	
	}

	public  static final class LogDeliverMessageSignal
	    implements
	        Signal {
	    private RoutingHeader messageID;
	    
	    /**
	     * 
	     * Constructor for class <code>LogDeliverMessageSignal</code>
	     * @param messageID
	     */
	    public LogDeliverMessageSignal(RoutingHeader messageID) {
	        this.messageID = messageID;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener service) {
	        ((LocalRoutingLogService) service).logDeliverMessage(messageID);
	    }
	
	}

	
	public  static final class LogForwardUnicastSignal
	    implements
	        Signal {
	    private MessageID messageID;
	    private RoutingHeader header;
	    private Address receiver;
	    
	    /**
	     * 
	     * Constructor for class <code>LogForwardUnicastSignal</code>
	     * @param messageID
	     * @param header
	     * @param receiver
	     */
	    public LogForwardUnicastSignal(
	        MessageID messageID,
	        RoutingHeader header,
	        Address receiver) {
	        this.messageID = messageID;
	        this.header = header;
	        this.receiver = receiver;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener  service) {
	        ((LocalRoutingLogService) service).logForwardUnicast(
	            messageID,
	            header,
	            receiver);
	    }
	
	}

	public  static final class LogForwardBroadcastSignal
	    implements
	        Signal {
	    private MessageID messageID;
	    private RoutingHeader header;
	    
	    /**
	     * 
	     * Constructor for class <code>LogForwardBroadcastSignal</code>
	     * @param messageID
	     * @param header
	     */
	    public LogForwardBroadcastSignal(
	        MessageID messageID,
	        RoutingHeader header) {
	        this.messageID = messageID;
	        this.header = header;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener service) {
	        ((LocalRoutingLogService) service).logForwardBroadcast(
	            messageID,
	            header);
	    }
	
	}

	public  static final class LogForwardErrorSignal
	    implements
	        Signal {
	    private MessageID messageID;
	    private RoutingHeader header;
	    private Address receiver;
	    
	    /**
	     * 
	     * Constructor for class <code>LogForwardErrorSignal</code>
	     * @param messageID
	     * @param header
	     * @param receiver
	     */
	    public LogForwardErrorSignal(
	        MessageID messageID,
	        RoutingHeader header,
	        Address receiver) {
	        this.messageID = messageID;
	        this.header = header;
	        this.receiver = receiver;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener service) {
	        ((LocalRoutingLogService) service).logForwardError(
	            messageID,
	            header,
	            receiver);
	    }
	
	}

	
	public  static final class LogMessageReceivedSignal
	    implements
	        Signal {
	    private MessageID messageID;
	    private RoutingHeader header;
	    private Address sender;
	    
	    /**
	     * 
	     * Constructor for class <code>LogMessageReceivedSignal</code>
	     * @param messageID
	     * @param header
	     * @param sender
	     */
	    public LogMessageReceivedSignal(
	        MessageID messageID,
	        RoutingHeader header,
	        Address sender) {
	        this.messageID = messageID;
	        this.header = header;
	        this.sender = sender;
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	    public void handle(SignalListener service) {
	        ((LocalRoutingLogService) service).logMessageReceived(
	            messageID,
	            header,
	            sender);
	    }
	
	}

	public  class LogDelagateMessageSignal implements Signal {
	
	
	
	    private ServiceID routingAlgorithmID;
	    private MessageID messageID;
	    private RoutingHeader routingHeader;
	
	    /**
	     * 
	     * Constructor for class <code>LogDelagateMessageSignal</code>
	     * @param routingAlgorithmID
	     * @param messageID
	     * @param routingHeader
	     */
	    public LogDelagateMessageSignal(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
	        this.routingAlgorithmID=routingAlgorithmID;
	        this.messageID=messageID;
	        this.routingHeader=routingHeader;
	    }
	
	    public void handle(SignalListener listener) {
	        ((LocalRoutingLogService)listener).logDelegateMessage(routingAlgorithmID,messageID,routingHeader);
	
	    }
	
	    public Dispatchable copy() {
	        return this;
	    }
	
	    public Class getReceiverServiceClass() {
	        return LocalRoutingLogService.class;
	    }
	
	
	}

    /**
     * TODO Comment method
     * @param messageID
     * @param header
     * @param receivers
     */
    public void logForwardMulticast(MessageID messageID, RoutingHeader header, Address[] receivers);

}