package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.util.*;

/**
 * This class realizes a generic implementation of a planar graph traversal. The
 * algorithm is intended to be used by devices having a local view of a planar
 * graph. The local view of the planar graph has to be at least the current
 * planar graph node and its adjacent neighbors. More elaborate local views of
 * more than the current adjacent neighbor nodes might also be provided (e.g. by
 * using two-hop information in order to construct the planar graph seen by all
 * one-hop neighbors, too). However, in all cases the local view of the planar
 * graph has to be limited by so called stop nodes. The evaluation of the
 * conditions is performed in the following order, while
 * <code>StartCondition</code> is evaluated only when face exploration is
 * started.
 * 
 * <ol>
 * <li><code>StartCondition</code></li>
 * <li><code>FinishCondition</code></li>
 * <li><code>ResumeGreedyCondition</code></li>
 * <li><code>CrossingCondition</code></li>
 * <li><code>TurnCondition</code></li>
 * <li><code>BreakCondition</code></li>
 * </ol>
 * 
 * @see de.uni_trier.jane.service.planarizer.PlanarGraphNode
 * @see de.uni_trier.jane.service.routing.face.planar_graph_explorer.StartCondition
 * @see de.uni_trier.jane.service.routing.face.planar_graph_explorer.FinishCondition
 * @see de.uni_trier.jane.service.routing.face.planar_graph_explorer.ResumeGreedyCondition
 * @see de.uni_trier.jane.service.routing.face.planar_graph_explorer.BreakCondition
 * @see de.uni_trier.jane.service.routing.face.planar_graph_explorer.CrossingCondition
 * @see de.uni_trier.jane.service.routing.face.planar_graph_explorer.TurnCondition
 */
public class PlanarGraphExplorer {

	// used in order to detect loops
	private Set visitedEdges;
	
	// global parameters of current calculation
	private NetworkNode[] neighbors;
	private NetworkNode sourceNode;
	private NetworkNode destinationNode;
	private NetworkNode greedyFailureNode;

	// the current condition states
	private StartCondition startCondition;
	private CrossingCondition crossingCondition;
	private TurnCondition turnCondition;
	private FinishCondition finishCondition;
	private BreakCondition breakCondition;
	private ResumeGreedyCondition resumeGreedyCondition;

	public PlanarGraphExplorer() {
		visitedEdges = new LinkedHashSet();
	}

