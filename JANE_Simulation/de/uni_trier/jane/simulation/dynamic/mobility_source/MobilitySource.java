/*****************************************************************************
 * 
 * MobilitySource.java
 * 
 * $Id: MobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.object.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * Interface for providing information about movements of devices with the
 * constraint that devices do not change their sending radius.
 */
public interface MobilitySource {

	public static final InitializationObjectElement[] INITIALIZATION_OBJECT_ELEMENTS = new InitializationObjectElement[] {
			FixedNodes.MOBILITY_SOURCE_STATIC_UDG_OBJECT,
			FixedNodes.MOBILITY_SOURCE_STATIC_FILE_OBJECT,
			ClickAndPlayMobilitySourceSimple.MOBILITY_SOURCE_CLICK_AND_PLAY_OBJECT,
			RandomMobilitySource.SIMPLE_RANDOM_WAYPOINT_OBJECT,
			RestrictedRandomWaypoint.INITIALIZATION_OBJECT };
	
	public static final InitializationObjectEnumeration INITIALIZATION_OBJECT_ENUMERATION =
		new InitializationObjectEnumeration("mobilitySource", 0, INITIALIZATION_OBJECT_ELEMENTS);

	public static final InitializationObjectList INITIALIZATION_OBJECT_LIST = new InitializationObjectList(
			"mobilitySource", INITIALIZATION_OBJECT_ELEMENTS);
	
	
	/**
	 * Checks whether more enter information is available.
	 * @return true, if more enter information is available; false otherwise.
	 */
	public boolean hasNextEnterInfo();

	/**
	 * Returns the next enter information available.
	 * @return the next enter information available
	 */
	public EnterInfo getNextEnterInfo();

	/**
	 * Checks whether the mobility source has more arrival information for the specified device.
	 * @param address the address of the device
	 * @return true, if more arrival information is available; false, otherwise.
	 */
	public boolean hasNextArrivalInfo(DeviceID address);

	/**
	 * Returns the next arrival information for the device with the specified address.
	 * @param address the address of the device
	 * @return the arrival information for the device
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address);

	/**
	 * Returns the rectangle for this mobility source
	 * @return the rectangle for this mobility source
	 */
	public Rectangle getRectangle();

	/**
	 * Returns the shape for this mobility source.
	 * @return the shape for this mobility source
	 */
	public Shape getShape();

	/**
	 * Returns the total number of devices created by this mobility source.
	 * @return the total number of devices
	 */
	public int getTotalDeviceCount();
	
	/**
	 * Contains all the information about a device that arrives at some place.
	 */
	public class ArrivalInfo {
		private Position position;
		private double time;
		private boolean suspend;
		
		/**
		 * Constructs a new ArrivalInfo with the given position and time.
		 * @param position the position where the device arrives
		 * @param time the simulation time when the device arrives
		 */
		public ArrivalInfo(Position position, double time) {
			this.position = position;
			this.time = time;
			suspend=false;
		}
		
		/**
		 * Constructs a new ArrivalInfo with the given position and time.
		 * @param position 	the position where the device arrives
		 * @param time 		the simulation time when the device arrives
		 * @param active  	false, if the device should be suspended after arrival 
		 */
		public ArrivalInfo(Position position, double time, boolean suspend) {
			this.position = position;
			this.time = time;
			this.suspend=suspend;
		}
		
		/**
		 * Returns the position where the device arrives
		 * @return the position where the device arrives
		 */
		public Position getPosition() {
			return position;
		}
		
		/**
		 * Returns the time when the device arrives.
		 * @return the time when the device arrives
		 */
		public double getTime() {
			return time;
		}
		
		/**
		 * @return Returns true if the device should be suspended.
		 */
		public boolean isSuspended() {
			return suspend;
		}
	}

	/**
	 * Contains all the information about a device that enters the simulation.
	 */
	public class EnterInfo {
		private DeviceID address;
		private double sendingRadius;
		private ArrivalInfo arrivalInfo;

		/**
		 * Constructs a new EnterInfo with the given address, sending radius and ArrivalInfo.
		 * @param address the address of the device that enters the simulation
		 * @param sendingRadius the sending radius of the device
		 * @param arrivalInfo the arrival info, that is where the device will enter the simulation
		 */
		public EnterInfo(DeviceID address, double sendingRadius, ArrivalInfo arrivalInfo) {
			this.address = address;
			this.sendingRadius = sendingRadius;
			this.arrivalInfo = arrivalInfo;
		}

		/**
		 * Returns the address of the device
		 * @return the address of the device
		 */
		public DeviceID getAddress() {
			return address;
		}

		/**
		 * Returns the sending radius of the device
		 * @return the sending radius of the device
		 */
		public double getSendingRadius() {
			return sendingRadius;
		}

		/**
		 * Returns the arrival info.
		 * @return the arrival info
		 */
		public ArrivalInfo getArrivalInfo() {
			return arrivalInfo;
		}
	}

    /**
     * @param clock
     * @return
     */
    public Condition getTerminalCondition(Clock clock);

    public double getMinimumTransmissionRange();

    public double getMaximumTransmissionRange();

}

