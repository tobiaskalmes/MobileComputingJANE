package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JToggleButton;

public class ContextAction {

	private Vector handler = new Vector();
	private Component component = null;
	
	public ContextAction(Component component) {
		this.component = component;
	}
	
	public JToggleButton[] getButtons() {
		if (handler.size()==0)
			return null;
		JToggleButton[] result = new JToggleButton[handler.size()];
		for (int i=0; i<result.length; i++)
			result[i] = ((ContextActionHandler)handler.get(i)).getButton();
		return result;
	}
	
	public void addContextActionHandler(ContextActionHandler handler) {
		if (this.handler.contains(handler))
			return;
		this.handler.add(handler);
		handler.registerListener(component);
	}
	
	public void removeContextActionHandler(ContextActionHandler handler) {
		if (!this.handler.contains(handler))
			return;
		handler.removeListener(component);
		this.handler.remove(handler);
	}
	
	public void clearListeners() {
		for (int i=0; i<handler.size(); i++)
			((ContextActionHandler)handler.get(i)).removeListener(component);
		handler.clear();
	}

}
