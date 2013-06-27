/*****************************************************************************
 * 
 * DefaultDynamicScheduler.java
 * 
 * $Id: DefaultDynamicScheduler.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.simulation.dynamic.*;

/**
 * Default implementation of the DynamicScheduler interface.
 */
public class DefaultDynamicScheduler implements DynamicScheduler {

	private final static String VERSION = "$Id: DefaultDynamicScheduler.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private DynamicInterpreter dynamicInterpreter;
	private DynamicSource dynamicSource;

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicScheduler#initialize(DynamicInterpreter, DynamicSource)
	 */
	public void initialize(DynamicInterpreter dynamicInterpreter, DynamicSource dynamicSource) {
		this.dynamicInterpreter = dynamicInterpreter;
		this.dynamicSource = dynamicSource;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicScheduler#scheduleNextEvent()
	 */
	public void scheduleNextEvent() {
		if(dynamicSource.hasNext()) {
			dynamicSource.next().execute(dynamicInterpreter);
		}
	}

}

