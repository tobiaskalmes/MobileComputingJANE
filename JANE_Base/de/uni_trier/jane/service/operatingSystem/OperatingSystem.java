/*****************************************************************************
 * 
 * OperatingSystem.java
 * 
 * $Id: OperatingSystem.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem;



import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.operatingSystem.manager.*;




/**
 * 
 * @author goergen
 *
 * Global Access to some OperatingSytem methods
 * work in progress: This class still not contain all methods that are accessible globally
 * The getRuntimeEnvironment(ServiceID) method is ugly, since every service can call it for other services
 * In some cases, a getRuntimeEnvironment() without any argument is not possible (user interaction, events that are not executed internally)
 */
public class OperatingSystem {
	
   /**
 	*	This ServiceID is used, when no other service is responsible for an event
 	*   e.g. at boot time, the method getCallerServiceID() returns this ID.  
 	*/
    public static final ServiceID OperatingSystemID = new OperatingServiceID();
    
    
    private static DeviceServiceManager theServiceManager;
	
    /**
     * Used internally 
     * Do not use this method if you do not know what you are doing...
     * @param serviceManager
     */
	public static void setServiceManager(DeviceServiceManager serviceManager){
	   theServiceManager=serviceManager;
	}
	
	/**
	 * Returns the current context if available
	 * The context is not available if a handler method is called due to an extern event 
	 * that is not handled by the internal execution manager.
	 * Such extern events can be caused by user interaction or extern threads
	 * @return the current context
	 */
	public static ServiceContext getCurrentContext(){
        if (theServiceManager==null) return null; 
	    return theServiceManager.getExecutionManager().getExecutionContext();
	}
	
	/**
	 * Returns the RuntimeEnvironment for the given ServiceID
	 * TODO should not be accessible... 
	 * 
	 * @param serviceID
     * @deprecated it should not be possible to get any OperatingSystem and thus each possible service context
	 * @return	the RuntimeEnvironment of the given service
     * 
	 */
	public static RuntimeEnvironment getRuntimeEnvironment(ServiceID serviceID){
		return theServiceManager.getOperatingSystem(new ServiceContext(serviceID,theServiceManager.getDeviceID()));
	}
    
    
    /**
     * Returns the current runtime environment.
     * It is not available if a handler method is called due to an extern event 
     * that is not handled by the internal execution manager.
     * Such extern events can be caused by user interaction or extern threads.
     * The method returns null in such cases.
     * @return the current runtimeEnvironment if set or null
     */
    public static RuntimeEnvironment getRuntimeEnvironment(){
        if (theServiceManager==null)return null;
        return theServiceManager.getOperatingSystem(getCurrentContext());
    }
	
	/**
	 * Returns the current time on a device
	 * 
	 * @return the current time
	 */
    public static double getTime() {
        return theServiceManager.getClock().getTime();
    }
    
    /**
     * Returns the deviceID 
     * @return the deviceID
     */
    public static DeviceID getDeviceID(){
    	return theServiceManager.getDeviceID();
    }
    
    /**
     * Returns true, if the operating system runs a service with the given ID
     * @param serviceID	the serviceID to check
     * @return true, if the service for the given serviceID exists
     */
    public static boolean hasService(ServiceID serviceID){
        return theServiceManager.hasService(serviceID);
    }
    
    /**
     * Returns true, if the operating system runs a service implementing the given class
     * @param serviceClass	the class to check
     * @return true, if a service implementing the given class exists
     */
    public static boolean hasService(Class serviceClass){
        return theServiceManager.hasService(serviceClass);
    }
    
    
    
	
	

}
