package de.uni_trier.jane.tools.pathneteditor.tools;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.Edge;
import de.uni_trier.jane.tools.pathneteditor.objects.Target;
import de.uni_trier.jane.tools.pathneteditor.objects.Waypoint;


public class RoutingAlgorithms {

	final private static double	WEIGHT = 3.0;
	
	private static HashMap	edgeCostCache = new HashMap();
		
	public static void	performDjikstra(PathNetModel model) {
		// be sure the edge cache is consistent
		edgeCostCache.clear();
		
		// do this for all waypoints and targets
		Waypoint[] wps = model.getTargetsAndWaypoints();
		for (int i=0; i<wps.length; i++) {
			HashMap	pred = new HashMap();
			HashMap	dist = new HashMap();
			dijkstra_implementation(model, wps[i], dist, pred);
			setProbabilities(model, wps[i], dist, pred);
		}
	}
	
	public static void performWeightedDijkstra(PathNetModel model) {
		// be sure the edge cache is consistent
		edgeCostCache.clear();
		
		// do the weighted algorithm for all waypoints and targets
		Waypoint[] wps = model.getTargetsAndWaypoints();
		for (int i=0; i<wps.length; i++) {
			// for all outgoing edges, calculate shortest paths of target nodes
			Edge[] edges = model.getEdges(wps[i]);
			HashMap dists = new HashMap();
			for (int j=0; j<edges.length; j++) {
				Waypoint target = (edges[j].getSource() != wps[i]) ? edges[j].getSource() : edges[j].getTarget();
				HashMap dist = new HashMap();
				HashMap pred = new HashMap();
				
				model.delete(edges[j]);
				dijkstra_implementation(model, target, dist, pred);
				model.add(edges[j]);
				
				dists.put(edges[j], dist);
			}
			
			setWeightedProbabilities(model, wps[i], dists);
		} 
	}
	
	private static void dijkstra_implementation(PathNetModel model, Waypoint s, HashMap dist, HashMap pred) {
		PriorityQueue pq = new RoutingAlgorithms().new PriorityQueue();
				
		Waypoint[] tgs = model.getTargetsAndWaypoints();
		for (int i=0; i<tgs.length; i++) {
			dist.put(tgs[i], new Integer(Integer.MAX_VALUE));			
		}
		
		pq.insert(0, s);
		dist.put(s, new Integer(0));
		
		while (!pq.isEmpty()) {
			Waypoint u = (Waypoint)(pq.del_min());
			int du = getInt(dist.get(u));
			
			Edge e;
			Edge[] eds = model.getEdges(u);
			
			for (int i=0; i<eds.length; i++) {
				Waypoint v = (u != eds[i].getTarget()) ? eds[i].getTarget(): eds[i].getSource();
				int c = du + cost(eds[i]);
				
				if (c < getInt(dist.get(v))) {
					if (getInt(dist.get(v)) == Integer.MAX_VALUE)
						pq.insert(c, v);
					else
						pq.decrease(c, v);
					
					dist.put(v, new Integer(c));
					pred.put(v, eds[i]);
				}
			}
		}
	}
	
	/*
	 * Returns the cost of an edge as its distance sum of its segments  
	 * @param e	The edge
	 * @return	The length of the edge as its cost
	 */
	private static int cost(Edge e) {				
		if (! edgeCostCache.containsKey(e)) {
			Point2D[] ipoints = e.getInnerPoints();
			Point2D[] points = new Point[ipoints.length + 2];
			
			System.arraycopy(ipoints, 0, points, 1, ipoints.length);
			points[0] = e.getSource().getPosition();
			points[points.length-1] = e.getTarget().getPosition();
			
			int cost = 0;
			for (int i=0; i<points.length-1; i++) {
				cost += points[i].distance(points[i+1]);
			}
			
			edgeCostCache.put(e, new Integer(cost));
		}
		
		return getInt(edgeCostCache.get(e));
	}
	
	private static void setProbabilities(PathNetModel model, Waypoint source, HashMap dist, HashMap pred) {
		/*
		 * debug
		 */
		
//		System.out.println("DIJKSTRA from waypoint w: "+ source.getDescription());
//		
//		Iterator i = dist.keySet().iterator();
//		while (i.hasNext()) {
//			Waypoint target = ((Waypoint)(i.next()));
//			System.out.println("\tto "+target.getDescription()+":\t"+getInt(dist.get(target))+"\tPath: "+getPath(target, pred));
//		}
		
		/* ***************************************** */
		
		Iterator it = dist.keySet().iterator();
		while (it.hasNext()) {
			Waypoint wp = ((Waypoint)(it.next()));
			if (wp.getObjectType() != PathNetConstants.TARGET)
				continue;
			
			Target target = (Target)wp;
			Edge lastPathSegment = getLastPathSegment(target, pred);
			
			model.setProb(source, target, lastPathSegment, 1.0);
			
		}
	}
	
