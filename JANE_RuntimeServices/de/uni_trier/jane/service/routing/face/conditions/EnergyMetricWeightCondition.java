/*
 * @author Stefan Peters
 * Created on 30.03.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.metric.*;

/**
 * This implements <CODE>WeightCondition</CODE> using an energy metric. It minimize the value
 * returned by a Metric like <CODE>EnergyMetric</CODE> and returns the number of the selected node.
 * @author Stefan Peters
 */
public class EnergyMetricWeightCondition implements StepSelector {

    private LocalRoutingMetric metricInterface;
    
    /**
     * Constructor <p>
     * Creates an energy metric object.
     * @param metricInterface The metric implementation
     */
	public EnergyMetricWeightCondition(LocalRoutingMetric metricInterface) {
		this.metricInterface=metricInterface;
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.planar_graph_explorer.WeightCondition#weightFunction(de.uni_trier.jane.service.routing.planar_graph_explorer.RoutingStep[])
	 */
	public int getStepIndex(RoutingStep[] steps,NetworkNode destination,NeighborDiscoveryData[] neighborDiscoveryDatas) {
		HopCountWeightCondition hopCountWeightCondition = new HopCountWeightCondition();
		int number=hopCountWeightCondition.getStepIndex(steps,destination,neighborDiscoveryDatas);
//		if(number==WeightStepSelector){
//			return WeightStepSelector;
//		}
		Position sourcePosition=steps[0].getNode().getPosition();
		double d=Double.POSITIVE_INFINITY;
		int chosenNode=number;
		for(int i=1;i<=number;i++){
			Position currentPosition=steps[i].getNode().getPosition();
			double test=metricInterface.calculate(sourcePosition,destination.getPosition(), currentPosition);
			if(test<d&& !steps[0].getNode().getAddress().equals(steps[i].getNode().getAddress())){
				d=test;
				chosenNode=i;
			}	
		}
		return chosenNode;
	}
	public boolean useReachableNodesOnly() {
		return true;
	}
	public Address getBestNeighbor(NeighborDiscoveryData currentNode, NeighborDiscoveryData[] neighbors, NetworkNode destinationNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
