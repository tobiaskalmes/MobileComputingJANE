/*****************************************************************************
 * 
 * EmptyNetworkStatistic.java
 * 
 * Id
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.global_shared_network;

import de.uni_trier.jane.basetypes.*;


/**
 * @author goergen
 *
 * This is an empty implementation of the interface NetworkStatistic. 
 * This class can be used if no Network Statistic is needed.
 * 
 */
public class EmptyNetworkStatistic implements NetworkStatistic {

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.NetworkStatistic#networkInUse(de.uni_trier.ssds.service.DeviceID)
     */
    public void networkInUse(DeviceID address) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.NetworkStatistic#networkFree(de.uni_trier.ssds.service.DeviceID)
     */
    public void networkFree(DeviceID owner) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.NetworkStatistic#unicastCollission(de.uni_trier.ssds.service.DeviceID, de.uni_trier.ssds.service.DeviceID)
     */
    public void unicastCollission(DeviceID sender, DeviceID receiver) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.NetworkStatistic#broadcastCollision(de.uni_trier.ssds.service.DeviceID, de.uni_trier.ssds.service.DeviceID)
     */
    public void broadcastCollision(DeviceID sender, DeviceID receiver) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.NetworkStatistic#unicastReceived(de.uni_trier.ssds.service.DeviceID, de.uni_trier.ssds.service.DeviceID)
     */
    public void unicastReceived(DeviceID sender, DeviceID receiver) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.NetworkStatistic#broadcastReceived(de.uni_trier.ssds.service.DeviceID, de.uni_trier.ssds.service.DeviceID)
     */
    public void broadcastReceived(DeviceID sender, DeviceID receiver) {
        // TODO Auto-generated method stub
        
    }

}
