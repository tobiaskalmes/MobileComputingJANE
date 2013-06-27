/*****************************************************************************
 * 
 * GeographicLocation.java}
 * 
 * $Id: EllipseGeographicLocation.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;



/**
 * TODO: comment class  
 * @author daniel
 */
public class EllipseGeographicLocation extends EllipseTarget implements GeographicLocation{


    /**
     * Constructor for class <code>GeographicLocation</code>
     * @param target
     */
    public EllipseGeographicLocation(EllipseTarget target) {
        super(target);
    }
    
    /**
     * Constructor for class <code>GeographicLocation</code>
     * @param position
     * @param diameter
     */
    public EllipseGeographicLocation(Position position, double diameter) {
        super(position, diameter);
    }
    
    public GeographicLocation newCenter(Position position) {        
        return new EllipseGeographicLocation(position,getDiameter());
    }
    

    public Class getLocationManagerClass() {
        return GeographicLocationManager.class;
    }
    
    public boolean isLocatedAt(RuntimeOperatingSystem operatingSystem) {        
        
        return getManager(operatingSystem).locatedAt(this);
    }

    /**
     * TODO Comment method
     * @param operatingSystem
     * @return
     */
    private static LocationManager_sync getManager(RuntimeOperatingSystem operatingSystem) {
        LocationManager_sync locationManager_sync=
            (LocationManager_sync) operatingSystem.getAccessListenerStub(GeographicLocationManager.serviceID,LocationManager_sync.class);
        return locationManager_sync;
    }
    

    public ServiceID createLocationManagerService(ServiceUnit operatingSystem) {
        return GeographicLocationManager.createInstance(operatingSystem); 
    }

	public Shape getShape() {
		return new EllipseShape(this.getCenterPosition(),this.getDiameter()/2,Color.PINK,false);
	}

    /**
     * TODO Comment method
     * @param i
     * @return
     */
    public static Location getLocation(double radius,RuntimeOperatingSystem operatingSystem) {
        return getManager(operatingSystem).getCurrentLocation(new EllipseGeographicLocation(null,radius));
    }

 
}
