package de.uni_trier.jane.service.routing.dijkstra;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.util.dijkstra.*;

/**
 * This class provides the link between the dijkstra algorithm inplementation
 * and the global knowledge graph provided by the simulation.
 */
public class GlobalKnowledgeGraph implements Graph {

	private WeightFunction weightFunction;
	private Map nodeIndexMap;
	private List nodeList;
	private List neighborList;
	private GlobalKnowledge globalKnowledge;

	/**
	 * Construct a new global knowledge graph object.
	 * @param weightFunction the weight function used to determine the edge weights
	 */
	public GlobalKnowledgeGraph(WeightFunction weightFunction) {
		this.weightFunction = weightFunction;
		nodeIndexMap = new HashMap();
		nodeList = new ArrayList();
		neighborList = new ArrayList();
	}
	
	/**
	 * Initialize this data structure by providing the global known network graph.
	 * @param globalKnowledge the network graph provided by the simulation
	 */
	public void start(GlobalKnowledge globalKnowledge) {
		this.globalKnowledge = globalKnowledge;
	}
	
	/**
	 * The global network graph may change over the time. Thus, each time the dijkstra
	 * algorithm is applied on this structure, the information stored in this data
	 * structure has to be updated by calling this method.
	 */
	public void refresh() {
		nodeIndexMap.clear();
		nodeList.clear();
		DeviceIDIterator addressIterator = globalKnowledge.getNodes().iterator();
		int i = 0;
		while(addressIterator.hasNext()) {
			DeviceID node = addressIterator.next();
			Integer index = new Integer(i);
			nodeIndexMap.put(node, index);
			nodeList.add(node);
			i++;
		}
		for(i=0; i<nodeList.size(); i++) {
			DeviceID node = (DeviceID)nodeList.get(i);
			addressIterator = globalKnowledge.getConnected(node);
			if(neighborList.size() <= i) {
				neighborList.add(i, new ArrayList());
			}
			List list = (List)neighborList.get(i);
			list.clear();
			while(addressIterator.hasNext()) {
				DeviceID dest = addressIterator.next();
				Integer index = (Integer)nodeIndexMap.get(dest);
				list.add(index);
			}
		}
	}

	/**
	 * Get the node address assigned to this node index.
	 * @param index the node index
	 * @return the address
	 */
	public DeviceID getAddress(int index) {
		return (DeviceID)nodeList.get(index);
	}

	/**
	 * Get the node index assigned to this node address.
	 * @param address the node address
	 * @return the node index in this data structure
	 */
	public int getNode(DeviceID address) {
		Integer index = (Integer)nodeIndexMap.get(address);
		return index.intValue();
	}

	public int getNodeCount() {
		return nodeList.size();
	}

	public int getNode(int number) {
		return number;
	}

	public int getNeighborCount(int node) {
		List list = (List)neighborList.get(node);
		return list.size();
	}

	public int getNeighbor(int node, int number) {
		List list = (List)neighborList.get(node);
		Integer index = (Integer)list.get(number);
		return index.intValue();
	}

	public double getWeight(int source, int destination) {
		DeviceID sourceAddress = (DeviceID)nodeList.get(source);
		DeviceID destinationAddress = (DeviceID)nodeList.get(destination);
		return weightFunction.getWeight(sourceAddress, destinationAddress, globalKnowledge);
	}

}
