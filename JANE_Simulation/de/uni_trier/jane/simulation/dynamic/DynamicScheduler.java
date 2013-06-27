/*****************************************************************************
 * 
 * DynamicScheduler.java
 * 
 * $Id: DynamicScheduler.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

/**
 * DynamicScheduler is used to coordinate a dynamic interpreter and a dynamic source.
 * 
 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter
 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource
 */
public interface DynamicScheduler {

	/**
	 * Initialize the dynamic scheduler with the given dynamic interpreter and source.
	 * @param dynamicInterpreter dynamic interpreter to use
	 * @param dynamicSource dynamic source to use
	 */
	public void initialize(DynamicInterpreter dynamicInterpreter, DynamicSource dynamicSource);

	/**
	 * Get the next action from the dynamic source and handle it. This normally results in 
	 * a new event being added to the EventSet of the simulation.
	 */
	public void scheduleNextEvent();

}

