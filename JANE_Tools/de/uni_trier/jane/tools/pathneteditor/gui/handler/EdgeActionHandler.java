package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * @author steffen
 *
 */
public class EdgeActionHandler extends AbstractActionHandler implements PathNetConstants {
	
	final private static Waypoint	SOURCE = new Waypoint("");
	final private static Waypoint	TARGET = new Waypoint("");
	final private static Edge		TEMPLATE_EDGE = new Edge(SOURCE, TARGET);
	
	final private static Color		MOVING_COLOR = new Color(
		Settings.getColor(DEFAULT_EDGE_COLOR).getRed(),
		Settings.getColor(DEFAULT_EDGE_COLOR).getGreen(),
		Settings.getColor(DEFAULT_EDGE_COLOR).getBlue(),
		Settings.getInt(DEFAULT_DRAG_ALPHA)	
	);
	
	private JToggleButton	edge_TB = new JToggleButton();
	private Waypoint		sourceWaypoint = null;
	private boolean			dragInProgress = false;			
		
	private MouseListener mouseListener = new MouseAdapter() {	
		
		public void mouseClicked(MouseEvent e) {
			// do nothing yet
		}

		public void mousePressed(MouseEvent e) {
			// are we selected?
			if (!edge_TB.isSelected()) return;
			
			// Maybe we want to create edge via drag'n'drop.
			// Get object below cursor
			PathNetObject object = PathNetTools.getObjectAtRealPoint(model, getModelCoords(e.getPoint()));
			if (object == null) return;
			
			// Edges can only be created from waypoints to waypoints. check object.
			switch (object.getObjectType()) {
				case PathNetConstants.TARGET:
				case WAYPOINT:
					sourceWaypoint = (Waypoint)object;
					SOURCE.setPosition(sourceWaypoint.getPosition());
					break;
				default:
					sourceWaypoint = null;					
			}			
		}

		public void mouseReleased(MouseEvent e) {
			// are we selected?
			if (!edge_TB.isSelected()) return;

			// Get object below cursor
			PathNetObject object = PathNetTools.getObjectAtRealPoint(model, getModelCoords(e.getPoint()));
			
			// clear temp shapes
			panel.clearTemporaryShapes(true);
			
			// no object below cursor? reset data and return
			if (object == null) {
				sourceWaypoint = null;
				dragInProgress = false;
				return;
			}
			
			// Edges can only be created from waypoints to waypoints. check object.
			switch (object.getObjectType()) {
				case PathNetConstants.TARGET:
				case WAYPOINT:
					// drag'n'drop mode? then create edge if object is valid
					if (dragInProgress && sourceWaypoint != object) {
						// get target waypoint
						Waypoint targetWaypoint = (Waypoint)object;
						
						// add to model
						Edge edge = new Edge(sourceWaypoint, targetWaypoint);
						model.add(edge);
						
						// first edge? then create standard probs for edge
						Target[] targets = model.getTargets();
						if (model.getEdges(sourceWaypoint).length == 1) {
							for (int i=0; i<targets.length; i++)
								model.setProb(sourceWaypoint, targets[i], edge, 1.0);							
						}
						if (model.getEdges(targetWaypoint).length == 1) {							
							for (int i=0; i<targets.length; i++)
								model.setProb(targetWaypoint, targets[i], edge, 1.0);							
						}
					}
					break;
				default:
					// do nothing					
			}
			
			// reset data
			sourceWaypoint = null;
			dragInProgress = false;
		}

		public void mouseEntered(MouseEvent e) {
			// do nothing			
		}

		public void mouseExited(MouseEvent e) {
			// clear temp shapes
			if (edge_TB.isSelected()) panel.clearTemporaryShapes(true);
		}
		
	};
	
	private MouseMotionListener motionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
			// are we selected ?
			if (!edge_TB.isSelected()) return;
			
			// is source valid?
			if (sourceWaypoint == null) return;
		
			// create temp edge by moving the TARGET
			TARGET.setPosition(getModelCoords(e.getPoint()));
			
			// draw temp line
			panel.clearTemporaryShapes(false);
			panel.addTemporaryShape(
					TEMPLATE_EDGE.getObjectShape(),
					MOVING_COLOR,
					true,
					true					
			);
			
			// mark as drag in progress
			dragInProgress = true;
		}

		public void mouseMoved(MouseEvent e) {
			// do nothing yet
		}
		
	};
	
	public EdgeActionHandler(PathNetModel model, GraphicsPanel panel) {
		super(model, panel);
		
		edge_TB.setIcon(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Edge.png"));
		edge_TB.setToolTipText("Creates a new edge between two waypoints");
	}
	/* (non-Javadoc)
	 * @see pathneteditor.gui.handler.ContextActionHandler#registerListener(java.awt.Component)
	 */
	public void registerListener(Component c) {
		c.addMouseListener(mouseListener);
		c.addMouseMotionListener(motionListener);
	}

	/* (non-Javadoc)
	 * @see pathneteditor.gui.handler.ContextActionHandler#removeListener(java.awt.Component)
	 */
	public void removeListener(Component c) {
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(motionListener);
	}

	/* (non-Javadoc)
	 * @see pathneteditor.gui.handler.ContextActionHandler#getButton()
	 */
	public JToggleButton getButton() { return edge_TB; }

}
