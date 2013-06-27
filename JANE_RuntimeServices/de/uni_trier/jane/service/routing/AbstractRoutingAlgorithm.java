package de.uni_trier.jane.service.routing;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.routing.greedy.PositionUnicastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.unicast.*;

/**
 * TODO:comment
 * @author Stefan Peters
 *
 */
public abstract class AbstractRoutingAlgorithm implements RuntimeService, RoutingAlgorithm, UnicastRoutingAlgorithm_Sync {

    protected RuntimeOperatingSystem runtimeOperatingSystem;


	
	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		this.runtimeOperatingSystem = runtimeOperatingSystem;
	}

	public void handleMessageReceivedRequest(RoutingTaskHandler handler, RoutingHeader header, Address sender) {
		DefaultPositionBasedHeader positionBasedHeader = (DefaultPositionBasedHeader)header;
		positionBasedHeader.handle(handler, this);
	}

	public void handleUnicastErrorRequest(final RoutingTaskHandler handler, final RoutingHeader header, Address receiver) {
		// TODO Auto-generated method stub
		
		// TODO Im Falle eines Fehlers wird hier kurz gewartet damit der Cluster-Planarisierer
		// den Nachbar-Update bekommt
		runtimeOperatingSystem.setTimeout(new ServiceTimeout(0.1) {

			public void handle() {
				DefaultPositionBasedHeader positionBasedHeader = (DefaultPositionBasedHeader)header;
				positionBasedHeader.handle(handler, AbstractRoutingAlgorithm.this);
			}});
		
	}

	public void handleMessageForwardProcessed(RoutingHeader header) {
		// ignore
	}

	public void handleMessageDelegateRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) {
		DefaultPositionBasedHeader positionBasedHeader = (DefaultPositionBasedHeader)routingHeader;
		positionBasedHeader.handle(handler, this);
		
	}

    /**
     * TODO Comment method
     * @return
     */
    public abstract PositionUnicastRoutingAlgorithm_Sync getRecovery();

	



}
