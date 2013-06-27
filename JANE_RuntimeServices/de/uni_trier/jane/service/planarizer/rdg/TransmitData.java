/*
 * @author Stefan Peters
 * Created on 20.06.2005
 */
package de.uni_trier.jane.service.planarizer.rdg;


import java.util.*;

import de.uni_trier.jane.service.planarizer.*;


/**
 * @author Stefan Peters
 */
public class TransmitData {//implements Iterable {
	private NetworkEdgeData edges;
	private NetworkNode[] neighbors;
	public TransmitData(NetworkEdgeData edges,NetworkNode[] neighbors) {
		this.edges=edges;
		this.neighbors=neighbors;
	}
	
	public NetworkEdgeData getEdgeData() {
		return edges;
	}
	
	public NetworkNode[] getNeighbors(){
		return neighbors;
	}
	
	public Map NeighborMap(){
		Map map = new HashMap();
		for(int i=0;i<neighbors.length;i++){
			map.put(neighbors[i].getAddress(),neighbors[i]);
		}
		return map;
	}
	
}
