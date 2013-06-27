/*****************************************************************************
 * 
 * RemoteOperatingSystem.java
 * 
 * $Id: HybridRemoteDeviceServiceManager.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.Console;


import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;

import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.visualization.shapes.*;


import java.rmi.RemoteException;
import java.util.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class HybridRemoteDeviceServiceManager extends DeviceServiceManager implements HybridRemoteDeviceServiceManagerI {

    
    
    private RemoteOperatingSystemServer remoteOperatingSystem;
    
    


    /**
     * 
     * Constructor for class <code>HybridRemoteDeviceServiceManager</code>
     * @param executionManager
     * @param localSignalManager
     * @param eventDB
     * @param finishManager
     * @param shutdownManager
     * @param timeoutManager
     * @param clock
     * @param deviceID
     * @param distributionCreator
     * @param console
     * @param remoteOperatingSystemServer
     */


    public HybridRemoteDeviceServiceManager(
    		ExecutionManager executionManager,
            LocalSignalManager localSignalManager,  FinishManager finishManager,
            ShutdownManager shutdownManager, TimeoutManager timeoutManager,
            Clock clock, DeviceID deviceID,
            DistributionCreator distributionCreator, Console console,
            RemoteOperatingSystemServer remoteOperatingSystemServer
            ) {
        super(executionManager, localSignalManager,  finishManager,
                shutdownManager, timeoutManager, clock, deviceID,
                distributionCreator, console
                );
        this.remoteOperatingSystem=remoteOperatingSystemServer;
        
    }
    
    
    
    

    //
    public boolean hasService(ServiceID serviceID) {

        try {
            return remoteOperatingSystem.hasService(serviceID);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    //
    public boolean hasService(Class serviceClass) {
        try {
            return remoteOperatingSystem.hasService(serviceClass);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    //
    public ServiceID[] getServiceIDs(Class serviceClass) {
        try {
            return remoteOperatingSystem.getServiceIDs(serviceClass);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //
    public ServiceID[] getServiceIDs() {
        try {
            return remoteOperatingSystem.getServiceIDs();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

   
    
   
    
    


    //
    public Shape getServiceShape() {
        // TODO Auto-generated method stub
        throw new IllegalAccessError("schould not be called");
    }


    public ServiceID startService(ServiceContext callerContext, Service service,
            Class serviceClass, ServiceID serviceID, boolean visualize) {
        
      if (!isShuttingDown) {
			RemoteServiceInformation serviceThread = new RemoteServiceInformation(remoteOperatingSystem,new ServiceContext(
					serviceID, deviceID),service, serviceClass,this);
			if (idServiceThreadMap.put(serviceThread.getServiceID(),
					serviceThread) != null) {
				throw new OperatingServiceException(
						"The given service ID already exists.");
			}

			Action action = new StartServiceAction(serviceThread.getContext(),
					callerContext);
            signalManager.registerListener(serviceThread.getContext(),service,serviceThread.getServiceID());    
            
			executionManager.schedule(action);
		
    
    
        	DefaultParameters parameters=new DefaultParameters();
        	service.getParameters(parameters);
        	try {
            	remoteOperatingSystem.addService(callerContext,new ServiceContext(serviceID,deviceID),parameters,serviceClass,visualize);
        	} catch (RemoteException e) {
            // 	TODO Auto-generated catch block
            	e.printStackTrace();
        	}
        	return serviceID;
      	}
        return null;    
        
        
    }

  


  


   

 

  

    //
    public boolean isAllowed(ServiceID receiverServiceID, ServiceID senderService) {

        try {
            return remoteOperatingSystem.isAllowed(receiverServiceID,senderService);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    
    public ServiceInformation getServiceInformation(ServiceContext serviceContext) {
    	ServiceInformation serviceInformation=super.getServiceInformation(serviceContext);
    	if (serviceInformation==null){
    		serviceInformation= new RemoteServiceInformation(remoteOperatingSystem,serviceContext,null,null,this);
    	}
    	return serviceInformation;
      
    }

    public Shape getServiceShape(ServiceID serviceID) {
        
        if (super.hasService(serviceID)){
            
            final RemoteServiceInformation serviceThread=(RemoteServiceInformation) super.getServiceInformation(new ServiceContext(serviceID,deviceID));
            
            ServiceContext callerContext=new ServiceContext(serviceID,deviceID);
           executionManager.schedule(new Action(callerContext,callerContext){
                    //
                  public void execute(Service executingService) {
                       //ServiceThread serviceThread=(ServiceThread) super.getServiceThread(getExecutingServiceID());
                       serviceThread.prepareShape();

                    }
                });
                return serviceThread.getPreparedShape();
                
        }
        
        return null;
    }
    
    public boolean hasRemoteService() {
        return super.getServiceIDs().length>0;
    }

}
