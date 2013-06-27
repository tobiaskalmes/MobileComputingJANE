/*****************************************************************************
 * 
 * ReceiveEvent.java
 * 
 * $Id: ReceiveTimeout.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.collision_free;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;

/**
 * This event is used to schedule the receipt of a message in the event set.
 */
public class ReceiveTimeout extends ServiceTimeout {

	private final static String VERSION = "$Id: ReceiveTimeout.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $";

	private DeviceID sender;
	private ReceiveListener receiveListener;
	
	/**
	 * Construct a new <code>ReceiveEvent</code> object.
	 * @param time the time of this event
	 * @param sender the address of the sending device
	 * @param receiveListener the listener for this event
	 */
	public ReceiveTimeout(double time, DeviceID sender, ReceiveListener receiveListener) {
		super(time);
		this.sender = sender;
		this.receiveListener = receiveListener;
	}
	
	public void handle() {
		receiveListener.notifyFinished(sender);
	}
	
}
