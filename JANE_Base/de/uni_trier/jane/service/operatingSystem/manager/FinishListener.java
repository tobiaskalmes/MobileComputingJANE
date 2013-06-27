/*
 * Created on 01.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.operatingSystem.manager;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;


public interface FinishListener {
    public void notifyFinished(ServiceID serviceID, ServiceContext finishContext);
}