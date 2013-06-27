/*****************************************************************************
 * 
 * SimulationDeviceServiceManager.java
 * 
 * $Id: SimulationDeviceServiceManager.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.hybrid.local.manager.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.SimulationOperatingSystemImpl.*;
import de.uni_trier.jane.simulation.operating_system.manager.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.simulationl.visualization.console.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class SimulationDeviceServiceManager extends DeviceServiceManager {

    
    private static final class ConsoleWrapper implements Console {

        private ConsoleTextBuffer consoleTextBuffer;

        /**
         * Constructor for class ConsoleWrapper 
         *
         * @param consoleTextBuffer
         */
        public ConsoleWrapper(ConsoleTextBuffer consoleTextBuffer) {
            
            this.consoleTextBuffer=consoleTextBuffer;
        }

        //
        public void println(String text) {
            consoleTextBuffer.add(new ConsoleText(text)); // TODO: println und print sind identisch. Eigentlich müsste println einen Zeilenumbruch anhängen!!!
            
        }

        //
        public void print(String text) {
            consoleTextBuffer.add(new ConsoleText(text));
            
        }

        //
        public void println() {
            
            
        }

        }

    protected EnergyManager energyManager;
    protected Clock simulationClock;
    
    protected AddressDeviceMapping addressDeviceMapping;
    protected DeviceID globalDeviceID;
    protected GlobalKnowledge globalKnowledge;
    protected DeviceKnowledge deviceKnowledge;
    private static Map internalFinishListenerMap;
    protected ShutdownAnnouncer simulationShutdownAnnouncer;
    protected DefaultSimulationParameters initializer;




  
    /**
     * Constructor for class <code>SimulationDeviceServiceManager</code>
     * @param globalDeviceID
     * @param deviceID
     * @param globalKnowledge
     * @param initializer
     * @param distributionCreator
     * @param runtimeClock
     * @param scheduleManager
     * @param processingTime
     * @param simulationShutdownAnnouncer
     * @param consoleTextBuffer
     * @param totalEnergy
     * @param deviceKnowledge
     * @param addressDeviceMapping
     * @param shutdownManager
     */
    public SimulationDeviceServiceManager(
    		DeviceID globalDeviceID, DeviceID deviceID, GlobalKnowledge globalKnowledge, 
    		DefaultSimulationParameters initializer,
            RuntimeClock runtimeClock, ActionScheduler scheduleManager, 
            ShutdownAnnouncer simulationShutdownAnnouncer, ConsoleTextBuffer consoleTextBuffer,  
            DeviceKnowledge deviceKnowledge, AddressDeviceMapping addressDeviceMapping, ShutdownManager shutdownManager) {
        this(globalDeviceID, deviceID, globalKnowledge, initializer,runtimeClock, 
				scheduleManager,simulationShutdownAnnouncer, consoleTextBuffer,
				deviceKnowledge,addressDeviceMapping,
				new LocalSignalManager(initializer.getEventReflectionDepth()),
				new FinishManager(),
				shutdownManager);
            
        
    }


    /**
     * 
     * Constructor for class <code>SimulationDeviceServiceManager</code>
     * @param globalDeviceID
     * @param deviceID
     * @param globalKnowledge
     * @param initializer
     * @param distributionCreator
     * @param runtimeClock
     * @param scheduleManager
     * @param processingTime
     * @param simulationShutdownAnnouncer
     * @param consoleTextBuffer
     * @param totalEnergy
     * @param deviceKnowledge
     * @param addressDeviceMapping
     * @param localSignalManager
     * @param eventDB
     * @param finishManager
     * @param shutdownManager
     */
    public SimulationDeviceServiceManager(
    		DeviceID globalDeviceID, DeviceID deviceID, GlobalKnowledge globalKnowledge, 
            DefaultSimulationParameters initializer, RuntimeClock runtimeClock, ActionScheduler scheduleManager, 
             ShutdownAnnouncer simulationShutdownAnnouncer, ConsoleTextBuffer consoleTextBuffer,  
            DeviceKnowledge deviceKnowledge, AddressDeviceMapping addressDeviceMapping, LocalSignalManager localSignalManager,  
            FinishManager finishManager,ShutdownManager shutdownManager) {
        super(new SimulationLocalExecutionManager(deviceKnowledge,deviceID,initializer,scheduleManager), localSignalManager,
                finishManager,shutdownManager,new LocalTimeoutManager(initializer.getEventSet(),runtimeClock),runtimeClock,
                deviceID,initializer.getDistributionCreator(),new ConsoleWrapper(consoleTextBuffer)
                
                );
                
        this.initializer=initializer;
        energyManager =new EnergyManager(initializer);
        simulationClock=new SimulationClock(initializer.getEventSet());
        this.addressDeviceMapping=addressDeviceMapping;
        this.globalDeviceID=globalDeviceID;
        this.globalKnowledge=globalKnowledge;
        this.deviceKnowledge=deviceKnowledge;
        internalFinishListenerMap=new HashMap(); 
        this.simulationShutdownAnnouncer=simulationShutdownAnnouncer;


    }


    protected void handleStartService(Service service,
            ServiceID executingServiceID) {
    	ServiceThread  serviceThread=(ServiceThread) idServiceThreadMap.get(executingServiceID);
        
        if (service instanceof SimulationService){
            
            SimulationService runtimeService = (SimulationService)service;
            SimulationOperatingSystemImpl operatingSystem=new SimulationOperatingSystemImpl(serviceThread, distributionCreator, timeoutManager,executionManager,
    		        this,signalManager,finishManager,shutdownManager,console,clock,simulationClock,energyManager,
    		        addressDeviceMapping,globalDeviceID, globalKnowledge,deviceKnowledge,simulationShutdownAnnouncer);
            serviceThread.setOperatingSystem(operatingSystem);
            runtimeService.start(operatingSystem);
        }else super.handleStartService(service,executingServiceID);
    }


    /**
     * 
     * TODO Comment method
     * @param listenerDeviceID
     * @param listenerID
     * @param handler
     * @param callerContext
     */
    public void addListenerHandlerInternal(DeviceID listenerDeviceID,ListenerID listenerID, ListenerFinishedHandler handler, ServiceContext callerContext) {
        ListenerFinishedHandler listener=new InternalFinishListener(handler,  callerContext);
        LocalSignalManager remoteSignalManager = deviceKnowledge.getSignalManager(listenerDeviceID);
        remoteSignalManager.addFinishListener(remoteSignalManager.getContextForListener(listenerID),listenerID,listener);
        internalFinishListenerMap.put(new HandlerListenerPair(handler,listenerID),listener);
    }
    
    /**
     * TODO Comment method
     * @param receiverDevice
     * @param listenerID
     * @param handler
     * @param context
     */
    public void removeListenerHandlerInternal(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler, ServiceContext context) {
        ListenerFinishedHandler listener= (ListenerFinishedHandler)internalFinishListenerMap.
    			remove(new HandlerListenerPair(handler,listenerID));
        deviceKnowledge.getSignalManager(receiverDevice).removeFinishListener(listenerID,listener);
        
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.operatingSystem.manager.DeviceServiceManager#getServiceInformation(de.uni_trier.jane.basetypes.ServiceID)
     */
    public ServiceInformation getServiceInformation(ServiceContext serviceContext) {
        ServiceInformation serviceThread;
        if (serviceContext.getServiceDeviceID().equals(deviceID)){
            serviceThread= super.getServiceInformation(serviceContext);
        }else{
            serviceThread=deviceKnowledge.getServiceManager(serviceContext.getServiceDeviceID()).getServiceInformation(serviceContext);
        }
        if (serviceThread==null&&deviceID!=globalDeviceID){
            serviceThread=deviceKnowledge.getServiceManager(globalDeviceID).getServiceInformation(new ServiceContext(serviceContext.getServiceID(),globalDeviceID));
        }
        
        return serviceThread;
    }
    
//  protected ServiceThread getServiceThread(ServiceID serviceID) {
//      ServiceThread serviceThread= super.getServiceThread(serviceID);
//      if (serviceThread==null&&deviceID!=globalDeviceID){
//          serviceThread=((SimulationDeviceServiceManager)deviceKnowledge.getServiceManager(globalDeviceID)).getServiceThread(serviceID);
//      }
//      
//      return serviceThread;
//      
//  }
    
    
    public ServiceID[] getServiceIDs(Class serviceClass) {
        ArrayList list=new ArrayList();
        // TODO: efficiently check implements
        Iterator iterator=idServiceThreadMap.values().iterator();
        while (iterator.hasNext()){
            ServiceThread thread=(ServiceThread)iterator.next();
            if (serviceClass.isAssignableFrom(thread.getServiceClass())){
                list.add(thread.getServiceID());
            }
        }
        if (!globalDeviceID.equals(deviceID)){
            list.addAll(Arrays.asList(deviceKnowledge.getServiceManager(globalDeviceID).getServiceIDs(serviceClass)));
        }
        return (ServiceID[])list.toArray(new ServiceID[list.size()]);
    }
  

    public ServiceID[] getServiceIDs() {
        HashSet keySet = new HashSet(idServiceThreadMap.keySet());
        if (!globalDeviceID.equals(deviceID)){
            keySet.addAll(Arrays.asList(deviceKnowledge.getServiceManager(globalDeviceID).getServiceIDs()));
        }
        return (ServiceID[])keySet.toArray(new ServiceID[keySet.size()]);
    }
    
    public ServiceID[] getLocalServiceIDs() {
        return super.getServiceIDs();
    }
    

    
    public boolean hasService(Class serviceClass) {
        if (super.hasService(serviceClass)){
            return true;
            
        }
        if (!globalDeviceID.equals(deviceID)){
            return deviceKnowledge.getServiceManager(globalDeviceID).hasService(serviceClass);
        }
        return false;
    }
    
    public boolean hasService(ServiceID serviceID) {
        if (super.hasService(serviceID)){
            return true;
            
        }
        if (!globalDeviceID.equals(deviceID)){
            return deviceKnowledge.getServiceManager(globalDeviceID).hasService(serviceID);
        }
        return false;
    }

    
    /**
     * TODO: comment class  
     * @author daniel
     **/

    private class InternalFinishListener implements ListenerFinishedHandler {

        private ListenerFinishedHandler handler;
        private DeviceID executingDeviceID;
        
        private ServiceContext remoteContext;

        /**
         * Constructor for class InternalFinishListener 
         *
         * @param handler
         * @param executingDeviceID
         * @param executingService
         */
        public InternalFinishListener(ListenerFinishedHandler handler, ServiceContext remoteContext) {
            this.handler=handler;
            this.remoteContext=remoteContext;
        }

        //
        public void handleFinished(ListenerID listenerID) {
            executionManager.schedule(new ListenerAction(remoteContext,executionManager.getCallerContext(),handler,listenerID));

        }

    }
    
    /**
     * TODO: comment class  
     * @author daniel
     **/

    private static final class ListenerAction extends Action {

        private ListenerFinishedHandler handler;
        private ListenerID finishedListener;
        

        /**
         * Constructor for class ListenerAction 
         *
         * @param executingServiceID
         * @param callingServiceID
         * @param callingDeviceID
         */
        public ListenerAction(ServiceContext executingServiceID, ServiceContext callerContext,
                ListenerFinishedHandler handler,ListenerID finishedListener) {
            super(executingServiceID, callerContext);
            this.handler=handler;
            this.finishedListener=finishedListener;

        }

        //
        public void execute(Service executingService) {
            handler.handleFinished(finishedListener);
            internalFinishListenerMap.remove(new HandlerListenerPair(handler,finishedListener));
            
        }

    }


}
