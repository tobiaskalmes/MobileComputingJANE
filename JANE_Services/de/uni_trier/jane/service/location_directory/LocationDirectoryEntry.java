package de.uni_trier.jane.service.location_directory;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;

/**
 * This class collects the information provided by the location service
 * when it replies to a location service request.
 */
public class LocationDirectoryEntry {

    private Address address;
    private Position position;
    
    /**
     * Construct a new location service info object.
     * @param address the address of the requested device
     * @param position the position of the requested device
     */
    public LocationDirectoryEntry(Address address, Position position) {
        this.address = address;
        this.position = position;
    }
    
    /**
     * Get the address of the requested device.
     * @return the device address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Get the position of the requested device.
     * @return the device position
     */
    public Position getPosition() {
        return position;
    }

}
