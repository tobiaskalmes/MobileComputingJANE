/*****************************************************************************
 * 
 * DynamicEvent.java
 * 
 * $Id: DynamicEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

import de.uni_trier.jane.simulation.kernel.eventset.*;

/**
 * Base class for all dynamic events, that is, all events that result from 
 * retrieving events from a dynamic scheduler. Each time a dynamic event is 
 * executed, the dynamic scheduler retrieves the next event using the 
 * dynamic source and the dynamic interpreter.
 */
public class DynamicEvent extends Event {

	private final static String VERSION = "$Id: DynamicEvent.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private DynamicScheduler dynamicScheduler;

	/**
	 * Constructs a new DynamicEvent. 
	 * @param time simulation time when this dynamic event should happen
	 * @param dynamicScheduler dynamic scheduler to use to retrieve new events.
	 */
	public DynamicEvent(double time, DynamicScheduler dynamicScheduler) {
		super(time);
		this.dynamicScheduler = dynamicScheduler;
	}
	
	/**
	 * Calls the scheduleNextEvent of the dynamic scheduler of this event. 
	 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
	 */
	protected void handleInternal() {
		dynamicScheduler.scheduleNextEvent();
	}

}
