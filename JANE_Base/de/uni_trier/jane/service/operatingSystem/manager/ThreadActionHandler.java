/*
 * Created on 21.01.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.service.operatingSystem.manager;

import java.util.*;

import de.uni_trier.jane.basetypes.*;

import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;



/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ThreadActionHandler extends Thread implements  ActionHandler{

	
	private Object localActionQueueMon;
	private ArrayList localActionQueue;
	protected boolean shuttingDown;
    
    private ServiceContext callerContext;
    private DeviceID deviceID;
    private ServiceManager serviceManager;
    private ServiceContext executingService;
    private FinishManager finishManager;
    private SyncObject syncObject;
    
    

    /**
     * 
     * Constructor for class <code>ThreadActionHandler</code>
     * @param deviceID
     * @param shutdownManager
     */
	public ThreadActionHandler(DeviceID deviceID, ShutdownManager shutdownManager) {
	    
	   this(deviceID,shutdownManager,new SyncObject());
	}

	




    /**
     * 
     * Constructor for class <code>ThreadActionHandler</code>
     * @param deviceID
     * @param shutdownManager
     * @param syncObject
     */
    public ThreadActionHandler(DeviceID deviceID, 
            ShutdownManager shutdownManager,  SyncObject syncObject) {
        
        this.deviceID=deviceID;

		localActionQueueMon=new SyncObject();
		
		this.syncObject=syncObject;
		localActionQueue=new ArrayList();
		this.callerContext=new ServiceContext(null,deviceID);
        if (shutdownManager!=null){
            shutdownManager.addShutdownListener(new DeviceShutdownListener() {
                //
                public void notifyBeginShutdown() {
                    // TODO Auto-generated method stub
    
                }
    
                //
                public void notifyEndShutdown() {
                    shuttingDown=true;
                  
    
                }
    
                public void notifyStartBoot() {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
    }


    public void init(ServiceManager serviceManager, FinishManager finishManager) {
	    this.serviceManager=serviceManager;
	    this.finishManager=finishManager;
        
    }
		




	
	


    public void run() {	
       
        
		while (!shuttingDown){
			Action action;
			synchronized(localActionQueueMon){
				try {
					while (localActionQueue.isEmpty()){
						localActionQueueMon.wait();
					}
				} catch (InterruptedException e) {
					
				
				}
				action=(Action)localActionQueue.remove(0);
			}
			synchronized(syncObject){
				callerContext=action.getCallerContext();
				Service service=serviceManager.getService(action.getExecutingServiceID());
				if (service!=null){
			    	executingService=action.getExecutingContext();
			    	action.execute(service);			
			    	callerContext=new ServiceContext(null,deviceID);
			    	executingService=callerContext;
			    	//serviceManager.exitContext();
				}
			}
			
		}
	}
	

	  /**
     * TODO: comment method 
     * 
     */
//    public void shutdownService(ServiceID serviceID) {
//        	shuttingDown=true;
//        	throw new IllegalStateException("not yet implemented");
//        final Object syncObject=new Object();
//        synchronized(syncObject){
//            synchronized(localActionQueueMon){
//                localActionQueue.clear();
//                localActionQueue.add(new Action(serviceID,callerContext.getCallerService(),callerContext.getCallerDevice() ) {
//                    public void execute(Service executingService) {
//                        synchronized(syncObject){
//                            shuttingDown=true;
//                            //service.finish();
//                            syncObject.notify();
//                        }
//                    }
//                });
//                localActionQueueMon.notify();
//                //this.interrupt();
//            }
//            
//            try {
//                syncObject.notify();
//                syncObject.wait();
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
        
 //   }


    
    
    public ServiceContext getCallerContext() {
        
        return callerContext;
    }
    
    
    
   
    /* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ExecutionManager#setCallerContext(de.uni_trier.jane.service.operatingSystem.ServiceContext, de.uni_trier.jane.service.operatingSystem.ServiceContext)
	 */
	public ServiceContext setCallerContext(ServiceContext excutingContext,
			ServiceContext callerContext) {
	
        ServiceContext oldContext=this.callerContext;
        this.callerContext=callerContext;
        return oldContext;
    }
    
    public ServiceContext getExecutionContext(){
        return executingService;
    }
	
	
    public void schedule(Action action) {
        synchronized(localActionQueueMon){
            localActionQueue.add(action);
            localActionQueueMon.notify();
        }

    }



    // Adrian, 20.10.2006, changes for 1.3
    public void startFinish( ServiceID serviceID, ServiceContext callerContext ) 
    {
    	// make the callerContext final ...
    	final ServiceContext serviceCallerContext = callerContext;
    	
        schedule( new Action( new ServiceContext( serviceID,deviceID ), getCallerContext() )
        {
            public void execute( Service executingService ) 
            {
            	// ... in order to use it here
                finishManager.finishComplete( getExecutingServiceID(), serviceCallerContext );
            }	
            
        });

    }



    //
    public void endFinish(ServiceID serviceID) {
                
        
    }






    /**
     * TODO Comment method
     * 
     */
    public void shutdown() {
        
        schedule(new Action(new ServiceContext(OperatingSystem.OperatingSystemID,deviceID), new ServiceContext(OperatingSystem.OperatingSystemID,deviceID)){
            

            public void execute(Service executingService) {
                shuttingDown=true;

            }
        });
        
    }





	

}
