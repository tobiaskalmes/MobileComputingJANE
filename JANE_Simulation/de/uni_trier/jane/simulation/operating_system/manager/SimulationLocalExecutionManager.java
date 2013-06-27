/*****************************************************************************
 * 
 * SimulationLocalExecutionManager.java
 * 
 * $Id: SimulationLocalExecutionManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.operating_system.manager;


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.ContinuousDistribution;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;

import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.kernel.eventset.*;

/**
 * 
 * TODO: comment class  
 * @author daniel
 *
 */
public class SimulationLocalExecutionManager extends LocalExecutionManager {

   
	private EventSet eventSet;
	
	//private double processingTime;

    private DeviceID deviceID;

    private DeviceKnowledge deviceKnowledge;

    private ContinuousDistribution processingTimeDistribution;

    private double processingTime;


	/**
     * 
     * Constructor for class <code>SimulationLocalExecutionManager</code>
     * @param deviceKnowledge
     * @param deviceID
     * @param initializer
     * @param scheduleManager
	 */
	public SimulationLocalExecutionManager(DeviceKnowledge deviceKnowledge, DeviceID deviceID,DefaultSimulationParameters initializer, ActionScheduler scheduleManager) {
	    super(scheduleManager);
		this.eventSet = initializer.getEventSet();
		this.processingTimeDistribution=initializer.getProcessingTimeDistribution();
		if (processingTimeDistribution.getInfimum()<0){
		    throw new IllegalStateException("Processing time must be greater or equal 0");
        }
		this.processingTime = processingTimeDistribution.getNext();
		this.deviceID=deviceID;
		this.deviceKnowledge=deviceKnowledge;
	}
	

    public ServiceContext setCallerContext(ServiceContext executionContext,ServiceContext callerContext) {
    	if (executionContext.getServiceDeviceID().equals(deviceID)){
    		ServiceContext oldContext=this.callerContext;
    		this.callerContext=callerContext;
    		return oldContext;
    	}
    	
    	return	deviceKnowledge.getExecutionMananger(executionContext.getServiceDeviceID())
				.setCallerContext(executionContext,callerContext);
				
    			
    		
    	
    }

    public void schedule(Action action) {
        if (action.getExecutingDeviceID().equals(deviceID)){
            super.schedule(action);
        }else{
            deviceKnowledge.getExecutionMananger(action.getExecutingDeviceID()).schedule(action);
        }
    }
	

	
   
	protected void scheduleProcessingEvent() {
		double time = eventSet.getTime();
		Event event = new ProcessingEvent(time + processingTime);
		eventSet.add(event);
	}
	
	protected void scheduleProcessingEventImmediately() {
		double time = eventSet.getTime();
		Event event = new ProcessingEvent(time );
		eventSet.add(event);
	}

	private class ProcessingEvent extends Event {

		public ProcessingEvent(double time) {
			super(time);
		}

		protected void handleInternal() {
			executeNextAction();
		}

	}



}