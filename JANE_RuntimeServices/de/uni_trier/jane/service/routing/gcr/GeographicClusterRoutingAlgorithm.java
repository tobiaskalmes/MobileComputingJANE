package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.routing.greedy.PositionUnicastRoutingAlgorithm_Sync;

public interface GeographicClusterRoutingAlgorithm extends
		StartConditionFactory,
		CrossingConditionFactory,
		TurnConditionFactory,
		ResumeGreedyConditionFactory,
		BreakConditionFactory,
		FinishConditionFactory {

	public PlanarGraphExplorer getPlanarGraphExplorer();
	public ClusterTransitionNodeOrder getClusterNodeOrder();
	
	public DestinationClusterNodeOrder getDestinationClusterNodeOrder();
	public DestinationClusterNextNodeSelector getDestinationClusterNextNodeSelector();
	
	public NextNodeSelector getNextNodeSelector();
	public ClusterPlanarizerService getClusterPlanarizerService();
	public ClusterDiscoveryService getClusterDiscoveryService();
	public NeighborDiscoveryService_sync getNeighborDiscoveryService();
    /**
     * TODO Comment method
     * @return
     */
    public PositionUnicastRoutingAlgorithm_Sync getRecovery();
    /**
     * TODO Comment method
     * @return
     */
    public Address getOwnAddress();
	
}
