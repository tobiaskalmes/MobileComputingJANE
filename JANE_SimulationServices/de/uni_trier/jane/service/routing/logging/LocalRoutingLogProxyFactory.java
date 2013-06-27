package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;

/**
 * This factory automatically creates one local routing log proxy on a device.
 */
public class LocalRoutingLogProxyFactory implements ServiceFactory {

    private ServiceID globalRoutingLogID;
    
    public LocalRoutingLogProxyFactory(ServiceID globalRoutingLogID) {
        this.globalRoutingLogID = globalRoutingLogID;
    }
    
    public void initServices(ServiceUnit serviceUnit) {
        if(!serviceUnit.hasService(LocalRoutingLogProxy.class)) {
            serviceUnit.addService(new LocalRoutingLogProxy(globalRoutingLogID));
        }
    }

}
