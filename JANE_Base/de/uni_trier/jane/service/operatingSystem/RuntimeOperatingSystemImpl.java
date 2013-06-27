/*****************************************************************************
 * 
 * RuntimeOperatingSystemImpl.java
 * 
 * $Id: RuntimeOperatingSystemImpl.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem;

import java.lang.reflect.Field;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.Console;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.reflectionSignal.ListenerStub;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.shapes.Shape;



/**
 * 
 * 
 * TODO: comment class  
 * @author daniel
 *
 */
public  class RuntimeOperatingSystemImpl implements RuntimeOperatingSystem{//, DeviceShutdownListener{    
    
	//protected DeviceID deviceID;
	
	private DistributionCreator distributionCreator;
	
	
	private Console consoleTextBuffer;
	protected ExecutionManager executionManager;
	protected FinishManager finishManager;
	protected TimeoutManager timeoutManager;
	
	protected LocalSignalManager localSignalManager;
    
   // protected EventDB eventDB;
	
	
   // protected DeviceServiceFactory serviceFactory;

    //private HashMap deprecatedTaskListenerMap;

    private Clock clock;


   

    protected ServiceManager serviceManager;

    protected ServiceInformation serviceThread;

    protected ShutdownManager shutdownManager;

    





	
	/**
     * 
     * Constructor for class <code>RuntimeOperatingSystemImpl</code>
     * @param serviceThread
     * @param distributionCreator
     * @param timeoutManager
     * @param executionManager
     * @param serviceManager
     * @param localSignalManager
     * @param eventDB
     * @param finishManager
     * @param shutdownManager
     * @param console
     * @param runtimeClock
	 */
    public RuntimeOperatingSystemImpl(ServiceInformation serviceThread, 
            DistributionCreator distributionCreator, TimeoutManager timeoutManager, 
            ExecutionManager executionManager, 
            ServiceManager serviceManager,
            LocalSignalManager localSignalManager, 
            //EventDB eventDB,
            FinishManager finishManager,ShutdownManager shutdownManager, Console console,
            Clock runtimeClock) {

	
    	// 
		this.serviceThread=serviceThread;
		this.distributionCreator = distributionCreator;
		this.consoleTextBuffer = console;
		this.clock=runtimeClock;
    	this.serviceManager=serviceManager;    	
    	this.executionManager =executionManager;
    	this.localSignalManager=localSignalManager;
        //this.eventDB=eventDB;
    	this.finishManager=finishManager;
    	this.shutdownManager=shutdownManager;
        this.timeoutManager=timeoutManager;
    	
       
        //deprecatedTaskListenerMap=new HashMap();
    }
	


	
	
	
	

    public ServiceID startService(Service service) {
	    return serviceManager.startService(serviceThread.getContext(),service);

        
    }

	
	

    public void finishService(ServiceID serviceID) {
		finishManager.finishService(serviceThread.getContext(),new ServiceContext(serviceID,getDeviceID()));
	}
	
    
    public boolean serviceSatisfies(ServiceID serviceID, Class serviceClass) {

        return serviceManager.serviceSatisfies(serviceID, serviceClass);
    }

    public boolean hasService(ServiceID serviceID) {

        return serviceManager.hasService(serviceID);
    }
    
    //
    public boolean hasService(Class serviceClass) {
     
        return serviceManager.hasService(serviceClass);
    }

    //
    public ServiceID[] getServiceIDs(Class serviceClass) {
        return serviceManager.getServiceIDs(serviceClass);
    }
	
  



  
    
    public Object accessSynchronous(ListenerID requestedService, ListenerAccess serviceData) {
    	return localSignalManager.accessSynchronous(serviceThread.getContext(),requestedService, serviceData);
    }
    
 
    
    public void setTimeout(ServiceTimeout timeout) {
        timeoutManager.setTimeout(serviceThread.getContext(),timeout);
    }

    public void removeTimeout(ServiceTimeout timeout) {
        timeoutManager.removeTimeout(timeout);
    }

	public void denyAllServices() {
		serviceThread.denyAllServices();
	}
	public void denyService(ServiceID service) {
	    serviceThread.denyService(service);
	}
	public void allowService(ServiceID service) {
	    serviceThread.allowService(service);
	}
	public void allowAllServices() {
	    serviceThread.allowAllServices();
	}

