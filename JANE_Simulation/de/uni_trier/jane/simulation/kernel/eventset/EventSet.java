/*****************************************************************************
 * 
 * EventSet.java
 * 
 * $Id: EventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.kernel.eventset;

/**
 * All implementations of FES (future event set) must implement this interface
 * in order to be used in the simulation.
 * 
 * @see de.uni_trier.ubi.appsim.kernel.ListEventSet
 * @see de.uni_trier.ubi.appsim.kernel.CascadeEventSet
 */
public interface EventSet {

	/**
	 * Returns the current simulation time of the EventSet.
	 * @return the current simulation time
	 */
	public double getTime();
	
	/**
	 * Returns the number of events already executed/handled by this EventSet.
	 * @return number of events executed/handled
	 */
	public long getCount();

	/**
	 * Returns the number of events in this EventSet.
	 * @return the number of events
	 */
	public int getSize();
	
	/**
	 * Adds a new event to the event set. Implementations of this method should
	 * throw a runtime exception if the event is in the past, i.e. the time of the
	 * event is smaller than the time of the EventSet.	 
	 * @param event The event to add
	 */	
	public void add(Event event);

	/**
	 * Checks if this EventSet contains unhandled events.
	 * @return true, if unhandled events exist; false, otherwise.
	 */
	public boolean hasNext();

	/**
	 * Handles the first unhandled event in this EventSet and removes it from 
	 * the EventSet. You should call hasNext() before calling this method. 
	 * Implementations should throw a runtime exception if this method is called 
	 * on an empty EventSet.
	 * 
	 */
	public void handleNext();

    
}
