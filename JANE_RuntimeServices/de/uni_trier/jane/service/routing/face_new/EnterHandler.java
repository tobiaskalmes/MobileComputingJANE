package de.uni_trier.jane.service.routing.face_new;

import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.planarizer.*;

/**
 * This interface is used to describe the behaviour of face routing with dominating set, with start node
 * not in dominating set.
 * @author Stefan Peters
 *
 */
public interface EnterHandler {
	
	/**
	 * This method decides which node is next routing step if current node is not a dominating set node
	 * @param current The current node
	 * @param datas Its neighbor data
	 * @return Returns the next routing step
	 */
	public NetworkNode handleEnter(NetworkNode previousNode, NetworkNode current, NeighborDiscoveryData[] datas, NetworkNode destination);
}
