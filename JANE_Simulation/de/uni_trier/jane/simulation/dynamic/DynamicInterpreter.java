/*****************************************************************************
 * 
 * DynamicInterpreter.java
 * 
 * $Id: DynamicInterpreter.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic;

import de.uni_trier.jane.basetypes.*;

/**
 * DynamicInterpreters are used to translate between actions from dynamic sources and
 * events in the simulation. Whenever the execute method of an action is called it gets 
 * a DynamicInterpreter as a parameter and calls the corresponding method in the 
 * DynamicInterpreter.
 * 
 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource
 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicScheduler
 * @see de.uni_trier.ubi.appsim.kernel.DefaultDynamicInterpreter
 */
public interface DynamicInterpreter {

	/**
	 * Called when an action causes a device to enter the simulation.
	 * @param time time when the device enters the simulation
	 * @param address address of the device
	 * @param trajectoryMapping trajectory mapping that defines the movement of the device over the time
	 * @param suspended
	 * @param sendingRadius double mapping defining the sending radius of the device over the time. The
	 *                      sending radius is defined in meters.
	 */
	public void enter(double time, DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended, DoubleMapping sendingRadius);

	/**
	 * Called when an action causes a device to exit the simulation.
	 * @param time time when the device exits the simulation
	 * @param address address of the device
	 */
	public void exit(double time, DeviceID address);
	
	/**
	 * Called when an action causes a device (sender) to be attached with another device (receiver), i.e. 
	 * when the device can reach the other device over the wireless network. 
	 * @param time time when the device will be able to reach the other device
	 * @param sender address of the sending device
	 * @param receiver address of the receiving device
	 * @param linkReliability double mapping defining the link reliability for this link (from 0..1) over the time
	 */
	public void attach(double time, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability);

	/**
	 * Called when an action causes a device (sender) to be detached from another device (receiver), i.e. 
	 * when the device cannot reach the other device over the wireless network anymore.
	 * @param time time when the device will be detached from the other device
	 * @param sender address of the sending device
	 * @param receiver address of the receiving device
	 */
	public void detach(double time, DeviceID sender, DeviceID receiver);

	/**
	 * Called when an action causes the movement of a device to change.
	 * @param time time when the device should change its movement
	 * @param address address of the device
	 * @param trajectoryMapping trajectory mapping defining the new movement of the device over the time
	 * @param suspended
	 */
	public void setTrack(double time, DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended);

	/**
	 * Called when an action causes a change of the link reliability of a link between the device
	 * (sender) and another device (receiver).
	 * @param time time when the change in the link reliability should happen
	 * @param sender address of the sender
	 * @param receiver address of the receiver
	 * @param linkReliability double mapping defining the new link reliability over the time (from 0..1)
	 */
	public void setLinkReliability(double time, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability);

	/**
	 * Called when an action causes a change of the sending radius of a device.
	 * @param time time when the change of the sending radius should happen
	 * @param address address of the device
	 * @param sendingRadius double mapping defining the new sending radius over the time. The sending radius is 
	 *                      defined in meters.
	 */
	public void setSendingRadius(double time, DeviceID address, DoubleMapping sendingRadius);

}
