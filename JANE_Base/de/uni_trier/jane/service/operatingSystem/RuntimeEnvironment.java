/*****************************************************************************
 * 
 * RuntimeEnvironment.java
 * 
 * $Id: RuntimeEnvironment.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.signaling.*;


/**
 * This interface describes the "operating system methods" provided to each service.
 * An object of this type is passed to each service before it is started.
 */
// TODO: throws in Javadoc Kommentar!!!
public interface RuntimeEnvironment {

   
/****************************** Basic OperatingSystem calls ******************************/	    
    //TODO: public void setPriority();
    


	/**
	 * Returns the unique ID of the hosting Device. 
	 * @return the unique DeviceID
	 */
	public DeviceID getDeviceID();
	
    /**
     * Returns the id of the current running service (e.g. the calling service)
     * @return	the serviceID
     */
    public ServiceID getServiceID();

	/**
	 * Write a text line to the simulation console.
	 * @param text the text to be written
	 */
	public void write(String text);

	/**
	 * Starts a timeout.
	 * @param timeout the timeout
	 */
	public void setTimeout(ServiceTimeout timeout);

	/** 
	 * Removes a timeout
	 * @param timeout	the ServiceTimeout
	 */
	public void removeTimeout(ServiceTimeout timeout);
	
	/**
	 * Get the source for distributions.
	 * @return the distribution creator
	 */
	public DistributionCreator getDistributionCreator();
	
	
/****************************** Service Management ******************************/	
	/**
	 * Starts a new service on this device
	 * @param service The <code>Service</code> to be started
	 * @return	the serviceID of the started service
	 */
	public ServiceID startService(Service service);
	
	/**
	 * Returns true if a running service with the given serviceID exists
	 * @param serviceID		the serviceID to test
	 * @return	true, if the serviceID is already used
	 */
	public boolean hasService(ServiceID serviceID);
    
    /**
     * 
     * Returns true, if the Service identified by the given ServiceID satisfies the given class 
     * @param serviceID
     * @param serviceClass
     * @return true, if the Service identified by the given ServiceID satisfies the given class
     */
    public boolean serviceSatisfies(ServiceID serviceID,Class serviceClass);
	
	/**
	 * Returns true if a running service implements the given Class or Iterface
	 * @param serviceClass	the class of the interface or class to test
	 * @return	true, if on or more services implements this the given class
	 */
	public boolean hasService(Class serviceClass);
	
	
	/** 
	 * Returns a list of IDs of all Services implementing the given Class or Iterface
	 * @param serviceClass the class of the interface or class to test
	 * @return a list of ServiceIDs of services implementing the given serviceClass
	 */
	public ServiceID[] getServiceIDs(Class serviceClass);
	
	/**
	 * Finish the service with the given <code>ServiceID</code>
	 * @param serviceID		the ID of the servicê to be finished
	 */
	public void finishService(ServiceID serviceID);
	


	
/****************************** Signaling ******************************/
    /**
     * Returns true if a <code>SignalListener</code> with id listenerID exists 
     * @param listenerID  id to check
     * @return true, if the id exists
     */
    public boolean hasListener(ListenerID listenerID);
    
    /**
     * Returns true if the given <code>SignalListener</code> exists
     * @param signalListener    the listener to check 
     * @return  true, if the listener exists
     */
    public boolean hasListener(SignalListener signalListener);
	
	/**
	 * Finish the <code>SignalListener</code> with id listenerID
	 * @param listenerID  the id of the Listener to finish
	 */
    public void finishListener(ListenerID listenerID);
    
    /**
     * Finish the given <code>SignalListener</code>
     * Note, if this listener has been registered under differend ListenerIDs, all IDs are removed!
     * @param signalListener	the listener to finish
     */
    public void finishListener(SignalListener signalListener);
    
    
    /**
     * TODO: comment method 
     * @param classToRegister
     */
    public void registerSignalListener(Class classToRegister);
    
	/**
	 * Register a listener within the operating system
	 * This object can be accessed using signals only
	 * The given class is exported so that other services are able to get a 
	 * signal listner Stub for this interface.
	 * The given class must contain only void methods
	 * The OS generates a unique ListenerID. 
	 * @param listener	the listener to register
	 * @return	the <code>ListenerID</code> of the listener
	 * @param classToRegister the interface to export
	 */
	public ListenerID registerSignalListener(SignalListener listener, Class classToRegister);
	
	
	/**
	 * Register a listener within the operating system with the given <code>ListenerID</code> 
	 * This object can be accessed using signals only
	 * The given class is exported so that other services are able to get a 
	 * signal listner Stub for this interface.
	 * The given class must contain only void methods.
	 * If the given listnerID already exists and the calling service has registerd it,
	 * the given class is added to the requestable stubs of the given listenerID.
	 * @param listener	 	the listener to register
	 * @param listenerID	the (unique) id of the listener
	 * @param classToRegister the interface to export
	 */
	public void registerSignalListener(SignalListener listener, ListenerID listenerID, Class classToRegister);
	
