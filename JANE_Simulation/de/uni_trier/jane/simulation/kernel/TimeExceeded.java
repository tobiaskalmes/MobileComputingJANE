/*****************************************************************************
 * 
 * TimeExceeded.java
 * 
 * $Id: TimeExceeded.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.simulation.kernel.eventset.*;



/**
 * Checks whether the an EventSet has reached a certain simulation time.
 */
public class TimeExceeded implements Condition {

	private final static String VERSION = "$Id: TimeExceeded.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private EventSet eventSet;
	private double time;

	/**
	 * Constructs a new TimeExceeded object for the specified event set and simulation time.
	 * @param eventSet the event set to check
	 * @param time the simulation time when this condition happens.
	 */
	public TimeExceeded(EventSet eventSet, double time) {
		this.eventSet = eventSet;
		this.time = time;
	}

	/**
	 * Checks if the simulation time in the event set is greater than the time of this condition.
	 * @return true, if the time is greater; false, otherwise.
	 */
	public boolean reached() {
		return eventSet.getTime() > time;
	}
}
