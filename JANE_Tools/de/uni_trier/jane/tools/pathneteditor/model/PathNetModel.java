package de.uni_trier.jane.tools.pathneteditor.model;

import de.uni_trier.jane.tools.pathneteditor.objects.*;

public interface PathNetModel {
	public void		setModelName(String model_name);
	public String	getModelName();
	
	public boolean	clearModelData();
	
	public boolean 	setProb(Waypoint source, Target target, Edge edge, double prob);
	public double 	getProb(Waypoint source, Target target, Edge edge);
	
//	public void		setName(PathNetObject obj, String name) throws IllegalArgumentException;
//	public String	getName(PathNetObject obj);
	
	public boolean		add(PathNetObject object);
	public boolean		addTarget(Target target);
	public boolean		addTarget(Area area, Target target);
	public boolean		addWaypoint(Waypoint waypoint);
	public boolean		addEdge(Edge edge);
	public boolean		addArea(Area area);
	
	public void		delete(PathNetObject object);
	public void		delTarget(Target target);
	public void		delWaypoint(Waypoint waypoint);
	public void		delEdge(Edge edge);
	public void		delArea(Area area);

	public boolean	connectAreaToTarget(Area a, Target t);
	
	public double[]	getProbs(Waypoint waypoint, Target target);	
	public Edge[]	getEdges(Waypoint waypoint);
	
	public boolean	containsObject(PathNetObject object);
	
	public PathNetObject[] getAllObjects();
	public PathNetObject	getObjectById(String id);
	public Edge[]	getEdges();
	public Target[]	getTargets();

	public Waypoint[] getWaypoints();
	public Waypoint[] getTargetsAndWaypoints();
	public Area[] getAreas();
	
	public Area getArea(Target target);
	public Target[] getTargets(Area area);
	
	public SelectionModel	getSelectionModel();
	
	public void 	addPathNetListener(PathNetModelListener listener);
	public void		removePathNetListener(PathNetModelListener listener);
}
