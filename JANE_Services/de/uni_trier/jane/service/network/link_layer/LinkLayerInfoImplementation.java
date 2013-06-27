/*****************************************************************************
 * 
 * LinkLayerInfoImplementation.java
 * 
 * $Id: LinkLayerInfoImplementation.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

/**
 * Default implementation of <class>LinkLayerInfo</class>
 * Uses this implementation if no additional info has to be passed to
 * the receiving <code>Service</code>.
 */
// TODO: benennen in DefaultLinkLayerInfo
public class LinkLayerInfoImplementation implements LinkLayerInfo {

	protected Address sender;
	private boolean isUnicastMessage;
	protected Address receiver;
    protected double signalStrength;

	/**
	 * Constructor for class <code>LinkLayerInfoImplementation</code>
	 * @param sender			The <code>Address</code> of the message sender
     * @param receiver          The <code>Address</code> of the message receiver
	 * @param isUnicastMessage	true, if message is a unicast message, false if it is a broadcast message
     * @param signalStrength    The estimated signal strength of the message sender (used for wireless media)
	 */
	public LinkLayerInfoImplementation(Address sender,Address receiver, boolean isUnicastMessage, double signalStrength) {
		this.sender = sender;
		this.receiver=receiver;
		this.isUnicastMessage = isUnicastMessage;
        this.signalStrength=signalStrength;
	}
	
    public double getSignalStrength() {
     
        return signalStrength;
    }
    
	
	public Address getSender() {
		return sender;
	}

	public Address getReceiver() {
	    return receiver;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo#isUnicastMessage()
	 */
	public boolean isUnicastMessage() {
		return isUnicastMessage;
	}
    
    public boolean isReliable() {
        return isUnicastMessage();
    }

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo#isBroadcastMessage()
	 */
	public boolean isBroadcastMessage() {
		return !isUnicastMessage;
	}

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo#copy()
	 */
	public LinkLayerInfo copy() {
		return this;
	}
 
}
