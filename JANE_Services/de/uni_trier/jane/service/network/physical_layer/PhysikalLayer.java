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
public interface PhysikalLayer {

    public void sendMessage(PhysicalLayerMessage message,PhysikalLayerCallbackHandler handle);
    public void sendMessage(PhysicalLayerMessage message, double signalStrength,PhysikalLayerCallbackHandler handle);
	public void sendMessage(PhysicalLayerMessage message);
	public void sendMessage(PhysicalLayerMessage message, double signalStrength);
	public void setSignalStrength(double signalStrength);
    public void requestMediaInfo();
	
    public class RequestMediaInfo implements Signal {
      
        public void handle(SignalListener service) {
            ((PhysikalLayer)service).requestMediaInfo();
            
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return PhysikalLayer.class;
        }
    }

//    public static class SendMessageTask implements TaskCallbackPair{
//        private ServiceTask serviceTask;
//        private CallbackHandler callbackHandler;
//        
//        
//        public SendMessageTask(PhysicalLayerMessage message,PhysikalLayerCallbackHandler callbackHandler) {
//            this(message,-1,callbackHandler);
//        }
//           
//        public SendMessageTask(PhysicalLayerMessage message, double signalStrength, PhysikalLayerCallbackHandler callbackHandler) {
//            serviceTask=new SendMessageSignal(message,signalStrength);
//            this.callbackHandler=callbackHandler;
//
//        }
//
//        public ServiceTask getServiceTask() {
//            return serviceTask;
//        }
//
//        public CallbackHandler getCallbackHandler() {
//            return callbackHandler;
//        }
//        
//    }
    
	public static class SendMessageSignal implements Signal{
		private PhysicalLayerMessage message;
		private double signalStrength;
		
		public SendMessageSignal(PhysicalLayerMessage message) {
			this.message=message;
			signalStrength=-1;
		
		}
		
		public SendMessageSignal(PhysicalLayerMessage message, double signalStrength) {
			
			this.signalStrength=signalStrength;	
			this.message=message;
		}
		
		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
		 */
//		public void handle(ServiceID sender, Service service, TaskHandle handle) {
//			if (signalStrength<0){
//				((PhysikalLayer)service).sendMessage(message,handle);
//			}else{
//				((PhysikalLayer)service).sendMessage(message,signalStrength,handle);
//			}	
//		}
		
		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
		 */
		public void handle(SignalListener service) {
			if (signalStrength<0){
				((PhysikalLayer)service).sendMessage(message);
			}else{
				((PhysikalLayer)service).sendMessage(message,signalStrength);
			}				
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.Dispatchable#copy()
		 */
		public Dispatchable copy() {
			PhysicalLayerMessage messageCopy=(PhysicalLayerMessage)message.copy();
			if (message==messageCopy){
				return this;
			}else{
				SendMessageSignal signal=new SendMessageSignal(messageCopy);
				signal.signalStrength=signalStrength;
				return signal;
			}
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
		 */
		public Class getReceiverServiceClass() {
			return PhysikalLayer.class;
		}


		
	}

	public static class SetSignalStrength implements Signal{
		private double signalStrength;
		/**
		 * @param signalStrength
		 */
		public SetSignalStrength(double signalStrength) {
			this.signalStrength = signalStrength;
		}
		
		/* (non-Javadoc)
		 * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
		 */
		public void handle(SignalListener service) {
			((PhysikalLayer)service).setSignalStrength(signalStrength);
			
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
			return PhysikalLayer.class;
		}
		
	}
}
