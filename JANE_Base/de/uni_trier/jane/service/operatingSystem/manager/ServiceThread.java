/*****************************************************************************
 * 
 * ServiceThread.java
 * 
 * $Id: ServiceThread.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem.manager;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.reflectionSignal.*;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.util.HashMapSet;
import de.uni_trier.jane.visualization.shapes.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * 
 * @author goergen
 *
 * TODO comment class
 */
public class ServiceThread implements ServiceInformation {


	

    private Service service;
   // private Set registeredServices;
    private Map deviceRegisteredServiceMap;
    private Set senderSet;
    private boolean allowAllMode;
    private boolean visualize;
	private Class serviceClass;
   // private RuntimeOperatingSystemImpl runtimeOperatingSystemImpl;
    private ServiceContext serviceContext;
    
    private RuntimeOperatingSystemImpl operatingSystem;
    private Shape shape;
    private HashMapSet signalProxys;
    private HashMapSet accessProxies;
	private ServiceManager serviceManager;
    
    /**
     * 
     * Constructor for class <code>ServiceThread</code>
     * @param serviceContext
     * @param service
     * @param serviceClass
     * @param signalManager
     * @param visualize
     */
	public ServiceThread(ServiceContext serviceContext, Service service, Class serviceClass, boolean visualize, ServiceManager serviceManager) {
     	this.serviceContext=serviceContext;
     	
     	
        this.service = service;
       // registeredServices = new HashSet();
        deviceRegisteredServiceMap = new HashMap();
        senderSet = new HashSet();
        allowAllMode = true;
        this.visualize=visualize;
        this.serviceClass=serviceClass;
        signalProxys=new HashMapSet();
        accessProxies=new HashMapSet();
        this.serviceManager=serviceManager;
        //autogenSignalProxies();
	}
    
//	private void autogenSignalProxies(Class classToGen)) {
//        if (classToGen.isInterface()){
//            signalProxys.put(serviceContext.getServiceID(),classToGen);
//        }
//	    Class[] ifs=classToGen.getInterfaces();
//        for (int i=0;i<ifs.length;i++){
//            sign
//        }
//        
//    }

    /**
	 * 
	 * Constructor for class <code>ServiceThread</code>
	 *
	 * @param serviceID
	 * @param service
	 * @param visualize
	 */
    //public ServiceThread(ServiceID serviceID, Service service,RuntimeOperatingSystemImpl runtimeOperatingSystemImpl, boolean visualize) {
    //	this(serviceID,service,service.getClass(),runtimeOperatingSystemImpl,visualize);
    //}

    /**
     * 
     * TODO: comment method 
     * @return
     */
	public ServiceID getServiceID() {
        return serviceContext.getServiceID();
    }
	
	//
    public DeviceID getDeviceID() {
     
        return serviceContext.getServiceDeviceID();
    }
	
	/**
     * @return Returns the serviceContext.
     */
    public ServiceContext getContext() {
        return serviceContext;
    }
	
	/**
	 * 
	 * TODO: comment method 
	 * @return
	 */
	public Class getServiceClass() {
		return serviceClass;
	}

	/**
	 * 
	 * TODO: comment method 
	 * @return
	 */
	public Service getService() {
        return service;
    }

	/**
	 * 
	 * TODO: comment method 
	 * @param listenerID
	 * @param serviceType
	 */
//    public void registerService(ListenerID listenerID, Class serviceType) {
//        checkInstanceOf(serviceType);
//        registeredServices.add(listenerID);
//    }
//
//    /**
//     * 
//     * TODO: comment method 
//     * @param listenerID
//     */
//	public void unregisterService(ListenerID listenerID, Class serviceType) {
//	    checkInstanceOf(serviceType);
//        registeredServices.remove(listenerID);
//    }

	/**
	 * 
	 * TODO: comment method 
	 * @param senderDevice
	 * @param senderService
	 * @param serviceType
	 */
	public void registerService(DeviceID senderDevice, ListenerID senderService,Class listenerClass, Class serviceType) {
		checkInstanceOf(serviceType);
		Map map = (Map)deviceRegisteredServiceMap.get(senderDevice);
		if(map == null) {
		    map = new HashMap();
			deviceRegisteredServiceMap.put(senderDevice, map);
		}
		map.put(senderService,listenerClass);
	}

