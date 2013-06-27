package de.uni_trier.jane.service.neighbor_discovery;


import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;

/**
 * A listener which has prevoiusly registered at a discovery service will be notified when new
 * device information is available.
 * @see de.uni_trier.jane.service.beaconing.BeaconingService_sync
 */
public interface NeighborDiscoveryListener extends SignalListener {

    /**
     * This method is called when a new information about a device is available, i.e.
     * information about this device was not previously stored in the data structure.
     * @param neighborData the information about the device
     */
    public void setNeighborData(NeighborDiscoveryData neighborData);

    /**
     * This method is called when new information about a previously stored device
     * is available.
     * @param neighborData the new information for the device
     */
    public void updateNeighborData(NeighborDiscoveryData neighborData);

    /**
     * This method is called when information about the device is no more valid.
     * @param linkLayerAddress the address of the removed device
     */
    public void removeNeighborData(Address linkLayerAddress);

}