	/**
	 * This method is called when face exploration from source to destination
	 * node is performed for the first time.
	 * 
	 * @param sourceNode
	 *            the node where face exploration is being started
	 * @param neighbors
	 *            all one-hop (and possibly two-hop if available) neigbors of
	 *            the start node
	 * @param destinationNode
	 *            the destination of planar graph traversal. Note, that planar
	 *            graph traversal is also possible for geocasting. In this case
	 *            the address of the destination node will be null.
	 * @param greedyFailureNode
	 *            the node, where greedy failure appeared.
	 * @param startCondition
	 *            determines if the first edge is selected in clockwise or
	 *            counterclockwise direction from the line connecting start and
	 *            destination node
	 * @param crossingCondition
	 *            used by the planar grap explorer in order to check if
	 *            traversing an edge crossing the line connecting source and
	 *            destination node will be traversed or not
	 * @param turnCondition
	 *            when limited face exploration is beeing applied, this
	 *            condition will return true when face exploration will lead
	 *            outside the current limited area
	 * @param finishCondition
	 *            determines if greedy mode can be applied once again or the
	 *            final destination is reached.
	 * @param breakCondition
	 *            determines if face exploration has to be finished as not
	 *            successful since the final destination is not reachable (e.g.
	 *            the first edge of the the current face exploration is
	 *            traversed in the same direction twice)
	 * @return a sequence of the routing hops. The last routing hop will always
	 *         be a stop node. Note, if one-hop information is used only, the
	 *         sequence will always consist of the start node and the next hop
	 *         node only.
	 */
	public RoutingStep[] startExploration(PlanarGraphNode sourceNode,
			NetworkNode[] neighbors, NetworkNode destinationNode,
			NetworkNode greedyFailureNode, StartCondition sourceStartCondition,
			CrossingCondition sourceCrossingCondition,
			TurnCondition sourceTurnCondition,
			FinishCondition sourceFinishCondition,
			BreakCondition sourceBreakCondition,
			ResumeGreedyCondition sourceResumeGreedyCondition) {

		visitedEdges.clear();
		
		// set global parameters
		this.neighbors = neighbors;
		this.sourceNode = sourceNode;
		this.destinationNode = destinationNode;
		this.greedyFailureNode = greedyFailureNode;

		// set current condition states
		this.startCondition = sourceStartCondition;
		this.crossingCondition = sourceCrossingCondition;
		this.turnCondition = sourceTurnCondition;
		this.finishCondition = sourceFinishCondition;
		this.breakCondition = sourceBreakCondition;
		this.resumeGreedyCondition = sourceResumeGreedyCondition;

		// the first intersection with the source destination line is the source
		// node itself
		Position firstIntersection = sourceNode.getPosition();

		// check if we are already done and return the start node as the single
		// routing step in that case
		EndNodeComputationResult endNodeComputationResult = computeEndNode(
				sourceNode, firstIntersection, true, true);
		if (endNodeComputationResult.isEndNode()) {
			return new RoutingStep[] { new RoutingStep(sourceNode,
					firstIntersection, true, true, endNodeComputationResult
							.isBreaked(), endNodeComputationResult
							.isResumeGreedy(), sourceFinishCondition,
					sourceResumeGreedyCondition, sourceStartCondition,
					sourceCrossingCondition, sourceTurnCondition,
					sourceBreakCondition) };
		}

		// determine face traversal direction and the position of the previous edge
		StartComputationResult startResult = computeStart(sourceNode);
		Position previousPosition = startResult.getPreviousPosition();
		boolean clockwise = startResult.isClockwise();

		// compute the first edge
		NextComputationResult result = computeNextRoutingStep(sourceNode,
				previousPosition, firstIntersection, clockwise, true);
		RoutingStep routingStep = new RoutingStep(sourceNode,
				firstIntersection, clockwise, true, result.isBreaked(),
				endNodeComputationResult.isResumeGreedy(),
				sourceFinishCondition, sourceResumeGreedyCondition,
				sourceStartCondition, sourceCrossingCondition,
				sourceTurnCondition, sourceBreakCondition);
		if (result.isBreaked()) {
			return new RoutingStep[] { routingStep };
		}

		// recursively compute the remaining and return all routing steps stored
		// in the vector
		Vector vector = new Vector();
		vector.add(routingStep);
		computeRemainingRoutingSteps(result.getNextNode(), sourceNode.getPosition(),
				result.getNextIntersection(), result.isClockwiseTraversal(),
				false, vector);
		return (RoutingStep[]) vector.toArray(new RoutingStep[vector.size()]);

	}

	/**
	 * This method is called in each intermediate node when previous face
	 * exploration which was finished at a stop node will be continued at the
	 * node where the message arrived.
	 * 
	 * @param currentNode
	 *            the node currently holding the message to be routed
	 * @param neighbors
	 *            the neighbors of the current node
	 * @param previousNode
	 *            the prevoius node of the current face traversal
	 * @param sourceNode
	 *            the source node of the planar graph routing
	 * @param destinationNode
	 *            the destination node of the planar graph routing
	 * @param lastIntersection
	 *            the last intersection point where planar graph routing changed
	 *            th the next face
	 * @param greedyFailureNode
	 *            the node, where greedy failure appeared.
	 * @param clockwise
	 *            the current face exploration direction
	 * @param startCondition
	 *            determines if the first edge is selected in clockwise or
	 *            counterclockwise direction from the line connecting start and
	 *            destination node
	 * @param crossingCondition
	 *            used by the planar grap explorer in order to check if
	 *            traversing an edge crossing the line connecting source and
	 *            destination node will be traversed or not
	 * @param turnCondition
	 *            when limited face exploration is beeing applied, this
	 *            condition will return true when face exploration will lead
	 *            outside the current limited area
	 * @param finishCondition
	 *            determines if greedy mode can be applied once again or the
	 *            final destination is reached.
	 * @param breakCondition
	 *            determines if face exploration has to be finished as not
	 *            successful since the final destination is not reachable (e.g.
	 *            the first edge of the the current face exploration is
	 *            traversed in the same direction twice)
	 * @return a sequence of the routing hops. The last routing hop will always
	 *         be a stop node. Note, if one-hop information is used only, the
	 *         sequence will always consist of the start node and the next hop
	 *         node only.
	 */
	public RoutingStep[] continueExploration(PlanarGraphNode currentNode,
			NetworkNode[] neighbors, NetworkNode previousNode,
			NetworkNode sourceNode, NetworkNode destinationNode,
			NetworkNode greedyFailureNode, Position lastIntersection,
			boolean clockwise, StartCondition startCondition,
			CrossingCondition crossingCondition, TurnCondition turnCondition,
			FinishCondition finishCondition, BreakCondition breakCondition,
			ResumeGreedyCondition resumeGreedyCondition) {

		visitedEdges.clear();

		// store all global parameters
		this.neighbors = neighbors;
		this.sourceNode = sourceNode;
		this.destinationNode = destinationNode;
		this.greedyFailureNode = greedyFailureNode;
		
		// store condition states
		this.startCondition = startCondition;
		this.crossingCondition = crossingCondition;
		this.turnCondition = turnCondition;
		this.finishCondition = finishCondition;
		this.breakCondition = breakCondition;
		this.resumeGreedyCondition = resumeGreedyCondition;

		// get the position of the previous sender while using the locally stored position information whenever possible
		Position previousPosition = previousNode.getPosition();
		NetworkNode locallyStoredPreviousNode = currentNode.getAdjacentNode(previousNode.getAddress());
		if(locallyStoredPreviousNode != null) {
			previousPosition = locallyStoredPreviousNode.getPosition();
		}
		
		// compute and return all routing steps
		Vector vector = new Vector();
		computeRemainingRoutingSteps(currentNode, previousPosition, lastIntersection, clockwise, false, vector);
		return (RoutingStep[]) vector.toArray(new RoutingStep[vector.size()]);

	}

