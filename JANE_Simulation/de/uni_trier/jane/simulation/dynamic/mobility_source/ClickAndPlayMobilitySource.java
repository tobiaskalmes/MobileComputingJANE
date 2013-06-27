/*****************************************************************************
 * 
 * ClickAndPlayMobilitySource.java
 * 
 * $Id: ClickAndPlayMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source;

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * A mobility source that can be interacted with. The position and speed, direction etc.
 * of nodes can be adapted and changed during runtime by the user.
 */
public interface ClickAndPlayMobilitySource extends MobilitySource{
    /**
     * Changes the position of a device
     * @param device		the device address of the device
     * @param newPosition	the new position of the device
     */
    public void setPosition(DeviceID device, Position newPosition);

    /**
     * Returns all addresses of (non moving) devices within a rectangle 
     * @param rectangle		the rectangle
     * @return	adresses of all devices within the given rectangle
     */
    public DeviceID[] getAddress(Rectangle rectangle);
}