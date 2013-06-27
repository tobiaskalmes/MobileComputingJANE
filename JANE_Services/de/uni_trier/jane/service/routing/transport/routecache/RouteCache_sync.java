/*****************************************************************************
 * 
 * RouteCaxhe_sync.java
 * 
 * $Id: RouteCache_sync.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.transport.routecache;

import java.util.*;


import de.uni_trier.jane.basetypes.ID;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * An interface specifying all necessary (synchroneous) methods for an arbitrary route cache.
 * Only routes starting at a fixed device or object are managed, no general or reverse routes. 
 */
public interface RouteCache_sync extends SignalListener 
{
    /**
     * Returns the (shortest) route to the target <code>ID</code> if available
     * @param target the target <code>ID</code>
     * @return the (shortest) route to the target or <code>null</code> if no route was found
     */
    public Route getRoute(ID target);
    
    /**
     * Returns all routes to the target <code>ID</code>
     * @param target the target <code>ID</code>
     * @return all routes to the target <code>ID</code> or an empty list if no routes were found
     */
    public Route[] getAllRoutes(ID target);
    
    /**
     * Checks of the cache contains at least one route to the designated target <code>ID</code>
     * @param target the target <code>ID</code>
     * @return <code>true</code> if the cache contains at least one route to the designated target <code>ID</code>
     */
    public boolean hasRoute(ID target);
    
    /**
     * Removes the specified <code>Route</code> from the cache and all other <code>Route</code>s
     * containing it as subroute. 
     * @param route the <code>Route</code> to be removed from the cache
     */
    public void removeRoute(Route route);
    
    /**
     * Returns all routes currently in the cache
     * @return all routes currently in the cache or an empty list if no routes were found
     */
    public Route[] getAllRoutes();
    
    /**
     * Removes the broken link from ALL routes
     * @param brokenLink the broken link
     */
    public Collection removeBrokenLink(Route brokenLink);

	public Collection removeBrokenLink(de.uni_trier.jane.basetypes.Address neighbor);
   
}
