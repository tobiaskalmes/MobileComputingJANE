/*****************************************************************************
 * 
 * Route.java
 * 
 * $Id: Route.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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


import de.uni_trier.jane.basetypes.*;

/**
 * A route contains a sorted list of <code>ID</code>s for all objects on a the specified 
 * path from one object to another through a linkbased network.
 * Routes should contain no circles, but this is not checked within this implementation. 
 */
public class Route extends Hashable
{
    /**
     * A list containing the actual device <code>ID</code>s
     */
    private List routeList;
    
    /**
     * Creates a new <code>Route</code>
     * @param route a list of <code>ID</code>s representing the route
     */
    public Route(List route) 
    {
        routeList = new ArrayList();
        
        Iterator iter = route.iterator();
        while (iter.hasNext()) 
        {
            ID element = (ID) iter.next();
            routeList.add(element);
        }  
    }
    
    /**
     * Returns the route in form of a <code>List</code> of <code>ID</code>s
     * @return the route as <code>List</code>
     */
    public List getRoute()
    {
        return new ArrayList(routeList);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() 
    {
        final int PRIME = 1000003;
        int result = 0;
        if (routeList != null) 
        {
            result = PRIME * result + routeList.hashCode();
        }

        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object oth) 
    {
        if (this == oth) 
        {
            return true;
        }

        if (oth == null) 
        {
            return false;
        }

        if (oth.getClass() != getClass()) 
        {
            return false;
        }

        Route other = (Route) oth;
        if (this.routeList == null) {
            if (other.routeList != null) {
                return false;
            }
        } 
        else
        {
            if (!this.routeList.equals(other.routeList)) 
            {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Checks if the <code>Route</code> contains the specified subroute
     * @param route the sub route to check
     * @return <code>true</code> if the specified sub route is contained within this <code>Route</code>
     */
    public boolean containsSubRoute(Route route)
    {
        ID startID = (ID) route.getRoute().get(0);
        
        List subRoute = route.getRoute();
        
        if (routeList.contains(startID))
        {
            int startIndex = routeList.indexOf(startID);
            if (routeList.size() - startIndex >= subRoute.size())
            {
                for (int i = 0; i < subRoute.size(); i++)
                {
                    ID id1 = (ID)routeList.get(i + startIndex);
                    ID id2 = (ID)subRoute .get(i);
                    
                    if (!id1.equals(id2))
                        return false;
                }
                
                return true;
            }
        }
            
        return false;
    }
    
    /**
     * Returns the reverse <code>Route</code> 
     * @return the reverse <code>Route</code> 
     */
    public Route getReverseRoute()
    {
       List reverseList = new ArrayList();
       
       Iterator iterator = routeList.iterator();
       
       while(iterator.hasNext())
       {
           reverseList.add(0, iterator.next());
       }
       
       return new Route(reverseList);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return(routeList.toString());
    }

	public boolean startsWith(Address neighbor) {
		
		return routeList.get(0).equals(neighbor);
	}

    /**
     * TODO: comment method 
     * @param reverseRoute
     * @return
     */
    public Route repair(List repairRoute) {
        List list=new ArrayList(routeList);
        int i=routeList.indexOf(repairRoute.get(0));
        if (i<0) 
            i=0;
         list=list.subList(0,i);
          list.addAll(i,repairRoute);
        
        
        
        return new Route(list);
    }
}
