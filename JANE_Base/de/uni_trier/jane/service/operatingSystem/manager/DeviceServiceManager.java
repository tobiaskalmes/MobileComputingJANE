/*****************************************************************************
 * 
 * DeviceServiceManager.java
 * 
 * $Id: DeviceServiceManager.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.util.*;

import sun.awt.GlobalCursorManager;


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class DeviceServiceManager implements ServiceManager, DeviceShutdownListener {
    
    private static final boolean USE3D = false;
    protected Map idServiceThreadMap;
    protected LocalSignalManager signalManager;
    protected DeviceID deviceID;
    protected boolean isShuttingDown;
    protected ExecutionManager executionManager;
    protected FinishManager finishManager;
    protected DistributionCreator distributionCreator;
    protected Clock clock;
    protected Console console;
    protected TimeoutManager timeoutManager;
    protected ShutdownManager shutdownManager;
    protected DeviceServiceFactory serviceFactory;
    private ArrayList synchronousCallSet;
    private HashMap deprecatedTaskListenerMap;
    private ServiceThread operatingServiceThread;
   // protected EventDB eventDB;

    /**
     * 
     * Constructor for class <code>DeviceServiceManager</code>
     * @param executionManager
     * @param localSignalManager
     * @param finishManager
     * @param timeoutManager
     * @param clock
     * @param deviceID
     * @param distributionCreator
     * @param console
     */
    public DeviceServiceManager(ExecutionManager executionManager, LocalSignalManager  localSignalManager, FinishManager finishManager, ShutdownManager shutdownManager,
            TimeoutManager timeoutManager, Clock clock, DeviceID deviceID, DistributionCreator distributionCreator, Console console) {
        finishManager.addFinishListener(new FinishListener() {
            //
            public void notifyFinished(ServiceID serviceID,
                    ServiceContext finishContext) {
                idServiceThreadMap.remove(serviceID);

            }
        });
        shutdownManager.addShutdownListener(this);
        this.signalManager=localSignalManager;
        signalManager.init(executionManager,finishManager,this);
        //this.eventDB=eventDB;
        this.executionManager=executionManager;
        executionManager.init(this,finishManager);
        this.finishManager=finishManager;
        finishManager.init(executionManager,this);
        this.shutdownManager=shutdownManager;
        shutdownManager.init(this,finishManager);
        this.deviceID=deviceID;
        this.distributionCreator=distributionCreator;
        this.console=console;
        this.timeoutManager=timeoutManager;
        timeoutManager.init(executionManager,finishManager);
        
        this.clock=clock;
        
       
        
        idServiceThreadMap=new HashMap();
        synchronousCallSet=new ArrayList();
        deprecatedTaskListenerMap=new HashMap();
        operatingServiceThread=new ServiceThread(new ServiceContext(OperatingSystem.OperatingSystemID,deviceID),
               new DummyService(), DummyService.class,false,this);
        
        
    }
    
    /**
     * 
     * TODO Comment method
     * @return
     */
    public LocalSignalManager getSignalManager() {
		return signalManager;
	}
    
    /**
     * TODO Comment method
     * @return
     */
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }
    
    /**
     * TODO Comment method
     * @return
     */
    public FinishManager getFinishManager() {
        return finishManager;
    }
    

    public RuntimeOperatingSystemImpl getOperatingSystem(ServiceContext callerContext) {
        
        ServiceInformation information=getServiceInformation(callerContext);
        if (information!=null){
            return information.getOperatingSystem();
        }
        return null;
        
    }
    
    
    public Service getService(ServiceID serviceID) {
        //ServiceThread serviceThread=(ServiceThread)getServiceThread(serviceID);
        ServiceInformation serviceThread=(ServiceInformation)idServiceThreadMap.get(serviceID);
        if (serviceThread!=null){
            return serviceThread.getService();
        }else if (serviceID.equals(operatingServiceThread.getServiceID())){
            return operatingServiceThread.getService();
        }
        return null;
    }
    
    

    

    /**
     * @param serviceFactory
     */
    public void setServiceFactory(DeviceServiceFactory serviceFactory) {
        this.serviceFactory=serviceFactory;
    }
    


    public boolean hasService(ServiceID serviceID) {
        return idServiceThreadMap.containsKey(serviceID);
    }

    public boolean hasService(Class serviceClass) {
        // TODO: efficiently check implements
        Iterator iterator=idServiceThreadMap.values().iterator();
        while (iterator.hasNext()){
            ServiceInformation thread=(ServiceInformation)iterator.next();
            if (serviceClass.isAssignableFrom(thread.getServiceClass())){
                return true;
            }
        }
        return false;
    }
    
    public boolean serviceSatisfies(ServiceID serviceID, Class serviceClass) {
        ServiceInformation serviceInformation=(ServiceInformation)idServiceThreadMap.get(serviceID);        
        return serviceInformation!=null&&serviceClass.isAssignableFrom(serviceInformation.getServiceClass());
    }


    public ServiceID[] getServiceIDs(Class serviceClass) {
        ArrayList list=new ArrayList();
        // TODO: efficiently check implements
        Iterator iterator=idServiceThreadMap.values().iterator();
        while (iterator.hasNext()){
            ServiceInformation thread=(ServiceInformation)iterator.next();
            if (serviceClass.isAssignableFrom(thread.getServiceClass())){
                list.add(thread.getServiceID());
            }
        }
        return (ServiceID[])list.toArray(new ServiceID[list.size()]);
    }
  

    public ServiceID[] getServiceIDs() {
        Set keySet = idServiceThreadMap.keySet();
        return (ServiceID[])keySet.toArray(new ServiceID[keySet.size()]);
    }
    
    public ServiceID[] getLocalServiceIDs() {

        return getServiceIDs();
    }

    public Shape getServiceShape(){
        ShapeCollection shape=new ShapeCollection();
        Iterator iterator=//new HashSet(
                idServiceThreadMap.values().iterator();
        Position position=Position.NULL_POSITION;
        while (iterator.hasNext()){
            
            Shape serviceShape=((ServiceInformation)iterator.next())
            		.getShape();
            if (serviceShape!=null){
                
                if (USE3D){
                    position=position.sub(new Position(0,0,1));
                }
                shape.addShape(serviceShape,position);
            }
        }
        if (USE3D&&!idServiceThreadMap.isEmpty()){
            shape.addShape(new DeviceLineShape(deviceID,Position.NULL_POSITION,position,Color.BLACK));
        }
        return shape;
    }
    
  

  





    public ServiceInformation getServiceInformation(ServiceContext serviceContext) {

        return (ServiceInformation) idServiceThreadMap.get(serviceContext.getServiceID());
    }

    public class StartServiceAction extends Action {
        
        public StartServiceAction(ServiceContext executingServiceID,ServiceContext callerContext) {
            super(executingServiceID, callerContext);
        }
        public void execute(Service executingService) {
            
            handleStartService(executingService,getExecutingServiceID());
            
        }
       
    }


    
