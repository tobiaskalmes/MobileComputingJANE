/*****************************************************************************
 * 
 * ServiceInformation.java
 * 
 * $Id: ServiceInformation.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.util.List;
import java.util.Set;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.shapes.Shape;


/**
 * 
 * TODO: comment class  
 * @author daniel
 *
 */
public interface ServiceInformation  extends AccessManager{
    
    
    /**
     * 
     * TODO: comment method 
     * @return
     */
	public ServiceID getServiceID();
   
 
    /**
     * @return Returns the deviceID.
     */
    public DeviceID getDeviceID();
    
    

    /**
     * 
     * TODO Comment method
     * @param deviceID
     * @param listenerID
     * @param serviceType
     */
    public void registerService(DeviceID deviceID, ListenerID listenerID, Class listenerClass, Class serviceType);
    
    /**
     * 
     * TODO Comment method
     * @param deviceID
     * @param listenerID
     * @param serviceType
     */
    void unregisterService(DeviceID deviceID, ListenerID listenerID, Class serviceType);
    
    /**
     * 
     * TODO Comment method
     * @param deviceID
     * @param receiverServiceClass
     * @return
     */
    public List getRegisteredListeners(DeviceID deviceID,Class receiverServiceClass);


    /**
     * TODO: comment method 
     * @param listenerID
     * @param listenerClass
     * @return
     */
    public Object getSignalStub(ServiceContext callerContext,ListenerID listenerID, Class listenerClass);


    /**
     * TODO: comment method 
     * @param listenerID
     * @param listenerClass
     * @return
     */
    public Object getAccessStub(ServiceContext callerContext,ListenerID listenerID, Class listenerClass);


	/**
	 * @param listenerID
	 * @param listenerClass
	 * @return
	 */
	public boolean hasSignalListenerStub(ListenerID listenerID, Class listenerClass);


	/**
	 * @param listenerID
	 * @param listenerClass
	 * @return
	 */
	public boolean hasAccessListenerStub(ListenerID listenerID, Class listenerClass);

	public boolean addSignalStub( ListenerID listenerID, Class classToRegister);
	public void addAccessStub( ListenerID listenerID, Class classToRegister);
	public void removeAccessStub(ListenerID listenerID, Class classToRegister);
	public void removeSignalStub(ListenerID listenerID, Class classToRegister);


	/**
	 * @return
	 */
	public ServiceContext getContext();


	/**
	 * @return
	 */
	public Class getServiceClass();


	/**
	 * @return
	 */
	public Service getService();


	/**
	 * @param operatingSystem
	 */
	public void setOperatingSystem(RuntimeOperatingSystemImpl operatingSystem);


	/**
	 * @return
	 */
	public RuntimeOperatingSystemImpl getOperatingSystem();


	/**
	 * @return
	 */
	public Shape getShape();


    public boolean isVisualized();


    /**
     * TODO Comment method
     * @param visualize
     * @return
     */
    public void setVisualized(boolean visualize);



    
   


}