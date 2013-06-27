/*****************************************************************************
 * 
 * LocalTimeoutManager.java
 * 
 * $Id: LocalTimeoutManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.simulation.basetypes.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.*;
import de.uni_trier.jane.util.HashMapSet;

/**
 * 
 * @author goergen
 *
 * TODO comment class
 */
public class LocalTimeoutManager implements TimeoutManager {
    
    private EventSet eventSet;
    //private ServiceInformation serviceInformation;
    private ExecutionManager executionManager;
    private ClockDeltaCalculator clockDeltaCalculator;
	private Map pendingTimeoutMap;
    private HashMapSet servidPendingTimeoutMap;
    

	/**
	 * 
	 * Constructor for class <code>LocalTimeoutManager</code>
	 * @param eventSet
	 * @param clockDeltaCalculator
	 */
    public LocalTimeoutManager(EventSet eventSet,ClockDeltaCalculator clockDeltaCalculator) {
        
		this.eventSet = eventSet;
        
        
        this.clockDeltaCalculator = clockDeltaCalculator;
		pendingTimeoutMap = new HashMap();
		servidPendingTimeoutMap=new HashMapSet();
    }
    
    
    public void init(ExecutionManager executionManager, FinishManager finishManager){
        this.executionManager = executionManager;
        finishManager.addFinishListener(new FinishListener() {
            //
            public void notifyFinished(ServiceID serviceID,
                    ServiceContext finishContext) {
                Set set=servidPendingTimeoutMap.get(serviceID);
                if (set!=null){
                	set=new LinkedHashSet(set);
                    Iterator iterator=set.iterator();
                    while (iterator.hasNext()) {
                        ServiceTimeout element = (ServiceTimeout) iterator.next();
                        removeTimeout(element);
                        
                    }
                }

            }
        });
    }

    public void setTimeout(ServiceContext context, ServiceTimeout serviceTimeout) {
		if(pendingTimeoutMap.containsKey(serviceTimeout)) {
			throw new OperatingServiceException("The timeout is already set.");
		}
        double oldDelta = serviceTimeout.getDelta();
	    double newDelta = clockDeltaCalculator.getDelta(oldDelta);
	    
	    double time = eventSet.getTime() + newDelta;
	    TimeoutEvent timeoutEvent = new TimeoutEvent(time, context, serviceTimeout);
		pendingTimeoutMap.put(serviceTimeout, timeoutEvent);
		servidPendingTimeoutMap.put(context.getServiceID(),serviceTimeout);
		eventSet.add(timeoutEvent);
	}

    public void removeTimeout(ServiceTimeout serviceTimeout) {
        TimeoutEvent event = (TimeoutEvent)pendingTimeoutMap.remove(serviceTimeout);
        if (event==null) return;
        Set set=servidPendingTimeoutMap.get(event.getContext().getServiceID());
        set.remove(serviceTimeout);
        if (set.isEmpty()){
            servidPendingTimeoutMap.remove(event.getContext().getServiceID());
        }
        if(event != null) {
        	event.disable();
        }
    }

    protected void handleTimeout(ServiceContext context, ServiceTimeout serviceTimeout) {
        TimeoutEvent event = (TimeoutEvent)pendingTimeoutMap.remove(serviceTimeout);
        Set set=servidPendingTimeoutMap.get(event.getContext().getServiceID());
        set.remove(serviceTimeout);
        if (set.isEmpty()){
            servidPendingTimeoutMap.remove(event.getContext().getServiceID());
        }
        Action action = new TimeoutAction(context,serviceTimeout);
        executionManager.schedule(action);
    }

	private class TimeoutEvent extends Event {


		private ServiceTimeout serviceTimeout;
        private ServiceContext context;

		public TimeoutEvent(double time, ServiceContext context, ServiceTimeout serviceTimeout) {
			super(time);
			this.context=context;
			this.serviceTimeout = serviceTimeout;
		}
		
		protected void handleInternal() {
			handleTimeout(context, serviceTimeout);	
		}
		/**
         * @return Returns the context.
         */
        public ServiceContext getContext() {
            return context;
        }

	}

    private static final class TimeoutAction extends Action {
        private ServiceTimeout serviceTimeout;
        /**
         * 
         * Constructor for class <code>TimeoutAction</code>
         * @param context
         * @param serviceTimeout
         */
        public TimeoutAction( ServiceContext context, ServiceTimeout serviceTimeout) {            
            super(context, context);
            this.serviceTimeout = serviceTimeout;
        }

        public void execute(Service executingService) {
            serviceTimeout.handle();
        }
    }
    
    public String toString() {
      
        return "CT:"+eventSet.getTime();
    }

}