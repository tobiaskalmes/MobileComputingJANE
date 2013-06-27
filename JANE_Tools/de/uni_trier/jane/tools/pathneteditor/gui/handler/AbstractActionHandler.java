package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JToggleButton;

import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;


public abstract class AbstractActionHandler implements ContextActionHandler {

	protected boolean 		isEnabled = true;
	protected PathNetModel	model;
	protected GraphicsPanel	panel;
	
	public abstract void registerListener(Component c);
	public abstract void removeListener(Component c);
	public abstract JToggleButton getButton();
	
	public AbstractActionHandler(PathNetModel model, GraphicsPanel panel) {
		this.model = model;
		this.panel = panel;
	}
	
	public void setEnabled(boolean value) { isEnabled = value; }
	public boolean isEnabled() { return isEnabled; }
	
	protected Point getModelCoords(Point p) {
		Point vp = PathNetTools.virtualToReal(p, panel.getZoom());
		vp.translate(panel.getOffset().x, panel.getOffset().y);
		
		return vp;
	}
}
