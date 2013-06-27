/*****************************************************************************
 * 
 * RemoteOperatingSystemClient.java
 * 
 * $Id: RemoteOperatingSystemClient.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.*;
import de.uni_trier.jane.hybrid.remote.manager.RemoteSignalClient;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.rmi.RemoteException;

/**
 * @author goergen
 *
 * TODO comment class
 */
public interface RemoteOperatingSystemClient extends RemoteSignalClient{

    /**
     * TODO Comment method
     * @param timeoutID
     * @throws RemoteException
     */
    void handleTimeout(RemoteTimeoutID timeoutID) throws RemoteException;

 

    /**
     * @param handle
     * @param callback
     * @param callbackReceiver
     */
    //void receiveReply(ReplyHandle handle, ServiceReply callback, ServiceID callbackReceiver) throws RemoteException;



    /**
     * TODO Comment method
     * @param handle
     */
    //void receiveFinishTask(TaskHandle handle) throws RemoteException;



    /**
     * TODO Comment method
     * @param handle
     * @param callback
     */
    //void receiveCallback(TaskHandle handle, ServiceCallback callback) throws RemoteException;



    /**
     * TODO Comment method
     * @param callingServiceContex
     * @param receiver
     * @param signal
     * @throws RemoteException
     */
    void receiveSignal(ServiceContext callingServiceContex, ListenerID receiver, Signal signal) throws RemoteException;



    /**
     * TODO Comment method
     * @param serviceID
     * @return
     * @throws RemoteException
     */
    Shape getShape(ServiceID serviceID) throws RemoteException;



    /**
     * TODO Comment method
     * @param callerContext
     * @param executionContext
     * @throws RemoteException
     */
    void handleFinish(ServiceContext callerContext,ServiceContext executionContext) throws RemoteException;



	/**
	 * @param runningService
	 * @param serviceRequest
	 * @param replyHandle
	 */
	//void receiveRequest(ServiceID runningService, ServiceID receivingService, ServiceRequest serviceRequest, ReplyHandle replyHandle) throws RemoteException;



	/**
	 * @param runningService
	 * @param receivingService
	 * @param serviceTask
	 * @param taskHandle
	 */
	//void receiveStartTask(ServiceID runningService, ServiceID receivingService, ServiceTask serviceTask, TaskHandle taskHandle) throws RemoteException;



    /**
     * TODO Comment method
     * @param requestedService
     * @param senderService
     * @param serviceAccess
     * @return
     * @throws RemoteException
     */
    Object handleAccessSynchronous(ListenerID requestedService, ServiceContext senderService, ListenerAccess serviceAccess) throws RemoteException;



    /**
     * TODO Comment method
     * @param pair
     * @param callerContext
     * @throws RemoteException
     */
    void notifyListenerFinished(ListenerHandlerID pair, ServiceContext callerContext) throws RemoteException;



    /**
     * TODO Comment method
     * @return
     * @throws RemoteException
     * 
     */
    boolean ping() throws RemoteException;



    /**
     * TODO Comment method
     * @throws RemoteException
     * 
     */
    void simulationShutdown()  throws RemoteException;

}