	/**
	 * 
	 * TODO: comment method 
	 * @param senderDevice
	 * @param senderService
	 * @param serviceType
	 */
	public void unregisterService(DeviceID senderDevice, ListenerID senderService, Class serviceType) {
	    checkInstanceOf(serviceType);
		Map map = (Map)deviceRegisteredServiceMap.get(senderDevice);
		if(map != null) {
		    map.remove(senderService);
			if(map.isEmpty()) {
				deviceRegisteredServiceMap.remove(senderDevice);
			}
		}
	}
	
	
	/**
	 * 
	 * TODO: comment method 
	 *
	 */
	public void denyAllServices() {
		allowAllMode = false;
		senderSet.clear();
	}
	
	
	/**
	 * 
	 * TODO: comment method 
	 * @param service
	 */
	public void denyService(ServiceID service) {
		if(allowAllMode) {
			senderSet.add(service);
		}
		else {
			senderSet.remove(service);
		}
	}
	
	/**
	 * 
	 * TODO: comment method 
	 * @param service
	 */
	public void allowService(ServiceID service) {
		if(allowAllMode) {
			senderSet.remove(service);
		}
		else {
			senderSet.add(service);
		}
	}
	
	/**
	 * 
	 * TODO: comment method 
	 *
	 */
	public void allowAllServices() {
		allowAllMode = true;
		senderSet.clear();
	}
	

    public boolean isAllowed(ServiceID senderService) {
     
		if(allowAllMode) {
			return !senderSet.contains(senderService);
		}
		
		return senderSet.contains(senderService);
		
	}

//	/**
//	 * 
//	 * TODO: comment method 
//	 * @param serviceID
//	 * @return
//	 */
//    public boolean isRegistered(ServiceID serviceID) {
//        return registeredServices.contains(serviceID);
//    }
//
//    /**
//     * 
//     * TODO: comment method 
//     * @param serviceType
//     * @return
//     */
//    public ListenerID[] getRegisteredListeners(Class serviceType) {
//        return getRegisteredServices(registeredServices, serviceType);
//    }

    /**
     * 
     * TODO: comment method 
     * @param senderDevice
     * @param serviceType
     * @return
     */
	public List getRegisteredListeners(DeviceID senderDevice, Class serviceType) {
	    Map registeredSet = (Map)deviceRegisteredServiceMap.get(senderDevice);
	    if (registeredSet!=null){
	        return getRegisteredServices(registeredSet, serviceType);
	    }
	    return new ArrayList();
	}

	/**
	 * 
	 * TODO: comment method 
	 * @param registeredSet
	 * @param serviceType
	 * @return
	 */
	protected List getRegisteredServices(Map registeredMap, Class serviceType) {
	    //LocalSignalManager signalManager=runtimeOperatingSystemImpl.getSignalManager();
        List result = new ArrayList();
		Iterator iterator = registeredMap.keySet().iterator();
        while (iterator.hasNext()) {
            ListenerID registeredListenerID = (ListenerID) iterator.next();
            //ServiceThread serviceThread = (ServiceThread)idServiceThreadMap.get(registeredServiceID);
            if(serviceType.isAssignableFrom((Class)registeredMap.get(registeredListenerID))){//ListenerClass(registeredListenerID))) {
                result.add(registeredListenerID);
            }
        }
        return result;//(ListenerID[])result.toArray(new ListenerID[result.size()]);
	}

	public void checkInstanceOf(Class serviceType) {
		if(!serviceType.isAssignableFrom(serviceClass)) {
            throw new OperatingServiceException(
                    "The service does not implement the given service type.");
        }
	}
	
	public Shape getShape(){
	    if (visualize&&operatingSystem!=null){
	    	
	    	// TODO: der Folgende try-catch Block ist nur ?bergangsweise.
	    	// Das Problem, dass bei start der Service noch nicht gestartet sein k?nnte,
	    	// sollte richtig gefixed werden!
	    	//try {
		        return service.getShape();
	    	//}
	    	//catch(Exception exception) {
	    	//	System.err.println("TODO: Shape wurde vor Servicestart abgefragt!!!");
	    	//	exception.printStackTrace();
			 //   return EmptyShape.getInstance();
	    	//}

	    }
	    return EmptyShape.getInstance();
	}

    /**
     * @param operatingSystem the operatingSystem to set
     */
    public void setOperatingSystem(RuntimeOperatingSystemImpl operatingSystem) {
        this.operatingSystem=operatingSystem;
       // prepareShape();
        
    }
    
    /**
     * @return Returns the operatingSystem.
     */
    public RuntimeOperatingSystemImpl getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * TODO Comment method
     * 
     */
    public void prepareShape() {
    	shape=getShape();
        
    }
    public boolean isVisualized() {
     
        return visualize;
    }
    
