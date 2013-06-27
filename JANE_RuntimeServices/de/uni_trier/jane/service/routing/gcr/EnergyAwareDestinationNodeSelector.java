package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.routing.greedy.metric.*;

// TODO Klasse nur zum testen
public class EnergyAwareDestinationNodeSelector implements DestinationClusterNextNodeSelector {

	private EnergyMetric energyMetric;
	
	public EnergyAwareDestinationNodeSelector() {
		EnergyEstimate energyEstimate = new EnergyEstimate(2, 1, 2000);
		energyMetric = new EnergyMetric(2, 1, 2000, energyEstimate);
	}
	
	public NeighborDiscoveryData selectNextNode(Cluster sourceCluster, Cluster destinationCluster, NetworkNode sourceNode, NetworkNode destinationNode, NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors) {

	

		Position sender = LocationData.getPosition(currentNode);
		Position destination = destinationCluster.getCenter();//destinationNode.getPosition();
//		destination = destination.sub(currentCluster.getCenter());
//		destination = destination.scale(destinationNode.getPosition().distance(sender));
//		destination = destination.add(currentCluster.getCenter());
		
		NeighborDiscoveryData bestNeighbor = null;
		double minEnergy = Double.POSITIVE_INFINITY;
		
		double alpha = calculateAlpha(currentNode, neighbors);
		double beta = calculateBeta(currentNode, neighbors);
		
		for(int i=0; i<neighbors.length; i++) {
			NeighborDiscoveryData neighbor = neighbors[i];
			
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
		return bestNeighbor;
		
		
	}

	private double calculateCost(NeighborDiscoveryData neighbor, Position destinationPosition) {
		Position position = LocationData.getPosition(neighbor);
		double cost = costFunction(neighbor);
		return cost * cost * (destinationPosition.distance(position) / 200.0);
	}

	private double calculateAlpha(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors) {
		double sum = costFunction(currentNode);
		for(int i=0; i<neighbors.length; i++) {
			sum += costFunction(neighbors[i]);
		}
		return sum / (neighbors.length + 1);
	}
	
	private double calculateBeta(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors) {
		Position u = LocationData.getPosition(currentNode);
		double sum = 0;
		for(int i=0; i<neighbors.length; i++) {
			Position v = LocationData.getPosition(neighbors[i]);
			sum += u.distance(v);
		}
		sum = sum / neighbors.length;
		return sum * sum + 2000;
	}
	
	private double costFunction(NeighborDiscoveryData neighbor) {
		double remainingEnergy = EnergyStatusData.getRemainingJoule(neighbor);
		if(remainingEnergy <= 0) {
			return Double.POSITIVE_INFINITY;
		}
		return 1000000.0/remainingEnergy;
	}

}
