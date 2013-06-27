/*****************************************************************************
 * 
 * SimulationDeviceServiceManager.java
 * 
 * $Id: SimulationGlobalDeviceServiceManager.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.operating_system; 

import de.uni_trier.jane.basetypes.AddressDeviceMapping;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDIterator;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.OperatingServiceException;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.basetypes.RuntimeClock;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.kernel.ShutdownAnnouncer;
import de.uni_trier.jane.simulation.kernel.TerminalCondition;
import de.uni_trier.jane.simulation.kernel.eventset.EventSet;
import de.uni_trier.jane.simulation.operating_system.manager.*;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.simulationl.visualization.console.ConsoleTextBuffer;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class SimulationGlobalDeviceServiceManager extends SimulationDeviceServiceManager {

    

	
    /**
     * 
     * Constructor for class <code>SimulationGlobalDeviceServiceManager</code>
     * @param globalDeviceID
     * @param globalKnowledge
     * @param initializer 
     * @param eventSet
     * @param distributionCreator
     * @param runtimeClock
     * @param scheduleManager
     * @param i 
     * @param processingTime
     * @param simulationShutdownAnnouncer
     * @param consoleTextBuffer
     * @param deviceKnowledge
     * @param addressDeviceMapping
     * @param shutdownManager
     * @param isSynchronizedWithGUI
     */
    public SimulationGlobalDeviceServiceManager(
    		DeviceID globalDeviceID,
            GlobalKnowledge globalKnowledge,
            
            
            DefaultSimulationParameters initializer,
            RuntimeClock runtimeClock, 
            ActionScheduler scheduleManager,
            
            ShutdownAnnouncer simulationShutdownAnnouncer,
            ConsoleTextBuffer consoleTextBuffer, 
            DeviceKnowledge deviceKnowledge,
            AddressDeviceMapping addressDeviceMapping, 
            ShutdownManager shutdownManager
            
            ) { 
        super(
        		globalDeviceID, 
        		globalDeviceID, 
        		globalKnowledge, 
        		initializer,
                runtimeClock, 
                scheduleManager, 
                simulationShutdownAnnouncer, 
                consoleTextBuffer,
                deviceKnowledge, 
                addressDeviceMapping,
                new GlobalSignalManager(deviceKnowledge, globalKnowledge,initializer.getEventReflectionDepth()),
				new FinishManager(),
                
				shutdownManager
				);

        finishManager.addFinishListener(new FinishListener() {
            
             public void notifyFinished(ServiceID serviceID,
                     ServiceContext finishContext) {
                 //cleaning up at all existing devices...
                 DeviceIDIterator deviceIDIterator=SimulationGlobalDeviceServiceManager.this.globalKnowledge.getNodes().iterator();
                 while (deviceIDIterator.hasNext()){
                     SimulationGlobalDeviceServiceManager.this.deviceKnowledge.getSignalManager(deviceIDIterator.next())
                     	.finish(new ServiceContext(serviceID,deviceID),serviceID);
                 }

             }
         });
        

    }
    protected void handleStartService(Service service,
            ServiceID executingServiceID) {
   
        
        if (service instanceof GlobalService){
            final ServiceThread  serviceThread=(ServiceThread) idServiceThreadMap.get(executingServiceID);
            GlobalService runtimeService = (GlobalService)service;
            SimulationOperatingSystemImpl operatingSystem;
            if (initializer.isSynchronizedWithGUI()) {
            	operatingSystem = new SyncSimulationOperatingSystemImpl(
            				serviceThread, 
                			distributionCreator, 
                			timeoutManager,
                			executionManager,
                			this,
                			signalManager,
                			finishManager,
                			shutdownManager,
                			console,
                			clock,
                			simulationClock,
                			energyManager,
                			addressDeviceMapping,
                			globalDeviceID, 
                			globalKnowledge,
                			deviceKnowledge,
                			simulationShutdownAnnouncer,
                			initializer.getEventSet()
            				);
            }
            else {
            	operatingSystem = new SimulationOperatingSystemImpl(
                			serviceThread, 
                			distributionCreator, 
                			timeoutManager,
                			executionManager,
                			this,
                			signalManager,
                			finishManager,
                			shutdownManager,
                			console,
                			clock,
                			simulationClock,
                			energyManager,
                			addressDeviceMapping,
                			globalDeviceID, 
                			globalKnowledge,
                			deviceKnowledge,
                			simulationShutdownAnnouncer
                			);
            }
            serviceThread.setOperatingSystem(operatingSystem);

            // register service for receiving signals from all devices
//            DeviceIDIterator deviceIDIterator=globalKnowledge.getNodes().iterator();
//            while (deviceIDIterator.hasNext()){
//                deviceKnowledge.getSignalManager(deviceIDIterator.next())
//                	.registerListener(serviceThread.getContext(),runtimeService,executingServiceID);
//            }
//            globalKnowledge.addDeviceListener(new DeviceListener() {
//                public void enter(DeviceID deviceID) {
//                    deviceKnowledge.getSignalManager(deviceID)
//                	.registerListener(serviceThread.getContext(),serviceThread.getService(),serviceThread.getServiceID());
//
//                }
//                public void exit(DeviceID deviceID) {/*ignored*/}
//                public void changeTrack(DeviceID deviceID, TrajectoryMapping trajectoryMapping, boolean suspended) {/*ignored*/}
//            });
            
                        
            runtimeService.start(operatingSystem);
        }else throw new OperatingServiceException("Only global services are allowed to be started on a global device");
//        ServiceThread  serviceThread=(ServiceThread) idServiceThreadMap.get(executingServiceID);
//        if (service instanceof GlobalService){
//            GlobalService runtimeService = (GlobalService)service;
//            runtimeService.start(new SimulationOperatingSystemImpl(serviceThread, distributionCreator, timeoutManager,executionManager,
//    		        this,signalManager,finishManager,shutdownManager,console,clock,simulationClock,energyManager,
//    		        addressDeviceMapping,globalDeviceID, globalKnowledge,deviceKnowledge));
//        }
//		
		
		
        
    }

}
