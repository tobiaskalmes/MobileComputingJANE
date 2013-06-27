/*****************************************************************************
* 
* PositionBasedMulticastDelegationData.java
* 
* $Id: PositionBasedMulticastDelegationData_AddressedMulticast.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.DelegationData;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;

import java.util.*;
import java.util.Map.Entry;


/**
 * @author matthias
 *
 */
public class PositionBasedMulticastDelegationData_AddressedMulticast implements DelegationData {

	
    private MulticastGroupID dest;
    private ServiceID delegationRoutingAlgorithm;
    // private PositionBasedMulticastHeader header;
     
    private LinkedList grids;
    
    /**
     * 
     * Constructor for class <code>PositionBasedMulticastDelegationData</code>
     *
     * @param routingHeader
     * @param service_id
     */
//    public PositionBasedMulticastDelegationData_AddressedMulticast(PositionBasedMulticastHeader_AddressedMulticast routingHeader, ServiceID service_id) {
//		delegationRoutingAlgorithm = service_id;
//		gridMap=routingHeader.getGridMap();
//        dest=routingHeader.getDest();
//	}

	/**
     * 
	 */
    public PositionBasedMulticastDelegationData_AddressedMulticast(LinkedList grids, MulticastGroupID dest, ServiceID service_id) {
        this.grids=grids;
        delegationRoutingAlgorithm = service_id;
        this.dest=dest;
    }

    /* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.DelegationData#getCodingSize()
	 */
	public int getCodingSize() {
        int size = 0;
        size += getDest().getCodingSize();
        size+=grids.size()* ((PositionBasedMulticastRoutingAlgorithmImplementation.griddepth * 2) + 8);
//        Iterator<Entry<Address, LinkedList<Grid>>> iterator = gridMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Entry<Address, LinkedList<Grid>> element = iterator.next();
//            size+=element.getKey().getCodingSize();
//            size+=element.getValue().size() * ((PositionBasedMulticastRoutingAlgorithmImplementation.griddepth * 2) + 8);   
//        } 
        return size;
	}


	
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
    
 
	/**
     * @return Returns the grids.
     */
    public LinkedList getGrids() {
        return this.grids;
    }
    

}