	// Recursively compute all routing steps and store them into the passed
	// vector.
	private void computeRemainingRoutingSteps(PlanarGraphNode currentNode,
			Position previousPosition, Position lastIntersection,
			boolean clockwise, boolean faceChanged, Vector vector) {

		// remember the current condition states
		StartCondition currentStartCondition = startCondition;
		CrossingCondition currentCrossingCondition = crossingCondition;
		TurnCondition currentTurnCondition = turnCondition;
		FinishCondition currentFinishCondition = finishCondition;
		BreakCondition currentBreakCondition = breakCondition;
		ResumeGreedyCondition currentResumeGreedyCondition = resumeGreedyCondition;

		// check if we are an end node for the current face traversal
		EndNodeComputationResult endNodeComputationResult = computeEndNode(
				currentNode, lastIntersection, clockwise, faceChanged);
		if (endNodeComputationResult.isEndNode()) {
			RoutingStep routingStep = new RoutingStep(currentNode,
					lastIntersection, clockwise, faceChanged,
					endNodeComputationResult.isBreaked(),
					endNodeComputationResult.isResumeGreedy(),
					currentFinishCondition, currentResumeGreedyCondition,
					currentStartCondition, currentCrossingCondition,
					currentTurnCondition, currentBreakCondition);
			vector.add(routingStep);
			return;
		}

		// compute the next edge
		NextComputationResult result = computeNextRoutingStep(currentNode,
				previousPosition, lastIntersection, clockwise, faceChanged);

		boolean isBreaked = result.isBreaked();
		Edge edge = new Edge(currentNode.getAddress(), result.getNextNode().getAddress());
		//TODO Paket laeuft im Kreis und besucht eine schon einmal besuchte Kante
		if(visitedEdges.contains(edge)) {
			isBreaked = true;
		}
		visitedEdges.add(edge);
//		System.out.println(edge + " now in " + visitedEdges);

		
		RoutingStep step = new RoutingStep(currentNode, lastIntersection,
				clockwise, faceChanged, isBreaked,
				endNodeComputationResult.isResumeGreedy(),
				currentFinishCondition, currentResumeGreedyCondition,
				currentStartCondition, currentCrossingCondition,
				currentTurnCondition, currentBreakCondition);
		vector.add(step);

		// recursively compute the remaining
		if (!isBreaked) {
			computeRemainingRoutingSteps(result.getNextNode(), currentNode
					.getPosition(), result.getNextIntersection(), result
					.isClockwiseTraversal(), false, vector);
		}

	}

	// compute start of face traversal
	private StartComputationResult computeStart(PlanarGraphNode currentNode) {
		PlanarGraphNode ccwNode = getNextNode(currentNode, destinationNode.getPosition(), false);
		PlanarGraphNode cwNode = getNextNode(currentNode, ccwNode.getPosition(), true);
		startCondition = startCondition.nextEdge(currentNode, cwNode, ccwNode, destinationNode, neighbors);
		boolean clockwise = startCondition.isClockwise();
		if (clockwise) {
			return new StartComputationResult(ccwNode.getPosition(), clockwise);
		} else {
			return new StartComputationResult(cwNode.getPosition(), clockwise);
		}
	}
	private static class StartComputationResult {
		private Position previousPosition;
		private boolean clockwise;
		public StartComputationResult(Position previousPosition, boolean clockwise) {
			this.previousPosition = previousPosition;
			this.clockwise = clockwise;
		}
		public boolean isClockwise() {
			return clockwise;
		}
		public Position getPreviousPosition() {
			return previousPosition;
		}
	}
	
