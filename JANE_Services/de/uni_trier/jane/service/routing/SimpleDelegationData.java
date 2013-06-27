/*****************************************************************************
 * 
 * SimpleDelegationData.java
 * 
 * $Id: SimpleDelegationData.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.ServiceID;

public class SimpleDelegationData implements DelegationData {

    private ServiceID delegationRoutingAlgorithm;

    public SimpleDelegationData(ServiceID delegationRoutingAlgorithm) {
        this.delegationRoutingAlgorithm=delegationRoutingAlgorithm;
    }

    public int getCodingSize() {
        return delegationRoutingAlgorithm.getCodingSize();
    }

    public ServiceID getDelegationServiceID() {

        return delegationRoutingAlgorithm;
    }

}
