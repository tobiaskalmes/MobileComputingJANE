/*****************************************************************************
 * 
 * InternalReceiveListener.java
 * 
 * $Id: InternalReceiveListener.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 * 
 * Copyright (C) 2002 Johannes K. Lehnert
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/
package de.uni_trier.jane.service.network.link_layer.collision_free;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;

/**
 * Implement this interface to get notified of all internal messages that 
 * are ready to be delivered.
 */
public interface InternalReceiveListener {
	public final static String VERSION = "$Id: InternalReceiveListener.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $";
	
	/**
	 * Notify the sending device that the internal message has arrived.
	 * @param sender the sender of the message
	 * @param message the message sent
	 */
	public void notifyBroadcastFinished(DeviceID sender, LinkLayerMessage message);
    
    /**
     * Notify the sending device that the internal message has arrived.
     * @param sender the sender of the message
     * @param message the message sent
     * @param handle
     */
    public void notifyUnicastFinished(DeviceID sender, LinkLayerMessage message, UnicastCallbackHandler handle);
}