	// compute the next edge
	private NextComputationResult computeNextRoutingStep(
			PlanarGraphNode currentNode, Position previousPosition,
			Position lastIntersection, boolean clockwise, boolean faceChanged) {

		// get positions
		Position currentPosition = currentNode.getPosition();
		Position sourcePosition = sourceNode.getPosition();
		Position destinationPosition = destinationNode.getPosition();

		// restart planar graph traversal when the current node is located on the source destination line
		if(GeometryCalculations.checkIntersect(currentPosition, currentPosition, sourcePosition, destinationPosition) &&
				currentPosition.distance(destinationPosition) < lastIntersection.distance(destinationPosition)) {

			// determine face traversal direction and the position of the previous edge
			StartComputationResult startResult = computeStart(currentNode);
			previousPosition = startResult.getPreviousPosition();
			clockwise = startResult.isClockwise();
			lastIntersection = currentPosition;
			faceChanged = true;

		}
		
		// get next routing step
		PlanarGraphNode nextNode = getNextNode(currentNode, previousPosition,
				clockwise);
		Position nextPosition = nextNode.getPosition();

		// check if the edge from current to next is intersected by the line
		// connecting the last intersection and the destination node
		boolean newClockwise = clockwise;
		Position nextIntersection = calculateCloserRegularIntersection(sourcePosition,
				destinationPosition, currentPosition, nextPosition,
				lastIntersection);
		
		// the line is regularily intersected
		if (nextIntersection != null) {

			// we changed the face
			faceChanged = true;

			// determine the direction for traversing the next face when the
			// crossing edge is used
			int turn = GeometryCalculations.checkTurn(
					currentNode.getPosition(), nextNode.getPosition(),
					destinationNode.getPosition());
			if (turn == GeometryCalculations.CLOCKWISE_TURN) {
				newClockwise = true;
			} else if (turn == GeometryCalculations.COUNTERCLOCKWISE_TURN) {
				newClockwise = false;
			} else {
				throw new PlanarGraphExplorerException(
						"Case is not implemented at the moment.");
			}

			// determine the next edge using the crossing condition
			PlanarGraphNode reverseNode = getNextNode(currentNode, nextNode
					.getPosition(), !newClockwise);
			Position reversePosition = reverseNode.getPosition();
			crossingCondition = crossingCondition.nextEdge(sourceNode,
					destinationNode, neighbors, currentNode, nextNode,
					reverseNode);
			Position reverseIntersection = calculateCloserRegularIntersection(
					sourcePosition, destinationPosition, currentPosition,
					reversePosition, nextIntersection);
			while (!crossingCondition.isSatisfied()
					&& reverseIntersection != null) {
				nextNode = reverseNode;
				nextIntersection = reverseIntersection;
				reverseNode = getNextNode(currentNode, nextNode.getPosition(),
						!newClockwise);
				reversePosition = reverseNode.getPosition();
				crossingCondition = crossingCondition.nextEdge(sourceNode,
						destinationNode, neighbors, currentNode, nextNode,
						reverseNode);
				reverseIntersection = calculateCloserRegularIntersection(
						sourcePosition, destinationPosition, currentPosition,
						reversePosition, nextIntersection);
			}
			if (!crossingCondition.isSatisfied()) {
				nextNode = reverseNode;
				newClockwise = !newClockwise;
			}

		}
		
		// the line is not intersected
		else {
			nextIntersection = lastIntersection;
		}

		// check if we have to turn the direction
		turnCondition = turnCondition.nextEdge(sourceNode, destinationNode,
				currentNode, nextNode);
		while (turnCondition.isSatisfied()) {
			newClockwise = !newClockwise;
			nextNode = getNextNode(currentNode, nextPosition, newClockwise);
			nextPosition = nextNode.getPosition();
			turnCondition = turnCondition.nextEdge(sourceNode, destinationNode,
					currentNode, nextNode);
		}

		// make state transition for break condition
		breakCondition = breakCondition.nextState(currentNode, nextNode,
				faceChanged);

		// return the result
		return new NextComputationResult(newClockwise, faceChanged,
				breakCondition.isSatisfied(), nextIntersection, nextNode);

	}

