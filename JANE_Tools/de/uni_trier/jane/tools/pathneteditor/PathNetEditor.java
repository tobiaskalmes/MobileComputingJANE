package de.uni_trier.jane.tools.pathneteditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.converter.PathNetConverter;
import de.uni_trier.jane.tools.pathneteditor.gui.*;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;
import de.uni_trier.jane.tools.pathneteditor.tools.RoutingAlgorithms;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class PathNetEditor extends JFrame implements PathNetConstants {
	
	static final long serialVersionUID = -4221731704524111365L;
	
	private JMenuBar	main_MB = new JMenuBar();
	
	private JMenu		file_M = new JMenu("File");	
	private JMenuItem	new_MI = new JMenuItem("New model");
	private JMenuItem	save_MI = new JMenuItem("Save model...");
	private JMenuItem	load_MI = new JMenuItem("Load model...");	
	private JMenuItem	exit_MI = new JMenuItem("Exit");
	
	private JMenu		edit_M = new JMenu("Edit");
	private JMenuItem	selAreas_MI = new JMenuItem("Select all Areas");
	private JMenuItem	selTargets_MI = new JMenuItem("Select all Targets");
	private JMenuItem	selWaypoints_MI = new JMenuItem("Select all Waypoints");
	private JMenuItem	selEdges_MI = new JMenuItem("Select all Edges");
	private JMenuItem	selNone_MI = new JMenuItem("Select none");
	private JMenuItem	selAll_MI = new JMenuItem("Select all");
	private JMenuItem	apply_factor_MI = new JMenuItem("Apply factor");
	
	private JMenu		create_M = new JMenu("Create");
	private JMenuItem	graph_MI = new JMenuItem("Create random graph...");
	private JMenuItem	planar_edges_MI = new JMenuItem("Add planar edges to graph");
	
	private JMenu		view_M = new JMenu("View");
	private JCheckBoxMenuItem	view_grid_MI = new JCheckBoxMenuItem("View Grid", Settings.getBoolean(DRAW_GRID));
	private JCheckBoxMenuItem	view_ruler_MI = new JCheckBoxMenuItem("View Ruler", Settings.getBoolean(DRAW_METER_RULE));
	private JCheckBoxMenuItem	view_zero_lines_MI = new JCheckBoxMenuItem("View Axis", Settings.getBoolean(DRAW_ZERO_LINES));
	private JCheckBoxMenuItem	view_coordinates_MI = new JCheckBoxMenuItem("View Coordinates", Settings.getBoolean(DRAW_COORDINATE_SYSTEM));
	private JCheckBoxMenuItem	auto_zoom_MI = new JCheckBoxMenuItem("Auto Zoom On Load", Settings.getBoolean(AUTO_ZOOM_ON_LOAD));
	
	private JMenu		tools_M = new JMenu("Tools");
	private JMenuItem	converter_MI = new JMenuItem("PathNet File-Converter");
	private JMenuItem	routing_MI = new JMenuItem("Run Dijkstra Algorithm (Shortest Path)");
	private JMenuItem	routingWeighted_MI = new JMenuItem("Run Dijkstra Algorithm (Weighted Path)");
	
	private JMenu		help_M = new JMenu("Help");
	private JMenuItem	about_MI = new JMenuItem("About PathNetEditor...");
	
	private JPanel		graphics_P = new JPanel();
		
	private JSplitPane table_graphics_SP = new JSplitPane();
	private JPanel		table_messages_P = new JPanel();
	
	private DefaultPathNetModel dpnm = createModel(); 
	private PathNetPanel gp = new PathNetPanel(dpnm);
	private PathNetLoader loader;
	private RandomGraphDialog randomGraphDialog = null;
	private ApplyFactorDialog apply_factor_dialog = null;
	
	public PathNetEditor() {
		super();
				
		init();
		
		loader = new PathNetLoader(this, dpnm);
		
		setTitle("[unnamed]");
		this.setSize(new Dimension(1024,768));
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}
	
	private void init() {
		final ModelSettingsPanel msp = new ModelSettingsPanel(dpnm);
		final MessagesPanel messages_P = new MessagesPanel(dpnm);
		
		/* The menubar */
		this.setJMenuBar(main_MB);
		main_MB.add(file_M);
		file_M.add(new_MI);
		file_M.addSeparator();		
		file_M.add(load_MI);
		file_M.add(save_MI);
		file_M.addSeparator();
		file_M.add(exit_MI);
		
		main_MB.add(edit_M);
		edit_M.add(selAreas_MI);
		edit_M.add(selTargets_MI);
		edit_M.add(selWaypoints_MI);
		edit_M.add(selEdges_MI);
		edit_M.addSeparator();
		edit_M.add(selAll_MI);
		edit_M.add(selNone_MI);
		edit_M.addSeparator();
		edit_M.add(apply_factor_MI);
		
		main_MB.add(create_M);
		create_M.add(graph_MI);
		create_M.addSeparator();
		create_M.add(planar_edges_MI);
		
		main_MB.add(view_M);
		view_M.add(view_grid_MI);
		view_M.add(view_ruler_MI);
		view_M.add(view_zero_lines_MI);
		view_M.add(view_coordinates_MI);
		view_M.addSeparator();
		view_M.add(auto_zoom_MI);

		main_MB.add(tools_M);
		tools_M.add(converter_MI);
		tools_M.add(routing_MI);
		tools_M.add(routingWeighted_MI);
		
		main_MB.add(help_M);
		help_M.add(about_MI);
		
		new_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						dpnm.clearModelData();
						setTitle("[unnamed]");
					}
				});				
			}
		});
		
		load_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loader.loadModel(dpnm);
				if (Settings.getBoolean(AUTO_ZOOM_ON_LOAD))
					gp.zoomToPage(dpnm);
				setTitle(dpnm.getModelName() + " [" + loader.getFilename() + "]");
			}
		});
		
		selAreas_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				dpnm.getSelectionModel().setEventsEnabled(false);
				
				dpnm.getSelectionModel().clear();
				Area[] a = dpnm.getAreas();
				for (int i=0; i<a.length; i++)
					dpnm.getSelectionModel().add(a[i]);
				
				dpnm.getSelectionModel().setEventsEnabled(true);
			}	
		});
		
		selTargets_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dpnm.getSelectionModel().setEventsEnabled(false);
				
				dpnm.getSelectionModel().clear();
				Target[] t = dpnm.getTargets();
				for (int i=0; i<t.length; i++)
					dpnm.getSelectionModel().add(t[i]);
				
				dpnm.getSelectionModel().setEventsEnabled(true);
			}	
		});
		
		selWaypoints_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				dpnm.getSelectionModel().setEventsEnabled(false);
				
				dpnm.getSelectionModel().clear();
				Waypoint[] w = dpnm.getWaypoints();
				for (int i=0; i<w.length; i++)
					dpnm.getSelectionModel().add(w[i]);
				
				dpnm.getSelectionModel().setEventsEnabled(true);
			}	
		});
		
		selEdges_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {			
				dpnm.getSelectionModel().setEventsEnabled(false);
				
				dpnm.getSelectionModel().clear();
				Edge[] e = dpnm.getEdges();
				for (int i=0; i<e.length; i++)
					dpnm.getSelectionModel().add(e[i]);
				
				dpnm.getSelectionModel().setEventsEnabled(true);
			}	
		});
		
		selNone_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				dpnm.getSelectionModel().clear();
			}	
		});
		
		selAll_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dpnm.getSelectionModel().setEventsEnabled(false);
				
				dpnm.getSelectionModel().clear();
				PathNetObject[] o = dpnm.getAllObjects();
				for (int i=0; i<o.length; i++)
					dpnm.getSelectionModel().add(o[i]);
				
				dpnm.getSelectionModel().setEventsEnabled(true);
			}	
		});
		
		save_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loader.saveModel(dpnm);
				setTitle(dpnm.getModelName() + " [" + loader.getFilename() + "]");
			}
		});
		
		exit_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		graph_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dpnm.clearModelData();
				
				dpnm.enableModelEvents(false);				
