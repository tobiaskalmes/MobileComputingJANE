/*****************************************************************************
 * 
 * PositionBasedMulticastService.java
 * 
 * $Id: PositionBasedMulticastRoutingAlgorithmImplementation.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.multicast.spbm;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationDataDisseminationService;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.positioning.PositioningData;
import de.uni_trier.jane.service.positioning.PositioningService;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.anycast.LocationRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.geocast.GeoFloodingRoutingAlgorithm;
import de.uni_trier.jane.service.routing.multicast.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.LineShape;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * TODO: comment class
 * 
 * @author daniel
 */

public class PositionBasedMulticastRoutingAlgorithmImplementation implements 
		RoutingAlgorithm, 
		MulticastRoutingAlgorithm,
		MulticastRoutingAlgorithm_Sync, 
		RuntimeService, 
		DelegationRoutingAlgorithm_Sync,
		FaceUsingRoutingAlgorithm, 
		PositionBasedMulticastRoutingAlgorithm , 
		LocationRoutingAlgorithm_Sync
{

	// Adrian, 20.10.2006, java 1.3
	//HashMap<Address, MulticastNeighborEntry> myNeighbors = new HashMap<Address, MulticastNeighborEntry>();
	HashMap myNeighbors = new HashMap();

	public static ServiceID createInstance(ServiceUnit serviceUnit, double sizex, double sizey, int griddepth,
			boolean useRecovery) {
		ServiceID recoveryServiceID = null;
		if (useRecovery) {
			recoveryServiceID = AnycastFaceRouting.createInstance(serviceUnit, new Conditions(false, false),
					AngleStartCondition.FACTORY, AngleCrossingCondition.FACTORY, ResumeGreedySoonest.FACTORY,
					SERVICE_ID);
		}
		return createInstance(serviceUnit, sizex, sizey, griddepth, recoveryServiceID);

	}

	/**
	 * create a position based multicast service with default values
	 * 
	 * @param serviceUnit
	 * @param recoveryServiceID the serviceID of the associated geoanycast face routing
	 * @param useRecovery use recovery service when greedy is not possible
	 * @return the serviceID of the created service
	 */
	public static ServiceID createInstance(ServiceUnit serviceUnit, double sizex, double sizey, int griddepth,
			ServiceID recoveryServiceID) {
		serviceUnit.setVisualizeAddedServices(false);
		ServiceID beaconingServiceID;
		if (!serviceUnit.hasService(LocationDataDisseminationService.class)) {
			LocationDataDisseminationService.createInstance(serviceUnit);
		}
		if (serviceUnit.hasService(NeighborDiscoveryService.class)) {
			beaconingServiceID = serviceUnit.getService(NeighborDiscoveryService.class);
		} else {
			beaconingServiceID = OneHopNeighborDiscoveryService.createInstance(serviceUnit);
		}
		ServiceID positioningServiceID;
		if (serviceUnit.hasService(PositioningService.class)) {
			positioningServiceID = serviceUnit.getService(PositioningService.class);
		} else {
			throw new IllegalStateException("Positioning service has to be started in advance!");
		}
		serviceUnit.setVisualizeAddedServices(true);
		ServiceID routingService;
		if (serviceUnit.hasService(RoutingService.class))
			routingService = serviceUnit.getService(RoutingService.class);
		else
			routingService = DefaultRoutingService.createInstance(serviceUnit);

		if (!serviceUnit.hasService(GeoFloodingRoutingAlgorithm.class)) {
			GeoFloodingRoutingAlgorithm.createInstance(serviceUnit);
		}
		ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
		return serviceUnit.addService(new PositionBasedMulticastRoutingAlgorithmImplementation(positioningServiceID,
				beaconingServiceID, routingService, linkLayerService, sizex, sizey, griddepth, recoveryServiceID,
				recoveryServiceID != null));
	}

	static int griddepth;

	private final Grid rootgrid;

	private LineShape shape;

	//
	public Grid getGrid() {
		return Grid.posToGrid(rootgrid, getCurrentPosition(), griddepth);
	}

	// a unique name for this service
	public static final ServiceID SERVICE_ID = new EndpointClassID(PositionBasedMulticastRoutingAlgorithm.class
			.getName());

	private ServiceID positioningServiceID;

	private PositioningService positioningService;

	private ServiceID neighborDiscoveryServiceID;

	private NeighborDiscoveryService neighborDiscoveryService;
	
	private ServiceID routingServiceID;

	private ServiceID linkLayerServiceID;

	private ServiceID recoveryServiceID;

	private BitSet myGroups = new BitSet();

	private static boolean useRecovery;

	/**
     * 
     * Constructor for class <code>PositionBasedMulticastRoutingAlgorithmImplementation</code>
     *
     * @param positioningServiceID
     * @param neighborDiscoveryServiceID
     * @param routingServiceID
     * @param linkLayerServiceID
     * @param xsize
     * @param ysize
     * @param griddepth
     * @param recoveryServiceID
     * @param useRecovery
	 */
	public PositionBasedMulticastRoutingAlgorithmImplementation(ServiceID positioningServiceID, ServiceID neighborDiscoveryServiceID,
			ServiceID routingServiceID, ServiceID linkLayerServiceID, double xsize, double ysize, int griddepth,
			ServiceID recoveryServiceID, boolean useRecovery) {
		this.positioningServiceID = positioningServiceID;
		this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
		this.routingServiceID = routingServiceID;
		this.linkLayerServiceID = linkLayerServiceID;
		this.recoveryServiceID = recoveryServiceID;
		PositionBasedMulticastRoutingAlgorithmImplementation.useRecovery = useRecovery;
		PositionBasedMulticastRoutingAlgorithmImplementation.griddepth = griddepth;
		GroupManagement.getInstance().addAlgo(this);
		rootgrid = new Grid(0, xsize, 0, ysize);
	}

	public void handleStartRoutingRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) {
		PositionBasedMulticastHeader multicastHeader = (PositionBasedMulticastHeader) routingHeader;
		LinkedList l = new LinkedList();
		l.add(rootgrid);
		multicastHeader.setGrids(l);
		route(handler, routingHeader);
	}

	public void useFaceRecovery(boolean recovery) {
		if (recoveryServiceID != null) {
			useRecovery = recovery;
		}
	}

	public void route(RoutingTaskHandler handler, RoutingHeader routingHeader) {
		handler.createOpenTask();

		PositionBasedMulticastHeader multicastHeader = (PositionBasedMulticastHeader) routingHeader;
		
		// Adrian, 20.10.2006, java 1.3, go assert yourseft!
		//assert(multicastHeader.getGrids() != null);

		if (multicastHeader.getPreviousNode() != null)
			shape = new LineShape(multicastHeader.getPreviousNode(), myAddress, Color.BLUE);

		if (isInGroup(multicastHeader.getDest()))
			handler.deliverMessage(routingHeader);
		// use the handler to start the next routing step
		// forwardAsUnicast/Broadcast, drop, ignore, deliver

		// Adrian, 20.10.2006, java 1.3
		HashMap h = new HashMap();

		// replace with known more exact grids
		GroupManagement.getInstance().replaceGrids(((PositionBasedMulticastHeader) routingHeader).getGrids(),
				getGrid(), multicastHeader.getDest());

		/* add local receivers */
		Iterator iter = ((PositionBasedMulticastHeader) routingHeader).getGrids().iterator();
		while (iter.hasNext()) {
			Grid g = (Grid) iter.next();
			if (g.equals(getGrid())) {
				Iterator iter2 = GroupManagement.getInstance().getGridReceivers(g, multicastHeader.getDest())
						.iterator();
				while (iter2.hasNext()) {
					Address a = (Address) iter2.next();
					if (!h.containsKey(a) && a != myAddress) {
						h.put(a, new LinkedList());
			             //TODO: myNeighbors.get(a).getGroups() causes NullPointerException when the devices are mobile
						//Solution: Don't use global knowledge above, but own neighor table instead.
						//System.out.println("added local receiver " + a + " (" + myNeighbors.get(a).getGroups() + ")");
					}
				}
			}
		}

		// use whole list and split by receiver
		LinkedList l = ((PositionBasedMulticastHeader) routingHeader).getGrids();
		for (int i = 0; i < l.size(); i++) {
			Grid g = (Grid) l.get(i);
			if (!g.equals(getGrid())) {
				Address a = nearestGreedyNeighbor(g);
				if (a != null) {
					//System.out.println("Nearest neighbour for grid " + g + " is " + a + " (" + myNeighbors.get(a).getGroups() + ")");
					// Adrian, 20.10.2006, java 1.3, added cast
					LinkedList lg = (LinkedList) h.get(a);
					if (lg == null) {
						lg = new LinkedList();
						h.put(a, lg);
					}
					lg.add(g);
				} else if (useRecovery) {
					AnycastFaceRoutingHeader recoveryHeader = new AnycastFaceRoutingHeader(
							(DefaultRoutingHeader) routingHeader, gridToRegion(g), recoveryServiceID);
					recoveryHeader.setDelegationData(new PositionBasedMulticastDelegationData(
							(PositionBasedMulticastHeader) routingHeader,
                            SERVICE_ID));
					handler.delegateMessage(recoveryHeader,multicastHeader);
				} else {
					System.out.println("Cannot route packet without recovery strategy!");
				}
			}
		}

		iter = h.keySet().iterator();
		while (iter.hasNext()) {
			Address a = (Address) iter.next();
			PositionBasedMulticastHeader rh = (PositionBasedMulticastHeader) ((PositionBasedMulticastHeader) routingHeader)
					.copy();
			// Adrian, 20.10.2006, java 1.3, added cast
			rh.setGrids((LinkedList) h.get(a));
			//rh.setPreviousNode(myAddress);
			rh.setDestinationAddress(a);
			if (a != null) {
				handler.forwardAsUnicast(rh, a);
			} else {
				System.out.println("No suitable known neighbour");
			}
		}
		// if more than one callback is need use openTask
		// the handler must then explicitely be finished with finishOpenTask
		//handler.finishOpenTask();
	}

	private GeographicLocation gridToRegion(Grid g) {
		return new RectangleGeographicLocation(new Rectangle(g.minx, g.miny, g.maxx, g.maxy));
	}

	public void handleMessageReceivedRequest(RoutingTaskHandler handler, RoutingHeader header, Address lastHop) {
		// resume routing on the next hop
		route(handler, header);
	}

	public void handleUnicastErrorRequest(RoutingTaskHandler handler, RoutingHeader header, Address receiver) {
		// unicast forward failed
		// neighbor is now out of reach, or sth else
		myNeighbors.remove(header.getReceiver());
		route(handler, header);
	}

	public void handleMessageForwardProcessed(RoutingHeader header) {
		// the message has been processed in the send system
	}
	
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }

	public void handleMessageDelegateRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) {
		route(handler, routingHeader);
	}

	public void joinGroup(MulticastGroupID multicastGroupID) {
		// join a group
		myGroups.set((int)multicastGroupID.getGroupID());
	}

	public void leaveGroup(MulticastGroupID multicastGroupID) {
		// leave a group
		myGroups.clear((int)multicastGroupID.getGroupID());
	}

	//
	public boolean isInGroup(MulticastGroupID mid) {
		return myGroups.get((int)mid.getGroupID());
	}

	public RoutingHeader getMulticastRoutingHeader(MulticastGroupID multicastGroupID) {
		// create an initial routing header
		return new PositionBasedMulticastHeader(multicastGroupID);
	}

	Address myAddress;

	public void start(final RuntimeOperatingSystem runtimeOperatingSystem) {
		// prepare for signal receiving
		runtimeOperatingSystem.registerSignalListener(RoutingAlgorithm.class);
		runtimeOperatingSystem.registerSignalListener(MulticastRoutingAlgorithm.class);
		runtimeOperatingSystem.registerSignalListener(FaceUsingRoutingAlgorithm.class);
		// prepare for synchron access
		runtimeOperatingSystem.registerAccessListener(MulticastRoutingAlgorithm_Sync.class);
		runtimeOperatingSystem.registerAccessListener(LocationRoutingAlgorithm_Sync.class);
		runtimeOperatingSystem.registerAccessListener(DelegationRoutingAlgorithm_Sync.class);

		LinkLayer.LinkLayerStub linkLayerFacade = new LinkLayer.LinkLayerStub(runtimeOperatingSystem,
				linkLayerServiceID);
		myAddress = linkLayerFacade.getLinkLayerProperties().getLinkLayerAddress();
		// get the positioning service (if started!)
		positioningService = (PositioningService) runtimeOperatingSystem.getAccessListenerStub(positioningServiceID,
				PositioningService.class);
		// get the beaconing service
		neighborDiscoveryService = (NeighborDiscoveryService) runtimeOperatingSystem.getSignalListenerStub(neighborDiscoveryServiceID, NeighborDiscoveryService.class);

		// get the routing service
		// routingService = (RoutingService) runtimeOperatingSystem
		// .getSignalListenerStub(routingServiceID, RoutingService.class);

		// register to receive messages sent by yourself over the routing
		// service as flooding messages...
		runtimeOperatingSystem.registerAtService(routingServiceID, RoutingService.class);

		// an example geographical flooding
		// routingService.startRoutingTask(
		// new GeoFloodingHeader(new RectangleTarget(new Position(50,50),50)),
		// new MyFloodingMessage());

		// create an BeaconListener and register at the beaconing service
		NeighborDiscoveryListener myBeaconingListener = new MyBeaconingListener(this);
		runtimeOperatingSystem.registerAtService(neighborDiscoveryServiceID, myBeaconingListener,
				NeighborDiscoveryService.class);
		MyBeaconingData myDataForTheBeacon=new MyBeaconingData(myGroups);
		neighborDiscoveryService.setOwnData(myDataForTheBeacon);

		// set a timeout
		runtimeOperatingSystem.setTimeout(new ServiceTimeout(1) {
			public void handle() {
				MyBeaconingData myDataForTheBeacon = new MyBeaconingData(myGroups);
				neighborDiscoveryService.setOwnData(myDataForTheBeacon);
			}
		});
	}

	Position getCurrentPosition() {
		return positioningService.getPositioningData().getPosition();
	}

	//
	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	//
	public void finish() {
		// TODO Auto-generated method stub
	}

	//
	public Shape getShape() {
		return shape;
	}

	//
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
	}

	/**
	 * TODO: comment method
	 * 
	 */
	public void receiveAnExampleMessage() {
		// this method is called by message MyFloodingMessage
	}

	public Address getAddress() {
		return myAddress;
	}
    
    public HashMap getNeighbors() {
        return myNeighbors;
    }

	public Address nearestGreedyNeighbor(Grid g) {
		//System.out.println(myAddress + ": searching nearest neighbour for grid " + g + " (" + myNeighbors.size()
		//		+ " known neighbours)");
		Iterator i = myNeighbors.keySet().iterator();
		Address bestaddress = null;
		double centerx = (g.maxx - g.minx) / 2.0 + g.minx;
		double centery = (g.maxy - g.miny) / 2.0 + g.miny;
		double bestdistance = Math.sqrt(Math.pow(getCurrentPosition().getX() - centerx, 2)
				+ Math.pow(getCurrentPosition().getY() - centery, 2));
		while (i.hasNext()) {
			Address a = (Address) i.next();
			Position p = ((PositioningData) myNeighbors.get(a)).getPosition();
			double dist = Math.sqrt(Math.pow(p.getX() - centerx, 2) + Math.pow(p.getY() - centery, 2));
			if (dist < bestdistance) {
				bestdistance = dist;
				bestaddress = a;
			}
		}
		return bestaddress;
	}

	public RoutingHeader getDelegationRoutingHeader(RoutingHeader routingHeaderWithDelegationData) {
		return new PositionBasedMulticastHeader(routingHeaderWithDelegationData);
	}

	public LocationBasedRoutingHeader getLocationRoutingHeader(Location location) {
		return null;
	}

	public LocationBasedRoutingHeader getLocationRoutingHeader(LocationBasedRoutingHeader header) {
		return null;
	}
    public LocationBasedRoutingHeader getLocationRoutingHeader(RoutingHeader otherRoutingHeader, Location location) {
        // TODO Auto-generated method stub
        return null;
    }

	//
	public MulticastGroupID[] getJoinedGroups() {
		// TODO Auto-generated method stub
		return null;
	}

}
