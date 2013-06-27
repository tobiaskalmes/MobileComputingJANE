/*****************************************************************************
* 
* PositioningData.java
* 
* $Id: PositioningData.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.positioning;

import de.uni_trier.jane.basetypes.*;

import java.io.Serializable;

/**
 * This class is used by location systems in order to provide the current location
 * information about the device.
 */
public class PositioningData implements Serializable {

    private Position position;
    
    private Position speed;

    /**
	 * Construct a new instance of this class.
	 * @param position the current position
	 */
    public PositioningData(Position position) {
        this.position = position;
        speed = null;
    }

    /**
     * 
     * Constructor for class <code>PositioningData</code>
     * @param position
     * @param speed
     */
    public PositioningData(Position position, Position speed) {
        this.position = position;
        this.speed = speed;
    }

    /**
	 * Get the current position. When there is currently no position info
	 * available (e.g. if a GPS receiver is operated indoors) the method will
	 * return <code>null</code>.
	 * @return the position
	 */
    public Position getPosition() {
        return position;
    }
    
    
    public double getSpeed(){
        if (speed==null) return Double.NaN;
        return speed.distance(position);
    }

    /**
     * 
     * TODO Comment method
     * @return
     */
	public Position getDirection() {
		return speed;
	}

	public String toString() {
        if (speed==null){
		    return position.toString();
        }
        return position+":"+speed;
	}

}
