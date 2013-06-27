package de.uni_trier.jane.service.network.link_layer.winfra.signals;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler;

/**
 * This Signal is used, to ask a class of the application using the winfra network,
 * if a given LinkLayerMessage should be stored, or rejected, if the destination of
 * the message is not available at the present time. 
 * @author christian.hiedels
 *
 */
public interface InquireMessageTypeSignal {

	/**
	 * This Signal is send to a class implementing the <code>MessageKnowledge</code>
	 * Interface, to check the type of a message.
	 * @param sender The DeviceID of the sending Device
	 * @param receiver The Address of the receiving Device
	 * @param llm The LinkLayerMessage which should be checked
	 * @param handle A UnicastCallback Handle
	 */
	public abstract void inquireThisMessage( DeviceID sender, Address receiver, LinkLayerMessage llm, UnicastCallbackHandler handle );
	
}
