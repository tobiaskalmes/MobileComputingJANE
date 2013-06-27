package de.uni_trier.jane.tools.pathneteditor.model;

import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;

public class SelectionEvent {
	private PathNetObject[]	objects;
	
	public SelectionEvent(PathNetObject[] objects) {
		this.objects = objects;
	}
	
	public PathNetObject[] getModifiedObjects() {
		return objects;
	}
}
