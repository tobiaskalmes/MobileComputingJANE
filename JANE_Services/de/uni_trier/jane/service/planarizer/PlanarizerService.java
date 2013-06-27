/*
 * @author Stefan Peters
 * Created on 09.05.2005
 */
package de.uni_trier.jane.service.planarizer;


/**
 * @author Stefan Peters
 */
public interface PlanarizerService {
	
	/**
	 * @return Returns a PlanarGraphNode 
	 */
	public PlanarGraphNode getPlanarGraphNode();
	
	/**
	 * Removes one Neighbor from neighborlist
	 * @param address The neighbor to remove
	 */
	//public void removeNeighbor(Address address);
	
}
