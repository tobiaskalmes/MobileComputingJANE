/*****************************************************************************
* 
* $Id: RoutingAlgorithm.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.service.routing;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;

/**
 * A routing algorithm implements the logic of a routing service. The routing service will
 * ask the routing algorithm for the next action when it received a message to be routed.
 * Possible actions are delivering, dropping, ignoring, and next hop forwarding.
 */
// TODO Kommentare nachziehen!
public interface RoutingAlgorithm extends SignalListener{


    /**
     * Start a routing task with a predifined routing header
     * @param handler
     * @param routingHeader
     */
    public void handleStartRoutingRequest(RoutingTaskHandler handler, RoutingHeader routingHeader);
    
    
    
     
    /**
     * Schedule the next action when a message arrived at the device. Possible actions
     * are forwarding to the next hop by unicast or broadcast, ignoring, dropping, and
     * delivering the message.
     * @param handler	the handler for the message
     * @param header the routing message header
     * @param sender the previous sender of the routing message

     */
    public void handleMessageReceivedRequest(RoutingTaskHandler handler, RoutingHeader header, Address sender);

    /**
     * Schedule the next action when message forwarding by unicast was not successful.
     * Possible actions are forwarding to an alternative next hop by unicast or broadcast,
     * ignoring, dropping, and delivering the message.
     * @param handler	the handler for this routing task
     * @param header the routing message header
     * @param receiver the previous unicast receiver of the routing message

     */
    public void handleUnicastErrorRequest(RoutingTaskHandler handler, RoutingHeader header, Address receiver);

    
    /**
     * TODO: comment method 
     * @param header
     */
    // TODO: Wo macht diese Methode Sinn??? An Hannes: primaer wohl nur zur Visualisierung
    public void handleMessageForwardProcessed(RoutingHeader header);


    /**
     * TODO: comment method 
     * @param handler
     * @param routingHeader
     */
    public void handleMessageDelegateRequest(RoutingTaskHandler handler, RoutingHeader routingHeader);



    /**
     * The device received the header using the promiscous mode of a network. 
     * The message has been addressed to another device! 
     * @param routingHeader the header for the neighboring device
     */
	public void handlePromiscousHeader(RoutingHeader routingHeader);





}
