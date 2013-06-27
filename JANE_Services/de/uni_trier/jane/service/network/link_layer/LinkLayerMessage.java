/*****************************************************************************
 * 
 * LinkLayerMessage.java
 * 
 * $Id: LinkLayerMessage.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * Interface for all messages sended using a <code>LinkLayer</code> service
 * When the message was received by a device, the handle is called and the 
 * <code>SignalListener</code> given by the getReceiverServiceClass() is passed.
 * Additional information given by the concrete LinkLayer implemenation is 
 * passed with the <code>LinkLayerInfo</code>. 
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayerInfo
 * @see de.uni_trier.jane.signaling.SignalListener
 */
public interface LinkLayerMessage extends Dispatchable, Sendable {

    /**
     * Is called, when the message is received by a device
     * @param info		additional, LinkLayer implementation dependent information
     * @param listener	the listener responsible for this message
     */
	public void handle(LinkLayerInfo info, SignalListener listener);
	
}
