/*****************************************************************************
 * 
 * LocalOperatingSystemServer.java
 * 
 * $Id: HybridOperatingSystemServer.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.local; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.hybrid.local.manager.*;
import de.uni_trier.jane.hybrid.remote.RemoteOperatingSystemClient;
import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.parameter.todo.DefaultParameters;
import de.uni_trier.jane.service.unit.DefaultServiceUnit;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.SimulationOperatingSystemImpl;

import java.rmi.RemoteException;
import java.util.*;




/**
 * @author goergen
 *
 * TODO comment class
 */
public class HybridOperatingSystemServer implements RemoteOperatingSystemServer {

   

    protected SyncObject syncObject;
    
    private RemoteOperatingSystemClient remoteClient;
    private EventSet eventSet;
    private Map timeoutMap;
	private DefaultServiceUnit deviceServiceUnit;
    private Map finishListenerMap;
    UnexpectedShutdownListener listener;
    Set finishedServices;

    protected HybridServiceManager serviceManager;

    private ShutdownManager shutdownManager;

   // private EventDB eventDB;
    
    
    
    
    /**
     * 
     * Constructor for class <code>HybridOperatingSystemServer</code>
     * @param syncObject
     * @param serviceManager
     * @param remoteClient
     * @param eventSet
     * @param deviceServiceUnit
     * @param listener
     * @param shutdownManager
     */
    public HybridOperatingSystemServer(SyncObject syncObject,
            HybridServiceManager serviceManager,
            RemoteOperatingSystemClient remoteClient,
			EventSet eventSet, DefaultServiceUnit deviceServiceUnit, 
			UnexpectedShutdownListener listener, ShutdownManager shutdownManager) {
        this.syncObject = syncObject;
        this.serviceManager=serviceManager;
        this.remoteClient = remoteClient;
        this.eventSet=eventSet;
        this.listener=listener;
        this.shutdownManager=shutdownManager;
        //eventDB=serviceManager.getEventDB();
        timeoutMap=new HashMap();
        finishedServices=new LinkedHashSet();
        this.deviceServiceUnit=deviceServiceUnit.copy(serviceManager.getDeviceID(),null);
        //((HybridLocalServiceManager)operatingSystem.getServiceManager()).setRemoteClient(remoteClient,this);
        ((RemoteSignalServer)serviceManager.getSignalManager()).setClient(remoteClient,this);
        finishListenerMap=new HashMap();
    }
   

    public DeviceID getDeviceID() {
        synchronized(syncObject){
            return serviceManager.getDeviceID();
        }
    }

 

    public double getTime() {
        synchronized(syncObject){
            return serviceManager.getClock().getTime();
        }
    }


    public void setTime(double time) {
        synchronized(syncObject){
            serviceManager.getClock().setTime(time);
        }

    }


//    public double getEnergy() {
//        synchronized(syncObject){
//            throw new OperatingServiceException("Not Yet implemented return serviceManager.getEnergy();
//        }
//    }

    /**
     * @param hostedService
     *
     */
    //
    public void finishService(ServiceContext callerContext,
            ServiceID serviceIDToFinish) throws RemoteException {
       synchronized(syncObject){
            //operatingSystem.getServiceManager().enterContext(callerContext.getCallerService());
            serviceManager.getFinishManager().finishService(callerContext,new ServiceContext(serviceIDToFinish,deviceServiceUnit.getDeviceID()));
            //operatingSystem.getServiceManager().exitContext();
        }
        

    }

    

