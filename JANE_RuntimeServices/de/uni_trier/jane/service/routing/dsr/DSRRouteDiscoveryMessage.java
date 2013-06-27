/*****************************************************************************
 * 
 * RouteDiscoveryMessage.java
 * 
 * $Id: DSRRouteDiscoveryMessage.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * An object for wrapping a DSR route discovery message 
 * @author Alexander Höhfeld
 */
public class DSRRouteDiscoveryMessage implements RoutingData 
{
    private static final Shape SHAPE =  new TextShape("RD",new Rectangle(0,0,5,5), Color.GREEN);
    private Address destination;

    /**
     * Constructor for class <code>AdvancedRouteDiscoveryMessage</code>
     * @param destination the destination
     */
    public DSRRouteDiscoveryMessage(Address destination) 
    {
        this.destination = destination;
    }

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
        return destination.getCodingSize();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingData#handle(de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.signaling.SignalListener)
     */
    public void handle(RoutingHeader routingHeader, SignalListener signalListener) 
    {
        if (routingHeader.getReceiver().equals(destination))
        {
            LocationBasedRoutingHeader locationBasedRoutingHeader = (LocationBasedRoutingHeader)routingHeader;
            
            ((DSRService)signalListener).handleRouteDiscovery(routingHeader.getRoute(), locationBasedRoutingHeader.getTargetLocation());
        }
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
