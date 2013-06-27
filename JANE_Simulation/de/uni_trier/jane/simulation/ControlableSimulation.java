/*****************************************************************************
 * 
 * ControlableSimulation.java
 * 
 * $Id: ControlableSimulation.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.SyncObject;
import de.uni_trier.jane.hybrid.basetypes.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystemImpl;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.operating_system.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public abstract class ControlableSimulation extends Simulation {

    public abstract void initExternGlobalServices(ServiceUnit serviceUnit);
    
    //
    public void run() {
        DefaultSimulationParameters parameters= new DefaultSimulationParameters();
        initSimulation(parameters);
        if (parameters.isUseVisualisationOn()){
            parameters.getSimulationFrame().show(parameters);
        }
        if (!parameters.isHybrid()){
            parameters.setEventSet(new SynchronizedEventSet(parameters.getEventSet()));
        }
        SyncObject syncObject = ((SynchronizedEventSet)parameters.getEventSet()).getSynchronizeObject();
        ApplicationSimulation simulation=new ApplicationSimulation(parameters,this);
        DeviceServiceManager operatingSystem = simulation.getServiceManager();
        ServiceCollection serviceCollection=new ServiceCollection();
        DefaultServiceUnit serviceUnit = simulation.getServiceUnit().copy(operatingSystem.getDeviceID(),serviceCollection);
        initExternGlobalServices(serviceUnit);
        //TODO: start extern services
        new ExternGlobalOperatingSystem(serviceCollection,syncObject,operatingSystem);
        simulation.run();
        
        
    }

}
