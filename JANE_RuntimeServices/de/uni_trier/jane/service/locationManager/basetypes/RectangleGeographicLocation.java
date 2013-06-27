/*****************************************************************************
 * 
 * RectangleGeographicLocation.java
 * 
 * $Id: RectangleGeographicLocation.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
import de.uni_trier.jane.service.*;
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
public class RectangleGeographicLocation extends RectangleTarget implements
        GeographicLocation {

    /**
     * Constructor for class <code>RectangleGeographicLocation</code>
     * @param center
     * @param extent
     */
    public RectangleGeographicLocation(Position center, Extent extent) {
        super(center, extent);
    }

    /**
     * Constructor for class <code>RectangleGeographicLocation</code>
     * @param center
     * @param diameter
     */
    public RectangleGeographicLocation(Position center, double diameter) {
        super(center, diameter);
    }

    /**
     * Constructor for class <code>RectangleGeographicLocation</code>
     * @param rectangle
     */
    public RectangleGeographicLocation(Rectangle rectangle) {
        super(rectangle);
    }
    
    /**
     * 
     * Constructor for class <code>RectangleGeographicLocation</code>
     * @param width
     * @param height
     */
    public RectangleGeographicLocation(double  width, double height) {
        
        super(new Rectangle(0,0,width,height));
    }

    public GeographicLocation newCenter(Position position) {
        
        return new RectangleGeographicLocation(position,getExtent());
    }

    public Class getLocationManagerClass() {
        return GeographicLocationManager.class;
    }
    
    public boolean isLocatedAt(RuntimeOperatingSystem operatingSystem) {
        LocationManager_sync locationManager_sync=
            (LocationManager_sync) operatingSystem.getAccessListenerStub(GeographicLocationManager.serviceID,LocationManager_sync.class);
        
        return locationManager_sync.locatedAt(this);
    }
    

    public ServiceID createLocationManagerService(ServiceUnit operatingSystem) {
        return GeographicLocationManager.createInstance(operatingSystem);
        
    }

	public Shape getShape() {
		return new RectangleShape(this.getCenterPosition(),this.getExtent(),Color.PINK,false);
	}
    
    

}
