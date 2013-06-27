/*****************************************************************************
 * 
 * RemoteSignalManagerServer.java
 * 
 * $Id: RemoteSignalManagerServer.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.hybrid.basetypes.ListenerHandlerID;
import de.uni_trier.jane.service.ListenerAccess;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;

import java.rmi.*;
import java.util.List;

/**
 * @author daniel
 *
 * TODO comment class
 */
public interface RemoteSignalManagerServer extends Remote{

    /**
     * 
     * TODO Comment method
     * @param listenerID
     * @param id
     * @throws RemoteException
     */
    void addFinishListener(ListenerID listenerID, ListenerHandlerID id) throws RemoteException;

    /**
     * 
     * TODO Comment method
     * @param currentContext
     * @param listenerID
     * @param listenerClass
     * @param oneShot
     * @return
     * @throws RemoteException
     */
    boolean registerListener(ServiceContext currentContext, ListenerID listenerID, Class listenerClass, boolean oneShot) throws RemoteException;

    /**
     * TODO Comment method
     * @param senderContext
     * @param receiver
     * @param signal
     */
    void sendSignal(ServiceContext senderContext, ListenerID receiver, Signal signal) throws RemoteException;

    /**
     * 
     * TODO Comment method
     * @param senderContext
     * @param listenerIDs
     * @param signal
     * @throws RemoteException
     */
    void sendSignal(ServiceContext senderContext, List listenerIDs, Signal signal) throws RemoteException;

    /**
     * TODO Comment method
     * @param receiver
     * @param callerContext
     */
    void finish(ListenerID receiver, ServiceContext callerContext) throws RemoteException;

    /**
     * TODO Comment method
     * @param listenerID
     * @param id
     */
    void removeFinishListener(ListenerID listenerID, ListenerHandlerID id) throws RemoteException;

    /**
     * TODO Comment method
     * @param currentContext
     * @param listenerID
     */
    void unregisterListener(ServiceContext currentContext, ListenerID listenerID) throws RemoteException;

    /**
     * TODO: comment method 
     * @param listenerID
     * @return
     */
    boolean hasListener(ListenerID listenerID) throws RemoteException;

	/**
	 * @param callerContext
	 * @param requestedListener
	 * @param listenerAccess
	 * @return
	 * @throws RemoteException
	 */
	Object accessSynchronous(ServiceContext callerContext, ListenerID requestedListener, ListenerAccess listenerAccess) throws RemoteException;

	/**
	 * @param listenerID
	 * @return
	 */
	ServiceContext getContextForListener(ListenerID listenerID) throws RemoteException;

    /**
     * TODO Comment method
     * @param context
     * @param class1
     * @return
     */
    ListenerID addAutogenListener(ServiceContext context, Class listenerClass)throws RemoteException;

    boolean sendEvent(ServiceContext senderContext, ServiceEvent serviceEvent, Class senderClass)throws RemoteException;

    void registerEventListener(ServiceContext context, ServiceEvent eventByExample, ListenerID listenerID)throws RemoteException;

}