    public void write(String text) {
        synchronized(syncObject){
            serviceManager.getConsole().println(text);  
        }

    }
    public void setTimeout(final RemoteTimeoutID timeoutID,double delta) {
        //TODO: use ExecutionManager?
        synchronized(syncObject){
            Event event=new Event(eventSet.getTime()+delta) {
                protected void handleInternal() {
                    try {
                        remoteClient.handleTimeout(timeoutID);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    timeoutMap.remove(timeoutID);
                    
                }
            };
        
        	timeoutMap.put(timeoutID,event);
        	eventSet.add(event);
        }
        
    }
    public void removeTimeout(RemoteTimeoutID timeoutID) {
        synchronized(syncObject){
            Event event=(Event)timeoutMap.remove(timeoutID);
            event.disable();
            
        }
        
    }
   
    
  

    public void denyAllServices(ServiceContext hostedService) {
        synchronized(syncObject){
	        //operatingSystem.getServiceManager().enterContext(hostedService);
	        serviceManager.getOperatingSystem(hostedService).denyAllServices();
	        //operatingSystem.getServiceManager().exitContext();
        }
        
    }
    
    public void denyService(ServiceContext hostedService, ServiceID service) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(hostedService);
            serviceManager.getOperatingSystem(hostedService).denyService(service);
            //operatingSystem.getServiceManager().exitContext();
        }

    }

    public void allowAllServices(ServiceContext hostedService) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(hostedService);
            serviceManager.getOperatingSystem(hostedService).allowAllServices();
            //operatingSystem.getServiceManager().exitContext();
        }

    }

    public void allowService(ServiceContext hostedService, ServiceID service) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(hostedService);
            serviceManager.getOperatingSystem(hostedService).allowService(service);
            //operatingSystem.getServiceManager().exitContext();
        }
    }
    
   
    public void registerAtService(ServiceContext registeredService, ListenerID listenerID,
            Class listenerType, Class serviceType) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(hostedServiceID);
            serviceManager.getServiceInformation(registeredService).registerService(deviceServiceUnit.getDeviceID(),
                    listenerID, listenerType, serviceType);
            //operatingSystem.getServiceManager().exitContext();
        }

    }
   

    public void unregisterAtService(
            ServiceContext registerServiceContext, ListenerID listenerID, Class serviceType) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(hostedService);
            if (serviceManager.hasService(registerServiceContext.getServiceID())){
                serviceManager.getServiceInformation(registerServiceContext).unregisterService(deviceServiceUnit.getDeviceID(),listenerID,serviceType);
            }
            //operatingSystem.getServiceManager().exitContext();
        }

    }
    
    //
    public List getRegisteredListeners(ServiceContext context, Class receiverServiceClass) throws RemoteException {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(context.getCallerService());
            //ListenerID[] listenerIDs=
            return serviceManager.getServiceInformation(context).getRegisteredListeners(deviceServiceUnit.getDeviceID(),receiverServiceClass);//erviceID,listenerID,serviceType);
            //operatingSystem.getServiceManager().exitContext();
            //return listenerIDs;
        }
        
        
    }

