package de.uni_trier.jane.service.planarizer.rdg.delaunay;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.planarizer.rdg.*;

public class DelaunayTriangulationHelper {

	public static NetworkEdge[] delaunayTriangulation(NetworkNode[] nodes){

		
		SortedMap map = new TreeMap();
		for(int i=0; i<nodes.length; i++) {
			map.put(nodes[i].getPosition(), nodes[i]);
		}
		nodes = new NetworkNode[map.size()];
		Iterator iterator = map.keySet().iterator();
		int j = 0;
		while (iterator.hasNext()) {
			Object key = (Object) iterator.next();
			nodes[j] = (NetworkNode)map.get(key);
			j++;
		}
		
		
		
		// calculate minimum bounding rectangle
		Rectangle bound = new Rectangle(nodes[0].getPosition(), nodes[0].getPosition());
		for(int i=1; i<nodes.length; i++) {
			nodes[i].getPosition();
			Rectangle rectangle = new Rectangle(nodes[i].getPosition(), nodes[i].getPosition());
			bound = bound.union(rectangle);
		}

		// TODO: calculate bounding simplex

		
        Simplex tri = new Simplex(new Pnt[] {new NetworkNodePoint(new Position(-100000,100000)), new NetworkNodePoint(new Position(100000,100000)), new NetworkNodePoint(new Position(0,-100000))});

        // TEST
	    double initialSize = 100000.0;//10000;      // Controls size of initial triangle
        tri = new Simplex(new NetworkNodePoint[] {
                new NetworkNodePoint(new Position(-initialSize, -initialSize)),
                new NetworkNodePoint(new Position( initialSize, -initialSize)),
                new NetworkNodePoint(new Position(           0,  initialSize))});

        
        
//        System.out.println("Triangle created: " + tri);
        DelaunayTriangulation dt = new DelaunayTriangulation(tri);
//        System.out.println("DelaunayTriangulation created: " + dt);
        
        for(int i=0; i<nodes.length; i++) {
        	dt.delaunayPlace(new NetworkNodePoint(nodes[i]));
        }
//        System.out.println("After adding 3 points, the DelaunayTriangulation is a " + dt);
//        dt.printStuff();

        Set result = new HashSet();
        Iterator simplexIterator = dt.iterator();
        while (simplexIterator.hasNext()) {
			Simplex simplex = (Simplex) simplexIterator.next();
			Iterator facetsIterator = simplex.facets().iterator();
			while (facetsIterator.hasNext()) {
				Set facet = (Set) facetsIterator.next();
                NetworkNodePoint[] endpoint = (NetworkNodePoint[]) facet.toArray(new NetworkNodePoint[2]);
				NetworkNode networkNode1 = endpoint[0].getNode();
				NetworkNode networkNode2 = endpoint[1].getNode();
				if(networkNode1 != null && networkNode2 != null) {
					result.add(new NetworkEdge(networkNode1, networkNode2));
				}
			}
		}

        return (NetworkEdge[])result.toArray(new NetworkEdge[result.size()]);
        
	}

	
	
}
