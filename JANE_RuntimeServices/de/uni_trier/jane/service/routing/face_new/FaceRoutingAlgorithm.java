package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.greedy.*;

/**
 * An interface to describe information face routing have to provide to handle a routing task in the header of an message
 * @author Stefan Peters
 *
 */
public interface FaceRoutingAlgorithm extends StartConditionFactory,
		CrossingConditionFactory, TurnConditionFactory,
		ResumeGreedyConditionFactory, BreakConditionFactory,
		FinishConditionFactory {

	/**
	 * 
	 * @return Returns an instance of the <code>PlanarGraphExcplore</code>
	 */
	public PlanarGraphExplorer getPlanarGraphExplorer();

	/**
	 * 
	 * @return Returns a <code>StepSelector</code> to chose next routing step
	 */
	public StepSelector getNextNodeSelector();

	/**
	 * 
	 * @return Returns a <code>PlanarizerService</code> to get a <code>PlanarGraphNode</code>
	 */
	public PlanarizerService getPlanarizerService();

	/**
	 * 
	 * @return Returns a <code>NeighborDiscoveryService_sync</code> to get access to the neighbor data
	 */
	public NeighborDiscoveryService_sync getNeighborDiscoveryService();
	
	/**
	 * 
	 * @return Returns the id of the greedy service, or null if it not exists
	 */
	public ServiceID getGreedyID();
	
	/**
	 * 
	 * @return Returns a <code>PositionUnicastRoutingAlgorithm_Sync</code> to change to Greedy routing
	 *  if greedy id is not null
	 */
	public PositionUnicastRoutingAlgorithm_Sync getRecovery();
	
	/**
	 * This is needed if destiantion node is a one hop neighbor of current node, because destination
	 * node must not be a member of the dominating set.
	 * @return Returns a handler to handle the end of face routing
	 *
	 */
	public EndHandler getEndHandler();
	
	/**
	 * This is needed if the start node is not a dominating set node
	 * @return Returns a handler to handle entering the dominating set
	 */
	public EnterHandler getEnterHandler();

	/**
	 * 
	 * @return Returns the current node as NetworkNode
	 */
	public NetworkNode getNetworkNode();
}
