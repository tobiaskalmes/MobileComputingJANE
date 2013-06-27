/*****************************************************************************
 * 
 * HybridParameters.java}
 * 
 * $Id: HybridParameters.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
import de.uni_trier.jane.console.Console;
import de.uni_trier.jane.random.DistributionCreator;

/**
 * The Parameter collection for initializing the remote JANE Client
 * @author daniel
 **/

public interface HybridParameters {
    
    /**
     * Returns the distribution creator
     * @return	the distribution creator
     */
    public DistributionCreator getDistributionCreator();

    /**
     * Sets the distribution creator
     * @param distributionCreator The distributionCreator to set.
     */
    public void setDistributionCreator(DistributionCreator distributionCreator);

    /**
     * Returns the currently set hostname of the JANEHybridServer 
     * @return	the hostname
     */
    public InetAddress getJANEHybridServerHost();
    
    /**
     * Sets the hostname of the JANEHybridServer
     * default is localhost
     * @param hostName	the name of the JANEHybridServer host 
     * @throws UnknownHostException 
     */
    public void setJANEHybridServerHost(String hostName) throws UnknownHostException;
    
    /**
     * Sets the deviceID of the simulated device to be connected to
     * default is any free device
     * @param deviceID	
     */
    public void setDeviceID(DeviceID deviceID);
    
    /**
     * Sets the deviceID of the simulated device to be connected to
     * default is any free device
     * @param deviceID
     * @param force also accept other devices when the given device is used
     */
    public void setDeviceID(DeviceID deviceID, boolean force);
    
    
    /**
     * The current default console for system out on the hybrid device  
     * @return	the default console
     */
    public Console getDefaultConsole();
        
    
    /**
     * Set the dafault console for system out on the hybrid device
     * @param defaultConsole	the new default console
     */
    public void setDefaultConsole(Console defaultConsole);

	public void setJANEHybridServerHost(InetAddress serverAddress);


}