	// the result of the computeNextRoutingStep method
	private static class NextComputationResult {
		private boolean clockwiseTraversal;
		private boolean faceChanged;
		private boolean breaked;
		private Position nextIntersection;
		private PlanarGraphNode nextNode;
		public NextComputationResult(boolean clockwiseTraversal,
				boolean faceChanged, boolean breaked,
				Position nextIntersection, PlanarGraphNode nextNode) {
			this.clockwiseTraversal = clockwiseTraversal;
			this.faceChanged = faceChanged;
			this.breaked = breaked;
			this.nextIntersection = nextIntersection;
			this.nextNode = nextNode;
		}
		public boolean isBreaked() {
			return breaked;
		}
		public boolean isClockwiseTraversal() {
			return clockwiseTraversal;
		}
		public boolean isFaceChanged() {
			return faceChanged;
		}
		public Position getNextIntersection() {
			return nextIntersection;
		}
		public PlanarGraphNode getNextNode() {
			return nextNode;
		}
	}

	// check if the current node is an end node regarding current face traversal.
	private EndNodeComputationResult computeEndNode(
			PlanarGraphNode currentNode, Position lastIntersection,
			boolean clockwise, boolean faceChanged) {

		// make condition state transitions
		finishCondition = finishCondition.nextNode(currentNode, neighbors);
		resumeGreedyCondition = resumeGreedyCondition.nextNode(sourceNode,
				destinationNode, greedyFailureNode, currentNode, neighbors);

		// return result
		boolean breaked = false;
		if(!currentNode.isStopNode() && !currentNode.hasAdjacentNodes()) {
			breaked = true;
		}
		return new EndNodeComputationResult(breaked, finishCondition
				.isSatisfied(), resumeGreedyCondition.isSatisfied(),
				currentNode.isStopNode());

	}

	// the result of the computeEndNode method
	private static class EndNodeComputationResult {
		private boolean finished;
		private boolean resumeGreedy;
		private boolean breaked;
		private boolean stopNode;
		public EndNodeComputationResult(boolean breaked, boolean finished,
				boolean resumeGreedy, boolean stopNode) {
			this.breaked = breaked;
			this.finished = finished;
			this.resumeGreedy = resumeGreedy;
			this.stopNode = stopNode;
		}
		public boolean isBreaked() {
			return breaked;
		}
		public boolean isFinished() {
			return finished;
		}
		public boolean isResumeGreedy() {
			return resumeGreedy;
		}
		public boolean isEndNode() {
			return finished || resumeGreedy || breaked || stopNode;
		}
	}

	// Calculate the intersection between the open line segements (source,destination)
	// and (current,next). The method returns the intersection if it exists and if it
	// is closer to the destination than the last intersection but not the destination itself.
	// In addition, only unique intersections are considered. When there is an
	// infinite number of possible intersections (due to parallel lines) the method
	// will return null.
	private Position calculateCloserRegularIntersection(Position source, Position destination, Position current,
			Position next, Position lastIntersection) {
		if (GeometryCalculations.checkIntersect(current, next, source, destination)) {
			Position intersection = GeometryCalculations.calculateIntersection(current, next, source, destination);
			if(intersection == null) {
				return null;
			}
			if(intersection.equals(current) || intersection.equals(next) ||
					intersection.equals(source) || intersection.equals(destination)) {
				return null;
			}
			if (lastIntersection.distance(destination) > intersection.distance(destination)) {
				return intersection;
			}
		}
		return null;
	}

