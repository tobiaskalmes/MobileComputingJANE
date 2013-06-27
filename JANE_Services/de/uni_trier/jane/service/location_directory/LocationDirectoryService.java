package de.uni_trier.jane.service.location_directory;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * This listener is implemented by a location service

 * @author Christoph Lange
 */
public interface LocationDirectoryService {
    /**
     * Request the location information about a device. After a given time span, this method must 
     * return <code>null</code> if it was not able to determine the location of the device. 
     * @param address the address of the device of this lookup
     * @param listener the ID of the requesting service
     * @param timeout the timeout, in seconds
     */
    public void requestLocationDirectoryEntry(Address address, ListenerID listener, double timeout);

    /**
     * Request the location information about a device with a default timeout.
     * (The default timeout can be infinite, meaning no timeout, or any other reasonable value.
     * A service may choose only to implement the other overload of this method and to calling
     * that one from here, probably with a timeout parameter <code>Double.NaN</code>.) 
     * @param address the address of the device of this lookup
     * @param listener the ID of the requesting service
     */
    public void requestLocationDirectoryEntry(Address address, ListenerID listener);
    
    /**
     * This class wraps a request to a location server. It can be sent to a location server using
     * <code>RuntimeEnvironment.sendSignal</code>. This class is not needed any more since we have
     * the generated stub :-)
     * 
     * @see de.uni_trier.jane.service.operatingSystem.RuntimeEnvironment#sendSignal(ListenerID, Signal)
     * @author langec
     */
    public static final class LocationDirectoryEntryRequest implements Signal {
        private static final long serialVersionUID = 3469842763121679414L;

        private ListenerID replyHandlerID;
        private Address address;
        /**
         * the timeout of this location directory entry request. <code>Double.NaN</code> if
         * undefined.
         */
        private double timeout;

        /**
         * Creates a new request for a node's location with a "default" timeout. It is up to
         * the location directory service to decide how to handle a default timeout. Note: The
         * "default" timeout will be stored as <code>Double.NaN</code>!
         * 
         * @param address
         * @param replyHandlerID
         * 
         * @see java.lang.Double#NaN
         */
        public LocationDirectoryEntryRequest(Address address, ListenerID replyHandlerID) {
            this(address, replyHandlerID, Double.NaN);
        }
        /**
         * Creates a new request for a node's location with a given timeout
         * 
         * @param address the address of the node
         * @param replyHandlerID the handler to process the reply from the location service
         * @param the timeout of this request
         */
        public LocationDirectoryEntryRequest(Address address, ListenerID replyHandlerID, double timeout) {
            // TODO could be made more elegant by using parametrized types
            // FIXME Should I uncomment the following code?
            // if (!LocationDirectoryEntryReplyHandler.class.isAssignableFrom(replyHandlerID.getClass()))
            //    throw new IllegalArgumentException("reply handler must implement the LocationReplyHandler class");
            this.address = address;
            this.replyHandlerID = replyHandlerID;
            this.timeout = timeout;
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return LocationDirectoryService.class;
        }

        /**
         * Handle the request by forwarding it to the location service. Depending on whether a <code>timeout</code>
         * was provided in the constructor, either
         * <code>LocationDirectoryService.requestLocationDirectoryEntry(Address, ListenerID, double)</code> (with timeout)
         * or <code>LocationDirectoryService.requestLocationDirectoryEntry(Address, ListenerID)</code> (without timeout) will
         * be called.
         * 
         * @see LocationDirectoryService#requestLocationDirectoryEntry(Address, ListenerID)
         * @see de.uni_trier.jane.service.Signal#handle(de.uni_trier.jane.signaling.SignalListener)
         */
        public void handle(SignalListener service) {
            if (timeout == Double.NaN) {
                ((LocationDirectoryService) service).requestLocationDirectoryEntry(
                    address, replyHandlerID);
            }
            else {
                ((LocationDirectoryService) service).requestLocationDirectoryEntry(
                    address, replyHandlerID, timeout);
            }
        }
    }

    /**
     * This is a generated stub for a LocationDirectoryService. Do not change!
     */
    public static final class LocationDirectoryServiceStub implements LocationDirectoryService {
    
        private RuntimeOperatingSystem operatingSystem;
        private ServiceID LocationDirectoryServiceServiceID;
        public LocationDirectoryServiceStub(
            RuntimeOperatingSystem operatingSystem,
            ServiceID LocationDirectoryServiceServiceID) {
            this.operatingSystem = operatingSystem;
            this.LocationDirectoryServiceServiceID = LocationDirectoryServiceServiceID;
        }
        public void registerAtService() {
            operatingSystem.registerAtService(
                LocationDirectoryServiceServiceID,
                LocationDirectoryService.class);
        }
        public void unregisterAtService() {
            operatingSystem.unregisterAtService(
                LocationDirectoryServiceServiceID,
                LocationDirectoryService.class);
        }
        private static final class RequestLocationDirectoryEntrySignal implements Signal {
    
            private Address address;
            private ListenerID listener;
            public RequestLocationDirectoryEntrySignal(Address address, ListenerID listener) {
                this.address = address;
                this.listener = listener;
            }
            public Dispatchable copy() {
                return this;
            }
            public Class getReceiverServiceClass() {
                return LocationDirectoryService.class;
            }
            public void handle(SignalListener signalListener) {
                ((LocationDirectoryService) signalListener).requestLocationDirectoryEntry(
                    address,
                    listener);
            }
        }
        public void requestLocationDirectoryEntry(Address address, ListenerID listener) {
            operatingSystem.sendSignal(
                LocationDirectoryServiceServiceID,
                new RequestLocationDirectoryEntrySignal(address, listener));
        }
        private static final class RequestLocationDirectoryEntrySignal2 implements Signal {
    
            private Address address;
            private ListenerID listener;
            private double timeout;
            public RequestLocationDirectoryEntrySignal2(
                Address address,
                ListenerID listener,
                double timeout) {
                this.address = address;
                this.listener = listener;
                this.timeout = timeout;
            }
            public Dispatchable copy() {
                return this;
            }
            public Class getReceiverServiceClass() {
                return LocationDirectoryService.class;
            }
            public void handle(SignalListener signalListener) {
                ((LocationDirectoryService) signalListener).requestLocationDirectoryEntry(
                    address,
                    listener,
                    timeout);
            }
        }
        public void requestLocationDirectoryEntry(
            Address address,
            ListenerID listener,
            double timeout) {
            operatingSystem.sendSignal(
                LocationDirectoryServiceServiceID,
                new RequestLocationDirectoryEntrySignal2(address, listener, timeout));
        }
    }
}
 