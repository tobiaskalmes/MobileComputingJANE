/*****************************************************************************
 * 
 * DynamicDijkstraAlgorithm.java
 * 
 * $Id: DynamicDijkstraAlgorithm.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.util.dynamic_dijkstra;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.util.dynamic_dijkstra.DynamicGraph.GraphState.*;
import de.uni_trier.jane.util.dynamic_dijkstra.DynamicGraph.GraphState.Node.*;


/**
 * This class is an implementation of Dijkstra's single source shortest paths algorithm for
 * arbitrary dynamically changing graphs. The input has to be a discrete dynamic graph.
 * @see de.uni_trier.jane.util.dynamic_dijkstra.DynamicGraph
 */
public class DynamicDijkstraAlgorithm {

	private final static String VERSION = "$Id: DynamicDijkstraAlgorithm.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";
	
	private DynamicGraph discreteDynamicGraph;
	private WeightFunction weightFunction;

	private Map addressEstimateMap;
	private SortedSet estimateSet;

	/**
	 * Construct a new <code>DijkstraAlgorithm</code> object.
	 * @param discreteDynamicGraph the discrete dynamic graph
	 * @param weightFunction the weight function used to calculate the edge weights
	 */
	public DynamicDijkstraAlgorithm(DynamicGraph discreteDynamicGraph, WeightFunction weightFunction) {
		this.discreteDynamicGraph = discreteDynamicGraph;
		this.weightFunction = weightFunction;
	}

	/**
	 * Solve the shortest path problem for the given source
	 * @param source the source
	 * @return the result
	 */
	public DijkstraAlgorithmResult solve(DeviceID source) {
		initializeSingleSource(source);
		while(!isEmpty()) {
			Estimate estimate = extractMin();
			Node node = discreteDynamicGraph.getGraphState(estimate.getStep()).getNode(estimate.getAddress());
			for(int i=0; i<node.getEdgeCount(); i++) {
				relax(node.getEdge(i));
			}
		}
		return new DijkstraAlgorithmResult(addressEstimateMap);
	}
	
	/**
	 * This class represents the result of the dijkstra algorithm for a single source. 
	 */
	public static class DijkstraAlgorithmResult {

		protected Map addressEstimatMap;

		protected DijkstraAlgorithmResult(Map addressEstimatMap) {
			this.addressEstimatMap = addressEstimatMap;
		}

		/**
		 * Get an address iterator for all destination vertices.
		 * @return the iterator
		 */
		public DeviceIDIterator getDestinationDomain() {
			return new DeviceIDIterator() {
				private Iterator iterator = addressEstimatMap.keySet().iterator();
				public boolean hasNext() {
					return iterator.hasNext();
				}
				public DeviceID next() {
					return (DeviceID)iterator.next();
				}
				public void remove() {
					throw new IllegalAccessError("Remove not supported");
					
				}
			};
		}
		
		/**
		 * Get the minimum Path weigth for the given destination
		 * @param desintation the desintation
		 * @return the weight
		 */
		public double getMinimumPathWeigth(DeviceID desintation) {
			return ((Estimate)addressEstimatMap.get(desintation)).getWeight();
		}

	}

	/**
	 * Implement this interface to define the weight function for edges used by the dijkstra algorithm.
	 */
	public interface WeightFunction {

		/**
		 * Get the weight for the given edge
		 * @param edge the edge
		 * @return the weight
		 */
		public double getWeight(Edge edge);

	}

	private void initializeSingleSource(DeviceID source) {
		addressEstimateMap = new HashMap();
		estimateSet = new TreeSet();
		DeviceIDIterator it = discreteDynamicGraph.getVertexDomain();
		while(it.hasNext()) {
			DeviceID address = it.next();
			if(!address.equals(source)) {
				Estimate estimate = new Estimate(Double.POSITIVE_INFINITY, 0, address);
				addressEstimateMap.put(address, estimate);
				estimateSet.add(estimate);
			}
		}
		Estimate estimate = new Estimate(0, 0, source);
		addressEstimateMap.put(source, estimate);
		estimateSet.add(estimate);
	}

	private void relax(Edge edge) {
		DeviceID source = edge.getSource();
		DeviceID destination = edge.getDestination();
		double weight = weightFunction.getWeight(edge);
		Estimate sourceEstimate = (Estimate)addressEstimateMap.get(source);
		Estimate destinationEstimate = (Estimate)addressEstimateMap.get(destination);
		double newWeight = sourceEstimate.getWeight() + weight;
		if(destinationEstimate.getWeight() > newWeight) {
			Estimate newEstimate = new Estimate(newWeight, sourceEstimate.getStep()+1, destination);
			estimateSet.remove(destinationEstimate);
			estimateSet.add(newEstimate);
			addressEstimateMap.put(destination, newEstimate);
		}
	}

	private Estimate extractMin() {
		Estimate estimate = (Estimate)estimateSet.first();
		estimateSet.remove(estimate);
		return estimate;
	}

	private boolean isEmpty() {
		return estimateSet.isEmpty();
	}
	
	private static class Estimate implements Comparable {
		private double weight;
		private int step;
		private DeviceID address;
		public Estimate(double weight, int step, DeviceID address) {
			this.weight = weight;
			this.step = step;
			this.address = address;
		}
		public double getWeight() {
			return weight;
		}
		public int getStep() {
			return step;
		}
		public DeviceID getAddress() {
			return address;
		}
		public int compareTo(Object object) {
			Estimate other = (Estimate)object;
			if(weight < other.weight) {
				return -1;
			}
			else if(weight > other.weight) {
				return 1;
			}
			else {
				if(step < other.step) {
					return -1;
				}
				else if(step > other.step) {
					return 1;
				}
				else {
					return address.compareTo(other.address);
				}
			}
		}
		public int hashCode() {
			long bits = Double.doubleToLongBits(weight);
			int a = (int)(bits ^ (bits >>> 32));
			int b = step;
			int c = address.hashCode();
			return a ^ b ^ c;
		}
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}
			else if (object == null) {
				return false;
			}
			else if (object.getClass() != getClass()) {
				return false;
			}
			else {
				Estimate other = (Estimate)object;
				return weight == other.weight && step == other.step && address.equals(other.address);
			}
		}
	}

}