	// Get the neighbor node coming next when starting in
	// clockwise/counterclockwise direction from the line connecting the
	// previous position and the position of the current node.
	private PlanarGraphNode getNextNode(PlanarGraphNode node,
			Position previousPosition, boolean clockwise) {
		PlanarGraphNode[] neighbors = node.getAdjacentNodes();


		Position current = node.getPosition();
		PlanarGraphNode png = null;
		for (int i = 0; i < neighbors.length; i++) {
			png = neighbors[i];
			if (!png.getPosition().equals(previousPosition)) {
				break;
			}
		}
		// bei clockwise läuft er counterclockwise und umgekehrt
		// negiere die Y Koordinate damit bei der Berechnung der richtige wert
		// herauskommt
		if (clockwise) {
			// System.err.println(neighbors.length);
			for (int i = 0; i < neighbors.length; i++) {
				/*
				 * if (neighbors[i].getPosition().equals(previousPosition)) {
				 * continue; }
				 */
				// System.err.println("in der For-schleife");
				if (isCWCloser(new Position(previousPosition.getX(),
						-previousPosition.getY()).sub(new Position(current
						.getX(), -current.getY())), new Position(neighbors[i]
						.getPosition().getX(), -neighbors[i].getPosition()
						.getY()).sub(new Position(current.getX(), -current
						.getY())), new Position(png.getPosition().getX(), -png
						.getPosition().getY()).sub(new Position(current.getX(),
						-current.getY())))) {

					// der vorher schon ausgewählte Knoten ist näher
					/*
					 * System.err.println("ändere Knoten");
					 * System.err.println(pngToString(neighbors[i]));
					 * System.err.println(pngToString(png));
					 */

					png = neighbors[i];
				} else {
					/*
					 * System.err.println("alles bleibt gleich");
					 * System.err.println(pngToString(png));
					 * System.err.println(pngToString(neighbors[i]));
					 */
				}
			}
		} else {
			for (int i = 0; i < neighbors.length; i++) {
				/*
				 * if (neighbors[i].getPosition().equals(previousPosition)) {
				 * continue; }
				 */
				// System.err.println("in der For-schleife");
				if (isCCWCloser(new Position(previousPosition.getX(),
						-previousPosition.getY()).sub(new Position(current
						.getX(), -current.getY())), new Position(neighbors[i]
						.getPosition().getX(), -neighbors[i].getPosition()
						.getY()).sub(new Position(current.getX(), -current
						.getY())), new Position(png.getPosition().getX(), -png
						.getPosition().getY()).sub(new Position(current.getX(),
						-current.getY())))) {
					// der vorher schon ausgewählte Knoten ist näher
					/*
					 * System.err.println("alles bleibt gleich");
					 * System.err.println(pngToString(png));
					 * System.err.println(pngToString(neighbors[i]));
					 */
					png = neighbors[i];
				} else {
					/*
					 * System.err.println("ändere Knoten");
					 * System.err.println(pngToString(neighbors[i]));
					 * System.err.println(pngToString(png));
					 */

				}
			}

		}
		return png;
	}

	// Check if vector a is closer to vector ref than vector b when starting at
	// vector ref and turning in the clockwise direction.
	private boolean isCWCloser(Position ref, Position a, Position b) {
		if (isRight(ref, b)) {
			return isRight(ref, a) && isLeft(b, a);
		}
		if (isLeft(ref, b)) {
			return isRight(ref, a) || isLeft(b, a);
		}
		if (isOpposite(ref, b)) {
			return isRight(ref, a);
		}
		if (isLeft(b, a) || isRight(b, a)) {
			return true;
		}
		if (isOpposite(a, b)) {
			return true;
		}
		return false;
	}

	// Check if vector a is closer to vector ref than vector b when starting at
	// vector ref and turning in the counterclockwise direction.
	private boolean isCCWCloser(Position ref, Position a, Position b) {
		if (isLeft(ref, b)) {
			return isLeft(ref, a) && isRight(b, a);
		}
		if (isRight(ref, b)) {
			return isLeft(ref, a) || isRight(b, a);
		}
		if (isOpposite(ref, b)) {
			return isLeft(ref, a);
		}
		if (isLeft(b, a) || isRight(b, a)) {
			return true;
		}
		if (isOpposite(a, b)) {
			return true;
		}
		return false;
	}

	// Check if two collinear vectors point in the opposite direction.
	private boolean isOpposite(Position a, Position b) {
		return ((a.getX() >= 0 && b.getX() <= 0) || (a.getX() <= 0 && b.getX() >= 0))
				&& ((a.getY() >= 0 && b.getY() <= 0) || (a.getY() <= 0 && b
						.getY() >= 0));
	}

	// Check if vector b is inside the left plane defined by vector a.
	private boolean isLeft(Position a, Position b) {
		return a.determinant2D(b) > 0;
	}

	// Check if vector b is inside the right plane defined by vector a.
	private boolean isRight(Position a, Position b) {
		return a.determinant2D(b) < 0;
	}
	
	private static class Edge {
		private Address start;
		private Address destination;
		private Edge(Address start, Address destination) {
			this.start = start;
			this.destination = destination;
		}
		public boolean equals(Object object) {
			Edge other = (Edge)object;
			return start.equals(other.start) && destination.equals(other.destination);
		}
		public int hashCode() {
			return start.hashCode() + destination.hashCode();
		}
		public String toString() {
			return start + "->" + destination;
		}
		
	}

}