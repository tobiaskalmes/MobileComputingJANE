package de.uni_trier.jane.service.planarizer.rdg;

import java.util.*;

public class PartialTriangulationImpl implements PartialTriangulation {

	private Vector vector;
	
	public PartialTriangulationImpl(){
		vector=new Vector();
	}
	
	public void add(NetworkEdge edge) {
		vector.add(edge);

	}

	public void delete(NetworkEdge edge) {
		vector.remove(edge);
	}

	public Vector getEdgesVector() {
		return vector;
	}

	public NetworkEdge[] getEdges() {
		return (NetworkEdge[]) vector.toArray(new NetworkEdge[vector.size()]);
	}

	public Iterator iterator() {
		return vector.iterator();
	}

}
