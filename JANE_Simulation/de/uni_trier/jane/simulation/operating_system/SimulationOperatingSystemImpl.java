 /*
 * Created on 04.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation.operating_system;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.Console;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.manager.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.shapes.*;

import java.util.*;


/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationOperatingSystemImpl extends RuntimeOperatingSystemImpl implements SimulationOperatingSystem{    
    /**
     * TODO: comment class  
     * @author daniel
     **/

 
//implements GlobalOperatingSystem{    
    
   
 
    
// TODO: als shutdownListener anmelden!

	private AddressDeviceMapping addressDeviceMapping;

	
	private GlobalKnowledge globalKnowledge;
	
	//
	
	private DeviceKnowledge deviceKnowledge;


    private DeviceID globalDeviceID;

    private EnergyManager energyManager;


    private Clock simulationClock;


    private ShutdownAnnouncer simulationShutdownAnnouncer;

   


    /**
     * 
     * Constructor for class <code>SimulationOperatingSystemImpl</code>
     * @param serviceThread
     * @param distributionCreator
     * @param timeoutManager
     * @param executionManager
     * @param serviceManager
     * @param localSignalManager
     * @param eventDB
     * @param finishManager
     * @param shutdownManager
     * @param console
     * @param runtimeClock
     * @param simulationClock
     * @param energyManager
     * @param addressDeviceMapping
     * @param globalDeviceID
     * @param globalKnowledge
     * @param deviceKnowledge
     * @param simulationShutdownAnnouncer
     */
    public SimulationOperatingSystemImpl(
    		ServiceThread serviceThread,
            DistributionCreator distributionCreator,
            TimeoutManager timeoutManager, ExecutionManager executionManager,
            ServiceManager serviceManager,
            LocalSignalManager localSignalManager,  FinishManager finishManager,
            ShutdownManager shutdownManager, Console console, Clock runtimeClock,
            Clock simulationClock, EnergyManager energyManager, AddressDeviceMapping addressDeviceMapping, 
            DeviceID globalDeviceID, GlobalKnowledge globalKnowledge,  
            DeviceKnowledge deviceKnowledge, ShutdownAnnouncer simulationShutdownAnnouncer
            ) {
        super(serviceThread, distributionCreator, timeoutManager,
                executionManager, serviceManager, localSignalManager,
                finishManager, shutdownManager, console, runtimeClock
                );


               
        
        this.simulationShutdownAnnouncer=simulationShutdownAnnouncer;
        
		this.addressDeviceMapping = addressDeviceMapping;
		this.globalDeviceID=globalDeviceID;
    	// TODO comment?
		
		this.globalKnowledge = globalKnowledge;
		
		this.simulationClock=simulationClock;
		this.energyManager=energyManager;
        
        this.deviceKnowledge=deviceKnowledge;
       

        
    	
    	
    }
	
	

	
	
	
	
	
	

	
	
	    
    public Object accessSynchronous(DeviceID receiverDevice, ListenerID receiverService, ListenerAccess serviceAccess) {
        return deviceKnowledge.getSignalManager(receiverDevice).
        	accessSynchronous(serviceThread.getContext(),receiverService,serviceAccess);
    }
    


    
 
	public void registerAtService(DeviceID device, ServiceID service, Class serviceType) {
	    //TODO
	    //((ServiceThread)deviceKnowledge.getServiceManager(device)
	    	serviceManager.getServiceInformation(new ServiceContext(service,device)).
	    	registerService(serviceThread.getDeviceID(), serviceThread.getServiceID(),localSignalManager.getListenerClass(serviceThread.getServiceID()), serviceType);
	}

	public void unregisterAtService(DeviceID device, ServiceID service, Class serviceType) {
	    //TODO
	    //((ServiceThread)deviceKnowledge.getServiceManager(device).getServiceInformation(service)).
		serviceManager.getServiceInformation(new ServiceContext(service,device)).
	    	unregisterService(serviceThread.getDeviceID(), serviceThread.getServiceID(), serviceType);
	}
	
 
    
    public void finishListener(DeviceID receiveDevice, ListenerID listenerID) {
        deviceKnowledge.getSignalManager(receiveDevice).finish(serviceThread.getContext(),listenerID);
    }
    
    public void finishListener(DeviceID receiveDevice, SignalListener signalListener) {
        deviceKnowledge.getSignalManager(receiveDevice).finish(serviceThread.getContext(),signalListener);
        
    }

    
    
    
    
   


    

 

    

    
    
    

   
    

 
    



  
    public void sendSignal(DeviceID receiverDevice, ListenerID signalReceiver, Signal signal) {
        deviceKnowledge.getSignalManager(receiverDevice).
        	sendSignal(serviceThread.getContext(),signalReceiver,signal);
        
        
    }
    
    public void sendEvent(DeviceID receiverDevice, ServiceEvent serviceEvent) {
        deviceKnowledge.getSignalManager(receiverDevice).
        	sendEvent(serviceThread.getContext(),serviceEvent,serviceThread.getServiceClass());
        
        
    }

    



	public void sendSignal(DeviceID receiverDevice, Signal signal) {
	    List listeners=serviceThread.getRegisteredListeners(receiverDevice,signal.getReceiverServiceClass());
        deviceKnowledge.getSignalManager(receiverDevice).
        	sendSignal(serviceThread.getContext(),listeners,signal);
	}

	



	
	
	public void finishSimulation() {
        simulationShutdownAnnouncer.shutdown();
	}

	public double getSimulationTime() {
		return simulationClock.getTime();
	}

	public GlobalKnowledge getGlobalKnowledge() {
		return globalKnowledge;
	}


	public DeviceID getCallingDeviceID() {

		return executionManager.getCallerContext().getServiceDeviceID();
	}

	

	
	public void registerAddress(Address address) {
		addressDeviceMapping.register(getDeviceID(), address);
	}

	public void deregisterAddress(Address address) {
		addressDeviceMapping.unregister(getDeviceID(), address);
	}

	

    public void addListenerHandler(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler) {
        
        ((SimulationDeviceServiceManager)serviceManager).
        	addListenerHandlerInternal(receiverDevice,listenerID,handler,serviceThread.getContext());
        
    }



    //
    public void removeListenerHandler(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler) {
        ((SimulationDeviceServiceManager)serviceManager).
        	removeListenerHandlerInternal(receiverDevice,listenerID,handler,serviceThread.getContext());
        
        
    }
    


    
    /**
     * TODO Comment method
     * @param deviceID
     * @param listenerID
     * @return
     */
    public boolean hasListener(DeviceID deviceID, ListenerID listenerID) {
        if (deviceID==null||listenerID==null) return false;
        return deviceKnowledge.getSignalManager(deviceID).hasListener(listenerID);
        
    }


    
    public DeviceID getGlobalDeviceID() {
		return globalDeviceID;
	}

	public void setCurrentEnergyConsumption(double watt) {
		energyManager.setCurrentEnergyConsumption(serviceThread.getServiceID(),watt);
	}



	public double getEnergy() {
		return energyManager.getRemainingEnergy();
	}


	public Object getSignalListenerStub(DeviceID callingDeviceID, ListenerID listenerID, Class listenerClass) {

		ServiceContext serviceContext=deviceKnowledge.getSignalManager(callingDeviceID).getContextForListener(listenerID);
        if (serviceContext==null) throw new OperatingServiceException("The given listener does not exist!");
		return serviceManager.getServiceInformation(serviceContext).getSignalStub(serviceContext,listenerID,listenerClass);
	}


	public Object getAccessListenerStub(DeviceID callingDeviceID, ListenerID listenerID, Class listenerClass) {
		ServiceContext serviceContext=deviceKnowledge.getSignalManager(callingDeviceID).getContextForListener(listenerID);
		return serviceManager.getServiceInformation(serviceContext).getAccessStub(serviceContext,listenerID,listenerClass);
	}

	public void sendSignal(ServiceContext receiverContext,
			ListenerID listenerID, Signal signal) {
		if (receiverContext.getServiceDeviceID().equals(serviceThread.getDeviceID())){
			super.sendSignal(receiverContext, listenerID, signal);
		}else{
			deviceKnowledge.getSignalManager(receiverContext.getServiceDeviceID()).sendSignal(serviceThread.getContext(),listenerID,signal);
		}
	}

    public void finishService(DeviceID deviceID, ServiceID serviceID) {
        deviceKnowledge.getFinishManager(deviceID).finishService(serviceThread.getContext(),new ServiceContext(serviceID,deviceID));
    }
    
    public ServiceID[] getServiceIDs(DeviceID deviceID, Class serviceClass) {
        return deviceKnowledge.getServiceManager(deviceID).getServiceIDs(serviceClass);
    }
   
    public ServiceID[] getServiceIDs(DeviceID deviceID) {
     
        return deviceKnowledge.getServiceManager(deviceID).getServiceIDs();
    }
    
    public boolean hasService(DeviceID deviceID, Class serviceClass) {
        return deviceKnowledge.getServiceManager(deviceID).hasService(serviceClass);
    }
    public boolean hasService(DeviceID deviceID, ServiceID serviceID) {
        return deviceKnowledge.getServiceManager(deviceID).hasService(serviceID);
    }
    public ServiceID startService(DeviceID deviceID, Service service) {
        return deviceKnowledge.getServiceManager(deviceID).startService(serviceThread.getContext(),service);
    }
    
    public boolean isVisualized(ServiceID serviceID) {
        return serviceManager.isVisualized(new ServiceContext(serviceID,getDeviceID()));
    }
    
    public boolean isVisualized(DeviceID deviceID,ServiceID serviceID){
        return serviceManager.isVisualized(new ServiceContext(serviceID,deviceID));
    }
    public void setVisualized(ServiceID serviceID, boolean visualize) {
        serviceManager.setVisualized(new ServiceContext(serviceID,getDeviceID()),visualize);
    }
    
    public void setVisualized(DeviceID deviceID,ServiceID serviceID, boolean visualize) {
        serviceManager.setVisualized(new ServiceContext(serviceID,deviceID),visualize);
    }
    
    
}
