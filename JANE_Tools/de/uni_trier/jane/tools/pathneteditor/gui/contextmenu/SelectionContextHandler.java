/*
 * Created on Apr 19, 2005
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
import de.uni_trier.jane.tools.pathneteditor.gui.PropertiesDialog;
import de.uni_trier.jane.tools.pathneteditor.model.SelectionModel;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SelectionContextHandler extends PathNetObjectContextHandler {

	private SelectionModel	selectionModel;
	private JMenuItem[]		selectionItems;
	
	public SelectionContextHandler(GraphicsPanel panel) {
		super(panel);
		this.selectionModel = model.getSelectionModel();
		
		selectionItems = new JMenuItem[3];
	}
	
	public boolean acceptsPoint(Point p) {
		PathNetObject obj = getObjectAt(p);
				
		return (selectionModel.isSelected(obj) && selectionModel.getSelectionSize()>1);
	}
	
	public JMenuItem[] getMenuItems(final Point p) {
		PathNetObject obj = getObjectAt(p);
		
		selectionItems[0] = new JMenuItem("Delete selection");
		selectionItems[0].addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				PathNetObject[] selected = selectionModel.getSelectedObjects();
				for (int i=0; i<selected.length; i++)
					model.delete(selected[i]);
				selectionModel.clear();
			}
		});
		
		selectionItems[1] = SEPARATOR;
		
//		selectionItems[2] = new JMenuItem("Cut selection");
//		selectionItems[2].setEnabled(false);
//		
//		selectionItems[3] = new JMenuItem("Copy selection");
//		selectionItems[3].setEnabled(false);
//		
//		selectionItems[4] = new JMenuItem("Paste selection");
//		selectionItems[4].setEnabled(false);
//
//		selectionItems[5] = SEPARATOR;
				 
		selectionItems[2] = new JMenuItem("Properties...");
		selectionItems[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(new PropertiesDialog(model, p)).setVisible(true);
			}
		});
		
//		// only enable if all selected objects are from the same class
//		PathNetObject[] selected = selectionModel.getSelectedObjects();
//		int type = selected[0].getObjectType();
//		for (int i=1; i<selected.length; i++)
//			selectionItems[6].setEnabled(selectionItems[6].isEnabled() && (type == selected[i].getObjectType()));
			
		return selectionItems;
	}
}
