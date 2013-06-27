package de.uni_trier.jane.tools.pathneteditor.model;

import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;

/**
 * @author steffen
 *
 */
public interface SelectionModel {
	
	public boolean add(PathNetObject object);
	
	public boolean remove(PathNetObject object);
	public void clear();
	
	public int		getSelectionSize();
	public boolean 	isSelected(PathNetObject object);

	public PathNetObject[]	getSelectedObjects();
	
	public void setEventsEnabled(boolean setEnabled);
	
	public void addSelectionListener(SelectionListener listener);
	public void removeSelectionListener(SelectionListener listener);
}
