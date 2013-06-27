/*****************************************************************************
 * 
 * Address.java
 * 
 * $Id: SimulationDeviceID.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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

import de.uni_trier.jane.basetypes.*;



/**
 * Each device has a unique address.
 */
public class SimulationDeviceID extends  DeviceID { 

	private final static String VERSION = "$Id: SimulationDeviceID.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private final static int PRIME = 1001;
	private long address;
	
	/**
	 * Construct a new <code>Address</code> object.
	 * @param address the address number the number should be greater than 0
	 * all other numbers are reserved
	 */
	public SimulationDeviceID(long address) {
        if (address==0) 
            throw new IllegalArgumentException("Address mus be greater 0");
		this.address = address;
	}

	public  int getCodingSize() {
		return 8*8;
	}

	public String toString() {
		return Long.toString(address);
	}
	
	public int hashCode() {
		int result = 0;
		result = PRIME*result + (int)(address >>> 32);
		result = PRIME*result + (int)(address & 0xFFFFFFFF);		
		return result;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		SimulationDeviceID oth = (SimulationDeviceID) other;
		if (oth.address == address) {
			return true;
		}
		return false;
	}

	/**
	 * Compares two addresses.
	 * @param other the other address
	 * @return -1, 0 or 1, if this address is lower, equal or greater
	 */
	public int compareTo(Object other) {
		SimulationDeviceID a = (SimulationDeviceID)other;
		if(address < a.address) {
			return -1;
		}
		else if(address == a.address) {
			return 0;
		}
		else {
			return 1;
		}
	}

    /**
     * TODO: comment method 
     * @return
     */
    public long getLong() {

        return address;
    }

}
