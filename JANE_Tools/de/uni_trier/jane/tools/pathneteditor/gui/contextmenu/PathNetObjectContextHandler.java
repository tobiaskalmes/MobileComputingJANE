/*
 * Created on Jan 26, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.contextmenu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.PropertiesDialog;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PathNetObjectContextHandler implements ContextHandler {
	
	protected Point			last_screen_point = null;
	protected Point			last_real_point = null;
	protected PathNetObject last_object = null;
	
	protected PathNetModel	model = null;
	protected GraphicsPanel panel = null;
		
	public PathNetObjectContextHandler(GraphicsPanel panel) {
		this.model = panel.getPathnetModel();
		this.panel = panel;		
	}
	
	public String getLabel(Point p) {
		return ( (getObjectAt(p)==null) ? "" : getObjectAt(p).getDescription() );
	}
	
	protected PathNetObject getObjectAt(Point p) {
		if (p.equals(last_screen_point)) {
			return last_object;
		}		
		
		last_screen_point = p;		
		last_real_point = PathNetTools.virtualToReal(p, panel.getZoom());
		last_real_point.translate(panel.getOffset().x, panel.getOffset().y);
				
		return (last_object=PathNetTools.getObjectAtRealPoint(model, last_real_point));
	}
	
	protected static ActionListener getDefaultPropertiesActionListener(final PathNetObject obj, final Point p, final PathNetModel model) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!model.getSelectionModel().isSelected(obj)) {
					model.getSelectionModel().setEventsEnabled(false);
					
					model.getSelectionModel().clear();
					model.getSelectionModel().add(obj);
					
					model.getSelectionModel().setEventsEnabled(true);
				}
				(new PropertiesDialog(model, p)).setVisible(true);
			}
		};
	}
}
