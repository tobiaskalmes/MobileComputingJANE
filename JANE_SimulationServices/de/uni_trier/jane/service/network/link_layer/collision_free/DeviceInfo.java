package de.uni_trier.jane.service.network.link_layer.collision_free;

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeviceInfo {
    private DeviceID deviceID;
    private Address linkLayerAddress;
    private ServiceID serviceID;
    /**
     * 
     * Constructor for class <code>DeviceInfo</code>
     *
     * @param deviceID
     * @param linkLayerAddress
     * @param serviceID
     */
    public DeviceInfo(DeviceID deviceID, Address linkLayerAddress, ServiceID serviceID) {
        this.deviceID=deviceID;
        this.linkLayerAddress=linkLayerAddress;
        this.serviceID=serviceID;   
    }

    public DeviceID getDeviceID() {
        return deviceID;
    }
    public Address getLinkLayerAddress() {
        return linkLayerAddress;
    }
    public ServiceID getServiceID() {
        return serviceID;
    }
}