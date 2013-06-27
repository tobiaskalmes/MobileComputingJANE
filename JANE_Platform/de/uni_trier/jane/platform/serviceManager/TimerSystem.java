/*****************************************************************************
 * 
 * TimerSystem.java
 * 
 * $Id: TimerSystem.java,v 1.1 2007/06/25 07:23:00 srothkugel Exp $
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
package de.uni_trier.jane.platform.serviceManager;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.util.HashMapSet;

/**
 * This is the implementation of the <code>SimulationTimer</code> interface.
 * This implementation is used for the device platform executed on real devices.
 * Timers are called by threads.
 */
public class TimerSystem  implements TimeoutManager{

    /**
     * @author goergen
     *
     * To change the template for this generated type comment go to
     * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
     */
    private static final class TimeoutAction extends Action{

        private ServiceTimeout timeout;

        /**
         * Constructor for class <code>TimeoutEvent</code>
         * @param timeout
         */
        public TimeoutAction(ServiceTimeout timeout,ServiceContext executionContext) {
            super(executionContext,executionContext);
            this.timeout=timeout;
        }

 

        public void execute(Service executingService) {
            timeout.handle();
            
        }
        
        

    }
	/**
	 * @author goergen
	 *
	 * To change this generated comment edit the template variable "typecomment":
	 * Window>Preferences>Java>Templates.
	 * To enable and disable the creation of type comments go to
	 * Window>Preferences>Java>Code Generation.
	 */
	public class TimeoutTask extends TimerTask {
		private ServiceTimeout timeout;
        private ServiceContext executionContext ;
       
        /**
         * 
         * Constructor for class <code>TimeoutTask</code>
         * @param timeout
         * @param executionContext
         */
		public TimeoutTask(ServiceTimeout timeout, ServiceContext executionContext){
			 this.timeout=timeout;
			 this.executionContext=executionContext;
			
		}
		/**
         * @return Returns the executionContext.
         */
        public ServiceContext getContext() {
            return executionContext;
        }
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
            synchronized (mutex) {
                pendingTimeoutMap.remove(this);
                Set set=serviceIDTimeoutMap.get(getContext().getServiceID());
                set.remove(timeout);
                if (set.isEmpty()){
                    serviceIDTimeoutMap.remove(getContext().getServiceDeviceID());
                }
                executionManager.schedule(new TimeoutAction(timeout,executionContext));    
            }
			
		}

	}

	private final static String VERSION = "$Id: TimerSystem.java,v 1.1 2007/06/25 07:23:00 srothkugel Exp $";

	
	protected HashMap pendingTimeoutMap;
	private HashMapSet serviceIDTimeoutMap;
	
	
	private Timer timer;


    private ExecutionManager executionManager;

    private Object mutex;
    
    

	/**
	 * Construct a new <code>TimerSystem</code> object.
	 * @param shutdownManager
	 * @param eventSet the event set used to schedule timer events
	 */
	public TimerSystem(ShutdownManager shutdownManager) {
	    serviceIDTimeoutMap=new HashMapSet();
		timer = new Timer();
		pendingTimeoutMap=new HashMap();
		mutex=new Object();
		
		shutdownManager.addShutdownListener(new DeviceShutdownListener() {
            //
            public void notifyBeginShutdown() {
                // TODO Auto-generated method stub

            }

            //
            public void notifyEndShutdown() {
                stop();

            }

            public void notifyStartBoot() {
                // TODO Auto-generated method stub
                
            }
        });
	}
    public void init(ExecutionManager executionManager,FinishManager finishManager) {
        this.executionManager=executionManager;
        finishManager.addFinishListener(new FinishListener() {
            //
            public void notifyFinished(ServiceID serviceID,
                    ServiceContext finishContext) {
                HashSet set=(HashSet) serviceIDTimeoutMap.get(serviceID);
                if (set!=null){
                    Iterator iterator=((Set)set.clone()).iterator();
                    while (iterator.hasNext()) {
                        ServiceTimeout element = (ServiceTimeout) iterator.next();
                        removeTimeout(element);
                        
                    }
                }


            }
        });
        
    }

	/**
	 * @param system
	 * @see de.uni_trier.ubi.appsim.kernel.SimulationTimer#set(Timeout)
	 */
	public void setTimeout(ServiceContext serviceContext,ServiceTimeout timeout) {
        synchronized (mutex) {
            TimeoutTask tot=new TimeoutTask(timeout,serviceContext);
            serviceIDTimeoutMap.put(serviceContext.getServiceID(),timeout);
            timer.schedule(tot,(long)(timeout.getDelta()*1000));
            pendingTimeoutMap.put(timeout,tot);    
        }
		
		//return true;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.SimulationTimer#removeTimeout(Timeout)
	 */
	public void removeTimeout(ServiceTimeout timeout) {
        synchronized (mutex) {
            TimeoutTask task=(TimeoutTask)pendingTimeoutMap.remove(timeout);
            if (task==null)return;
            Set set=serviceIDTimeoutMap.get(task.getContext().getServiceID());
            set.remove(timeout);
            if (set.isEmpty()){
                serviceIDTimeoutMap.remove(task.getContext().getServiceID());
            }
            
            task.cancel();    
        }
		
		
	}

    /**
     * 
     */
    public void stop() {
        timer.cancel();
        
    }





	
	
}
