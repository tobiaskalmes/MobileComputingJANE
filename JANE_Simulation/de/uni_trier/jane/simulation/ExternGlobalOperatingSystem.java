/*****************************************************************************
 * 
 * ExternGlobalOperatingSystem.java
 * 
 * $Id: ExternGlobalOperatingSystem.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.operating_system.*;
import de.uni_trier.jane.simulation.service.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class ExternGlobalOperatingSystem {

    
    private SyncObject syncObject;
    private DeviceServiceManager deviceServiceManager;

    /**
     * Constructor for class ExternGlobalOperatingSystem 
     *
     * @param serviceCollection
     * @param syncObject
     * @param deviceServiceManager
     */
    public ExternGlobalOperatingSystem(ServiceCollection serviceCollection, SyncObject syncObject, DeviceServiceManager deviceServiceManager) {
        this.deviceServiceManager=deviceServiceManager;
        this.syncObject=syncObject;
        Iterator iterator = serviceCollection.getServiceIDs().iterator();
        while (iterator.hasNext()){
            ServiceID serviceID=(ServiceID)iterator.next();
            Service service=serviceCollection.getService(serviceID);
            if (service instanceof GlobalService){
            
                //((GlobalService)iterator.next()).start(this);
            }else{
                throw new OperatingServiceException("Only global services can be started to control the simulation");
            }
        }

    }

}
