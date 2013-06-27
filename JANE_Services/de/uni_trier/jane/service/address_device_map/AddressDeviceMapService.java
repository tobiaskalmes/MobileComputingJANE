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
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface AddressDeviceMapService {

	public Address getLinkLayerAddress(DeviceID deviceID);
	public DeviceID getDeviceID(Address address);
    
	public static final class AddressDeviceMapFassade{
	    private ServiceID addressDeviceMapService;
	    private RuntimeOperatingSystem operatingSystem;
	    
        /**
         * Constructor for class <code>AddressDeviceMap</code>
         * @param addressDeviceMapService
         * @param operatingSystem
         */
        public AddressDeviceMapFassade(ServiceID addressDeviceMapService,
                RuntimeOperatingSystem operatingSystem) {
            this.addressDeviceMapService = addressDeviceMapService;
            this.operatingSystem = operatingSystem;
        }
        private static class GetLinkLayerAddressAccess extends AddressDevicePair implements ListenerAccess {

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
            
        public Address getAddress(DeviceID deviceID){
            return (Address)operatingSystem.accessSynchronous(addressDeviceMapService,new GetLinkLayerAddressAccess(deviceID));
        }
        
        
    	private static class GetDeviceIDAccess extends AddressDevicePair implements ListenerAccess {

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

        
        public DeviceID getDeviceID(Address linkLayerAddress){
            return (DeviceID)operatingSystem.accessSynchronous(addressDeviceMapService,new GetDeviceIDAccess(linkLayerAddress));  
        }

        
        
        
	}
	


}
