/*****************************************************************************
 * 
 * LectureRoom.java
 * 
 * $Id: LectureRoom.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class models a simple lecture room. Mobile devices are moved from one
 * door inside or from inside to a door. The inside location is named with the
 * given name in the constructor. Doors are named with the name of the inside
 * location plus a suffix ':i', while i ist the number of the door. The
 * distribution of the mobile devices inside the room is uniform.
 */
public final class LectureRoom implements DeviceMover {

	private Position offset;
	private ContinuousDistribution selectDistribution;
	
	private Extent extent;
	private String name;
	private Position[] doorPosition;
	
	private final double ROOM_DEFAULT_HEIGHT = 5.0;
	
	/**
	 * Construct a lecture room object with the given name, extent and door
	 * positions.
	 * @param name 			the name of the lecture room
	 * @param extent 		the extent of the lecture room
	 * @param doorPosition 	the positions of the doors of this lecture room.
	 * @param distributionCreator	the simulation <code>DistributionCreator</code>
	 */
	public LectureRoom(String name, Extent extent, Position[] doorPosition,DistributionCreator distributionCreator) {
		selectDistribution=distributionCreator.getContinuousUniformDistribution(0,1);
		this.name = name;
		this.extent=extent;
		offset=Position.NULL_POSITION;
		if(extent.getWidth()<0 || extent.getHeight()<0) {
			throw new IllegalArgumentException(
				"the extent is not allowed to be negativ.");
		}
		this.doorPosition = new Position[doorPosition.length];
		for(int i=0; i<doorPosition.length; i++) {
			Position pos = doorPosition[i];
			if(pos==null) {
				throw new IllegalArgumentException("the " + i +
					"th door is null.");
			}
			this.doorPosition[i] = new Position(pos);
//			if(pos.getX()<0 || (extent.getWidth()-pos.getX())<-0.1 || pos.getY()<0 ||
//				(extent.getHeight()-pos.getY())<-0.1 ) {
//				throw new IllegalArgumentException("the " + i +
//					"th door is not inside the given cube.");
//			}
			if(Math.abs(pos.getX())>0.1 && Math.abs(pos.getX()-extent.getWidth())>0.1 && 
                    Math.abs(pos.getY())>0.1 && Math.abs(pos.getY()-extent.getHeight())>0.1 ) {
				throw new IllegalArgumentException("the " + i +
					"th door is not on a wall.");
			}
		}
		//arrivedDevices = new ArrayList();
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
		Position pos = new Position(extent.getWidth(),extent.getHeight());
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
		return new Rectangle(offset,new Position(extent.getWidth(),extent.getHeight()).add(offset));
	}

	/**
	 * @see DeviceMover#getLocationNames()
	 */
	public String[] getLocationNames() {
		String[] names = new String[doorPosition.length+1];
		names[0] = name;
		for(int i=1; i<=doorPosition.length; i++) {
			names[i] = name + ":" + i;
		}
		return names;
	}
	
	/**
	 * @see DeviceMover#getLocationPosition(String)
	 */
	public Position getLocationPosition(String name)
		throws UnknownLocationException {
		int i = getIndex(name);
		if(i<0) {
			return getPosition();
		}
		else {
			return new Position(doorPosition[i]);
		}
	}

	/**
	 * @see DeviceMover#getMinDistance(String, String)
	 */
	public double getMinDistance(String l1, String l2)
		throws UnknownLocationException {
		Position pos1 = getLocationPosition(l1);
		Position pos2 = getLocationPosition(l2);
		if(l1.equals(name) || l2.equals(name)) {
			return 0;
		}
		else {
			return pos1.sub(pos2).length();
		}
	}


	/**
	 * 
	 * @param location
	 * @return
	 * @throws UnknownLocationException
	 */
	private Position getPosition(String location)
		throws UnknownLocationException {
		int i = getIndex(location);
		Position result;
		if(i<0) {
			Position pos=new Position(extent.getWidth()*selectDistribution.getNext(),
									extent.getHeight()*selectDistribution.getNext());
			result=pos.add(offset);
		}
		else {
			result = new Position(doorPosition[i]);
			result=result.add(offset);
		}
		return result;
	}
	
	

	private int getIndex(String location) throws UnknownLocationException {
		if(!location.startsWith(name)) {
			throw new UnknownLocationException("the given location name '" +
				location + "' has to be or to begin with '" + name + "'.");
		}
		if(location.equals(name)) {
			return -1;
		}
		else {
			if(location.charAt(name.length())!=':') {
				throw new UnknownLocationException(
					"the separator between the name '" + name +
					"' and the door number is ':'.");
			}
			try {
				int i = Integer.parseInt(location.substring(name.length()+1));
				if(i<1 || i>doorPosition.length) {
					throw new UnknownLocationException(
						"the integer suffix of '" + location +
						"' has to be beween 1 and " + doorPosition.length +
						".");
				}
				return i-1;
			}
			catch(NumberFormatException e) {
				throw new UnknownLocationException(
					"the given location name '" + location +
					"' has to end with an integer.");
			}
		}
	}



	/**
	 * @see de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.DeviceMover#getShape()
	 */
	public Shape getShape() {
		return new RectangularBoxShape(new Position(offset.getX()+extent.getWidth()/2,offset.getY()+extent.getHeight()/2),extent,ROOM_DEFAULT_HEIGHT,Color.ORANGE,false);
		
	}
	
	/**
	 * @see DeviceMover#createPath(DevicePath, String, String)
	 */
	public void createPath(DevicePath devicePath, String start, String finish)
		throws UnknownLocationException {
		int i = getIndex(finish);
		if(i<0) {
			devicePath.addNextHop(getPosition(finish),name);
		}
	}
}

