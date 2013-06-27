package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.PlanarGraphNode;

/**
 * This class describes a routing step of the planar graph explorer. It provides information
 * about the current visited node, the last intersection point, and information about the
 * conditions used by the planar graph explorer.
 */
public class RoutingStep {

    private PlanarGraphNode node;
	private Position lastIntersection;
	private boolean clockwise;
	private boolean faceChanged;

	private StartCondition startCondition;
	private CrossingCondition crossingCondition;
	private TurnCondition turnCondition;
	private FinishCondition finishCondition;
	private BreakCondition breakCondition;
	private ResumeGreedyCondition resumeGreedyCondition;
	
	
//	private boolean turned;
	private boolean breaked;
//	private boolean finished;
	private boolean resumeGreedy;

	/**
	 * Construct a new routing step object.
     * @param node the current network node of the face exploration
     * @param lastIntersection the last intersection occured during planar graph routing
     * @param clockwise true if the current node was obtained by going in clockwise direction
     * @param faceChanged true if the face (and thus also last intersection) was changed at the
     * current node.
     * @param turned true if face exploration was turned at the current node due to the turn
     * condition used by the planar graph explorer
     * @param breaked true if face routing was interrupted as not successful at the current
     * node due to the break condition used by the planar graph explorer
     * @param finised true if face routing was finished as successful at the current node due
     * to the finish condition used by the planar graph explorer
	 * @param resumeGreedy true if greedy routing has to be resumed
     */
//    public RoutingStep(PlanarGraphNode node, Position lastIntersection,
//            boolean clockwise, boolean faceChanged, boolean turned,
//            boolean breaked, boolean finished, boolean resumeGreedy) {
//        this.node = node;
//        this.lastIntersection = lastIntersection;
//        this.clockwise = clockwise;
//        this.faceChanged = faceChanged;
//        this.turned = turned;
//        this.breaked = breaked;
//        this.finished = finished;
//        this.resumeGreedy = resumeGreedy;
//    }

    public RoutingStep(PlanarGraphNode node, Position lastIntersection,
			boolean clockwise, boolean faceChanged, boolean breaked, boolean resumeGreedy,
			FinishCondition finishCondition,
			ResumeGreedyCondition resumeGreedyCondition,
			StartCondition startCondition, CrossingCondition crossingCondition,
			TurnCondition turnCondition, BreakCondition breakCondition) {
    	
        this.node = node;
        this.lastIntersection = lastIntersection;
        this.clockwise = clockwise;
        this.faceChanged = faceChanged;
        
        this.breaked = breaked;
        this.resumeGreedy = resumeGreedy;
        
		this.finishCondition = finishCondition;
		this.resumeGreedyCondition = resumeGreedyCondition;
		this.startCondition = startCondition;
		this.crossingCondition = crossingCondition;
		this.turnCondition = turnCondition;
		this.breakCondition = breakCondition;
    	
	}

	/**
     * Get the current node of the planar graph routing step.
     * @return the current node
     */
    public PlanarGraphNode getNode() {
        return node;
    }

    /**
     * Check if the current node was obtained by using clockwise or counterclockwise
     * face exploration.
     * @return true if clockwise
     */
    public boolean isClockwise() {
        return clockwise;
    }

    /**
     * Get the last intersection point where face routing switched toi the next face.
     * @return the last intersection point
     */
    public Position getLastIntersection() {
        return lastIntersection;
    }

    /**
     * Check whether face exploration changed to the next face at the current node.
     * @return true if the face was changed
     */
    public boolean isFaceChanged() {
        return faceChanged;
    }

	public BreakCondition getBreakCondition() {
		return breakCondition;
	}

	public CrossingCondition getCrossingCondition() {
		return crossingCondition;
	}

	public FinishCondition getFinishCondition() {
		return finishCondition;
	}

	public ResumeGreedyCondition getResumeGreedyCondition() {
		return resumeGreedyCondition;
	}

	public StartCondition getStartCondition() {
		return startCondition;
	}

	public TurnCondition getTurnCondition() {
		return turnCondition;
	}

//    /**
//     * Check whether face exploration was turned in the opposite direction at the current node
//     * due to the turn condition.
//     * @return true if face routing turned in the opposite direction
//     */
//    public boolean isTurned() {
//        return turnCondition.isSatisfied();
//    }
//
    /**
     * Check whether face exploration was terminated as not successful at the current node.
     * @return true if not successful
     */
    public boolean isBreaked() {
        return breaked;
    }
//
//    /**
//     * Check if face routing was finished as successful at the current node due to the finish condition.
//     * @return true if face routing was finised as successful at the current node
//     */
//    public boolean isFinished() {
//        return finishCondition.isSatisfied();
//    }
//
    /**
     * Check if greedy routing has to be resumed.
     * @return true if greedy routing has to be resumed
     */
    public boolean isResumeGreedy() {
        return resumeGreedy;
    }
    
    public String toString(){
    	return node.toString();
    }
    
}
