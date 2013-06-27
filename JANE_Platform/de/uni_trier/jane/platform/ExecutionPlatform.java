/*****************************************************************************
 * 
 * PlatformManager.java
 * 
 * $Id: ExecutionPlatform.java,v 1.1 2007/06/25 07:23:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.platform;


import de.uni_trier.jane.basetypes.*;

import de.uni_trier.jane.platform.basetypes.*;
import de.uni_trier.jane.platform.serviceManager.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;





/**
 * @author goergen
 * JANE Execution platform for real devices.
 * <code>RuntimeService</code> implemented within the JANE simulation can be executed here
 * Overide the method for initializing the Device
 */
public abstract class ExecutionPlatform implements ServiceFactory, DeviceServiceFactory{
    
    private PlatformDeviceID deviceID;
    private DefaultServiceUnit serviceUnit;

    private DistributionCreator distributionCreator;

    /**
     * Initialize the platform parameters
     * Change default paramters by setting them in the given parameters object
     * @param parameters containing	default parameters
     */
    public abstract void initPlatform(PlatformParameters parameters);
    
    
    
    
    /**
     * Starts the platform and boots all services for this device
     * 
     */
    public void run(){
        DefaultPlatformParameters platformParameters=new DefaultPlatformParameters();
        initPlatform(platformParameters);
        
        distributionCreator = platformParameters.getDistributionCreator();
        
        try {
			deviceID=new PlatformDeviceID(Network.getFirstInetAddress());   
		} catch (NetworkException e) {
			e.printStackTrace();
		}
        
        FinishManager finishManager=new FinishManager();
        ShutdownManager shutdownManager=new ShutdownManager(deviceID);
        SyncObject syncObject=new SyncObject();
        ActionHandler platformExecutionManager=
            new ThreadActionHandler(
            	deviceID,shutdownManager,syncObject);
        platformExecutionManager.start();
        
        LocalSignalManager localSignalManager=new LocalSignalManager(platformParameters.getEventReflectionDepth());
             
		DeviceServiceManager operatingSystem=new DeviceServiceManager(
				platformExecutionManager,localSignalManager, //new EventDBImplementation(),
                finishManager,shutdownManager,
		        new TimerSystem(shutdownManager),
		        new RealClock(),deviceID,platformParameters.getDistributionCreator(),
		        platformParameters.getDefaultConsole()
		);        
        
        
        operatingSystem.setServiceFactory(this);
        operatingSystem.notifyStartBoot();
        
        
        
    }
    
    //
    public ServiceCollection getServiceCollection() {
        ServiceCollection serviceCollection=new ServiceCollection();
        serviceUnit=new DefaultServiceUnit(deviceID,serviceCollection, distributionCreator);
        initServices(serviceUnit);
        return serviceCollection;
    }



    //
    public ServiceID checkServiceID(Service service) {
        return serviceUnit.checkServiceID(service);
    }
        
    
    
 
    
}