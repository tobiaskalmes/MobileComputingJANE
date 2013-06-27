/*******************************************************************************
 * 
 * NeighborDiscoveryData.java
 * 
 * $Id: NeighborDiscoveryData.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 * 
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K.
 * Lehnert
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 ******************************************************************************/
package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.service.beaconing.DataMap;
import de.uni_trier.jane.service.beaconing.ReceivedDataMap;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;


/**
 *TODO: comment??? This interface descibes a collection of all information about a device
 * which is currently known to this service.
 */
public class NeighborDiscoveryData extends ReceivedDataMap {

	private int hopDistance;

	private boolean linksChanged;

    

    
	


	/**
     * 
     * Constructor for class <code>NeighborDiscoveryData</code>
     *
     * @param info
     * @param timeStamp
     * @param validityDelta
     * @param dataMap
     * @param hopDistance
     * @param linksChanged
	 */
	public NeighborDiscoveryData(
            LinkLayerInfo info, 
            double timeStamp, 
            double validityDelta, 
            DataMap dataMap, 
            int hopDistance, 
            boolean linksChanged) {
		super(info, timeStamp,validityDelta, dataMap);
		this.hopDistance = hopDistance;
		this.linksChanged = linksChanged;

	}
    
    

	/**
     * ?? 
     * TODO: comment method 
     * @return
	 */
    public boolean neighborSetChanged() {
        return linksChanged;
    }
    
    /**
     * Get the distance in hops this information has traveled.
     * @return the distance
     */
    public int getHopDistance() {
    	return hopDistance;
    }
    
    // TODO comment
//    public NeighborDiscoveryData copy() {
//    	return new NeighborDiscoveryData(sender, timeStamp, dataMap.copy(), hopDistance);
//    }

}
