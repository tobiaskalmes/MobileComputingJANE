/*****************************************************************************
 * 
 * RFIDLocation.java
 * 
 * $Id: DeviceAddressLocation.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.locationManager.basetypes; 

import de.uni_trier.jane.basetypes.*;

import de.uni_trier.jane.service.locationManager.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;



/**
 * @author goergen
 *
 * TODO comment class
 */
public class DeviceAddressLocation implements Location {
    
    private Address locationID; 

    /**
     * Constructor for class <code>DeviceAddressLocation</code>
     * @param locationID
     */
    public DeviceAddressLocation(Address locationID) {
        this.locationID = locationID;
    }
    public Class getLocationManagerClass() {

        return DeviceIDLocationManager.class;
    }

    public ServiceID createLocationManagerService(ServiceUnit serviceUnit) {
        return DeviceIDLocationManager.createInstance(serviceUnit);
    }
    
    
    public boolean isLocatedAt(RuntimeOperatingSystem operatingSystem) {
        LocationManager_sync locationManager_sync=
            (LocationManager_sync) operatingSystem.getAccessListenerStub(DeviceIDLocationManager.serviceID,LocationManager_sync.class);
        
        return locationManager_sync.locatedAt(this);
    }
    public Address getNeighborDeviceAddress(){
        return locationID;
    }



    public int getCodingSize() {
        return 8*8;
    }

    public Shape getShape(DeviceID address, Color color, boolean filled) {

        return new LineShape(address,locationID,color);
    }
    
    public String toString() {
     
        return "LocationID: "+locationID ;
    }

}
