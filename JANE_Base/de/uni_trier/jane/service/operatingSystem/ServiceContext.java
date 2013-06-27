/*****************************************************************************
 * 
 * ServiceContext.java
 * 
 * $Id: ServiceContext.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem; 

import java.io.*;

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class ServiceContext implements Serializable {

    private DeviceID deviceID;
    private ServiceID serviceID;

    /**
     * Constructor for class <code>ServiceContext</code>
     * @param serviceID
     * @param deviceID
     */
    public ServiceContext(ServiceID serviceID, DeviceID deviceID) {
        this.deviceID=deviceID;
        this.serviceID=serviceID;
    }

    /**
     * Constructor for class <code>ServiceContext</code>
     * 
     */
    public ServiceContext() {
        //empty...
    }
    
    /**
     * @return Returns the deviceID.
     */
    public DeviceID getServiceDeviceID() {
        return deviceID;
    }
    
    public ServiceContext getOSContext(){
        return new ServiceContext(OperatingSystem.OperatingSystemID,deviceID);
        
    }
    
    /**
     * @return Returns the serviceID.
     */
    public ServiceID getServiceID() {
        return serviceID;
    }
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (deviceID != null) {
            result = PRIME * result + deviceID.hashCode();
        }
        if (serviceID != null) {
            result = PRIME * result + serviceID.hashCode();
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

        ServiceContext other = (ServiceContext) oth;
        if (this.deviceID == null) {
            if (other.deviceID != null) {
                return false;
            }
        } else {
            if (!this.deviceID.equals(other.deviceID)) {
                return false;
            }
        }
        if (this.serviceID == null) {
            if (other.serviceID != null) {
                return false;
            }
        } else {
            if (!this.serviceID.equals(other.serviceID)) {
                return false;
            }
        }

        return true;
    }
    
    public String toString() {
     
        return deviceID+":"+serviceID;
    }
}
