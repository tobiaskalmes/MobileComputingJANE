/*****************************************************************************
 * 
 * PathNetMobilitySource.java
 * 
 * $Id: PathNetMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * The mobility source for the pathnet mobility model.
 * This mobility source use a timetable to move devices on a given pathnet, represented  by  <code>Campus</code>
 * 
 * Devices can be grouped by lecture classes and are moved at random to their next event given by a lecture timetable
 * The application class and the user behavior class can be created by the timetable configuration file and can be initialized 
 * with key/value parameters if the classes implements <code>TimeTableCreateable</code>
 * The location names of the lecture rooms in the timetable description file has to be the same as in the pathnet description files.  
 * 
 * TimeTable example:
 * <TIMETABLE>
 *	<!- Definition of lecture classes >
 *	<CLASS NAME="Semester1">
 *		<MEMBER NAME="Student1" ID="D1" POWER="25" MINSPEED="0.5" MAXSPEED="1.5">
 *			<APPLICATION>
 *				<USERCLASS NAME="name.of.userclass">
 *					<PARAM  NAME="TEXT" VALUE="Hello"/>
 *				</USERCLASS>
 *				<APPCLASS NAME="name.of.applicationclass">	
 *				</APPCLASS>
 * 			</APPLICATION>  
 *		</MEMBER>				
 *		<MEMBER NAME="SA101" ID="D2" POWER="25" MINSPEED="0.5" MAXSPEED="1.5">
 *			<APPLICATION>
 *				<USERCLASS NAME="name.of.userclass">
 *					<PARAM  NAME="TEXT" VALUE="Another Text"/>
 *				</USERCLASS>
 *				<APPCLASS NAME="name.of.applicationclass">	
 *				</APPCLASS>
 *			</APPLICATION>  
 *		</MEMBER>
 *	</CLASS>
 *  <CLASS NAME="Semester2">
 *		<MEMBER NAME="Student3" ID="D3" POWER="25" MINSPEED="0.5" MAXSPEED="1.5">
 *			<APPLICATION>
 *				<USERCLASS NAME="name.of.userclass">
 *					<PARAM  NAME="TEXT" VALUE="My name is Student3"/>
 *				</USERCLASS>
 *				<APPCLASS NAME="name.of.applicationclass">	
 *				</APPCLASS>
 * 			</APPLICATION>  
 *		</MEMBER>
 *	</CLASS>
 *
 *  <!- Definition of lecture timetable
 * <- classes enter the simulation>
 *	<ENTER NAME="BEGINN_ALL" END="10" FUZZ="100">
 *		<PARTICIPANTS CLASS="Semester1"/>
 *		<PARTICIPANTS CLASS="Semester2"/>
 *		<PLACE NAME="LectureRoom1" PROB="0.3"/>
 *		<PLACE NAME="LectureRoom2" PROB="0.3"/>
 *		<PLACE NAME="FOYER" PROB="0.4"/> 		
 * 	</ENTER>
 *	
 *  <!- some lectures>
 *	<EVENT NAME="Lecture1ForSemester1" END="1000" FUZZ="100">
 *		<PARTICIPANTS CLASS="Semester1"/>
 *		<PLACE NAME="LectureRoom1" PROB="1.0"/>
 *	</EVENT>
 * 	<EVENT NAME="Lecture1ForSemester2" END="1000" FUZZ="100">
 *		<PARTICIPANTS CLASS="Semester2"/>
 *		<PLACE NAME="LectureRoom2" PROB="1.0"/>
 *	</EVENT>
 *	<EVENT NAME="Lecture1ForAll" END="2000" FUZZ="100">
 *		<PARTICIPANTS CLASS="Semester1"/>
 *		<PARTICIPANTS CLASS="Semester2"/>
 *		<PLACE NAME="LectureRoom2" PROB="1.0"/>
 *	</EVENT>
 *	<!- ...>
 *  <!-students at home (deactivated radio interface
 * 	<SUSPEND NAME="HOME1" END="5000" FUZZ="100">
 *		<PARTICIPANTS CLASS="Semester1"/>
 *		<PARTICIPANTS CLASS="Semester2"/>
 *		<PLACE NAME="ExitRoom" PROB="1.0"/>
 *	</EVENT>
 *  <! ...>
 *	<!- classses leave the simulation>
 *  <EXIT NAME="EXIT_ALL" END="10000" FUZZ="100">
 *		<PARTICIPANTS CLASS="Semester1"/>
 *		<PARTICIPANTS CLASS="Semester2"/>
 *		<PLACE NAME="LectureRoom1" PROB="0.3"/>
 *		<PLACE NAME="LectureRoom2" PROB="0.3"/>
 *		<PLACE NAME="FOYER" PROB="0.4"/> 		
 * 	</ENTER>
 * </TIMETABLE>
 * 
 * a CLASS contain one or more devices. This group is scheduled together. 
 * A group MEMBER needs a NAME and a unique ID starting with the letter D and a unique number. 
 * Each device must be configured with POWER, containing the sending range of the radio interface,
 * MINSPEED and MAXSPEED, the minimum and maximum device moving speed . The current device speesd 
 * is uniformly distributed between these values.
 * A device can have an application and an user class description. They are loaded when the device 
 * are created by the <code>PathNetMobiltySoure</code> generated <code>ApplicationUserSource</code>.
 * This classes can be initialized by a sequence of key/value parameters.
 * </br>
 * A timetable event defines events at pathnet locations for the given classes at a given time.
 * An event has a unique NAME, a fixed endtime when the devices starts to leave this event and the 
 * endtime variation parameter FUZZ.
 *  
 * Each event has a list of possible target locations for the devices of all given classes. 
 * The target selection is controled by the given propability. The sum of all location propabilities 
 * for an event has to be excatly 1. Finally, each event has a list of participating classes. All the 
 * devices specified by the classes are handled by this event at this time.
 * Timetable events are:
 * ENTER	this event defines when and where the classes can enter the simulation	
 * EVENT	a normal lecture event
 * SUSPEND	as event, but all members of the given classes are suspend at the target location
 * EXIT  	this event defines when and where the classes leaves the simulation
 * 
 * 
 */
