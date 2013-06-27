/*****************************************************************************
 * 
 * SetPositionMappingAction.java
 * 
 * $Id: SetPositionMappingAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * An SetPositionMappingAction is emitted from a dynamic source when a device 
 * changes its position mapping.
 */
public class SetPositionMappingAction implements Action {

	private final static String VERSION = "$Id: SetPositionMappingAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private double time;
	private TrajectoryMapping trajectoryMapping;
	private DeviceID address;

	private boolean suspended;

	/**
	 * Create a new SetPositionMappingAction.
	 * @param time 				the simulation time when this action should happen
	 * @param trajectoryMapping the new trajectory mapping for the device
	 * @param suspended			true if the device is suspended
	 * @param address 			the address of the device
	 */
	public SetPositionMappingAction(double time, TrajectoryMapping trajectoryMapping, boolean suspended, DeviceID address) {
		this.time = time;
		this.trajectoryMapping = trajectoryMapping;
		this.address = address;
		this.suspended=suspended;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.Action#execute(DynamicInterpreter)
	 */
	public void execute(DynamicInterpreter dynamicManager) {
		dynamicManager.setTrack(time, address, trajectoryMapping,suspended);
	}

}
