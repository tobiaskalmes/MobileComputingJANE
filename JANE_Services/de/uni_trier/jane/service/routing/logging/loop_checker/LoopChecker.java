/*****************************************************************************
 * 
 * LoopChecker.java
 * 
 * $Id: LoopChecker.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.logging.loop_checker;

import de.uni_trier.jane.basetypes.*;

/**
 * A loop checker can be used by the routing service in order to determine if
 * the routing algorithm produced a routing loop. In this case routing of that
 * message is stopped and all routing logs are notified about the loop.
 * 
 * @author Hannes Frey
 */
public interface LoopChecker {

	/**
	 * This method initializes the loop checker. The method is called by the
	 * routing service when routing is started for the first time or when the
	 * message was passed to another routing algorithm.
	 * @param node the node where routing was started or delegated.
	 */
	public void reset(Address node);

	/**
	 * This method is called once by the routing service when the message arrived
	 * at the next forwarding node.
	 * @param node the which received the routing message
	 */
	public void addNode(Address node);

	/**
	 * This method tells the routing service that the message got into a routing
	 * loop. The method is called each time after adding the current forwarding node.
	 * @return <code>true</code> if a loop occured
	 */
	public boolean checkForLoop();

	/**
	 * This method should return the length of the loop when a loop occured. Otherwise
	 * return <code>0</code>.
	 * @return the length of the loop or <code>0</code> if no loop has occured so far
	 */
	public int getLoopLength();
	
}
