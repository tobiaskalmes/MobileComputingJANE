package de.uni_trier.jane.service.routing.face_new;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.routing.greedy.metric.*;

// TODO Klasse nur zum testen
public class EnergyStepSelector extends PositionCalculator implements StepSelector {

	private double udgRadius = 200.0; // TODO test
	private double transmissionTime = 1.0; // TODO test

	private EnergyMetric energyMetric;
	
	public EnergyStepSelector() {
		EnergyEstimate energyEstimate = new EnergyEstimate(2, 1, 2000);
		energyMetric = new EnergyMetric(2, 1, 2000, energyEstimate);
	}
	

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

		NeighborDiscoveryData bestNeighbor = null;
		double minEnergy = Double.POSITIVE_INFINITY;
		
		Position sender = LocationData.getPosition(currentNode);
		Position destination = destinationNode.getPosition();

		
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
			

			double remainingEnergy = EnergyStatusData.getRemainingJoule(neighbor);
			if(remainingEnergy > 0) {
				Position receiver = LocationData.getPosition(neighbor);
				double energy = energyMetric.calculate(sender, destination, receiver);
				energy = 1 * energy + 30 * calculateCost(neighbor, destination);
				if(energy < minEnergy) {
					bestNeighbor = neighbor;
					minEnergy = energy;
				}
			}
				
		}
		
		if(bestNeighbor == null) {
			return null;
		}
		
		return bestNeighbor.getSender();

	}
	
	public boolean useReachableNodesOnly() {
		return true;
	}

	private double calculateCost(NeighborDiscoveryData neighbor, Position destinationPosition) {
		Position position = LocationData.getPosition(neighbor);
		double cost = costFunction(neighbor);
		return cost * cost * (destinationPosition.distance(position) / 150.0);
	}

	private double costFunction(NeighborDiscoveryData neighbor) {
		double remainingEnergy = EnergyStatusData.getRemainingJoule(neighbor);
		if(remainingEnergy <= 0) {
			return Double.POSITIVE_INFINITY;
		}
		return 1000000.0/remainingEnergy;
	}

}
