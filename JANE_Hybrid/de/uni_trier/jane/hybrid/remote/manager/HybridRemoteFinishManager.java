/*****************************************************************************
 * 
 * HybridFinishManager.java
 * 
 * $Id: HybridRemoteFinishManager.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote.manager;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.operatingSystem.manager.FinishManager;

import java.rmi.RemoteException;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class HybridRemoteFinishManager extends FinishManager {
    private RemoteOperatingSystemServer server;
    
    
    /**
     * Constructor for class <code>HybridRemoteFinishManager</code>
     *
     * @param server
     */
    public HybridRemoteFinishManager(RemoteOperatingSystemServer server) {
        super();
        this.server = server;
    }
    //
    public void finishService(ServiceContext callerContext, ServiceContext executionContext) {
        try {
            server.finishService(callerContext,executionContext.getServiceID());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    //
    public void finishComplete(ServiceID serviceID, ServiceContext callerContext) {
        try {
            server.endFinish(serviceID);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.finishComplete(serviceID, callerContext);
    }
    /**
     * TODO: comment method 
     * @param callerContext
     * @param executionContext
     */
    public void finishRemote(ServiceContext callerContext, ServiceContext executionContext) {
        super.finishService(callerContext, executionContext);
        
    }

}
