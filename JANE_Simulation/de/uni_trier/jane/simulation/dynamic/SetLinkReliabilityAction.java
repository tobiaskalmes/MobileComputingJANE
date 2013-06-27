/*****************************************************************************
 * 
 * SetLinkReliabilityAction.java
 * 
 * $Id: SetLinkReliabilityAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * An SetLinkReliabilityAction is emitted from a dynamic source when a link between to 
 * devices changes its reliability.
 */
public class SetLinkReliabilityAction implements Action {

	private final static String VERSION = "$Id: SetLinkReliabilityAction.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private double time;
	private DoubleMappingInterval linkReliability;
	private DeviceID sender;
	private DeviceID receiver;

	/**
	 * Constructs a new SetLinkReliabilityAction.
	 * @param time the simulation time when this action should happen
	 * @param linkReliability a double mapping defining the link reliability
	 * @param sender the address of the sending device
	 * @param receiver the address of the receiving device
	 */
	public SetLinkReliabilityAction(double time, DoubleMappingInterval linkReliability, DeviceID sender, DeviceID receiver) {
		this.time = time;
		this.linkReliability = linkReliability;
		this.sender = sender;
		this.receiver = receiver;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.Action#execute(DynamicInterpreter)
	 */
	public void execute(DynamicInterpreter dynamicInterpreter) {
		dynamicInterpreter.setLinkReliability(time, sender, receiver, linkReliability);
	}

}

