/*****************************************************************************
 * 
 * DeviceTimeTable.java
 * 
 * $Id: DeviceTimeTable.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.*;
import de.uni_trier.jane.simulation.gui.*;


/**
 * Implements the timetable for one device.
 * It provides all movement information which is needed for the simulator to move this device. 
 */
public class DeviceTimeTable {

	
	
	//device Parameters

	
	
	

	
	
	//current Location
	private double currentTime;
	private Position currentPosition;
	private DeviceTarget currentTarget;

	//current movement
	private DevicePath currentPath;

	
	//future movements
	
	//private double lessonFinishTime;
	
	
	private boolean lastmove;
    private MoveEventProvider moveEventProvider;
    private MobileDeviceParameter parameter;
	
	

	/**
	 * 
	 * Constructor for class <code>DeviceTimeTable</code>
	 * @param parameter
	 * @param moveEventProvider
	 */
	public DeviceTimeTable(MobileDeviceParameter parameter, MoveEventProvider moveEventProvider) {

		this.parameter=parameter;
		
		this.moveEventProvider=moveEventProvider;
		
	
		currentPath=new DevicePath();		
	}


	
	/**
	 * Returns true until the device leaves the simulation.
	 * @return	true if the device has still movement events left
	 */
	public boolean hasNextArrival() {	
		return (!lastmove);
	}
	
	/**
	 * Returns the next movement step for this device.
	 * @param campus	the campus (PathNet) on which the device has to be moved
	 * @return			the next <code>ArrivalInfo</code> for this device
	 * @throws	UnknownLocationException	when a location name on the devices path is not defined within the PathNet campus. 
	 */
	public ArrivalInfo getNextArrival(Campus campus) throws UnknownLocationException {
	
		if (currentPath.isFinished()){
		    //use the next timetable event
		    
			
			currentTime=moveEventProvider.lastPathFinished(currentTime);
			

			
			if (!moveEventProvider.hasNextTarget()){
			    // now we are finnished. The device leaves the simulation after this Arrival
				lastmove=true;	
				return new ArrivalInfo(currentPosition,currentTime);
			}
			boolean suspend=currentTarget.suspend();
			
			if (moveEventProvider.hasNewTarget()){
			    DeviceTarget nextTarget=moveEventProvider.getNextTarget();//(DeviceTarget)mainMoveEvents.remove(mainMoveEvents.firstKey());
			
			    currentPath=new DevicePath();
			    //	create the path to the next lesson
			    campus.createPath(currentPath,currentTarget.getPlaceName(),nextTarget.getPlaceName());
			    currentTarget=nextTarget;
			    // No path should have only one hop. minimum starting and end hop!
			    if(currentPath.isFinished()) throw new IllegalStateException("corrupted device path");
			}
			//stay at the current target until the lesson is finished, suspend the device if needed
			//The lesson has now started for this device
			return new ArrivalInfo(currentPosition,currentTime,suspend);
		}
		//still on the way to the next lesson
		// return the next hop of the current path
		Position nextHop=currentPath.getNextHop();
		double speed=parameter.getCurrentSpeed();
		
		
		currentTime+=currentPosition.distance(nextHop)/speed;		    
		currentPosition=nextHop;

		return new ArrivalInfo(nextHop,currentTime);

	}
	
	/**
	 * Returns the enter information of this device. The enter location is searched with in the PathNet Campus 
	 * @param campus		the <code>Campus</code> for searching the enter location
	 * @return				the <code>EnterInfo</code>	for this device
	 * @throws UnknownLocationException		if the enter location of the device does not exist in PathNet campus
	 */
	public EnterInfo getEnterInfo(Campus campus) throws UnknownLocationException {

		currentPath=new DevicePath();
		currentTarget=moveEventProvider.getNextTarget();
		currentTime=moveEventProvider.lastPathFinished(currentTime);//currentTarget.getEndTime();
		campus.createPath(currentPath,currentTarget.getPlaceName(),currentTarget.getPlaceName());
		//the path must have at least one hop!
		if(currentPath.isFinished()) throw new IllegalStateException("corrupted device path");

		currentPosition=currentPath.getNextHop();
		
		//lessonFinishTime=((Double)mainMoveEvents.firstKey()).doubleValue();
		DeviceTarget nextTarget=moveEventProvider.getNextTarget();//(DeviceTarget)mainMoveEvents.remove(mainMoveEvents.firstKey());
		//lessonFinishTime=nextTarget.getEndTime();
		currentPath=new DevicePath();
		campus.createPath(currentPath,currentTarget.getPlaceName(),nextTarget.getPlaceName());
		currentTarget=nextTarget;
		if (currentPath.isFinished()){
			return new EnterInfo(parameter.getAddress(),parameter.getPower(),new ArrivalInfo(currentPosition,currentTime,currentTarget.suspend()));
		}
		return new EnterInfo(parameter.getAddress(),parameter.getPower(),new ArrivalInfo(currentPosition,currentTime));
		
	
	}

	
	
}
