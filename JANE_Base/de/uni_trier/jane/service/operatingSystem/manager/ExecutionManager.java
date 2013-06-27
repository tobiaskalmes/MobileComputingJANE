/*
 * Created on Jan 15, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.operatingSystem.manager;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.operatingSystem.*;




/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ExecutionManager {
    
    
    public abstract void schedule(Action action);

    //public ServiceID getCallingServiceID();
    
    

    /**
     * TODO: comment method 
     * @return
     */
    //public abstract DeviceID getCallingDeviceID();
    public ServiceContext getCallerContext();

    /**
     * 
     * TODO Comment method
     * @param excutingContext
     * @param callerContext
     * @return
     */
    public abstract ServiceContext setCallerContext(ServiceContext excutingContext, ServiceContext callerContext);

    /**
     * TODO Comment method
     * @return
     */
    public abstract ServiceContext getExecutionContext();
    
    /**
     * TODO: comment method 
     * @param serviceID
     * @param callerContext
     */
    public abstract void startFinish(ServiceID serviceID, ServiceContext callerContext);

    /**
     * TODO: comment method 
     * @param serviceID
     * @param callerContext
     */
    public abstract void endFinish(ServiceID serviceID);
    
    /**
     * 
     * TODO Comment method
     * @param serviceManager
     * @param finishManager
     */
    public void init(ServiceManager serviceManager,FinishManager finishManager); 

    
}