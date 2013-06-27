/*****************************************************************************
 * 
 * TimetableMoveEventProvider.java
 * 
 * $Id: TimetableMoveEventProvider.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable; 

import java.util.*;

import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable.DeviceTimeTable.*;
import de.uni_trier.jane.simulation.gui.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class TimetableMoveEventProvider implements MoveEventProvider {
	private boolean hasEnterInfo;
	private TreeMap mainMoveEvents;
    private DeviceTarget currentTarget;
    private double lessonFinishTime;
    private String name;
    
    /**
     * Constructor for class <code>TimetableMoveEventProvider</code>
     * 
     */
    public TimetableMoveEventProvider(String name) {
        mainMoveEvents=new TreeMap();
        this.name=name;
    }
    
	/**
	 * Sets the enter event for this device
	 * @param placeName		the name of the enter location
	 * @param enterTime		the time of the enter event
	 * @throws InvalidTimeTableException	if the device alreadz has an enter info
	 */
	public void setEnterEvent(String placeName, double enterTime)throws InvalidTimeTableException {
		if (hasEnterInfo) throw new InvalidTimeTableException("Device has already an Enter Info");
		this.lessonFinishTime=enterTime;
		currentTarget=new DeviceTarget(placeName,false);
		hasEnterInfo=true;
	}

	/**
     * @return Returns the currentTarget.
     */
    public DeviceTarget getNextTarget() {
        DeviceTarget nextTarget=currentTarget;
        Double nextLessonFinishTime=(Double)mainMoveEvents.firstKey();
        lessonFinishTime=nextLessonFinishTime.doubleValue();
        currentTarget=(DeviceTarget) mainMoveEvents.remove(nextLessonFinishTime);
        return nextTarget;
    }
    
    public boolean hasNextTarget(){
        return currentTarget!=null;
    }
	/**
	 * Sets a timetable event. The device has start moving to  location with name placeName at the given time 
	 * @param placeName			the name of the target location
	 * @param startTime			the beginning of the movement 
	 * @throws InvalidTimeTableException
	 */
	public void setMainMoveEvent(String placeName, double startTime) throws InvalidTimeTableException {
		setMainMoveEvent(placeName,startTime,false);
		
	}
	
	
	private void setMainMoveEvent(String placeName, double startTime,boolean suspend) throws InvalidTimeTableException {
		if (!hasEnterInfo) throw new IllegalStateException("Create enter info first!");
		if (startTime<lessonFinishTime) throw new InvalidTimeTableException("First Event before Enter");
		mainMoveEvents.put(new Double(startTime),new DeviceTarget(placeName,suspend));
	}
	
	
	/**
	 * Sets a timetable event. Same as <code>setMainMoveEvent()</code> but the device is suspended after reaching the target location.
	 * @param placeName		the name of the target location
	 * @param startTime		the beginning of the movement
	 * @throws InvalidTimeTableException
	 */
	public void setSuspendMoveEvent(String placeName, double startTime) throws InvalidTimeTableException {
		setMainMoveEvent(placeName,startTime,true);
		
		
	}

    public double lastPathFinished(double currentTime) {
        if (currentTime>lessonFinishTime){
		    //oops - tight timetable planning... fixing
			System.out.println("Device "+name+" arrived " +(currentTime-lessonFinishTime) +" seconds to late at Target " + currentTarget +" ("+currentTime+")");
			return currentTime;
		}
		return lessonFinishTime;
		
        
    }

    public boolean hasNewTarget() {
        return true;
    }

}
