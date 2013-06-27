package de.uni_trier.jane.service.location_directory;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * A location directory service which is able to feed a location directory cache must implement this interface
 * instead of just LocationDirectoryService.
 * 
 * @author langec
 *
 */
public interface CacheableLocationDirectoryService extends LocationDirectoryService {
    /* Uncomment these for stub generation
    public void requestLocationDirectoryEntry(Address address, ListenerID listener, double timeout);
    public void requestLocationDirectoryEntry(Address address, ListenerID listener);
    */

    public void registerCache(ServiceID cache);
    public void unregisterCache(ServiceID cache);
    public static final class CacheableLocationDirectoryServiceStub
        implements
            CacheableLocationDirectoryService {
    
        private RuntimeOperatingSystem operatingSystem;
        private ServiceID CacheableLocationDirectoryServiceServiceID;
        public CacheableLocationDirectoryServiceStub(
            RuntimeOperatingSystem operatingSystem,
            ServiceID CacheableLocationDirectoryServiceServiceID) {
            this.operatingSystem = operatingSystem;
            this.CacheableLocationDirectoryServiceServiceID = CacheableLocationDirectoryServiceServiceID;
        }
        public void registerAtService() {
            operatingSystem.registerAtService(
                CacheableLocationDirectoryServiceServiceID,
                CacheableLocationDirectoryService.class);
        }
        public void unregisterAtService() {
            operatingSystem.unregisterAtService(
                CacheableLocationDirectoryServiceServiceID,
                CacheableLocationDirectoryService.class);
        }
        private static final class UnregisterCacheSignal implements Signal {
    
            private ServiceID cache;
    
            public UnregisterCacheSignal(ServiceID cache) {
                this.cache = cache;
            }
    
            public Dispatchable copy() {
                return this;
            }
    
            public Class getReceiverServiceClass() {
                return CacheableLocationDirectoryService.class;
            }
    
            public void handle(SignalListener signalListener) {
                ((CacheableLocationDirectoryService) signalListener).unregisterCache(cache);
            }
        }
        public void unregisterCache(ServiceID cache) {
            operatingSystem.sendSignal(
                CacheableLocationDirectoryServiceServiceID,
                new UnregisterCacheSignal(cache));
        }
        private static final class RegisterCacheSignal implements Signal {
    
            private ServiceID cache;
    
            public RegisterCacheSignal(ServiceID cache) {
                this.cache = cache;
            }
    
            public Dispatchable copy() {
                return this;
            }
    
            public Class getReceiverServiceClass() {
                return CacheableLocationDirectoryService.class;
            }
    
            public void handle(SignalListener signalListener) {
                ((CacheableLocationDirectoryService) signalListener).registerCache(cache);
            }
        }
        public void registerCache(ServiceID cache) {
            operatingSystem.sendSignal(
                CacheableLocationDirectoryServiceServiceID,
                new RegisterCacheSignal(cache));
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
                return CacheableLocationDirectoryService.class;
            }
            public void handle(SignalListener signalListener) {
                ((CacheableLocationDirectoryService) signalListener).requestLocationDirectoryEntry(
                    address,
                    listener);
            }
        }
        public void requestLocationDirectoryEntry(Address address, ListenerID listener) {
            operatingSystem.sendSignal(
                CacheableLocationDirectoryServiceServiceID,
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
                return CacheableLocationDirectoryService.class;
            }
            public void handle(SignalListener signalListener) {
                ((CacheableLocationDirectoryService) signalListener).requestLocationDirectoryEntry(
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
                CacheableLocationDirectoryServiceServiceID,
                new RequestLocationDirectoryEntrySignal2(address, listener, timeout));
        }
    }
}
