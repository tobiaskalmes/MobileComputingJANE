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
public interface PhysikalLayerListener {
	public void mediaFree(boolean mediaFree);
	
	public static class MediaFreeSignal implements Signal{
		private boolean mediaFree;
		
		/**
		 * @param mediaFree
		 */
		public MediaFreeSignal(boolean mediaFree) {
			super();
			this.mediaFree = mediaFree;
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
		 */
		public void handle(SignalListener service) {
			((PhysikalLayerListener)service).mediaFree(mediaFree);
			
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
			return PhysikalLayerListener.class;
		}
		
	}
	

}
