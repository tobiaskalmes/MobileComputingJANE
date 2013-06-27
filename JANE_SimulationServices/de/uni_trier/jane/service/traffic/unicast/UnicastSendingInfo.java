/*****************************************************************************
 * 
 * SendingInfo.java
 * 
 * $Id: UnicastSendingInfo.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.traffic.unicast;

import de.uni_trier.jane.basetypes.*;

/**
 * This class provides all information for generating unicast traffic.
 */
public class UnicastSendingInfo {

	private final static String VERSION = "$Id: UnicastSendingInfo.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $";

	private double startTime;
	private DeviceID source;
	private DeviceID destination;
	private int payload;

	/**
	 * Constructs a new <code>SendingInfo</code> object.
	 * @param time the time to send
	 * @param receiver the receiver of the packet
	 * @param packetSize the packet size
	 */
	public UnicastSendingInfo(double startTime, DeviceID source, DeviceID destiantion, int payload) {
		this.startTime = startTime;
		this.source = source;
		this.destination = destiantion;
		this.payload = payload;
	}

	/**
	 * Get the packet size
	 * @return the packet size
	 */
	public int getPayload() {
		return payload;
	}

	/**
	 * Get the packet sender
	 * @return the sender
	 */
	public DeviceID getSource() {
		return source;
	}

	/**
	 * Get the packet receiver
	 * @return the receiver
	 */
	public DeviceID getDestiantion() {
		return destination;
	}

	/**
	 * Get the sending time
	 * @return the sending time
	 */
	public double getStartTime() {
		return startTime;
	}

}