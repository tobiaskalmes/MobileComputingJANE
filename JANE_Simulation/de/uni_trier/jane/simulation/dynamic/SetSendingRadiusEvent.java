/*****************************************************************************
 * 
 * SetSendingRadiusEvent.java
 * 
 * $Id: SetSendingRadiusEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
import de.uni_trier.jane.simulation.kernel.*;

/**
 * A SetSendingRadiusEvent occurs when a mobile device should change
 * its sending radius.
 */
public class SetSendingRadiusEvent extends DynamicEvent {

	private final static String VERSION = "$Id: SetSendingRadiusEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private DoubleMapping sendingRadius;
	private DeviceID address;
	private DynamicInterpreter dynamicInterpreter;
	private DynamicEventListener dynamicEventListener;

	/**
	 * Constructs a new SetSendingRadiusEvent.
	 * @param time the simulation time when this event occurs
	 * @param sendingRadius a double mapping defining the sending radius over time
	 * @param address the address of the device
	 * @param dynamicScheduler dynamic scheduler to retrieve the next action from the dynamic source.
	 * @param dynamicEventListener dynamic event listener to notify about this event
	 */
	public SetSendingRadiusEvent(double time, DoubleMapping sendingRadius, DeviceID address, DynamicScheduler dynamicScheduler, DynamicEventListener dynamicEventListener) {
		super(time, dynamicScheduler);
		this.sendingRadius = sendingRadius;
		this.address = address;
		this.dynamicEventListener = dynamicEventListener;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
	 */
	protected void handleInternal() {
		super.handleInternal();
		dynamicEventListener.handleChangeSendingRadius(address, sendingRadius);
	}
}

