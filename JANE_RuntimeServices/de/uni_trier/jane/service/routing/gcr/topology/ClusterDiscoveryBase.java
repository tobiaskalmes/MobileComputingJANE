/*****************************************************************************
 * 
 * ClusterDiscoveryServiceBase.java
 * 
 * $Id: ClusterDiscoveryBase.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

public abstract class ClusterDiscoveryBase implements ClusterDiscoveryService {

	/**
	 * The CVS version number of this class.
	 */
	public static final String VERSION = "$Id: ClusterDiscoveryBase.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $";
	
	public static final ServiceReference DEFAULT = new ServiceReference("clusterDiscovery") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
	    	if(!serviceUnit.hasService(ClusterDiscoveryService.class)) {
	    		OneHopClusterDiscoveryService.createInstance(serviceUnit);
	    	}
	    	return serviceUnit.getService(ClusterDiscoveryService.class);
		}
	};
	
}
