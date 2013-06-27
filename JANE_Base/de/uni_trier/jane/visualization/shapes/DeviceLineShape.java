/*****************************************************************************
 * 
 * DeviceLineShape.java
 * 
 * $Id: DeviceLineShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.visualization.shapes; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class DeviceLineShape implements Shape {

    private Address address;
    private Position relFrom;
    private Position relTo;
    private Color color;

    /**
     * 
     * Constructor for class <code>DeviceLineShape</code>
     * @param address
     * @param relFrom
     * @param relTo
     * @param color
     */
    public DeviceLineShape(Address address, Position relFrom, Position relTo, Color color) {
        this.address=address;
        this.relFrom=relFrom;
        this.relTo=relTo;
        this.color=color;
        
    }

    public void visualize(Position position, Worldspace worldspace,
            DeviceIDPositionMap addressPositionMap) {
        Position devicePos=addressPositionMap.getPosition(address);
        if (devicePos==null) return;
        devicePos=devicePos.add(position);
        Position from=devicePos.add(relFrom);
        Position to=devicePos.add(relTo);
        worldspace.drawLine(from,to,color);
    }

    public Rectangle getRectangle(Position position, Matrix matrix) {
        // TODO Auto-generated method stub
        return null;
    }

}
