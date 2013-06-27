/*****************************************************************************
 * 
 * DeviceMover.java
 * 
 * $Id: DeviceMover.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;



public interface DeviceMover {

	/**
	 * Returns the Rectangle bordering the <code>DeviceMover</code>
	 * @return the Rectangle
	 */
	public Rectangle getRectangle();

	/**
	 * Returns the <code>DeviceMover</code>s name
	 * @return 	the name
	 */
	public String getName();

	/**
	 * Returns the Position of a location which is part of this <code>DeviceMover</code>
	 * @param string
	 * @return	the <code>Position</code> of the location
	 * @throws UnknownLocationException
	 */
	public Position getLocationPosition(String string) throws UnknownLocationException;

	/**
	 * Returns the <code>DeviceMover</code>s position
	 * @return	the <code>Position</code> 
	 */
	public Position getPosition();

	/**
	 * Sets the <code>DeviceMover</code>s position
	 * @param position	the new <code>Position</code>
	 */
	public void setPosition(Position position);
	
	/**
	 * Creates a path from location start to location finish and appends it to the given <code>DevicePath</code>
	 * @param devicePath	the current DevicePath to be extended
	 * @param start			the name of the start location
	 * @param finish		the name of the final location
	 * @throws UnknownLocationException		when a given location is not known
	 */
	public void createPath(DevicePath devicePath, String start, String finish) throws UnknownLocationException;
	


	/**
	 * Returns the name of all locations that are part of this <code>DeviceMover</code>
	 * @return	array of all location names
	 */
	public String[] getLocationNames();
	
	/**
	 * Returns the shape of the <code>DeviceMover</code>
	 * @return	<code>DeviceMover</code>s shape
	 */
	public Shape getShape();

	/**
	 * Calculates the minimum distance between two locations
	 * @param location1
	 * @param location2
	 * @return	the min distance between
	 * @throws UnknownLocationException when a given location is not known
	 */
	public double getMinDistance(String location1, String location2) throws UnknownLocationException;



	
}
