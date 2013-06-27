/*****************************************************************************
 * 
 * RemoteTimeoutManager.java
 * 
 * $Id: HybridTimeoutManager.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.hybrid.basetypes.RemoteTimeoutID;
import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.Action;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.util.HashMapSet;

import java.rmi.RemoteException;
import java.util.*;
import java.util.HashMap;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class HybridTimeoutManager implements TimeoutManager {
    private long id;
    private RemoteOperatingSystemServer remoteOperatingSystemServer;
    private HashMap idTimeoutMap;
    private HashMap timeoutIdMap;
    //private ServiceInformation serviceInformation;
    private ExecutionManager executionManager;
    private HashMapSet serviceIDTimeoutMap;
    
    
    /**
     * 
     * Constructor for class <code>HybridTimeoutManager</code>
     * @param remoteOperatingSystemServer
     */
    public HybridTimeoutManager(RemoteOperatingSystemServer remoteOperatingSystemServer) {
        this.remoteOperatingSystemServer=remoteOperatingSystemServer;
        serviceIDTimeoutMap=new HashMapSet();
        
        timeoutIdMap=new HashMap();
        idTimeoutMap=new HashMap();
        

    }
    

    public void init(ExecutionManager executionManager,FinishManager finishManager) {
        this.executionManager=executionManager;
        finishManager.addFinishListener(new FinishListener() {
            //
            public void notifyFinished(ServiceID serviceID,
                    ServiceContext finishContext) {
                Set set=serviceIDTimeoutMap.get(serviceID);
                if (set!=null){
                    Iterator iterator=set.iterator();
                    while (iterator.hasNext()) {
                        removeTimeout((ServiceTimeout) idTimeoutMap.get(iterator.next()));
                        //RemoteTimeoutID element = (RemoteTimeoutID) iterator.next();
                        //timeoutIdMap.remove(idTimeoutMap.remove(element));
                        
                    }
                }

            }
        });

    }
  
    

    public void setTimeout(ServiceContext serviceContext,
            ServiceTimeout serviceTimeout) {
        RemoteTimeoutID timeoutID=new RemoteTimeoutID(id++, serviceContext);
        idTimeoutMap.put(timeoutID,serviceTimeout);
        timeoutIdMap.put(serviceTimeout,timeoutID);
        serviceIDTimeoutMap.put(serviceContext.getServiceID(),timeoutID);
        try {
            remoteOperatingSystemServer.setTimeout(timeoutID,serviceTimeout.getDelta());
            idTimeoutMap.put(timeoutID,serviceTimeout);
            timeoutIdMap.put(serviceTimeout,timeoutID);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

   
    public void removeTimeout(ServiceTimeout serviceTimeout) {
        RemoteTimeoutID timeoutID=(RemoteTimeoutID)timeoutIdMap.remove(serviceTimeout);
        idTimeoutMap.remove(timeoutID);
        Set set=serviceIDTimeoutMap.get(timeoutID.getServiceContext().getServiceID());
        set.remove(timeoutID);
        if (set.isEmpty()){
            serviceIDTimeoutMap.remove(timeoutID.getServiceContext().getServiceID());
        }
        try {
            remoteOperatingSystemServer.removeTimeout(timeoutID);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public void handleTimeout(RemoteTimeoutID remoteTimeoutID){
        final ServiceTimeout serviceTimeout=(ServiceTimeout)idTimeoutMap.remove(remoteTimeoutID);
        if (serviceTimeout==null) return;
        timeoutIdMap.remove(serviceTimeout);
        Set set=serviceIDTimeoutMap.get(remoteTimeoutID.getServiceContext().getServiceID());
        set.remove(remoteTimeoutID);
        if (set.isEmpty()){
            serviceIDTimeoutMap.remove(remoteTimeoutID.getServiceContext().getServiceID());
        }
        //HybridOperatingSystem operatingSystem = client.getOperatingSystem(executingService);
        executionManager.schedule(new Action(remoteTimeoutID.getServiceContext(),remoteTimeoutID.getServiceContext()) {

            public void execute(Service service) {
                serviceTimeout.handle();        

            }
        });
        
    }


}