//				createGraph(dpnm, 30, 10, 40, 50, false);
				
				if (randomGraphDialog==null)
					randomGraphDialog = new RandomGraphDialog(dpnm, PathNetEditor.this);
				
				randomGraphDialog.setVisible(true);
								
				dpnm.enableModelEvents(true);
				
				gp.getGraphicsPanel().zoomToPage(dpnm);
				gp.update();
			}
		});

		planar_edges_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {						
				dpnm.enableModelEvents(false);
				
				PathNetTools.addPlanarEdges(dpnm);
				
				dpnm.enableModelEvents(true);				
			}
		});
		
		apply_factor_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (apply_factor_dialog==null)
					apply_factor_dialog = new ApplyFactorDialog(PathNetEditor.this, dpnm, gp);
				apply_factor_dialog.setVisible(true);
			}
		});
		
		view_grid_MI.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				Settings.set(DRAW_GRID, new Boolean(view_grid_MI.isSelected()));
				gp.getGraphicsPanel().showGrid(view_grid_MI.isSelected());
				gp.update();
			}
		});
		
		view_coordinates_MI.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				Settings.set(DRAW_COORDINATE_SYSTEM, new Boolean(view_coordinates_MI.isSelected()));
				gp.getGraphicsPanel().showCoordinateSystem(view_coordinates_MI.isSelected());				
				gp.update();
			}
		});
		
		view_ruler_MI.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				Settings.set(DRAW_METER_RULE, new Boolean(view_ruler_MI.isSelected()));
				gp.getGraphicsPanel().showMeterRule(view_ruler_MI.isSelected());
				gp.update();
			}
		});
		
		view_zero_lines_MI.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				Settings.set(DRAW_ZERO_LINES, new Boolean(view_zero_lines_MI.isSelected()));
				gp.getGraphicsPanel().showZeroLines(view_zero_lines_MI.isSelected());
				gp.update();
			}
		});
		
		auto_zoom_MI.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				Settings.set(AUTO_ZOOM_ON_LOAD, new Boolean(auto_zoom_MI.isSelected()));
				gp.getGraphicsPanel().setAutoZoomOnLoad(auto_zoom_MI.isSelected());
			}
		});
		
		converter_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PathNetConverter converter = new PathNetConverter(PathNetEditor.this);
				converter.setVisible(true);
			}
		});
		
		routing_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dpnm.enableModelEvents(false);
				
				dpnm.clearProbs();
				RoutingAlgorithms.performDjikstra(dpnm);
				
				dpnm.enableModelEvents(true);
			}
		});
		
		routingWeighted_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dpnm.enableModelEvents(false);
				
				dpnm.clearProbs();
				RoutingAlgorithms.performWeightedDijkstra(dpnm);
				
				dpnm.enableModelEvents(true);
			}
		});
		
		about_MI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutDialog about = new AboutDialog(PathNetEditor.this);
				about.setVisible(true);
			}
		});
		
		graphics_P.setLayout(new BorderLayout());
		graphics_P.add(gp, BorderLayout.CENTER);
					
		System.setErr(messages_P.getPrintStream(Settings.getColor(MESSAGES_DEFAULT_ERR_COLOR)));
		System.setOut(messages_P.getPrintStream(Settings.getColor(MESSAGES_DEFAULT_OUT_COLOR)));
		messages_P.writeMessage("Logging started: " + new Date() + "\n");
				
		table_messages_P.setLayout(new BorderLayout());
		table_messages_P.add(msp, BorderLayout.CENTER);
		table_messages_P.add(messages_P, BorderLayout.SOUTH);
		
		table_graphics_SP.setLeftComponent(table_messages_P);
		table_graphics_SP.setRightComponent(graphics_P);
		table_graphics_SP.setDividerLocation(350);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(table_graphics_SP);
	}
	
	private DefaultPathNetModel createModel() {
		DefaultPathNetModel dpnm = new DefaultPathNetModel();
		//addData(dpnm);
				
		return dpnm;
	}

	
	private void close() {
		// ask whether to save data...
		Settings.saveSettings();
		System.exit(0);
	}
	
	// overwritten methods
	public void setTitle(String title) {
		super.setTitle("PathNetEditor: " +title);
	}
	
	public static void main(String[] args) {
		PathNetEditor main = new PathNetEditor();
		main.setVisible(true);
	}

}
