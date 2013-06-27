/*
 * Created on 20.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.address_device_map;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.service.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface GlobalAddressDeviceMapService extends AddressDeviceMapService {

	public void put(DeviceID deviceID, Address address);
	public void remove(DeviceID deviceID);

	public static class PutSignal implements Signal {

		private DeviceID deviceID;
		private Address address;

		public PutSignal(DeviceID deviceID, Address address) {
			this.deviceID = deviceID;
			this.address = address;
		}

		public void handle(SignalListener service) {
			GlobalAddressDeviceMapService addressDeviceMapService = (GlobalAddressDeviceMapService)service;
			addressDeviceMapService.put(deviceID, address);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return GlobalAddressDeviceMapService.class;
		}
		
	}

	public static class RemoveSignal implements Signal {

		private DeviceID deviceID;

		public RemoveSignal(DeviceID deviceID) {
			this.deviceID = deviceID;
		}

		public void handle(SignalListener service) {
			GlobalAddressDeviceMapService addressDeviceMapService = (GlobalAddressDeviceMapService)service;
			addressDeviceMapService.remove(deviceID);
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return GlobalAddressDeviceMapService.class;
		}
		
	}
	
	public static final class GlobalAddressDeviceMap{
	    private ServiceID addressDeviceMapService;
	    private SimulationOperatingSystem operatingSystem;
        private DeviceID globalDeviceID;
	    
        /**
         * Constructor for class <code>AddressDeviceMap</code>
         * @param addressDeviceMapService
         * @param operatingSystem
         */
        public GlobalAddressDeviceMap(ServiceID addressDeviceMapService,
                SimulationOperatingSystem operatingSystem) {
            this.addressDeviceMapService = addressDeviceMapService;
            this.operatingSystem = operatingSystem;
            globalDeviceID=operatingSystem.getGlobalDeviceID();
        }

            
        public Address getLinkLayerAddress(DeviceID deviceID){
            return (Address)operatingSystem.accessSynchronous(globalDeviceID,addressDeviceMapService,new GetLinkLayerAddressAccess(deviceID));
            
        }
        
        
    	public static class GetLinkLayerAddressAccess extends AddressDevicePair implements ListenerAccess {

    		public GetLinkLayerAddressAccess(DeviceID deviceID) {
    			this.deviceID = deviceID;
    		}

    		public Object handle(SignalListener service) {
    			AddressDeviceMapService addressDeviceMapService = (AddressDeviceMapService)service;
    			return addressDeviceMapService.getLinkLayerAddress(deviceID);
    		}

    		public Class getReceiverServiceClass() {
    			return AddressDeviceMapService.class;
    		}

    	}

        
        public DeviceID getDeviceID(Address linkLayerAddress){
            return (DeviceID)operatingSystem.accessSynchronous(globalDeviceID,addressDeviceMapService,new GetDeviceIDAccess(linkLayerAddress));  
        }
        
        public static class GetDeviceIDAccess extends AddressDevicePair implements ListenerAccess {

    		/**
    		 * TODO comment
    		 * @param linkLayerAddress
    		 */
    		public GetDeviceIDAccess(Address linkLayerAddress) {
    			this.linkLayerAddress = linkLayerAddress;
    		}
    		
    		public Object handle(SignalListener service) {
    			AddressDeviceMapService addressDeviceMapService = (AddressDeviceMapService)service;
    			return addressDeviceMapService.getDeviceID(linkLayerAddress);
    		}

    		public Class getReceiverServiceClass() {
    			return AddressDeviceMapService.class;
    		}

    	}

        
        
        
	}


	

}
