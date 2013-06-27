/*****************************************************************************
 * 
 * RouteReplyMessage.java
 * 
 * $Id: DSRRouteReplyMessage.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.dsr; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * An object wrapping a DSR route reply message (a route to a certain destination has been found)
 * @author Alexander Hoehfeld
 */
public class DSRRouteReplyMessage implements RoutingData 
{
    private static final Shape SHAPE = new TextShape("RR",new Rectangle(0,0,5,5),Color.GREEN);

    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Dispatchable#copy()
     */
    public Dispatchable copy() 
    {
        return this;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Sendable#getSize()
     */
    public int getSize() 
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingData#handle(de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.signaling.SignalListener)
     */
    public void handle(RoutingHeader routingHeader, SignalListener signalListener) 
    {
        ((DSRService)signalListener).handleRouteReply(routingHeader);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Dispatchable#getReceiverServiceClass()
     */
    public Class getReceiverServiceClass() 
    {
        return DSRService.class;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Sendable#getShape()
     */
    public Shape getShape() 
    {
        return SHAPE;
    }
}
