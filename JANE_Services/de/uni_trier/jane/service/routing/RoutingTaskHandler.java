/*****************************************************************************
* 
* $Id: RoutingTaskHandler.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.*;

/**
 * A routing service is the most common description of a routing protocol.
 * The characteristic of the routing protocol will be determined by the routing
 * algorithms which are running on this device. These algorithms will decide
 * the next routing hop (or routing hops), and will tell the routing service
 * immediately by the appropriate signal, where to forward the message next.
 */
// TODO alle kommentare prüfen!!!
public interface RoutingTaskHandler extends SignalListener {

    
    /**
     * Drop a message.
     * @param routingHeader the new header of the routing message
     */
    public void dropMessage(RoutingHeader routingHeader);

    /**
     * Ignore a message.
     * @param routingHeader the new header of the routing message
     */
    public void ignoreMessage(RoutingHeader routingHeader);

    /**
     * Deliver a message locally
     * @param routingHeader the new header of the routing message
     * 
     */
    public void deliverMessage(RoutingHeader routingHeader);

    /**
     * Forward a message by unicast.
     * @param routingHeader the new header of the routing message
     * @param receiver the next hop receiver
     */
    public void forwardAsUnicast(RoutingHeader routingHeader, Address receiver);
    
    /**
     * Forward a message by an addressed multicast.  
     * @param routingHeader the new header of the routing message
     * @param receivers the next hop receivers
     */
    public void forwardAsAddressedMulticast(RoutingHeader routingHeader, Address[] receivers);
    
    /**
     * 
     * TODO: comment method 
     * @param routingHeader
     * @param receivers
     */
    //public void forwardAsAddressedMulticast(RoutingHeader[] routingHeader, Address[] receivers);

    /**
     * Forward a message by broadcast.
     * @param routingHeader the new header of the routing message
     */
    public void forwardAsBroadcast(RoutingHeader routingHeader);
    
    /**
     * Delegates a routing Task to another RoutingAlgorithm
     * @param newRoutingHeader the header for the other routing service
     * @param oldRoutingHeader the header for the current routing service
     */
    public void delegateMessage(RoutingHeader newRoutingHeader,RoutingHeader oldRoutingHeader);
    
    /**
     * Enables the Routing Service to send multiple replies.
     * Open tasks must be explicitely finished after the last reply 
     * or must be reset to be delegated
     * Initially, a task is automatically finished after the first handler method is called  
     * @deprecated task is closed automatically
     */
    public void createOpenTask();
    
    /**
     * Finishes an open task, so that no subsequent calls are possible
     * Open tasks must always be finished
     * @deprecated task is closed automatically
     */
    public void finishOpenTask();
    
    /**
     * Resets an open task, so that only one subsequent call is possible.
     * This is the default status of a task
     * @deprecated task is closed automatically    
     */
    public void resetOpenTask();

    
    

   
}
