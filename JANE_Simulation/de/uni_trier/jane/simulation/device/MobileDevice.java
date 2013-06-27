/*****************************************************************************
 * 
 * MobileDevice.java
 * 
 * $Id: MobileDevice.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.device;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.hybrid.remote.*;
import de.uni_trier.jane.hybrid.remote.manager.*;
import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;

import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.*;
import de.uni_trier.jane.simulation.operating_system.manager.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A mobile device hosts the user, the application and all protocols.
 */
public class MobileDevice implements Visualizable, DeviceServiceFactory {

	private final static String VERSION = "$Id: MobileDevice.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";




	private TrajectoryMapping trajectoryMapping;
	private DoubleMapping sendingRadius;

	protected Map addressConnectedMap;
	
	private boolean suspended;

    private DeviceServiceManager serviceManager;

    

    private Shape deviceDefaultShape;








	private DefaultServiceUnit globalServiceUnit;




	private SimulationServiceFactory serviceFactory;




	private DefaultServiceUnit deviceServiceUnit;

    

	private AddressDeviceMapping addressDeviceMapping;




    private HybridOperatingSystemServer operatingSystemServer;




    private ShutdownManager shutdownManager;




    private EventSet eventSet;




    
 
	
	/**
     * 
     * Constructor for class <code>MobileDevice</code>
     * @param deviceID
     * @param globalDeviceID
     * @param globalKnowledge
     * @param deviceKnowledge
	 * @param initializer 
     * @param eventSet
     * @param distributionCreator
     * @param consoleTextBuffer
     * @param simulationShutdownAnnouncer
     * @param serviceFactory
     * @param globalServiceUnit
     * @param isHybrid
     * @param addressDeviceMapping
	 */
    public MobileDevice(DeviceID deviceID, DeviceID globalDeviceID, GlobalKnowledge globalKnowledge, DeviceKnowledge deviceKnowledge, 
             DefaultSimulationParameters initializer, ConsoleTextBuffer consoleTextBuffer, 
            ShutdownAnnouncer simulationShutdownAnnouncer, SimulationServiceFactory serviceFactory, DefaultServiceUnit globalServiceUnit,  AddressDeviceMapping addressDeviceMapping) {
        
    	this.addressDeviceMapping = addressDeviceMapping;
    	
        //TODO initialize globally!
		RuntimeClock clockDriftCalculator = new ExactClock(new SimulationClock(initializer.getEventSet()));
		
		ActionScheduler scheduleManager = new ActionScheduler();
		shutdownManager=new ShutdownManager(deviceID);
		if (initializer.isHybrid()){
		
			serviceManager=new HybridSimulationDeviceServiceManager(
					globalDeviceID,deviceID,
					globalKnowledge,initializer,clockDriftCalculator,scheduleManager,
                    simulationShutdownAnnouncer,consoleTextBuffer,deviceKnowledge, addressDeviceMapping,shutdownManager);
		}else{
			serviceManager=new SimulationDeviceServiceManager(
				globalDeviceID,deviceID,
		        globalKnowledge,initializer,clockDriftCalculator,scheduleManager,
                simulationShutdownAnnouncer,consoleTextBuffer,deviceKnowledge, addressDeviceMapping,shutdownManager);
		}
		
		serviceManager.setServiceFactory(this);
				//new DeviceServiceFactory(serviceUnit, serviceFactory, this));
		this.globalServiceUnit=globalServiceUnit;
		this.serviceFactory=serviceFactory;
        
        eventSet=initializer.getEventSet();
        
		

		addressConnectedMap = new HashMap();
	}
	

    

    
	


	/**
     * Constructor for class <code>MobileDevice</code>
	 * @param shutdownManager
     * @param globalOperatingSystem
     */
    public MobileDevice(SimulationGlobalDeviceServiceManager globalDeviceServiceManager, ShutdownManager shutdownManager) {
        
        serviceManager=globalDeviceServiceManager;
        this.shutdownManager=shutdownManager;
    }








    /**
	 * Get the current trajectory of the device.
	 * @return the trajectory
	 */
	public Trajectory getTrajectory() {
		return trajectoryMapping.getValue(eventSet.getTime());
	}



	/**
	 * @see de.uni_trier.jane.visualization.Visualizable#getShape()
	 */
	public Shape getShape() {
	    
	    if (deviceDefaultShape!=null){
	        ShapeCollection shapeCollection=new ShapeCollection();
	        shapeCollection.addShape(deviceDefaultShape,Position.NULL_POSITION);
		    shapeCollection.addShape(serviceManager.getServiceShape(),Position.NULL_POSITION);
			return shapeCollection;
	    }
	    return serviceManager.getServiceShape();
	    
	}

