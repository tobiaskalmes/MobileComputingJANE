package de.uni_trier.jane.service.routing;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.location_directory.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * TODO:comment
 * @author Stefan Peters
 *
 */
public abstract class AbstractPositionBasedRoutingAlgorithm extends AbstractRoutingAlgorithm implements PositionUnicastRoutingAlgorithm_Sync, LocationDirectoryEntryReplyHandler {

    private Map pendingMessageMap;

    
	// initialized in constructor
	private ServiceID locationDirectoryID;
	private ServiceID neighborDiscoveryID;
    // initialized on startup
    private Address ownAddress;

    // initialized on demand
	private LocationDirectoryService locationDirectory;
	private NeighborDiscoveryService_sync neighborDiscoveryService;

	public AbstractPositionBasedRoutingAlgorithm(ServiceID locationDirectoryID, ServiceID neighborDiscoveryID) {
		this.locationDirectoryID = locationDirectoryID;
		this.neighborDiscoveryID = neighborDiscoveryID;
		pendingMessageMap = new HashMap();
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {

		super.start(runtimeOperatingSystem);
		
		// remember OS
//		this.runtimeOperatingSystem = runtimeOperatingSystem;
		
		// export implemented interfaces
    	runtimeOperatingSystem.registerSignalListener(RoutingAlgorithm.class);
		runtimeOperatingSystem.registerAccessListener(PositionUnicastRoutingAlgorithm_Sync.class);

		// determine the stub for accessing the location directory service
        if (locationDirectoryID!=null){
            locationDirectory = (LocationDirectoryService) runtimeOperatingSystem.getSignalListenerStub(
				locationDirectoryID, LocationDirectoryService.class);
        }

	}



	public void handleLocationDataReply(LocationDirectoryEntry reply) {
		Position destinationPosition = reply.getPosition();
		Address destinationAddress = reply.getAddress();
		List list = (List) pendingMessageMap.remove(destinationAddress);
		if (list != null) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				PendingMessageEntry pendingMessageEntry = (PendingMessageEntry) iterator.next();
				PositionbasedRoutingHeader header = (PositionbasedRoutingHeader) pendingMessageEntry.getRoutingHeader();
				if (destinationPosition != null) {
					header.setReceiverPosition(destinationPosition);
					handleFirstHop(pendingMessageEntry.getRoutingTaskHandler(), header);
				}
				else {
					pendingMessageEntry.getRoutingTaskHandler().dropMessage(header);
				}
			}
		}
	}


	public void handleStartRoutingRequest(RoutingTaskHandler handler, RoutingHeader routingHeader) {

        PositionbasedRoutingHeader header = (PositionbasedRoutingHeader)routingHeader;
        if (header.hasReceiverPosition()){
            handleFirstHop(handler, header);
            return;
        }
        
        
        Address destination = header.getReceiver();
        if (destination.equals(getOwnAddress())){
            handler.deliverMessage(routingHeader);
            return;
        }
        List pendingMessageList = (List)pendingMessageMap.get(destination);
        
        // we are requested about the destination for the first time
        if(pendingMessageList == null) {
            pendingMessageList = new ArrayList();
            pendingMessageMap.put(destination, pendingMessageList);

            // This is the NEW location directory entry request
            ListenerID listenerID = runtimeOperatingSystem.registerOneShotListener(this, LocationDirectoryEntryReplyHandler.class);
            locationDirectory.requestLocationDirectoryEntry(destination, listenerID);

        }

        pendingMessageList.add(new PendingMessageEntry(handler, header));

	}
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }
	
	
	
	protected Position getOwnPosition() {
		Address ownAddress = getOwnAddress();
		LocationData data = (LocationData)getNeighborDiscoveryService().getData(ownAddress, LocationData.DATA_ID);
		if(data == null) {
			return null;
		}
		return data.getPosition();
	}
	
	public Address getOwnAddress() {
		if(ownAddress == null) {
			ownAddress = getNeighborDiscoveryService().getOwnAddress();
		}
		return ownAddress;
	}
	
//	protected abstract void handleFirstHop(RoutingTaskHandler handler, PositionbasedRoutingHeader routingHeader);

	
	public NeighborDiscoveryService_sync getNeighborDiscoveryService() {
		if(neighborDiscoveryService == null) {
			neighborDiscoveryService =
				(NeighborDiscoveryService_sync)runtimeOperatingSystem.getAccessListenerStub(
						neighborDiscoveryID, NeighborDiscoveryService_sync.class);
		}
		return neighborDiscoveryService;
	}

	
	private void handleFirstHop(RoutingTaskHandler handler, PositionbasedRoutingHeader header) {
		DefaultPositionBasedHeader positionBasedHeader = (DefaultPositionBasedHeader)header;
		positionBasedHeader.handle(handler, this);
	}


	public ServiceID getServiceID() {
		return null;
	}

	
	public void finish() {
		//ignore
	}

	public Shape getShape() {
		return null;
	}

	public void getParameters(Parameters parameters) {
//		ignore
	}

}
