/*
 * Created on 14.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.neighbor_discovery.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DominatingSetData implements Data {

	public static final DataID DATA_ID = new ClassDataID(DominatingSetData.class);

	public static DominatingSetData fromNeighborDiscoveryData(NeighborDiscoveryData data) {
		return (DominatingSetData)data.getDataMap().getData(DATA_ID);
	}
	
	private boolean member;
	
	/**
	 * @param member
	 */
	public DominatingSetData(boolean member) {
		this.member = member;
	}
	
	public DataID getDataID() {
		return DATA_ID;
	}

	public Data copy() {
		return this;
	}

	public int getSize() {
		return 1;
	}

	public boolean isMember() {
		return member;
	}
	
}
