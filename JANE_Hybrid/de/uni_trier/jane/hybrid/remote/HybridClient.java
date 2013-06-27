/*****************************************************************************
 * 
 * HybridClient.java
 * 
 * $Id: HybridClient.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
import de.uni_trier.jane.hybrid.basetypes.*;
import de.uni_trier.jane.hybrid.remote.manager.*;
import de.uni_trier.jane.hybrid.server.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Set;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class HybridClient implements RemoteOperatingSystemClient, DeviceServiceFactory,Remote {
    
    
    protected RemoteSimulationClient initializer;
    protected DefaultHybridParameters parameters;
    protected RemoteJANEServer janeServer;
    private DefaultServiceUnit serviceUnit;
    protected DeviceID deviceID;
    protected RemoteOperatingSystemServer remoteOperatingSystem;
    protected RemoteClientID myClientID;
    protected HybridRemoteDeviceServiceManagerI operatingSystem;
//    
    protected HybridTimeoutManager timeoutManager;
//    private ExecutionManager executionManager;
    protected RemoteSignalManager signalManager;
    protected HybridRemoteFinishManager finishManager;
   //protected HybridServiceManager serviceManager;
    protected ActionHandler executionManager;
    protected HybridRemoteShutdownManager shutdownManager;

    /**
     * Constructor for class HybridClient 
     *
     * @param client the initializer
     */
    public HybridClient(RemoteSimulationClient initializer) {
        this.initializer=initializer;
             
    }
   
	 /**
     * Starts the JANE hybrid client and connect it to a JANE hybrid server running on the given host
     * @param hostname		the host to connect to
     * @throws HybridModeException
     */
	public void run() throws HybridModeException{
		try {
			parameters=new DefaultHybridParameters();
		} catch (UnknownHostException e) {
			throw new HybridModeException(e);
		}
        initializer.initHybrid(parameters); 
	    connectToSimulation(parameters.getJANEHybridServerHost());
	    SyncObject syncObject=new SyncObject();
	    finishManager=new HybridRemoteFinishManager(remoteOperatingSystem);
	    

	    shutdownManager=new HybridRemoteShutdownManager(deviceID,remoteOperatingSystem);
	    
	    executionManager=new ThreadActionHandler(deviceID,shutdownManager,syncObject);
	    
        shutdownManager.addShutdownListener(new DeviceShutdownListener(){
            public void notifyEndShutdown() {
                try {
                    executionManager.shutdown();
                    UnicastRemoteObject.unexportObject(HybridClient.this,true);
                } catch (NoSuchObjectException exeception) {
                    // TODO Auto-generated catch block
                    exeception.printStackTrace();
                }         
            }

            public void notifyBeginShutdown() {
                // TODO Auto-generated method stub
                
            }

            public void notifyStartBoot() {
                // TODO Auto-generated method stub
                
            };
        });
		
		timeoutManager=new HybridTimeoutManager(remoteOperatingSystem);
		signalManager=new RemoteSignalManager(remoteOperatingSystem,syncObject);
			
		HybridRemoteDeviceServiceManager serviceManager=new HybridRemoteDeviceServiceManager(
				executionManager,signalManager, 
                finishManager,shutdownManager,timeoutManager,
		        new HybridClock(remoteOperatingSystem),deviceID,parameters.getDistributionCreator(),
		        new HybridConsole(remoteOperatingSystem,parameters.getDefaultConsole()),remoteOperatingSystem
		    );
		operatingSystem=serviceManager;
		OperatingSystem.setServiceManager(serviceManager);
        
        finishManager.addFinishListener(new FinishListener() {
            
            public void notifyFinished(ServiceID serviceID, ServiceContext finishContext) {
                if (!operatingSystem.hasRemoteService()){
                    shutdown();
                }
        
            }
        
        });
		operatingSystem.setServiceFactory(this);
        operatingSystem.notifyStartBoot();
		executionManager.start();
		
		
	}
	
	
	
	/**
	 * connects the remote client to the simulator on the host given by hostname 
	 * @param address	the name of the remote host
	 *
	 *	@throws HybridModeException
	 */
	protected void connectToSimulation(InetAddress address) throws HybridModeException{
		try {
		    janeServer= (RemoteJANEServer) Naming.lookup("//"+address.getHostName()+"/JANESimulationServer");
			
			

			RemoteOperatingSystemClient remoteStub=(RemoteOperatingSystemClient)UnicastRemoteObject.exportObject((RemoteOperatingSystemClient)this);
			
			myClientID = janeServer.registerRemoteOperatingSystem(remoteStub);
			if (parameters.getDeviceID()!=null){
			    remoteOperatingSystem=janeServer.getOperatingSystemServer(myClientID,parameters.getDeviceID(),parameters.isForceDeviceSelect());
			}else{
			    remoteOperatingSystem=janeServer.getOperatingSystemServer(myClientID);
			    
			}
            deviceID=remoteOperatingSystem.getDeviceID();
			//RemoteUserCommunicationObject remoteCommunicationObject = rmiServer.getRemoteUserCommunicationObject(clientId);
			//if (remoteCommunicationObject!=null){
			//	user.setRemoteUserCommunicationObject(remoteCommunicationObject);
			//}
			serviceUnit=remoteOperatingSystem.getDeviceServiceUnit();

		} catch (MalformedURLException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
			//return false;
			throw new HybridModeException(e);
		} catch (RemoteException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
			//return false;
			throw new HybridModeException(e);
		} catch (NotBoundException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
			//return false;
			throw new HybridModeException(e);
		}



	
    
	}
	

    //
    public void handleTimeout(RemoteTimeoutID timeoutID) throws RemoteException {
        timeoutManager.handleTimeout(timeoutID);
        
    }

    //
    public void receiveSignal(ServiceContext callingServiceContex, ListenerID receiver, Signal signal) throws RemoteException {
        signalManager.sendSignal(callingServiceContex,receiver,signal);
        
    }

    
    public void notifyListenerFinished(ListenerHandlerID pair, ServiceContext callerContext) {
        signalManager.notifyListenerFinished(pair, callerContext);
        
    }




    public void sendRemoteSignal(ServiceContext context, ListenerID receiver, Signal signal) {
        signalManager.sendRemoteSignal(context,receiver,signal);
        
    }




    public void sendRemoteSignal(ServiceContext context, Signal signal, Collection listenerIDs) {
        signalManager.sendRemoteSignal(context,signal,listenerIDs);
        
    }




    public void finish(ServiceContext callerContext, ListenerID receiver) {
        signalManager.finishLocal(receiver,callerContext);
        
    }


    //
    public void handleFinish(ServiceContext callerContext, ServiceContext executionContext) throws RemoteException {
        
        finishManager.finishRemote(callerContext,executionContext);
    }

    //
    public Shape getShape(ServiceID serviceID) throws RemoteException {
        // TODO Auto-generated method stub
        
        return operatingSystem.getServiceShape(serviceID);
    }

    //
    public Object handleAccessSynchronous(ListenerID requestedService, ServiceContext senderService, ListenerAccess serviceAccess) throws RemoteException {
        //executionManager.enterContext(new ServiceContext(senderService,deviceID));
        
        Object object=signalManager.accessSynchronous(senderService,(ServiceID)requestedService,serviceAccess,null);
        //executionManager.leaveContext();
        return object;
    }


    public boolean ping() throws RemoteException {
        //pong...
        return true;
    }

    public void simulationShutdown() throws RemoteException {
      //  shutdownManager.shutdown(new ServiceContext(OperatingSystem.OperatingSystemID,deviceID));

       

    }

    public ServiceCollection getServiceCollection() {
        ServiceCollection serviceCollection=new ServiceCollection();
		ServiceUnit remoteServiceUnit=serviceUnit.copy(deviceID,serviceCollection);
		initializer.initServices(remoteServiceUnit);
        return serviceCollection;
    }


    //
    public ServiceID checkServiceID(Service service) {
        
        return serviceUnit.checkServiceID(service);
    }

    
    /**
     * TODO Comment method
     * 
     */
    public void shutdown() {
        try {
            janeServer.deregisterClient(myClientID);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
        
    }

}
