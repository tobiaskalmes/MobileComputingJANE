package de.uni_trier.jane.tools.pathneteditor.gui.handler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.Waypoint;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class WaypointActionHandler extends AbstractActionHandler implements PathNetConstants {

	final private static Waypoint TEMPLATE_WP = new Waypoint("");
	final private static Color MOVING_COLOR = new Color(
			Settings.getColor(DEFAULT_WAYPOINT_COLOR).getRed(),
			Settings.getColor(DEFAULT_WAYPOINT_COLOR).getGreen(),
			Settings.getColor(DEFAULT_WAYPOINT_COLOR).getBlue(),
			Settings.getInt(DEFAULT_DRAG_ALPHA)
	);
	
	private JToggleButton waypoint_TB = new JToggleButton();
	
	protected MouseListener mouseListener = new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			// are we selected?
			if (!waypoint_TB.isSelected()) return;
			
			// not left mouse click? do nothing
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			// get model coordinates
			Point vp = getModelCoords(e.getPoint());
			
			// create new waypoint at point vp
			model.add(new Waypoint(vp.x, vp.y));			
		}
		
		public void mouseExited(MouseEvent e) {
			panel.clearTemporaryShapes(true);
		}
	};
	
	protected MouseMotionListener motionListener = new MouseMotionListener() {
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}

		public void mouseMoved(MouseEvent e) {
			// are we selected?
			if (!waypoint_TB.isSelected()) return;
			
			// get model coordinates and set them		
			TEMPLATE_WP.setPosition(getModelCoords(e.getPoint()));
			if (TEMPLATE_WP.getSymbolSize() != Settings.getInt(DEFAULT_WAYPOINT_SIZE))
				TEMPLATE_WP.setSymbolSize(Settings.getInt(DEFAULT_WAYPOINT_SIZE));
			
			// draw temp shape
			panel.clearTemporaryShapes(false);
			panel.addTemporaryShape(
					TEMPLATE_WP.getObjectShape(),
					MOVING_COLOR,					
					true,
					true
			);
		}		
	};
	
	public WaypointActionHandler(PathNetModel model, GraphicsPanel panel) {
		super(model, panel);
		
		waypoint_TB.setIcon(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Waypoint.png"));
		waypoint_TB.setToolTipText("Creates a new waypoint as a cross for edges");
	}
	
	public void registerListener(Component c) {
		c.addMouseListener(mouseListener);
		c.addMouseMotionListener(motionListener);
	}

	public void removeListener(Component c) {
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(motionListener);
	}
	
	public JToggleButton getButton() {
		return waypoint_TB;
	}	
}
