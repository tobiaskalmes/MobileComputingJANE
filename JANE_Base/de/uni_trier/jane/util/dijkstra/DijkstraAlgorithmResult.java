/*****************************************************************************
 * 
 * DijkstraAlgorithmResult.java
 * 
 * $Id: DijkstraAlgorithmResult.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.util.dijkstra;

import java.util.*;

/**
 * An instance of this class represents the result of Dijkstra's single source shortest path algorithm.
 */
public class DijkstraAlgorithmResult {

	private Map addressEstimatMap;
	private Map predecessorMap;
	private int source;

	/**
	 * Construct an instance of the algorithm result.
	 * @param addressEstimatMap a mapping from addresses to estimates calculated by the algorithm
	 * @param predecessorMap a mapping from addresses to their predecessor along the shortest path
	 * @param source the source used to calculate the shortest paths to all destinations
	 */
	DijkstraAlgorithmResult(Map addressEstimatMap, Map predecessorMap, int source) {
		this.addressEstimatMap = addressEstimatMap;
		this.predecessorMap = predecessorMap;
		this.source = source;
	}

	/**
	 * Get the number of all nodes of the graph
	 * @return the number of nodes
	 */
	public int getNodeCount() {
		return addressEstimatMap.keySet().size();
	}

	/**
	 * Get the id of the ith node.
	 * @param number the index i
	 * @return the node id
	 */
	public int getNode(int number) {
		Iterator it = addressEstimatMap.keySet().iterator();
		for(int i=0; i<number; i++) {
			it.next();
		}
		return ((Integer)it.next()).intValue();
	}

	/**
	 * Get the minimum path weight to the destination.
	 * @param desintation the desintaion id
	 * @return the minumum path weight (positive inifinity if there is no path)
	 */
	public double getMinimumPathWeigth(int desintation) {
		return ((Estimate)addressEstimatMap.get(new Integer(desintation))).getWeight();
	}

	/**
	 * Get the path from source to desintation
	 * @param destination the destination id
	 * @return the path or null if source an destination are not connected
	 */
	public Path getPath(int destination) {
		if(getMinimumPathWeigth(destination) == Double.POSITIVE_INFINITY) {
			return null;
		}
		return new Path(predecessorMap, source, destination);
	}

}