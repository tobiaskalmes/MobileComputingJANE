/*****************************************************************************
 * 
 * EnterEvent.java
 * 
 * $Id: EnterEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.device;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.kernel.*;

/**
 * This event occurs when a device enters the simulation.
 */
public class EnterEvent extends DynamicEvent {

	private final static String VERSION = "$Id: EnterEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private DynamicEventListener dynamicEventListener;
	private DeviceID address;
	private TrajectoryMapping trajectoryMapping;
	private DoubleMapping sendingRadius;

	private boolean suspended;
	
	/**
	 * Constructs a new EnterEvent.
	 * @param time the simulation time when this enter event should happen
	 * @param dynamicScheduler the dynamic scheduler to use
	 * @param dynamicEventListener the dynamic event listener to use
	 * @param address the address of the device that enters the simulation
	 * @param trajectoryMapping the trajectory mapping of the device
	 * @param suspended
	 * @param sendingRadius the double mapping defining the sending radius of the device over the time
	 */
	public EnterEvent(double time, DynamicScheduler dynamicScheduler, DynamicEventListener dynamicEventListener, DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended, DoubleMapping sendingRadius) {
		super(time, dynamicScheduler);
		this.dynamicEventListener = dynamicEventListener;
		this.address = address;
		this.trajectoryMapping = trajectoryMapping;
		this.suspended=suspended;
		this.sendingRadius = sendingRadius;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
	 */
	protected void handleInternal() {
		super.handleInternal();
		dynamicEventListener.handleEnter(address, trajectoryMapping,suspended, sendingRadius);
	}

}
