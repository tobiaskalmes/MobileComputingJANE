/*****************************************************************************
 * 
 * DynamicGraph.java
 * 
 * $Id: DynamicGraph.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
import de.uni_trier.jane.simulation.device.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.dynamic.linkcalculator.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;


/**
 * This class represents a discretization of the unit graph resulting from a mobility source.
 * The class takes snapshots of the dynamic graph in sucessive steps of fixed interval length.
 * For all vertices connected during such an interval an edge is recorded for this step. 
 * If the interval length represents the time a packet needs to travel between two adjacent nodes,
 * this graph can be used to calculate the shortest path for a packet traveling from source to
 * destination in a dynamic unit graph.
 * @see de.uni_trier.jane.util.dynamic_dijkstra.DynamicDijkstraAlgorithm
 */

public class DynamicGraph {

	private final static String VERSION = "$Id: DynamicGraph.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private Set addressSet;
	private List graphStateList;

	private DynamicGraph(Set addressSet, List graphStateList) {
		this.addressSet = addressSet;
		this.graphStateList = graphStateList;
	}

	/**
	 * Get the all vertices occured during recording of this graph.
	 * @return an iterator over all vertice addresses
	 */
	public DeviceIDIterator getVertexDomain() {
		return new DeviceIDIterator() {
			private Iterator iterator = addressSet.iterator();
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
	 * Get the number of recorded graph steps
	 * @return the number of steps
	 */
	public int getGraphStateCount() {
		return graphStateList.size();
	}

	/**
	 * Get the ith recorded graph step
	 * @param i the step index
	 * @return the graph state
	 */
	public GraphState getGraphState(int i) {
		return (GraphState)graphStateList.get(i);
	}

	/**
	 * Create a new <code>DiscreteDynamicGraph</code> object using the given mobility source.
	 * @param mobilitySource the mobility source
	 * @param startTime the time for the first interval
	 * @param stepCount the number of snapshots
	 * @param stepInterval the time interval between two sucessive snapshots
	 * @return the graph representing the discretization of th egiven mobility source
	 */
	public static DynamicGraph createFromMobilitySource(MobilitySource mobilitySource, double startTime, int stepCount, double stepInterval) {
		DiscreteDynamicGraphBuilder builder = new DiscreteDynamicGraphBuilder(mobilitySource, startTime, stepCount, stepInterval);
		return builder.buildDiscreteDynamicGraph();
	}

	/**
	 * This class represents the graph for two sucessive snapshot steps at time t1 and t2.
	 * The vertice set is recorded at time t1. The edges have to last at least for the interval
	 * (t1,t2).
	 */
	public static class GraphState {
		private Map addressNodeMap;
		private GraphState(Map addressNodeMap) {
			this.addressNodeMap = addressNodeMap;
		}
		/**
		 * Get all vertices in this snapshot.
		 * @return an iterator over all vertice addresses
		 */
		public DeviceIDIterator getAddressIterator() {
			return new DeviceIDIterator() {
				private Iterator iterator = addressNodeMap.keySet().iterator();
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
		 * Get all information about the vertice with the given address.
		 * @param address the vertice address
		 * @return the information about the vertice
		 */
		public Node getNode(DeviceID address) {
			return (Node)addressNodeMap.get(address);
		}
		/**
		 * This class represents all information about a vertice for the current recording step.
		 */
		public static class Node {
			private DeviceID address;
			private List edgeList;
			private Node(DeviceID address, List edgeList) {
				this.address = address;
				this.edgeList = edgeList;
			}
			/**
			 * Get the number of outgoing edges.
			 * @return the number of edges
			 */
			public int getEdgeCount() {
				return edgeList.size();
			}
			/**
			 * Get the ith edge.
			 * @param i the index
			 * @return the edge
			 */
			public Edge getEdge(int i) {
				return (Edge)edgeList.get(i);
			}
			/**
			 * This class represents an edge lasting for the interval between two sucessive recording steps.
			 */
			public static class Edge {
				private DeviceID source;
				private DeviceID destination;
				private double distance;
				private Edge(DeviceID source, DeviceID destination, double distance) {
					this.source = source;
					this.destination = destination;
					this.distance = distance;
				}
				/**
				 * Get the address of the source vertice
				 * @return the address
				 */
				public DeviceID getSource() {
					return source;
				}
				/**
				 * Get the address of the destination vertice
				 * @return the address
				 */
				public DeviceID getDestination() {
					return destination;
				}
				/**
				 * get the distance between source and destination vertice. Let t1 and t2 be the time of two
				 * sucessive recording steps. Let ps(t) and pd(t) the position of source and destination vertice at
				 * time t. The distance d is calculated as follows: d = |ps(t1)-pd(t2)|.
				 * @return the distance
				 */
				public double getDistance() {
					return distance;
				}
			}
		}
	}

	private static class DiscreteDynamicGraphBuilder implements DynamicInterpreter, DynamicScheduler, DynamicEventListener {
		private LinkCalculator dynamicSource;
		private double startTime;
		private int stepCount;
		private double stepInterval;
		private EventSet eventSet;
		private Map addressNodeMap;
		private boolean recordingFinished;
		private DynamicGraph discreteDynamicGraph;
		private Set addressSet;
		private List graphStateList;
		private Map addressLastPositionMap;
		public DiscreteDynamicGraphBuilder(MobilitySource mobilitySource, double startTime, int stepCount, double stepInterval) {
			dynamicSource = new LinkCalculator(null);
			dynamicSource.start(mobilitySource);
			this.startTime = startTime;
			this.stepCount = stepCount;
			this.stepInterval = stepInterval;
			eventSet = new CascadeEventSet(10, 25);
			addressNodeMap = new HashMap();
		}
		public DynamicGraph buildDiscreteDynamicGraph() {
			eventSet.add(new StartRecordingEvent(startTime));
			for(int i=1; i<stepCount; i++) {
				eventSet.add(new TakeSnapshotEvent(startTime + (i*stepInterval)));
			}
			eventSet.add(new StopRecordingEvent(startTime + (stepCount*stepInterval)));
			scheduleNextEvent();
			recordingFinished = false;
			while(!recordingFinished && eventSet.hasNext()) {
				eventSet.handleNext();//next().handle();
			}
			return discreteDynamicGraph;
		}
		public void enter(double time, DeviceID address, TrajectoryMapping trajectoryMapping,boolean suspended, DoubleMapping sendingRadius) {
			eventSet.add(new EnterEvent(time, this, this, address, trajectoryMapping,suspended, sendingRadius));
		}
		public void exit(double time, DeviceID address) {
			eventSet.add(new ExitEvent(time, this, this, address));
		}
		public void attach(double time, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
			eventSet.add(new AttachEvent(time, this, sender, receiver, linkReliability, this));
		}
		public void detach(double time, DeviceID sender, DeviceID receiver) {
			eventSet.add(new DetachEvent(time, this, sender, receiver, this));
		}
		public void setTrack(double time, DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended) {
			eventSet.add(new SetTrackEvent(time, trajectoryMapping,suspended, address, this, this));
		}
		public void setLinkReliability(double time, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
			eventSet.add(new SetLinkReliabilityEvent(time, linkReliability, sender, receiver, this, this));
		}
		public void setSendingRadius(double time, DeviceID address, DoubleMapping sendingRadius) {
			eventSet.add(new SetSendingRadiusEvent(time, sendingRadius, address, this, this));
		}
		public void initialize(DynamicInterpreter dynamicInterpreter, DynamicSource dynamicSource) {
			// ignored
		}
		public void scheduleNextEvent() {
			if(dynamicSource.hasNext()) {
				dynamicSource.next().execute(this);
			}
		}
		public void handleEnter(DeviceID address, TrajectoryMapping trajectoryMapping,boolean suspended, DoubleMapping sendingRadius) {
			addressNodeMap.put(address, new Node(trajectoryMapping, eventSet.getTime()));
		}
		public void handleExit(DeviceID address) {
			addressNodeMap.remove(address);
		}
		public void handleAttach(DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
			((Node)addressNodeMap.get(sender)).attach(receiver, eventSet.getTime());
		}
		public void handleDetach(DeviceID sender, DeviceID receiver) {
			((Node)addressNodeMap.get(sender)).detach(receiver, eventSet.getTime());
		}
		public void handleChangeTrack(DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended) {
			((Node)addressNodeMap.get(address)).changeTrack(trajectoryMapping);
		}
		public void handleChangeSendingRadius(DeviceID address, DoubleMapping sendingRadius) {
			// ignored
		}
		public void handleChangeLinkReliability(DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
			// ignored
		}
		private void startRecording() {
			addressSet = new HashSet();
			graphStateList = new ArrayList();
			addressLastPositionMap = new HashMap();
			recordCurrentPositions();
		}
		private void takeSnapshot() {
			createGraphSnapshot();
			recordCurrentPositions();
		}
		private void stopRecording() {
			createGraphSnapshot();
			discreteDynamicGraph = new DynamicGraph(addressSet, graphStateList);
			graphStateList = null;
			addressLastPositionMap = null;
			recordingFinished = true;
		}	
		private void recordCurrentPositions() {
			double currentTime = eventSet.getTime();
			addressLastPositionMap.clear();
			Iterator it = addressNodeMap.keySet().iterator();
			while (it.hasNext()) {
				DeviceID address = (DeviceID) it.next();
				addressSet.add(address);
				Node node = (Node)addressNodeMap.get(address);
				addressLastPositionMap.put(address, node.getPosition(currentTime));
			}
		}
		private void createGraphSnapshot() {
			Map addressNodeMap2 = new HashMap();
			double currentTime = eventSet.getTime();
			Iterator it = addressLastPositionMap.keySet().iterator();
			while (it.hasNext()) {
				DeviceID address = (DeviceID) it.next();
				Position position = (Position)addressLastPositionMap.get(address);
				Node node = (Node)addressNodeMap.get(address);
				if(node != null) {
					if(node.getCreationTime() <= currentTime - stepInterval) {
						DeviceID[] neighborAddresses = node.getConnected(currentTime - stepInterval, currentTime);
						List neighborList = new ArrayList();
						for(int i=0; i<neighborAddresses.length; i++) {
							DeviceID neighborAddress = neighborAddresses[i];
							double distance = ((Node)addressNodeMap.get(neighborAddress)).getPosition(currentTime).distance(position);
							neighborList.add(new DynamicGraph.GraphState.Node.Edge(address, neighborAddress, distance));
						}
						addressNodeMap2.put(address, new DynamicGraph.GraphState.Node(address, neighborList));
					}
				}
			}
			graphStateList.add(new DynamicGraph.GraphState(addressNodeMap2));
		}
		private class StartRecordingEvent extends Event {
			public StartRecordingEvent(double time) {
				super(time);
			}
			protected void handleInternal() {
				startRecording();
			}
		}
		private class TakeSnapshotEvent extends Event {
			public TakeSnapshotEvent(double time) {
				super(time);
			}
			protected void handleInternal() {
				takeSnapshot();
			}
		}
		private class StopRecordingEvent extends Event {
			public StopRecordingEvent(double time) {
				super(time);
			}
			protected void handleInternal() {
				stopRecording();
			}
		}
		private static class Node {
			private TrajectoryMapping trajectoryMapping;
			private double creationTime;
			private Map addressLinkMap;
			public Node(TrajectoryMapping trajectoryMapping, double creationTime) {
				this.trajectoryMapping = trajectoryMapping;
				this.creationTime = creationTime;
				addressLinkMap = new HashMap();
			}
			public void attach(DeviceID receiver, double time) {
				addressLinkMap.put(receiver, new Link(time));
			}
			public void detach(DeviceID receiver, double time) {
				Link link = (Link)addressLinkMap.get(receiver);
				link.finish(time);
			}
			public void changeTrack(TrajectoryMapping trajectoryMapping) {
				this.trajectoryMapping = trajectoryMapping;
			}
			public Position getPosition(double time) {
				return trajectoryMapping.getValue(time).getPosition();
			}
			public double getCreationTime() {
				return creationTime;
			}
			public DeviceID[] getConnected(double stepStart, double stepStop) {
				List result = new ArrayList();
				Iterator it = addressLinkMap.keySet().iterator();
				while(it.hasNext()) {
					DeviceID address = (DeviceID)it.next();
					Link link = (Link)(addressLinkMap.get(address));
					if(link.isOn(stepStart, stepStop)) {
						result.add(address);
					}
				}
				return (DeviceID[])result.toArray(new DeviceID[result.size()]);
			}
			private static class Link {
				private double attachTime;
				private double detachTime;
				public Link(double attachTime) {
					this.attachTime = attachTime;
					detachTime = Double.POSITIVE_INFINITY;
				}
				public boolean isOn(double startTime, double endTime) {
					return attachTime <= startTime && endTime <= detachTime;
				}
				public void finish(double detachTime) {
					this.detachTime = detachTime;
				}
			}
		}
	}

}
