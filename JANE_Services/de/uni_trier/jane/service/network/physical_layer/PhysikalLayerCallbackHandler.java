/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.physical_layer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface PhysikalLayerCallbackHandler extends SignalListener {
    public void startSend(double deliveryDelta, DeviceIDSet receiverSet);
	public void messageSend(PhysicalLayerMessage message);
	public void sendFailed(PhysicalLayerMessage message);
	
//	public static class StartSendCallback implements ServiceCallback{
//		
//		private double deliveryDelta;
//        private DeviceIDSet receiverSet;
//
//        /**
//		 * @param receiverSet
//         * @param message
//		 */
//		public StartSendCallback(double deliveryDelta, DeviceIDSet receiverSet) {
//		    this.deliveryDelta=deliveryDelta;
//		    this.receiverSet=receiverSet;
//		}
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.ServiceCallback#handle(de.uni_trier.ssds.service.CallbackHandler)
//		 */
//		public void handle(CallbackHandler handler) {
//			((PhysikalLayerCallbackHandler)handler).startSend(deliveryDelta,receiverSet);
//			
//		}
//
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.Dispatchable#copy()
//		 */
//		public Dispatchable copy() {
//			return this;
//			
//		}
//
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
//		 */
//		public Class getReceiverServiceClass() {
//			return PhysikalLayerCallbackHandler.class;
//		}		
//	}
//	
//	
//	public static class MessageSendCallback implements ServiceCallback{
//		private PhysicalLayerMessage message;
//		/**
//		 * @param message
//		 */
//		public MessageSendCallback(PhysicalLayerMessage message) {
//			super();
//			this.message = message;
//		}
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.ServiceCallback#handle(de.uni_trier.ssds.service.CallbackHandler)
//		 */
//		public void handle(CallbackHandler handler) {
//			((PhysikalLayerCallbackHandler)handler).messageSend(message);
//			
//		}
//
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.Dispatchable#copy()
//		 */
//		public Dispatchable copy() {
//			PhysicalLayerMessage messageCopy=(PhysicalLayerMessage)message.copy();
//			if(messageCopy==message){
//				return this;
//			}else{
//				return new MessageSendCallback(messageCopy);
//			}
//			
//		}
//
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
//		 */
//		public Class getReceiverServiceClass() {
//			return PhysikalLayerCallbackHandler.class;
//		}		
//	}
//	
//	public static class SendFailedCallback implements ServiceCallback{
//		private PhysicalLayerMessage message;
//		/**
//		 * @param message
//		 */
//		public SendFailedCallback(PhysicalLayerMessage message) {
//			super();
//			this.message = message;
//		}
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.ServiceCallback#handle(de.uni_trier.ssds.service.CallbackHandler)
//		 */
//		public void handle(CallbackHandler handler) {
//			((PhysikalLayerCallbackHandler)handler).sendFailed(message);
//			
//		}
//
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.Dispatchable#copy()
//		 */
//		public Dispatchable copy() {
//			PhysicalLayerMessage messageCopy=(PhysicalLayerMessage)message.copy();
//			if(messageCopy==message){
//				return this;
//			}else{
//				return new MessageSendCallback(messageCopy);
//			}
//			
//		}
//
//		/* (non-Javadoc)
//		 * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
//		 */
//		public Class getReceiverServiceClass() {
//			return PhysikalLayerCallbackHandler.class;
//		}
//		
//	}

}
