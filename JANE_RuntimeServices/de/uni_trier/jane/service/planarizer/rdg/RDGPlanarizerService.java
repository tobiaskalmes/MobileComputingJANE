/*
 * @author Stefan Peters
 * Created on 14.06.2005
 */
package de.uni_trier.jane.service.planarizer.rdg;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.rdg.delaunay.*;
import de.uni_trier.jane.service.routing.face.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Stefan Peters
 */
public class RDGPlanarizerService extends AbstractPlanarizerService implements NeighborDiscoveryListener {
	
	private NetworkEdge[] networkEdges;

	private Map neighborTriangulation;

	private Address address;
	
	private RuntimeOperatingSystem runtimeOperatingSystem;

	private boolean calculateNew;
	
	public static void createInstance(ServiceUnit serviceUnit){
		ServiceID neighborDiscoveryServiceID=null;
		if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)){
			OneHopNeighborDiscoveryService.createInstance(serviceUnit,true,false);
		}
		neighborDiscoveryServiceID=serviceUnit.getService(NeighborDiscoveryService_sync.class);
		Service planarizerService=new RDGPlanarizerService(neighborDiscoveryServiceID);
		serviceUnit.addService(planarizerService);
	}

	/**
	 * @param neighborDiscoveryServiceID
	 * @param udg
	 * @param linkLayerServiceID
	 */
	public RDGPlanarizerService(ServiceID neighborDiscoveryServiceID) {
		super(neighborDiscoveryServiceID);
		neighborTriangulation=new HashMap();
	}

	public PlanarGraphNode getPlanarGraphNode() {
		Position ownPosition=((LocationData) neighborDiscoveryService.getNeighborDiscoveryData(address).getDataMap().getData(LocationData.DATA_ID)).getPosition();
		return getPlanarGraphNode(new NetworkNodeImpl(address,ownPosition,false));
	}

	public PlanarGraphNode getPlanarGraphNode(NetworkNode centerNode) {
		

//if(address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(20)))) {
//	System.out.println("compute nodes of node 2.");
//}

		PartialTriangulation data=computePartialDelauneyGraph();
		// ich habe jetzt einen Graphen mit allen verbleibenden Kanten
		// daraus muss jetzt ein PlanarGraphNode erstellt werden
		
				
		
		return new TriangulationPlanarGraphNode(data,centerNode,centerNode);
	}

	public Shape getShape() {

		if(true) {
			return super.getShape();
		}
		
		if(
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(56))) &&
				
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(20))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(32))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(37))) &&
			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(20))) 
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(58))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(47))) &&
			
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(16))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(24))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(89))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(83))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(40))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(68))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(1))) &&
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(38))) &&
			
//			!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(60)))
			) {
			return null;
//			return super.getShape();
		}

		if(false) {
			System.out.print(address + " : ");
			NeighborDiscoveryData[] neighborDiscoveryData=neighborDiscoveryService.getNeighborDiscoveryData();
			for(int i=0; i<neighborDiscoveryData.length; i++) {
				if(neighborDiscoveryData[i].getHopDistance() == 1) {
					System.out.print(neighborDiscoveryData[i].getSender() + " ");
				}
			}
			System.out.println();
		}		

//		if(true)
//		return super.getShape();

		
		NeighborDiscoveryData[] neighborDiscoveryData=neighborDiscoveryService.getNeighborDiscoveryData();
		NetworkNode[] nodes=new NetworkNode[neighborDiscoveryData.length];
		if(nodes.length<=1)
			return null;
		for(int i=0;i<nodes.length;i++){
			LocationData data=(LocationData) neighborDiscoveryData[i].getDataMap().getData(LocationData.DATA_ID);
			nodes[i]=new NetworkNodeImpl(neighborDiscoveryData[i].getSender(),data.getPosition(),neighborDiscoveryData[i].getHopDistance()==1);
		}
		NetworkNode me=new NetworkNodeImpl(neighborDiscoveryService.getOwnAddress(),((LocationData)(neighborDiscoveryService.getNeighborDiscoveryData(neighborDiscoveryService.getOwnAddress()).getDataMap().getData(LocationData.DATA_ID))).getPosition());
		if(true) {
			System.out.print(address + " : ");
			for(int i=0; i<nodes.length; i++) {
				System.out.print(nodes[i].getAddress() + " ");
			}
			System.out.println();
		}		

		
		NetworkEdge[] edges = DelaunayTriangulationHelper.delaunayTriangulation(nodes);

		
		ShapeCollection shapeCollection = new ShapeCollection();
