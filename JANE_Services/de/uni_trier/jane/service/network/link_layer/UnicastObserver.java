/*
 * Created on 06.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
interface UnicastObserver extends SignalListener{

    /**
     * Is called when the message has been completely put on the medium
     * @param receiver	the receiver address
     * @param message	the message 
     */
    public void notifyUnicastProcessed(Address receiver, LinkLayerMessage message);
    
    /**
     * Is called when the message has been received by the receiving device
     * @param receiver	the receiver address
     * @param message	the message 
     */
    public void notifyUnicastReceived(Address receiver, LinkLayerMessage message);
    
    /**
     * Is called when the message has been lost
     * @param receiver	the receiver address
     * @param message	the message
     */
	public void notifyUnicastLost(Address receiver, LinkLayerMessage message);
	
	/**
	 * Is called when the message status is unknown. It is possible, that the message 
	 * has been lost but also that the message has been received.
     * @param receiver	the receiver address
     * @param message	the message
	 */
	public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message);

}
