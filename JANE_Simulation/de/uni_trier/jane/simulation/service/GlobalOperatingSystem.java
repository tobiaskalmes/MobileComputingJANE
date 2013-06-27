/*****************************************************************************
 * 
 * GlobalOperatingSystem.java
 * 
 * $Id: GlobalOperatingSystem.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.service;

import de.uni_trier.jane.basetypes.*;

import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.global_knowledge.*;



/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//TODO: throws in Javadoc Kommentar!!!

public interface GlobalOperatingSystem extends RuntimeEnvironment {
    
    
	
/****************************** Service Management ******************************/	
	/**
	 * Starts a new service on the given device
	 * @param deviceID		the devcice to check
	 * @param service The <code>Service</code> to be started
	 * @return	the serviceID of the started service
	 * 
	 */
	public ServiceID startService(DeviceID deviceID,Service service);
	
	/**
	 * Finish the service with the given <code>ServiceID</code> on the given device
	 * @param deviceID		the devcice to check
	 * @param serviceID		the ID of the servic� to be finished
	 */
	public void finishService(DeviceID deviceID,ServiceID serviceID);
	
	/**
	 * Returns true if a running service with the given serviceID exists on the given device
	 * @param deviceID		the devcice to check
	 * @param serviceID		the serviceID to test
	 * @return	true, if the serviceID is already used
	 */
	public boolean hasService(DeviceID deviceID,ServiceID serviceID);
	
	/**
	 * Returns true if a running service implements the given Class or Iterface on the given device
	 * @param deviceID		the devcice to check 
	 * @param serviceClass	the class of the interface or class to test
	 * @return	true, if on or more services implements this the given class
	 */
	public boolean hasService(DeviceID deviceID,Class serviceClass);
	
	
	/** 
	 * Returns a list of IDs of all Services implementing the given Class or Iterface on the given device
	 * @param deviceID		the devcice to check
	 * @param serviceClass the class of the interface or class to test
	 * @return a list of ServiceIDs of services implementing the given serviceClass
	 */
	public ServiceID[] getServiceIDs(DeviceID deviceID,Class serviceClass);
    
    
    /**
     * Returns a list of IDs of all running Services on the given device
     * @param deviceID  the devcice to check
     * @return a list of ServiceIDs
     */
    public ServiceID[] getServiceIDs(DeviceID deviceID);
    
    
/********************************** Visualization ********************************************/    
    
    /**
     * Returns true, if the service is currently visualized.
     * 
     * @param serviceID
     * @return  true, if visualization is turned on 
     */
    public boolean isVisualized(ServiceID serviceID);
    
    
    /**
     * Returns true, if the service on the given device is currently visualized.
     * @param deviceID
     * @param serviceID
     * @return  true, if visualization is turned on
     */
    public boolean isVisualized(DeviceID deviceID, ServiceID serviceID);
    
    
    /**
     * Turns the visualization of a service on or off
     * 
     * @param serviceID
     * @param visualize
     */
    public void setVisualized(ServiceID serviceID, boolean visualize);
    
    
    /**
     *  Turns the visualization of a service on the given device on or off
     * 
     * @param deviceID
     * @param serviceID
     * @param visualize
     */    
    public void setVisualized(DeviceID deviceID,ServiceID serviceID, boolean visualize);
	

	
    	
