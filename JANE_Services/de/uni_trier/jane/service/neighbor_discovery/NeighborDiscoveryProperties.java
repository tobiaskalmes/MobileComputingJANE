/*
 * Created on 13.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NeighborDiscoveryProperties {

	private ServiceID serviceID;
	private boolean includeOwnDevice;

	/**
	 * @param includeOwnDevice
	 */
	public NeighborDiscoveryProperties(ServiceID serviceID, boolean includeOwnDevice) {
		this.serviceID = serviceID;
		this.includeOwnDevice = includeOwnDevice;
	}
	
	public ServiceID getServiceID() {
		return serviceID;
	}
	
	public boolean isNotifyAboutOwnChanges() {
		return includeOwnDevice;
	}

}