    public void setVisualized(boolean visualize) {
        this.visualize=visualize;
    }

    /**
     * TODO Comment method
     * @return
     */
    public Shape getPreparedShape() {

        return shape;
    }

    /**
     * TODO: comment method 
     * @param listener
     * @param listenerID
     * @param classToRegister
     */
    public boolean addSignalStub( ListenerID listenerID, Class classToRegister) {
        Method[] methods= classToRegister.getMethods();
    	for (int i=0;i<methods.length;i++){			
			if (!methods[i].getReturnType().equals(Void.TYPE)&&
                    !methods[i].getDeclaringClass().equals(Object.class)&&!Proxy.isProxyClass(classToRegister)){
				//throw new OperatingServiceException("Registered SignalListener Object contains non void methods");
                Class[] ifs=classToRegister.getInterfaces();
                Class s=classToRegister.getSuperclass();
                System.err.println("Registered SignalListener Object contains non void methods");
			}
    	}
       // Object signalListener= Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{classToRegister,SignalProxy.class},
       //         new SignalListenerProxy(listenerID,classToRegister,false));
        if (methods.length>0){
            signalProxys.put(listenerID,classToRegister);//r,signalListener);
            return true;
        }
        return false;
        
    }

    /**
     * TODO: comment method 
     * @param listenerID
     * @param classToRegister
     */
    public void removeSignalStub(ListenerID listenerID, Class classToRegister) {
        signalProxys.remove(listenerID,classToRegister);
        
    }
    
    //
    public Object getSignalStub(ServiceContext callerContext,ListenerID listenerID,
            Class listenerClass) {

        
        if (!hasSignalListenerStub(listenerID,listenerClass)){
            throw new OperatingServiceException("A signal listener stub for the given listenerID implementing " +
            		"the given listener class does not exist");
        }
        RuntimeOperatingSystemImpl runtimeEnvironment=serviceManager.getOperatingSystem(callerContext);
        Set classList=new HashSet();
        classList.add(ListenerStub.class);
        if (listenerClass.isInterface()){
        	classList.add(listenerClass);
        	
        }else{
        	classList.addAll(Arrays.asList(listenerClass.getInterfaces()));
        	
        }
        return Proxy.newProxyInstance(getClass().getClassLoader(),(Class[])classList.toArray(new Class[classList.size()]),
                new SignalListenerProxy(listenerID,callerContext,getContext(),listenerClass,runtimeEnvironment));
    }

    /**
     * TODO: comment method 
     * @param listenerID
     * @param classToRegister
     */
    public void addAccessStub( ListenerID listenerID, Class classToRegister) {
        accessProxies.put(listenerID,classToRegister);
        
    }
    
    /**
     * TODO: comment method 
     * @param listenerID
     * @param classToRegister
     */
    public void removeAccessStub(ListenerID listenerID, Class classToRegister) {
        accessProxies.remove(listenerID,classToRegister);
        
    }
    
    //
    public Object getAccessStub(ServiceContext callerContext,ListenerID listenerID,
            Class listenerClass) {

        
        if (!hasAccessListenerStub(listenerID,listenerClass)){
            throw new OperatingServiceException("A signal listener stub for the given listenerID implementing " +
            		"the given listener class does not exist");
        }
        RuntimeEnvironment runtimeEnvironment=serviceManager.getOperatingSystem(callerContext);
        return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{listenerClass},
                new AccessListenerProxy(listenerID,listenerClass,runtimeEnvironment));
    }
    
	public boolean hasAccessListenerStub(ListenerID listenerID,
			Class listenerClass) {
	    if(!accessProxies.containsKey(listenerID))return false;
	    
	    Iterator iterator=accessProxies.get(listenerID).iterator();
	    while (iterator.hasNext()){
	        if (listenerClass.isAssignableFrom((Class)iterator.next())){
	            return true;
	        }
	    }
		return false;
	}
	
	public boolean hasSignalListenerStub(ListenerID listenerID,
			Class listenerClass) {
	    
        if (serviceContext.getServiceID().equals(listenerID)){
            return listenerClass.isAssignableFrom(serviceClass);
        }
        if (!signalProxys.containsKey(listenerID)) return false; 
	    Iterator iterator=signalProxys.get(listenerID).iterator();
	    while (iterator.hasNext()){
	        Class testClass=(Class)iterator.next();
	        if (listenerClass.isAssignableFrom(testClass)){
	            return true;
	        }
	    }
		return false;//signalProxys.contains(listenerID,listenerClass);
	}

    
    
    



}