/*****************************************************************************
 * 
 * SimpleRoutingHeader.java
 * 
 * $Id: SimpleRoutingHeader.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing; 

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.TextShape;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class SimpleRoutingHeader extends DefaultRoutingHeader {

    private ServiceID routingServiceID;

    /**
     * Constructor for class <code>SimpleRoutingHeader</code>
     * @param sender
     * @param receiver
     * @param countHops
     * @param traceRoute
     */
    public SimpleRoutingHeader(Address sender, Address receiver, boolean countHops, boolean traceRoute, ServiceID routingServiceID) {
        super(sender, receiver, countHops, traceRoute);
        this.routingServiceID=routingServiceID;
    }

    /**
     * Constructor for class <code>SimpleRoutingHeader</code>
     * @param header
     */
    protected SimpleRoutingHeader(SimpleRoutingHeader header) {
        super(header);
        routingServiceID=header.routingServiceID;
    }

    /**
     * 
     * Constructor for class <code>SimpleRoutingHeader</code>
     * @param receiver
     * @param otherRoutingHeader
     * @param routingServiceID
     */
    public SimpleRoutingHeader(Address receiver, DefaultRoutingHeader otherRoutingHeader, ServiceID routingServiceID) {
        super(otherRoutingHeader);
        setDestinationAddress(receiver);
        this.routingServiceID=routingServiceID;
    }

    public LinkLayerInfo copy() {
        return new SimpleRoutingHeader(this);
    }

    public int getCodingSize() {
        return routingServiceID.getCodingSize();
    }

    public Shape getShape() {
        return new TextShape("APP",new Rectangle(0,0,5,5),Color.BLUE);
    }

    public ServiceID getRoutingAlgorithmID() {
        return routingServiceID;
    }

}