//    protected ServiceThread getServiceThread(ServiceID serviceID){
//       return (ServiceThread) idServiceThreadMap.get(serviceID);
//    }
    
  

    public ServiceID startService(ServiceContext context, Service service) {
        
        return startService(context,service,service.getClass(),serviceFactory.checkServiceID(service),true);

    }
    
    
    public ServiceID startService(ServiceContext callerContext, Service service, Class serviceClass, ServiceID serviceID, boolean visualize) {
        if(!isShuttingDown) {
            ServiceThread serviceThread=new ServiceThread(new ServiceContext(serviceID,deviceID),service,serviceClass,visualize,this);
            if(idServiceThreadMap.put(serviceThread.getServiceID(), serviceThread) != null) {
                throw new OperatingServiceException("The given service ID already exists.");
            }
            signalManager.registerListener(serviceThread.getContext(),service,serviceThread.getServiceID());
            Action action = new StartServiceAction(serviceThread.getContext(), callerContext);
            executionManager.schedule(action);
            return serviceID;
        }
        return null;
        
    }
    
//    /**
//     * TODO Comment method
//     * @param taskHandle
//     * @param runningService
//     */
//    public void removeTaskListenerInternal(DeprecatedTaskHandle taskHandle, ServiceID runningService) {
//        ListenerFinishedHandler handler = (ListenerFinishedHandler)deprecatedTaskListenerMap.remove(new DeprecatedTaskPair(taskHandle,runningService));
//        if (handler==null) throw new OperatingServiceException("No handler registered by this service for the given TaskHandle");
//        signalManager.removeFinishListener(taskHandle.getListenerID(),handler);
//        
//        
//    }
    
    /**
     * 
     * TODO: comment method 
     * @param taskHandle
     * @param taskFinishListener
     * @param registeringService
     */
