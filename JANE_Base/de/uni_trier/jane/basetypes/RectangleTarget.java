/*****************************************************************************
 * 
 * QuadrateTarget.java
 * 
 * $Id: RectangleTarget.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes;

import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class RectangleTarget implements GeographicTarget {
    private Rectangle rectangle;
    

    /**
     * Constructor for class <code>QuadrateTarget</code>
     *
     * @param center
     * @param extent
     */
    public RectangleTarget(Position center, Extent extent) {
        this(new Rectangle(center,extent));
        
    }
    
    /**
     * 
     * Constructor for class <code>RectangleTarget</code>
     * This constructs a quadrate target
     * @param center
     * @param diameter
     */

    public RectangleTarget(Position center,double diameter) {
        this(center,new Extent(diameter,diameter));

    }
    /**
     * Constructor for class <code>RectangleTarget</code>
     *
     * @param rectangle
     */
    public RectangleTarget(Rectangle rectangle) {
        this.rectangle=rectangle;

    }
    
    public Position getCenterPosition() {
     
        return rectangle.getCenter();
    }

    public Extent getExtent(){
        return rectangle.getExtent();
        
    }

    public boolean isInside(Position position) {
        
        return rectangle.contains(position);    
        
        
    }

    //
    public int getCodingSize() {
        
        return (8+8+8+8)*8;
    }



    //
    public String toString() {
        return rectangle.toString();
    }
    
    

    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (rectangle != null) {
            result = PRIME * result + rectangle.hashCode();
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

        RectangleTarget other = (RectangleTarget) oth;
        if (this.rectangle == null) {
            if (other.rectangle != null) {
                return false;
            }
        } else {
            if (!this.rectangle.equals(other.rectangle)) {
                return false;
            }
        }

        return true;
    }
    
    //
    public Shape getShape(DeviceID address,Color color, boolean filled) {
     
        return new RectangleShape(rectangle,color,filled);
    }
    
}
