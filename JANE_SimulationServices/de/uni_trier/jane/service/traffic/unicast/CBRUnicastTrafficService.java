package de.uni_trier.jane.service.traffic.unicast;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.traffic.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This traffic source generates a constant rate of packets sent between sender receiver pairs.
 */
public class CBRUnicastTrafficService implements GlobalService {

	// initialized in constructor
	private int numberOfPairs;
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

	public static void createInstance(ServiceUnit serviceUnit, int numberOfPairs, double startDelta, double pauseDelta, double jitter, int payload) {
		Service service = new CBRUnicastTrafficService(numberOfPairs, startDelta, pauseDelta, jitter, payload);
		serviceUnit.addService(service);
	}

	private CBRUnicastTrafficService(int numberOfPairs, double startDelta, double pauseDelta, double jitter, int payload) {
		this.numberOfPairs = numberOfPairs;
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
		for(int i=0; i<numberOfPairs; i++) {
			double delta = startDelta + calculatePauseDelta() - pauseDelta;
			operatingSystem.setTimeout(new ServiceTimeout(delta) {
				public void handle() {
					handleStartTimeout();
				}
			});
		}
	}
	
	public ServiceID getServiceID() {
		return null;
	}

	public void finish() {
		// ignore
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
		parameters.addParameter("number of pairs", numberOfPairs);
		parameters.addParameter("start delta", startDelta);
		parameters.addParameter("pause delta", pauseDelta);
		parameters.addParameter("jitter", jitter);
		parameters.addParameter("payload", payload);
	}

	protected void handleStartTimeout() {
		shape = null;
		DeviceID source = selectFreeDevice(sourceDeviceSet, destinationDeviceSet);
		DeviceID destination = selectFreeDevice(destinationDeviceSet, sourceDeviceSet);
		handleSendTimeout(source, destination);
	}

	protected void handleSendTimeout(final DeviceID source, final DeviceID destination) {
		proxyServiceStub.startUnicast(source, destination, payload);
		double delta = calculatePauseDelta();
		operatingSystem.setTimeout(new ServiceTimeout(delta) {
			DeviceID s = source;
			DeviceID d = destination;
			public void handle() {
				handleSendTimeout(s, d);
			}});
	}

	private DeviceID selectFreeDevice(Set set1, Set set2) {
		DeviceID[] deviceIDs = operatingSystem.getGlobalKnowledge().getNodes().toArray();
		int n = deviceIDs.length;
		DeviceID deviceID = null;
		while(deviceID == null || set1.contains(deviceID) || set2.contains(deviceID)) {
			int i = (int)(n * indexDistribution.getNext());
			deviceID = deviceIDs[i];
		}
		set1.add(deviceID);
		return deviceID;
	}

	private double calculatePauseDelta() {
		return pauseDelta * (1.0 + jitterDistribution.getNext());
	}

}