    public void registerAtService(ServiceID serviceID, Class serviceType) {
        if (!serviceManager.hasService(serviceID)) throw new OperatingServiceException("Service does not exist");
        registerAtService(serviceID, serviceThread.getServiceID(), serviceType);
        
    }
    

    public void registerAtService(final ServiceID serviceID, ListenerID listenerID,
            final Class serviceType) {
        if (!serviceManager.hasService(serviceID)) throw new OperatingServiceException("Servic does not exist");
        serviceManager.getServiceInformation(new ServiceContext(serviceID,getDeviceID())).registerService(getDeviceID(),listenerID,localSignalManager.getListenerClass(listenerID),serviceType);
        localSignalManager.addFinishListener(serviceThread.getContext().getOSContext(), listenerID,new ListenerFinishedHandler() {
            //
            public void handleFinished(ListenerID listenerID) {
                if (serviceManager.hasService(serviceID)){
                    serviceManager.getServiceInformation(new ServiceContext(serviceID,getDeviceID()))
                    	.unregisterService(getDeviceID(),listenerID,serviceType);
                }

            }
        });
    }
    
    //
    public ListenerID registerAtService(ServiceID serviceID,
            SignalListener listener, Class serviceType) {
        if (!serviceManager.hasService(serviceID)) throw new OperatingServiceException("Servic does not exist");
        ListenerID listenerID=localSignalManager.registerListener(serviceThread.getContext(),listener);
        registerAtService(serviceID,listenerID,serviceType);
        return listenerID;
    }
    
    //
    public void unregisterAtService(ServiceID serviceID, ListenerID listenerID,
            Class serviceType) {
        if (!serviceManager.hasService(serviceID)) throw new OperatingServiceException("Servic does not exist");
        serviceManager.getServiceInformation(new ServiceContext(serviceID,getDeviceID())).unregisterService(getDeviceID(),listenerID,serviceType);
    }


    public void unregisterAtService(ServiceID serviceID, Class serviceType) {
        if (!serviceManager.hasService(serviceID)) throw new OperatingServiceException("Servic does not exist");
        unregisterAtService(serviceID,serviceThread.getServiceID(),serviceType);
    }


	
    
    public ListenerID registerSignalListener(SignalListener listener,
            Class classToRegister) {
        if (!classToRegister.isInstance(listener)){
            throw new OperatingServiceException("The listener to register must implement the given listenerClass");
        }
        ListenerID listenerID=localSignalManager.registerListener(serviceThread.getContext(),listener);
        registerStub(listenerID, classToRegister);
        return listenerID;
    }
    
    
    /**
     * 
     * TODO: comment method 
     * @param listenerID
     * @param classToRegister
     */
    private void registerStub( ListenerID listenerID, final Class classToRegister) {

        if (serviceThread.addSignalStub(listenerID,classToRegister)){
            localSignalManager.addFinishListener(serviceThread.getContext().getOSContext(),listenerID,new ListenerFinishedHandler() {
                //
                public void handleFinished(ListenerID listenerID) {
                    serviceThread.removeSignalStub(listenerID,classToRegister);
                }
            });
        }
            
    }



    //
    public void registerSignalListener(Class classToRegister) {
    	if (!classToRegister.isAssignableFrom(serviceThread.getServiceClass())){
    		throw new OperatingServiceException("Service does not implement the given class");
    	}
        registerStub(serviceThread.getServiceID(),classToRegister);

    }



    //
    public void registerSignalListener(SignalListener listener,
            ListenerID listenerID, Class classToRegister) {
        if (!classToRegister.isInstance(listener)){
            throw new OperatingServiceException("The listener to register must implement the given listenerClass");
        }
        if  (localSignalManager.registerListener(serviceThread.getContext(),listener,listenerID)){
            if (!localSignalManager.getContextForListener(listenerID).equals(serviceThread.getContext())){
                throw new OperatingServiceException("Services are not allowed to add export interfaces " +
                		"to listeners created by other services!");
            }
        }
        registerStub(listenerID,classToRegister);
    }

