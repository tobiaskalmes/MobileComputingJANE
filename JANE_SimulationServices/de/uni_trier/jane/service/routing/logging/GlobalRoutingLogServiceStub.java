package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.service.*;

/**
 * This is semi aoutomatic generated code in order to provide the stubs for
 * a global routing log service
 */
public final class GlobalRoutingLogServiceStub implements GlobalRoutingLogService {
    private DeviceID globalDeviceID;
    private SimulationOperatingSystem operatingSystem;
    private ServiceID globalRoutingLogID;
    public GlobalRoutingLogServiceStub(SimulationOperatingSystem operatingSystem, ServiceID globalRoutingLogID) {
        globalDeviceID = operatingSystem.getGlobalDeviceID();
        this.operatingSystem = operatingSystem;
        this.globalRoutingLogID = globalRoutingLogID;
    }

    private static final class LogStartSignal implements Signal {
        private Address address;
        private MessageID messageID;
        public LogStartSignal(Address address, MessageID messageID) {
            this.address = address;
            this.messageID = messageID;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logStart(address, messageID);
        }

    }
    public void logStart(Address address, MessageID messageID) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogStartSignal(address, messageID));
    }

    private static final class LogDropMessageSignal
        implements
        Signal {
        private Address address;
        private MessageID messageID;
        public LogDropMessageSignal(Address address, MessageID messageID) {
            this.address = address;
            this.messageID = messageID;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logDropMessage(address, messageID);
        }

    }
    public void logDropMessage(Address address, MessageID messageID) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogDropMessageSignal(address, messageID));
    }

    private static final class LogLoopMessageSignal implements Signal {
    	
    	private Address address;
    	private MessageID messageID;
    	private int loopLength;
    	
    	public LogLoopMessageSignal(Address address, MessageID messageID, int loopLength) {
    		this.address = address;
    		this.messageID = messageID;
    		this.loopLength = loopLength;
    	}

    	public Dispatchable copy() {
    		return this;
    	}

    	public Class getReceiverServiceClass() {
    		return GlobalRoutingLogService.class;
    	}

    	public void handle(SignalListener service) {
    		((GlobalRoutingLogService) service).logLoopMessage(address, messageID, loopLength);
    	}

    }
    
    public void logLoopMessage(Address address, MessageID messageID, int loopLength) {
    	operatingSystem.sendSignal(globalDeviceID, globalRoutingLogID, new LogLoopMessageSignal(address, messageID, loopLength));
    }


    private static final class LogIgnoreMessageSignal
        implements
        Signal {
        private Address address;
        private MessageID messageID;
        public LogIgnoreMessageSignal(Address address, MessageID messageID) {
            this.address = address;
            this.messageID = messageID;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logIgnoreMessage(
                address,
                messageID);
        }

    }
    public void logIgnoreMessage(Address address, MessageID messageID) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogIgnoreMessageSignal(address, messageID));
    }

    private static final class LogDeliverMessageSignal
        implements
        Signal {
        private Address address;
        private MessageID messageID;
        public LogDeliverMessageSignal(Address address, MessageID messageID) {
            this.address = address;
            this.messageID = messageID;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logDeliverMessage(
                address,
                messageID);
        }

    }
    public void logDeliverMessage(Address address, MessageID messageID) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogDeliverMessageSignal(address, messageID));
    }

    private static final class LogForwardUnicastSignal
        implements
            Signal {
        private Address sender;
        private MessageID messageID;
        private RoutingHeader header;
        private Address receiver;
        public LogForwardUnicastSignal(
            Address sender,
            MessageID messageID,
            RoutingHeader header,
            Address receiver) {
            this.sender = sender;
            this.messageID = messageID;
            this.header = header;
            this.receiver = receiver;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logForwardUnicast(
                sender,
                messageID,
                header,
                receiver);
        }

    }
    public void logForwardUnicast(
        Address sender,
        MessageID messageID,
        RoutingHeader header,
        Address receiver) {
        operatingSystem
            .sendSignal(globalDeviceID,
                    globalRoutingLogID,
                new LogForwardUnicastSignal(
                    sender,
                    messageID,
                    header,
                    receiver));
    }

    private static final class LogForwardBroadcastSignal
        implements
            Signal {
        private Address sender;
        private MessageID messageID;
        private RoutingHeader header;
        public LogForwardBroadcastSignal(
            Address sender,
            MessageID messageID,
            RoutingHeader header) {
            this.sender = sender;
            this.messageID = messageID;
            this.header = header;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logForwardBroadcast(
                sender,
                messageID,
                header);
        }

    }
    public void logForwardBroadcast(
        Address sender,
        MessageID messageID,
        RoutingHeader header) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogForwardBroadcastSignal(sender, messageID, header));
    }

    private static final class LogForwardErrorSignal
        implements
            Signal {
        private Address sender;
        private MessageID messageID;
        private RoutingHeader header;
        private Address receiver;
        public LogForwardErrorSignal(
            Address sender,
            MessageID messageID,
            RoutingHeader header,
            Address receiver) {
            this.sender = sender;
            this.messageID = messageID;
            this.header = header;
            this.receiver = receiver;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logForwardError(
                sender,
                messageID,
                header,
                receiver);
        }

    }
    public void logForwardError(
        Address sender,
        MessageID messageID,
        RoutingHeader header,
        Address receiver) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogForwardErrorSignal(sender, messageID, header, receiver));
    }

    private static final class LogMessageReceivedSignal
        implements
            Signal {
        private Address receiver;
        private MessageID messageID;
        private RoutingHeader header;
        private Address sender;
        public LogMessageReceivedSignal(
            Address receiver,
            MessageID messageID,
            RoutingHeader header,
            Address sender) {
            this.receiver = receiver;
            this.messageID = messageID;
            this.header = header;
            this.sender = sender;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

        public void handle(SignalListener service) {
            ((GlobalRoutingLogService) service).logMessageReceived(
                receiver,
                messageID,
                header,
                sender);
        }

    }
    public void logMessageReceived(
        Address receiver,
        MessageID messageID,
        RoutingHeader header,
        Address sender) {
        operatingSystem.sendSignal(globalDeviceID,
                globalRoutingLogID,
            new LogMessageReceivedSignal(
                receiver,
                messageID,
                header,
                sender));
    }

	public boolean requiresMessageTrace() {
		RequiresMessageTraceAccess access = new RequiresMessageTraceAccess();
		Boolean bool = (Boolean)operatingSystem.accessSynchronous(globalRoutingLogID, access);
		return bool.booleanValue();
	}
	private class RequiresMessageTraceAccess implements ListenerAccess {

		public Object handle(SignalListener listener) {
			// TODO diese Methode gibt es nicht mehr
			return null;
//			
//			GlobalRoutingLogService service = (GlobalRoutingLogService)listener;
//			boolean result = service.requiresMessageTrace();
//			return new Boolean(result);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return GlobalRoutingLogService.class;
		}
		
	}

    private class LogDelagateMessageSignal implements Signal {

		private static final long serialVersionUID = -7641580378594696154L;

		private Address address;
        private ServiceID routingAlgorithmID;
        private MessageID messageID;
        private RoutingHeader routingHeader;

        public LogDelagateMessageSignal(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
        	this.address = address;
            this.routingAlgorithmID=routingAlgorithmID;
            this.messageID=messageID;
            this.routingHeader=routingHeader;
        }

        public void handle(SignalListener listener) {
            ((GlobalRoutingLogService)listener).logDelegateMessage(address, routingAlgorithmID, messageID, routingHeader);

        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return GlobalRoutingLogService.class;
        }

    }

    public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
        operatingSystem.sendSignal(globalRoutingLogID, new LogDelagateMessageSignal(address, routingAlgorithmID,messageID,routingHeader));
    }


}