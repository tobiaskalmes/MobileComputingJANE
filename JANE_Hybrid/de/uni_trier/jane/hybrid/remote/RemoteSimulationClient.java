/*****************************************************************************
 * 
 * RemoteSimulationClient.java}
 * 
 * $Id: RemoteSimulationClient.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote;

import de.uni_trier.jane.hybrid.remote.HybridParameters;
import de.uni_trier.jane.hybrid.remote.manager.HybridModeException;
import de.uni_trier.jane.service.unit.ServiceFactory;

/**
 * The remote client that can be connected to a running simulation.
 * The client is assigned to a singel simulated device, all allready started devices can be accessed is if they were local.
 * They are also mapped to the ServiceUnit provided by init services.
 * By calling run(), the client is started with the parameters given in initHybrid
 * @author daniel
 **/

public abstract class RemoteSimulationClient implements ServiceFactory{
    
    /**
     * Inits the hybrid mode parameters
     * Default parameters can be changed by changing the given object
     * @param parameters
     */
    public abstract void initHybrid(HybridParameters parameters);
    
    /**
     * Starts the HybridClient, tries to connect it to the simulation and boots the given services
     * 
     * @throws HybridModeException
     */
    public void run() throws HybridModeException{
        new HybridClient(this).run();
        
    }

}
