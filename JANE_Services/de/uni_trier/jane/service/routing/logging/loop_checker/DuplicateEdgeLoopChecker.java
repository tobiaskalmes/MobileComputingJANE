package de.uni_trier.jane.service.routing.logging.loop_checker;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public class DuplicateEdgeLoopChecker implements LoopChecker {

	public static LoopCheckerFactory FACTORY = new LoopCheckerFactory() {
		public LoopChecker getLoopChecker() {
			return new DuplicateEdgeLoopChecker();
		}
	};

	public static final ServiceObjectElement DUPLICATE_EDGE_LOOP_CHECKER_FACTORY = new ServiceObjectElement("duplicateEdge") {
		public Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			return FACTORY;
		}
	};
	

	private int hopCount;
	private int loopLength;
	private Address lastNode;
	private Map visited;

	public DuplicateEdgeLoopChecker() {
		hopCount = 0;
		loopLength = -1;
		lastNode = null;
		visited = new HashMap();
	}

	public void reset(Address node) {
		hopCount = 0;
		loopLength = -1;
		lastNode = node;
		visited.clear();
		visited.put(node, new Integer(hopCount));
	}

	public void addNode(Address node) {
		hopCount++;
		Edge edge = new Edge(lastNode, node);
		lastNode = node;
		if(visited.containsKey(edge)) {
			Integer oldHopCount = (Integer)visited.get(edge);
			loopLength = hopCount - oldHopCount.intValue();
		}
		visited.put(edge, new Integer(hopCount));
	}

	public boolean checkForLoop() {
		return loopLength >= 0;
	}

	public int getLoopLength() {
		return loopLength;
	}

	private static class Edge {
		private Address sender;
		private Address receiver;
		public Edge(Address sender, Address receiver) {
			this.sender = sender;
			this.receiver = receiver;
		}
		// TODO equals und hashCode richtig implementieren
		public boolean equals(Object obj) {
			Edge edge = (Edge)obj;
			return sender.equals(edge.sender) && receiver.equals(edge.receiver);
		}
		// TODO equals und hashCode richtig implementieren
		public int hashCode() {
			return sender.hashCode() + receiver.hashCode();
		}
		
	}

}
