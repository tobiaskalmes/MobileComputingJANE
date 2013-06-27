package de.uni_trier.jane.service.location_directory;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

public interface LocationDirectoryCache extends LocationDirectoryService {
    public abstract void addLocationDirectoryEntry(Address address, Position position,
            double cachingDelta);

    /* Uncomment these for stub generation
    public void requestLocationDirectoryEntry(Address address, ListenerID listener, double timeout);
    public void requestLocationDirectoryEntry(Address address, ListenerID listener);
    */
    
    public static final class LocationDirectoryCacheStub implements LocationDirectoryCache {
    
        private RuntimeOperatingSystem operatingSystem;
        private ServiceID LocationDirectoryCacheServiceID;
        public LocationDirectoryCacheStub(
            RuntimeOperatingSystem operatingSystem,
            ServiceID LocationDirectoryCacheServiceID) {
            this.operatingSystem = operatingSystem;
            this.LocationDirectoryCacheServiceID = LocationDirectoryCacheServiceID;
        }
        public void registerAtService() {
            operatingSystem.registerAtService(
                LocationDirectoryCacheServiceID,
                LocationDirectoryCache.class);
        }
        public void unregisterAtService() {
            operatingSystem.unregisterAtService(
                LocationDirectoryCacheServiceID,
                LocationDirectoryCache.class);
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
                return LocationDirectoryCache.class;
            }
            public void handle(SignalListener signalListener) {
                ((LocationDirectoryCache) signalListener).requestLocationDirectoryEntry(
                    address,
                    listener);
            }
        }
        public void requestLocationDirectoryEntry(Address address, ListenerID listener) {
            operatingSystem.sendSignal(
                LocationDirectoryCacheServiceID,
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
                return LocationDirectoryCache.class;
            }
            public void handle(SignalListener signalListener) {
                ((LocationDirectoryCache) signalListener).requestLocationDirectoryEntry(
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
                LocationDirectoryCacheServiceID,
                new RequestLocationDirectoryEntrySignal2(address, listener, timeout));
        }
        private static final class AddLocationDirectoryEntrySignal implements Signal {
    
            private Address address;
            private Position position;
            private double cachingDelta;
            public AddLocationDirectoryEntrySignal(
                Address address,
                Position position,
                double cachingDelta) {
                this.address = address;
                this.position = position;
                this.cachingDelta = cachingDelta;
            }
            public Dispatchable copy() {
                return this;
            }
            public Class getReceiverServiceClass() {
                return LocationDirectoryCache.class;
            }
            public void handle(SignalListener signalListener) {
                ((LocationDirectoryCache) signalListener).addLocationDirectoryEntry(
                    address,
                    position,
                    cachingDelta);
            }
        }
        public void addLocationDirectoryEntry(
            Address address,
            Position position,
            double cachingDelta) {
            operatingSystem.sendSignal(
                LocationDirectoryCacheServiceID,
                new AddLocationDirectoryEntrySignal(address, position, cachingDelta));
        }
    }


}