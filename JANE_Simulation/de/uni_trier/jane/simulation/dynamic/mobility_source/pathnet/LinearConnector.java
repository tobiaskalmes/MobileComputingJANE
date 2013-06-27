/*****************************************************************************
 * 
 * LinearConnector.java
 * 
 * $Id: LinearConnector.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * Use this class to move <code>MobileDevice</code> objects between to locations
 * in a simple linear manner.
 */
public final class LinearConnector implements DeviceMover {

	private Position position;
	
	private String name;
	private Position offset;
	private String locationName1;
	private Position locationPos1;
	private String locationName2;
	private Position locationPos2;


	/**
	 * Construct a <code>LinearConnector<code> object.
	 * @param name the name of this device mover.
	 * @param locationName1 the name of the first connection point.
	 * @param locationName2 the name of the second connection point.
	 * @param locationPos1 the position of the first connection point.
	 * @param locationPos2 the position of the second connection point.
	 */
	public LinearConnector(String name, String locationName1,
		String locationName2, Position locationPos1, Position locationPos2) {
		this.name = name;
		this.locationName1 = locationName1;
		this.locationName2 = locationName2;
		this.locationPos1 = new Position(locationPos1);
		this.locationPos2 = new Position(locationPos2);
	}


	/**
	 * @see DeviceMover#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see DeviceMover#getPosition()
	 */
	public Position getPosition() {
		Position pos = new Position(locationPos1);
		pos=pos.sub(locationPos2);
		pos=pos.scale(1/2);
		pos=pos.add(offset);
		return pos;
	}

	/**
	 * @see DeviceMover#setPosition(Position)
	 */
	public void setPosition(Position pos) {
		Position diff = pos.sub(getPosition());
		offset=offset.add(diff);
	}

	/**
	 * @see DeviceMover#getRectangle()
	 */
	public Rectangle getRectangle() {
		return new Rectangle(locationPos1, locationPos2);
	}

	/**
	 * @see DeviceMover#getLocationNames()
	 */
	public String[] getLocationNames() {
		String[] names = new String[2];
		names[0] = locationName1;
		names[1] = locationName2;
		return names;
	}

	/**
	 * @see DeviceMover#getLocationPosition(String)
	 */
	public Position getLocationPosition(String name)
		throws UnknownLocationException {
		if(name.equals(locationName1)) {
			return new Position(locationPos1);
		}
		else if(name.equals(locationName2)) {
			return new Position(locationPos2);
		}
		else {
			throw new UnknownLocationException("the location '" + name +
			"' does not exist.");
		}
	}

	/**
	 * @see DeviceMover#getMinDistance(String, String)
	 */
	public double getMinDistance(String l1, String l2)
			throws UnknownLocationException {
		Position pos1 = getLocationPosition(l1);
		Position pos2 = getLocationPosition(l2);
		
		return pos1.sub(pos2).length();
	}
	
	/**
	 * @see DeviceMover#createPath(DevicePath, String, String)
	 */
	public void createPath(DevicePath devicePath, String start, String finish)
		throws UnknownLocationException {
		
		if(start.equals(locationName1) && finish.equals(locationName2)) {
			devicePath.addNextHop(locationPos2);
		}
		else if(start.equals(locationName2) && finish.equals(locationName1)) {
			devicePath.addNextHop(locationPos1);
		}
		else if(start.equals(finish) && start.equals(locationName1)) {

			devicePath.addNextHop(locationPos1);
		}
		else if(start.equals(finish) && start.equals(locationName2)) {
			devicePath.addNextHop(locationPos2);
		}
		else {
			throw new UnknownLocationException(
				"one of the given location does not exist.");
		}
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.DeviceMover#getShape()
	 */
	public Shape getShape() {
		
		
		return new LineShape(locationPos1,locationPos2,Color.BLUE);
	}	
}