    //
    public ListenerID registerOneShotListener(SignalListener listener,
            Class classToRegister) {
        if (!classToRegister.isInstance(listener)){
            throw new OperatingServiceException("The listener to register must implement the given listenerClass");
        }
        ListenerID listenerID= localSignalManager.registerOneShotListener(serviceThread.getContext(),listener);
        registerStub(listenerID,classToRegister);
        return listenerID;
    }
    
    //
    public void registerOneShotListener(SignalListener listener,
            ListenerID listenerID,Class classToRegister) {
        if (!classToRegister.isInstance(listener)){
            throw new OperatingServiceException("The listener to register must implement the given listenerClass");
        }
        if (!localSignalManager.registerOneShotListener(serviceThread.getContext(),listener,listenerID)){
            if (!localSignalManager.getContextForListener(listenerID).equals(serviceThread.getContext())){
                throw new OperatingServiceException("Services are not allowed to add export interfaces " +
                		"to listeners created by other services!");
            }
        }
        registerStub(listenerID,classToRegister);
        
    }
    
    //
    public Object getSignalListenerStub(ListenerID listenerID,
            Class listenerClass) {
        if (!localSignalManager.hasListener(listenerID)){
            throw new OperatingServiceException("A listener for the given ListenerID does not exist");
        }
        ServiceContext executingService=localSignalManager.getContextForListener(listenerID);//.getCallerService();
        return serviceManager.getServiceInformation(executingService).
			getSignalStub(serviceThread.getContext(),listenerID,listenerClass);
        
    }
    
    //
    public Object getAccessListenerStub(ListenerID listenerID,
            Class listenerClass) {
        if (!localSignalManager.hasListener(listenerID)){
            throw new OperatingServiceException("A listener for the given ListenerID does not exist");
        }
        ServiceContext executingService=localSignalManager.getContextForListener(listenerID);//.getCallerService();
        return serviceManager.getServiceInformation(executingService).getAccessStub(serviceThread.getContext(),listenerID,listenerClass);
    }
    
    //
    public void registerAccessListener(Class classToRegister) {
    	if (!classToRegister.isAssignableFrom(serviceThread.getServiceClass())){
    		throw new OperatingServiceException("Service does not implement the given class");
    	}
        registerAccessStub(serviceThread.getServiceID(),classToRegister);

    }
    
    /**
     * 
     * TODO: comment method 
     * @param listenerID
     * @param classToRegister
     */
    private void registerAccessStub( ListenerID listenerID, final Class classToRegister) {
        
        serviceThread.addAccessStub(listenerID,classToRegister);
        localSignalManager.addFinishListener(serviceThread.getContext().getOSContext(),listenerID,new ListenerFinishedHandler() {
            //
            public void handleFinished(ListenerID listenerID) {
                serviceThread.removeAccessStub(listenerID,classToRegister);
                

            }
        });

        
        
    }







    //
    public ListenerID registerAccessListener(SignalListener listener,
            Class classToRegister) {
    	if (!classToRegister.isInstance(listener)){
            throw new OperatingServiceException("The listener to register must implement the given listenerClass");
        }
        ListenerID listenerID=localSignalManager.registerListener(serviceThread.getContext(),listener);
        registerAccessStub( listenerID, classToRegister);
        return listenerID;       
    }
    
    
    
    public void registerAccessListener(SignalListener listener,
            ListenerID listenerID, Class classToRegister) {
    	if (!classToRegister.isInstance(listener)){
            throw new OperatingServiceException("The listener to register must implement the given listenerClass");
        }
        if  (localSignalManager.registerListener(serviceThread.getContext(),listener,listenerID)){
            if (!localSignalManager.getContextForListener(listenerID).equals(serviceThread.getContext())){
                throw new OperatingServiceException("Services are not allowed to add export interfaces " +
                		"to listeners created by other services!");
            }
        }
        registerAccessStub(listenerID,classToRegister);  
    }
    
    //
    public boolean hasListener(ListenerID listenerID) {
        
        if (listenerID==null)return false;

        
        return localSignalManager.hasListener(listenerID);
    }
    
