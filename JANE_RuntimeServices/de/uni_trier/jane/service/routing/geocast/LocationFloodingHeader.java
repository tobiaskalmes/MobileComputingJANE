/*****************************************************************************
 * 
 * LocationFloodingHeader.java
 * 
 * $Id: LocationFloodingHeader.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
 * A <code>RoutinHeader</code> for location based flooding
 * @author Daniel Görgen
 **/

public class LocationFloodingHeader extends DefaultRoutingHeader implements LocationBasedRoutingHeader 
{
    private static final Shape SHAPE = new EllipseShape(Position.NULL_POSITION, new Extent(4, 4), Color.GREEN, true);
    private Location location;
    private ServiceID delegationService;
    
    /**
     * Constructor for class <code>LocationFloodingHeader</code>
     * @param location the <code>Location</code>
     * @param countHops <code>true</code> if hops are to be counted
     * @param traceRoute <code>true</code> if a traceroute should be performed
     */
    public LocationFloodingHeader(Location location, boolean countHops, boolean traceRoute) 
    {
        this(location,null,countHops,traceRoute);
    }

    /**
     * Constructor for class <code>LocationFloodingHeader</code>
     * @param header the <code>LocationFloodingHeader</code>
     */
    protected LocationFloodingHeader(LocationFloodingHeader header) 
    {
        super(header);
        location = header.getTargetLocation();
    }
    
    /**
     * Constructor for class <code>LocationFloodingHeader</code>
     * @param location the <code>Location</code>
     * @param delegationService the <code>ServiceID</code> of the delegate service
     * @param countHops <code>true</code> if hops are to be counted
     * @param traceRoute <code>true</code> if a traceroute should be performed
     */
    public LocationFloodingHeader(Location location, ServiceID delegationService, boolean countHops, boolean traceRoute) 
    {
        super(null, null, countHops, traceRoute);
        this.location = location;
        this.delegationService = delegationService;
    }

    /**
     * 
     * Constructor for class <code>LocationFloodingHeader</code>
     *
     * @param header
     * @param location
     * @param delegationService
     */
    public LocationFloodingHeader(DefaultRoutingHeader header, GeographicLocation location, ServiceID delegationService) {
        super((DefaultRoutingHeader)header);
        this.location=location;
        this.delegationService=delegationService;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.LocationBasedRoutingHeader#getTargetLocation()
     */
    public Location getTargetLocation() 
    {
        return location;
    }
    
    /**
     * Returns if a delegate service is specified
     * @return <code>true</code> if a delegate service is specified
     */
    public boolean hasDelegationService()
    {
        return delegationService != null;
    }
    
    /**
     * Returns the <code>ServiceID</code> of the delegate service
     * @return the <code>ServiceID</code> of the delegate service
     * @throws IllegalAccessError if no delegate service is specified
     */
    public ServiceID getDelegationService() 
    {
        if (delegationService == null) throw new IllegalAccessError("This routing header does not support delegation service information");
        return delegationService;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingHeader#getRoutingAlgorithmID()
     */
    public ServiceID getRoutingAlgorithmID() 
    {
        return LocationFloodingRoutingAlgorithm.SERVICE_ID;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#getCodingSize()
     */
    public int getCodingSize() 
    {
        int size=0;
        if (delegationService!=null) size += delegationService.getCodingSize();
        return size+location.getCodingSize();
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.network.link_layer.LinkLayerInfo#copy()
     */
    public LinkLayerInfo copy() 
    {
        return new LocationFloodingHeader(this);
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#getShape()
     */
    public Shape getShape() 
    {
        return SHAPE;
    }
}
