/*****************************************************************************
 * 
 * ReceiveListener.java
 * 
 * $Id: ReceiveListener.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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





/**
 * The listener for all <code>ReceiveEvents</code> has to implement this interface.
 */
public interface ReceiveListener {

	/**
	 * Notify that the message of the sending device arrived.
	 * @param sender the address of the sending device
	 */
	public void notifyFinished(DeviceID sender);

}
