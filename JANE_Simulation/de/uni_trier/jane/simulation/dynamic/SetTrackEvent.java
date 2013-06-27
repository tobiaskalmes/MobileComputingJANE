/*****************************************************************************
 * 
 * SetTrackEvent.java
 * 
 * $Id: SetTrackEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * A SetTrackEvent occurs when a device should change its track.
 */
public class SetTrackEvent extends DynamicEvent {

	private final static String VERSION = "$Id: SetTrackEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private TrajectoryMapping trajectoryMapping;
	private DeviceID address;
	private DynamicInterpreter dynamicInterpreter;
	private DynamicEventListener dynamicEventListener;

	private boolean suspended;

	/**
	 * Constructs a new SetTrackEvent.
	 * @param time the simulation time when this event occurs
	 * @param trajectoryMapping the new trajectory mapping of the device
	 * @param suspended
	 * @param address the address of the device 
	 * @param dynamicScheduler dynamic scheduler to retrieve the next action from the dynamic source.
	 * @param dynamicEventListener dynamic event listener to notify about this event
	 */
	public SetTrackEvent(double time, TrajectoryMapping trajectoryMapping, boolean suspended, DeviceID address, DynamicScheduler dynamicScheduler, DynamicEventListener dynamicEventListener) {
		super(time, dynamicScheduler);
		this.trajectoryMapping = trajectoryMapping;
		this.suspended=suspended;
		this.address = address;
		this.dynamicEventListener = dynamicEventListener;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
	 */
	protected void handleInternal() {
		super.handleInternal();
		dynamicEventListener.handleChangeTrack(address, trajectoryMapping,suspended);
	}
}
