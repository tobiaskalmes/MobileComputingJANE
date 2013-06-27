/*****************************************************************************
 * 
 * GlobalShutdownAnnouncer.java
 * 
 * $Id: 
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
package de.uni_trier.jane.simulation.kernel;

import java.util.*;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;



/**
 * Anounces SimulationShutdown events to all registered <code>ShutdownListeners</code>
 * The Simulation must call shutdown at termination time.
 */
public class SimulationShutdownAnnouncer implements ShutdownListener, ShutdownAnnouncer {

	private final static String VERSION = "$Id:";
	private ArrayList shutdownListeners;
    private EventSet eventSet;
    private boolean shuttingDown;

	/**
	 * The constructor for <code>GlobalShutdownAnnouncer</code>
	 */
	public SimulationShutdownAnnouncer() {
		shutdownListeners=new ArrayList();
	
	}
	
	
	public void shutdown() {
	    if (!shuttingDown){
	        shuttingDown=true;
            shutdownInternal();
//	        eventSet.add(new Event(0) {
//	            
//                public double getTime() {
//                 
//                    return eventSet.getTime();
//                }
//                protected void handleInternal() {
//                    shutdownInternal();
//
//               }
//
//               
//            });
	    }
		

	}
	 protected void shutdownInternal() {
	    Iterator iterator=shutdownListeners.iterator();
		while (iterator.hasNext()){
			((ShutdownListener)iterator.next()).shutdown();
		}
			
         
     }
	
	/**
	 * Adds a shutdown listener to the shutdown announcer
	 * @param shutdownListener	the listener to add
	 */
	public void addShutdownListener(ShutdownListener shutdownListener){
		if (shutdownListener!=null){
			shutdownListeners.add(0,shutdownListener);
		}
	}
	
	/**
	 * Removes a shutdown listener from the shutdown announcer
	 * @param shutdownListener	the listener to remove
	 */
	public void removeShutdownListener(ShutdownListener shutdownListener){
		shutdownListeners.remove(shutdownListener);
	}


    /**
     * TODO Comment method
     * @param eventSet
     */
    public void init(EventSet eventSet) {
        this.eventSet=eventSet;
        
    }
	

}
