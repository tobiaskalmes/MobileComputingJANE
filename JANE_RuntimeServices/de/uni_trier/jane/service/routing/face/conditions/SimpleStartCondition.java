/*
 * Funktioniert
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * This is a simple implementation of the start condition. It always returns the value
 * it was initialized with.
 * @author stefan
 *
 */
public class SimpleStartCondition implements StartCondition {
	private boolean clockwise;
	
	/**
	 * The Constructor
	 * @param clockwise True for clockwise, false for counterclockwise
	 */
	public SimpleStartCondition(boolean clockwise) {
		this.clockwise=clockwise;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.StartCondition#isClockwise(de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.PlanarGraphNode, de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.PlanarGraphNode, de.uni_trier.ubi.appsim.service.routing.planar_graph_explorer.PlanarGraphNode, de.uni_trier.ubi.appsim.kernel.basetype.NetworkNode, de.uni_trier.ubi.appsim.kernel.basetype.NetworkNode[])
	 */
	public boolean isClockwise(PlanarGraphNode startNode,
			PlanarGraphNode cwNode, PlanarGraphNode ccwNode,
			NetworkNode destinationNode, NetworkNode[] neighbors) {
		return clockwise;
	}

	public StartCondition nextEdge(PlanarGraphNode startNode, PlanarGraphNode cwNode, PlanarGraphNode ccwNode, NetworkNode destinationNode, NetworkNode[] neighbors) {
		return new SimpleStartCondition(clockwise);
	}

	public boolean isClockwise() {
		return clockwise;
	}
	
	

}
