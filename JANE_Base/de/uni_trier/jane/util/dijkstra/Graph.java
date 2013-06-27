/*****************************************************************************
 * 
 * Graph.java
 * 
 * $Id: Graph.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

/**
 * This interface describes a generic graph based in node ids.
 * @see de.uni_trier.jane.util.dijkstra.DijkstraAlgorithm
 */
public interface Graph {

	/**
	 * Get the node count of the graph
	 * @return the node count
	 */
	public int getNodeCount();

	/**
	 * get the id ot the ith node.
	 * @param number the node index i
	 * @return the node id
	 */
	public int getNode(int number);

	/**
	 * get the number of neighbors of a node
	 * @param node the node id
	 * @return the number of neighbors
	 */
	public int getNeighborCount(int node);

	/**
	 * Get the ith neighbor of a node
	 * @param node the node id
	 * @param number the index i
	 * @return the node id of the neighbor
	 */
	public int getNeighbor(int node, int number);

	/**
	 * Get the weight of the edge from source to destination
	 * @param source the node id of the source
	 * @param destination the node id of the desitnation
	 * @return the weight
	 */
	public double getWeight(int source, int destination);

}
