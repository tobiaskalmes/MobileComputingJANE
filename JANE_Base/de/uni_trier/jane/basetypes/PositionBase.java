/*****************************************************************************
 * 
 * PositionBase.java
 * 
 * $Id: PositionBase.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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

import java.io.*;

/**
 * This abstract base class provides common methods for mutable and immutable 
 * two dimensional vectors.
 */
public abstract class PositionBase implements Serializable, Comparable {
	
	public final static String VERSION = "$Id: PositionBase.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";
	
	protected double x;
	protected double y;
	protected double z;
	
	/**
	 * Construct a new two dimensional vector with the given values.
	 * @param x the x component
	 * @param y the y component
	 */
	protected PositionBase(double x, double y) {
	    this(x,y,0);
	}
	
	/**
	 * Construct a new two dimensional vector with the given values.
	 * @param x the x component
	 * @param y the y component
	 * @param z the z component
	 */
	protected PositionBase(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Calulate the distance between two vectors.
	 * @param position the other vector
	 * @return the distance
	 */
	public double distance(PositionBase position) {
		double dx = x-position.x;
		double dy = y-position.y;
        double dz = z-position.z;
        
		return Math.sqrt(dx*dx + dy*dy+ dz*dz);
	}
	/**
	 * Calulate the distance between the x components.
	 * @param position the other vector
	 * @return the distance
	 */
	public double distanceX(PositionBase position) {
		return Math.abs(x-position.x);
	}
	/**
	 * Calulate the distance between the y components.
	 * @param position the other vector
	 * @return the distance
	 */
	public double distanceY(PositionBase position) {
		return Math.abs(y-position.y);
	}

	/**
	 * Get the x component.
	 * @return the x component
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get the y component.
	 * @return the y component
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get the z component.
	 * @return the z component
	 */
	public double getZ() {
		return z;
	}


	/**
	 * Multiplicates the given position.
	 * @param position the other position
	 * @return the result
	 */
	public double mult(PositionBase position) {
		return (x*position.x+y*position.y+z*position.z);
	}

	/**
	 * Calculate the cross product.
	 * @param position the other position
	 * @return the result
	 */
	public double determinant2D(PositionBase position) {
		return x * position.y - position.x * y;
	}

	/**
	 * Returns the angle of this position.
	 * @return the angle in degrees
	 */
	public double getAngleZ() {
		double length = Math.sqrt(x*x+y*y);
		if(length == 0.0) {
			throw new IllegalStateException("A position with length 0 has no angle.");
		}
		double alpha = Math.toDegrees(Math.asin(Math.abs(getY()) / length));
		if(getX() >= 0) {
			if(getY() >= 0) {
				return 0 + alpha;
			}
			return 360 - alpha;
			
		}
		if(getY() >= 0) {
			return 180 - alpha;
		}
		return 180 + alpha;
		
		
	}

	/**
	 * Get the length of this vector.
	 * @return the length
	 */
	public double length() {
		return Math.sqrt(x*x + y*y+ z*z);
	}

	/**
	 * get the scalar product of itself
	 * @return length with no squareroot
	 */
	public double lengthnosqrt() {
		return x*x + y*y+ z*z;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + x + "," + y + ","+ z + ")";
	}


	
	/**
	 * Compares two Positions.
	 * returns 1 when this position is greater than other
	 * returns -1 when this position is less than other
	 * returns 0 when both are equal
	 * @param other
	 * @return 1,-1 or 0 
	 */
	public int compare(Position other){
		if (x>other.x) return 1;
		if (x<other.x) return -1;
		if (y>other.y) return 1;
		if (y<other.y) return -1;
        if (z>other.z) return 1;
        if (y<other.z) return -1;
        return 0; 
	}

	public int compareTo(Object o) {
		return compare((Position)o);
	}

    public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            long temp = Double.doubleToLongBits(x);
            result = PRIME * result + (int) (temp >>> 32);
            result = PRIME * result + (int) (temp & 0xFFFFFFFF);
            temp = Double.doubleToLongBits(y);
            result = PRIME * result + (int) (temp >>> 32);
            result = PRIME * result + (int) (temp & 0xFFFFFFFF);
            temp = Double.doubleToLongBits(z);
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
    
            PositionBase other = (PositionBase) oth;
    
            if (this.x != other.x) {
                return false;
            }
    
            if (this.y != other.y) {
                return false;
            }
    
            if (this.z != other.z) {
                return false;
            }
    
            return true;
        }
        
    public int getCodingSize(){
        return 8*8*2;
    }

	
}
