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
import de.uni_trier.jane.tools.pathneteditor.objects.Target;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class TargetActionHandler extends AbstractActionHandler implements PathNetConstants {

	final private static Target TEMPLATE_TG = new Target("");
	final private static Color MOVING_COLOR = new Color(
			Settings.getColor(DEFAULT_TARGET_COLOR).getRed(),
			Settings.getColor(DEFAULT_TARGET_COLOR).getGreen(),
			Settings.getColor(DEFAULT_TARGET_COLOR).getBlue(),
			Settings.getInt(DEFAULT_DRAG_ALPHA)
	);
	
	private JToggleButton target_TB = new JToggleButton();
	
	protected MouseListener mouseListener = new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			// are we selected?
			if (!target_TB.isSelected()) return;
			
			// not left mouse click? do nothing
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			// get model coordinates
			Point vp = getModelCoords(e.getPoint());
			
			// create new waypoint at point vp
			model.add(new Target(vp.x, vp.y));			
		}
		
		public void mouseExited(MouseEvent e) {
			// clear temporary shapes
			panel.clearTemporaryShapes(true);
		}
	};
	
	protected MouseMotionListener motionListener = new MouseMotionListener() {
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}

		public void mouseMoved(MouseEvent e) {
			// are we selected?
			if (!target_TB.isSelected()) return;
			
			// get model coordinates and set them		
			TEMPLATE_TG.setPosition(getModelCoords(e.getPoint()));
			if (TEMPLATE_TG.getSymbolSize() != Settings.getInt(DEFAULT_TARGET_SIZE))
				TEMPLATE_TG.setSymbolSize(Settings.getInt(DEFAULT_TARGET_SIZE));
			
			// draw temp shape
			panel.clearTemporaryShapes(false);
			panel.addTemporaryShape(
					TEMPLATE_TG.getObjectShape(),
					MOVING_COLOR,					
					true,
					true
			);
		}		
	};
	
	public TargetActionHandler(PathNetModel model, GraphicsPanel panel) {
		super(model, panel);
		
		target_TB.setIcon(new ImageIcon("icons/Target.png"));
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
		return target_TB;
	}	
}
