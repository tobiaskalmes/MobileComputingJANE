/*****************************************************************************
 * 
 * MessageReceiveSignal.java
 * 
 * $Id: MessageReceiveSignal.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
import de.uni_trier.jane.signaling.*;

/**
 *	This Signal should be used by all <code>LinkLayer</code> implementations to deliver 
 *	<code>LinkLayerMessage</code>s to the receiving	Service(s).
 *	Additional, link layer implementation dependent information can be passed to the receiving 
 *	service using the <code>LinkLayerInfo</code>.
 *
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayerMessage
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayerInfo
 */
public class MessageReceiveSignal implements Signal {

	private LinkLayerInfo info;
	private LinkLayerMessage message;
	
	/**
	 * Constructor for class <code>MessageReceiveSignal</code>
	 * @param info		additional informtation given by the concrete LinkLayer implementation
	 * @param message	the message to be delivered
	 */
	public MessageReceiveSignal(LinkLayerInfo info, LinkLayerMessage message) {
		this.info = info;
		this.message = message;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
	 */
	public void handle(SignalListener listener) {
		message.handle(info, listener);
	}

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.Dispatchable#copy()
	 */
	public Dispatchable copy() {
		LinkLayerInfo infoCopy = info.copy();
		LinkLayerMessage messageCopy = (LinkLayerMessage)message.copy();
		if(infoCopy == info && messageCopy == message) {
			return this;
		}
		return new MessageReceiveSignal(infoCopy, messageCopy);
	}

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
	 */
	public Class getReceiverServiceClass() {
		return message.getReceiverServiceClass();
	}

}
