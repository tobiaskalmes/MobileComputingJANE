/*****************************************************************************
 * 
 * LocalExecutionManager.java
 * 
 * $Id: LocalExecutionManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;

/**
 * 
 * @author goergen
 *
 * TODO comment class
 */
public abstract class LocalExecutionManager implements ExecutionManager {

    /**
     * @author goergen
     *
     * TODO comment class
     */
    private static final class FinishOperator {
        private FinishManager  finishManager;
        private Map openFinishSet;
        private Map openServiceIDActionsMap;
        
        
        
        
        /**
         * Constructor for class <code>FinishOperator</code>
         * @param simulationServiceManager
         */
        public FinishOperator(FinishManager finishManager) {
            this.finishManager=finishManager;
            
            openServiceIDActionsMap=new HashMap();
            openFinishSet=new HashMap();
        }
        /**
         * TODO Comment method
         * @param serviceID
         * @param callerContext
         */
        public void startFinish(ServiceID serviceID, ServiceContext callerContext) {
            openFinishSet.put(serviceID,callerContext);
           
            
        }

        /**
         * TODO Comment method
         * @param serviceID
         */
        public void endFinish(ServiceID serviceID) {
            ServiceContext callerContext=(ServiceContext)openFinishSet.remove(serviceID);
            if (!openServiceIDActionsMap.containsKey(serviceID)){
                finishManager.finishComplete(serviceID,callerContext);
            }
            
            
        }

        /**
         * TODO Comment method
         * @param action
         */
        public void scheduleAction(Action action) {
            if (openFinishSet.containsKey(action.getExecutingServiceID())){
                Set set=(Set)openServiceIDActionsMap.get(action.getExecutingServiceID());
                if (set==null){
                    set=new LinkedHashSet();
                    openServiceIDActionsMap.put(action.getExecutingServiceID(),set);
                }
                set.add(action);
            }
            
        }

        /**
         * TODO Comment method
         * @param action
         */
        public void executeAction(Action action) {
            if (openServiceIDActionsMap.containsKey(action.getExecutingServiceID())){
                Set set=(Set)openServiceIDActionsMap.get(action.getExecutingServiceID());
                set.remove(action);
                if (set.isEmpty()){
                    openServiceIDActionsMap.remove(action.getExecutingServiceID());;
                    ServiceContext callerContext=(ServiceContext)openFinishSet.remove(action.getExecutingServiceID());
                    
                    finishManager.finishComplete(action.getExecutingServiceID(),callerContext);    
                }
            }
            
        }

    }
	
	private ServiceManager serviceManager;
	private ActionScheduler scheduleManager;

	private boolean currentlyExecuting;
	
    protected ServiceContext callerContext;
    private FinishOperator finishOperator;
    private ServiceContext executionContext;

    

	/**
	 * 
	 * Constructor for class <code>LocalExecutionManager</code>
	 * @param scheduleManager
	 */
	public LocalExecutionManager(ActionScheduler scheduleManager) {
		
		
		this.scheduleManager = scheduleManager;
		
		currentlyExecuting = false;
	}

	
    public void init(ServiceManager serviceManager,FinishManager finishManager) {
        
        this.serviceManager = serviceManager;
        finishOperator=new FinishOperator(finishManager);
    }
	public void schedule(Action action) {
	    finishOperator.scheduleAction(action);
	    
		if (!currentlyExecuting && scheduleManager.isEmpty()) {
			scheduleProcessingEventImmediately();
		}
		scheduleManager.addAction(action);
	}

	

    public ServiceContext getCallerContext() {
        if (callerContext==null){
            return new ServiceContext();
        }
        return callerContext;
    }
    
    

    public ServiceContext getExecutionContext() {
        return executionContext;

    }
    

    public void startFinish(ServiceID serviceID, ServiceContext callerContext) {
        
        finishOperator.startFinish(serviceID,callerContext);
    }
    
    

    public void endFinish(ServiceID serviceID) {
        
        finishOperator.endFinish(serviceID);

    }
  

    /**
     * 
     * TODO Comment method
     *
     */
	public void executeNextAction() {
		if (scheduleManager.isEmpty()) {
			currentlyExecuting = false;
		}
		else {
			Action action =  scheduleManager.nextAction();
			ServiceID serviceID = action.getExecutingServiceID();
			executionContext=action.getExecutingContext();
			Service service = serviceManager.getService(serviceID);
			if (service != null) {
				currentlyExecuting = true;
				
				callerContext = action.getCallerContext();
				//TODO:performance!!!
				    //TODO: test it _before_ action is scheduled in context of the caller service
	//			if(!serviceManager.isAllowed(serviceID,callerContext.getServiceID())){
	//			    // throw a real exception....
	//			    throw new OperatingServiceException("Access to this service is denied");
	//			}
				OperatingSystem.setServiceManager((DeviceServiceManager)serviceManager);
				action.execute(service);
				OperatingSystem.setServiceManager(null);
				finishOperator.executeAction(action);
				callerContext = null;
				
				
				
				
				scheduleProcessingEvent();
			}
			else {
				currentlyExecuting = false;
				if (!scheduleManager.isEmpty()) {
					scheduleProcessingEventImmediately();
				}
			}
			executionContext=new ServiceContext(null,serviceManager.getDeviceID());
		}
	}

	protected abstract void scheduleProcessingEvent();
	protected abstract void scheduleProcessingEventImmediately();
	
	public String toString() {
	 
	    return callerContext.toString();
	}


}