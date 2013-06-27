package de.uni_trier.jane.service.traffic.broadcast;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.traffic.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This traffic source generates a constant rate of packets sent between sender receiver pairs.
 */
public class CBRBroadcastTrafficService implements GlobalService {

	/**
     * @author goergen
     *
     * TODO comment class
     */
    private class Payload implements LinkLayerMessage {

        private int payload;

        /**
         * Constructor for class <code>Payload</code>
         * @param payload
         */
        public Payload(int payload) {
            this.payload=payload;
        }

        public void handle(LinkLayerInfo info, SignalListener listener) {
            // TODO Auto-generated method stub

        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            // TODO Auto-generated method stub
            return getClass();
        }

        public int getSize() {
            return payload;
        }

        public Shape getShape() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    // initialized in constructor
	private int numberOfSenders;
	private double startDelta;
	private double pauseDelta;
	private double jitter;
	private int payload;
	private Set sourceDeviceSet;
	private Set destinationDeviceSet;
	private Shape shape;

	// initialized on startup
	private GlobalOperatingSystem operatingSystem;
	private TrafficProxyServiceStub proxyServiceStub;
	private ContinuousDistribution indexDistribution;
	private ContinuousDistribution jitterDistribution;

	public static void createInstance(ServiceUnit serviceUnit) {
		createInstance(serviceUnit, 1, 1.0, 1.0, 0.0, 1024);
	}

	public static void createInstance(ServiceUnit serviceUnit, int numberOfSenders, double startDelta, double pauseDelta, double jitter, int payload) {
		Service service = new CBRBroadcastTrafficService(numberOfSenders, startDelta, pauseDelta, jitter, payload);
		serviceUnit.addService(service);
	}

	private CBRBroadcastTrafficService(int numberOfSenders, double startDelta, double pauseDelta, double jitter, int payload) {
		this.numberOfSenders = numberOfSenders;
		this.startDelta = startDelta;
		this.pauseDelta = pauseDelta;
		this.jitter = jitter;
		this.payload = payload;
		sourceDeviceSet = new HashSet();
		destinationDeviceSet = new HashSet();
		shape = null;
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
		this.operatingSystem = globalOperatingSystem;
		proxyServiceStub = new TrafficProxyServiceStub(operatingSystem);
		DistributionCreator distributionCreator = globalOperatingSystem.getDistributionCreator();
		indexDistribution = distributionCreator.getContinuousUniformDistribution(0.0, 1.0);
		jitterDistribution = distributionCreator.getContinuousUniformDistribution(-jitter, jitter);
		for(int i=0; i<numberOfSenders; i++) {
			double delta = startDelta + calculatePauseDelta() - pauseDelta;
			operatingSystem.setTimeout(new ServiceTimeout(delta) {
				public void handle() {
					handleSendTimeout();
				}
			});
		}
	}
	
	public ServiceID getServiceID() {
		return null;
	}

	public void finish() {
	    
	}

	public Shape getShape() {
		if(shape == null) {
			ShapeCollection shapeCollection = new ShapeCollection();
			Extent extent = new Extent(20.0, 20.0);
			boolean filled = false;
			Iterator iterator = sourceDeviceSet.iterator();
			while(iterator.hasNext()) {
				DeviceID deviceID = (DeviceID)iterator.next();
				Shape s = new EllipseShape(deviceID, extent, Color.GREEN, filled);
				shapeCollection.addShape(s);
			}
			iterator = destinationDeviceSet.iterator();
			while(iterator.hasNext()) {
				DeviceID deviceID = (DeviceID)iterator.next();
				Shape s = new EllipseShape(deviceID, extent, Color.RED, filled);
				shapeCollection.addShape(s);
			}
			shape = shapeCollection;
		}
		return shape;
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("number of senders", numberOfSenders);
		parameters.addParameter("start delta", startDelta);
		parameters.addParameter("pause delta", pauseDelta);
		parameters.addParameter("jitter", jitter);
		parameters.addParameter("payload", payload);
	}



	protected void handleSendTimeout() {
        DeviceID source = selectDevice();
        if (source==null) return;
        ServiceID serviceID=operatingSystem.getServiceIDs(source,LinkLayer_async.class)[0];
        LinkLayer_async linkLayer_async=(LinkLayer_async)operatingSystem.getSignalListenerStub(source,serviceID,LinkLayer_async.class);
        linkLayer_async.sendBroadcast(new Payload(payload));
		
		double delta = calculatePauseDelta();
		operatingSystem.setTimeout(new ServiceTimeout(delta) {
				
			public void handle() {
				handleSendTimeout();
			}});
	}

	private DeviceID selectDevice() {
		DeviceID[] deviceIDs = operatingSystem.getGlobalKnowledge().getNodes().toArray();
		int n = deviceIDs.length;
        if (n==0) return null;
		DeviceID deviceID = null;

		int i = (int)(n * indexDistribution.getNext());
        
		deviceID = deviceIDs[i];
		
		return deviceID;
	}

	private double calculatePauseDelta() {
		return pauseDelta * (1.0 + jitterDistribution.getNext());
	}

}
