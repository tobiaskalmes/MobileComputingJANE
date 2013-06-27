/*
 * Funktioniert
 * Created on 03.11.2004
 *
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This Condition implements the so called sooner back strategy. This means 
 * the routing algorithm will change back to greedy soon as possible, if currentNode has
 * a neighbor nearer to destination as sourceNode.
 * 
 * @author Stefan Peters
 *
 */
public class ResumeGreedyConditionImpl implements ResumeGreedyCondition {

    public static final ResumeGreedyConditionFactory FACTORY = new ResumeGreedyConditionFactory() {
        public ResumeGreedyCondition createResumeGreedyCondition() {
            return new ResumeGreedyConditionImpl();
        }
    };
	private boolean satisfied;

    public ResumeGreedyConditionImpl() {
	}

	private ResumeGreedyConditionImpl(boolean satisfied) {
		this.satisfied = satisfied;

	}

//    public boolean resumeGreedy(NetworkNode startNode, NetworkNode destinationNode, PlanarGraphNode currentNode) {
//
//    }
	public ResumeGreedyCondition nextNode(NetworkNode startNode, NetworkNode destinationNode, NetworkNode greedyFailureNode, PlanarGraphNode currentNode, NetworkNode[] neighbors) {

        Position start=startNode.getPosition();
        Position destination=destinationNode.getPosition();
        double distance=start.distance(destination);
        double distance2=currentNode.getPosition().distance(destination);
        double distanceGreedy=greedyFailureNode.getPosition().distance(destination);
//        if(){
//            
//            return new ResumeGreedyConditionImpl(greedyFailureNode.getPosition()., destinationNode, currentNode));    
//        }
        return new ResumeGreedyConditionImpl(distance2<distance&&distance2<distanceGreedy);
        
		
	}

	public boolean isSatisfied() {
		return satisfied;
	}

	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

}
