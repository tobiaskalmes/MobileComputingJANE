/*******************************************************************************
 * 
 * DijkstraProtocol.java
 * 
 * $Id: DijkstraProtocol.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 * 
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K.
 * Lehnert
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 ******************************************************************************/
package de.uni_trier.jane.service.routing.dijkstra;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.address_device_map.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.unicast.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.util.dijkstra.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements the dijkstra single source shortest path routing algorithm
 * which utilizes global information provided by the simulation in order to determine
 * the best routing path according to the given link metric.
 */
public class DijkstraProtocol implements SimulationService, RoutingAlgorithm, 
    UnicastRoutingAlgorithm,
    UnicastRoutingAlgorithm_Sync{

    /**
     * The ID of dijkstras algorithm
     */
	public static final ServiceID SERVICE_ID = new EndpointClassID(DijkstraProtocol.class.getName());

	// initialized in constructor
    private WeightFunction weightFunction;
	
	private ServiceID linkLayerServiceID;
	private ServiceID addressDeviceMapServiceID;
	
	// initialized on startup
	private AddressDeviceMapService.AddressDeviceMapFassade addressDeviceMapServiceStub;
	private SimulationOperatingSystem operatingService;
	private GlobalKnowledge globalKnowledge;
	private GlobalKnowledgeGraph graph;
	private Address address;
	private DijkstraAlgorithm dijkstraAlgorithm;

    /**
     * Create the global dijkstra routing algorithm with the default hop count metric
     * 
     * @param serviceUnit   the service unit the service has to be added to
     * @return              the serviceID of the created Service
     */
	public static ServiceID createInstance(ServiceUnit serviceUnit) {
		return createInstance(serviceUnit, new HopCountMetric());
	}

    /**
     * Create the global dijkstra routing algorithm with the given metric
     * 
     * @param serviceUnit       the service unit the service has to be added to
     * @param weightFunction    the metric to be used for each routing step
     * @return                  the serviceID of the created Service
     */
	public static ServiceID createInstance(ServiceUnit serviceUnit, WeightFunction weightFunction) {
		ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
		if(!serviceUnit.hasService(RoutingService.class)) {
			DefaultRoutingService.createInstance(serviceUnit);
		}
		//ServiceID routtingServiceID = serviceUnit.getService(RoutingService.class);
		ServiceID addressDeviceMapServiceID = serviceUnit.getService(LocalAddressDeviceMapService.class);
		Service dijkstraProtocol = new DijkstraProtocol(weightFunction, linkLayerService, addressDeviceMapServiceID);
		return serviceUnit.addService(dijkstraProtocol);
	}

	/**
     * Contruct a new dijkstra protocol object.
     * @param weightFunction the weight function used by djikstra's single source
     * shortest path algorithm.
     * @param linkLayerServiceID
     * @param addressDeviceMapService
     */
    public DijkstraProtocol(WeightFunction weightFunction, ServiceID linkLayerServiceID, ServiceID addressDeviceMapService) {
        this.weightFunction = weightFunction;
        //this.routingServiceID = routingServiceID;
        this.linkLayerServiceID = linkLayerServiceID;
        this.addressDeviceMapServiceID = addressDeviceMapService;
    }

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

    public void start(SimulationOperatingSystem operatingService) {
        this.operatingService = operatingService;
        operatingService.registerSignalListener(UnicastRoutingAlgorithm.class);
    	operatingService.registerAccessListener(UnicastRoutingAlgorithm_Sync.class);

        addressDeviceMapServiceStub = new AddressDeviceMapService.AddressDeviceMapFassade(addressDeviceMapServiceID, operatingService);
        LinkLayer.LinkLayerStub facade = new LinkLayer.LinkLayerStub(operatingService, linkLayerServiceID);
        address = facade.getLinkLayerProperties().getLinkLayerAddress();
    	globalKnowledge = operatingService.getGlobalKnowledge();
		graph = new GlobalKnowledgeGraph(weightFunction);
		dijkstraAlgorithm = new DijkstraAlgorithm(graph);
		graph.start(globalKnowledge);
		
//		operatingService.registerAtService(routingServiceID, RoutingAlgorithmReplyHandler.class);

    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        return null;
    }

    //
    public void handleStartUnicastRequest(
            RoutingTaskHandler routingTaskHandler, LinkLayerAddress destination) {
        RoutingHeader routingHeader = getUnicastHeader(destination);
        forward(routingTaskHandler, routingHeader);
    }

    
    public void handleStartRoutingRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        forward(handler,routingHeader);

    }
    

    public void handleMessageReceivedRequest(RoutingTaskHandler replyHandle, RoutingHeader header, Address sender) {
        forward(replyHandle, header);
    }

    public void handleUnicastErrorRequest(RoutingTaskHandler replyHandle, RoutingHeader header, Address receiver) {
        forward(replyHandle, header);
    }
    
    public void handleMessageForwardProcessed(RoutingHeader header) {
        //ignore
    }
    
    //
    public void handleMessageDelegateRequest(RoutingTaskHandler handler,
            RoutingHeader routingHeader) {
        //ignore

    }
    
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }

//    public void notifyMessageDropped(MessageID messageID) {
//        // ignore
//    }

    // get the next routing action by applying the Dijkstra algorithm on the global knowledge
	private void forward(RoutingTaskHandler replyHandle, RoutingHeader header) {
	    DijkstraHeader dijkstraHeader = (DijkstraHeader)header;
	    Address destinationAddress = dijkstraHeader.getReceiver();
		if(!destinationAddress.equals(address)) {
			graph.refresh();
			int source = graph.getNode(operatingService.getDeviceID());
			DijkstraAlgorithmResult result = dijkstraAlgorithm.solve(source);
			Address destinationLinkLayerAddress = dijkstraHeader.getReceiver();
			DeviceID destinationID = addressDeviceMapServiceStub.getDeviceID(destinationLinkLayerAddress);
			int destination = graph.getNode(destinationID);
			Path path = result.getPath(destination);
			if(path != null) {
				int sucessor = path.getSuccessor(source);
				DeviceID next = graph.getAddress(sucessor);
				Address nextAddress = addressDeviceMapServiceStub.getAddress(next);
				replyHandle.forwardAsUnicast(header,nextAddress);
				
			}
			else {
			    replyHandle.dropMessage(dijkstraHeader);
			   
				
			}
		}
		else {
		    
		    replyHandle.deliverMessage(header);
		}
	}

    /**
     * 
     * @author goergen
     *
     * TODO comment class
     */
	public static class DijkstraHeader extends DefaultRoutingHeader  {

        
        private static final EllipseShape SHAPE = new EllipseShape(Position.NULL_POSITION, new Extent(20,20), Color.RED, true);
	    public DijkstraHeader(Address sender, Address receiver, boolean countHops, boolean traceRoute) {
            super(sender, receiver, countHops, traceRoute);
        }

        
	    
		


        
 
        
        /**
         * Constructor for class <code>DijkstraHeader</code>
         * @param header
         */
        public DijkstraHeader(DijkstraHeader header) {
            super(header);
        }



        public ServiceID getRoutingAlgorithmID() {
            return SERVICE_ID;
        }

        public LinkLayerInfo copy() {
            return new DijkstraHeader(this);
        }

        public int getCodingSize() {
            return 0;
        }

        public Shape getShape() {
            return SHAPE;
        }

	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("weight function", weightFunction.toString());
	}

    public RoutingHeader getUnicastHeader(Address destination) {
        return new DijkstraHeader(null,destination,false,false);
    }

}