public class PathNetMobilitySource implements MobilitySource{
	
	private HashMap addressDeviceTimeTableMap;
	private DistributionCreator distributionCreator;
	private Iterator enterInfoIterator;
	private Campus campus;
	private TimeTable timeTable;
	private TimeTableTerminalCondition terminalCondition;
	
	/**
	 * Constructor for class <code>PathNetMobilitySource</code>
	 * @param timeTableFileName			name of XML file containing the timetable description
	 * @param campus					the pathnet which is used by the timetable description to move devices
	 * @param distributionCreator		the simulation <code>DistributionCreator</code>
	 * @throws InvalidTimeTableException
	 */
	public PathNetMobilitySource(String timeTableFileName, Campus campus, DistributionCreator distributionCreator) throws InvalidTimeTableException{
		this.distributionCreator=distributionCreator;
		this.campus=campus;
		
		timeTable = new TimeTable(timeTableFileName,distributionCreator);
		terminalCondition=new TimeTableTerminalCondition();
		addressDeviceTimeTableMap=timeTable.getDeviceTimeTableMap();
		enterInfoIterator=addressDeviceTimeTableMap.values().iterator();
			
	}
	
	
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		
		return enterInfoIterator.hasNext();
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		DeviceTimeTable deviceTimeTable=(DeviceTimeTable)enterInfoIterator.next();		
		try {
			return deviceTimeTable.getEnterInfo(campus);
		} catch (UnknownLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		return (addressDeviceTimeTableMap.containsKey(address)&&((DeviceTimeTable)addressDeviceTimeTableMap.get(address)).hasNextArrival());
	}
	
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		
		try {
			DeviceTimeTable deviceTimeTable=(DeviceTimeTable)addressDeviceTimeTableMap.get(address);
			ArrivalInfo arrivalInfo=deviceTimeTable.getNextArrival(campus);
			if (!deviceTimeTable.hasNextArrival()){
				addressDeviceTimeTableMap.remove(address);
			}
			if (addressDeviceTimeTableMap.isEmpty()){
				terminalCondition.setLastArrivalInfo(arrivalInfo);
			}
			return arrivalInfo;
		} catch (UnknownLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return campus.getRectangle();
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getShape()
	 */
	public Shape getShape() {
		return campus.getShape();
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return addressDeviceTimeTableMap.size();
	}


	



	/**
	 * Returns the terminal condition for this mobility source to terminate the simulation when no mobility source events are left
	 * @param clock	the simulation clock containing the current simualtion time 
	 * @return the terminal condition for this mobility source
	 */
	public Condition getTerminalCondition(Clock clock) {
		terminalCondition.setClock(clock);
		return terminalCondition;
	}



    /**
     * @return
     */
    public SimulationServiceFactory getServiceFactory() {
        
        return timeTable;
    }

	public double getMinimumTransmissionRange() {
		return timeTable.getMinimumRadius();
	}

	public double getMaximumTransmissionRange() {
		return timeTable.getMaximumRadius();
	}

}
