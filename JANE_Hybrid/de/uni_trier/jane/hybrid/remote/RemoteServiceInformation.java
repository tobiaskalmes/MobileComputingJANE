/*****************************************************************************
 * 
 * RemoteServiceInformation.java
 * 
 * $Id: RemoteServiceInformation.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote; 

import java.lang.reflect.Proxy;
import java.rmi.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.server.*;
import de.uni_trier.jane.reflectionSignal.AccessListenerProxy;
import de.uni_trier.jane.reflectionSignal.ListenerStub;
import de.uni_trier.jane.reflectionSignal.SignalListenerProxy;

import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.visualization.shapes.Shape;


/**
 * @author goergen
 *
 * TODO comment class
 */
public class RemoteServiceInformation implements ServiceInformation {

    private RemoteOperatingSystemServer remoteOperatingSystem;
    private ServiceContext context;
	private ServiceManager serviceManager;
	private Class serviceClass;
	private Shape preparedShape;
	private Service service;
	private RuntimeOperatingSystemImpl operatingSystem;

    /**
     * Constructor for class <code>RemoteServiceInformation</code>
     * @param remoteOperatingSystem
     * @param context
     */
    public RemoteServiceInformation(RemoteOperatingSystemServer remoteOperatingSystem, ServiceContext context, Service service,Class serviceClass,ServiceManager serviceManager) {
    	this.serviceClass=serviceClass;
        this.remoteOperatingSystem=remoteOperatingSystem;
        this.context=context;
        this.serviceManager=serviceManager;
        this.service=service;
        
    }

    public ServiceID getServiceID() {
        
        return context.getServiceID();
    }

    public DeviceID getDeviceID() {
     
        return context.getServiceDeviceID();
    }
    //
    public void registerService(DeviceID deviceID,ListenerID listenerID,Class listenerClass,
            Class serviceType) {
        try {
            remoteOperatingSystem.registerAtService(context,listenerID,listenerClass,serviceType);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }
    
    //
    public void unregisterService(DeviceID deviceID, ListenerID listenerID, Class serviceType) {
        try {
            remoteOperatingSystem.unregisterAtService(context,listenerID,serviceType);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //
    public List getRegisteredListeners(DeviceID deviceID,Class receiverServiceClass) {
        try{
            return remoteOperatingSystem.getRegisteredListeners(context,receiverServiceClass);
        }catch  (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }


    //
    public void denyAllServices() {
        try {
            remoteOperatingSystem.denyAllServices(context);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    //
    public void denyService(ServiceID service) {
        try {
            remoteOperatingSystem.denyService(context,service);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    //
    public void allowService(ServiceID service) {
        try {
            remoteOperatingSystem.allowService(context,service);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    //
    public void allowAllServices() {
        try {
            remoteOperatingSystem.allowAllServices(context);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    

    public boolean isAllowed(ServiceID senderService) {
        try {
            return remoteOperatingSystem.isAllowed(context.getServiceID(),senderService);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
        
    }

	
	public Object getSignalStub(ServiceContext callerContext,ListenerID listenerID, Class listenerClass) {
		try {
			if (!remoteOperatingSystem.hasSignalListenerStub(context,listenerID,listenerClass)){
			     throw new OperatingServiceException("A signal listener stub for the given listenerID implementing " +
				"the given listener class does not exist");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RuntimeOperatingSystemImpl runtimeEnvironment=serviceManager.getOperatingSystem(callerContext);
		List classList=new ArrayList();
        classList.add(ListenerStub.class);
        if (listenerClass.isInterface()){
        	classList.add(listenerClass);
        	
        }else{
        	classList.addAll(Arrays.asList(listenerClass.getInterfaces()));
        	
        }
        return Proxy.newProxyInstance(getClass().getClassLoader(),(Class[])classList.toArray(new Class[classList.size()]),
                new SignalListenerProxy(listenerID,callerContext,getContext(),listenerClass,runtimeEnvironment));
	}

	
	public Object getAccessStub(ServiceContext callerContext,ListenerID listenerID, Class listenerClass) {
		try {
			if (!remoteOperatingSystem.hasAccessListenerStub(context,listenerID,listenerClass)){
				 throw new OperatingServiceException("A signal listener stub for the given listenerID implementing " +
			 		"the given listener class does not exist");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RuntimeEnvironment runtimeEnvironment=serviceManager.getOperatingSystem(callerContext);
		return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{listenerClass},
					new AccessListenerProxy(listenerID,listenerClass,runtimeEnvironment));
	}
	
	
	
	public boolean hasAccessListenerStub(ListenerID listenerID, Class listenerClass) {
	
		try {
			return remoteOperatingSystem.hasAccessListenerStub(context,listenerID,listenerClass);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean hasSignalListenerStub(ListenerID listenerID, Class listenerClass) {
	
		try {
			return remoteOperatingSystem.hasSignalListenerStub(context,listenerID,listenerClass);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#addAccessStub(de.uni_trier.jane.signaling.SignalListener, de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public void addAccessStub( ListenerID listenerID,
			Class classToRegister) {
		try {
			remoteOperatingSystem.addAccessStub(listenerID,classToRegister);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#addSignalStub(de.uni_trier.jane.signaling.SignalListener, de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public boolean addSignalStub( ListenerID listenerID,
			Class classToRegister) {
		try {
			return remoteOperatingSystem.addSignalStub(listenerID,classToRegister);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;

	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#removeAccessStub(de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public void removeAccessStub(ListenerID listenerID, Class classToRegister) {
		try {
			if (!listenerID.equals(context.getServiceID())){
				remoteOperatingSystem.removeAccessStub(context,listenerID,classToRegister);
			}//else: the service itself has already be deleted...
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#removeSignalStub(de.uni_trier.jane.basetypes.ListenerID, java.lang.Class)
	 */
	public void removeSignalStub(ListenerID listenerID, Class classToRegister) {
		try {
			if (!listenerID.equals(context.getServiceID())){
				
				remoteOperatingSystem.removeSignalStub(context,listenerID,classToRegister);
			}//else: the service itself has already be deleted...
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public ServiceContext getContext() {
		return context;
	}

	public Class getServiceClass() {
		if (serviceClass==null){
			
	        throw new OperatingServiceException("Illegal state");
	        
		}
		return serviceClass;
	}


	public void prepareShape() {
		if (service!=null){
            try{
                preparedShape=service.getShape();
            }catch (Exception e) {
                System.err.println("TODO: get remote service's shape before service start catched");
            }
		}
		
	}

	
	public Shape getPreparedShape() {

		return preparedShape;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#getService()
	 */
	public Service getService() {
		if (service==null){
			throw new IllegalStateException("This is only a remote instance of a service. This methd can only be called when a service is local!");
		}
		return service;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#setOperatingSystem(de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem)
	 */
	public void setOperatingSystem(RuntimeOperatingSystemImpl operatingSystem) {
		this.operatingSystem=operatingSystem;

	}
	
	
	/**
	 * @return Returns the operatingSystem.
	 */
	public RuntimeOperatingSystemImpl getOperatingSystem() {
		return operatingSystem;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.operatingSystem.manager.ServiceInformation#getShape()
	 */
	public Shape getShape() {
		if (service==null){
			throw new IllegalStateException("This is only a remote instance of a service. This methd can only be called when a service is local!");
		}
		return service.getShape();
	}
    
    public boolean isVisualized() {
     //TODO:
        return true;
    }
    
    public void setVisualized(boolean visualize) {
        // TODO Auto-generated method stub
        //      TODO:
    }
    

  
}
