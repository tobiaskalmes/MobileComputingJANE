/*****************************************************************************
 * 
 * SimulationLinkLayerAddress.java
 * 
 * $Id: SimulationLinkLayerAddress.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer; 

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimulationLinkLayerAddress extends LinkLayerAddress {

    private DeviceID deviceID;

    /**
     * Constructor for class <code>SimulationLinkLayerAddress</code>
     * @param deviceID
     */
    public SimulationLinkLayerAddress(DeviceID deviceID) {
        this.deviceID=deviceID;
    }
    
    /**
     * Constructor for class SimulationLinkLayerAddress 
     *
     * 
     */
    public SimulationLinkLayerAddress(int deviceID) {
        this.deviceID=new SimulationDeviceID(deviceID);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress#getCodingSize()
     */
    public int getCodingSize() {
        return deviceID.getCodingSize();
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        SimulationLinkLayerAddress address=(SimulationLinkLayerAddress)o;
        
        return deviceID.compareTo(address.deviceID);
    }

    
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (deviceID != null) {
            result = PRIME * result + deviceID.hashCode();
        }

        return result;
    }

    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth == null) {
            return false;
        }

        if (oth.getClass() != getClass()) {
            return false;
        }

        SimulationLinkLayerAddress other = (SimulationLinkLayerAddress) oth;
        if (this.deviceID == null) {
            if (other.deviceID != null) {
                return false;
            }
        } else {
            if (!this.deviceID.equals(other.deviceID)) {
                return false;
            }
        }

        return true;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
     
        return deviceID.toString();
    }
    
    /**
     * @return Returns the deviceID.
     */
    public DeviceID getDeviceID() {
        return deviceID;
    }
}
