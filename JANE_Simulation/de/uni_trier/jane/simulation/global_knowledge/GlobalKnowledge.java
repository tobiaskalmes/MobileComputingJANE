/*****************************************************************************
 * 
 * GlobalKnowledge.java
 * 
 * $Id: GlobalKnowledge.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.global_knowledge;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.Service;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface GlobalKnowledge {
    /**
     * Get the addresses of all devices in this network.
     * @return an iterator over the addresses of all devices in this network
     */
    public DeviceIDSet getNodes();

    /**
     * Test if there is a connection from sender to receiver.
     * @param sender the sender address
     * @param receiver the receiver address
     * @return true <=> there is a connection
     */
    public boolean isConnected(DeviceID sender,
            					DeviceID receiver);

    /**
     * Get the connected devices of a sender.
     * @param sender the address of the sender
     * @return an iterator over the addresses of all connected devices
     */
    public DeviceIDIterator getConnected(DeviceID deviceID);

    /**
     * Get the connected devices of a sender.
     * @param sender the address of the sender
     * @return the setof all connected devices
     */
    public DeviceIDSet getConnectedSet(DeviceID sender);

    /**
     * Returns the current trajectory of the device.
     * @param address the address of the device
     * @return the trajectory
     */
    public Trajectory getTrajectory(DeviceID deviceID);

    public DeviceID getDeviceID(Address address);

    
    /**
     * Returns the current sending radius of a device.
     * @param address the device address
     * @return the sending radius
     */
    public double getSendingRadius(DeviceID deviceID);
	
    
    
    public void addLinkListener(LinkListener linkListener);
    public void removeLinkListener(LinkListener linkListener);
    
	public void addDeviceListener(DeviceListener deviceListener);
	public void removeDeviceListener(DeviceListener deviceListener);
	
    public double getMinimumTransmissionRadius();
    public double getMaximumTransmissionRadius();
}