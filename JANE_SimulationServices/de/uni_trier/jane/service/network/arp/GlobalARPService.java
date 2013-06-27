/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.arp;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.arp.*;
import de.uni_trier.jane.signaling.*;


/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface GlobalARPService {

	public void propagateARPMapping(AddressMapping addressMapping);

	public void deregisterDevice();

	
	
	public static class PropagateARPMappingSignal implements Signal{

		private AddressMapping addressMapping;
		/**
		 * @param macLayerAddress
		 * @param linkLayerAddress
		 */
		public PropagateARPMappingSignal(AddressMapping addressMapping) {
			super();
			this.addressMapping=addressMapping;
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
		 */
		public void handle(SignalListener service) {
			((GlobalARPService)service).propagateARPMapping(addressMapping);
			
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
			return GlobalARPService.class;
		}
		
	}


 
    public class DeregisterDevice implements Signal {

        public void handle(SignalListener service) {
            ((GlobalARPService)service).deregisterDevice();
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalARPService.class;
        }

    }
}
