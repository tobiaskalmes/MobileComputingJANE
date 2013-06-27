package de.uni_trier.jane.service.routing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;


/**
 * Implement this interface in order to send payload over the routing protocol.
 */
public interface RoutingData extends Dispatchable,Sendable{

    /**
     * Copy is used when messages gets duplicated at the network.
     * @return a copy of the payload. You may return this, if data is immutable.
     */
    //public RoutingData copy();

    /**
     * Get the size of the payload to be sent.
     * @return the size (e.g. in bits)
     */
    //public int getSize();

    /**
     * This method is called, when the data is received by an addressed destination node.
     * @param routingHeader the complete header of the received message
     * @param signalListener the signalListener which will receive the data
     * 
     */
    public void handle(RoutingHeader routingHeader, SignalListener signalListener);

    /**
     * Determine the class of the service which will receive the data
     * @return the class of the service
     */
    //public Class getReceiverServiceClass();
    
    //public Shape getShape();

    /**
     * This signal is used by a routing service in order to deliver
     * the payload to the recipients when the message arrived at the
     * destination device.
     */
    public static class DeliverMessageSignal implements Signal {

        private RoutingData data;
        private DefaultRoutingHeader routingHeader;

        /**
         * Construct a new deliver message signal.
         * @param data the data to be delivered
         */
        public DeliverMessageSignal(DefaultRoutingHeader routingHeader, RoutingData data) {
            this.data = data;
            this.routingHeader=routingHeader;
        }

        public Class getReceiverServiceClass() {
            return data.getReceiverServiceClass();
        }

        public void handle(SignalListener signalListener) {
            data.handle(routingHeader,signalListener);
        }

        public Dispatchable copy() {
            return new DeliverMessageSignal((DefaultRoutingHeader)routingHeader.copy(),(RoutingData)data.copy());
        }
        
    }
}
