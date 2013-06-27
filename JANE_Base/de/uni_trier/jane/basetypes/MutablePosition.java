/*****************************************************************************
 * 
 * MutablePosition.java
 * 
 * $Id: MutablePosition.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 * 
 * Copyright (C) 2002 Johannes K. Lehnert
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/
package de.uni_trier.jane.basetypes;

/**
 * This class implements a mutable two dimensional vector. This mutable version
 * of Position should never be used in interfaces or as the result of
 * public methods. Use it to speed-up position calculations in private methods.
 * the <code>min</code>, <code>max</code> and <code>turn</code> methods are
 * 2-dimensional transformations thereby preserving the simulation
 */
public class MutablePosition extends PositionBase {

	private final static String VERSION = "$Id: MutablePosition.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	/**
	 * Construct a new <code>MutablePosition</code> object
	 */
	public MutablePosition() {
		super(.0,.0,.0);
	}
	
	/**
	 * Construct a new <code>MutablePosition</code> object.
	 * @param x the x component
	 * @param y the y component
	 * @param z the z component
	 */
	public MutablePosition(double x, double y, double z) {
		super(x,y,z);
	}

	/**
	 * Construct a new <code>MutablePosition</code> object.
	 * @param pos the x and y component
	 */
	public MutablePosition(PositionBase pos) {
		super(pos.x, pos.y, pos.z);
	}

	/**
	 * set the values of position from another object instance.
	 * @param p
	 */
	public MutablePosition set(PositionBase p) {
		x=p.x;
		y=p.y;
		z=p.z;
		return this;
	}
	
	/**
	 * explicitly set the x,y values
	 * @param x
	 * @param y
	 */
	public MutablePosition set(double x, double y) {
		this.x = x;
		this.y = y;
		this.z =.0;
		return this;
	}
	
	/**
	 * explicitly set the x,y,z values
	 * @param x
	 * @param y
	 * @param z
	 */
	public MutablePosition set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * Get a Position object for this MutablePosition.
	 * @return Position
	 */
	public Position getPosition() {
		return new Position(x, y, z);
	}
	

	/**
	 * Substract the given position.
	 * @param position the other position
	 * @return the result
	 */
	public MutablePosition sub(PositionBase position) {
		x -= position.getX();
		y -= position.getY();
		z -= position.getZ();
		return this;
	}

	/**
	 * Add the given position.
	 * @param position the other position
	 * @return the result
	 */
	public MutablePosition add(PositionBase position) {
		x += position.x;
		y += position.y;
		z += position.z;
		return this;
	}

	
	/**
	 * scale the first two dimensions 
	 * @param x
	 * @param y
	 * @return this
	 */
	public MutablePosition mul(double x, double y) {
		this.x*=x;
		this.y*=y;
		return this;
	}

	/**
	 * scale the x and z part of the vector
	 * @param x
	 * @param z
	 * @return this
	 */
	public MutablePosition mulXZ(double x, double z) {
		this.x*=x;
		this.z*=z;
		return this;
	}

	/**
	 * scale the y and z part of the vector 
	 * @param x
	 * @param y
	 * @return this
	 */
	public MutablePosition mulYZ(double y, double z) {
		this.y*=y;
		this.z*=z;
		return this;
	}
	
	/**
	 * Scale this position.
	 * @param scalar the scalar
	 * @return the result
	 */
	public MutablePosition scale(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
		
	/**
	 * Turn this position by the given angle.
	 * @param angle the angle in degrees
	 * @return Position the turned position
	 */

	public MutablePosition turnZ(double angle) {
	    double length=Math.sqrt(x*x+y*y);        
        if(length!=0) {
			double currentAngle = getAngleZ();
			x = length * Math.cos(Math.toRadians(currentAngle + angle));
			y = length * Math.sin(Math.toRadians(currentAngle + angle));
		}
		return this;
	}
		
	/**
	 * Get the minimum resulting from two positions.
	 * @param position the other position
	 * @return the result
	 */
	public MutablePosition min(PositionBase position) {
		x = Math.min(x, position.x);
		y = Math.min(y, position.y);
		 z = Math.min(z, position.z);
		return this;
	}

	/**
	 * Get the maximum resulting from two positions.
	 * @param position the other position
	 * @return the result
	 */
	public MutablePosition max(PositionBase position) {
		x = Math.max(x, position.x);
		y = Math.max(y, position.y);
        z = Math.max(z, position.z);
		return this;
	}
	
	/**
	 * transform mutable position
	 * @param matrix
	 * @return new transformed position instance
	 */
	public MutablePosition transform(Matrix a)
	{
		double xt,yt;
		double w=1.0;
		xt = a.v[0].x*x + a.v[0].y*y + a.v[0].z*z + a.v[0].w*w;
		yt = a.v[1].x*x + a.v[1].y*y + a.v[1].z*z + a.v[1].w*w;
		z  = a.v[2].x*x + a.v[2].y*y + a.v[2].z*z + a.v[2].w*w;
		//w  = a.v[3].x*x + a.v[3].y*y + a.v[3].z*z + a.v[3].w*w;
		x=xt; y=yt;
		return this;
	}

}
