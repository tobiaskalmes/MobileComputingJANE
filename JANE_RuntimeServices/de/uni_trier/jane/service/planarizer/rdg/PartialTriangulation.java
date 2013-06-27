package de.uni_trier.jane.service.planarizer.rdg;

import java.util.*;

public interface PartialTriangulation {

	public void add(NetworkEdge edge);
	
	public void delete(NetworkEdge edge);
	
	public Vector getEdgesVector();
	
	public NetworkEdge[] getEdges();

}