//		PartialTriangulation partialTriangulation = DelaunayTriangulation.delaunayTriangulationcomputePartialDelauneyGraph();
//		NetworkEdge[] edges = partialTriangulation.getEdges();
		for(int i=0; i<edges.length; i++) {
			NetworkEdge edge = edges[i];
			Shape line = new LineShape(edge.getNodeA().getAddress(), edge.getNodeB().getAddress(), Color.BLUE);
			shapeCollection.addShape(line);
		}
		
//		if(!address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(22)))) {
//			return null;
//		}
		
		return shapeCollection;
	}

	private PartialTriangulation computePartialDelauneyGraph() {
		/*Pseudo Code
		 * u node
		 * N(u):= neighbors of u\
		 * T(u):= delauney triangulation denoted by u on N(u)
		 * 
		 * E(u):=(uv|uv in T(u)}
		 * For each edge uv in E(u) do
		 * 		For each w in N(u) do
		 * 			if(u,v in N(w) and uv not in T(w)) then
		 * 				delete uv from E(u)
		 */
		TransmitData data=(TransmitData) neighborTriangulation.get(address);
		NetworkEdge[] localTrian=data.getEdgeData().getEdges();
		//E_u erstellen
		PartialTriangulation E_u=PartialTriangulationFactory.getPartialTriangulation();
		Set set=new HashSet();
		for(int i=0;i<localTrian.length;i++){
			NetworkEdge edge=localTrian[i];
			NetworkNode nodeA=edge.getNodeA();
			NetworkNode nodeB=edge.getNodeB();
			if(nodeA.getAddress().equals(address)){
				if(!set.contains(nodeB.getAddress())){
					set.add(nodeB.getAddress());
					E_u.add(new NetworkEdge(nodeA,nodeB));
				}
			}else if( nodeB.getAddress().equals(address)){
				if(!set.contains(nodeA.getAddress())){
					set.add(nodeA.getAddress());
					E_u.add(new NetworkEdge(nodeB,nodeA));
				}
			}
		}
		//NetworkEdge[] E_u=(NetworkEdge[]) vector.toArray(new NetworkEdge[vector.size()]);
		NetworkNode[] neighbors=data.getNeighbors();
		NetworkEdge[] edges=E_u.getEdges();
		for(int j=0;j<edges.length;j++){
			//System.err.println(j);
			NetworkEdge edge=edges[j];
			for(int i=0;i<neighbors.length;i++){
				
				NetworkNode node=neighbors[i];
				NetworkNode nodeA=edge.getNodeA();
				NetworkNode nodeB=edge.getNodeB();
				
//				if(!node.getAddress().equals(nodeA.getAddress())) {

					TransmitData nodeData=(TransmitData) neighborTriangulation.get(node.getAddress());
					if(nodeData==null) // TODO wozu das???
						continue;
					Map nodeNeighbors=nodeData.NeighborMap();
					set=nodeNeighbors.keySet();
					if(set.contains(nodeA.getAddress())&& set.contains(nodeB.getAddress())){
						// check whether uv is in T(w) or not
						if(!nodeData.getEdgeData().contains(edge)){
							E_u.delete(edge);
						}
					}

//				}
				
				
			}
		}
		return E_u;
	}

	private void storeDelauneyTriangulation(NeighborDiscoveryData data) {
		if(data.getDataMap().hasData(PartialDelauneyData.DATA_ID)){
			PartialDelauneyData delauneyData=(PartialDelauneyData) data.getDataMap().getData(PartialDelauneyData.DATA_ID);
			neighborTriangulation.put(data.getSender(),delauneyData.getTransmitData());
		}
	}


	
	/*
	 * Using stored neighbordata for constructing a delauney graph
	 */
	private void sendDelauneyTriangulation() {
		
		runtimeOperatingSystem.setTimeout(new CalculationTimeout(2.0));
		if(!calculateNew) {
			return;
		}
		calculateNew = false;
		
		
		NeighborDiscoveryData[] neighborDiscoveryData=neighborDiscoveryService.getNeighborDiscoveryData();
		NetworkNode[] nodes=new NetworkNode[neighborDiscoveryData.length];
		if(nodes.length<=1)
			return;
		for(int i=0;i<nodes.length;i++){
			LocationData data=(LocationData) neighborDiscoveryData[i].getDataMap().getData(LocationData.DATA_ID);
			nodes[i]=new NetworkNodeImpl(neighborDiscoveryData[i].getSender(),data.getPosition(),neighborDiscoveryData[i].getHopDistance()==1);
		}
		NetworkNode me=new NetworkNodeImpl(neighborDiscoveryService.getOwnAddress(),((LocationData)(neighborDiscoveryService.getNeighborDiscoveryData(neighborDiscoveryService.getOwnAddress()).getDataMap().getData(LocationData.DATA_ID))).getPosition());
		
//if(address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(2)))) {
//	System.out.println(nodes.length);
//	for(int i=0; i<nodes.length; i++) {
//		System.out.println(nodes[i].getAddress() + " " + nodes[i].getPosition());
//	}
//}
		
		networkEdges=DelaunayTriangulationHelper.delaunayTriangulation(nodes);
		
//		if(address.equals(new SimulationLinkLayerAddress(new SimulationDeviceID(56)))) {
//			System.out.println(networkEdges.length);
//		}
		
		TransmitData data=new TransmitData(new NetworkEdgeDataImpl(networkEdges),nodes);
		neighborTriangulation.put(me.getAddress(),data);
		neighborDiscoveryService.setOwnData(new PartialDelauneyData(data));
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		super.start(runtimeOperatingSystem);
		this.runtimeOperatingSystem = runtimeOperatingSystem;
		
		calculateNew = true;
		runtimeOperatingSystem.setTimeout(new CalculationTimeout(0.0));
		
		address = neighborDiscoveryService.getOwnAddress();
		neighborTriangulation.put(address, new TransmitData(new NetworkEdgeDataImpl(new NetworkEdge[0]), new NetworkNode[0]));
	}


	public void setNeighborData(NeighborDiscoveryData neighborData) {
//sendDelauneyTriangulation();
		calculateNew = true;
		storeDelauneyTriangulation(neighborData);
	}

	
