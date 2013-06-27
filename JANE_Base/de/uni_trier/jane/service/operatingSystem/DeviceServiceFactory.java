/*
 * Created on 27.01.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.service.operatingSystem;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.unit.*;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface DeviceServiceFactory {
	//public abstract DefaultServiceUnit getRemoteServiceUnit();

	/**
	 * 
	 * @return
	 */
	public abstract ServiceCollection getServiceCollection();

    /**
     * 
     * TODO Comment method
     * @param service
     * @return
     */
    public abstract ServiceID checkServiceID(Service service);
}