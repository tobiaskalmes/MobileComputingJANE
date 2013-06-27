/*
 * Created on Feb 15, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.contextmenu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.handler.AreaActionHandler;
import de.uni_trier.jane.tools.pathneteditor.objects.Area;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AreaContextHandler extends PathNetObjectContextHandler {

	private JMenuItem[]	area_items;
	private Area		act_area = null;
	private AreaActionHandler actionHandler;
	
	public AreaContextHandler(GraphicsPanel panel, AreaActionHandler actionHandler) {
		super(panel);
		this.actionHandler = actionHandler;
		
		area_items = new JMenuItem[4];		
	}

	public JMenuItem[] getMenuItems(Point p) {
		if (!acceptsPoint(p))
			return null;
				
		if (act_area != null && act_area.equals(getObjectAt(p)))
			return area_items;
		
		act_area = (Area)getObjectAt(p);
		
		// Item 0
		area_items[0] = new JMenuItem("Delete area");
		area_items[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.delete(act_area);
				act_area = null;
			}
		});
		
		// Item 1
		area_items[1] = new JMenuItem("Add more entries...");
		area_items[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionHandler.getButton().setSelected(true);
				actionHandler.addEntries(act_area);
			}
		});
		
		// Item 2
		area_items[2] = SEPARATOR;
		
		// Item 3
		area_items[3] = new JMenuItem("Properties...");
		area_items[3].addActionListener(getDefaultPropertiesActionListener(act_area, p, model));
		
		return area_items;
	}
	
	public boolean acceptsPoint(Point p) {		
		return (getObjectAt(p)!=null && getObjectAt(p).getObjectType()==PathNetObject.AREA);
	}

	public String getLabel(Point p) {
		return "Area: " + super.getLabel(p);
	}

}
