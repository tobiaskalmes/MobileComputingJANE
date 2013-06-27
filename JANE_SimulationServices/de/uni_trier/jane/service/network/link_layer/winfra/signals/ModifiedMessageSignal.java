package de.uni_trier.jane.service.network.link_layer.winfra.signals;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;

/**
 * These methods are called via a signal, which is invoked by the service, which
 * modified a LinkLayerMessage. They correspond to the methods of the
 * <code>ModifyMessageSignal</code> class.
 * @author christian.hiedels
 *
 */
public interface ModifiedMessageSignal {
	public abstract void receiveModifiedMessageSignal( LinkLayerMessage llm );

	public abstract void receiveModifiedMessageSignal( LinkLayerMessage llm, DeviceID finalReceiver );
	
	public abstract void receiveModifiedMessageSignal( LinkLayerMessage llm, DeviceID targetBSreceiver, DeviceID finalReceiver  );
}
