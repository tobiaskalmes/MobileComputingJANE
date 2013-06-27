package de.uni_trier.jane.tools.pathneteditor.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;


public final class DefaultSelectionModel implements SelectionModel {

	private HashSet		selected_objects = new HashSet();
	private Vector		listenerList = new Vector();
	
	private PathNetObject[]	selectionArray = new PathNetObject[0];
	private boolean	selected_objects_changed = false;
	private boolean eventsEnabled = true;
			
	public boolean add(PathNetObject object) {
		if (selected_objects_changed = selected_objects.add(object))
			fireSelectionChanged(new PathNetObject[] {object} );
		
		return selected_objects_changed;
	}
	public boolean add(PathNetObject[] objects) {
		boolean result = true;
		
		for (int i=0; i<objects.length; i++)
			result = result & selected_objects.add(objects[i]);
		
		fireSelectionChanged(objects);
		
		return result;
	}
	
	public boolean remove(PathNetObject object) {
		if (selected_objects_changed = selected_objects.remove(object))
			fireSelectionChanged(new PathNetObject[] {object} );
		
		return selected_objects_changed;
	}
	
	public void clear() {
		fireSelectionChanged(getSelectedObjects());
		
		selected_objects.clear();
		selected_objects_changed = true;		
	}

	public int getSelectionSize() { return selected_objects.size(); }
	public boolean isSelected(PathNetObject object) { return selected_objects.contains(object); }
	public PathNetObject[] getSelectedObjects() {
		if (selected_objects_changed)
			updateSelectionArray();
		
		return selectionArray;
	}
	
	private void updateSelectionArray() {
		if (selectionArray.length != selected_objects.size())
			selectionArray = new PathNetObject[selected_objects.size()];
		
		Iterator it = selected_objects.iterator();
		for (int i=0; it.hasNext(); i++)
			selectionArray[i] = (PathNetObject)it.next();
		
		selected_objects_changed = false;
	}
	
	protected void fireSelectionChanged(PathNetObject[] newObjects) {
		if (!eventsEnabled)
			return;
		
		SelectionEvent event = new SelectionEvent(newObjects);
		
		for (int i=0; i<listenerList.size(); i++)
			((SelectionListener)(listenerList.get(i))).selectionChanged(event);
	}
	
	public void addSelectionListener(SelectionListener listener) {
		listenerList.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		listenerList.remove(listener);
	}
	
	public void setEventsEnabled(boolean setEnabled) {
		if (this.eventsEnabled = setEnabled)
			fireSelectionChanged(getSelectedObjects());
	}
}
