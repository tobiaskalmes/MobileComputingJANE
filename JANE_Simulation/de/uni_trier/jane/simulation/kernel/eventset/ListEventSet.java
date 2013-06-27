/*****************************************************************************
 * 
 * ListEventSet.java
 * 
 * $Id: ListEventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

import java.util.*;

/**
 * Simple FES implementation backed by a linked list. Do not use this EventSet
 * for simulations since its performance is quite bad compared to the 
 * CascadeEventSet.
 * 
 * @see de.uni_trier.ubi.appsim.kernel.CascadeEventSet
 */
public class ListEventSet implements EventSet {

	private final static String VERSION = "$Id: ListEventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private LinkedList eventList;
	private double time;
	private long count;
	
	/**
	 * Constructs a new ListEventSet.
	 */
	public ListEventSet() {
		eventList = new LinkedList();
	}
	
	/**
	 * Adds a new event to this EventSet.
	 * @param event 	the event to add
	 * @throws IllegalArgumentException if the time of the event is smaller than
	 *         the time of this EventSet.
	 */
	public void add(Event event) {
		if (event.getTime() < time) {
			throw new IllegalArgumentException("an event can't be scheduled to the past.");
		}

		ListIterator it = eventList.listIterator();
		while (it.hasNext()) {
			Event entry = (Event) it.next();
			if (event.getTime() < entry.getTime()) {
				it.previous();
				it.add(event);
				return;
			}
		}
        
      
		eventList.add(event);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#hasNext()
	 */
	public boolean hasNext() {
		return !eventList.isEmpty();
	}

	/**
	 * handles the first unhandled event. 
	 * @throws Error if this method is called on an empty EventSet.
	 */
//	public Event next() {
//		if (!hasNext()) {
//			throw new Error("Can't call next() on empty event set!");
//		}
//		Event event = (Event) eventList.getFirst();
//		eventList.removeFirst();
//		time = event.getTime();
//		count++;
//		return event;
//	}
	public void handleNext() {
		if (!hasNext()) {
			throw new Error("Can't call next() on empty event set!");
		}
		Event event = (Event) eventList.getFirst();
		eventList.removeFirst();
		time = event.getTime();
		count++;
        
//        System.out.println(event.getTime()+":"+event);
//        if (count==1000) System.exit(0);
		event.handle();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getTime()
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getCount()
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getSize()
	 */
	public int getSize() {
		return eventList.size();
	}
}
