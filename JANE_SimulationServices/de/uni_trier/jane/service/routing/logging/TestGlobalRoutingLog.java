package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.logging.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This global routing log simply writes all routing actions to the console.
 */
public class TestGlobalRoutingLog implements GlobalService, GlobalRoutingLogService {

    private static final ServiceID SERVICE_ID = new EndpointClassID(TestGlobalRoutingLog.class);
    
    private GlobalOperatingSystem globalOperatingSystem;
    
    private Address start;

    public static void createInstance(ServiceUnit serviceUnit) {
        TestGlobalRoutingLog routingLog = new TestGlobalRoutingLog();
        serviceUnit.addService(routingLog);
        LocalRoutingLogProxyFactory proxyFactory = new LocalRoutingLogProxyFactory(routingLog.getServiceID());
        serviceUnit.addServiceFactory(proxyFactory);
    }

    public void start(GlobalOperatingSystem globalOperatingSystem) {
        this.globalOperatingSystem = globalOperatingSystem;
    }

    public ServiceID getServiceID() {
        return SERVICE_ID;
    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        if(start != null) {
            DeviceID deviceID = globalOperatingSystem.getGlobalKnowledge().getDeviceID(start);
            return new EllipseShape(deviceID, new Extent(20, 20), Color.BLUE, true);
        }
        return null;
    }

    public void getParameters(Parameters parameters) {
        // ignore
    }

    public void logDropMessage(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " DROP");
    }

    public void logIgnoreMessage(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " IGNORE");
    }

    public void logDeliverMessage(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " DELIVER");
    }

    public void logForwardUnicast(Address address, MessageID messageID, RoutingHeader header,
    		Address receiver) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " UNICAST " + receiver);
    }

    public void logForwardBroadcast(Address address, MessageID messageID, RoutingHeader header) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " BROADCAST");
    }

    public void logForwardError(Address address, MessageID messageID, RoutingHeader header, Address receiver) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " ERROR " + receiver);
    }

    public void logMessageReceived(Address address, MessageID messageID, RoutingHeader header, Address sender) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " RECEIVE " + sender);
    }

    public void logStart(Address address, MessageID messageID) {
        start = address;
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " START");
    }

	public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " DELEGATE");
	}

	public void logLoopMessage(Address address, MessageID messageID, int loopLength) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " LOOP " + loopLength);
	}

}