//	private Map tmpMap = new HashMap();

	
	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		
		if(neighborData.getSender().equals(address))
			return;
//sendDelauneyTriangulation();
		calculateNew = true;
		storeDelauneyTriangulation(neighborData);

//		// TODO: test
//		Set oldSet = (Set)tmpMap.get(neighborData.getSender());
//		Set newSet = new HashSet();
//		if(neighborData.getDataMap().hasData(PartialDelauneyData.DATA_ID)){
//			PartialDelauneyData delauneyData=(PartialDelauneyData) neighborData.getDataMap().getData(PartialDelauneyData.DATA_ID);
//			NetworkNode[] nodes = delauneyData.getTransmitData().getNeighbors();
//			for(int i=0; i<nodes.length; i++) {
//				newSet.add(nodes[i]);
//			}
//			tmpMap.put(neighborData.getSender(), newSet);
//		}
//		if(oldSet == null || !(oldSet.containsAll(newSet) && newSet.containsAll(oldSet)) ) {
//			sendDelauneyTriangulation();
//			storeDelauneyTriangulation(neighborData);
//		}
		
	}


	
	public void removeNeighborData(Address linkLayerAddress) {
		neighborTriangulation.remove(linkLayerAddress);
		sendDelauneyTriangulation();
	}

	public class CalculationTimeout extends ServiceTimeout {

		public CalculationTimeout(double delta) {
			super(delta);
		}

		public void handle() {
			sendDelauneyTriangulation();
		}
		
	}
	
}
