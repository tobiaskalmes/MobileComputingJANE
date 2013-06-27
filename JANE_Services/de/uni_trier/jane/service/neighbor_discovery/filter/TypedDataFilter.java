/*
 * Created on 08.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.neighbor_discovery.filter;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TypedDataFilter implements NeighborDiscoveryFilter {

	public static TypedDataFilter OPEN_ONE_HOP_NEIGHBOR_LOCATION_FILTER =
		new TypedDataFilter(1, 1, LocationData.DATA_ID, true);

	public static TypedDataFilter CLOSED_ONE_HOP_NEIGHBOR_LOCATION_FILTER =
		new TypedDataFilter(1, 0, LocationData.DATA_ID, true);

	private int maximumHopCount;
	private int minimumHopCount;
	private DataID dataID;
	private boolean ignoreNull;

	/**
	 * @param maximumHopCount
	 * @param ignoreLessHopCount
	 * @param dataID
	 * @param ignoreNull
	 */
	public TypedDataFilter(int maximumHopCount, int minimumHopCount,
			DataID dataID, boolean ignoreNull) {
		this.maximumHopCount = maximumHopCount;
		this.minimumHopCount = minimumHopCount;
		this.dataID = dataID;
		this.ignoreNull = ignoreNull;
	}
	
	public boolean matches(NeighborDiscoveryData neighborData) {
		int hopCount = neighborData.getHopDistance();
		if(hopCount > maximumHopCount || hopCount < minimumHopCount) {
			return false;
		}
		DataMap dataMap = neighborData.getDataMap();
		if(!dataMap.hasData(dataID)) {
			return false;
		}
		Data data = dataMap.getData(dataID);
		if(ignoreNull && data == null) {
			return false;
		}
		return true;
	}

}
