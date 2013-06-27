/*****************************************************************************
* 
* $Id: RoutingPathPainterGlobal.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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

public class RoutingPathPainterGlobal implements GlobalService, LocalRoutingLogService {


    public static final ServiceID SERVICE_ID = new EndpointClassID(RoutingPathPainterGlobal.class.getName());
	
	public static ServiceElement SERVICE_ELEMENT = new ServiceElement("routingPathPainter") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			RoutingPathPainterGlobal.createInstance(serviceUnit);
		}
	};
	
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param displayDuration
     * @param linewidth
     * @param paintPath
     * @param paintDevice
     */
    public static void createInstance(ServiceUnit serviceUnit,double displayDuration, int linewidth, boolean paintPath, boolean paintDevice) {
        RoutingPathPainterGlobal routingLog = new RoutingPathPainterGlobal(displayDuration,linewidth,paintPath,paintDevice);
        serviceUnit.addService(routingLog);

    }
    
    public static void createInstance(ServiceUnit serviceUnit,double displayDuration, int linewidth ) {
    	RoutingPathPainterGlobal routingLog = new RoutingPathPainterGlobal(displayDuration,linewidth,true,true);
        serviceUnit.addService(routingLog);

    }
    public static void createInstance(ServiceUnit serviceUnit) {
        createInstance(serviceUnit,5.0,1);
    }

    private Color startColor = Color.BLACK;
    private Color endColor = Color.WHITE;
    private double displayDuration = 5.0;
    private int lineWidth=2;
    private Map solidLinks;
	private LinkedList fadingLinks;

	private GlobalOperatingSystem operatingSystem;

    private LinkedHashSet excludedServices;

    private boolean paintPath;

    private boolean paintReceivers;

    private LinkedList receivers;
	
    /**
     * 
     * Constructor for class <code>RoutingPathPainterGlobal</code>
     * @param displayDuration
     * @param lineWidth
     * @param paintPath
     * @param paintReceivers
     */
	public RoutingPathPainterGlobal(double displayDuration,int lineWidth, boolean paintPath, boolean paintReceivers) {
        this.displayDuration=displayDuration;
        this.paintPath=paintPath;
        this.paintReceivers=paintReceivers;
        this.lineWidth=lineWidth;
		solidLinks = new HashMap();
		fadingLinks = new LinkedList();
        receivers=new LinkedList();
        excludedServices=new LinkedHashSet();
	}
    
    public void excludeRoutingService(ServiceID serviceID){
        excludedServices.add(serviceID);
    }
    public void addLoggingAlgorithm(ServiceID routingAlgorithmToLog){
        
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
        if (paintPath){
    		Iterator iterator = fadingLinks.iterator();
            
    		while (iterator.hasNext()) {
    			FadingLink link = (FadingLink) iterator.next();
    			shapeCollection.addShape(link.getShape(currentTime));
    		}
    		iterator = solidLinks.values().iterator();
    		while (iterator.hasNext()) {
    			SolidLink link = (SolidLink) iterator.next();
                Shape shape=link.getShape(currentTime);
                if (shape==null) iterator.remove();
                else shapeCollection.addShape(shape);
    		}
        }
        if (paintReceivers){
            Iterator iterator = receivers.iterator();
            while (iterator.hasNext()) {
                FadingDevice link = (FadingDevice) iterator.next();
                shapeCollection.addShape(link.getShape(currentTime));
            }
        }
		return shapeCollection;
	}

	public void getParameters(Parameters parameters) {
		// ignore
	}
	
	public void logStart(RoutingHeader header) {
		// ignore
	}
	
	public void logDropMessage(RoutingHeader header) {
		// ignore
	}

    
	public void logLoopMessage(RoutingHeader header, int loopLength) {
		// ignore
	}

	public void logIgnoreMessage( RoutingHeader header) {
		// ignore
	}

	public void logDeliverMessage(RoutingHeader header) {
        if (!visualize(header)) return;
        DeviceID receiver=operatingSystem.getCallingDeviceID();
        receivers.add(new FadingDevice(receiver,header,operatingSystem.getSimulationTime()));
        cleanup();
	}

	public void logForwardUnicast( MessageID messageID, RoutingHeader header, Address receiver) {
        if (!visualize(header)) return;
        DeviceID sender=operatingSystem.getCallingDeviceID();
		solidLinks.put(sender, new SolidLink(sender, receiver,header,operatingSystem.getSimulationTime()));
	}

	/**
     * TODO Comment method
     * @param header
     * @return
     */
    private boolean visualize(RoutingHeader header) {
        if (header!=null){
            return !excludedServices.contains(header.getRoutingAlgorithmID());
        }
        return true ;
    }
    public void logForwardBroadcast( MessageID messageID, RoutingHeader header) {
		// ignore
	}

	public void logForwardError( MessageID messageID, RoutingHeader header, Address receiver) {
        if (!visualize(header)) return;
		solidLinks.remove(operatingSystem.getCallingDeviceID());
	}

	public void logMessageReceived( MessageID messageID, RoutingHeader header, Address sender) {
        if (!visualize(header)) return;
		solidLinks.remove(sender);
		double timeStamp = operatingSystem.getSimulationTime();
		FadingLink link = new FadingLink(sender, operatingSystem.getCallingDeviceID(),header, timeStamp);
		fadingLinks.add(link);
		cleanup();
	}
    
    public void logForwardMulticast(MessageID messageID, RoutingHeader header, Address[] receivers) {
        if (!visualize(header)) return;
        DeviceID sender=operatingSystem.getCallingDeviceID();
        for (int i=0;i<receivers.length;i++){
            solidLinks.put(sender, new SolidLink(sender, receivers[i],header,operatingSystem.getSimulationTime()));    
        }
        
    }

	public void logDelegateMessage(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
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
				break;
			}
		}
        iterator = receivers.listIterator();
        while(iterator.hasNext()) {
            FadingDevice link = (FadingDevice)iterator.next();
            if(!link.isValid(currentTime)) {
                iterator.remove();
            }
            else {
                break;
            }
        }
	}
	
	private class SolidLink {
		protected Address sender;
		protected Address receiver;
        private double timeStamp;
        protected String messageID;
		public SolidLink(Address sender, Address receiver,RoutingHeader header, double timeStamp) {
			this.sender = sender;
			this.receiver = receiver;
            if (header!=null){
                this.messageID=header.getMessageID().toString();
            }
            this.timeStamp=timeStamp+displayDuration;
		}
		public Shape getShape(double currentTime) {
            if (currentTime>timeStamp){
                
                return null;
            }
            
			return new LineShapeText(sender, receiver, startColor,lineWidth,messageID);
		}
		public String toString() {
			return sender + "->" + receiver;
		}
	}
	
	private class FadingLink extends SolidLink implements Comparable {
		private double timeStamp;
		public FadingLink(Address sender, Address receiver, RoutingHeader header, double timeStamp) {
			super(sender, receiver,header,timeStamp);
			this.timeStamp = timeStamp;
		}
		public Shape getShape(double currentTime) {
			double alpha = (currentTime-timeStamp)/displayDuration;
			Color color = Color.mixColor(startColor, endColor, alpha);
			return new LineShapeText(sender, receiver, color,lineWidth,(messageID==null)?"null":messageID);
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
    
    private class FadingDevice  implements Comparable {
        private double timeStamp;
        private String messageID;
        private Address receiver;
        public FadingDevice(Address receiver, RoutingHeader header, double timeStamp) {
            
            
            this.receiver = receiver;
            if (header!=null){
                this.messageID=header.getMessageID().toString();
            }
            this.timeStamp=timeStamp+displayDuration;
           
        }
        public Shape getShape(double currentTime) {
            double alpha = (currentTime-timeStamp)/displayDuration;
            Color color = Color.mixColor(startColor, endColor, alpha);
            return new EllipseShape(receiver,5*lineWidth,color,true);
            //return new LineShapeText(sender, receiver, color,lineWidth,messageID);
        }
        public boolean isValid(double currentTime) {
            return currentTime <= timeStamp + displayDuration;
        }
        public String toString() {
            return timeStamp + ":" + receiver+":"+ messageID;
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
