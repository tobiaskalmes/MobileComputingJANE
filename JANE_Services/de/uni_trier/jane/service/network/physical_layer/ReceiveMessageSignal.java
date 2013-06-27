/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.physical_layer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

		
	public  class ReceiveMessageSignal implements Signal{
		private PhysicalLayerMessage physikalLayerMessage;
		
		/**
		 * @param physikalLayerMessage
		 */
		public ReceiveMessageSignal(
				PhysicalLayerMessage physikalLayerMessage) {
			super();
			this.physikalLayerMessage = physikalLayerMessage;
		}

		public void handle(SignalListener service) {
		    physikalLayerMessage.handle(service);
		}


		public Dispatchable copy() {
			PhysicalLayerMessage messageCopy=(PhysicalLayerMessage)physikalLayerMessage.copy();
			if (messageCopy==physikalLayerMessage){
				return this;
			}else{
				return new ReceiveMessageSignal(messageCopy);
			}
		}

		
		public Class getReceiverServiceClass() {
			return physikalLayerMessage.getReceiverServiceClass();
		}
		
	}