	private static void setWeightedProbabilities(PathNetModel model, Waypoint source, HashMap dists) {
		Target[] targets = model.getTargets();
		Edge[] edges = model.getEdges(source);
		
		for (int i=0; i<targets.length; i++) {
			
			int sum = 0;
			double[] wayLengths = new double[edges.length];
			
			// get individual length of paths
			// dist.get(targets[i]) == k?rzeste entfernung des targets[i] von source ?ber die edge edges[j] 
			for (int j=0; j<edges.length; j++) {
				HashMap dist = (HashMap)(dists.get(edges[j]));
				wayLengths[j] = getInt(dist.get(targets[i])) + cost(edges[j]);
				sum += wayLengths[j];
			}			
			
			// get sum of "kehrwerte"
			double sumKW = 0.0;
			for (int j=0; j<edges.length; j++) {
				wayLengths[j] = Math.pow((double)sum / wayLengths[j], WEIGHT);
				sumKW += wayLengths[j];
			}
			
			// set probs for paths
			double rest = 1.0;
			for (int j=0; j<edges.length-1; j++) {
				double prob = Math.round((double)wayLengths[j] / sumKW * 100.0) / 100.0;  
				rest -= prob;
				if (rest<0){
				    prob=prob-rest;
                    rest=0;
                }
				model.setProb(source, targets[i], edges[j], prob);
			}
            
			
			if (edges.length >= 1)
				model.setProb(source, targets[i], edges[edges.length-1], Math.round(rest * 100.0) / 100.00);
		}
	}
		
	private static Edge getLastPathSegment(Waypoint target, HashMap pred) {
		Edge e = null;
		
		Waypoint w = target;
		while (pred.get(w) != null) {
			e = (Edge)(pred.get(w));
			w = (e.getSource() == w) ? e.getTarget() : e.getSource();
		}
		
		return e;
	}
	
	private static String getPath(Waypoint s, HashMap pred) {
		StringBuffer sb = new StringBuffer();
		
		Waypoint w = s;
		while (pred.get(w) != null) {
			Edge e = (Edge)(pred.get(w));
			
			sb.insert(0, "/"+e.getDescription()+"["+e.getSource().getDescription()+"-"+e.getTarget().getDescription()+"]");
			w = (e.getSource() == w) ? e.getTarget() : e.getSource();
		}
		
		return sb.toString();
	}
	
	private class PriorityQueue {
		
		private Comparator prioComparator = new PrioritySetComparator();
		
		private Vector	queue = new Vector();
		private HashMap	valueToPrioritySetMap = new HashMap();
		
		private class PrioritySet {
			public int		priority;
			public Object	value;
			
			public PrioritySet(int priority, Object value) {
				this.priority = priority;
				this.value = value;
			}
						
			public String toString() {
				return "[P:"+priority+";V:"+value.toString()+"]";
			}			
		}
		
		private class PrioritySetComparator implements Comparator {
			public int compare(Object o1, Object o2) {				
				return ((PrioritySet)o1).priority - ((PrioritySet)o2).priority;
			}			
		}
				
		public void insert(int priority, Object o) {
			PrioritySet set = new PrioritySet(priority, o);
			if (queue.contains(set))
				return;
			
			int idx = Collections.binarySearch(queue, set, prioComparator);		
			queue.insertElementAt(set, (idx < 0) ? -(idx+1) : idx);
			valueToPrioritySetMap.put(o, set);
		}
		
		public boolean isEmpty() {
			return queue.isEmpty();
		}
		
		public Object del_min() {
			Object result = ((PrioritySet)(queue.firstElement())).value;
			queue.remove(queue.firstElement());
			
			valueToPrioritySetMap.remove(result);			
			return result;
		}
		
		public void decrease(int priority, Object o) {			
			queue.remove(valueToPrioritySetMap.get(o));
			valueToPrioritySetMap.remove(o);
			
			insert(priority, o);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<queue.size(); i++) {				
				sb.append(queue.elementAt(i).toString());
			}
			return sb.toString();
		}
	}
	
	private static int getInt(Object o) {
		return ((Integer)o).intValue();
	}
	
	public static void main(String[] args) {
		String value = "AnotherValue";
		PriorityQueue pq = new RoutingAlgorithms().new PriorityQueue();
		
		for (int i=10; i>=0; i--) {
			pq.insert(i, "" + i);
		}
		pq.insert(100, "VALUE");
		pq.insert(75, "VALUE2");
		
		System.out.println(pq);
		
		pq.insert(3, value);
		System.out.println(pq);
		
		System.out.println(pq.del_min());
		System.out.println(pq);
		
		pq.decrease(6, value);
		pq.decrease(1, "VALUE");
		
		System.out.println(pq);
		
		while (!pq.isEmpty())
			System.out.println(pq.del_min());
	}
}
