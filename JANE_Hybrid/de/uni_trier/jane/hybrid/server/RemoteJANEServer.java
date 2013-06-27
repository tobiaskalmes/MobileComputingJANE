/*****************************************************************************
 * 
 * RemoteJANEServer.java
 * 
 * $Id: RemoteJANEServer.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.hybrid.basetypes.RemoteClientID;
import de.uni_trier.jane.hybrid.remote.RemoteOperatingSystemClient;

import java.rmi.*;


/**
 * @author goergen
 *
 * TODO comment class
 */
public interface RemoteJANEServer extends Remote {
    
    /**
     * TODO Comment method
     * @param remoteStub
     * @return
     */
    RemoteClientID registerRemoteOperatingSystem(RemoteOperatingSystemClient remoteOperatingSystemClient) throws RemoteException;

    
    /**
     * TODO Comment method
     * @param myClientID
     */
    RemoteOperatingSystemServer getOperatingSystemServer(RemoteClientID myClientID) throws RemoteException;
    
    /**
     * TODO Comment method
     * @param myClientID
     * @param force 
     */
    RemoteOperatingSystemServer getOperatingSystemServer(RemoteClientID myClientID, DeviceID deviceID, boolean force) throws RemoteException;


    /**
     * TODO Comment method
     * @param myClientID
     */
    void deregisterClient(RemoteClientID myClientID)throws RemoteException;


}
