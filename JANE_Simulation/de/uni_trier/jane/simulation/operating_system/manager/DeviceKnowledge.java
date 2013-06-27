/*****************************************************************************
 * 
 * DeviceKnowledge.java
 * 
 * $Id: DeviceKnowledge.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.operating_system.manager; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.simulation.device.*;
import de.uni_trier.jane.simulation.operating_system.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeviceKnowledge {

    private DeviceManager manager;

    /**
     * Constructor for class <code>DeviceKnowledge</code>
     * @param manager
     */
    public DeviceKnowledge(DeviceManager manager) {
        
        this.manager=manager;
    }

    
    /**
     * @param receiverDevice
     * @return
     */
    public LocalSignalManager getSignalManager(DeviceID device) {
        return manager.getMobileDevice(device).getServiceManager().getSignalManager();
    }

 

    /**
     * TODO: comment method 
     * @param receiverDevice
     * @return
     */
    public DeviceServiceManager getServiceManager(DeviceID device) {
        return manager.getMobileDevice(device).getServiceManager();
        
    }

    /**
     * TODO: comment method 
     * @param executingDeviceID
     * @return
     */
    public ExecutionManager getExecutionMananger(DeviceID device) {
        return manager.getMobileDevice(device).getServiceManager().getExecutionManager(); 
        
    }
    

    /**
     * TODO Comment method
     * @param deviceID
     */
    public FinishManager getFinishManager(DeviceID device) {
           return manager.getMobileDevice(device).getServiceManager().getFinishManager();
        
    }


    /**
     * TODO Comment method
     * @param context
     * @return
     */
    public SimulationOperatingSystemImpl getOperatingSystem(ServiceContext context) {

        return (SimulationOperatingSystemImpl) getServiceManager(context.getServiceDeviceID()).getOperatingSystem(context);
    }



   
}
