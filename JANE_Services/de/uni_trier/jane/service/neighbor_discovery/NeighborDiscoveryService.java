/*****************************************************************************
 * 
 * NeighborDiscoveryService.java
 * 
 * $Id: NeighborDiscoveryService.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.Data;
import de.uni_trier.jane.basetypes.DataID;
import de.uni_trier.jane.basetypes.ServiceID;

/**
 * @author goergen
 *
 * TODO comment class
 */
public interface NeighborDiscoveryService {
    /**
     * This method is called when a service has some data to be disseminated
     * to nearby devices.
     * @param sender the sending service of this device data
     * @param beaconData the data to be transmitted by the next beacon messages
     */
    public void setOwnData(Data data);

    /**
     * Remove the identified data entry which was previously provided.
     * @param id the identifier of the data to be removed
     */
    public void removeOwnData(DataID id);

    /**
     * 
     * TODO: comment method 
     * @param serviceID
     */
    public void addUnicastErrorProvider(ServiceID serviceID);

}