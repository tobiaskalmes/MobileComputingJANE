package de.uni_trier.jane.service.network.link_layer.winfra.signals;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler;

public interface TellingMessageResponseSignal {

	/**
	 * This Method tells the Winfra Network, if a specified
	 * LinkLayerMessage should be stored.
	 * @param sender The DeviceID of the sending Device
	 * @param receiver The Address of the receiving Device
	 * @param llm the LinkLayerMessage
	 * @param handle A UnicastCallbackHandle
	 * @param store true, if the llm should be stored
	 */
	public abstract void inquiryResponse( DeviceID sender, Address receiver, LinkLayerMessage llm, UnicastCallbackHandler handle, boolean store );
}
