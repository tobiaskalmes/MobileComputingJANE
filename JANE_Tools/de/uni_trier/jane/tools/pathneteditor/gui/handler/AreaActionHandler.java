package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.SelectionHandler;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.ContextMenu;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.Area;
import de.uni_trier.jane.tools.pathneteditor.objects.Target;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class AreaActionHandler extends AbstractActionHandler implements PathNetConstants {

	final protected static Target	TEMPLATE_TG = new Target(""); 
	final protected static Color	CREATE_AREA_COLOR = new Color(
			Settings.getColor(DEFAULT_AREA_COLOR).getRed(),
			Settings.getColor(DEFAULT_AREA_COLOR).getGreen(),
			Settings.getColor(DEFAULT_AREA_COLOR).getBlue(),
			Settings.getInt(DEFAULT_DRAG_ALPHA)
	);
	final protected static Color		CREATE_ENTRY_COLOR = new Color(
			Settings.getColor(DEFAULT_AREA_TARGET_COLOR).getRed(),
			Settings.getColor(DEFAULT_AREA_TARGET_COLOR).getGreen(),
			Settings.getColor(DEFAULT_AREA_TARGET_COLOR).getBlue(),
			Settings.getInt(DEFAULT_DRAG_ALPHA)
	);
	
	final protected static int	CREATE_START		= 0;
	final protected static int	CREATE_VERTICES		= -1;
	final protected static int	CREATE_ENTRIES		= -2;	
	protected int		creationPhase = CREATE_START;	
	
	protected Vector		areaOutline = new Vector();
	protected Point		lastVertex	= null, firstVertex = null;
	protected Area		actualArea	= null;
	
	protected ContextMenu 		contextMenu;
	protected SelectionHandler	selectionHandler;
	
	protected MouseListener[]			mouseListeners;
	protected MouseMotionListener[]		motionListeners;
	protected JToggleButton	area_TB = new JToggleButton();
	
	protected MouseListener mouseListener = new MouseAdapter() {
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
			if (e.getButton()!=MouseEvent.BUTTON1) return;
			
			// continue depending on the creation phase
			switch (creationPhase) {			
				// start creation of a new area
				case CREATE_START:
					// reset data
					areaOutline.clear();
					firstVertex = lastVertex = vp;
					areaOutline.add(firstVertex);
					actualArea = null;
					
//					disableOtherMouselisteners(mouseListener, motionListener);
					selectionHandler.setEnabled(false);
					contextMenu.setEnabled(false, AreaActionHandler.this);
					
					creationPhase = CREATE_VERTICES;	
										
					break;
					
				// creation of vertices in progress
				case CREATE_VERTICES:					
					// add vertex to outline											
					areaOutline.add(lastVertex = vp);
					
					// doubleclick or near startpoint? end creation and add area to model
					if (e.getClickCount()==2 || firstVertex.distance(vp) <= Settings.getInt(SELECTION_TOLERANCE)) {
						// skip last added point from outline
						areaOutline.remove(lastVertex);
												
						// area is valid if there are at least 3 vertices 
						if (areaOutline.size()< 3) {
							resetData();
							return;
						}
						
						// create new Area and add vertices
						actualArea = new Area();
						for (int i=0; i<areaOutline.size(); i++) {
							Point p = (Point)areaOutline.get(i);
							actualArea.addVertex(p);
						}						
						
						// add to model
						model.add(actualArea);
						
						// change creation phase
						creationPhase = CREATE_ENTRIES;					
					}				
					
					break;
				
				// create some entries
				case CREATE_ENTRIES:
					// add entry with pos of outline nearest to vp to model
					Target t = new Target();
					t.setPosition(actualArea.getNearestOutlinePoint(vp));
					model.addTarget(actualArea, t);
					
					// doubleclick? stop creation of entries
					if (e.getClickCount()==2)
						resetData();
					
					break;
					
			} // end of switch
		}		
	};
	
	protected MouseMotionListener motionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
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
					// clear old temp shapes
					panel.clearTemporaryShapes(false);
					
					// draw outline					
					for (int i=1; i<areaOutline.size(); i++) {
						Point p1 = (Point)(areaOutline.get(i-1));
						Point p2 = (Point)(areaOutline.get(i));						
						
						// draw line
						panel.addTemporaryShape(
								new Line2D.Float(p1, p2),
								CREATE_AREA_COLOR,
								false,
								false
						);
						
						// draw vertex
						panel.addTemporaryShape(
								new Ellipse2D.Float(
										p2.x-Settings.getInt(DEFAULT_VERTICES_SIZE)/2, p2.y-Settings.getInt(DEFAULT_VERTICES_SIZE)/2,
										Settings.getInt(DEFAULT_VERTICES_SIZE), Settings.getInt(DEFAULT_VERTICES_SIZE)
								),
								CREATE_ENTRY_COLOR,
								true,
								false								
						);
					}
					
					// model coords
					Point vp = getModelCoords(e.getPoint());
					
					// draw line from lastVertex to vp
					panel.addTemporaryShape(
							new Line2D.Float(lastVertex, vp),
							CREATE_AREA_COLOR,
							false,
							false
					);
					panel.addTemporaryShape(
							new Ellipse2D.Float(
									vp.x-Settings.getInt(DEFAULT_VERTICES_SIZE)/2, vp.y-Settings.getInt(DEFAULT_VERTICES_SIZE)/2,
									Settings.getInt(DEFAULT_VERTICES_SIZE), Settings.getInt(DEFAULT_VERTICES_SIZE)
							),
							CREATE_ENTRY_COLOR,
							true,
							false								
					);
					
					// draw line from firstVertex to vp
					panel.addTemporaryShape(
							new Line2D.Float(firstVertex, vp),
							CREATE_AREA_COLOR,
							false,
							false
					);
					panel.addTemporaryShape(
							new Ellipse2D.Float(
									firstVertex.x-Settings.getInt(DEFAULT_VERTICES_SIZE)/2, firstVertex.y-Settings.getInt(DEFAULT_VERTICES_SIZE)/2,
									Settings.getInt(DEFAULT_VERTICES_SIZE), Settings.getInt(DEFAULT_VERTICES_SIZE)
							),
							CREATE_ENTRY_COLOR,
							true,
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
			}
		}
		
	};
	
	public AreaActionHandler (PathNetModel model, GraphicsPanel panel, ContextMenu contextMenu, SelectionHandler selectionHandler) {
		super(model, panel);
		this.contextMenu = contextMenu;
		this.selectionHandler = selectionHandler;
		
		area_TB.setIcon(new ImageIcon("icons/Area.png"));
		area_TB.setToolTipText("Creates a new area with entries");
		area_TB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {				
				if (!area_TB.isSelected()) resetData();
			}			
		});
	}
	
	public void registerListener(Component c) {		
		c.addMouseListener(mouseListener);
		c.addMouseMotionListener(motionListener);
	}

	public void removeListener(Component c) {
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(motionListener);
	}

	public JToggleButton getButton() { return area_TB; }
	
	/**
	 * Add target entries for the area <i>area</i>.
	 * @param area The area to work on.
	 */
	public void addEntries(Area area) {
		actualArea = area;
		creationPhase = CREATE_ENTRIES;
//		disableOtherMouselisteners(mouseListener, motionListener);
		selectionHandler.setEnabled(false);
		contextMenu.setEnabled(false, AreaActionHandler.this);
	}
	
	// private methods
	private void resetData() {
		creationPhase = CREATE_START;
		panel.clearTemporaryShapes(true);
//		reenableOtherMouseListeners(mouseListener, motionListener);
		selectionHandler.setEnabled(true);
		contextMenu.setEnabled(true, AreaActionHandler.this);
	}
	
	protected void disableOtherMouselisteners(MouseListener mouseListener, MouseMotionListener motionListener) {
		mouseListeners = panel.getMouseListeners();
		motionListeners = panel.getMouseMotionListeners();
		
		for (int i=0; i<mouseListeners.length; i++)
			if (mouseListeners[i] != mouseListener)
				panel.removeMouseListener(mouseListeners[i]);
		
		for (int i=0; i<motionListeners.length; i++)
			if (motionListeners[i] != motionListener)
				panel.removeMouseMotionListener(motionListeners[i]);
	}
	
	protected void reenableOtherMouseListeners(MouseListener mouseListener, MouseMotionListener motionListener) {
		if (mouseListeners != null) {
			for (int i=0; i<mouseListeners.length; i++)
				if (mouseListeners[i] != mouseListener)
					panel.addMouseListener(mouseListeners[i]);
		}

		if (motionListeners != null) {
			for (int i=0; i<motionListeners.length; i++)
				if (motionListeners[i] != motionListener)
					panel.addMouseMotionListener(motionListeners[i]);
		}
	}
}
