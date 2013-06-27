package de.uni_trier.jane.service.network.link_layer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerObserver.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeEnvironment;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.SimulationOperatingSystem;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class LinkLayerObserverGlobalStub {

    /**
     * Constructor for class <code>LinkLayerObserverGlobalStub</code>
     */
    public LinkLayerObserverGlobalStub(GlobalOperatingSystem operatingSystem) {
        super();
        runtimeEnvironment=operatingSystem;
    }
    
    private GlobalOperatingSystem runtimeEnvironment;
    
    

    

    public void notifyUnicastProcessed(DeviceID sender,Address receiver, LinkLayerMessage message) {
        runtimeEnvironment.sendSignal(sender,new UnicastProcessedSignal(receiver,message));
    }

    public void notifyUnicastReceived(DeviceID sender,Address receiver, LinkLayerMessage message) {
        runtimeEnvironment.sendSignal(sender, new UnicastReceivedSignal(receiver,message));
    }

    public void notifyUnicastLost(DeviceID sender,Address receiver, LinkLayerMessage message) {
        runtimeEnvironment.sendSignal(sender, new UnicastLostSignal(receiver,message));
    }

    public void notifyUnicastUndefined(DeviceID sender,Address receiver, LinkLayerMessage message) {
        runtimeEnvironment.sendSignal(sender, new UnicastUndefinedSignal(receiver,message));
    }

    public void notifyBroadcastProcessed(DeviceID sender,LinkLayerMessage message) {
        runtimeEnvironment.sendSignal(sender, new BroadcastProcessedSignal(message));
    }

}