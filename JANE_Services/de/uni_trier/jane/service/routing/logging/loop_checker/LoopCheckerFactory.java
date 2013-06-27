package de.uni_trier.jane.service.routing.logging.loop_checker;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public interface LoopCheckerFactory {

	// TODO Diese Option word überflüssig, wenn in der Datenstruktur ServiceObjectEnumeration
	// direkt codiert ist, dass man auch no service auswählen kann.
	public static final ServiceObjectElement NO_SERVICE = new ServiceObjectElement("none") {
		public Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			return null;
		}
	};

	public static final ServiceObjectEnumeration LOOP_CHECKER_FACTORY = new ServiceObjectEnumeration(
			"loopChecker", 0, new ServiceObjectElement[] {
					NO_SERVICE,
					DuplicateNodeLoopChecker.DUPLICATE_NODE_LOOP_CHECKER_FACTORY,
					DuplicateEdgeLoopChecker.DUPLICATE_EDGE_LOOP_CHECKER_FACTORY,
					DuplicatePathLoopChecker.DUPLICATE_NODE_SEQUENCE_LOOP_CHECKER_FACTORY,
					DuplicatePathLoopChecker.DUPLICATE_EDGE_SEQUENCE_LOOP_CHECKER_FACTORY });

	
	public LoopChecker getLoopChecker();
	
}
