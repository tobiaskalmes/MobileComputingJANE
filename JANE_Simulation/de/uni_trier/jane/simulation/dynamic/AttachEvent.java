/*****************************************************************************
 * 
 * AttachEvent.java
 * 
 * $Id: AttachEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * An attach event occurs when a device can reach another device. 
 * An attach event does not necessarily mean that a bidirectional link exists between
 * both devices since they may have different sending radii.
 */
public class AttachEvent extends DynamicEvent {

	private final static String VERSION = "$Id: AttachEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private DeviceID sender;
	private DeviceID receiver;
	private DoubleMappingInterval linkReliability;
	private DynamicEventListener dynamicEventListener;

	/**
	 * Constructs a new AttachEvent for the specified devices.
	 * @param time simulation time when the event should happen
	 * @param dynamicScheduler dynamic scheduler to retrieve the next action from the dynamic source.	 
	 * @param sender the address of the sending device
	 * @param receiver the address of the receiving device
	 * @param linkReliability a double mapping defining the link reliability over time
	 * @param dynamicEventListener dynamic event listener to notify about this event
	 */
	public AttachEvent(double time, DynamicScheduler dynamicScheduler, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability, DynamicEventListener dynamicEventListener) {
		super(time, dynamicScheduler);
		this.sender = sender;
		this.receiver = receiver;
		this.linkReliability = linkReliability;
		this.dynamicEventListener = dynamicEventListener;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
	 */
	protected void handleInternal() {
		super.handleInternal();
		dynamicEventListener.handleAttach(sender, receiver, linkReliability);
	}

}

