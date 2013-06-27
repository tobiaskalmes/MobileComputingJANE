/****************************************************************************
 * 
 * BeaconingService_sync.java
 * 
 * $Id: BeaconingService_sync.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;

/**
 * A beaconing service periodically sends a broadcast message to all devices
 * which are reachable in one hop. Besides the device address, the periodical
 * beacon message contains all data which has been provided by other services
 * in advance. After the beacon message has been transmitted, all listeners for
 * this event have to be notified. In addition, this service also listens for all incoming
 * beacon messages and notifies all registered beacon listeners if a beacon message
 * was received. Finally, when the beacon message of a device was not received for
 * a certain timeout, all listeners are notfied that information about this device
 * is no more valid. Note, that own beacon messages which might be recieved
 * due to the broadcast implementation should be ignored by this service.
 * @see de.uni_trier.jane.service.beaconing.BeaconingListener
 */
public interface BeaconingService_sync extends BeaconingService {

    /**
     * Returns the address  of the underlying network for this device
     * @return	the network address used by the beaconing service
     */
	public Address getOwnAddress();
	
	
	/**
	 * Returns the network addresses of all known neighbors
	 * @return	the list of all neighbors
	 */
	public Address[] getNeighbors();
	
	
    /**
     * Returns true, if the dataID is already registered
     *  
     * @param dataID
     * @return
     */
    public boolean hasBeaconData(DataID dataID);
}