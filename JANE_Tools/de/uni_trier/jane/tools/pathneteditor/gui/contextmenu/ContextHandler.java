/*
 * Created on Jan 26, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.contextmenu;

import java.awt.Point;

import javax.swing.JMenuItem;

/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ContextHandler {
	
	final public static JMenuItem SEPARATOR = new JMenuItem();
	
	public abstract JMenuItem[] getMenuItems(Point p);
	public abstract boolean 	acceptsPoint(Point p);
	public abstract String		getLabel(Point p);
	
}
