/*****************************************************************************
 * 
 * MulticastObserver.java
 * 
 * $Id: MulticastObserver.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer; 

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MulticastObserver extends SignalListener {
    /**
     * Is called when the Multicast message has been completely put on the medium
     * @param receivers		the initial receiver list
     * @param message		the message 
     */
    void notifyMulticastProcessed(Address[] receivers, LinkLayerMessage message);

    /**
     * Is called when the message is delivered to all reachable devices in the multicast list 
     * @param receivers		these devices received the multicast
     * @param nonReceivers	these devices deid not received the multicast or their status is unknown
     * @param message		the message
     */
    void notifyMulticastReceived(Address[] receivers, Address[] nonReceivers, LinkLayerMessage message); 

}
