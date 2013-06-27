package de.uni_trier.jane.service.routing.logging.loop_checker;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public class DuplicateNodeLoopChecker implements LoopChecker {

	public static LoopCheckerFactory FACTORY = new LoopCheckerFactory() {
		public LoopChecker getLoopChecker() {
			return new DuplicateNodeLoopChecker();
		}
	};
	
	public static final ServiceObjectElement DUPLICATE_NODE_LOOP_CHECKER_FACTORY = new ServiceObjectElement("duplicateNode") {
		public Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			return FACTORY;
		}
	};

	
	private int hopCount;
	private int loopLength;
	private Map visited;

	public DuplicateNodeLoopChecker() {
		hopCount = 0;
		loopLength = -1;
		visited = new HashMap();
	}

	public void reset(Address node) {
		hopCount = 0;
		loopLength = -1;
		visited.clear();
		visited.put(node, new Integer(hopCount));
	}

	public void addNode(Address node) {
		hopCount++;
		if(visited.containsKey(node)) {
			Integer oldHopCount = (Integer)visited.get(node);
			loopLength = hopCount - oldHopCount.intValue();
		}
		visited.put(node, new Integer(hopCount));
	}

	public boolean checkForLoop() {
		return loopLength >= 0;
	}

	public int getLoopLength() {
		return loopLength;
	}

}
