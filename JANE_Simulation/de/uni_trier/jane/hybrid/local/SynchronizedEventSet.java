/*****************************************************************************
 * 
 * SynchronizedEventSet.java
 * 
 * $Id: SynchronizedEventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.hybrid.local;

import de.uni_trier.jane.basetypes.SyncObject;
import de.uni_trier.jane.simulation.kernel.eventset.*;

/**
 * This class provides a wrapper with synchronized-only methods for event sets.
 * Use if you want to access the main event set of the simulation
 * concurrently from several threads, e.g. if you want to control a device from
 * an external program.
 */
public class SynchronizedEventSet implements EventSet {

	private EventSet eventSet;
	
	private SyncObject syncObject;
	
	/**
	 * Constructor for SynchronizedEventSet. You must supply the implementation
	 * of EventSet you wish to synchronize.
	 * @param eventSet the EventSet to synchronize
	 */
	public SynchronizedEventSet(EventSet eventSet) {
		this.eventSet = eventSet;
		 syncObject=new SyncObject();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getTime()
	 */
	public double getTime() {
		synchronized(syncObject) {
			return eventSet.getTime();
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getCount()
	 */
	public long getCount() {
		synchronized(syncObject) {
			return eventSet.getCount();
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#getSize()
	 */
	public int getSize() {
		synchronized(syncObject) {
			return eventSet.getSize();
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#add(de.uni_trier.ubi.appsim.kernel.Event)
	 */
	public void add(Event event) {
		synchronized(syncObject) {
			eventSet.add(event);
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#hasNext()
	 */
	public boolean hasNext() {
		synchronized(syncObject) {
			return eventSet.hasNext();
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.EventSet#next()
	 */
	public void handleNext() {
		synchronized(syncObject) {
			//return 
			eventSet.handleNext();
            
			
		}
//		try {
//		    //TODO: implement with semaphore?!?
//            Thread.sleep(0);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
	}

	/**
	 * Returns the synchronisation object on which the EventSet is synchronized
	 * @return Synchronization object
	 */
	public SyncObject getSynchronizeObject() {
		return syncObject;
	}
}