	/**
	 * Notify this device that it enter the simulation.
	 * @param trajectoryMapping the current trajectory mapping for this device
	 * @param suspended
	 * @param sendingRadius the current sending radius for this device
	 */
	public void notifyEnter(TrajectoryMapping trajectoryMapping, boolean suspended, DoubleMapping sendingRadius) {
	   // addressDeviceMapping.enterDevice(getDeviceID());
	    serviceManager.notifyStartBoot();
		this.trajectoryMapping = trajectoryMapping;
		this.suspended=suspended;
		this.sendingRadius = sendingRadius;

	}

	/**
	 * Notify this device that it left the simulation.
	 */
	public void notifyExit() {
	    //if (operatingSystem instanceof SimulationLocalOperatingSystem){
	    //   ((SimulationLocalOperatingSystem) operatingSystem).shutdown();
	    //serviceManager.shutdown();
	    shutdownManager.shutdown(new ServiceContext(OperatingSystem.OperatingSystemID,getDeviceID()));
	    
	   // addressDeviceMapping.exitDevice(getDeviceID());

        
////////////////////////////        
//	    if (operatingSystemServer!=null){
//            
//	           operatingSystemServer.simulationShutdown();
//	           endHybrid();
//	    }
	   
	}


	/**
	 * Notify a unidirectional link attach.
	 * @param sender the address of the sending device
	 */
	public void notifyAttachUnidirectional(DeviceID sender) {
		addressConnectedMap.put(sender, new Boolean(false));
		
	}


	/**
	 * Notify a unidirectional link detach.
	 * @param sender the address of the sending device
	 */
	public void notifyDetachUnidirectional(DeviceID sender) {
		addressConnectedMap.remove(sender);
	}	


	/**
	 * Notify that the track of this device has changed.
	 * @param trajectoryMapping the new track
	 * @param suspended
	 */
	public void notifyChangeTrack(TrajectoryMapping trajectoryMapping, boolean suspended) {
		this.trajectoryMapping = trajectoryMapping;
		this.suspended=suspended;
	}
	
	/**
	 * Notify that the sending radius of this device has changed.
	 * @param sendingRadius the new sending radius 
	 */
	public void notifyChangeSendingRadius(DoubleMapping sendingRadius) {
		this.sendingRadius = sendingRadius;
	}




	/**
	 * Get all currently known neighbours.
	 * @return an iterator over all neighbour device addresses 
	 */
	public DeviceIDSet getNeighbours() {
	    return new MutableDeviceIDSet(addressConnectedMap.keySet());
	}



	/**
	 * Returns true if the device is currently suspended - i.e. the wireless interface is deactivated
	 * @return	true if wireless is deactivated
	 */
	public boolean isSuspended() {

		return suspended;
	}








    /**
     * @return
     */
    public double getSendingRadius() {
        return sendingRadius.getValue(eventSet.getTime());
    }








    /**
     * @return
     */
    public DeviceID getDeviceID() {
        return serviceManager.getDeviceID();
    }








    /**
     * @return
     */
    public DeviceServiceManager getServiceManager() {

        return serviceManager;
    }








    /**
     * @param deviceDefaultShape
     */
    public void setDefaultShape(Shape deviceDefaultShape) {
        this.deviceDefaultShape=deviceDefaultShape;
        
    }








	/**
	 * @param client
	 * @return
	 * @throws RemoteException
	 */
	public RemoteOperatingSystemServer setHybrid(RemoteOperatingSystemClient client,SyncObject syncObject, UnexpectedShutdownListener listener) throws RemoteException {
		operatingSystemServer=
			new HybridOperatingSystemServer(
				syncObject,
				(HybridSimulationDeviceServiceManager)serviceManager,
				client,eventSet,deviceServiceUnit,listener,shutdownManager);
			
		return (RemoteOperatingSystemServer)UnicastRemoteObject.exportObject(operatingSystemServer);
		
	}
	 /**
     * TODO Comment method
     * 
     */
    public void endHybrid() {
        try {
            UnicastRemoteObject.unexportObject(operatingSystemServer,true);
        } catch (NoSuchObjectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }



	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.operating_system.DeviceServiceFactory#getServiceUnit()
	 */
	//public DefaultServiceUnit getRemoteServiceUnit() {
		
	//	return deviceServiceUnit.copy(operatingSystem.getDeviceID(),null);
	//}








	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.operating_system.DeviceServiceFactory#getServiceCollection()
	 */
	public ServiceCollection getServiceCollection() {
		ServiceCollection serviceCollection = new ServiceCollection();
    	deviceServiceUnit = globalServiceUnit.copy(getDeviceID(), serviceCollection);
        serviceFactory.initServices(deviceServiceUnit);
        deviceDefaultShape=deviceServiceUnit.getDefaultShape();
		return serviceCollection;
	}



    public ServiceID checkServiceID(Service service) {
        return deviceServiceUnit.checkServiceID(service);
        
    }
    /**
     * TODO Comment method
     */
    public ShutdownManager getShutdownManager() {
        return shutdownManager;
        
    }

 
}
