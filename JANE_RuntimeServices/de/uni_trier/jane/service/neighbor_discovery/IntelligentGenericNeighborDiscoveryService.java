/*****************************************************************************
* 
* IntelligentGenericNeighborDiscoveryService.java
* 
* $Id: IntelligentGenericNeighborDiscoveryService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;

/**
 * This service implements a generic neighbor discovery service which determines all devices which
 * can transmit a message to this device in one hop. It handles information propagation in an "intelligent way", e.g. if no information changed value
 * it commits an empty beacon with a flag saying nothing changed.
 */
public abstract class IntelligentGenericNeighborDiscoveryService extends GenericNeighborDiscoveryService{
	
	
	//needed for intelligent neighborDiscoveryService
	//how many times should new data be propagated?

    /**
     * Construct a new intelligent discovery service.
     * @param beaconingServiceID the beaconing service used to send own data to all one hop neighbors
     * @param includeOwnDevice this flag determines if own data is stored as 0-hop information as well
     */
    public IntelligentGenericNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice,boolean propagateEvents) {
    	super(ownServiceID,beaconingServiceID,includeOwnDevice,propagateEvents);
    }
    /**
     * Constructs a new intelligent discovery service
     * @param ownServiceID 
     * @param beaconingServiceID
     * @param includeOwnDevice A flag ro decide if own data is stored as 0-hop information as well
     * @param propagateCount An int value for deciding how many times neighbor information should be propagated after a change of the data set.
     */
    public IntelligentGenericNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice,int propagateCount,boolean propagateEvents) {
    	this(ownServiceID,beaconingServiceID,includeOwnDevice,propagateEvents);
    }

    protected abstract boolean containsData(DataID dataID,Data data);
    
    public void setNeighbor(BeaconingData beaconingData) {
    	
    	beaconingData.getSender();
    	
    	if(!beaconingData.getSender().equals(getOwnAddress())) {
    		Data data = beaconingData.getDataMap().getData(getDataID());
    		if(!containsData(NullData.DATA_ID,data)) {
                //TODO: der nachbar ist aber trotzdem da und neu!

    			double timestamp = beaconingData.getTimeStamp();
                //TODO: use correct(?) vality delta!

    			setNeighborData(beaconingData.getReceiveInfo(), timestamp, -1, data); // storing neighbor information. The called Method have to be implemented by all sub classes.
    		}
    	}
    }

    public void updateNeighbor(BeaconingData beaconingData) {
    	if(!beaconingData.getSender().equals(getOwnAddress())) {
    		Data data =  beaconingData.getDataMap().getData(getDataID());
    		if(!containsData(NullData.DATA_ID,data)) {
                //TODO: die Neighbordata aendert sich aber trotzdem (timestamp, validity, receiveInfo)!
    			double timestamp = beaconingData.getTimeStamp();
                //TODO: use correct(?) vality delta!
                //TODO: use last receiveinfo!
    			updateNeighborData(beaconingData.getReceiveInfo(), timestamp, -1, data); // updating neighbor information.
    		}
    	}
    }

    
    
    // Just a flag saying their are no changes
    protected static class NullData implements Data{
		private static final long serialVersionUID = -8428380038707942880L;
		public static final DataID DATA_ID = new ClassDataID(NullData.class);
		public DataID getDataID() {
			return DATA_ID;
		}	
		public int getSize() {
			return 1;
		} 	
    }

}
