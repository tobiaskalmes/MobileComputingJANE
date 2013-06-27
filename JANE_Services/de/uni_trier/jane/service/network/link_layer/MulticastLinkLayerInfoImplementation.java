/*****************************************************************************
 * 
 * LinkLayerInfoImplementation.java
 * 
 * $Id: MulticastLinkLayerInfoImplementation.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
public class MulticastLinkLayerInfoImplementation extends  LinkLayerInfoImplementation {

	
	private Address[] receivers;
	

	/**
	 * Constructor for class <code>LinkLayerInfoImplementation</code>
	 * @param sender			The <code>LinkLayerAddress</code> of the message sender
	 * @param receiver			The <code>LinkLayerAddress</code> of the message receiver
	 * @param receivers			The other receivers of this message
	 */
	public MulticastLinkLayerInfoImplementation(Address sender, Address receiver, Address[] receivers, double signalStrength) {
	    super(sender,receiver,false,signalStrength);
		this.receivers=receivers;
	}
	
	/**
	 * Constructor for class <code>MulticastLinkLayerInfoImplementation</code>
	 * @param sender		the sender of the message
	 * @param receiver			The <code>LinkLayerAddress</code> of the message receiver
	 * @param isUnicast		true, if message was a unicast, false if the message was a broadcast
	 */
	public MulticastLinkLayerInfoImplementation(Address sender, Address receiver, boolean isUnicast, double signalStrength) {
	    super(sender,receiver,isUnicast,signalStrength);
		
	}
	
	

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo#isBroadcastMessage()
	 */
	public boolean isBroadcastMessage() {
		return !isUnicastMessage()&&receivers==null;
	}
	
	/**
	 * @return the all known receivers of the message 
	 */
	public Address[] getReceivers(){
	    if (receivers!=null){
	        return receivers;
	    }
	     throw new IllegalAccessError("Message was not a multicast message");
	    
	        
	}
	
	/**
	 * Returns true if the message was a multicast message
	 * @return	true if the message was a multicast message
	 */
	public boolean isMulticastMessage(){
	    return receivers!=null;
	}

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo#copy()
	 */
	public LinkLayerInfo copy() {
		return this;
	}
	
	
}
