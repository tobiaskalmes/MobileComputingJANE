/*
 * Created on Apr 17, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.SelectionModel;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SelectionHandler {

	private GraphicsPanel 	panel;
	private PathNetModel	model;
	private SelectionModel	selectionModel;
	private boolean			isEnabled = true;
	
	public SelectionHandler(final GraphicsPanel panel) {
		this.panel = panel;
		this.model = panel.getPathnetModel();
		this.selectionModel = model.getSelectionModel();
		
		panel.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				if (!isEnabled)
					return;
				
				Point p = e.getPoint();
				
				Point vp = PathNetTools.virtualToReal(p, panel.getZoom());
				vp.translate(panel.getOffset().x, panel.getOffset().y);
				
				PathNetObject obj = PathNetTools.getObjectAtRealPoint(model, vp);
				
				switch(e.getButton()) {
				case MouseEvent.BUTTON1:
					boolean strgPressed = (e.getModifiers() & MouseEvent.CTRL_MASK) != 0;
									
					if (obj != null) {
						if (strgPressed && selectionModel.isSelected(obj))
							removeSelection(obj);
						
						else
							setSelection(obj, !strgPressed);
					}
					
					break;
				}
			}
		});
	}
	
	private void setSelection(PathNetObject obj, boolean clearSelection) {
		selectionModel.setEventsEnabled(false);
		
		if (clearSelection)
			selectionModel.clear();
				
		selectionModel.add(obj);
		
		selectionModel.setEventsEnabled(true);
	}
	
	private void removeSelection(PathNetObject obj) {
		selectionModel.remove(obj);		
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
}
