/*****************************************************************************
 * 
 * MyFloodingMessage.java
 * 
 * $Id: MyFloodingMessage.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.service.routing.RoutingData;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * TODO: comment class
 * 
 * @author daniel
 */

public class MyFloodingMessage implements RoutingData {

	//
	public Dispatchable copy() {
		// TODO Auto-generated method stub
		return this;
	}

	//
	public int getCodingSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	//
	public void handle(RoutingHeader routingHeader, SignalListener signalListener) {
	//	((PositionBasedMulticastRoutingAlgorithm) signalListener).receiveAnExampleMessage();

	}

	//
	public Class getReceiverServiceClass() {

		return PositionBasedMulticastRoutingAlgorithm.class;
	}

	//
	public Shape getShape() {
		// TODO Auto-generated method stub

		return null;
	}

	public int getSize() {
		return 100;
	}
}
