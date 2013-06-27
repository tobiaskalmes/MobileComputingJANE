/*****************************************************************************
 * 
 * DetachAction.java
 * 
 * $Id: DetachAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * An dettach action is emitted from a dynamic source when a device loses the 
 * link to another device.
 */
public class DetachAction implements Action {

	private final static String VERSION = "$Id: DetachAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private double time;
	private DeviceID source;
	private DeviceID destination;

	/**
	 * Constructs a new DetachAction.
	 * @param time the simulation time when this action should happen
	 * @param source the address of the sending device
	 * @param destination the address of the receiving device
	 */
	public DetachAction(double time, DeviceID source, DeviceID destination) {
		this.time = time;
		this.source = source;
		this.destination = destination;
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.Action#execute(DynamicInterpreter)
	 */
	public void execute(DynamicInterpreter dynamicManager) {
		dynamicManager.detach(time, source, destination);
	}

}

