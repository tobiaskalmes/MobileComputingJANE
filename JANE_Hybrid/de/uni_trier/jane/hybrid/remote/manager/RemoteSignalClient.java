/*****************************************************************************
 * 
 * RemoteSignalClient.java
 * 
 * $Id: RemoteSignalClient.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote.manager; 

import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;

import java.rmi.*;
import java.util.Collection;
import java.util.List;

/**
 * @author daniel
 *
 * TODO comment class
 */
public interface RemoteSignalClient extends Remote{

    /**
     * TODO Comment method
     * @param context
     * @param receiver
     * @param signal
     */
    void sendRemoteSignal(ServiceContext context, ListenerID receiver, Signal signal) throws RemoteException;

    /**
     * TODO Comment method
     * @param context
     * @param signal
     * @param listenerIDs
     */
    void sendRemoteSignal(ServiceContext context, Signal signal, Collection listenerIDs) throws RemoteException;

    /**
     * TODO Comment method
     * @param callerContext
     * @param receiver
     */
    void finish(ServiceContext callerContext, ListenerID receiver) throws RemoteException;

}
