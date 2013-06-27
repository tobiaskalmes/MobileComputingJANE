package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.*;
import de.uni_trier.jane.tools.pathneteditor.gui.handler.*;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class PathNetPanel extends JPanel implements PathNetConstants {
	static final long serialVersionUID = 8774353965355923236L;
	
	// gui
	private JToolBar	  create_TB = new JToolBar(JToolBar.VERTICAL);
	private JToggleButton info_TB = new JToggleButton();
	private JToggleButton create_waypoint_TB = new JToggleButton();
	private JToggleButton create_target_TB = new JToggleButton();
	private JToggleButton create_edge_TB = new JToggleButton();
	private JToggleButton create_area_TB = new JToggleButton();
	
	private JToggleButton label_wp_TB, label_tg_TB, label_ed_TB, label_area_TB;
	
	private SpinnerChangeListener width_change_listener = new SpinnerChangeListener();
	
	private JToolBar	size_TB = new JToolBar(JToolBar.HORIZONTAL);
	// target width spinner
	private SpinnerNumberModel target_width_model = new SpinnerNumberModel( Settings.getInt(PathNetConstants.DEFAULT_TARGET_SIZE), // value
			1, //min
			Settings.getInt(PathNetConstants.MAX_SYMBOL_SIZE), //max
			1 // step size
	);
	private JSpinner	target_width = new JSpinner(target_width_model);
	
	// waypoint width spinner
	private SpinnerNumberModel waypoint_width_model = new SpinnerNumberModel( Settings.getInt(PathNetConstants.DEFAULT_WAYPOINT_SIZE), // value
			1, //min
			Settings.getInt(PathNetConstants.MAX_SYMBOL_SIZE), //max
			1 // step size
	);
	private JSpinner	waypoint_width = new JSpinner(waypoint_width_model);
	
	// edge inner point size spinner
	private SpinnerNumberModel edge_ip_size_model = new SpinnerNumberModel( (int)(Settings.getDouble(PathNetConstants.DEFAULT_INNER_POINT_SIZE)), // value
			1, //min
			Settings.getInt(PathNetConstants.MAX_SYMBOL_SIZE), //max
			1 // step size
	);
	private JSpinner	edge_ip_size = new JSpinner(edge_ip_size_model);
	
	
	
	// datastructure
	private GraphicsPanel gp;
	private PathNetModel model;
	private SelectionHandler selectionHandler;
	private ContextMenu cm;	
	private ContextAction ca;
		
	public PathNetPanel(final PathNetModel model) {
		
		this.model = model;
		gp = new GraphicsPanel(model);

		/*
		 * Register a new SelectionHandler for this PathNetPanel 
		 */
		selectionHandler = new SelectionHandler(gp);
		
		/*
		 * Create containers for context menu and context action 
		 */
		cm = new ContextMenu(gp);
		ca = new ContextAction(gp);
		
		/*
		 * Area action handlers need to be created here
		 */
		//AreaActionHandler areaActionHandler = new AreaActionHandler(model, gp, cm);
		SimpleAreaActionHandler simpleAreaActionHandler = new SimpleAreaActionHandler(model, gp, cm, selectionHandler);
		
		/*
		 * create contex menu handlers
		 */
		cm.addContextHandler(new SelectionContextHandler(gp));
		cm.addContextHandler(new WaypointAndTargetContextHandler(gp));
		cm.addContextHandler(new EdgeContextHandler(gp));
		cm.addContextHandler(new AreaContextHandler(gp, simpleAreaActionHandler));		
		
		/*
		 * create context actions and its handlers
		 */
		ca = new ContextAction(gp);		
		ca.addContextActionHandler(new InfoActionHandler());
		ca.addContextActionHandler(new WaypointActionHandler(model, gp));
		//ca.addContextActionHandler(new TargetActionHandler(model, gp));
		ca.addContextActionHandler(new EdgeActionHandler(model, gp));				
		//ca.addContextActionHandler(areaActionHandler);
		ca.addContextActionHandler(simpleAreaActionHandler);
		ca.addContextActionHandler(new MovePathNetObjectContextAction(gp, model, cm));
		
		/*
		 * build toolbar (tools)
		 */
		ButtonGroup bg = new ButtonGroup();
		JToggleButton[] buttons = ca.getButtons();
		for (int i=0; i<buttons.length; i++) {
			create_TB.add(buttons[i]);
			if (i==0 || i==3) create_TB.addSeparator();
			
			bg.add(buttons[i]);			
		}
		
		JButton button = new JButton(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/FitToPage.png"));
		button.setToolTipText("Zoom graph to fit in the viewable panel");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gp.zoomToPage(model);
			}
		});
		create_TB.add(button);
		
		create_TB.addSeparator(new Dimension(20, 20));
		create_TB.add(label_wp_TB = new JToggleButton(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Waypoint_Label.png"), false));
		create_TB.add(label_tg_TB = new JToggleButton(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Target_Label.png"), false));
		create_TB.add(label_ed_TB = new JToggleButton(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/Edge_Label.png"), Settings.getBoolean(SHOW_EDGE_LABELS)));
		create_TB.add(label_area_TB = new JToggleButton(new ImageIcon("de/uni_trier/jane/tools/pathneteditor/icons/SimpleArea_Label.png"), false));
		
		label_ed_TB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gp.showEdgeLabels(label_ed_TB.isSelected());
				Settings.set(SHOW_EDGE_LABELS, new Boolean(label_ed_TB.isSelected()));
			}
		});
		label_ed_TB.setToolTipText("Show labels for all edges");
		label_area_TB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gp.showAreaLabels(label_area_TB.isSelected());
				Settings.set(SHOW_AREA_LABELS, new Boolean(label_area_TB.isSelected()));
			}
		});
		label_area_TB.setToolTipText("Show labels for all areas");
		label_tg_TB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gp.showTargetLabels(label_tg_TB.isSelected());
				Settings.set(SHOW_TARGET_LABELS, new Boolean(label_tg_TB.isSelected()));
			}
		});
		label_tg_TB.setToolTipText("Show labels for all targets");
		label_wp_TB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gp.showWaypointLabels(label_wp_TB.isSelected());
				Settings.set(SHOW_WAYPOINT_LABELS, new Boolean(label_wp_TB.isSelected()));
			}
		});
		label_wp_TB.setToolTipText("Show labels for all waypoints");
		/*
		 * build toolbar (sizes)
		 */
		size_TB.add(getPanel("Target Size", target_width));
		size_TB.add(getPanel("Waypoint Size", waypoint_width));
		size_TB.add(getPanel("Edge Innerpt. Size", edge_ip_size));
		target_width.addChangeListener(width_change_listener);
		edge_ip_size.addChangeListener(width_change_listener);
		waypoint_width.addChangeListener(width_change_listener);
		
		
		this.setLayout(new BorderLayout());
		this.add(gp, BorderLayout.CENTER);
		this.add(create_TB, BorderLayout.WEST);
		this.add(size_TB, BorderLayout.NORTH);
	}
	
	/**
	 * updates the settings according to the specified values in the spinners
	 * @param source
	 */
	private void updateWidth() {
		Settings.set(PathNetConstants.DEFAULT_INNER_POINT_SIZE, new Double(edge_ip_size_model.getNumber().intValue()));
		Settings.set(PathNetConstants.DEFAULT_WAYPOINT_SIZE, new Integer(waypoint_width_model.getNumber().intValue()));
		Settings.set(PathNetConstants.DEFAULT_TARGET_SIZE, new Integer(target_width_model.getNumber().intValue()));
	}
	
	private JPanel getPanel(String label, JSpinner spinner) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("   " + label + "   "), BorderLayout.WEST);
		panel.add(spinner, BorderLayout.CENTER);
		return panel;
	}
	
	public void update() {
		gp.update();
	}

	public void zoomToPage(DefaultPathNetModel dpnm) {
		gp.zoomToPage(dpnm);
	}
	
	public GraphicsPanel getGraphicsPanel() {
		return gp;
	}
	
	private class SpinnerChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			updateWidth();
		}
	}
}
