/*****************************************************************************
 * 
 * ServiceManager.java
 * 
 * $Id: ServiceManager.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem.manager; 



import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author daniel
 *
 * TODO comment class
 */
public interface ServiceManager {//extends ServiceInformation{
    
    
//    public void init(//ExecutionManager executionManager, 
//            
//            //RuntimeOperatingSystemImpl runtimeEnvironment, 
//            FinishManager finishManager);

    
    
    /**
     * TODO Comment method
     * @param client
     */
    void setServiceFactory(DeviceServiceFactory client);
    /**
     * TODO Comment method
     * @param serviceID
     * @return
     */
    boolean hasService(ServiceID serviceID);
    

    /**
     * TODO: comment method 
     * @param serviceClass
     * @return
     */
    public boolean hasService(Class serviceClass);
    
    /**
     * 
     * TODO: comment method 
     * @param serviceID
     * @param serviceClass
     * @return
     */
    boolean serviceSatisfies(ServiceID serviceID, Class serviceClass);
    
    /**
     * TODO: comment method 
     * @param serviceClass
     * @return
     */
    public ServiceID[] getServiceIDs(Class serviceClass);
    
    /**
     * TODO Comment method
     * @return
     */
    ServiceID[] getServiceIDs();
    




    /**
     * TODO: comment method 
     * @return
     */
    Shape getServiceShape();


    /**
     * TODO: comment method 
     * @param service
     * @param serviceClass
     * @param serviceID
     * @param visualize
     */
    //void add(ServiceThread serviceThread);// service, Class serviceClass, ServiceID serviceID, boolean visualize);


    /**
     * TODO Comment method
     * @param serviceContext
     * @return
     */
    ServiceInformation getServiceInformation(ServiceContext serviceContext);


    /**
     * 
     * TODO Comment method
     * @param callerContext
     * @param service
     * @param serviceClass
     * @param serviceID
     * @param visualize
     */
    ServiceID startService(ServiceContext callerContext, Service service, Class serviceClass, ServiceID serviceID, boolean visualize);


    /**
     * TODO: comment method 
     * @param requestedService
     * @param serviceData
     * @param deviceID
     * @return
     */
    //Object accessSynchronous(ServiceContext callerContext,ServiceID requestedService, ListenerAccess serviceData);


    /**
     * TODO Comment method
     * @param callerContext
     * @return
     */
    RuntimeOperatingSystemImpl getOperatingSystem(ServiceContext callerContext);


    /**
     * TODO Comment method
     * @param serviceID
     * @param callerService
     * @return
     */
    boolean isAllowed(ServiceID serviceID, ServiceID callerService);


    /**
     * TODO Comment method
     * @param context
     * @param service
     * @param class1
     * @param visualize
     */
    ServiceID startService(ServiceContext context, Service service);


//    /**
//     * TODO Comment method
//     * @param taskHandle
//     * @param serviceID
//     * @deprecated
//     */
//    void removeTaskListenerInternal(DeprecatedTaskHandle taskHandle, ServiceID serviceID);
//
//
//    /**
//     * TODO Comment method
//     * @param taskHandle
//     * @param taskFinishListener
//     * @param serviceID
//     * @deprecated
//     */
//    void addTaskListenerInternal(DeprecatedTaskHandle taskHandle, TaskFinishListener taskFinishListener, ServiceContext executingContext);
//

    /**
     * TODO Comment method
     * @param executingServiceID
     * @return
     */
    Service getService(ServiceID executingServiceID);
    /**
     * TODO Comment method
     * @return
     */
    DeviceID getDeviceID();
    
    /**
     * 
     * TODO: comment method 
     * @param serviceContext
     * @return
     */
    boolean isVisualized(ServiceContext serviceContext);
    
    
    /**
     * TODO Comment method
     * @param context
     * @param visualize 
     */
    void setVisualized(ServiceContext context, boolean visualize);
    
    /**
     * TODO Comment method
     * @return
     */
    boolean isShuttingDown();
    /**
     * TODO Comment method
     * @return
     */
    ServiceID[] getLocalServiceIDs();
    /**
     * TODO Comment method
     * @return
     */
    LocalSignalManager getSignalManager();
    
 






    /**
     * TODO: comment method 
     * @param serviceID
     * @return
     */
    //Service enterContext(ServiceID serviceID);


    /**
     * TODO: comment method 
     * 
     */
    //void exitContext();


   
    


}