//    /* (non-Javadoc)
//     * @see de.uni_trier.jana.hybrid.remote.manager.RemoteOperatingSystemServer#shutdown(de.uni_trier.jane.basetypes.ServiceID)
//     */
//    public void shutdown(ServiceContext hostedService) {
//        synchronized (syncObject) {
//            //operatingSystem.getServiceManager().enterContext(hostedService);
//            serviceManager.getOperatingSystem(hostedService).shutdown();
//            //operatingSystem.getServiceManager().exitContext();
//        }
//        
//    }
//    /* (non-Javadoc)
//     * @see de.uni_trier.jana.hybrid.remote.manager.RemoteOperatingSystemServer#reboot(de.uni_trier.jane.basetypes.ServiceID)
//     */
//    public void reboot(ServiceContext hostedService) {
//        synchronized (syncObject) {
//            //operatingSystem.getServiceManager().enterContext(hostedService);
//            serviceManager.getOperatingSystem(hostedService).reboot();
//            //operatingSystem.getServiceManager().exitContext();
//        }
//    }
//    
   
    
    
  
    public Object accessSynchronous(ServiceContext hostedService, ListenerID requestedService, ListenerAccess serviceAccess) throws RemoteException {
            synchronized (syncObject) {
                //operatingSystem.getServiceManager().enterContext(hostedService);
                
                Object object = serviceManager.getSignalManager().accessSynchronous(hostedService,(ServiceID)requestedService,serviceAccess);
                //operatingSystem.getServiceManager().exitContext();
                return object;
            }
    }
	
    
    
    
	public void addService(ServiceContext callerContext, ServiceContext serviceContext, DefaultParameters params, Class serviceClass, boolean visualize) throws RemoteException {
		synchronized (syncObject) {
		    RemoteService service=new RemoteService(serviceContext,
		            	params,
		            	serviceClass,
		            	remoteClient,
		            	serviceManager.getExecutionManager(),
		            	this,
		            	syncObject); 
		    
			serviceManager.startRemoteService(callerContext,service,visualize);
			//((RemoteSignalServer)serviceManager.getSignalManager()).addRemoteListener(new ServiceContext(serviceID,deviceServiceUnit.getDeviceID()),serviceID,serviceClass,false);
			//((RemoteSignalServer) ServiceThread serviceThread=(ServiceThread).getSignalManager()).addRemoteListener(serviceID,serviceID,false);
		}
		
	}
	
	public void hybridCallFailed(ListenerID listenerID){
//	    boolean exitContext = false;
        if (!serviceManager.getSignalManager().hasListener(listenerID)) return;
        de.uni_trier.jane.service.operatingSystem.ServiceContext serviceContext=serviceManager.getSignalManager().getContextForListener(listenerID);
        
	    if (serviceManager.hasService(serviceContext.getServiceID())){
	        serviceManager.getOperatingSystem(serviceContext).finishService(serviceContext.getServiceID());
//	        if (operatingSystem.getServiceManager().getRunningService()==null){
//	            operatingSystem.getServiceManager().enterContext(serviceID);
//	            exitContext = true;
//	        }
//	        operatingSystem.finishService(serviceID);
	    }

//	    if (exitContext){
//	        operatingSystem.getServiceManager().exitContext();
//	    }
	    
	}
    /**
     * TODO Comment method
     * @param serviceContext
     */
    public void hybridFinishFailed(ServiceContext serviceContext) {
        
        
        serviceManager.getExecutionManager().schedule(new Action(serviceContext,serviceContext) {
            public void execute(Service executingService) {
                finishedServices.add(getExecutingServiceID());
                pingClient();        
            }
        });
        try {
            endFinish(serviceContext.getServiceID());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
    }
	
	 void pingClient() {
	     	List list= new ArrayList(Arrays.asList(serviceManager.getRemoteServices()));
	     	list.removeAll(finishedServices);
	     	if (list.isEmpty()){
		        listener.shutdown();
		    }else{
		        try{
		           remoteClient.ping();         
		        }catch(RemoteException e){
		            Iterator iterator=list.iterator();
		            while (iterator.hasNext()) {
                        ServiceID element = (ServiceID) iterator.next();
                        
                        try {
                            finishService(new ServiceContext(OperatingSystem.OperatingSystemID,getDeviceID()),element);
                        } catch (RemoteException e1) {/* locally!can be ignored */}  
                    }  
		              
		           
		        }
		    }
         
     }
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.local.RemoteOperatingSystemServer#getDeviceServiceUnit()
	 */
	public DefaultServiceUnit getDeviceServiceUnit() throws RemoteException {

		return deviceServiceUnit;
	}
	
    public boolean hasService(ServiceID serviceID) throws RemoteException {
		synchronized (syncObject) {
		    return serviceManager.hasService(serviceID);
		}
    }
    //
    public boolean hasService(Class serviceClass) throws RemoteException {
		synchronized (syncObject) {
		    return serviceManager.hasService(serviceClass);
		}
    }
    //
    public ServiceID[] getServiceIDs(Class serviceClass) throws RemoteException {
		synchronized (syncObject) {
		    return serviceManager.getServiceIDs(serviceClass);
		}
    }
    public ServiceID[] getServiceIDs() throws RemoteException {
        synchronized (syncObject) {
		    return serviceManager.getServiceIDs();
		}
    }
    public boolean isAllowed(ServiceID receiverServiceID, ServiceID senderService) throws RemoteException {
        synchronized (syncObject) {
		    return serviceManager.isAllowed(receiverServiceID,senderService);
		}
    }
    
    public void addFinishListener( ListenerID listenerID, final ListenerHandlerID pair) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(pair.getExecutingService());

            ListenerFinishedHandler listener=new ListenerFinishedHandler() {
          
                public void handleFinished(ListenerID listenerID) {
                    try {
                        remoteClient.notifyListenerFinished(pair,serviceManager.getExecutionManager().getCallerContext());
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            
            };
            finishListenerMap.put(pair,listener);
            serviceManager.getOperatingSystem(pair.getExecutingContext()).addListenerHandler(listenerID,listener);
            //operatingSystem.getServiceManager().exitContext();
        }    
    }
    
    public void removeFinishListener(ListenerID listenerID, ListenerHandlerID id) {
        synchronized (syncObject) {
            serviceManager.getSignalManager().removeFinishListener(listenerID,(ListenerFinishedHandler)finishListenerMap.remove(id));
        }
        
    }
    
    public boolean registerListener(ServiceContext serviceContext, ListenerID listenerID,Class listenerClass, boolean oneShot) {
        synchronized (syncObject) {
            return ((RemoteSignalServer)serviceManager.getSignalManager()).addRemoteListener(serviceContext,listenerID,listenerClass,oneShot);
                    
                    
        }
    }
    public void unregisterListener(ServiceContext serviceContext, ListenerID listenerID) throws RemoteException {
        synchronized (syncObject) {
            ((RemoteSignalServer)serviceManager.getSignalManager()).removeRemoteListener(serviceContext,listenerID);
        }
        
    }
    
    
    
    public void sendSignal(ServiceContext serviceContext, ListenerID receiver, Signal signal) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(serviceContext.getCallerService());
            serviceManager.getSignalManager().sendSignal(serviceContext,receiver,signal);
            //operatingSystem.getServiceManager().exitContext();
        }
            

        
    }
    public void sendSignal(ServiceContext serviceContext,List listenerIDs, Signal signal) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(serviceContext.getCallerService());
            serviceManager.getSignalManager().sendSignal(serviceContext,listenerIDs,signal);
            //operatingSystem.getServiceManager().exitContext();
        }
        
    }
    
    public void finish(ListenerID receiver, ServiceContext callerContext) {
        synchronized (syncObject) {
            //operatingSystem.getServiceManager().enterContext(callerContext.getCallerService());
            serviceManager.getSignalManager().finish(callerContext,receiver);
            //operatingSystem.getServiceManager().exitContext();
        }
            
        
    }
    //
    public boolean hasListener(ListenerID listenerID) throws RemoteException {
        synchronized(syncObject){
            return serviceManager.getSignalManager().hasListener(listenerID);
        }
    }
    
    

    public void endFinish(ServiceID serviceID) throws RemoteException {
        synchronized (syncObject) {
            serviceManager.getExecutionManager().endFinish(serviceID);
        }

    }
    /**
     * TODO Comment method
     * 
     */
    public void simulationShutdown() {
        try {
            remoteClient.simulationShutdown();
        } catch (RemoteException e) {
            // TODO is that nessessary?
            //e.printStackTrace();
        }
        
    }
    

    public void shutdown(ServiceContext callerContext) throws RemoteException {
        synchronized (syncObject) {
            shutdownManager.shutdown(callerContext);
        }

    }
    
    

    public void reboot(ServiceContext callerContext) throws RemoteException {
        synchronized (syncObject) {
            shutdownManager.reboot(callerContext);
        }

    }

	public boolean hasSignalListenerStub(ServiceContext context,ListenerID listenerID, Class listenerClass) throws RemoteException {
		synchronized (syncObject) {
			 //ServiceContext callerContext=serviceManager.getSignalManager().getContextForListener(listenerID);
			 //if (callerContext!=null){
			 	return serviceManager.getServiceInformation(context).hasSignalListenerStub(listenerID , listenerClass); 
			 //}
			 //return false;
		}
	}

	public boolean hasAccessListenerStub(ServiceContext context,ListenerID listenerID, Class listenerClass) throws RemoteException {
		synchronized (syncObject) {
			 //ServiceContext callerContext=serviceManager.getSignalManager().getContextForListener(listenerID);
			 //if (callerContext!=null){
			 	return serviceManager.getServiceInformation(context).hasAccessListenerStub(listenerID , listenerClass); 
			 //}
			 //return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.server.RemoteSignalManagerServer#getContextForListener(de.uni_trier.jane.basetypes.ListenerID)
	 */
	public ServiceContext getContextForListener(ListenerID listenerID)
			throws RemoteException {
		synchronized (syncObject) {
			return serviceManager.getSignalManager().getContextForListener(listenerID);
		}
		
	}
	
	



    
  
    


	
	public void addAccessStub(ListenerID listenerID, Class classToRegister)
			throws RemoteException {
		synchronized (syncObject) {
			 ServiceContext callerContext=serviceManager.getSignalManager().getContextForListener(listenerID);
			 if (callerContext!=null){
			 	 serviceManager.getServiceInformation(callerContext).addAccessStub(listenerID , classToRegister); 
			 }else{
			 	throw new OperatingServiceException("The given listnerID does not exist");
			 }
			 
			
		}

	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer#addSignalStub(de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public boolean addSignalStub(ListenerID listenerID, Class classToRegister)
			throws RemoteException {
		synchronized (syncObject) {
			 ServiceContext callerContext=serviceManager.getSignalManager().getContextForListener(listenerID);
			 if (callerContext!=null){
			 	 return serviceManager.getServiceInformation(callerContext).addSignalStub(listenerID , classToRegister); 
			 }
			 throw new OperatingServiceException("The given listnerID does not exist");
		}
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer#removeAccessStub(de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public void removeAccessStub(ServiceContext callerContext,ListenerID listenerID, Class classToRegister)
			throws RemoteException {
		synchronized (syncObject) {
			ServiceInformation info=serviceManager.getServiceInformation(callerContext);
			if (info!=null){
				info.removeAccessStub(listenerID , classToRegister);
			}
		}

	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer#removeSignalStub(de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public void removeSignalStub(ServiceContext callerContext,ListenerID listenerID, Class classToRegister)
			throws RemoteException {
		synchronized (syncObject) {
			ServiceInformation info=serviceManager.getServiceInformation(callerContext);
			if (info!=null){
				info.removeSignalStub(listenerID , classToRegister);
			}
		}

	}
    
    public ListenerID addAutogenListener(ServiceContext context, Class listenerClass) throws RemoteException {
        synchronized (syncObject) {
            return ((RemoteSignalServer)serviceManager.getSignalManager()).addAutogenListener(context,listenerClass);
        }
        
    }
    
    
    public boolean sendEvent(ServiceContext senderContext, ServiceEvent serviceEvent, Class senderClass) throws RemoteException {
        synchronized (syncObject) {
            return ((RemoteSignalServer)serviceManager.getSignalManager()).sendEvent(senderContext,serviceEvent,senderClass);
        }
    }
    
    public void registerEventListener(ServiceContext context, ServiceEvent eventByExample, ListenerID listenerID) throws RemoteException {
        synchronized (syncObject) {
             ((RemoteSignalServer)serviceManager.getSignalManager()).registerEventListener(context,eventByExample,listenerID);
        }
        
    }
    
    
}
