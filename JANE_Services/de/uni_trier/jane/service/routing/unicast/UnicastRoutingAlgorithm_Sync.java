/*****************************************************************************
 * 
 * UnicastRoutingAlgorithm_Sync.java
 * 
 * $Id: UnicastRoutingAlgorithm_Sync.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.unicast;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.RoutingHeader;

/**
 * TODO: comment class  
 * @author daniel
 **/

public interface UnicastRoutingAlgorithm_Sync {
    public RoutingHeader getUnicastHeader(Address destination);
    

}
