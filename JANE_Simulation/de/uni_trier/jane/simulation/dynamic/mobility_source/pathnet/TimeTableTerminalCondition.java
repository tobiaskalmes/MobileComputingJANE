/*****************************************************************************
 * 
 * TimeTableTerminalCondition.java
 * 
 * $Id: TimeTableTerminalCondition.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.*;
import de.uni_trier.jane.simulation.kernel.*;

/**
 * This class implements the terminal condition by using the timtable information of the PathNet mobility model.
 * This terminal condition can be used to terminate the simualtion after the last device has left the simulation.
 * Before calling <code>reached()</code> the condition must be intitialized witdth the simualtion clock.
 */
public class TimeTableTerminalCondition implements Condition {
	private Clock clock;
	private ArrivalInfo lastArrivalInfo;
	
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.Condition#reached()
	 */
	public boolean reached() {
		
		return lastArrivalInfo!=null&&lastArrivalInfo.getTime()<clock.getTime();
	}
	/**
	 * Sets the simulation clock needed to check the terminal condition
	 * @param clock		the <code>Clock</code>
	 */
	public void setClock(Clock clock) {
		this.clock=clock;
	}
	/**
	 * Is called when the lastArrivalInfo has been passed to the simulator 
	 * @param lastArrivalInfo	the last <code>ArrivalInfo</code>
	 */
	public void setLastArrivalInfo(ArrivalInfo lastArrivalInfo) {
		this.lastArrivalInfo=lastArrivalInfo;
		
	}
}
