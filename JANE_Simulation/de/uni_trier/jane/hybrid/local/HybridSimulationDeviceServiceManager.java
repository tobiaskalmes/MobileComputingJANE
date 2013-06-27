/*
 * Created on 04.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.hybrid.local;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.hybrid.local.manager.*;
import de.uni_trier.jane.random.ContinuousDistribution;
import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.basetypes.RuntimeClock;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.kernel.ShutdownAnnouncer;
import de.uni_trier.jane.simulation.kernel.TerminalCondition;
import de.uni_trier.jane.simulation.kernel.eventset.EventSet;
import de.uni_trier.jane.simulation.operating_system.*;
import de.uni_trier.jane.simulation.operating_system.manager.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.simulationl.visualization.console.ConsoleTextBuffer;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
// TODO: throws in Javadoc-Kommentare !!!!
public class HybridSimulationDeviceServiceManager extends SimulationDeviceServiceManager implements HybridServiceManager{


	private Set remoteServices;
    

    /**
     * 
     * Constructor for class <code>HybridSimulationDeviceServiceManager</code>
     * @param globalDeviceID
     * @param deviceID
     * @param simulationGlobalKnowledge
     * @param initializer
     * @param runtimeClock
     * @param scheduleManager
     * @param simulationShutdownAnnouncer
     * @param consoleTextBuffer
     * @param deviceKnowledge
     * @param addressDeviceMapping
     * @param shutdownManager
     */
	public HybridSimulationDeviceServiceManager(
			DeviceID globalDeviceID, DeviceID deviceID, GlobalKnowledge simulationGlobalKnowledge, 
	        DefaultSimulationParameters initializer,  RuntimeClock runtimeClock, 
	        ActionScheduler scheduleManager,  ShutdownAnnouncer simulationShutdownAnnouncer,
	        ConsoleTextBuffer consoleTextBuffer,  DeviceKnowledge deviceKnowledge, 
	        AddressDeviceMapping addressDeviceMapping,ShutdownManager shutdownManager) {
	   
		super(globalDeviceID, deviceID, simulationGlobalKnowledge, initializer,runtimeClock, 
				scheduleManager,simulationShutdownAnnouncer, consoleTextBuffer,
				deviceKnowledge,addressDeviceMapping,
				//new HybridLocalServiceManager(deviceID,deviceKnowledge),
				new RemoteSignalServer(initializer.getEventReflectionDepth()),                
				new HybridLocalFinishManager(),
				shutdownManager);
		
		remoteServices=new LinkedHashSet();
		finishManager.addFinishListener(new FinishListener() {
         
            public void notifyFinished(ServiceID serviceID,
                    ServiceContext finishContext) {
                remoteServices.remove(serviceID);

            }
        });

	
	}
	
    /**
     * 
     * TODO Comment method
     * @param serviceID
     * @return
     */
    public boolean isRemote(ServiceID serviceID) {

        return remoteServices.contains(serviceID);
    }

    /**
     * 
     * TODO Comment method
     * @param callerContext
     * @param service
     * @param visualize
     */
    public void startRemoteService(ServiceContext callerContext, RemoteService service,boolean visualize){
        remoteServices.add(service.getServiceID());
        startService(callerContext,service,service.getServiceClass(),service.getServiceID(),visualize);
        
        
    }
  

  


   
   


    /**
     * TODO Comment method
     * @return
     */
    public boolean hasRemoteService() {

        return !remoteServices.isEmpty();
    }

    /**
     * TODO Comment method
     * @return
     */
    public ServiceID[] getRemoteServices() {

        return (ServiceID[])remoteServices.toArray(new ServiceID[remoteServices.size()]);
    }

    /**
     * TODO Comment method
     * @return
     */
    public Console getConsole() {

        return console;
    }

	
    /**
     *
     */

    protected void handleStartService(Service service,
            ServiceID executingServiceID) {
        if (service instanceof RemoteService){
            ServiceThread  serviceThread=(ServiceThread) idServiceThreadMap.get(executingServiceID);
            
            SimulationOperatingSystemImpl operatingSystem=new SimulationOperatingSystemImpl(serviceThread, distributionCreator, timeoutManager,executionManager,
    		        this,signalManager,finishManager,shutdownManager,console,clock,simulationClock,energyManager,
    		        addressDeviceMapping,globalDeviceID, globalKnowledge,deviceKnowledge,simulationShutdownAnnouncer);
            serviceThread.setOperatingSystem(operatingSystem);
            
        }else{
            super.handleStartService(service, executingServiceID);
        }
    }



    public FinishManager getFinishManager() {
        return finishManager;
    }

 
 
	

}
