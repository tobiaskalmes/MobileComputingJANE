/*****************************************************************************
 * 
 * AddressDeviceMapping.java
 * 
 * $Id: AddressDeviceMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.basetypes;

import java.util.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddressDeviceMapping {

	private Map deviceAddressMap;
	private Map addressDeviceMap;
	
	public AddressDeviceMapping() {
		deviceAddressMap = new HashMap();
		addressDeviceMap = new HashMap();
	}
	
	public DeviceID getDeviceID(Address address) {
		return (DeviceID)addressDeviceMap.get(address);
	}

	public Address[] getAddresses(DeviceID deviceID) {
		if(!deviceAddressMap.containsKey(deviceID)) {
			return null;
		}
		Set addressSet = (Set)deviceAddressMap.get(deviceID);
		return (Address[])addressSet.toArray(new Address[addressSet.size()]);
	}
	
	public void register(DeviceID deviceID, Address address) {
		Set addressSet = (Set)deviceAddressMap.get(deviceID);
		addressSet.add(address);
		addressDeviceMap.put(address, deviceID);
	}
	
	public void unregister(DeviceID deviceID, Address address) {
		if(!address.equals(deviceID)) {
			Set addressSet = (Set)deviceAddressMap.get(deviceID);
			addressSet.remove(address);
			addressDeviceMap.remove(address);
		}
	}
	
	public void enterDevice(DeviceID deviceID) {
		Set addressSet = new LinkedHashSet();
		addressSet.add(deviceID);
		deviceAddressMap.put(deviceID, addressSet);
		addressDeviceMap.put(deviceID, deviceID);
	}
	
	public void exitDevice(DeviceID deviceID) {
		Set addressSet = (Set)deviceAddressMap.get(deviceID);
		Iterator iterator = addressSet.iterator();
		while (iterator.hasNext()) {
			Address address = (Address) iterator.next();
			addressDeviceMap.remove(address);
		}
		deviceAddressMap.remove(deviceID);
	}

}