	/**
	 * Register a listener within the operating system.
	 * This listener is finished automatically after receiving one Signal.
	 * The given class is exported so that other services are able to get a 
	 * signal listner Stub for this interface.
	 * The given class must contain only void methods 
	 * The OS generates a unique ListenerID. 
	 * @param listener the listener to register
	 * @param classToRegister the interface to export
	 * @return	the <code>ListenerID</code> of the listener
	 */
	public ListenerID registerOneShotListener(SignalListener listener, Class classToRegister);
	
	/**
	 * Register a listener within the operating system with the given <code>ListenerID</code>
	 * This listener is finished automatically after receiving one Signal.
	 * The given class is exported so that other services are able to get a 
	 * signal listner Stub for this interface.
	 * The given class must contain only void methods.
	 * If the given listnerID already exists and the calling service has registerd it,
	 * the given class is added to the requestable stubs of the given listenerID.	 	
	 * @param listener	 	the listener to register
	 * @param listenerID	the (unique) id of the listener
	 * @param classToRegister the interface to export
	 */
	public void registerOneShotListener(SignalListener listener, ListenerID listenerID, Class classToRegister);
		
	
	/**
	 * Register a listener within the operating system with the given <code>ListenerID</code> 
	 * This object can be accessed using synchronous access only.
	 * The given class is exported so that other services are able to get a 
	 * signal listner Stub for this interface.
	 * Note, one should only export methods not invoking other signal listeners synchronously. 
	 * Moreover, by JANE coding convention, only methods returning current service state 
	 * should by exported.
	 * If the given listnerID already exists and the calling service has registerd it,
	 * the given class is added to the requestable stubs of the given listenerID.
	 * @param listener	 	the listener to register
	 * @param listenerID	the (unique) id of the listener
	 * @param classToRegister	the interface to be exported
	 */
	public void registerAccessListener(SignalListener listener, ListenerID listenerID, Class classToRegister);
	
	/**
	 * Register a listener within the operating system
	 * This object can be accessed using synchronous access only
	 * The given class is exported so that other services are able to get a 
	 * signal listner Stub for this interface.
	 * Note, one should only export methods not invoking other signal listeners synchronously. 
	 * Moreover, by JANE coding convention, only methods returning current service state 
	 * should be exported.
	 * The OperatingSystem generates a unique ListenerID. 
	 * @param listener	the listener to register
	 * @return	the <code>ListenerID</code> of the listener
	 * @param classToRegister 	the interface to be exported
	 */
	public ListenerID registerAccessListener(SignalListener listener, Class classToRegister);
	
	/**
	 * Adds the given class to the synchrounously accessible interfaces of the calling service.
 	 * Note, one should only export methods not invoking other signal listeners synchronously. 
	 * Moreover, by JANE coding convention, only methods returning current service state 
	 * should by exported 
	 * @param classToRegister 	the interface to be exported
	 */
	public void registerAccessListener(Class classToRegister);

	
	/**
	 * Returns the stub object of a <code>SignalListener</code> given by the listenerID 
	 * implementing the given listenerClass
	 * The returned object can be securely casted to the given lsitener class
	 * Note, that all methods are invocated asynchronously and one should use only
	 * immutable or copied arguments!
	 * @param listenerID	the listenerID of the signal listener
	 * @param listenerClass	the class the signal listner should implent
	 * @return	the stub of a signal listener
	 */
	public Object getSignalListenerStub(ListenerID listenerID, Class listenerClass);

	/**
	 * Returns the stub object of a <code>SignalListener</code> given by the listenerID 
	 * implementing the given listenerClass
	 * The returned object can be securely casted to the given lsitener class
	 * Note, that all methods are invocated synchronously.
	 * @param listenerID	the listenerID of the signal listener
	 * @param listenerClass	the class the signal listner should implent
	 * @return	the stub of a signal listener
	 */

