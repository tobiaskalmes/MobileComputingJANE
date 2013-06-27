package de.uni_trier.jane.service.network.link_layer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeEnvironment;
import de.uni_trier.jane.signaling.*;

/**
 * TODO: comment
 */
public interface LinkLayerObserver extends UnicastObserver, BroadcastObserver {

	// TODO Kommentare sind nur von den LinkLayer ServiceCallbacks übernommen!!!

    /**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a unicast to the @see LinkLayerAddress receiver. 
	 * Send this callback when the message has been processed and completely put on the medium
	 * @see LinkLayer 
	 */
    public static class UnicastProcessedSignal implements Signal {

		private Address receiver;
		private LinkLayerMessage message;

		/**
		 * Constructor for class <code>UnicastProcessedCallback</code>
		 * @param receiver	the message receiver
		 * @param message	the message
		 */
		public UnicastProcessedSignal(Address receiver, LinkLayerMessage message) {
			this.receiver = receiver;
			this.message = message;
		}

		public Dispatchable copy() {
			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
			if (messageCopy == message) {
				return this;
			}
			return new UnicastProcessedSignal(receiver, messageCopy);
		}

		public Class getReceiverServiceClass() {
			return LinkLayerObserver.class;
		}

		public void handle(SignalListener service) {
			LinkLayerObserver linkLayerObserver = (LinkLayerObserver)service;
			linkLayerObserver.notifyUnicastProcessed(receiver, message);
		}

    }
    
	/**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a unicast to the @see LinkLayerAddress receiver.
	 * Send this callback when the message has been received correctly.
	 * @see LinkLayer 
	 */    
    public static class UnicastReceivedSignal implements Signal {

		private LinkLayerMessage message;

		private Address receiver;

		/** 
		 * Constructor for class <code>UnicastReceivedCallback</code>
		 * @param receiver	the message receiver
		 * @param message	the message
		 */
		public UnicastReceivedSignal(Address receiver,
				LinkLayerMessage message) {
			this.message = message;
			this.receiver = receiver;

		}

		public Dispatchable copy() {
			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
			if (messageCopy == message) {
				return this;
			}
			return new UnicastReceivedSignal(receiver, messageCopy);
		}

		public Class getReceiverServiceClass() {
			return LinkLayerObserver.class;
		}

		public void handle(SignalListener service) {
			LinkLayerObserver linkLayerObserver = (LinkLayerObserver)service;
			linkLayerObserver.notifyUnicastReceived(receiver, message);
		}

	}    

    /**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a unicast to the @see LinkLayerAddress receiver. 
	 * Send this callback when the message has been lost.
	 * @see LinkLayer 
	 */   
    public static class UnicastLostSignal implements  Signal {

        private LinkLayerMessage message;
        private Address receiver;

        /**
         * Constructor for class <code>UnicastLostCallback</code>
		 * @param receiver	the message receiver
		 * @param message	the message
         */
        public UnicastLostSignal(Address receiver, LinkLayerMessage message) {
            this.message=message;
            this.receiver=receiver;
            
        }

        public Dispatchable copy() {
        	LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
            if (messageCopy==message){
                return this;
            }
            return new UnicastLostSignal(receiver,messageCopy);
        }
        
		public Class getReceiverServiceClass() {
			return LinkLayerObserver.class;
		}

		public void handle(SignalListener service) {
			LinkLayerObserver linkLayerObserver = (LinkLayerObserver)service;
			linkLayerObserver.notifyUnicastLost(receiver, message);
		}

    }    

    /**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a unicast to the @see LinkLayerAddress receiver. 
	 * Send this callback when the message status is unknown. Probably the message has been lost or
	 * the message has been received.
	 * @see LinkLayer 
	 */   
    public static class UnicastUndefinedSignal implements Signal {
        
		private Address receiver;
		private LinkLayerMessage message;

		/**
		 * Constructor for class <code>UnicastUndefinedCallback</code>
		 * @param receiver	the message receiver
		 * @param message	the message
		 */
		public UnicastUndefinedSignal(Address receiver,
				LinkLayerMessage message) {
			this.receiver = receiver;
			this.message = message;
		}

		public Dispatchable copy() {
			LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
			if (messageCopy == message) {
				return this;
			}
			return new UnicastUndefinedSignal(receiver, messageCopy);
		}

		public Class getReceiverServiceClass() {
			return LinkLayerObserver.class;
		}

		public void handle(SignalListener service) {
			LinkLayerObserver linkLayerObserver = (LinkLayerObserver)service;
			linkLayerObserver.notifyUnicastUndefined(receiver, message);
		}

	} 

    
	/**
	 * Uses this ServiceCallback to inform a message sending service about the status of its message
	 * send by a broadcast. 
	 * Send this callback when the message has been processed and completely put on the medium
	 * @see LinkLayer 
	 */
    public static class BroadcastProcessedSignal implements Signal {

    	private LinkLayerMessage message;

    	/**
    	 * Constructor for class <code>BroadcastProcessedCallback</code>
    	 * @param message	the send message
    	 */
		public BroadcastProcessedSignal(LinkLayerMessage message) {
			this.message = message;
		}

        public Dispatchable copy() {
            return this;
//        	LinkLayerMessage messageCopy = (LinkLayerMessage) message.copy();
//			if (messageCopy == message) {
//				return this;
//			}
//			return new BroadcastProcessedSignal(messageCopy);
        }

		public Class getReceiverServiceClass() {
			return LinkLayerObserver.class;
		}

		public void handle(SignalListener service) {
			LinkLayerObserver linkLayerObserver = (LinkLayerObserver)service;
			linkLayerObserver.notifyBroadcastProcessed(message);
		}
        
    }
    
    
    public class LinkLayerObserverStub implements LinkLayerObserver{
        
        private RuntimeEnvironment runtimeEnvironment;
        
        

        /**
         * Constructor for class <code>LinkLayerObserverStub</code>
         * @param environment
         */
        public LinkLayerObserverStub(RuntimeEnvironment environment) {
            runtimeEnvironment = environment;
        }

        public void notifyUnicastProcessed(Address receiver, LinkLayerMessage message) {
            runtimeEnvironment.sendSignal(new UnicastProcessedSignal(receiver,message));
        }

        public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
            runtimeEnvironment.sendSignal(new UnicastReceivedSignal(receiver,message));
        }

        public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
            runtimeEnvironment.sendSignal(new UnicastLostSignal(receiver,message));
        }

        public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message) {
            runtimeEnvironment.sendSignal(new UnicastUndefinedSignal(receiver,message));
        }

        public void notifyBroadcastProcessed(LinkLayerMessage message) {
            runtimeEnvironment.sendSignal(new BroadcastProcessedSignal(message));
        }
        
    }

}
