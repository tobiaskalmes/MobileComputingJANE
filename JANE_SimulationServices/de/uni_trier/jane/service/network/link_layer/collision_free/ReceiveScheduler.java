 /*****************************************************************************
 * 
 * ReceiveSchedulerImpl.java
 * 
 * $Id: ReceiveScheduler.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer.collision_free;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.service.*;




/**
 * The default ReceiveScheduler implementation. 
 */
public class ReceiveScheduler  {
	
	//private final static String VERSION = "$Id: ReceiveScheduler.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $";


	private ReceiveListener receiveListener;
	private InternalReceiveListener internalReceiveListener;	
	private HashMap senderEventMap;


    protected GlobalOperatingSystem operatingSystem;
	
	/**
	 * Constructs a new ReceiveSchedulerImpl for the given event set.
	 * @param operatingSystem
	 */
	public ReceiveScheduler(GlobalOperatingSystem operatingSystem) {
		this.operatingSystem=operatingSystem;
		senderEventMap = new HashMap();
	}
	
	/**
	 * 
	 * @param receiveListener
	 * @param internalReceiveListener
	 */
	public void initialize(ReceiveListener receiveListener, InternalReceiveListener internalReceiveListener) {
		this.receiveListener = receiveListener;
		this.internalReceiveListener = internalReceiveListener;
	}
	
	/**
	 * 
	 * @param delta
	 * @param progress
	 * @param sender
	 */
	public void add(double delta, double progress, DeviceID sender) {
		Entry entry = new Entry(progress, delta);
		senderEventMap.put(sender, entry);

		if (progress==1.0){
			
			ServiceTimeout timeout = new ReceiveTimeout(delta, sender, receiveListener);
			operatingSystem.setTimeout(timeout);
			entry.setTimeout(timeout);
		}

		
	}
	
	/**
	 * 
	 * @param delta
	 * @param progress
	 * @param sender
	 */
	public void changeProgress(double delta, double progress, DeviceID sender) {
		Entry entry=(Entry)senderEventMap.get(sender);
		if (entry!=null){
			if (progress==1.0){
				ServiceTimeout timeout = new ReceiveTimeout( delta, sender, receiveListener);
				operatingSystem.setTimeout(timeout);
				
				entry.setTimeout(timeout);						
			}
			entry.changeProgress(progress,delta);
		}
	}
	
	/**
	 * 
	 * @param sender
	 * @param message
	 * @param handle
	 */
	public void addInternalUnicast(DeviceID sender, LinkLayerMessage message, UnicastCallbackHandler handle) {
		operatingSystem.setTimeout(new InternalUnicastReceiveEvent(0, sender, message, handle, internalReceiveListener));		
	}
    
    /**
     * 
     * TODO Comment method
     * @param sender
     * @param message
     */
    public void addInternalBroadcast(DeviceID sender, LinkLayerMessage message) {
        operatingSystem.setTimeout(new InternalBroadcastReceiveEvent(0, sender, message, internalReceiveListener));       
    }
	
	/**
	 * 
	 * @param sender
	 */
	public void remove(DeviceID sender) {
		Entry entry = (Entry)senderEventMap.remove(sender);
		ServiceTimeout timeout=entry.getTimeout();
		if (timeout!=null){
		
			operatingSystem.removeTimeout(timeout);
		}
	}

	/**
	 * 
	 * @param sender
	 * @return	progress
	 */
	public double getProgress(DeviceID sender) {
		Entry entry = (Entry)senderEventMap.get(sender);
		if(entry == null) {
			return 0.0;
		}
		
		return entry.getProgress();
		
	}
	
	private class Entry {
		private double progress;
		private ServiceTimeout timeout;
		private double startTime;
		private double endTime;
		
		/**
		 * 
		 * @param progress
		 * @param delta
		 */
		public Entry(double progress, double delta) {
			//this.event = event;
			this.progress=progress;
			this.startTime = operatingSystem.getSimulationTime();
			this.endTime = operatingSystem.getSimulationTime() + delta;
		}
		
		/**
		 * Returns the event
		 * @return event
		 */
		public ServiceTimeout getTimeout() {
			return timeout;
		}
		
		/**
		 * sets the event
		 * @param timeout
		 */
		public void setTimeout(ServiceTimeout timeout){
			this.timeout=timeout;
		}
		
		/**
		 * Changes the progress
		 * @param progress
		 * @param delta
		 */
		public void changeProgress(double progress, double delta){
			endTime+=delta;
			this.progress=progress;
		}
		
		/**
		 * gets the progress
		 * @return progress
		 */
		public double getProgress() {
			double delta = endTime-startTime;
			if(delta == 0.0) {
				return 1.0;
			}
			
			return ((operatingSystem.getSimulationTime()- startTime) / delta)*progress;
			
		}
	}

}

