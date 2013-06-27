/*
 * Created on 17.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.global;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

/**
 * 
 * 
 */
public interface GlobalLinkLayer extends VisualizableNetwork {

	public static final ServiceReference REQUIRED_SERVICE = new ServiceReference("network") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
			return serviceUnit.getService(GlobalLinkLayer.class);
		}
	};

	/**
    * 
    * @param linkLayerAddress
    */
    //void registerDevice(Address linkLayerAddress);
    
    /**
     * 
     * @param message
     * @param visualize
     */
    void sendBroadcast(LinkLayerMessage message,boolean visualize);
    
    /**
     * 
     * @param message
     * @param visualize
     * @param handle
     */
    void sendBroadcast(LinkLayerMessage message,boolean visualize,BroadcastCallbackHandler handle);
    
    /**
     * 
     * @param receiver
     * @param message
     * @param visualize
     */
    void sendUnicast(Address receiver, LinkLayerMessage message, boolean visualize);
    
    /**
     * 
     * @param receiver
     * @param message
     * @param visualize
     * @param handle
     */
    void sendUnicast(Address receiver,LinkLayerMessage message, boolean visualize,UnicastCallbackHandler handle);
    
    /**
     * Turns the promiscuous mode of the linkLayer on or of 
     * @param promiscuous
     */
    public void setPromiscuous(boolean promiscuous);
}
