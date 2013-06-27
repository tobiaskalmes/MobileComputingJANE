/*
 * Created on 21.01.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.hybrid.local;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.RemoteClientID;
import de.uni_trier.jane.hybrid.remote.RemoteOperatingSystemClient;
import de.uni_trier.jane.hybrid.server.*;
import de.uni_trier.jane.simulation.ShutdownListener;
import de.uni_trier.jane.simulation.device.*;
import de.uni_trier.jane.simulation.global_knowledge.DeviceListener;
import de.uni_trier.jane.simulation.kernel.ShutdownAnnouncer;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RemoteJANEServerImplementation extends UnicastRemoteObject implements RemoteJANEServer,Runnable, ShutdownListener {
	
	private DeviceManager deviceManager;
	private SyncObject syncObject;
	protected Set allDevices;
	protected Set freeDevices;
	private int firstFreeClientID;
	private Map clientMap;
	private Thread janeServerThread;
    private Map clientDeviceMap;
	
	
	
	

	/**
	 * @param deviceManager
	 * @param syncObject
	 * @param shutdownAnnouncer
	 * @throws RemoteException
	 */
	public RemoteJANEServerImplementation(DeviceManager deviceManager,
			final SyncObject syncObject, ShutdownAnnouncer shutdownAnnouncer) throws RemoteException {
		super();
		shutdownAnnouncer.addShutdownListener(this);
		this.deviceManager = deviceManager;
		this.syncObject = syncObject;
		allDevices=new LinkedHashSet();
		freeDevices=new LinkedHashSet();
		clientMap=new HashMap();
		clientDeviceMap=new HashMap();
		
		firstFreeClientID=0;
		
		deviceManager.getGlobalKnowledge().addDeviceListener(new DeviceListener() {
			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.global_knowledge.DeviceListener#enter(de.uni_trier.jane.basetypes.DeviceID)
			 */
			public void enter(DeviceID deviceID) {
				synchronized(syncObject){
					allDevices.add(deviceID);
					freeDevices.add(deviceID);
				}

			}

			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.global_knowledge.DeviceListener#exit(de.uni_trier.jane.basetypes.DeviceID)
			 */
			public void exit(DeviceID deviceID) {
				// TODO handle exit device

			}

			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.global_knowledge.DeviceListener#changeTrack(de.uni_trier.jane.basetypes.DeviceID, de.uni_trier.ubi.appsim.kernel.basetype.TrajectoryMapping, boolean)
			 */
			public void changeTrack(DeviceID deviceID,
					TrajectoryMapping trajectoryMapping, boolean suspended) {
				// TODO Auto-generated method stub

			}
		});
	
		janeServerThread=new Thread(this);
		janeServerThread.start();
		
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.local.RemoteJANEServer#registerRemoteOperatingSystem(de.uni_trier.jane.hybrid.RemoteOperatingSystemClient)
	 */
	public RemoteClientID registerRemoteOperatingSystem(
			RemoteOperatingSystemClient remoteOperatingSystemClient)
			throws RemoteException {
	    synchronized(syncObject){
	        RemoteClientID clientID=new RemoteClientID(firstFreeClientID);
			clientMap.put(clientID,remoteOperatingSystemClient);
			return clientID;
	    }
	}
	
    public void deregisterClient(RemoteClientID clientID) throws RemoteException {
        
        synchronized(syncObject){
            if (!clientMap.containsKey(clientID)) throw new RemoteException("ClientID does not exist");
            clientMap.remove(clientID);
            DeviceID deviceID=(DeviceID)clientDeviceMap.remove(clientID);
            freeDevices.add(deviceID);
            MobileDevice mobileDevice=deviceManager.getMobileDevice(deviceID);
            if (mobileDevice!=null){
                mobileDevice.endHybrid();
            }
        }
        
    }


	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.local.RemoteJANEServer#getOperatingSystemServer(de.uni_trier.jane.hybrid.RemoteClientID)
	 */
	public RemoteOperatingSystemServer getOperatingSystemServer(
			RemoteClientID myClientID) throws RemoteException {
	    synchronized(syncObject){
	        if (freeDevices.isEmpty()) throw new RemoteException("No free devices available");
	        Iterator iterator=freeDevices.iterator();
	        DeviceID deviceID=(DeviceID)iterator.next();
	        
		
	        return getOperatingSystemServer(myClientID,deviceID,true);
	    }
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.local.RemoteJANEServer#getOperatingSystemServer(de.uni_trier.jane.hybrid.RemoteClientID, de.uni_trier.jane.basetypes.DeviceID)
	 */
	public RemoteOperatingSystemServer getOperatingSystemServer(
			final RemoteClientID clientID, DeviceID deviceID, boolean force)
			throws RemoteException {
	    synchronized(syncObject){
            
	        if (!freeDevices.contains(deviceID)){
	            if (force) throw new RemoteException("Remote device is not free");
                return getOperatingSystemServer(clientID);
            }
	        if (!clientMap.containsKey(clientID)) throw new RemoteException("ClientID does not exist");
	        freeDevices.remove(deviceID);
	        clientDeviceMap.put(clientID,deviceID);
	        MobileDevice mobileDevice=deviceManager.getMobileDevice(deviceID);
	        return mobileDevice.setHybrid((RemoteOperatingSystemClient)clientMap.get(clientID),syncObject, new UnexpectedShutdownListener() {
             
                public void shutdown() {
                    try {
                        
                        deregisterClient(clientID);
                    } catch (RemoteException e) {
                        // should not occour...
                        
                    }

                }
            });
	    }
		
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Registry localRegistry;
		try {
			
			
			localRegistry = LocateRegistry.createRegistry(1099);
		}catch (Exception e) {
			
			try {
				localRegistry = LocateRegistry.getRegistry();
			} catch (RemoteException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		try{	
			
			Naming.rebind("JANESimulationServer", this );
		
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
    public void shutdown() {
        
//          Iterator iterator=clientMap.keySet().iterator();
//        while (iterator.hasNext()) {
//            RemoteClientID clientID=(RemoteClientID)iterator.next();
//            RemoteOperatingSystemClient client = (RemoteOperatingSystemClient)clientMap.get(clientID); 
//            try {
//                client.simulationShutdown();
//            } catch (RemoteException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//            
//            
//        }
//        	try {
//                Naming.unbind("JANESimulationServer");
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (NotBoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            
//        }
        
    }


}