	public Object getAccessListenerStub(ListenerID listenerID, Class listenerClass);


	
	/**
	 * Register a listener at a service. With that, it is possible to receive signal multicast send by sendSignal(Signal)
	 * The OS generates a unique ListenerID. 
	 * @param serviceID		the Service to be registered at
	 * @param listener		the listener to register
	 * @param serviceType	a superclass or interface if the expected service - only for preventing coding errors
	 * @return	the <code>ListenerID</code> of the listener		
	 */
	public ListenerID registerAtService(ServiceID serviceID, SignalListener listener, Class serviceType);
	
	/**
	 * Register an existing listener at a service. With that, it is possible to receive signal multicast send by sendSignal(Signal)
	 * @param serviceID		the Service to be registered at
	 * @param listenerID	the ID of the listener 
	 * @param serviceType	a superclass or interface if the expected service - only for preventing coding errors
	 */
	public void registerAtService(ServiceID serviceID, ListenerID listenerID, Class serviceType);

	/**
	 * Register an existing listener from a service.
	 * @param serviceID		the Service to be registered at
	 * @param listenerID	the ID of the listener 
	 * @param serviceType	a superclass or interface if the expected service - only for preventing coding errors
	 */
	public void unregisterAtService(ServiceID serviceID, ListenerID listenerID, Class serviceType);
	
	
	/**
	 * Register the calling service at a service which has to be derived from the given interface or class type.
	 * @param serviceID 	the ID of the service to register at
	 * @param serviceType 	the super class or interface of the service   
	 */
	public void registerAtService(ServiceID serviceID, Class serviceType);

	/**
	 * Unregister the calling service at a service where it was previously registered.
	 * @param serviceID the ID of the service where to deregister
	 * @param serviceType 	the super class or interface of the service   
	 */
	public void unregisterAtService(ServiceID serviceID, Class serviceType);
	
	
	/**
	 * Returns the ID of the calling Service when a signal handling method is called by a 
	 * Signal,Request, Task or a synchronous call.
	 * At service startup the "parent" (the service which started this service) is returned or null if no parent exists
	 * in all other cases (e.g. timeout) the id of this service is returned. 
	 * @return serviceID the ID of the calling service
	 */
	public ServiceID getCallingServiceID();
	





	/**
	 * Send a signal to a the <code>SignalListener</code> assigned to the <code>ListenerID</code> given by receiver 
	 * @param receiver	the ID of the receiving signal listener
	 * @param signal 	the signal to be sent
	 */
	public void sendSignal(ListenerID receiver, Signal signal);
	

	/**
	 * Send a  signal to all <code>SignalListeners</code> matching to the signal receiver class given by the Signal
	 * which has previously been registered at the sending service.
	 * @param signal the signal to be sent
	 */
	public void sendSignal(Signal signal);
	
	/**
	 * Adds a finish handler to the <code>SignalListener</code> given by the listenerID.
	 * The finish method is called when the Listener has been finished.
	 * @param listenerID	the ListenerID
	 * @param handler		the handler to add
	 */
	public void addListenerHandler(ListenerID listenerID, ListenerFinishedHandler handler);
    
    /**
     * Adds a finish handler to the <code>SignalListener</code> given.
     * The finish method is called each time an assigned ListenerID is finished.
     * @param signalListener the SignalListener
     * @param handler       the handler to add
     */
    public void addListenerHandler(SignalListener signalListener, ListenerFinishedHandler handler);
    
	/**
	 * Removes a finish handler from the <code>SignalListener</code> given by the listenerID.
	 * @param listenerID	the ListenerID
	 * @param handler		the handler to remove
	 */
	public void removeListenerHandler(ListenerID listenerID, ListenerFinishedHandler handler);

	/**
	 * Send a Request to the Service with the ID serviceID and register a Replyhandler object.
	 * @param receiver				the ID of the receiver service
	 * @param requestCallbackPair	the Pair containig the request access object and the reply handler object
	 * @deprecated use registerOneShotListener(SignalListener) and sendSignal() instead
	 */
	//public void sendRequest(ServiceID receiver, RequestHandlerPair requestCallbackPair);

	/**
	 * Send a reply signal to the sender of a currently handled request signal.
	 * @param replyHandle	the request-reply handle received with the request signal	
	 * @param serviceReply 	the signal to be replied
	 * @deprecated use sendSignal(ListenerID,Signal) instead
	 */
	//public void sendReply(ReplyHandle replyHandle, ServiceReply serviceReply);
	
