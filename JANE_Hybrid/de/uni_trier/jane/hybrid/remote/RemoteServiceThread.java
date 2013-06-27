/*
 * Created on Jun 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.hybrid.remote;

import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.operatingSystem.manager.ServiceManager;
import de.uni_trier.jane.service.operatingSystem.manager.ServiceThread;

/**
 * @author goergen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoteServiceThread extends ServiceThread {

	/**
	 * @param serviceContext
	 * @param service
	 * @param serviceClass
	 * @param visualize
	 * @param serviceManager
	 */
	public RemoteServiceThread(ServiceContext serviceContext, Service service, Class serviceClass, boolean visualize, ServiceManager serviceManager) {
		super(serviceContext, service, serviceClass, visualize, serviceManager);

	}

	
}
