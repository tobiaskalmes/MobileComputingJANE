/*****************************************************************************
 * 
 * Event.java
 * 
 * $Id: Event.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * Base class for all events in the EventSet. 
 * The EventSet uses the handle method to execute the event. If the event is not 
 * disabled, the method handleInternal will be called.
 * 
 * @see de.uni_trier.ubi.appsim.kernel.EventSet
 */
public abstract class Event {

	private final static String VERSION = "$Id: Event.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private boolean disabled;
	private double time;
	
	
	/**
	 * Constructs a new event. New events are enabled by default and may be 
	 * disabled by calling the disable() method. Note that disabled events 
	 * cannot be enabled again.
	 * 
	 * @param time the simulation time when this event should be executed.
	 */
	public Event(double time) {
		this.time = time;
		this.disabled = false;
	}
	
	/**
	 * Returns the simulation time when this event will be executed.
	 * @return the simulation time when this event will be executed.
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * Disables this event. A disabled event will do nothing when it is executed.
	 */
	public void disable() {
		disabled = true;
	}
	
	/**
	 * This method should be called by an EventSet only. Do not overwrite this 
	 * method! Implement the handleInternal method instead.
	 */
	public void handle() {
		if (!disabled) {
			handleInternal();
		}
	}
	
	
	/**
	 * Implement this method to define what should happen when this event is executed.
	 * This method will be called when an enabled event is executed.
	 */
	// TODO: braucht man nicht immer! --> Besser zwei Typen: Event und DiscardableEvent, wobei letzterer die funktionalität für disable implementiert
	protected abstract void handleInternal();
}
