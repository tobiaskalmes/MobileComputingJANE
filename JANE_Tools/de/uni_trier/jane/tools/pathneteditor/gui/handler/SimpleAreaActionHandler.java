/*
 * Created on Feb 19, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;

import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.SelectionHandler;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.ContextMenu;
import de.uni_trier.jane.tools.pathneteditor.model.*;
import de.uni_trier.jane.tools.pathneteditor.objects.*;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleAreaActionHandler extends AreaActionHandler {

	private static final Rectangle	AREA_RECT = new Rectangle();
	
	private Point		startRectPoint = null;
	private boolean		mouseDragging = false;
	
	/**
	 * NOTE:
	 * Here is a bug. It is documented by the sun bug database:
	 * 		http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5039416
	 * 
	 * In linux with java 1.5, an additional unwanted mouseClicked event is thrown after a mouseReleased when dragging.
	 * Use java version 1.4 or motif toolkit or windows.
	 */
	
	private MouseListener	rectMouseListener = new MouseAdapter() {		
		public void mouseReleased(MouseEvent e) {
			if (mouseDragging && area_TB.isSelected()) {
				mouseDragging = false;
				creationPhase = CREATE_VERTICES;				
				mouseClicked(e);
			}			
		}
		
		public void mouseClicked(MouseEvent e) {
			// are we selected?
			if (!area_TB.isSelected()) return;			
			
			// get actual model position
			Point vp = getModelCoords(e.getPoint());
			
			// right mouse click stops every action
			if (e.getButton()==MouseEvent.BUTTON3) {
				resetData();
				
				return;
			}
			
			// other than left mouse click? return
			if (e.getButton()!=MouseEvent.BUTTON1)  return;
			
			// here we can use some already written code
			switch (creationPhase) {
				case CREATE_START:
					AREA_RECT.setLocation(startRectPoint = vp);
					AREA_RECT.setSize(0, 0);
					
//					disableOtherMouselisteners(rectMouseListener, rectMotionListener);
					selectionHandler.setEnabled(false);
					contextMenu.setEnabled(false, SimpleAreaActionHandler.this);
					
					creationPhase = CREATE_VERTICES;
					break;
			
				case CREATE_VERTICES:
					actualArea = new Area();
					actualArea.addVertex( AREA_RECT.getLocation() );
					actualArea.addVertex( new Point(
							AREA_RECT.x + AREA_RECT.width, AREA_RECT.y
					));
					actualArea.addVertex( new Point(
							AREA_RECT.x + AREA_RECT.width, AREA_RECT.y + AREA_RECT.height
					));
					actualArea.addVertex( new Point(
							AREA_RECT.x, AREA_RECT.y + AREA_RECT.height
					));
					
					model.add(actualArea);
					
					actualArea.addObjectListener(new ObjectListener() {

						public void propertyChanged(ObjectEvent e) {
							if (e.getActionType() == ACTION_ID_CHANGED) {
								renameTargets(actualArea, e.getNewValue().toString());
							}
						}
						
					});
					
					creationPhase = CREATE_ENTRIES;
					break;
					
				case CREATE_ENTRIES:						
					// doubleclick? stop creation of entries
					if (e.getClickCount()==2) {						
						resetData();
						break;
					}
					
					String forcedID = actualArea.getID()+":"+(model.getTargets(actualArea).length+1);
					
					Target t = new Target(forcedID);
					t.setPosition(actualArea.getNearestOutlinePoint(vp));
					model.addTarget(SimpleAreaActionHandler.this.actualArea, t);
										
					break;
			}
			
		}
	};
	
	private MouseMotionListener	rectMotionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
			// are we selected?
			if (!area_TB.isSelected()) return;
			
			// dragging allowed if new object is in work
			if (creationPhase==CREATE_START && !mouseDragging) {
				
				// simulate left mouse click and call mouseClicked
				MouseEvent event = new MouseEvent(
						(Component)(e.getSource()), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(),
						e.getClickCount(), false, MouseEvent.BUTTON1
				);
				
				rectMouseListener.mouseClicked(event);
				mouseDragging = true;
			}
				
			mouseMoved(e);
		}

		public void mouseMoved(MouseEvent e) {
			// are we selected?
			if (!area_TB.isSelected()) return;
			
			// continue depending on the creation phase
			switch(creationPhase) {
				
				// do nothing if nothing is in creation
				case CREATE_START:
					return;
				
				// vertices are in creation. draw path
				case CREATE_VERTICES:
					// model coords
					Point vp = getModelCoords(e.getPoint());
					
					// helpers for rect calculation
					int x = vp.x<startRectPoint.x ? vp.x : startRectPoint.x;
					int y = vp.y<startRectPoint.y ? vp.y : startRectPoint.y;
					int w = (vp.x>startRectPoint.x ? vp.x : startRectPoint.x) - x;
					int h = (vp.y>startRectPoint.y ? vp.y : startRectPoint.y) - y;
					
					// set AREA_RECT to calculated values
					AREA_RECT.setLocation(x, y);
					AREA_RECT.setSize(w, h);
					
					// draw it
					panel.clearTemporaryShapes(false);
					panel.addTemporaryShape(
							AREA_RECT,
							CREATE_AREA_COLOR,
							false,
							true
					);
					
					break;
					
				case CREATE_ENTRIES:
					panel.clearTemporaryShapes(false);
					
					// get model coordinates and set them		
					TEMPLATE_TG.setPosition(actualArea.getNearestOutlinePoint(getModelCoords(e.getPoint())));
					
					// draw temp shape
					panel.clearTemporaryShapes(false);
					panel.addTemporaryShape(
							TEMPLATE_TG.getObjectShape(),
							CREATE_ENTRY_COLOR,					
							true,
							true
					);
					
					break;
			}			
		}
		
	};
	
	/**
	 * @param model
	 * @param panel
	 * @param contextMenu
	 */
	public SimpleAreaActionHandler(final PathNetModel model, GraphicsPanel panel, ContextMenu contextMenu, SelectionHandler selectionHandler) {
		super(model, panel, contextMenu, selectionHandler);
		area_TB.setToolTipText("Creates a new rectangular area with entries");		
		area_TB.setIcon(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/SimpleArea.png"));
		
		model.addPathNetListener(new PathNetModelListener() {

			public void objectAdded(PathNetModelEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void objectDeleted(PathNetModelEvent event) {
				PathNetObject o = (PathNetObject)(event.getSource());
				if ( o.getObjectType() == TARGET ) {
					Area a = model.getArea((Target)o);
					if (a==null) // FIXME: just necessary if target are not linked to an area (planar graph...)
						return;
					renameTargets(a, a.getID());
				}
					
			}

			public void objectPropertyChanged(ObjectEvent e) {				
			}

			public void modelDataCleared(PathNetModelEvent e) {
			}

			public void modelDataLoaded(PathNetModelEvent e) {
			}

			public void modelDataSaved(PathNetModelEvent e) {
			}

			public void modelEventsReenabled(PathNetModelEvent e) {
			}

			public void modelSelectionChanged(PathNetModelEvent e) {
			}
			
		});
	}
	
	public void registerListener(Component c) {		
		c.addMouseListener(rectMouseListener);
		c.addMouseMotionListener(rectMotionListener);
	}

	public void removeListener(Component c) {
		c.removeMouseListener(rectMouseListener);
		c.removeMouseMotionListener(rectMotionListener);
	}
	
	private void resetData() {
		creationPhase = CREATE_START;
		panel.clearTemporaryShapes(true);
//		reenableOtherMouseListeners(rectMouseListener, rectMotionListener);
		selectionHandler.setEnabled(true);
		contextMenu.setEnabled(true, SimpleAreaActionHandler.this);
	}
	
	private void renameTargets(Area area, String newPrefix) {
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(false);
		
		Target[] oldTargets = model.getTargets(area);								
		for (int i=0; i<oldTargets.length; i++) {
			model.delete(oldTargets[i]);																
		
			Target newTarget = new Target(newPrefix +":"+(i+1));
			newTarget.setDescription(oldTargets[i].getDescription());
			newTarget.setPosition(oldTargets[i].getPosition());
			newTarget.setSymbolSize(oldTargets[i].getSymbolSize());
			
			model.addTarget(area, newTarget);
		}
		
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(true);

	}
}
