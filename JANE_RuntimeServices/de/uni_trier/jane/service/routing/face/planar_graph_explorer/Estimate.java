/*
 * @author Stefan Peters
 * Created on 31.03.2005
 */
package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import de.uni_trier.jane.basetypes.Position;

/**
 * This interface describes the calculation of an estimate., e.g. an estimate for
 * appreciating the energy demand to destination 
 * @author Stefan Peters
 */
public interface Estimate {

	/**
	 * Calculates the appreciated energy demand from neighbor to destination
	 * @param current The current node
	 * @param destination The destination node
	 * @param neighbor The actuale neighbor
	 * @return the appreciated energy demand to destination
	 */
	public double calculateEstimate(Position current, Position destination, Position neighbor);
}
