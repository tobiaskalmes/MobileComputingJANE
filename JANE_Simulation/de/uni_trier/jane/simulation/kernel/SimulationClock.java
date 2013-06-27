/*****************************************************************************
 * 
 * SimulationClock.java
 * 
 * $Id: SimulationClock.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;

/**
 * The simulation clock provides access to the simulation time 
 * of the event set used in the simulation. This way objects that
 * don't need direct access to the event set can still access the
 * simulation time.
 */
public class SimulationClock implements Clock {

	private final static String VERSION = "$Id: SimulationClock.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private EventSet eventSet;

	/**
	 * Constructs a new simulation clock for the given event set.
	 * @param eventSet the event set to use
	 */
	public SimulationClock(EventSet eventSet) {
		this.eventSet = eventSet;
	}

	/**
	 * Returns the simulation time of the event set of this simulation clock.
	 * @return the simulation time
	 */
	public double getTime() {
		return eventSet.getTime();
	}
	
	//
    public void setTime(double time) {
        throw new IllegalAccessError("set time on simuleation clock not allowed");

    }
}
