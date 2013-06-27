package de.uni_trier.jane.tools.pathneteditor.model;

import de.uni_trier.jane.tools.pathneteditor.objects.ObjectEvent;

public interface PathNetModelListener {
	public void		objectAdded(PathNetModelEvent event);
	public void		objectDeleted(PathNetModelEvent event);
	public void 	objectPropertyChanged(ObjectEvent e);
	
	/** A notification that the model data has been deleted.
	 * @param e		The associated PathNetModelEvent
	 */
	public void		modelDataCleared(PathNetModelEvent e);	
	public void		modelDataLoaded(PathNetModelEvent e);
	public void		modelDataSaved(PathNetModelEvent e);
	
	/**
	 * Model events were disabled via the enableModelEvents(boolean) method and
	 * are now reenabled again.
	 * @param e 	The associated PathNetModelEvent	 * 
	 */
	public void		modelEventsReenabled(PathNetModelEvent e);
	
	public void		modelSelectionChanged(PathNetModelEvent e);
}
