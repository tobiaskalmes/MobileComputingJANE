/*****************************************************************************
 * 
 * EllipseTarget.java
 * 
 * $Id: EllipseTarget.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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



package de.uni_trier.jane.basetypes;

import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * 
 * @author goergen
 *
 *This class represents a Target region with a given center position and a radius
 * 
 */
public class EllipseTarget implements GeographicTarget{

/**
 * The center position of the target
 */
	private Position position;
	/**
	 * the region radius
	 */
	private double diameter;

/**
 * Constructor for Target 
 * @param position		the targets center
 * @param diameter		the targets region diameter
 */
	public EllipseTarget(Position position, double diameter) {
		this.position = position;
		this.diameter = diameter;
	}

	/**
	 * Copy Constructor for Target.
	 * @param target	the target to copy
	 */
	public EllipseTarget(EllipseTarget target) {
		position=target.position;
		diameter=target.diameter;
	}


/**
 * Returns the targets center position
 * @return	the center position
 */
	public Position getPosition() {
		return position;
	}
    
    public double getDiameter() {
        return diameter;
    }

///**
// * Returns the targets region radius
// * @return	the targets radius
// */
//	public double getRadius() {
//		return radius;
//	}

/**
 * Testes whether the given position is inside this target or not.  
 * @param position	the <code>Position</code> to test
 * @return	true if the position is inside the <code>Target</code>
 */
	public boolean isInside(Position position) {
		return this.position.distance(position)<diameter/2;
	}
	
	public String toString(){
		return position+"("+diameter+")";
	}
	


	public  int getCodingSize() {
		
		return (8+8+8)*8;
	}
	
	
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (position != null) {
            result = PRIME * result + position.hashCode();
        }
        long temp = Double.doubleToLongBits(diameter);
        result = PRIME * result + (int) (temp >>> 32);
        result = PRIME * result + (int) (temp & 0xFFFFFFFF);

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

        EllipseTarget other = (EllipseTarget) oth;
        if (this.position == null) {
            if (other.position != null) {
                return false;
            }
        } else {
            if (!this.position.equals(other.position)) {
                return false;
            }
        }

        if (this.diameter != other.diameter) {
            return false;
        }

        return true;
    }
    
    //
    public Shape getShape(DeviceID address, Color color, boolean filled) {
     
        return new EllipseShape(position, new Extent(diameter,diameter),color,filled);
    }

    public Position getCenterPosition() {
        return position;
    }
}

