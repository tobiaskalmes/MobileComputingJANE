/*
 * Created on Jun 6, 2005
 * File: SyncSimulationOperatingSystemImpl.java
 * Author: Ulf Wehling
 */
package de.uni_trier.jane.simulation.operating_system;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.hybrid.local.SynchronizedEventSet;
import de.uni_trier.jane.random.*;

import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.manager.*;

/**
 * This implementation is a wrapper for the 
 * <code>SimulationOperatingSystemImpl</code> class. All methods are
 * synchronized at the event queue of the simulation.
 * TODO: All methods from the classes SimulationOperatingSystemImpl and 
 * RuntimeOperatingSystemImpl should be wrapped, but it has to be tested if
 * everything works correct. Some parts are a little bit strange and therefore
 * I do not know if everything is synchronized correct or if the implementation
 * is deadlock free. Also the hybrid mode has to be tested.
 * 
 * @author ulf.wehling
 * @version Jun 28, 2005
 */
public class SyncSimulationOperatingSystemImpl 
extends SimulationOperatingSystemImpl {

	/**
	 * <code>EventSet</code> which is used in the simulation.  
	 * We must synchronize all calls to the <code>EventSet</code>.
	 */
	private SyncObject syncObject;
	
	/**
     * 
     * Constructor for class <code>SyncSimulationOperatingSystemImpl</code>
     * @param serviceThread
     * @param distributionCreator
     * @param timeoutManager
     * @param executionManager
     * @param serviceManager
     * @param localSignalManager
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
     * @param eventSet
	 */
	public SyncSimulationOperatingSystemImpl(
			ServiceThread serviceThread,	            
			DistributionCreator distributionCreator,
	        TimeoutManager timeoutManager, 
	        ExecutionManager executionManager,
	        ServiceManager serviceManager,
	        LocalSignalManager localSignalManager, 
	        FinishManager finishManager,
	        ShutdownManager shutdownManager, 
	        Console console, 
	        Clock runtimeClock,
	        Clock simulationClock, 
	        EnergyManager energyManager, 
	        AddressDeviceMapping addressDeviceMapping, 
	        DeviceID globalDeviceID, 
	        GlobalKnowledge globalKnowledge,  
	        DeviceKnowledge deviceKnowledge, 
	        ShutdownAnnouncer simulationShutdownAnnouncer,
	        EventSet eventSet
	        ) {
		 super(
				 serviceThread,
				 distributionCreator,
				 timeoutManager,
				 executionManager,
				 serviceManager,
				 localSignalManager,
				 finishManager,
				 shutdownManager,
				 console,
				 runtimeClock,
				 simulationClock,
				 energyManager,
				 addressDeviceMapping,
				 globalDeviceID,
				 globalKnowledge,
				 deviceKnowledge,
                 simulationShutdownAnnouncer
				 );
		 this.syncObject = ((SynchronizedEventSet)eventSet).getSynchronizeObject();
	}
	
    
    
	// ##### wrapped operating system methods ##################################
	// ##### methods from SimulationOperatingSystemImpl.java ###################
	
	public Object accessSynchronous(DeviceID receiverDevice, ListenerID receiverService, ListenerAccess serviceAccess) {
        synchronized(syncObject) {
        	return super.accessSynchronous(receiverDevice, receiverService, serviceAccess);
        }
    }
    


	public void registerAtService(DeviceID device, ServiceID service, Class serviceType) {
	    synchronized(syncObject) {
	    	super.registerAtService(device, service, serviceType);
	    }
	}

	public void unregisterAtService(DeviceID device, ServiceID service, Class serviceType) {
		synchronized(syncObject) {
			super.unregisterAtService(device, service, serviceType);
		}
	}
    
    public void finishListener(DeviceID receiveDevice, ListenerID listenerID) {
        synchronized(syncObject) {
        	super.finishListener(receiveDevice, listenerID);
        }
    }  
    
    public void finishListener(DeviceID receiveDevice, SignalListener signalListener) {
        synchronized(syncObject) {
            super.finishListener(receiveDevice, signalListener);
        }
    }
    public void registerEventListener(ServiceEvent eventByExample, ListenerID listenerID) {
        synchronized(syncObject) {
            super.registerEventListener(eventByExample, listenerID);
        }
    }
    
    public ListenerID registerEventListener(ServiceEvent eventByExample, SignalListener signalListener) {
        synchronized(syncObject) {
            return super.registerEventListener(eventByExample, signalListener);
        }
    }
    
    public boolean sendEvent(ServiceEvent serviceEvent) {
        synchronized(syncObject) {
            return super.sendEvent(serviceEvent);
        }
    }
    
    public ListenerID registerSignalListenerAutogen(SignalListener listener) {
        synchronized(syncObject) {
            return super.registerSignalListenerAutogen(listener);
        }
    }
    
    

    public void sendSignal(ListenerID receiver, Signal signal) {
         synchronized(syncObject) {
        	super.sendSignal(receiver, signal);
        }
    }
    
//    public void sendRequest(DeviceID receiverDevice, ServiceID receiverService, DeviceRequestCallbackPair requestCallbackPair) {
//        synchronized(syncObject) {
//        	super.sendRequest(receiverDevice, receiverService, requestCallbackPair);
//        }
//    }
//    
//    public void sendReply(ReplyHandle replyHandle, ServiceReply serviceReply) {
//        synchronized(syncObject) {
//        	super.sendReply(replyHandle, serviceReply);
//        }
//    }  
//    
//    public TaskHandle startTask(DeviceID receiverDevice, ServiceID receiverService, DeviceTaskCallbackPair signalCallbackPair) {
//        synchronized(syncObject) {
//        	return super.startTask(receiverDevice, receiverService, signalCallbackPair);
//        }
//    }
//
//    public void sendCallback(TaskHandle handle, ServiceCallback callback) {
//        synchronized(syncObject) {
//        	super.sendCallback(handle, callback);
//        }
//    }  

//    public void finishTask(TaskHandle handle) {
//        synchronized(eventSet) {
//        	super.finishTask(handle);
//        }
//    }
//
//    public void addTaskListener(TaskHandle handle, TaskFinishListener taskFinishListener) {
//        synchronized(eventSet) {
//        	super.addTaskListener(handle, taskFinishListener);
//        }
//    }
//    
//    public void addTaskListener(DeviceID deviceID, TaskHandle taskHandle, TaskFinishListener listener) {
//    	// method is synchronized 
//        addTaskListener(taskHandle,listener);
//    }
//    
//    public void removeTaskListener(DeviceID deviceID, TaskHandle taskHandle) {
//    	// method is synchronized
//        removeTaskListener(taskHandle);
//    }  
//
//    public void removeTaskListener(TaskHandle handle) {
//        synchronized(eventSet) {
//        	super.removeTaskListener(handle);
//        }
//    }
    
    public void sendSignal(DeviceID receiverDevice, ListenerID signalReceiver, Signal signal) {
        synchronized(syncObject) {
        	super.sendSignal(receiverDevice, signalReceiver, signal);
        }   
    }


	public void sendSignal(DeviceID receiverDevice, Signal signal) {
        synchronized(syncObject) {
        	super.sendSignal(receiverDevice, signal);
        }
	}

	public void finishSimulation() {
		synchronized(syncObject) {
			super.finishSimulation();
		}
	}

	public double getSimulationTime() {
		synchronized(syncObject) {
			return super.getSimulationTime();
		}
	}

	public GlobalKnowledge getGlobalKnowledge() {
		synchronized(syncObject) {
			return super.getGlobalKnowledge();
		}
	}

	public DeviceID getCallingDeviceID() {
		synchronized(syncObject) {
			return super.getCallingDeviceID();
		}
	}
	
	public void registerAddress(Address address) {
		synchronized(syncObject) {
			super.registerAddress(address);
		}
	}

	public void deregisterAddress(Address address) {
		synchronized(syncObject) {
			super.deregisterAddress(address);
		}
	}	

    public void addListenerHandler(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler) {    
        synchronized(syncObject) {
        	super.addListenerHandler(receiverDevice, listenerID, handler);
        }  
    }

    public void removeListenerHandler(DeviceID receiverDevice, ListenerID listenerID, ListenerFinishedHandler handler) {
        synchronized(syncObject) {
        	super.removeListenerHandler(receiverDevice, listenerID, handler);
        }    
    }  
    
    public DeviceID getGlobalDeviceID() {
		synchronized(syncObject) {
			return super.getGlobalDeviceID();
		}
	}

	public void setCurrentEnergyConsumption(double watt) {
		synchronized(syncObject) {
			super.setCurrentEnergyConsumption(watt);
		}
	}

	public double getEnergy() {
		synchronized(syncObject) {
			return super.getEnergy();
		}
	}

	public Object getSignalListenerStub(DeviceID callingDeviceID, ListenerID listenerID, Class listenerClass) {

		synchronized(syncObject) {
			return super.getSignalListenerStub(callingDeviceID, listenerID, listenerClass);
		}
	}

	public Object getAccessListenerStub(DeviceID callingDeviceID, ListenerID listenerID, Class listenerClass) {
		synchronized(syncObject) {
			return super.getAccessListenerStub(callingDeviceID, listenerID, listenerClass);
		}
	}

	public void sendSignal(ServiceContext receiverContext, ListenerID listenerID, Signal signal) {
		synchronized(syncObject) {
			super.sendSignal(receiverContext, listenerID, signal);
		}
	}

    public void finishService(DeviceID deviceID, ServiceID serviceID) {
        synchronized(syncObject) {
        	super.finishService(deviceID, serviceID);
        }
    }
    
    public ServiceID[] getServiceIDs(DeviceID deviceID, Class serviceClass) {
         synchronized(syncObject) {
        	return super.getServiceIDs(deviceID, serviceClass);
        }
    }
    
    public boolean hasService(DeviceID deviceID, Class serviceClass) {
        synchronized(syncObject) {
        	return super.hasService(deviceID, serviceClass);
        }
    }
    
    public boolean hasService(DeviceID deviceID, ServiceID serviceID) {
        synchronized(syncObject) {
        	return super.hasService(deviceID, serviceID);
        }
    }
    
    public ServiceID startService(DeviceID deviceID, Service service) {
        synchronized(syncObject) {
        	return super.startService(deviceID, service);
        }
    }
    
    
    // ##### methods from RuntimeOperatingSystemImpl.java ######################
    
    public ServiceID startService(Service service) {
	    synchronized(syncObject) {
	    	return super.startService(service);
	    }  
    }

    public void finishService(ServiceID serviceID) {
		synchronized(syncObject) {
			super.finishService(serviceID);
		}
    }
	
    public boolean hasService(ServiceID serviceID) {
        synchronized(syncObject) {
        	return super.hasService(serviceID);
        }
    }
    
    public boolean hasService(Class serviceClass) {
        synchronized(syncObject) {
        	return super.hasService(serviceClass);
        }
    }

    public ServiceID[] getServiceIDs(Class serviceClass) {
        synchronized(syncObject) {
        	return super.getServiceIDs(serviceClass);
        }
    }

    
    public Object accessSynchronous(ListenerID requestedService, ListenerAccess serviceData) {
     	synchronized(syncObject) {
    		return super.accessSynchronous(requestedService, serviceData);
    	}
    }
    
    public void setTimeout(ServiceTimeout timeout) {
        synchronized(syncObject) {
        	super.setTimeout(timeout);
        }
    }

    public void removeTimeout(ServiceTimeout timeout) {
        synchronized(syncObject) {
        	super.removeTimeout(timeout);
        }
    }

	public void denyAllServices() {
		synchronized(syncObject) {
			super.denyAllServices();
		}
	}
	
	public void denyService(ServiceID service) {
	    synchronized(syncObject) {
	    	super.denyService(service);
	    }
	}
	
	public void allowService(ServiceID service) {
	    synchronized(syncObject) {
	    	super.allowService(service);
	    }
	}
	
	public void allowAllServices() {
	    synchronized(syncObject) {
	    	super.allowAllServices();
	    }
	}

    public void registerAtService(ServiceID serviceID, Class serviceType) {
        synchronized(syncObject) {
        	super.registerAtService(serviceID, serviceType);
        }
    }
    

    public void registerAtService(final ServiceID serviceID, ListenerID listenerID, final Class serviceType) {
    	synchronized(syncObject) {
        	super.registerAtService(serviceID, listenerID, serviceType);
        }
    }
    
    public ListenerID registerAtService(ServiceID serviceID, SignalListener listener, Class serviceType) {
        synchronized(syncObject) {
        	return super.registerAtService(serviceID, listener, serviceType);
        }
    }
    
    public void unregisterAtService(ServiceID serviceID, ListenerID listenerID, Class serviceType) {
        synchronized(syncObject) {
        	super.unregisterAtService(serviceID, listenerID, serviceType);
        }
    }

    public void unregisterAtService(ServiceID serviceID, Class serviceType) {
        synchronized(syncObject) {
        	super.unregisterAtService(serviceID, serviceType);
        }
    }

    public ListenerID registerSignalListener(SignalListener listener, Class classToRegister) {
        synchronized(syncObject) {
        	return super.registerSignalListener(listener, classToRegister);
        }
    }  
    
    
// TODO: what to do here?! see super class RuntimeOperatingSystemImpl.java
    public void registerSignalListener(Class classToRegister) {
    	synchronized(syncObject) {
    		super.registerSignalListener(classToRegister);
    	}
    }
    
    public void registerSignalListener(SignalListener listener, ListenerID listenerID, Class classToRegister) {
        synchronized(syncObject) {
        	super.registerSignalListener(listener, listenerID, classToRegister);
        }
    }

    public ListenerID registerOneShotListener(SignalListener listener, Class classToRegister) {
        synchronized(syncObject) {
        	return super.registerOneShotListener(listener, classToRegister);
        }
    }
    
    public void registerOneShotListener(SignalListener listener, ListenerID listenerID,Class classToRegister) {
        synchronized(syncObject) {
        	super.registerOneShotListener(listener, listenerID, classToRegister);
        }
    }
// TODO: see todo above
    
    
    public Object getSignalListenerStub(ListenerID listenerID, Class listenerClass) {
        synchronized(syncObject) {
        	return super.getSignalListenerStub(listenerID, listenerClass);
        }
    }
    
    public Object getAccessListenerStub(ListenerID listenerID, Class listenerClass) {
        synchronized(syncObject) {
        	return super.getAccessListenerStub(listenerID, listenerClass);
        }
    }
    
    
// TODO: is code below safe?!
    public void registerAccessListener(Class classToRegister) {
         synchronized(syncObject) {
        	super.registerAccessListener(classToRegister);
        }
    }
    
    public ListenerID registerAccessListener(SignalListener listener, Class classToRegister) {
        synchronized(syncObject) {
        	return super.registerAccessListener(listener, classToRegister);
        }
    }
   
    public void registerAccessListener(SignalListener listener, ListenerID listenerID, Class classToRegister) {
        synchronized(syncObject) {
        	super.registerAccessListener(listener, listenerID, classToRegister);
        }
    }
    
    public boolean hasListener(ListenerID listenerID) {
        synchronized(syncObject) {
        	return super.hasListener(listenerID);
        }
    }
    
    public void finishListener(ListenerID listenerID) {
        synchronized(syncObject) {
        	super.finishListener(listenerID);
        }
    }
    
    public void finishListener(SignalListener signalListener) {

   		synchronized(syncObject) {
   			localSignalManager.finish(serviceThread.getContext(),signalListener);
   		}
    }
// TODO: is code above safe?!
    
 


    public void sendSignal(Signal signal) {
    	synchronized(syncObject) {
    		super.sendSignal(signal);
    	}
    }
    
    
//  TODO: code below is not safe?!
//    public void sendRequest(ServiceID receiver, RequestHandlerPair requestCallbackPair) {
//    	// this method is already synchronized
//    	ListenerID listenerID = registerOneShotListener(requestCallbackPair.getCallbackHandler(), requestCallbackPair.getCallbackHandler().getClass());
//    	DeprecatedReplyHandle replyHandle = new DeprecatedReplyHandle(listenerID,getDeviceID());
//        // this method is already synchronized
//    	sendSignal(receiver, new DeprecatedRequestSignal(requestCallbackPair.getServiceRequest(), replyHandle, serviceThread.getServiceID()));
//    }
//   
//    public TaskHandle startTask(ServiceID receiver, TaskCallbackPair signalCallbackPair) {
//        // this method is already synchronized
//    	ListenerID listenerID=registerSignalListener(signalCallbackPair.getCallbackHandler(),signalCallbackPair.getCallbackHandler().getClass());
//        TaskHandle handle=new DeprecatedTaskHandle(listenerID,serviceThread.getContext());
//        // this method is already synchronized
//        sendSignal(receiver, new DeprecatedTaskSignal(signalCallbackPair.getServiceTask(),handle,serviceThread.getServiceID()));
//        return handle;
//    }
// TODO: code above is not safe?!
 

    public DistributionCreator getDistributionCreator() {
        synchronized(syncObject) {
        	return super.getDistributionCreator();
        }
    }

	public DeviceID getDeviceID() {
		synchronized(syncObject) {
			return super.getDeviceID();
		}
	}

    public ServiceID getServiceID() {
    	synchronized(syncObject) {
    		return super.getServiceID();
    	}
    }

	public void write(String text) {
		synchronized(syncObject) {
			super.write(text);
		}
	}

    /**
     * @return Returns the executionManager.
     */
    public ExecutionManager getExecutionManager() {
        synchronized(syncObject) {
        	return super.getExecutionManager();
        }
    }

	public ServiceID getCallingServiceID() {
		synchronized(syncObject) {
			return super.getCallingServiceID();
		}
	}

    public void addListenerHandler(ListenerID listenerID, ListenerFinishedHandler handler) {
        synchronized(syncObject) {
        	super.addListenerHandler(listenerID, handler);
        }
    }
    
    public void addListenerHandler(SignalListener signalListener, ListenerFinishedHandler handler) {
    	
    	synchronized(syncObject) {
    		localSignalManager.addFinishListener(serviceThread.getContext(),signalListener,handler);
    	}
    	
          
    }
    
    public void removeListenerHandler(SignalListener signalListener, ListenerFinishedHandler handler) {
    	synchronized(syncObject) {
    		localSignalManager.removeFinishListener(signalListener,handler);
    	} 
    }
    
    public void removeListenerHandler(ListenerID listenerID, ListenerFinishedHandler handler) {
        synchronized(syncObject) {
        	super.removeListenerHandler(listenerID, handler);
        }
    }
 
	public double getTime() {
		synchronized(syncObject) {
			return super.getTime();
		}
	}

	public void setTime(double time) {
		synchronized(syncObject) {
			super.setTime(time);
		}
	}

	public void reboot() {
		synchronized(syncObject) {
			super.reboot();
		}
	}

	public void shutdown() {
		synchronized(syncObject) {
			super.shutdown();
		}
	}
    
}
