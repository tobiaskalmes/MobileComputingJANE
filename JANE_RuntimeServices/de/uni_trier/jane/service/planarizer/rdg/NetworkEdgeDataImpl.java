package de.uni_trier.jane.service.planarizer.rdg;

import java.util.*;

public class NetworkEdgeDataImpl implements NetworkEdgeData {

	private Vector vector;
	private NetworkEdge[] edges;
	
	public NetworkEdgeDataImpl(NetworkEdge[] edges){
		this.edges=edges;
		vector=new Vector();
		for(int i=0;i<edges.length;i++){
			vector.add(edges[i]);
		}
	}
	
	public boolean contains(NetworkEdge edge) {
		return vector.contains(edge);
	}

	public NetworkEdge[] getEdges() {
		return edges;
	}

}
