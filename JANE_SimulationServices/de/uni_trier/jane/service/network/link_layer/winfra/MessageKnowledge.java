package de.uni_trier.jane.service.network.link_layer.winfra;

import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.winfra.signals.InquireMessageTypeSignal;


/**
 * This Interface can be implemented by a class, which knows the Types of Messages,
 * which pass through the Winfra Network. It determines if the Message should be
 * stored and forwarded, as soon as the destination is available, or if it should
 * be rejected, telling the application simply, that the recipient is not there.
 * Since the Winfra only knows LinkLayerMessages, this has to be decided 'externally'
 * by a class, that knows the Type of Messages.
 * For Communication between Winfra and the class implementing this interface, the
 * Signals <code>InquireMessageTypeSignal</code> and <code>TellingMessageTypeSignal</code>
 * are used.
 * @author christian.hiedels
 *
 */
public interface MessageKnowledge extends InquireMessageTypeSignal {

	public boolean storeThisTypeOfMessage( LinkLayerMessage llm );
}