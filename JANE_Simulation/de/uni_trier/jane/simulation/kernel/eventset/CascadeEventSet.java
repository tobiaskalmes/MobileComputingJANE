/*****************************************************************************
 * 
 * CascadeEventSet.java
 * 
 * $Id: CascadeEventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * FES implementation using the Cascade algorithm by Norbert M&uuml;ller 
 * and Martin Luckow<br>
 * (Martin Luckow, Norbert Th. M&uuml;ller: Cascade: A Simple and Efficient 
 * Algorithm for Priority Queues. Trierer Forschungsberichte 
 * Mathematik/Informatik 94-15, Universit&auml;t Trier (1994))
 */
public class CascadeEventSet implements EventSet {

	private final static String VERSION = "$Id: CascadeEventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private SubList[] lists;
	private int handledEvents;
	private int elements;
	private double time;
	private int trigger;

	/**
	 * Constructs a new CascadeEventSet.
	 * @param trigger the length of the first sublist
	 * @param maxLists the maximum number of sublists used
	 */
	public CascadeEventSet(int trigger, int maxLists) {
		this.trigger = trigger;
		lists = new SubList[maxLists];
		lists[0] = new SubList(trigger);
		for(int i=1; i<maxLists; i++) {
			lists[i] = new SubList(2*lists[i-1].getMaxLength());
			lists[i-1].setNextList(lists[i]);
		}
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
		return handledEvents;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getSize()
	 */
	public int getSize() {
		return elements;
	}

	/**
	 * Adds a new event to this EventSet. 
	 * @param event		the Event to add
	 * @throws IllegalArgumentException if an event with a smaller time  
	 *         than the time of this EventSet is added.
	 * @throws Error if the maximum number of entries is already reached.
	 */
	public void add(Event event) {
		if(event.getTime() < time) {
			throw new IllegalArgumentException("an event can't be scheduled to the past.");
		}
        if(event.getTime() > Double.MAX_VALUE) {
            throw new IllegalArgumentException("an event end time can not be inifinity");
        }
		elements++;
		lists[0].addEvent(event);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#hasNext()
	 */
	public boolean hasNext() {
		return elements != 0;
	}

	/**
	 * handles the first unhandled event.
	 * @throws Error if the method is called on an empty EventSet
	 */
	//public Event next() {
	public void handleNext(){
		if (elements == 0) {
			throw new Error("CascadeEventSet is empty!");
		}
		double minTime = Double.POSITIVE_INFINITY;
		int index = lists.length+1;
		for(int i=lists.length-1; i>=0; i--) {
			if (lists[i].getLength() != 0) {
				double timeOfFirstElement = lists[i].getFirstElement().getTime();
				if (timeOfFirstElement < minTime) {
					index = i;
					minTime = timeOfFirstElement;	
				}
			}
		} 
		if (index == lists.length+1) {
            //System.err.println("CascadeEventSet empty while elements="+elements);
			throw new Error("CascadeEventSet empty while elements="+elements);
		}
		Event event = lists[index].removeFirstElement();
		time = event.getTime();
		elements--;
		handledEvents++;
		//	return event;
//        if (event.getTime()>517.4871143007605) System.exit(0);
//		System.out.println(event.getTime()+":"+event);
//		 
		event.handle();
	}
	
	
	
	/**
	 * Returns a string representation of all sublists and events for 
	 * debugging purposes.
	 * @return string representation of this EventSet.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for(int i=0; i<lists.length; i++) {
			result.append("Sublist ");
			result.append(i);
			result.append(": ");
			result.append(lists[i]);
			result.append("\n");
		}
		return result.toString();
	}
	
	private class SubList {
		private SubList nextList;
		private LinkedList list;
		private int maxLength;
		
		/**
		 * 
		 * @param maxLength
		 */
		public SubList(int maxLength) {
			this.maxLength = maxLength;
			list = new LinkedList();
		}		
		
		/**
		 * add an Event
		 * @param ev
		 */
		public void addEvent(Event ev) {
			if (list.size() == 0) {
				list.add(ev);
				return;	
			}
			boolean added = false;
			ListIterator iter = list.listIterator();
			while (iter.hasNext()) {
				Event element = (Event) iter.next();
				if (ev.getTime() < element.getTime()) {
					iter.previous();
					iter.add(ev);
					added = true;
					break;
				}
			}
			if (!added) {
				list.add(ev);
			}
			if (list.size() > maxLength) {
				handleOverflow();
			}
		}
		/**
		 * gets the first element
		 * @return	the first event
		 */		
		public Event getFirstElement() {
			return (Event) list.getFirst();
		}
		
		
		/**
		 * removes the first element
		 * @return	the first event
		 */
		public Event removeFirstElement() {
			return (Event) list.removeFirst();
		}
		
		/**
		 * returns the length
		 * @return the length
		 */
		public int getLength() {
			return list.size();
		}
		
		/**
		 * returns the maximum length
		 * @return the maximum length
		 */
		public int getMaxLength() {
			return maxLength;			
		}
		
		/**
		 * 
		 * @param nextList
		 */
		public void setNextList(SubList nextList) {
			this.nextList = nextList;
		}
		
		/**
		 * removes the given event
		 * @param ev
		 * @return true if the list contained the event
		 */
		public boolean remove(Event ev) {
			return list.remove(ev);
		}
		
		private void handleOverflow() {
			SubList cursor = this;
			while(cursor != null && cursor.getLength() > cursor.getMaxLength()) {
				SubList nextList = cursor.nextList;
				if (nextList == null) {
					throw new Error("Cascade overflow!");
				}
				nextList.merge(cursor);
				cursor.list.clear();
				cursor = nextList;
			}
			if (cursor == null && cursor.getLength() > cursor.getMaxLength()) {
				throw new Error("Cascade overflow!");
			}
			
		}	
		
		private void merge(SubList other) {
			if (list.size() == 0) {
				list.addAll(other.list);
			} else {
				ListIterator iter1 = list.listIterator(); // next level
				ListIterator iter2 = other.list.listIterator(); // previous level

				while(iter2.hasNext()) {
					// add these entries at the right places
					Event event2 = (Event) iter2.next();
					boolean added = false;
					while(iter1.hasNext()) {
						Event event1 = (Event) iter1.next();
						if (event2.getTime() < event1.getTime()) {
							// insert event2 before event1
							if (iter1.hasPrevious()) {
								iter1.previous();
								iter1.add(event2);
							} else {
								list.addFirst(event2);								
							}
							iter2.remove();							
							added = true;
							break;
						}						
					}
					if (!added) {
						list.addLast(event2);
						iter2.remove();												
						break;
					}
				}
				if (other.list.size() != 0) {
					list.addAll(other.list);
				}
			}	
		}	
		
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("[");
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Event element = (Event) iter.next();
				result.append(element);
				if (iter.hasNext()) {
					result.append(", ");
				}
			}
			result.append("]");
			return result.toString();
		}
	}
}
