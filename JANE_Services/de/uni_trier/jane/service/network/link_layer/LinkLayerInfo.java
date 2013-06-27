/*****************************************************************************
 * 
 * LinkLayerInfo.java
 * 
 * $Id: LinkLayerInfo.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.routing.RoutingHeader;

import java.io.*;

/**
 * This info is passed to the handler of a <code>LinkLayerMessage</code> 
 * It can be used to give the receiver additional, <code>LinkLayer</code>
 * implementation depended information. 
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayerMessage
 */
public interface LinkLayerInfo extends Serializable{
	
    /**
     * A value estimating the senders signal strength.
     * No spezific unit is predefined, but a lower value is a lower signal strength. 
     *  
     * @return signalstrength value
     */
    public double getSignalStrength();
    
    
    /**
     * Returns the senders <code>LinkLayerAddress</code>
     * @return	the senders address
     */
	public Address getSender();
    
    /**
     * Returns the receivers <code>LinkLayerAddress</code>
     * @return  the receivers address
     */
    public Address getReceiver();

	/**
	 * Returns true, if the message was received as unicast
	 * @return	true, if unicast message
	 */
	public boolean isUnicastMessage();
    
    /**
     * Returns true, if the message has been acknowldged by the receiver
     * @return true, if reliable message
     */
    public boolean isReliable();

	/**
	 * Returns true, if the message was received as broadcast
	 * @return	true, if broadcast message
	 */
	public boolean isBroadcastMessage();
	
	/**
	 * Is called when the information is copied
	 * return <code>this</code> when the info is immutable
	 * @return	a copy of LinkLayerInfo or <code>this</code>
	 */
	public LinkLayerInfo copy();



}