	/**
	 * Starts a Task on the receiving Service. A task is a single signal with possiply multiple replies.
	 * The task is active until it is finnished by the receiving service
	 * @param receiver				the ID of the receiver service
	 * @param signalCallbackPair	object containing the signal for starting the task and the callback 
	 * 								object for receiveing the servers replies. 
	 * @return	the handle for the started task
	 * @deprecated use registerListener(SignalListener) and sendSignal() instead
	 */
	//public TaskHandle startTask(ServiceID receiver, TaskCallbackPair signalCallbackPair);
	
	/**
	 * Send a task callback
	 * The TaskHandle is received with the task starting signal 
	 * Task server side method.
	 * @param handle	the handle of the addressed task
	 * @param callback	Callback access object. This object calls the methd within the task callback receiver object.
	 * @deprecated use sendSignal(ListenerID,Signal) instead
	 */
	//public void sendCallback(TaskHandle handle, ServiceCallback callback);
	
	/**
	 * Finish a task and deregister the task callback object.
	 * The TaskHandle is received with the task starting signal 
	 * Task server side method
	 * @param handle	the handle of the addressd task
	 * @deprecated use finishListener() instead
	 */
	//public void finishTask(TaskHandle handle);
	
	/**
	 * Adds a task status listener object to the given task.
	 * It is called when the task has been finished.
	 * @param handle				the handle of the addressed task
	 * @param taskFinishListener	the task finish listener
	 * @deprecated use addListenerFinishedHandler() instead
	 */
	//public void addTaskListener(TaskHandle handle, TaskFinishListener taskFinishListener);
	
	/**
	 * Removes a task statis listener object from the given task
	 * @param handle	the handle of the addressed task
	 * @deprecated use addListenerFinishedHandler() instead
	 */
	//public void removeTaskListener(TaskHandle handle);

    /**
     * Synchronous access method for accessing other Services directly
     * Normally used by a Service Stub.
     * @param requestedService 	The ID of the <code>Service</code> to be accessed synchronously
     * @param listenerAccess		The access object
     * @return Object			The return object of the synchronous access
     * TODO: implement general listener access
     */
	public Object accessSynchronous(ListenerID requestedService, ListenerAccess listenerAccess);

/****************************** EventManagement *********************************/
    /**
     * Sends a service event. All matching registered EventListeners are called. 
     * @param serviceEvent
     * @return true, if any mathing event receiver exist
     */
    public boolean sendEvent(ServiceEvent serviceEvent);
    
    /**
     * Registers an event listener for a given <code>ServiceEvent</code> template.
     * The event listener is called when an event is fired matching the given event example.
     * A null value (also when using primitive types) within the event example matches all values.
     * A none nul value must match excatly, i.e. using the objects equals method.
     * A special case are Class objects as attributes. There, instanceof instead of equals is used.
     * By giving a superclass of the fired event all subclasses matching the example are handled.  
     * @param eventByExample    the <code>ServiceEvent</code> by example that should be handled 
     * @param signalListener     the listener called for all fired matching events
     *                          by default implement at least <code>EventListener</code> 
     * @return                  the listenerID of the registered EventListener
     */
    public ListenerID registerEventListener(ServiceEvent eventByExample, SignalListener signalListener);
    
    
    /**
     * Registers an event listener for a given <code>ServiceEvent</code> template.
     * The event listener is called when an event is fired matching the given event example.
     * A null value (also when using primitive types) within the event example matches all values.
     * A none nul value must match excatly, i.e. using the objects equals method.
     * A special case are Class objects as attributes. There, instanceof instead of equals is used.
     * By giving a superclass of the fired event all subclasses matching the example are handled.  
     * @param eventByExample    the <code>ServiceEvent</code> by example that should be handled 
     * @param idOfEventListener     the ID of the listener that should be called for all fired matching events
     *                          by default use the identified listener should implement at least <code>EventListener</code>  
     */
    public void registerEventListener(ServiceEvent eventByExample, ListenerID idOfEventListener);

	
/****************************** Signaling Security ******************************/	
	/**
	 * Deny all Services the access to this Services
	 */
	public void denyAllServices();
	
	/**
	 * Deny the access to this service for the Service with the given serviceID
	 * @param serviceID 	the ID of the service which should not be able to access this service
	 */
	public void denyService(ServiceID serviceID);
	
	/**
	 * Allow the access to this service for the Service with the given serviceID
	 * @param serviceID 	the ID of the service which should be able to access this service
	 */
	public void allowService(ServiceID serviceID);

	// TODO: allow signals, request and tasks from all services. call <code>denyService(ServiceID service)</code> in order
	// to deny some services.
	
	/**
	 * Allow the access to this service for all other Services
	 */
	public void allowAllServices();
	

	
}
