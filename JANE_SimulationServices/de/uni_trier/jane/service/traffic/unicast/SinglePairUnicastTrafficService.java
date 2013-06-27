/*****************************************************************************
 * 
 * SinglePairUnicastTrafficService.java
 * 
 * $Id: SinglePairUnicastTrafficService.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.service.traffic.unicast;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.traffic.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This traffic source generates a sequence of packets sent between one sender
 * receiver pair.
 */
public class SinglePairUnicastTrafficService implements GlobalService {

	/**
	 * The CVS version number of this class.
	 */
	public static final String VERSION = "$Id: SinglePairUnicastTrafficService.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $";

	// used for visualization
	private static final Position TEXT_OFFSET = new Position(0, 8);
	
	// parameters used for service creation
	private static final LongParameter SOURCE = new LongParameter("source", 0);
	private static final LongParameter DESTINATION = new LongParameter("destination", 1);
	private static final DoubleParameter START_DELTA = new DoubleParameter("startDelta", 10.0);
	private static final DoubleParameter PAUSE_DELTA = new DoubleParameter("pauseDelta", 1.0);
	private static final IntegerParameter PAYLOAD = new IntegerParameter("payload", 1024);
	private static final IntegerParameter MESSAGE_COUNT = new IntegerParameter("messageCount", 10);
	
	/**
	 * Use this element in order to create an instance of this service sending a sequence
	 * of messages between the given source destination pair.
	 */
	public static final ServiceElement SINGLE_PAIR = new ServiceElement("singlePair") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			long source = SOURCE.getValue(initializationContext);
			long destination = DESTINATION.getValue(initializationContext);
			double startDelta = START_DELTA.getValue(initializationContext);
			double pauseDelta = PAUSE_DELTA.getValue(initializationContext);
			int payload = PAYLOAD.getValue(initializationContext);
			int messageCount = MESSAGE_COUNT.getValue(initializationContext);
			SinglePairUnicastTrafficService.createInstance(serviceUnit, source, destination,
					startDelta, pauseDelta, messageCount, payload);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { SOURCE, DESTINATION, START_DELTA, PAUSE_DELTA, PAYLOAD, MESSAGE_COUNT };
		}
	};

	/**
	 * Use this element in order to create an instance of this service sending a single
	 * message between the given source destination pair.
	 */
	public static final ServiceElement SINGLE_MESSAGE = new ServiceElement("singleMessage") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			long source = SOURCE.getValue(initializationContext);
			long destination = DESTINATION.getValue(initializationContext);
			double startDelta = START_DELTA.getValue(initializationContext);
			double pauseDelta = 0.0;
			int payload = PAYLOAD.getValue(initializationContext);
			int messageCount = 1;
			SinglePairUnicastTrafficService.createInstance(serviceUnit, source, destination,
					startDelta, pauseDelta, messageCount, payload);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { SOURCE, DESTINATION, START_DELTA, PAYLOAD };
		}
	};

	/**
	 * Create an instance of this service into a service unit. This instance will start
	 * routing of a single message from device 0 to device 1. Routing starts after 10.0
	 * time units.
	 * @param serviceUnit the service unit this service has to be inserted
	 */
	public static void createInstance(ServiceUnit serviceUnit) {
		createInstance(serviceUnit, 0, 1, 10.0, 0.0, 1, 1024);
	}

	/**
	 * Create an instance of this service into a service unit.
	 * @param serviceUnit the service unit this service has to be inserted
	 * @param source the number of the source device
	 * @param destination the number of the destination device
	 * @param startDelta the time to wait until the first message transmission
	 * @param pauseDelta the time to wait between two sucessive transmissions
	 * @param messageCount the number of messages to be transmitted
	 * @param payload the payload sent by each message
	 */
	public static void createInstance(ServiceUnit serviceUnit, long source, long destination, double startDelta,
			double pauseDelta, int messageCount, int payload) {
		SinglePairUnicastTrafficService testGlobalTrafficService = new SinglePairUnicastTrafficService(source,
				destination, startDelta, pauseDelta, messageCount, payload);
		serviceUnit.addService(testGlobalTrafficService);
	}

	// initialized in constructor
	private DeviceID source;
	private DeviceID destination;
	private double startDelta;
	private double pauseDelta;
	private int payload;
	private int messageCount;
	private int sentMessages;
	
	// initialized on startup
	private GlobalOperatingSystem globalOperatingSystem;
	private TrafficProxyServiceStub localTrafficServiceStub;

	/**
	 * Create an instance of this class.
	 * @param source the number of the source device
	 * @param destination the number of the destination device
	 * @param startDelta the time to wait until the first message transmission
	 * @param pauseDelta the time to wait between two sucessive transmissions
	 * @param messageCount the number of messages to be transmitted
	 * @param payload the payload sent by each message
	 */
	public SinglePairUnicastTrafficService(long source, long destination, double startDelta, double pauseDelta, int messageCount, int payload) {
		this.source = new SimulationDeviceID(source);
		this.destination = new SimulationDeviceID(destination);
		this.startDelta = startDelta;
		this.pauseDelta = pauseDelta;
		this.payload = payload;
		this.messageCount = messageCount;
		sentMessages = 0;
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
		localTrafficServiceStub.startUnicast(source, destination, payload);
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
		// ignore
	}

	public Shape getShape() {
		ShapeCollection shapeCollection = new ShapeCollection();
		Extent extent = new Extent(20.0, 20.0);
		boolean filled = false;
		Shape shape = new EllipseShape(source, extent, Color.GREEN, filled);
		shapeCollection.addShape(shape);
		shape = new EllipseShape(destination, extent, Color.RED, filled);
		shapeCollection.addShape(shape);
		shape = new ArrowShape(source, destination, Color.LIGHTBLUE, 6.0);
		shapeCollection.addShape(shape);
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
