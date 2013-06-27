/*
 * Created on 02.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.operatingSystem.manager;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;




public class ShutdownManager implements FinishListener { // TODO finish listener anmelden

	protected ServiceManager serviceManager;
	private FinishManager finishManager;
    private Set shutdownListenerSet;
    protected int missingFinishCount;
    protected boolean shuttingDown;
    protected boolean reboot;
    private DeviceID deviceID;

    /**
     * 
     * Constructor for class ShutdownManager 
     *
     */
    public ShutdownManager(DeviceID deviceID) {
        shutdownListenerSet = new HashSet();
        missingFinishCount = 0;
        shuttingDown=false;
        this.deviceID=deviceID;
    }
    
    
    /**
     * 
     * TODO: comment method 
     * @param serviceInformation
     * @param finishManager
     */
    public void init(ServiceManager serviceInformation, FinishManager finishManager){
    	this.serviceManager = serviceInformation;
        this.finishManager = finishManager;
        finishManager.addFinishListener(this);
    }
    
    public void addShutdownListener(DeviceShutdownListener listener) {
        shutdownListenerSet.add(listener);
    }
    
    public void removeShutdownListener(DeviceShutdownListener listener) {
        shutdownListenerSet.remove(listener);
    }

    public void shutdown(ServiceContext callerContext) {
        if(!shuttingDown) {
            shuttingDown=true;
            // notify all shutdown listeners
            
            notifyBegin();

            // finish all running services
            ServiceID[] serviceIDs = serviceManager.getLocalServiceIDs();
            missingFinishCount = serviceIDs.length;
            for(int i=0; i<missingFinishCount; i++) {
                ServiceID serviceID = serviceIDs[i];
                finishManager.finishService(callerContext,new ServiceContext(serviceID,deviceID));
            }
            
        }
    }

    
    /**
     * TODO Comment method
     * 
     */
    protected void notifyBegin() {
        Iterator iterator = shutdownListenerSet.iterator();
        while(iterator.hasNext()) {
            DeviceShutdownListener listener = (DeviceShutdownListener)iterator.next();
            listener.notifyBeginShutdown();
        }
    }


    public void notifyFinished(ServiceID seviceID, ServiceContext finishContext) {
        if  (!shuttingDown) return;
        missingFinishCount--;
        if(shutDownFinished()) {
            shuttingDown=true;
            Iterator iterator = shutdownListenerSet.iterator();
            while(iterator.hasNext()) {
                DeviceShutdownListener listener = (DeviceShutdownListener)iterator.next();
                listener.notifyEndShutdown();
                if (reboot){
                    listener.notifyStartBoot();
                    reboot=false;
                }
            }
        }
    }
    private boolean shutDownFinished(){
        return missingFinishCount == 0;
    }

    boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     * TODO Comment method
     * 
     */
    public void reboot(ServiceContext callerContext) {
        reboot=true;
        shutdown(callerContext);
        
        
    }


}