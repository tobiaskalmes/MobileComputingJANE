/*****************************************************************************
 * 
 * Trajectory.java
 * 
 * $Id: Trajectory.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.io.*;





/**
 * A trajectory describes the current position and the relative position of a
 * device in one second.
 */
public class Trajectory implements Serializable{

	private final static String VERSION = "$Id: Trajectory.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Position position;
	private Position direction;

	/**
	 * Construct a new <code>Trajectory</code> object.
	 * @param position the current position  of the device
	 * @param direction the relative position of the device in one second
	 */
	public Trajectory(Position position, Position direction) {
		this.position = position;
		this.direction = direction;
	}

	/**
	 * Get the current position.
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Get the relative position in one second.
	 * @return the direction
	 */
	public Position getDirection() {
		return direction;
	}

}