//    public void addTaskListenerInternal(DeprecatedTaskHandle taskHandle, TaskFinishListener taskFinishListener, ServiceContext registeringService) {
//        ListenerFinishedHandler handler=new DeprecatedTaskFinishListener(taskFinishListener,taskHandle);
//        signalManager.addFinishListener(registeringService,taskHandle.getListenerID(),handler);
//        //addListenerHandler(taskHandle.getListenerID(),handler);
//        deprecatedTaskListenerMap.put(new DeprecatedTaskPair(taskHandle,registeringService.getServiceID()),handler);
//    }
  
    /**
     * 
     * TODO Comment method
     * @param service
     * @param executingServiceID
     */
    protected void handleStartService(Service service, ServiceID executingServiceID) {
        ServiceInformation  serviceThread=getServiceInformation(new ServiceContext(executingServiceID,deviceID));
        if (service instanceof RuntimeService){
            RuntimeService runtimeService = (RuntimeService)service;
            RuntimeOperatingSystemImpl operatingSystem=new RuntimeOperatingSystemImpl(serviceThread, distributionCreator, timeoutManager,executionManager,
                    this,signalManager,finishManager,shutdownManager,console,clock);
            serviceThread.setOperatingSystem(operatingSystem);
            runtimeService.start(operatingSystem);
        }else throw new OperatingServiceException("Only Runtime services are allowed to be started on a runtime environment");
		
	}
    
    public void notifyStartBoot() {
     
        ServiceCollection bootSequence=serviceFactory.getServiceCollection();
        Iterator iterator = bootSequence.getServiceIDs().iterator();
        while (iterator.hasNext()) {
            ServiceID serviceID = (ServiceID)iterator.next();
            Service service=bootSequence.getService(serviceID);
            startService(new ServiceContext(OperatingSystem.OperatingSystemID,deviceID),service,service.getClass(),serviceID,bootSequence.visualize(serviceID));
        
        }
    }
    
    public void notifyBeginShutdown() {
     
    }

    public void notifyEndShutdown() {

    }





    public boolean isAllowed(ServiceID accessedServiceID, ServiceID callerService) {
    	//TODO: ist das OK?
    	if (accessedServiceID.equals(OperatingSystem.OperatingSystemID)) return true;
        ServiceInformation serviceThread=getServiceInformation(new ServiceContext(accessedServiceID,deviceID));
        if (serviceThread!=null){
            return serviceThread.isAllowed(callerService);
        }else if (accessedServiceID.equals(operatingServiceThread.getServiceID())){
            return true;
        }
        return false;
    }





    /**
     * TODO Comment method
     * @return
     */
    public DeviceID getDeviceID() {
        return deviceID;
    }


    /**
     * TODO Comment method
     * @return
     */
    public Clock getClock() {
        
        return clock;
    }

    public boolean isVisualized(ServiceContext context) {
        ServiceInformation info = getServiceInformation(context);
        if (info==null) return false;
        return info.isVisualized();
    }
    
    public void setVisualized(ServiceContext context, boolean visualize) {
        ServiceInformation info = getServiceInformation(context);
        if (info==null) return;
        info.setVisualized(visualize);
    }
    
    public boolean isShuttingDown() {
     
        return shutdownManager.isShuttingDown();
    }

    


    




}
