package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.ContextMenu;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class MovePathNetObjectContextAction implements ContextActionHandler, MouseMotionListener, MouseListener {

	private boolean is_enabled = true;
	private JToggleButton button = new JToggleButton();
	private GraphicsPanel panel = null;
	private PathNetModel model = null;
	private PathNetObject dragged_object = null; // the original object to drag
	private PathNetObject dragged_object_copy = null; // the copy of the original object (to move...)
	private ContextMenu context_menu = null;
	private Point last_position = null; // used when dragging an area to determine the difference
	private int inner_point = -1; // the inner point that is dragged
	
	public MovePathNetObjectContextAction(GraphicsPanel panel, PathNetModel model, ContextMenu context_menu) {
		this.panel = panel;
		this.model = model;
		this.context_menu = context_menu;
		
		button.setIcon(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Move.png"));
		button.setToolTipText("Moves a PathNetObject with drag'n'drop");
	}
	
	private boolean is_enabled() {
		return is_enabled && button.isSelected();
	}
	
	public void registerListener(Component c) {
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}

	public void removeListener(Component c) {
		c.removeMouseListener(this);
		c.removeMouseMotionListener(this);
	}

	public void setEnabled(boolean value) {
		is_enabled = value;
	}

	public boolean isEnabled() {
		return is_enabled;
	}

	public JToggleButton getButton() {
		return button;
	}

	public void mouseDragged(MouseEvent e) {
		if (dragged_object==null)
			return;
		switch (dragged_object.getObjectType()) {
		case PathNetConstants.EDGE:
			((Edge)dragged_object_copy).setInnerPointPosition(inner_point, PathNetTools.getRealPoint(panel, e.getPoint()));
			panel.clearTemporaryShapes(false);
			panel.addTemporaryShape(dragged_object_copy.getObjectShape(), getObjectColor(), true, true);
			break;
		case PathNetConstants.WAYPOINT:
			((Waypoint)dragged_object_copy).setPosition(PathNetTools.getRealPoint(panel, e.getPoint()));
			panel.clearTemporaryShapes(false);
			drawEdgesToWaypoint(e);
			panel.addTemporaryShape(dragged_object_copy.getObjectShape(), getObjectColor(), true, true);
			break;
		case PathNetConstants.TARGET:
			Area area = model.getArea((Target)dragged_object);
			((Waypoint)dragged_object_copy).setPosition(area.getNearestOutlinePoint(PathNetTools.getRealPoint(panel, e.getPoint())));
			panel.clearTemporaryShapes(false);
			drawEdgesToWaypoint(e);
			panel.addTemporaryShape(dragged_object_copy.getObjectShape(), getObjectColor(), true, true);
			break;
		case PathNetConstants.AREA:
			Point p = PathNetTools.getRealPoint(panel, e.getPoint());
			((Area)dragged_object_copy).translate(p.x - last_position.x , p.y - last_position.y);
			panel.clearTemporaryShapes(false);
			panel.addTemporaryShape(dragged_object_copy.getObjectShape(), getObjectColor(), true, true);
			last_position = p;
			break;
		}
	}
	
	private Color getObjectColor() {
		if (dragged_object==null)
			return Color.black;
		
		Color dc = null; // default color

		dc = dragged_object.getObjectColor();
		return new Color(dc.getRed(), dc.getGreen(), dc.getBlue(), Settings.getInt(PathNetConstants.DEFAULT_DRAG_ALPHA));
	}
	
	private Color getAlphaColor(Color ec, int alpha) {
		return new Color(ec.getRed(), ec.getGreen(), ec.getBlue(), alpha);
	}
	
	private void drawEdgesToWaypoint(MouseEvent e) {
		Edge[] edges = model.getEdges((Waypoint)dragged_object);
		Point p = PathNetTools.getRealPoint(panel, e.getPoint());
		int i=0;
		for (i=0; edges!=null && i<edges.length; i++) {			
			Edge copy = edges[i].clone((Waypoint)dragged_object, new Waypoint(p.x,p.y));
			panel.addTemporaryShape(copy.getObjectShape(), getAlphaColor(edges[i].getObjectColor(), Settings.getInt(PathNetConstants.DEFAULT_DRAG_ALPHA)), true, false);
		}
	}

	
	private void disableContextMenu() {
//		has_disabled_context_menu = context_menu.isEnabled();
		context_menu.setEnabled(false, this);
	}
	
	private void reenableContextMenu() {
//		if (has_disabled_context_menu)
			context_menu.setEnabled(true, this);
	}
	
	private void startDragging(Point p) {
		dragged_object = PathNetTools.getObjectAtRealPoint(model, PathNetTools.getRealPoint(panel, p));
		if (dragged_object==null)
			return;
		disableContextMenu();
		//dragged_object = current_object;
		switch(dragged_object.getObjectType()) {
			case PathNetConstants.WAYPOINT:
				dragged_object_copy = new Waypoint("");
				((Waypoint)dragged_object_copy).setSymbolSize( ((Waypoint)(dragged_object)).getSymbolSize() );
				break;
			case PathNetConstants.TARGET:
				// FIXME: just necessary if the target is not attached to an area (when create planar graphs...)
				if (model.getArea((Target)dragged_object)==null) {
					dragged_object = null;
					return;
				}
				dragged_object_copy = new Target("");
				((Target)dragged_object_copy).setSymbolSize( ((Target)(dragged_object)).getSymbolSize() );
				break;
			case PathNetConstants.AREA:
				dragged_object_copy = (PathNetObject) ((Area)dragged_object).clone();
				last_position = PathNetTools.getRealPoint(panel, p);
				break;
			case PathNetConstants.EDGE:
				inner_point = ((Edge)dragged_object).getInnerPoint(PathNetTools.getRealPoint(panel, p));
				if (inner_point!=-1)
					dragged_object_copy = (PathNetObject) ((Edge)dragged_object).clone();
				else
					dragged_object = null;
				break;
		}
		
	}
	
	/**
	 * called whenever dragging is done
	 * (aborted or finished)
	 */
	private void doneDragging() {
		panel.clearTemporaryShapes(true);
		dragged_object = null;
		reenableContextMenu();
	}
	
	/**
	 * called when successfully finished dragging
	 * @param mouse_position
	 */
	private void finishDragging(Point mouse_position) {
		switch(dragged_object.getObjectType()) {
			case PathNetConstants.TARGET:
				Area area = model.getArea((Target)dragged_object);
				((Waypoint)dragged_object).setPosition(area.getNearestOutlinePoint(PathNetTools.getRealPoint(panel, mouse_position)));
				panel.update();
				break;
			case PathNetConstants.WAYPOINT:
				((Waypoint)dragged_object).setPosition(PathNetTools.getRealPoint(panel, mouse_position));
				panel.update();
				break;
			case PathNetConstants.AREA:
				Point translation = ((Area)dragged_object).getTranslation((Area)dragged_object_copy);
				Target[] targets = model.getTargets((Area) dragged_object);
				for (int i=0; targets!=null && i<targets.length; i++) {
					Point p = targets[i].getPosition();
					p.translate(translation.x, translation.y);
					targets[i].setPosition(p);
				}
				((Area)dragged_object).translate(translation.x, translation.y);
				panel.update();
				break;
			case PathNetConstants.EDGE:
				((Edge)dragged_object).setInnerPointPosition(inner_point, PathNetTools.getRealPoint(panel, mouse_position));
				panel.update();
				break;
		}
		doneDragging();
	}
	
	/**
	 * called when dragging is aborted
	 *
	 */
	private void abortDragging() {
		doneDragging();
	}

	public void mousePressed(MouseEvent e) {
		if (!is_enabled())
			return;
		if (e.getButton()==MouseEvent.BUTTON3) { // abort dragging
			abortDragging();
			return;
		} else if (e.getButton()!=MouseEvent.BUTTON1) // invalid button
			return;

		startDragging(e.getPoint());
	}

	public void mouseReleased(MouseEvent e) {
		if (dragged_object==null)
			return;
		if (e.getButton()==MouseEvent.BUTTON1)
			finishDragging(e.getPoint());
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}


}
