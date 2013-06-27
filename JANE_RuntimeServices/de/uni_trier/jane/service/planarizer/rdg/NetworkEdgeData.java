package de.uni_trier.jane.service.planarizer.rdg;


public interface NetworkEdgeData {

	public boolean contains(NetworkEdge edge);
	
	public NetworkEdge[] getEdges();
}
