package de.uni_trier.jane.service.routing.logging;

import java.util.*;

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
/**
 * 
 * TODO: comment class  
 * @author daniel
 *@deprecated use  de.uni_trier.jane.service.routing.logging.RoutingPathPainterGlobal
 */
public class RoutingPathPainter implements GlobalService, GlobalRoutingLogService {

	public static final ServiceID SERVICE_ID = new EndpointClassID(RoutingPathPainter.class.getName());
	
	public static ServiceElement SERVICE_ELEMENT = new ServiceElement("routingPathPainter") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			RoutingPathPainter.createInstance(serviceUnit);
		}
	};
	
    public static void createInstance(ServiceUnit serviceUnit,double displayDuration ) {
    	RoutingPathPainter routingLog = new RoutingPathPainter(displayDuration);
        serviceUnit.addService(routingLog);
        LocalRoutingLogProxyFactory proxyFactory = new LocalRoutingLogProxyFactory(routingLog.getServiceID());
        serviceUnit.addServiceFactory(proxyFactory);
    }
    public static void createInstance(ServiceUnit serviceUnit) {
        createInstance(serviceUnit,5.0);
    }

    private Color startColor = Color.BLACK;
    private Color endColor = Color.WHITE;
    private double displayDuration = 5.0;
    private Map solidLinks;
	private LinkedList fadingLinks;

	private GlobalOperatingSystem operatingSystem;
	
	public RoutingPathPainter(double displayDuration) {
        this.displayDuration=displayDuration;
		solidLinks = new HashMap();
		fadingLinks = new LinkedList();
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
		cleanup();
		double currentTime = operatingSystem.getSimulationTime();
		ShapeCollection shapeCollection = new ShapeCollection();
		Iterator iterator = fadingLinks.iterator();
		while (iterator.hasNext()) {
			FadingLink link = (FadingLink) iterator.next();
			shapeCollection.addShape(link.getShape(currentTime));
		}
		iterator = solidLinks.values().iterator();
		while (iterator.hasNext()) {
			SolidLink link = (SolidLink) iterator.next();
			shapeCollection.addShape(link.getShape(currentTime));
		}
		return shapeCollection;
	}

	public void getParameters(Parameters parameters) {
		// ignore
	}

	public void logStart(Address address, MessageID messageID) {
		// ignore
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
		solidLinks.put(sender, new SolidLink(sender, receiver));
	}

	public void logForwardBroadcast(Address sender, MessageID messageID, RoutingHeader header) {
		// ignore
	}

	public void logForwardError(Address sender, MessageID messageID, RoutingHeader header, Address receiver) {
		solidLinks.remove(sender);
	}

	public void logMessageReceived(Address receiver, MessageID messageID, RoutingHeader header, Address sender) {
		solidLinks.remove(sender);
		double timeStamp = operatingSystem.getSimulationTime();
		FadingLink link = new FadingLink(sender, receiver, timeStamp);
		fadingLinks.add(link);
		cleanup();
	}

	public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
		// ignore
	}

	private void cleanup() {
		double currentTime = operatingSystem.getSimulationTime();
		ListIterator iterator = fadingLinks.listIterator();
		while(iterator.hasNext()) {
			FadingLink link = (FadingLink)iterator.next();
			if(!link.isValid(currentTime)) {
				iterator.remove();
			}
			else {
				return;
			}
		}
	}
	
	private class SolidLink {
		protected Address sender;
		protected Address receiver;
		public SolidLink(Address sender, Address receiver) {
			this.sender = sender;
			this.receiver = receiver;
		}
		public Shape getShape(double currentTime) {
			return new LineShape(sender, receiver, startColor);
		}
		public String toString() {
			return sender + "->" + receiver;
		}
	}
	
	private class FadingLink extends SolidLink implements Comparable {
		private double timeStamp;
		public FadingLink(Address sender, Address receiver, double timeStamp) {
			super(sender, receiver);
			this.timeStamp = timeStamp;
		}
		public Shape getShape(double currentTime) {
			double alpha = (currentTime-timeStamp)/displayDuration;
			Color color = Color.mixColor(startColor, endColor, alpha);
			return new LineShape(sender, receiver, color);
		}
		public boolean isValid(double currentTime) {
			return currentTime <= timeStamp + displayDuration;
		}
		public String toString() {
			return timeStamp + ":" + super.toString();
		}
		public int compareTo(Object object) {
			FadingLink other = (FadingLink)object;
			if(timeStamp < other.timeStamp) {
				return -1;
			}
			if(timeStamp > other.timeStamp) {
				return 1;
			}
			return 0;
		}
	}

}
