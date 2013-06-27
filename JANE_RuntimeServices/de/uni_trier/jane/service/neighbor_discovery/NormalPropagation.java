package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;

/**
 * This implementation of PropagationStateInterface will always return true, so with every beacon the
 * NeighborDiscoveryData will be propagated
 * @author Stefan Peters
 *
 */
public class NormalPropagation implements PropagationStateInterface {

	public boolean propagateData(DataMap dataMap, Address[] neighbors) {
		return true;
	}

}
