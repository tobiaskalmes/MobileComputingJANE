package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;

/**
 * This interface is used to describe the behaviour of face routing with dominating set, with destination
 * as one hop neighbor
 * @author Stefan Peters
 *
 */
public interface EndHandler {
	
	/**
	 * This method decides which neighbor is chosen as next routing hop, if destination is one of current nodes one hop neighbors
	 * @param current The current Node
	 * @param destination The destination node
	 * @param datas The neighbor discovery data
	 * @return Returns the chosen next networkNode
	 */
	public NetworkNode handleEnd(NetworkNode previousNode,NetworkNode current, NetworkNode destination,NeighborDiscoveryData[] datas);
}
