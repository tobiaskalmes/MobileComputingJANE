/*****************************************************************************
 * 
 * AttachAction.java
 * 
 * $Id: AttachAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * An attach action is emitted from a dynamic source when a device obtains the
 * link to another device.
 */
public class AttachAction implements Action {

	private final static String VERSION = "$Id: AttachAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private double time;
	private DeviceID source;
	private DeviceID destination;
	private DoubleMappingInterval reliability;
	
	/**
	 * Constructs a new AttachAction.
	 * @param time the simulation time when the action occurs
	 * @param source the address of the sending device
	 * @param destination the address of the receiving device
	 * @param reliability a double mapping defining the reliability of the link between the devices over time
	 */
	public AttachAction(double time, DeviceID source, DeviceID destination, DoubleMappingInterval reliability) {
		this.time = time;
		this.source = source;
		this.destination = destination;
		this.reliability = reliability;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.Action#execute(DynamicInterpreter)
	 */
	public void execute(DynamicInterpreter dynamicManager) {
		dynamicManager.attach(time, source, destination, reliability);
	}

}

