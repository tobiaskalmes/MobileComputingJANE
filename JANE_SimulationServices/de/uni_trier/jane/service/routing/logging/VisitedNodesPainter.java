package de.uni_trier.jane.service.routing.logging;

import java.util.*;

import sun.java2d.pipe.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

public class VisitedNodesPainter implements GlobalService, GlobalRoutingLogService {

	public static final ServiceID SERVICE_ID = new EndpointClassID(VisitedNodesPainter.class.getName());
	
	public static ServiceElement SERVICE_ELEMENT = new ServiceElement("visitedNodesPainter") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			VisitedNodesPainter.createInstance(serviceUnit);
		}
	};
	
    public static void createInstance(ServiceUnit serviceUnit) {
    	VisitedNodesPainter routingLog = new VisitedNodesPainter();
        serviceUnit.addService(routingLog);
        LocalRoutingLogProxyFactory proxyFactory = new LocalRoutingLogProxyFactory(routingLog.getServiceID());
        serviceUnit.addServiceFactory(proxyFactory);
    }

    private Color startColor = Color.LIGHTGREY;
    private Color endColor = Color.BLACK;
    private int colorSteps = 4;
    private Map visitedCount;

	private GlobalOperatingSystem operatingSystem;
	
	public VisitedNodesPainter() {
		visitedCount = new HashMap();
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
		this.operatingSystem = globalOperatingSystem;
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {
		ShapeCollection shapeCollection = new ShapeCollection();
		Iterator iterator = visitedCount.values().iterator();
		while (iterator.hasNext()) {
			VisitedNode node = (VisitedNode) iterator.next();
			shapeCollection.addShape(node.getShape());
		}
		return shapeCollection;
	}

	public void getParameters(Parameters parameters) {
		// ignore
	}

	public void logStart(Address address, MessageID messageID) {
		visitNode(address);
	}

	public void logDropMessage(Address address, MessageID messageID) {
		// ignore
	}

	public void logLoopMessage(Address address, MessageID messageID, int loopLength) {
		// ignore
	}

	public void logIgnoreMessage(Address address, MessageID messageID) {
		// ignore
	}

	public void logDeliverMessage(Address address, MessageID messageID) {
		// ignore
	}

	public void logForwardUnicast(Address sender, MessageID messageID, RoutingHeader header, Address receiver) {
		// ignore
	}

	public void logForwardBroadcast(Address sender, MessageID messageID, RoutingHeader header) {
		// ignore
	}

	public void logForwardError(Address sender, MessageID messageID, RoutingHeader header, Address receiver) {
		// ignore
	}

	public void logMessageReceived(Address receiver, MessageID messageID, RoutingHeader header, Address sender) {
		visitNode(receiver);
	}

	public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
		// ignore
	}

	// Possibly create and visit the node.
	private void visitNode(Address address) {
		VisitedNode visitedNode = (VisitedNode)visitedCount.get(address);
		if(visitedNode == null) {
			visitedNode = new VisitedNode(address);
			visitedCount.put(address, visitedNode);
		}
		visitedNode.visit();
	}

	// Used to store the shape of each visited node
	private class VisitedNode {
		private Address address;
		private int visitedCount;
		private Shape shape;
		private VisitedNode(Address address) {
			this.address = address;
			visitedCount = 0;
			shape = null;
		}
		private void visit() {
			if(colorSteps <= 1) {
				createShape(endColor);
			}
			if(visitedCount < colorSteps) {
				visitedCount++;
				double alpha = (((double)visitedCount)-1.0) / (((double)colorSteps)-1.0);
				createShape(Color.mixColor(startColor, endColor, alpha));
			}
		}
		private Shape getShape() {
			return shape;
		}
		private void createShape(Color color) {
			shape = new EllipseShape(address, 8.0, color, false);
		}
	}

}
