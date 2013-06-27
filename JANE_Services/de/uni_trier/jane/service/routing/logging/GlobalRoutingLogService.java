package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.*;

/**
 * A global routing log service gets notified about all routing actions where
 * an appropriate local routing log proxy is running on. Use this interface in
 * order to log such routing events to a console or a file, for instance.
 * @deprecated uses LocalRoutingLogService! GlobalServiceProxies are not requiered anymore
 */
public interface GlobalRoutingLogService extends RoutingLogService {

    /**
     * Log that a routing task was started
     * @param address the address of the device where this event occured
     * @param messageID the ID of the received message
     */
    public void logStart(Address address, MessageID messageID);

    /**
     * Log a dropped message. 
     * @param address the address of the device where this event occured
     * @param messageID the ID of the message to be dropped
     */
    public void logDropMessage(Address address, MessageID messageID);

    public void logLoopMessage(Address address, MessageID messageID, int loopLength);

    /**
     * Log an ignored message.
     * @param address the address of the device where this event occured
     * @param messageID the ID of the message to be ignored
     */
    public void logIgnoreMessage(Address address, MessageID messageID);

    /**
     * Log a delivered message.
     * @param address the address of the device where this event occured
     * @param messageID the ID of the message to be forwarded
     */
    public void logDeliverMessage(Address address, MessageID messageID);

    /**
     * Log that a message is sent by unicast.
     * @param sender the address of the device where this event occured
     * @param messageID the ID of the message to be sent
     * @param header the new header of the routing message
     * @param receiver the next hop receiver
     */
    public void logForwardUnicast(Address sender, MessageID messageID, RoutingHeader header, Address receiver);

    /**
     * Log that a message is sent by broadcast.
     * @param sender the address of the device where this event occured
     * @param messageID the ID of the message to be sent
     * @param header the new header of the routing message
     */
    public void logForwardBroadcast(Address sender, MessageID messageID, RoutingHeader header);

    /**
     * Log that there was an error during unicast message forwarding.
     * @param sender the address of the device where this event occured
     * @param messageID the ID of the message to be forwarded
     * @param header the header of the routing message
     * @param receiver the unicast receiver
     */
    public void logForwardError(Address sender, MessageID messageID, RoutingHeader header, Address receiver);

    /**
     * Log that a message was received on a device.
     * @param receiver the address of the device where this event occured
     * @param messageID the ID of the received message
     * @param header the header of the routing message
     * @param sender the message sender
     */
    public void logMessageReceived(Address receiver, MessageID messageID, RoutingHeader header, Address sender);
    
    /**
     * Log that a message has been delegated to another routing service
     * @param routingAlgorithmID    the delegating routing service
     * @param messageID             the ID of the delegated message
     * @param routingHeader         the new header of the delegated message
     */
    public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader);

}
