/*
 * Created on 04.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.operatingSystem.manager;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;


/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FinishManager {

   protected ServiceManager serviceManager;
    protected ExecutionManager executionManager;
    protected List finishListenerSet;

    /**
     * 
     * Constructor for class <code>FinishManager</code>
     * @param executionManager
     */
    public FinishManager() {

		finishListenerSet = new ArrayList();
    }
    
    
    /**
     * TODO Comment method
     * @param serviceManager
     * @param executionManager
     */
    public void init(ExecutionManager executionManager, ServiceManager serviceManager) {

        this.executionManager = executionManager;
        this.serviceManager=serviceManager;
        
    }
  
    

    public void addFinishListener(FinishListener listener) {
        finishListenerSet.add(listener);
    }
    
    public void removeFinishListener(FinishListener listener) {
        finishListenerSet.remove(listener);
    }

    public void finishService(ServiceContext callerContext,ServiceContext executionContext) {
        Action action = new FinishServiceAction(executionContext,callerContext); 
                
        executionManager.schedule(action);
    }

    protected void handleFinishService(Service executingService, ServiceID serviceID,ServiceContext callerContext) {
        executionManager.startFinish(serviceID,callerContext);
       // serviceManager.remove(serviceID);
                
       
        executingService.finish();
        executionManager.endFinish(serviceID);
      

    }

    private final class FinishServiceAction extends Action {
        
        /**
         * Constructor for class FinishServiceAction 
         *
         * @param executingServiceID
         * @param senderContext
         */
        public FinishServiceAction(ServiceContext executingServiceID, ServiceContext senderContext) {
            super(executingServiceID,senderContext);

        }
        public void execute(Service executingService) {
            handleFinishService(executingService,getExecutingServiceID(),getCallerContext());
        }
    }

    /**
     * 
     * TODO: comment method 
     * @param serviceID
     * @param callerContext
     */
    public void finishComplete(ServiceID serviceID,ServiceContext callerContext) {
        
        Iterator iterator = finishListenerSet.iterator();
        while(iterator.hasNext()) {
            FinishListener listener = (FinishListener)iterator.next();
            listener.notifyFinished(serviceID, callerContext);
        }
    }


    
    


}
