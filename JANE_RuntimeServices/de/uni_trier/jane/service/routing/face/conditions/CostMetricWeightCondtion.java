/*
 * @author Stefan Peters
 * Created on 22.04.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.EnergyStatusData;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * This is an implementation of the <CODE>WeightCondition</CODE> with the use of a cost metric.
 * It returns the number of the RoutingStep in the RoutingStep field with the heighest amount of remaining energy.
 * @author Stefan Peters
 */
public class CostMetricWeightCondtion implements StepSelector {

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.planar_graph_explorer.WeightCondition#weightFunction(de.uni_trier.jane.service.routing.planar_graph_explorer.RoutingStep[], de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData[])
	 */
	public int getStepIndex(RoutingStep[] steps, NetworkNode destination,
			NeighborDiscoveryData[] neighborData) {
		int k=0;
		double cost=Double.POSITIVE_INFINITY;
		for(int i=1;i<steps.length;i++){
			for(int j=0;j<neighborData.length;j++){
				if(neighborData[j].getSender().equals(steps[i].getNode().getAddress())){
					EnergyStatusData energyStatusData=((EnergyStatusData)neighborData[j].getDataMap().getData(EnergyStatusData.DATA_ID));
					if(cost>energyStatusData.getRemainingJoule()){
						cost=energyStatusData.getRemainingJoule();
					}
					break;
				}
			}
		}
		return k;
	}

	public boolean useReachableNodesOnly() {
		return true;
	}

	public Address getBestNeighbor(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors, NetworkNode destinationNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