    public boolean hasListener(SignalListener signalListener) {
        return localSignalManager.hasListener(signalListener);
    }
    
    public void finishListener(ListenerID listenerID) {

        localSignalManager.finish(serviceThread.getContext(),listenerID);   
    }
    
    
    
    public void finishListener(SignalListener signalListener) {
    	
    	
        localSignalManager.finish(serviceThread.getContext(),signalListener);
    	

    }

    
 
    
    
    
    
   

    public void sendSignal(ListenerID receiver, Signal signal) {
        localSignalManager.sendSignal(serviceThread.getContext(), receiver,signal);
    }
    

 



    public void sendSignal(Signal signal) {
        List listeners=serviceThread.getRegisteredListeners(getDeviceID(),signal.getReceiverServiceClass());
        localSignalManager.sendSignal(serviceThread.getContext(),listeners,signal);
    }

/********************* event management *****************************/
    
    public ListenerID registerEventListener(ServiceEvent eventByExample, SignalListener signalListener) {
        ListenerID listenerID=localSignalManager.registerListener(serviceThread.getContext(),signalListener);
        registerEventListener(eventByExample,listenerID);
        return listenerID;
    }
    
    public void registerEventListener(ServiceEvent eventByExample,  ListenerID listenerID) {
        localSignalManager.registerEventListener(serviceThread.getContext(), eventByExample, listenerID);
    }
    

    
    
    public boolean  sendEvent(ServiceEvent serviceEvent) {
        return localSignalManager.sendEvent(serviceThread.getContext(),serviceEvent,serviceThread.getServiceClass());
    }
    
    
    

 

    public DistributionCreator getDistributionCreator() {
        return distributionCreator;
    }

 
	

	public DeviceID getDeviceID() {
		return serviceThread.getDeviceID();
	}
	

    public ServiceID getServiceID() {

        return serviceThread.getServiceID();
    }

	public void write(String text) {
		//ConsoleText consoleText = new ConsoleText(text);
		consoleTextBuffer.println(text);
	}
	
	







    /**
     * @return Returns the executionManager.
     */
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }

	public ServiceID getCallingServiceID() {
		return executionManager.getCallerContext().getServiceID();
	}





    //
    public void addListenerHandler(ListenerID listenerID, ListenerFinishedHandler handler) {
        localSignalManager.addFinishListener(serviceThread.getContext(),listenerID,handler);  
    }
    
    /**
     * 
     * TODO: comment method 
     * @param signalListener
     * @param handler
     */
    public void addListenerHandler(SignalListener signalListener, ListenerFinishedHandler handler) {
    	
    	
        localSignalManager.addFinishListener(serviceThread.getContext(),signalListener,handler);
    	
          
    }
    
    /**
     * 
     * TODO: comment method 
     * @param signalListener
     * @param handler
     */
    public void removeListenerHandler(SignalListener signalListener, ListenerFinishedHandler handler) {
    	
    	
    	localSignalManager.removeFinishListener(signalListener,handler);
    	
          
    }

    //
    public void removeListenerHandler(ListenerID listenerID, ListenerFinishedHandler handler) {
        
        localSignalManager.removeFinishListener(listenerID,handler);
        
    }
    
  

	public double getTime() {
		return clock.getTime();
	}

	public void setTime(double time) {
		clock.setTime(time);
	}

	

	public void reboot() {
		shutdownManager.reboot(serviceThread.getContext());
	}

	public void shutdown() {
		shutdownManager.shutdown(serviceThread.getContext());
	}







	/**
	 * @param receiverContext
	 * @param listenerID
	 * @param object
	 */
	public void sendSignal(ServiceContext receiverContext, ListenerID listenerID, Signal signal) {
		sendSignal(listenerID,signal);
		
	}

    /**
     * TODO Comment method
     * @param listener
     * @param class1
     * @return
     */
    public ListenerID registerSignalListenerAutogen(SignalListener listener) {
        
        return localSignalManager.registerListenerAutogen(serviceThread.getContext(),listener);
    }








    public Shape getDeviceShape() {
        return serviceManager.getServiceShape();
        
    }



    public String toString() {
     
        return serviceManager.getDeviceID().toString();
    }




	




    


}
