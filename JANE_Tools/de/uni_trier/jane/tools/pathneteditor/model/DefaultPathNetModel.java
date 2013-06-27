package de.uni_trier.jane.tools.pathneteditor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.constants.XMLConstants;
import de.uni_trier.jane.tools.pathneteditor.objects.*;


public class DefaultPathNetModel
	implements PathNetModel, XMLConstants {

	/**
	 * the listener added to all objects in the model
	 */
	final protected ObjectListener	default_object_listener = new ObjectListener() {
		public void propertyChanged(ObjectEvent e) {			
			fireObjectPropertyChanged(e);
		}		
	};
	
	final protected ObjectListener 	target_object_listener = default_object_listener;
//	final protected ObjectListener target_object_listener = new ObjectListener() {
//		public void propertyChanged(ObjectEvent e) { 
//			switch (e.getActionType()) {
//				case PathNetConstants.ACTION_TARGETS_CHANGED:
//					Target t = (Target)(e.getNewValue());
//					Target s = (Target)(e.getOldValue());
//					
//					if ( t != null )
//						addTarget(t);
//					else
//						delTarget(s);
//					
//					break;					
//			}
//			
//			fireObjectPropertyChanged(e);
//		}			
//	};
	
	/**
	 * the lists of model listeners
	 */
	protected List		listenerList 	= 	new LinkedList();
	
	/**
	 * The containing data
	 */	
	protected Vector	waypoints 		=	new Vector();
	protected Vector	targets			=	new Vector();
	protected Vector	edges			=	new Vector();
	protected Vector	areas			=	new	Vector();
	protected HashMap	probs			= 	new HashMap();
	protected HashMap	target2area		=	new HashMap();
	
	//	protected PathNetModel selection_model = null;
	// protected PathNetObject[]	selection = null;
	protected SelectionModel selection_model = new DefaultSelectionModel();
	
	protected HashMap	name_cache = new HashMap();
	
	/**
	 * All the caches
	 */
	protected HashMap		waypoint_edge_cache = new HashMap();
		
	protected Edge[]		edge_cache		=	new Edge[0];
	protected Waypoint[]	waypoint_cache	=	new Waypoint[0];
	protected Target[]		target_cache	=	new	Target[0];
	protected Area[]	area_cache = new Area[0];
	protected Waypoint[]	waypoint_and_target_cache = new Waypoint[0];

	private boolean			edge_cache_has_changed = false;
	private boolean			waypoint_cache_has_changed = false;
	private boolean			target_cache_has_changed = false;
	private boolean			area_cache_has_changed = false;
	private boolean			waypoint_and_target_cache_has_changed = false;

	private boolean			enableEvents	=	true;
	protected String		model_name		=	"";
	
	/*
	 *  Constructors
	 * 
	 */
	
	public DefaultPathNetModel() {
		setModelName("PN" + hashCode());
	}
	public DefaultPathNetModel(String model_name) {
		setModelName(model_name);
	}

	/*
	 * Add object methods
	 *	
	 */
	
	public boolean add(PathNetObject object) {
		switch(object.getObjectType()) {
			case PathNetConstants.TARGET:
				return addTarget((Target)object);
				
			case PathNetConstants.WAYPOINT:
				return addWaypoint((Waypoint)object);
				
			case PathNetConstants.EDGE:
				return addEdge((Edge)object);
			
			case PathNetConstants.AREA:
				return addArea((Area)object);
				
			default:
				System.err.println("DPNM.add: Undefined object type no #"+object.getObjectType());
		}
		return false;
	}
	
	public boolean addTarget(Target target) {
//		System.out.println("adding target");
		if (targets.contains(target)) return false;
	
		//this.setName(target, "T:" + (target_obj_counter++));
		if (name_cache.containsKey(target.getID())) {
			System.err.println("Adding target failed: ID is already set by: " + name_cache.get(target.getID()) + "; target: "+target);
			return false;
		}
			
		else
			name_cache.put(target.getID(), target);
						
		targets.add(target);		
		target_cache_has_changed = true;
		waypoint_and_target_cache_has_changed = true;
		
		// add listener so that changes can be fired via the model
		target.addObjectListener(default_object_listener);		
		
		fireObjectAdded(target);
		return true;
	}

	public boolean addWaypoint(Waypoint waypoint) {
		if (waypoints.contains(waypoint)) return false;

		//this.setName(waypoint, "W:" + (waypoint_obj_counter++));
		if (name_cache.containsKey(waypoint.getID()))				
			return false;
		else
			name_cache.put(waypoint.getID(), waypoint);
		
		waypoints.add(waypoint);
		waypoint_cache_has_changed = true;
		waypoint_and_target_cache_has_changed = true;

		// add listener so that changes can be fired via the model
		waypoint.addObjectListener(default_object_listener);
		
		fireObjectAdded(waypoint);
		return true;
	}
	
	public boolean addEdge(Edge edge) {
		if (edges.contains(edge)) return false;
		
		//this.setName(edge, "E:" + (edge_obj_counter++));
		if (name_cache.containsKey(edge.getID()))				
			return false;
		else
			name_cache.put(edge.getID(), edge);
		
		edges.add(edge);
		
		// cache consistence
		edge_cache_has_changed = true;

		// be sure the waypoint-edge cache is consistent by deleting the waypoints from the cache
		waypoint_edge_cache.remove(edge.getTarget());
		waypoint_edge_cache.remove(edge.getSource());
		
		// add listener so that changes can be fired via the model
		edge.addObjectListener(default_object_listener);
		
		fireObjectAdded(edge);
		return true;
	}
	
	public boolean addArea(Area area) {
		if (areas.contains(area)) return false;
		
		if (name_cache.containsKey(area.getID()))
			return false;
		else
			name_cache.put(area.getID(), area);
		
		areas.add(area);
		
		// temporarily disable events
		enableEvents = false;
		
		// add all targets from the target area to this model
//		Target[] t = area.getTargets();
//		for (int i=0; i<t.length; i++)
//			addTarget(t[i]);
		
		// reenable events
		enableEvents = true;
		
		// cache consistence
		area_cache_has_changed = true;

		// add listener so that changes can be fired via the model
		area.addObjectListener(target_object_listener);
		
		fireObjectAdded(area);
		return true;
	}
	
	/*
	 * Deletion methods start here
	 *	
	 */
	
	public void delete(PathNetObject object) {
		switch(object.getObjectType()) {
			case PathNetConstants.TARGET:
				delTarget((Target)object);
				break;
				
			case PathNetConstants.WAYPOINT:
				delWaypoint((Waypoint)object);
				break;
				
			case PathNetConstants.EDGE:
				delEdge((Edge)object);
				break;
				
			case PathNetConstants.AREA:
				delArea((Area)object);
				break;
				
			default:
				System.err.println("DPNM.remove: Undefined object type no #"+object.getObjectType());
		}		
	}

	public void delTarget(Target target) {
		if (!targets.contains(target))
			return;
		
		// temporarily disable events
		boolean oldEnableEvents = enableEvents;
		enableEvents = false;
		
		// remove assosiated edges
		Edge[] edges = getEdges(target);		
		for (int i=0; i<edges.length; i++)
			delEdge(edges[i]);
		
		// remove target
		name_cache.remove(target.getID());
		targets.remove(target);		
		
		target_cache_has_changed = true;
		waypoint_and_target_cache_has_changed = true;
		target.removeObjectListener(default_object_listener);
		
		// reenable events
		enableEvents = oldEnableEvents;
		
		fireObjectDeleted(target);
	}
	
	public void delWaypoint(Waypoint waypoint) {
		if (!waypoints.contains(waypoint))
			return;
		
		// temporarily disable events
		boolean oldEnableEvents = enableEvents;
		enableEvents = false;
		
		// remove assosiated edges
		Edge[] edges = getEdges(waypoint);		
		for (int i=0; i<edges.length; i++)
			delEdge(edges[i]);
		
		// remove waypoint		
		name_cache.remove(waypoint.getID());
		waypoints.remove(waypoint);		
		
		waypoint_cache_has_changed = true;
		waypoint_and_target_cache_has_changed = true;
		waypoint.removeObjectListener(default_object_listener);
		
		// reenable events
		enableEvents = oldEnableEvents;
		
		fireObjectDeleted(waypoint);
	}
	
	public void delEdge(Edge edge) {
		if (!edges.contains(edge)) {
			System.err.println("cannot del, edge is not member of this model: " + edge);
			return;
		}
		
		name_cache.remove(edge.getID());
		edges.remove(edge);
		
		// cache consistence
		edge_cache_has_changed = true;
		
		// be sure the waypoint-edge cache is consistent by deleting the waypoints
		waypoint_edge_cache.remove(edge.getTarget());
		waypoint_edge_cache.remove(edge.getSource());		
		
		edge.removeObjectListener(default_object_listener);
		
		fireObjectDeleted(edge);
	}
	
	public void delArea(Area area) {
		if (!areas.contains(area))
			return;
		
		// remove target area
		name_cache.remove(area.getID());
		areas.remove(area);
		
		// disable events
		boolean oldEnableEvents = enableEvents;
		enableEvents = false;
		
		// remove associated targets
		Target[] t = getTargets(area);
		for (int i=0; i<t.length; i++)
			delTarget(t[i]);
		
		// enable events
		enableEvents = oldEnableEvents;
		
		// cache consistence
		area_cache_has_changed = true;
		
		area.removeObjectListener(target_object_listener);
		fireObjectDeleted(area);
	}
	
	/*
	 * Probability methods
	 *	
	 */
	
	public boolean setProb(Waypoint source, Target target, Edge edge, double prob) {
		if ( 	   (waypoints.contains(source) || targets.contains(source))
				&& targets.contains(target)
				&& edges.contains(edge)
				&& (target != source)
//				&& edge.getSource() == source
			) {
			probs.put(getHash(source, target, edge), new Double(prob));
			fireObjectPropertyChanged(new ObjectEvent(source, PathNetConstants.ACTION_PROB_CHANGED));
			return true;
		} else {
			System.err.println("DefaultPathNetModel: setProb denied");
			System.err.println(" wp  : " + (waypoints.contains(source) || targets.contains(source)));
			System.err.println(" tg  : " + targets.contains(target));
			System.err.println(" ed  : " + edges.contains(edge));
			System.err.println(" t!=s: " + (target != source));
//			System.err.println(" sr: " + (edge.getSource() == source));		
		}
		
		return false;
	}

	/*
	 * Get methods
	 * 
	 */
	public PathNetObject getObjectById(String id) {
		Object o = name_cache.get(id);
		return (o==null)?null:(PathNetObject)o;
	}
	
	public double getProb(Waypoint source, Target target, Edge edge) {
		Object value = probs.get(getHash(source, target, edge));
		if (value != null)
			return ((Double)value).doubleValue();	

		return 0;
	}
	
	public double[] getProbs(Waypoint waypoint, Target target) {
		Edge[] out_edges = getEdges(waypoint);
		
		double[] result = new double[out_edges.length];
		for (int i=0; i<out_edges.length; i++) {
			result[i] = getProb(waypoint, target, out_edges[i]);
		}
		
		return result;
	}
	
	public Edge[] getEdges(Waypoint waypoint) {
		if (!waypoint_edge_cache.containsKey(waypoint)) {
			Edge[] all_edges = getEdges();
			Vector out_edges = new Vector();
			
			// retrieve all fitting edges
			for (int i=0; i<all_edges.length; i++) {
				if (all_edges[i].getTarget() == waypoint || all_edges[i].getSource() == waypoint)
					out_edges.add(all_edges[i]);
			}
			
			// save them in a smaller array
			Edge[] result = new Edge[out_edges.size()];
			for (int i=0; i<out_edges.size(); i++) {
				result[i] = (Edge)out_edges.get(i);
			}
			
			// put it into the cache
			waypoint_edge_cache.put(waypoint, result);
			return result;
		}
			
		return (Edge[])waypoint_edge_cache.get(waypoint);
	}

	public Edge[] getEdges() {
		if (edge_cache_has_changed){
			// create new array if old cache has different size
			if (edge_cache.length!=edges.size()) edge_cache = new Edge[edges.size()];
			
			// fill new buffer			
			for (int i=0; i<edges.size(); i++)
				edge_cache[i] = (Edge)edges.get(i);
			
			// reset changed flag
			edge_cache_has_changed = false;
		}		
		return edge_cache;
	}

	public Target[] getTargets() {
		if (target_cache_has_changed){
			// create new array if old cache has different size
			if (target_cache.length!=targets.size())
				target_cache = new Target[targets.size()];
			
			// fill new buffer			
			for (int i=0; i<targets.size(); i++)
				target_cache[i] = (Target)targets.get(i);
			
			// reset changed flag
			target_cache_has_changed = false;
		}		
		return target_cache;
	}

	public Target[] getTargets(Area area) {
		Target[] all = getTargets();
		Vector entries = new Vector();
		
		// get entries for area
//		for (int i=0; i<all.length; i++)
//			if (area.containsPoint(all[i].getPosition()))
//				entries.add(all[i]);
		
		for (int i=0; i<all.length; i++)
			if (target2area.get(all[i])==area)
				entries.add(all[i]);

		
		// cast them
		Target[] targets = new Target[entries.size()];
		for (int i=0; i<targets.length; i++)
			targets[i] = (Target)(entries.get(i));
		
		// and return
		return targets;
	}
	
	
	public Waypoint[] getWaypoints() {
		if (waypoint_cache_has_changed){
			// create new array if old cache has different size
			if (waypoint_cache.length!=waypoints.size())
				waypoint_cache = new Waypoint[waypoints.size()];
			
			// fill new buffer			
			for (int i=0; i<waypoints.size(); i++)
				waypoint_cache[i] = (Waypoint)waypoints.get(i);
			
			// reset changed flag
			waypoint_cache_has_changed = false;
		}		
		return waypoint_cache;
	}

	public Waypoint[] getTargetsAndWaypoints() {
		if (waypoint_and_target_cache_has_changed) {
			Target[] targets = getTargets();
			Waypoint[] waypoints = getWaypoints();
				
			if (waypoint_and_target_cache.length != (targets.length + waypoints.length)) 
				waypoint_and_target_cache = new Waypoint[targets.length + waypoints.length];
				
			System.arraycopy(targets, 0, waypoint_and_target_cache, 0, targets.length);
			System.arraycopy(waypoints, 0, waypoint_and_target_cache, targets.length, waypoints.length);
		
			waypoint_and_target_cache_has_changed = false;
		}
		
		return waypoint_and_target_cache;
	}
	
	public Area[] getAreas() {
		if (area_cache_has_changed){
			// create new array if old cache has different size
			if (area_cache.length!=areas.size())
				area_cache = new Area[areas.size()];
			
			// fill new buffer			
			for (int i=0; i<areas.size(); i++)
				area_cache[i] = (Area)areas.get(i);
			
			// reset changed flag
			area_cache_has_changed = false;
		}
		
		return area_cache;
	}
	
	public PathNetObject[] getAllObjects() {
		Target[] targets = getTargets();
		Waypoint[] waypoints = getWaypoints();
		Edge[] edges = getEdges();
		Area[] areas = getAreas();
		
		PathNetObject[] objects = new PathNetObject[targets.length + waypoints.length + edges.length + areas.length];
		System.arraycopy(targets, 0, objects, 0, targets.length);
		System.arraycopy(waypoints, 0, objects, targets.length, waypoints.length);
		System.arraycopy(edges, 0, objects, targets.length + waypoints.length, edges.length);
		System.arraycopy(areas, 0, objects, targets.length + waypoints.length + edges.length, areas.length);
		
		return objects;
	}

	public void addPathNetListener(PathNetModelListener listener) {
		listenerList.add(listener);
	}

	public void removePathNetListener(PathNetModelListener listener) {		
		listenerList.remove(listener);
	}
	
	// private/protected methods	
	private Object getHash(Waypoint source, Target target, Edge edge) {
		return "" + source.hashCode() + target.hashCode() + edge.hashCode();
	}
		
	protected void fireObjectDeleted(PathNetObject obj) {
		if (!enableEvents) return;
		PathNetModelEvent e = new PathNetModelEvent(obj);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).objectDeleted(e);
	}
	
	protected void fireObjectAdded(PathNetObject obj) {
		if (!enableEvents) return;
		PathNetModelEvent e = new PathNetModelEvent(obj);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).objectAdded(e);
	}
	
	protected void fireObjectPropertyChanged(ObjectEvent e) {
		if (!enableEvents) return;
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).objectPropertyChanged(e);
	}
	
	protected void fireModelDataCleared() {
		if (!enableEvents) return;
		PathNetModelEvent e = new PathNetModelEvent(null);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).modelDataCleared(e);
	}
	
	private void fireModelEventsReenabled() {		
		PathNetModelEvent e = new PathNetModelEvent(null);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).modelEventsReenabled(e);
	}
	
	protected void fireModelDataLoaded() {
		if (!enableEvents) return;
		PathNetModelEvent e = new PathNetModelEvent(null);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).modelDataLoaded(e);
	}
	
	protected void fireModelDataSaved() {
		if (!enableEvents) return;
		PathNetModelEvent e = new PathNetModelEvent(null);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).modelDataSaved(e);
	}
	
	protected void fireSelectionChanged() {
		if (!enableEvents) return;
		PathNetModelEvent e = new PathNetModelEvent(null);
		
		Iterator it = listenerList.iterator();
		while (it.hasNext())
			((PathNetModelListener)it.next()).modelSelectionChanged(e);
	}
	
	public void setModelName(String model_name) {	this.model_name = (model_name == null || model_name.equals("")) ? "PN" + hashCode() : model_name; }	
	public String getModelName() {					return model_name; }
	
	public void clearProbs() {
		probs.clear();
		fireModelDataCleared();
	}
	
	public boolean clearModelData() {		
		edges.clear();
		targets.clear();
		areas.clear();
		waypoints.clear();
		waypoint_edge_cache.clear();
		probs.clear();
		
		selection_model.clear();
				
		edge_cache_has_changed = true;
		area_cache_has_changed = true;
		target_cache_has_changed = true;
		waypoint_cache_has_changed = true;
		waypoint_and_target_cache_has_changed = true;
		
		name_cache.clear();
		
		enableEvents = true;
		
		// NOTE: The model name must not be cleared
		
		fireModelDataCleared();
		return true;
	}

	public boolean containsObject(PathNetObject object) {
		return (targets.contains(object) || waypoints.contains(object) || edges.contains(object));
	}
	
	public SelectionModel getSelectionModel() { return selection_model; }
	
	public void enableModelEvents(boolean enableEvents) {
		if (this.enableEvents = enableEvents)
			fireModelEventsReenabled();
	}

	public boolean isModelEventsEnabled() { return enableEvents; }
	
	public boolean connectAreaToTarget(Area a, Target t) {
		if (!areas.contains(a) || !targets.contains(t))
			return false;
		
		
		target2area.put(t, a);
		return true;
	}
	
	public boolean addTarget(Area area, Target target) {
//		System.out.println("adding target (" + target + ") to area " + area);
		if (!areas.contains(area) || !addTarget(target))
			return false;
		
		if (!target.getID().startsWith(area.getID()))
			throw new RuntimeException("Trying to add a badly named target to an area. Target ID must start with: "+area.getID());
		
		return connectAreaToTarget(area, target);
	}
	
	public Area getArea(Target target) {
		return target2area.containsKey(target)
				? (Area) target2area.get(target)
				: null;
	}
}
