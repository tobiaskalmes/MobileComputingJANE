/*
 * @author Stefan Peters
 * Created on 09.05.2005
 */
package de.uni_trier.jane.service.planarizer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This abstract class implements a localized planarizer. It calculates, with the given neighbor information,
 * a planar graph node with current node at its center.
 * 
 * @author Stefan Peters
 */

// TODO: funktioniert noch nicht mit mobilität ? Kann auch am Neighbor-Discovery liegen!!!
public abstract class AbstractPlanarizerService implements RuntimeService, PlanarizerService {
	
	private static boolean DEBUG_MODE = false;
	
	public static ServiceID SERVICE_ID=new EndpointClassID(AbstractPlanarizerService.class.getName());
	
	private ServiceID neighborDiscoveryServiceID;
	private RuntimeOperatingSystem runtimeOperatingSystem;
	protected NeighborDiscoveryService_sync neighborDiscoveryService;
	protected Address myAddress;
	
	/**
	 * Creates a new PlanarizerService object running on each device.
	 * @param neighborDiscoveryServiceID The service ID of a neighbor discovery service.
	 */
	public AbstractPlanarizerService(ServiceID neighborDiscoveryServiceID) {
		this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		this.runtimeOperatingSystem=runtimeOperatingSystem;
		runtimeOperatingSystem.registerAccessListener(PlanarizerService.class);
		neighborDiscoveryService=new NeighborDiscoveryServiceStub(runtimeOperatingSystem,neighborDiscoveryServiceID);
		myAddress=neighborDiscoveryService.getOwnAddress();
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {
		PlanarGraphNode thisPlanarGraphNode = getPlanarGraphNode();
		if(thisPlanarGraphNode == null) {
			return null;
		}
		
		ShapeCollection result = new ShapeCollection();
		Address from = runtimeOperatingSystem.getDeviceID();
		PlanarGraphNode[] planarGraphNodes = thisPlanarGraphNode.getAdjacentNodes();
		int len = planarGraphNodes.length;
		for(int i = 0; i < len; i++) {
			PlanarGraphNode planarGraphNode = planarGraphNodes[i];
			Address to = planarGraphNode.getAddress();
			Shape shape;
			if(DEBUG_MODE) {
				shape = new HalfLineShape(from, to, Color.RED);
			}
			else {
				shape = new LineShape(from, to, Color.RED);
			}
			result.addShape(shape);
		}
		return result;
	}

	public void getParameters(Parameters parameters) {
		// ignore
	}
	
	/**
	 * 
	 * @return Returns an array of NetworkNodes of all neighbors the NeighborDiscoveryService contain information
	 */
	protected NetworkNode[] getNetworkNodes() {
		NeighborDiscoveryData[] neighborDiscoveryData = neighborDiscoveryService.getNeighborDiscoveryData(); 
		NetworkNode[] nodes = new NetworkNode[neighborDiscoveryData.length];
		for (int i = 0; i < neighborDiscoveryData.length; i++) {
			Address address=neighborDiscoveryData[i].getSender();
			Position position=((LocationData) neighborDiscoveryData[i].getDataMap().getData(LocationData.DATA_ID)).getPosition();
			int hopDistance=neighborDiscoveryData[i].getHopDistance();
			nodes[i] = new NetworkNodeImpl(address, position,hopDistance == 1);
		}
		return nodes;
	}

	/**
	 * 
	 * @param address The address of the NetworkNode
	 * @return Returns the NetworkNode specified by the given address
	 */
	protected NetworkNode getNetworkNode(Address address) {
		LocationData locationData =
			(LocationData)neighborDiscoveryService.getData(address, LocationData.DATA_ID);
		return new NetworkNodeImpl(address, locationData.getPosition());
	}

	/**
	 * 
	 * @return Returns the current node as NetworkNode
	 */
	protected NetworkNode getCurrentNetworkNode() {
		NeighborDiscoveryData neighborDiscoveryData=neighborDiscoveryService.getNeighborDiscoveryData(myAddress);
		Position myPosition=((LocationData)neighborDiscoveryData
                .getDataMap()
                .getData(LocationData.DATA_ID))
                .getPosition();
		return new NetworkNodeImpl(myAddress, myPosition);
	}
	
}
