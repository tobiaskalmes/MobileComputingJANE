/*****************************************************************************
* 
* LocationData.java
* 
* $Id: LocationData.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.neighbor_discovery.*;

import java.io.*;

/**
 * TODO: comment
 */
public class LocationData implements Data {

    
    private static final long serialVersionUID = 1L;
    
    public static final DataID DATA_ID = new ClassDataID(LocationData.class);
    
    static{
        map();
    }


    /**
     * TODO Comment method
     */
    public static void map() {
        DataMapper.map(LocationData.class,serialVersionUID,new DataSerializer() {
        
            public Object readData(ObjectInputStream in) throws IOException {
                return new LocationData(new Position(
                        in.readDouble(),
                        in.readDouble()));
            }
        
            public void write(Object data, ObjectOutputStream out) throws IOException {
                LocationData locationData=(LocationData)data;
                out.writeDouble(locationData.getPosition().getX());
                out.writeDouble(locationData.getPosition().getY());
        
            }
        
        });
    }
    
    

    private Position position;
    
    
    public static LocationData fromNeighborDiscoveryData(NeighborDiscoveryData data) {
    	return (LocationData)data.getDataMap().getData(DATA_ID);
    }

    public static Position getPosition(NeighborDiscoveryData data) {
    	LocationData locationData = (LocationData)data.getDataMap().getData(DATA_ID);
    	if(locationData == null) {
    		return null;
    	}
    	return locationData.getPosition();
    }
    
    /**
     * TODO: comment
     * @param position
     */
    public LocationData(Position position) {
        this.position = position;
    }
    
    public DataID getDataID() {
        return DATA_ID;
    }

    public Data copy() {
        return this;
    }

    public int getSize() {
    	// TODO: welche Größe wählt man am besten???
        return 32;
    }

    /**
     * @return Returns the position.
     */
    public Position getPosition() {
        return position;
    }

	
	public boolean equals(Object arg0) {
		if (arg0 instanceof LocationData) {
			LocationData locationData = (LocationData) arg0;
			boolean value=locationData.getPosition().equals(position);
			return value;
		}
		return false;
	}
    
    

}
