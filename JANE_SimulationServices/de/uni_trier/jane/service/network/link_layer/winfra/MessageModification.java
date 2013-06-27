package de.uni_trier.jane.service.network.link_layer.winfra;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.winfra.signals.ModifyMessageSignal;


/**
 * This Interface can be implemented by a class, which is able to modify a message.
 * It is extended from a Signal, which allows listening to modify requests.
 * @author christian.hiedels
 *
 */
public interface MessageModification extends ModifyMessageSignal {

	/**
	 * Modifies a LinkLayerMessage in an application specific way
	 * @param llm The LinkLayerMessage
	 * @param bsa The Address of the last Base Station
	 * @return The modified LinkLayerMessage
	 */
	public LinkLayerMessage modifyMessage( LinkLayerMessage llm, Address bsa );
}
