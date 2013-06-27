/*****************************************************************************
 * 
 * Position.java
 * 
 * $Id: Position.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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



/**
 * This class implements a two dimensional vector.
 */
public class Position extends PositionBase {

	private final static String VERSION = "$Id: Position.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	/**
	 * The position (0,0)
	 */
	public static final Position NULL_POSITION = new Position(0, 0);

	/**
	 * Construct a new <code>Position</code> object.
	 * @param x the x component
	 * @param y the y component
	 */
	public Position(double x, double y) {
		super(x, y);
	}

	/**
	 * Construct a new <code>Position</code> object.
	 * @param x the x component
	 * @param y the y component
	 * @param z the z component
	 */
	public Position(double x, double y, double z) {
		super(x,y,z);
	}
	
	/**
	 * Construct a new <code>Position</code> object.
	 * @param pos the x and y component
	 */
	public Position(PositionBase pos) {
		super(pos.x, pos.y, pos.z);
	}

	/**
	 * Substract the given position.
	 * @param position the other position
	 * @return the result
	 */
	public Position sub(Position position) {
        if (Position.NULL_POSITION.equals(position)) return this;
		return new Position(x-position.x, y-position.y, z-position.z);
	}

	/**
	 * Add the given position.
	 * @param position the other position
	 * @return the result
	 */
	public Position add(Position position) {
        if (Position.NULL_POSITION.equals(position)) return this;
        if (Position.NULL_POSITION.equals(this)) return position;
		return new Position(x+position.x, y+position.y,z+position.z);
	}
    
    /**
     * Add the given position.
     * @param x
     * @param y
     * @param z
     * @return the result
     */
    public Position add(double x, double y, double z) {
        
        return new Position(x+this.x, y+this.y,z+this.z);
    }   
    
	
	/**
	 * Scale this position.
	 * @param scalar the scalar
	 * @return the result
	 */
	public Position scale(double scalar) {
		return new Position(x*scalar, y*scalar, z*scalar);
	}

	/**
	 * Get the minimum resulting from two positions.
	 * @param position the other position
	 * @return the result
	 */
	public Position min(Position position) {
		return new Position(Math.min(x, position.x), Math.min(y, position.y), Math.min(z, position.z));
	}

	/**
	 * Get the maximum resulting from two positions.
	 * @param position the other position
	 * @return the result
	 */
	public Position max(Position position) {
		return new Position(Math.max(x, position.x), Math.max(y, position.y),Math.max(z, position.z));
	}

	/**
	 * Turn this position by the given angle.
	 * @param angle the angle in degrees
	 * @return Position the turned position
	 */
	public Position turnZ(double angle) {
        
		double length=Math.sqrt(x*x+y*y);
		
		if(length==0) {
			return this;
		}
		else {
			double currentAngle = getAngleZ();
			return new Position(length * Math.cos(Math.toRadians(currentAngle + angle)), 
                    length * Math.sin(Math.toRadians(currentAngle + angle)),
                    z);
		}
	}

	/**
	 * transform position
	 * @param matrix
	 * @return new transformed instance (Position type)
	 */
	public Position transform(Matrix a)
	{
		double xt,yt,zt;
		double w=1.0;
		xt = a.v[0].x*x + a.v[0].y*y + a.v[0].z*z + a.v[0].w*w;
		yt = a.v[1].x*x + a.v[1].y*y + a.v[1].z*z + a.v[1].w*w;
		zt = a.v[2].x*x + a.v[2].y*y + a.v[2].z*z + a.v[2].w*w;
		//w  = a.v[3].x*x + a.v[3].y*y + a.v[3].z*z + a.v[3].w*w;
		//x=xt; y=yt; z=zt;
		return new Position(xt,yt,zt);
	}


}
