/*****************************************************************************
 * 
 * DynamicEventListener.java
 * 
 * $Id: DynamicEventListener.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.kernel;

import de.uni_trier.jane.basetypes.*;

/**
 * DynamicEventListeners are notified of all relevant events that are executed
 * in the simulation. Implement this interface if you want to be notified of 
 * devices that enter or exit the simulation, obtain or lose links to other devices, 
 * change their tracks or sending radii or if you need information on changes to the 
 * link reliabilities.
 * 
 * @see de.uni_trier.jane.simulation.device.DeviceManager
 */
public interface DynamicEventListener {
	
	/**
	 * Called when a device enters the simulation. 
	 * @param address the address of the device
	 * @param trajectoryMapping the trajectory mapping of the device
	 * @param suspended
	 * @param sendingRadius the double mapping defining the sending radius of the device over time
	 */
	public void handleEnter(DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended, DoubleMapping sendingRadius);

	/**
	 * Called when a device leaves the simulation.
	 * @param address the address of the device
	 */
	public void handleExit(DeviceID address);

	/**
	 * Called when a device obtains a link to another device. 
	 * @param sender the address of the sending device
	 * @param receiver the address of the receiving device
	 * @param linkReliability a double mapping defining the link reliability of the new link over time.
	 */
	public void handleAttach(DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability);

	/**
	 * Called when a device loses a link to another device.
	 * @param sender the address of the sending device
	 * @param receiver the address of the receiving device
	 */
	public void handleDetach(DeviceID sender, DeviceID receiver);

	/**
	 * Called when a device changes its track.
	 * @param address the address of the device
	 * @param trajectoryMapping the new trajectory mapping for the device.
	 * @param suspended
	 */
	public void handleChangeTrack(DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended);

	/**
	 * Called when the mapping for the sending radius of a device changes.
	 * @param address the address of the device
	 * @param sendingRadius the new double mapping defining the sending radius over time.
	 */
	public void handleChangeSendingRadius(DeviceID address, DoubleMapping sendingRadius);

	/**
	 * Called when the link reliability of a link changes
	 * @param sender the address of the sending device
	 * @param receiver the address of the receiving device
	 * @param linkReliability the new double mapping defining the link reliability
	 */
	public void handleChangeLinkReliability(DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability);
}

