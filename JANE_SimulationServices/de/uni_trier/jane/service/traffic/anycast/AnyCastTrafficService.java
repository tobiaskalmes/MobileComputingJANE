package de.uni_trier.jane.service.traffic.anycast;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.traffic.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * This traffic source generates a sequence of packets sent between one sender and a GeographicLocation
 * 
 * @author Stefan Peters
 *
 */

public class AnyCastTrafficService implements GlobalService {

	
	private static final Position TEXT_OFFSET = new Position(0, 8);
	
	private DeviceID source;
	private GeographicLocation destination;
	private double startDelta;
	private double pauseDelta;
	private int payload;
	private int messageCount;
	
	//Initialized on startup
	private GlobalOperatingSystem globalOperatingSystem;
	private TrafficProxyServiceStub localTrafficServiceStub;

	private int sentMessages;
	
	/**
	 * Creating an instance of this global service
	 * @param serviceUnit The service unit
	 * @param source The source of packet transmitting
	 * @param location The geographic location as destination 
	 * @param messageCount The number of messages
	 * @param pauseDelta The time to wait between two packets
	 * @param startDelta The time to wait after startup
	 * @param payload The size of the payload
	 */
	public static void createInstance(ServiceUnit serviceUnit,long source,GeographicLocation location, int messageCount,double pauseDelta, double startDelta, int payload) {
		serviceUnit.addService(new AnyCastTrafficService(location,messageCount,pauseDelta,payload,source,startDelta));
		
		
	}
	
	
	/**
	 * The Constructor for an anycast traffic service between one sender and a geographic location
	 * @param serviceUnit The service unit
	 * @param source The source of packet transmitting
	 * @param location The geographic location as destination 
	 * @param messageCount The number of messages
	 * @param pauseDelta The time to wait between two packets
	 * @param startDelta The time to wait after startup
	 * @param payload The size of the payload
	 */
	
	public AnyCastTrafficService(GeographicLocation destination, int messageCount, double pauseDelta, int payload, long source, double startDelta) {
		this.destination = destination;
		this.messageCount = messageCount;
		this.pauseDelta = pauseDelta;
		this.payload = payload;
		this.source = new SimulationDeviceID(source);
		this.startDelta = startDelta;
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
		this.globalOperatingSystem = globalOperatingSystem;
		localTrafficServiceStub = new TrafficProxyServiceStub(
				globalOperatingSystem);
		globalOperatingSystem.setTimeout(new ServiceTimeout(startDelta) {
			public void handle() {
				handleSendTimeout();
			}
		});
	}

	// send the next message and possibly set the next send timeout
	private void handleSendTimeout() {
		localTrafficServiceStub.startAnyCast(source,destination,payload);
		sentMessages++;
		if (sentMessages < messageCount) {
			globalOperatingSystem.setTimeout(new ServiceTimeout(pauseDelta) {
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
		//ignore
		
	}

	public Shape getShape() {
		ShapeCollection shapeCollection = new ShapeCollection();
		Extent extent = new Extent(20.0, 20.0);
		boolean filled = false;
		Shape shape = new EllipseShape(source, extent, Color.GREEN, filled);
		shapeCollection.addShape(shape);
		shapeCollection.addShape(destination.getShape());
//		shape = new EllipseShape(destination.getCenterPosition(), extent, Color.RED, filled);
//		shapeCollection.addShape(shape);
		//shape = new ArrowShape(source, destination.getCenterPosition(), Color.LIGHTBLUE, 6.0);
		//shapeCollection.addShape(shape);
		shape = new TextShape(sentMessages + "/" + messageCount, source, Color.BLACK, TEXT_OFFSET);
		shapeCollection.addShape(shape);
		return shapeCollection;
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("source", source);
		parameters.addParameter("destination", destination);
		parameters.addParameter("startDelta", startDelta);
		parameters.addParameter("pauseDelta", pauseDelta);
		parameters.addParameter("payload", payload);
		parameters.addParameter("messageCount", messageCount);
		
	}

}
