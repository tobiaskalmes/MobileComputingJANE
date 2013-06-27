package de.uni_trier.jane.service.routing.unicast;



import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.service.routing.*;


/**
 * This interface describes a specialized routing algorithm which is able to
 * send unicast messages to one recipient.
 * @deprecated
 */
public interface UnicastRoutingAlgorithm extends RoutingAlgorithm {

    /**
     * Handle a start unicast request
     * @param routingTaskHandler the ID of the message to be sent
     * @param destination the message destination
     * @deprecated
     */
    public void handleStartUnicastRequest(RoutingTaskHandler routingTaskHandler, LinkLayerAddress destination);
    
    //public RoutingHeader getUnicastRoutingHeader(LinkLayerAddress source,LinkLayerAddress destination);



}
