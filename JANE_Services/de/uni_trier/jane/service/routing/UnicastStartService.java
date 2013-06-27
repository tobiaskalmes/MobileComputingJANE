package de.uni_trier.jane.service.routing;


import de.uni_trier.jane.service.network.link_layer.*;


/**
 * This interface describes a specialized routing algorithm which is able to
 * send unicast messages to one recipient.
 */
public interface UnicastStartService extends RoutingAlgorithm {

    /**
     * Handle a start unicast request
     * @param routingTaskHandler the handler for the message to be sent
     * @param destination the message destination
     */
    public void handleStartUnicastRequest(RoutingTaskHandler routingTaskHandler, LinkLayerAddress destination);

   
}
