/*****************************************************************************
* 
* PositionBasedMulticastDelegationData.java
* 
* $Id: PositionBasedMulticastDelegationData.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
/**
 * 
 */
package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.routing.DelegationData;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;

import java.util.LinkedList;


/**
 * @author matthias
 *
 */
public class PositionBasedMulticastDelegationData implements DelegationData {

	private LinkedList grids;
    private MulticastGroupID dest;

    
    /**
     * 
     * Constructor for class <code>PositionBasedMulticastDelegationData</code>
     *
     * @param routingHeader
     * @param service_id
     */
    public PositionBasedMulticastDelegationData(PositionBasedMulticastHeader routingHeader, ServiceID service_id) {
		delegationRoutingAlgorithm = service_id;
		grids=routingHeader.getGrids();
        dest=routingHeader.getDest();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.DelegationData#getCodingSize()
	 */
	public int getCodingSize() {
        int size = 0;
        size += getDest().getCodingSize();
        size += getGrids().size() * (PositionBasedMulticastRoutingAlgorithmImplementation.griddepth * 2);
        return size;
	}

    private ServiceID delegationRoutingAlgorithm;
   // private PositionBasedMulticastHeader header;
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.DelegationData#getDelegationServiceID()
	 */
	public ServiceID getDelegationServiceID() {
		// TODO Auto-generated method stub
	       return delegationRoutingAlgorithm;
	}

	public MulticastGroupID getDest() {
        return dest;
    }
    
    public LinkedList getGrids() {
        return grids;
    }

}
