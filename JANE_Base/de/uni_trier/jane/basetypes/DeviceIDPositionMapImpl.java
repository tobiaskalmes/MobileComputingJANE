/*
 * Created on 22.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.basetypes;

import java.util.*;



/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeviceIDPositionMapImpl implements DeviceIDPositionMap {

    private Map addressPositionMap;
    
    
    /**
     * 
     */
    public DeviceIDPositionMapImpl() {
        addressPositionMap = new HashMap();
    }
    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.AddressPositionMap#hasPosition(de.uni_trier.ubi.appsim.kernel.basetype.Address)
     */
    public boolean hasPosition(Address address) {
        return addressPositionMap.containsKey(address);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.AddressPositionMap#getPosition(de.uni_trier.ubi.appsim.kernel.basetype.Address)
     */
    public Position getPosition(Address address) {
        return (Position)addressPositionMap.get(address);
    }

    /**
     * @param address
     * @param position
     */
    public void addAddressPositionPair(Address address, Position position) {
        addressPositionMap.put(address, position);
    }
    public void removeAddressPositionPair(Address address) {
        addressPositionMap.remove(address);
        
    }

}
