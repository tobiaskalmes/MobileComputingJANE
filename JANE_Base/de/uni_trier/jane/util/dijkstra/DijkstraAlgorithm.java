/*****************************************************************************
 * 
 * DijkstraAlgorithm.java
 * 
 * $Id: DijkstraAlgorithm.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This is an implementation of Dijkstra's single source shortest path algorithm.
 * (see <i>Introduction to Algorithms. Thomas H. Cormen, Charles E. Leiserson. Ronald L. Rivest.
 * MIT Press. pages 527-532</i>).

 */
public class DijkstraAlgorithm {
	
	private final static String VERSION = "$Id: DijkstraAlgorithm.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";
	
	private Graph graph;
	private Map addressEstimateMap;
	private Map predecessorMap;
	private SortedSet estimateSet;

	/**
	 * Construct an instance of the algorithm.
	 * @param graph the graph to be analyzed by the algorithm
	 */
	public DijkstraAlgorithm(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Solve the shortest path problem for the given source node.
	 * @param source the source node
	 * @return the algorithm result
	 */
	public DijkstraAlgorithmResult solve(int source) {
		initializeSingleSource(source);
		while (!isEmpty()) {
			Estimate estimate = extractMin();
			int node = estimate.getAddress();
			int neighborCount = graph.getNeighborCount(node);
			for (int i = 0; i < neighborCount; i++) {
				relax(node, graph.getNeighbor(node, i));
			}
		}
		return new DijkstraAlgorithmResult(addressEstimateMap, predecessorMap, source);
	}

	private void initializeSingleSource(int source) {
		addressEstimateMap = new HashMap();
		predecessorMap = new HashMap();
		estimateSet = new TreeSet();
		int nodeCount = graph.getNodeCount();
		for (int i = 0; i < nodeCount; i++) {
			int address = graph.getNode(i);
			if (address != source) {
				Estimate estimate = new Estimate(Double.POSITIVE_INFINITY, address);
				addressEstimateMap.put(new Integer(address), estimate);
				estimateSet.add(estimate);
			}
		}
		Estimate estimate = new Estimate(0, source);
		addressEstimateMap.put(new Integer(source), estimate);
		estimateSet.add(estimate);
	}

	private void relax(int u, int v) {
		int source = u;
		int destination = v;
		double weight = graph.getWeight(u, v);
		Estimate sourceEstimate = (Estimate) addressEstimateMap.get(new Integer(source));
		Estimate destinationEstimate = (Estimate) addressEstimateMap.get(new Integer(destination));
		double newWeight = sourceEstimate.getWeight() + weight;
		if (destinationEstimate.getWeight() > newWeight) {
			Estimate newEstimate = new Estimate(newWeight, destination);
			estimateSet.remove(destinationEstimate);
			estimateSet.add(newEstimate);
			addressEstimateMap.put(new Integer(destination), newEstimate);
			predecessorMap.put(new Integer(destination), new Integer(source));
		}
	}

	private Estimate extractMin() {
		Estimate estimate = (Estimate) estimateSet.first();
		estimateSet.remove(estimate);
		return estimate;
	}

	private boolean isEmpty() {
		return estimateSet.isEmpty();
	}

}
