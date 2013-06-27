/*
 * Created on 22.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.routing.greedy;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ForwardingMetric {

	public double calculate(NeighborDiscoveryData currentNode, NeighborDiscoveryData neighborNode, Position destinationPosition);
	
}
