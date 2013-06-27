package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Component;

import javax.swing.JToggleButton;

public interface ContextActionHandler {
	public abstract void registerListener(Component c);
	public abstract void removeListener(Component c);
	
	public abstract void setEnabled(boolean value);
	public abstract boolean isEnabled();
	
	public abstract JToggleButton getButton();
}
