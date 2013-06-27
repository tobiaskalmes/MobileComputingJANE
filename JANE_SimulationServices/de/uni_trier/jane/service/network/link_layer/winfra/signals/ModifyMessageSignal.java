package de.uni_trier.jane.service.network.link_layer.winfra.signals;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;

/**
 * These Methods are called via a signal, depending on the information which has to
 * be signalled together with the LinkLayerMessage, which should be modified.
 * @author christian.hiedels
 *
 */
public interface ModifyMessageSignal {

	/**
	 * Receive a Message from the Base Station, which should be modified.
	 * This Signal is send by the sendCellBroadcast Method.
	 */
	public abstract void receiveMessage2ModifySignal( LinkLayerMessage llm, Address BaseStationAddress );
	
	/**
	 * Receive a Message from the Base Station, which should be modified.
	 * This Signal is send by the receiveMessageFromBSO Method.
	 */
	public abstract void receiveMessage2ModifySignal( LinkLayerMessage llm, Address BaseStationAddress, DeviceID finalReceiver );

	/**
	 * Receive a Message from the Base Station, which should be modified.
	 * This Signal is send by the receiveMessageFromCN Method.
	 */
	public abstract void receiveMessage2ModifySignal( LinkLayerMessage llm, Address BaseStationAddress, DeviceID targetBSreceiver, DeviceID finalReceiver );
}