/*****************************************************************************************/    	
    /**
     * Returns the deviceID of the calling Service. 
     * If a Signal from an other device has been received this method returns the id of the other device.
     * In all other cases the own deviceID is returned
     * @return	the deviceID of the device caused the current handler mehtod 
     */
	public DeviceID getCallingDeviceID();
	
	
	
	// TODO: String angeben f�r den Grund??
    /**
     * Finishes the simulation
     */
	public void finishSimulation();
	
	/** 
	 * Gets the exact simulation time. Possily differs from the simulated real time on a device
	 * @return	the simulation time
	 */
	public double getSimulationTime();
	
	
	/**
	 * Returns an object containing simulation global knowledge.
	 * E.g. ids of all simulated devices, the currently connected neighbors,...
	 * TODO: to be replaced with GlobalServices providing these information
	 * @return the object containing some simulation global knowledge 
	 */
	public GlobalKnowledge getGlobalKnowledge();

	/**
	 * Register at a service on the device with the given deviceID which has to be derived from the given interface or class type.
	 * @param deviceID		the ID of the device
	 * @param serviceID 	the ID of the service to register at
	 * @param serviceType 	the super class or interface of the service   
	 */
	public void registerAtService(DeviceID deviceID, ServiceID serviceID, Class serviceType);

	/**
	 * Unregister the calling service at a service on the given device where it was previously registered.
	 * @param deviceID		the ID of the device
	 * @param serviceID the ID of the service where to deregister
	 * @param serviceType 	the super class or interface of the service
	 */
	public void unregisterAtService(DeviceID deviceID, ServiceID serviceID, Class serviceType);

	
	/**
     * Synchronous access method for accessing other Services on other devices directly
     * Normally used by a Service Stub.
     * @param deviceID			The ID of the device hosting the service
     * @param requestedListener 	The ID of the <code>SignalListener</code> to be accessed synchronously
     * @param listenerAccess		The access object
     * @return Object			The return object of the synchronous access
     */
	public Object accessSynchronous(DeviceID deviceID, ListenerID requestedListener, ListenerAccess listenerAccess);


	
	/**
	 * Send a  signal to all <code>SignalListener</code> on the given device matching to the signal receiver class 
	 * which has previously been registered at the sending service.
	 * @param receiverDevice		The ID of the device hosting the services
	 * @param signal 				The signal to be sent
	 */
	public void sendSignal(DeviceID receiverDevice, Signal signal);
	

	
	/**
	 * Send a Request to the Service with the ID serviceID on the given device and register a Replyhandler object.
	 * @param receiverDevice		The ID of the device hosting the service
	 * @param receiverService		The ID of the receiver service
	 * @param requestCallbackPair	The pair containig the request access object and the reply handler object
	 * @deprecated use registerOneShotListener() and sendSignal(DeviceID,ServiceID,Signal) instead 
	 */
	//public void sendRequest(DeviceID receiverDevice, ServiceID receiverService, DeviceRequestCallbackPair requestCallbackPair);

	
	/**
	 * Send a signal to the <code>SignalReceiver</code> identified by the ID signalReceiver on the given device.
	 * @param receiverDevice	The ID of the device hosting the signal listener
	 * @param signalReceiver	The ID of the receiving signal listener
	 * @param signal 	The signal to be sent
	 */
	public void sendSignal(DeviceID receiverDevice, ListenerID signalReceiver, Signal signal);
	
	/**
	 * send an <code>ServiceEvent</code> to the given device
	 * @see RuntimeEnvironment.sendEvent()
	 * @param receiverDevice	The ID of the device where the event sould be propagated
	 * @param event 	The event to be sent
	 */
	public void sendEvent(DeviceID receiverDevice, ServiceEvent event);
	
	/**
	 * Finish the listener with the given id which has been registered on the given device
     * @param receiveDevice
     * @param listenerID
     */
    public void finishListener(DeviceID receiveDevice, ListenerID listenerID);
    /**
     * Finish the given listener on the given device. All assigned listenerIDs are removed.
     * If the given listener is an automatic generated stub, only the stub and the assigned ID is removed.  
     * @param deviceID
     * @param signalListener
     */
    public void finishListener(DeviceID deviceID, SignalListener signalListener);
    
    /**
     * Checks wether the given listenerID exists on the given device 
     * @param deviceID      the device to check
     * @param listenerID    the listener to check
     * @return true, if the listener exits
     */
    public boolean hasListener(DeviceID deviceID, ListenerID listenerID);
    
	/**
	 * Adds a finish handler to the <code>SignalListener</code> given by the listenerID on the given device.
	 * The finish method is called when the Listener has been finished.
	 * @param receiverDevice 	the device hosting the listener
	 * @param listenerID		the ListenerID
	 * @param handler			the handler to add
	 */
	public void addListenerHandler(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler);
	
	/**
	 * Removes a finish handler from the <code>SignalListener</code> given by the listenerID on the given device.
	 * @param receiverDevice	the device hosting the listener
	 * @param listenerID		the ListenerID
	 * @param handler			the handler to remove
	 */
	public void removeListenerHandler(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler);
	
	
	
	/**
	 * Starts a Task on the receiving Service running on the given device.
	 * A task is a single signal with possiply multiple replies.
	 * The task is active until it is finnished by the receiving service
	 * @param receiverDevice				The ID of the device hosting the service 
	 * @param receiverService				the ID of the receiver service
	 * @param signalCallbackPair	object containing the signal for starting the task and the callback 
	 * 								object for receiveing the servers replies. 
	 * @return	the handle for the started task
	 * @deprecated use registerListener(Listener) and sendSignal(DeviceID,ListenerID,Signal) instead
	 */
	//public TaskHandle startTask(DeviceID receiverDevice, ServiceID receiverService, DeviceTaskCallbackPair signalCallbackPair);

    /**
     * TODO: comment method 
     *
     * @param globalDeviceID
     * @param globalTask
     * @param listener
     * @deprecated use addListenerHandler(DeviceID, ListenerID, ListenerFinishedHandler) instead
     */
    //public void addTaskListener(DeviceID deviceID, TaskHandle taskHandle, TaskFinishListener listener);
	
    
    /**
     * 
     * TODO: comment method 
     *
     * @param deviceID
     * @param taskHandle
     * @deprecated use removeListenerHandler(DeviceID, ListenerID, ListenerFinishedHandler) instead
     */
    //public void removeTaskListener(DeviceID deviceID, TaskHandle taskHandle);



	/**
     * 
     * TODO Comment method
     * @param receiverDeviceID
     * @param listenerID
     * @param listenerClass
     * @return
	 */
	public Object getSignalListenerStub(DeviceID receiverDeviceID, ListenerID listenerID, Class listenerClass);
    
	/**
     * 
     * TODO Comment method
     * @param receiverDeviceID
     * @param listenerID
     * @param listenerClass
     * @return
	 */
	public Object getAccessListenerStub(DeviceID receiverDeviceID, ListenerID listenerID, Class listenerClass);





  


}
