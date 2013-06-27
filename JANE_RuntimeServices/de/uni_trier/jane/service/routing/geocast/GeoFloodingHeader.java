/*****************************************************************************
 * 
 * GeoFloodingHeader.java
 * 
 * $Id: GeoFloodingHeader.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.routing.geocast;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class GeoFloodingHeader extends DefaultRoutingHeader implements LocationBasedRoutingHeader {

    
    private static final Shape SHAPE = new EllipseShape(Position.NULL_POSITION, new Extent(4, 4), Color.GREEN, true);
    
    private GeographicLocation geographicLocation;
    
    
    
    
    

    
    /**
     * Constructor for class <code>GeoFloodingHeader</code>
     * @param sender
     * @param receiver
     * @param countHops
     * @param traceRoute
     */
    public GeoFloodingHeader(GeographicLocation geographicLocation, boolean countHops, boolean traceRoute) {
        super(null, null, countHops, traceRoute);
        this.geographicLocation=geographicLocation;
    }

    /**
     * Constructor for class <code>GeoFloodingHeader</code>
     * @param header
     */
    public GeoFloodingHeader(GeoFloodingHeader header) {
        super(header);
        geographicLocation=header.geographicLocation;
    }

    /**
     * Constructor for class <code>GeoFloodingHeader</code>
     *
     * @param header
     * @param location
     */
    public GeoFloodingHeader(RoutingHeader header, GeographicLocation location)  {
        super((DefaultRoutingHeader)header);
        this.geographicLocation=location;
    }

    //
    public ServiceID getRoutingAlgorithmID() {
        return GeoFloodingRoutingAlgorithm.SERVICE_ID;
    }

   
    
    public Location getTargetLocation() {  
        return geographicLocation;
    }
    
    //
    public LinkLayerInfo copy() {
        return new GeoFloodingHeader(this);
    }

    public int getCodingSize() {     
        return geographicLocation.getCodingSize();
    }

    //
    public Shape getShape() {

        return SHAPE;
    }

}
