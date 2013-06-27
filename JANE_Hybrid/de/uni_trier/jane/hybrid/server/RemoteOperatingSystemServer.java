/*****************************************************************************
 * 
 * RemoteOperatingSystemServer.java
 * 
 * $Id: RemoteOperatingSystemServer.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.server; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.RemoteTimeoutID;
import de.uni_trier.jane.hybrid.remote.manager.*;
import de.uni_trier.jane.service.ListenerAccess;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.parameter.todo.DefaultParameters;
import de.uni_trier.jane.service.unit.DefaultServiceUnit;

import java.rmi.*;
import java.util.List;
import java.util.Set;

/**
 * @author goergen
 *
 * TODO comment class
 */
public interface RemoteOperatingSystemServer extends Remote, RemoteSignalManagerServer, LocalRemoteShutdownManager{//, RemoteEventDB {

   

	DefaultServiceUnit getDeviceServiceUnit()  throws RemoteException;
	
    /**
     * TODO Comment method
     * @return
     */
    DeviceID getDeviceID() throws RemoteException;

    /**
     * TODO Comment method
     * @return
     */
    double getTime() throws RemoteException;

    /**
     * TODO Comment method
     * @param time
     * @return
     */
    void setTime(double time) throws RemoteException;

    /**
     * TODO Comment method
     * @return
     */
    //double getEnergy() throws RemoteException;

    /**
     * TODO Comment method
     * @param serviceID
     */
    void finishService(ServiceContext callerContext, ServiceID serviceIDToFinish)  throws RemoteException;

    /**
     * TODO Comment method
     * @param text
     */
    void write(String text)  throws RemoteException;

    /**
     * TODO Comment method
     * @param timeoutID
     */
    void setTimeout(RemoteTimeoutID timeoutID,double delta)  throws RemoteException;

    /**
     * TODO Comment method
     * @param timeoutID
     */
    void removeTimeout(RemoteTimeoutID timeoutID) throws RemoteException;




    /**
     * TODO Comment method
     * @param hostedService
     */
    void denyAllServices(ServiceContext hostedService) throws RemoteException;


    /**
     * TODO Comment method
     * @param hostedService
     * @param service
     */
    void denyService(ServiceContext hostedService, ServiceID service) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param service
     */
    void allowService(ServiceContext hostedService, ServiceID service) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     */
    void allowAllServices(ServiceContext hostedService) throws RemoteException;

    
    /**
     * TODO: comment method 
     * @param context
     * @param receiverServiceClass
     */
    List getRegisteredListeners(ServiceContext context, Class receiverServiceClass)  throws RemoteException;
    /**
     * TODO Comment method
     * @param hostedService
     * @param serviceID
     * @param serviceType
     * @param serviceType
     */
    void registerAtService(ServiceContext registeredService, ListenerID listenerID, Class listenerType, Class serviceType)  throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param serviceID
     * @param serviceType
     */
    void unregisterAtService(ServiceContext registerService, ListenerID serviceID, Class serviceType) throws RemoteException;



    /**
     * @param runningService
     * @param receivingService
     * @param serviceRequest
     */
    //ReplyHandle sendRequest(ServiceID runningService, ServiceID receivingService, ServiceRequest serviceRequest) throws RemoteException;

    /**
     * @param runningService
     * @param handle
     * @param callback
     */
    //void sendReply(ServiceID runningService, ReplyHandle handle, ServiceReply callback) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param handle
     * @return
     */
    //boolean disableCallback(ServiceID hostedService, TaskHandle handle)throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param handle
     * @return
     */
    //boolean enableCallback(ServiceID hostedService, TaskHandle handle) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param handle
     */
    //void finishTask(ServiceID hostedService, TaskHandle handle) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param handle
     * @param callback
     */
    //void sendCallback(ServiceID hostedService, TaskHandle handle, ServiceCallback callback) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param receivingService
     * @param serviceTask
     * @return
     */
    //TaskHandle startTask(ServiceID hostedService, ServiceID receivingService, ServiceTask serviceTask) throws RemoteException;

    /**
     * TODO Comment method
     * @param hostedService
     * @param requestedService
     * @param listenerAccess
     * @return
     */
    //Object accessSynchronous(ServiceContext hostedService, ListenerID requestedService, ListenerAccess listenerAccess)throws RemoteException;

	/**
	 * @param serviceID
	 * @param params
	 * @param visualize
	 */
	void addService(ServiceContext callerContext, ServiceContext serviceContext, DefaultParameters params, Class serviceClass, boolean visualize)throws RemoteException;

	

    /**
     * TODO: comment method 
     * @param serviceID
     * @return
     */
    boolean hasService(ServiceID serviceID) throws RemoteException;

    /**
     * TODO: comment method 
     * @param serviceClass
     * @return
     */
    boolean hasService(Class serviceClass) throws RemoteException;

    /**
     * TODO: comment method 
     * @param serviceClass
     * @return
     */
    ServiceID[] getServiceIDs(Class serviceClass) throws RemoteException;

    /**
     * TODO Comment method
     * @return
     */
    ServiceID[] getServiceIDs() throws RemoteException;

    /**
     * TODO Comment method
     * @param receiverServiceID
     * @param senderService
     * @return
     */
    boolean isAllowed(ServiceID receiverServiceID, ServiceID senderService) throws RemoteException;

    /**
     * TODO Comment method
     * @param serviceID
     */
    void endFinish(ServiceID serviceID) throws RemoteException;

	/**
	 * @param context
	 * @param listenerID
	 * @param listenerClass
	 * @return
	 * @throws RemoteException
	 */
	boolean hasSignalListenerStub(ServiceContext context, ListenerID listenerID, Class listenerClass) throws RemoteException;

	/**
	 * @param listenerID
	 * @param listenerClass
	 * @return
	 * @throws RemoteException
	 */
	boolean hasAccessListenerStub(ServiceContext context,ListenerID listenerID, Class listenerClass) throws RemoteException;

	/**
	 * @param listenerID
	 * @param classToRegister
	 */
	void addAccessStub(ListenerID listenerID, Class classToRegister) throws RemoteException;

	/**
	 * @param listenerID
	 * @param classToRegister
	 */
	boolean addSignalStub(ListenerID listenerID, Class classToRegister)throws RemoteException;

	/**
	 * @param context
	 * @param listenerID
	 * @param classToRegister
	 */
	void removeAccessStub(ServiceContext context, ListenerID listenerID, Class classToRegister)throws RemoteException;

	/**
	 * @param context
	 * @param listenerID
	 * @param classToRegister
	 */
	void removeSignalStub(ServiceContext context, ListenerID listenerID, Class classToRegister) throws RemoteException;



}
