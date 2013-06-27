package de.uni_trier.jane.service.planarizer.gg;

import de.uni_trier.jane.service.planarizer.*;

/**
 * This interface describes all methods a planar graph constructor has to implement to create a
 * local view of on a planar graph.
 * 
 * @author Stefan Peters
 *
 */
public interface Planarizer {

	/**
	 * Construct the local view on the planar graph.
	 * @param u the current node
	 * @param nu the current node's neighbors
	 * @return the current node's adjacent edges of the planar subgraph
	 */
	public abstract NetworkNode[] stdPlanarizer(NetworkNode u, NetworkNode[] nu);

}