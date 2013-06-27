/*
 * Created on 01.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.operatingSystem.manager;


public interface DeviceShutdownListener {
    public void notifyBeginShutdown();
    public void notifyEndShutdown();
    /**
     * TODO Comment method
     * 
     */
    public void notifyStartBoot();
}