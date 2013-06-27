/*
 * Created on 08.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.neighbor_discovery.filter;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LocationDataView {

	public LocationData getLocationData(NeighborDiscoveryData neighborDiscoveryData) {
		return (LocationData)neighborDiscoveryData.getDataMap().getData(LocationData.DATA_ID);
	}

}
