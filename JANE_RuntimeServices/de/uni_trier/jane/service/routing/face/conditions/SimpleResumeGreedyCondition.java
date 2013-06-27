/*
 * Created on 20.03.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * An absolute simple implementation of ResumeGreedyCondtion. It always returns false.
 * @author Stefan Peters
 */
public class SimpleResumeGreedyCondition implements ResumeGreedyCondition {

	public static final ResumeGreedyConditionFactory FACTORY = new ResumeGreedyConditionFactory() {
		public ResumeGreedyCondition createResumeGreedyCondition() {
			return new SimpleResumeGreedyCondition();
		}
	};
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.planar_graph_explorer.ResumeGreedyCondition#resumeGreedy(de.uni_trier.jane.service.routing.planar_graph_explorer.NetworkNode, de.uni_trier.jane.service.routing.planar_graph_explorer.NetworkNode, de.uni_trier.jane.service.routing.planar_graph_explorer.PlanarGraphNode)
	 */
	public boolean resumeGreedy(NetworkNode startNode,
			NetworkNode destinationNode, PlanarGraphNode currentNode) {
		return false;
	}

	public ResumeGreedyCondition nextNode(NetworkNode startNode, NetworkNode destinationNode, NetworkNode greedyFailureNode, PlanarGraphNode currentNode, NetworkNode[] neighbors) {
		return this;
	}

	public boolean isSatisfied() {
		return false;
	}

	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

}
