/*
 * Created on Nov 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.service.network.link_layer.global;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface GlobalLinkLayerMessageReceiver {

    public void deliverMessage(LinkLayerInfo info, LinkLayerMessage linkLayerMessage);
    /**
     * @author goergen
     *
     * To change the template for this generated type comment go to
     * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
     */
    public static class ReceiveSignal implements Signal {

        private LinkLayerInfo info;
        private LinkLayerMessage linkLayerMessage;

        /**
         * @param sender
         * @param serviceMessage
         */
        public ReceiveSignal(LinkLayerInfo info, LinkLayerMessage linkLayerMessage) {
            this.info=info;
            this.linkLayerMessage=linkLayerMessage;
            
        }

        /* (non-Javadoc)
         * @see de.uni_trier.ssds.service.InterDeviceServiceSignal#handle(de.uni_trier.ssds.service.SimulationDeviceID, de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
         */
        public void handle(SignalListener service) {
            ((GlobalLinkLayerMessageReceiver)service).deliverMessage(info,linkLayerMessage);

        }

        /* (non-Javadoc)
         * @see de.uni_trier.ssds.service.Dispatchable#copy()
         */
        public Dispatchable copy() {
            return this;
        }

        /* (non-Javadoc)
         * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
         */
        public Class getReceiverServiceClass() {
            
            return GlobalLinkLayerMessageReceiver.class;
        }

    }

    /**
     * @param sender
     * @param linkLayerMessage
     */
    
}
