package de.uni_trier.jane.service.routing.face_new;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;

public class MobilityStepSelector extends PositionCalculator implements StepSelector {

	private double udgRadius = 100.0; // TODO test
	private double transmissionTime = 1.0; // TODO test

	public int getStepIndex(RoutingStep[] steps, NetworkNode destination, NeighborDiscoveryData[] neighborData) {

		int maxStep = 0;
		
		// TODO Das ist nicht die Aufgabe des Step-Selectors!!!
		Map indexMap = new HashMap();
		List neighborList = new LinkedList();
		NeighborDiscoveryData currentNeighbor = null;
		
		for(int i=0; i<steps.length; i++) {
			indexMap.put(steps[i].getNode().getAddress(), new Integer(i));
		}
		
		for(int i=0; i<neighborData.length; i++) {
			if(neighborData[i].getHopDistance() == 0) {
				currentNeighbor = neighborData[i];
			}
			else if(neighborData[i].getHopDistance() == 1 && indexMap.containsKey(neighborData[i].getSender())) {
				Integer index = (Integer)indexMap.get(neighborData[i].getSender());
				maxStep = Math.max(maxStep, index.intValue());
				neighborList.add(neighborData[i]);
			}
		}


		NeighborDiscoveryData[] neighbors = (NeighborDiscoveryData[])neighborList.toArray(new NeighborDiscoveryData[neighborList.size()]);
		
		Address address = getBestNeighbor(currentNeighbor, neighbors, destination);
		
		if(address != null) {
			Integer index = (Integer)indexMap.get(address);
//			System.out.println(steps.length + " " + index);
					return index.intValue();
		}

		return maxStep;
		

	}

	// find the best neighbor
	public Address getBestNeighbor(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors, NetworkNode destinationNode) {

		Position currentPosition = getPosition(currentNode, transmissionTime);

		NeighborDiscoveryData result = null;
		double dist = Double.POSITIVE_INFINITY;
		for(int i=0; i<neighbors.length; i++) {
			
			NeighborDiscoveryData neighbor = neighbors[i];
			
			// return the destination node if it is reachable
			if(neighbor.getSender().equals(destinationNode.getAddress())) {
				Position destinationPosition = getPosition(neighbor, transmissionTime);
				if(currentPosition == null || destinationPosition == null || currentPosition.distance(destinationPosition) < udgRadius) {
					return neighbor.getSender();
				}
			}

			Position neighborPosition = getPosition(neighbor, transmissionTime);
			
			if(neighborPosition != null) {
				
				if(currentPosition == null || currentPosition.distance(neighborPosition) < udgRadius) {
					double d = destinationNode.getPosition().distance(neighborPosition);
					if(d < dist) {
						result = neighbor;
						dist = d;
					}
				}

			}
		}
		
		if(result == null) {
			return null;
		}
		
		return result.getSender();

	}
	
	public boolean useReachableNodesOnly() {
		return true;
	}

}
