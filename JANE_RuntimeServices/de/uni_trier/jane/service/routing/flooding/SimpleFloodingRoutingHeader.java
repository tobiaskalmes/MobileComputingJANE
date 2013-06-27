/*****************************************************************************
 * 
 * SimpleFloodingRoutingHeader.java
 * 
 * $Id: SimpleFloodingRoutingHeader.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.flooding; 

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.DefaultRoutingHeader;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * A <code>RoutinHeader</code> for simple flooding
 * @author Daniel Goergen
 */
public class SimpleFloodingRoutingHeader extends DefaultRoutingHeader 
{

    /**
     * Constructor for class <code>SimpleFloodingRoutingHeader</code>
     * @param header the <code>DefaultRoutingHeader</code>
     */
    protected SimpleFloodingRoutingHeader(DefaultRoutingHeader header) 
    {
        super(header);
    }

    /**
     * Constructor for class <code>SimpleFloodingRoutingHeader</code>
     */
    public SimpleFloodingRoutingHeader() 
    {
        super(null, null, false, false);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.network.link_layer.LinkLayerInfo#copy()
     */
    public LinkLayerInfo copy() 
    {
        return new SimpleFloodingRoutingHeader(this);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#getCodingSize()
     */
    public int getCodingSize() 
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#getShape()
     */
    public Shape getShape() 
    {
        return null;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingHeader#getRoutingAlgorithmID()
     */
    public ServiceID getRoutingAlgorithmID() 
    {
        return SimpleFloodingRoutingAlgorithm.SERVICE_ID;
    }

}
