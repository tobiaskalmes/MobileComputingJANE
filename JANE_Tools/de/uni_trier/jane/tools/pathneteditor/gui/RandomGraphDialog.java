package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.Pair;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class RandomGraphDialog extends JDialog implements PathNetConstants {

	private static final long serialVersionUID = 3256725091286070328L;

	/*
	 * Datastructure and constants
	 */
	private PathNetModel model;
	private static int DEFAULT_MODEL_WIDTH = 100000;
	private static int DEFAULT_MODEL_HEIGHT = 100000;
	
	/*
	 * The GUI components
	 */
	private static final int 	REMAINDER = GridBagConstraints.REMAINDER;
	
	// areas
	private JPanel		area_P = new JPanel();
	
    private JRadioButton   create_area_RB = new JRadioButton("create nonintersecting Areas", true);
    private JRadioButton   create_area_intersecting_RB = new JRadioButton("create Areas", true);
    private JRadioButton   create_area_none_RB = new JRadioButton("create no Areas", true);
	private JLabel		area_no_L = new JLabel("No of Areas");
	private JLabel		area_extends_w_L = new JLabel("Area width");
	private JLabel		area_extends_h_L = new JLabel("Area height");
	private JLabel		area_targets_no_L = new JLabel("Targets per Area");
	private JLabel		area_targets_size_L = new JLabel("Size of Targets");
	private JSpinner	area_min_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_max_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_min_extends_w_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_max_extends_w_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_min_extends_h_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_max_extends_h_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_min_targets_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_max_targets_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_min_targets_size_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	area_max_targets_size_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	
	// waypoints
	private JPanel		waypoint_P = new JPanel();
	//private JCheckBox	create_waypoint_CB = new JCheckBox("create Waypoints", true);
    private JRadioButton   create_waypoint_intersecting_RB = new JRadioButton("create Waypoints", true);
    private JRadioButton   create_waypoint_RB = new JRadioButton("create Waypoints outsite Areas", true);
    private JRadioButton   create_waypoint_none_RB = new JRadioButton("create no Waypoints", true);
	private JLabel		waypoint_no_L = new JLabel("No of Waypoints");
	private JLabel		waypoint_size_L = new JLabel("Size of Waypoints");
	private JSpinner	waypoint_min_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	waypoint_max_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	waypoint_min_size_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	waypoint_max_size_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	
	// edges
	private JPanel		edge_P = new JPanel();
	private JRadioButton edge_none_RB	= new JRadioButton("create no edges", false);
	private JRadioButton edge_planar_RB = new JRadioButton("create planar graph", false);
	private JRadioButton edge_custom_RB = new JRadioButton("create custom edges", true);
	private JLabel		edge_no_L = new JLabel("No of Edges");
	private JLabel		edge_no_ip_L = new JLabel("Innerpoints per Edge");
	private	JLabel		edge_size_ip_L = new JLabel("Size of Innerpoints");
	private JSpinner	edge_min_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	edge_max_no_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	edge_min_no_ip_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	edge_max_no_ip_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	edge_min_size_ip_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	private JSpinner	edge_max_size_ip_S = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
	
	// model size
	private JPanel		size_P = new JPanel();
	private JLabel		size_offset_L = new JLabel("Model offset");
	private JLabel		size_x_offset_L = new JLabel("X:");
	private JLabel		size_y_offset_L = new JLabel("Y:");
	private JLabel		size_extend_L = new JLabel("Model size");
	private JLabel		size_extend_w_L = new JLabel("width:");
	private JLabel		size_extend_h_L = new JLabel("height:");
	private JTextField	size_extend_x_TF = new JTextField();
	private JTextField	size_extend_y_TF = new JTextField();
	private JTextField	size_extend_w_TF = new JTextField();
	private JTextField	size_extend_h_TF = new JTextField();
	
	// buttons
	private JPanel		button_P = new JPanel();
	private JButton		create_B = new JButton("Create");
	private JButton		cancel_B = new JButton("Cancel");
	
		
	public RandomGraphDialog(PathNetModel model, Frame parent) {
		super(parent);
		this.model = model;
		init();
		
		setSize(370,780);
		setTitle("Create new random graph");
		setModal(true);
		
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((sd.width - getSize().width)/2, (sd.height - getSize().height)/2);
		
		presetValues();
	}
	
	private void init() {
		JPanel center_P = new JPanel();
		
		Container c = this.getContentPane();		
		c.setLayout(new BorderLayout());
		c.add(center_P, BorderLayout.CENTER);
		c.add(button_P, BorderLayout.SOUTH);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		center_P.setLayout(gbl);
		
		// size section
		size_P.setBorder(new TitledBorder("Model size"));
		
		doLayoutSizePanel();
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		set(gbl, gbc, REMAINDER, 1, 1.0, 1.0, size_P, center_P);
		
		// area section		
		area_P.setBorder(new TitledBorder("Areas"));
		doLayout(
				area_P,
				new JLabel[] {		area_no_L,		area_extends_w_L	,	area_extends_h_L,		area_targets_no_L,		area_targets_size_L },
				new JSpinner[] {	area_min_no_S,	area_min_extends_w_S,	area_min_extends_h_S,	area_min_targets_no_S,	area_min_targets_size_S },
				new JSpinner[] {	area_max_no_S,	area_max_extends_w_S,	area_max_extends_h_S,	area_max_targets_no_S,	area_max_targets_size_S }
		);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 5, 0, 5);		
		//set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_area_CB, center_P);
        set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_area_intersecting_RB, center_P);
        set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_area_RB, center_P);
        set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_area_none_RB, center_P);
        ButtonGroup bg = new ButtonGroup();
        bg.add(create_area_intersecting_RB);
        bg.add(create_area_RB);
        bg.add(create_area_none_RB);
        
		gbc.insets = new Insets(0, 20, 10, 5);
		gbc.fill = GridBagConstraints.BOTH;
		set(gbl, gbc, REMAINDER, 1, 1.0, 1.0, area_P, center_P);
		create_area_none_RB.addChangeListener(new ChangeListener() {
		//create_area_CB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean enable = !create_area_none_RB.isSelected();
				area_min_no_S.setEnabled(enable);
				area_min_extends_w_S.setEnabled(enable);
				area_min_extends_h_S.setEnabled(enable);
				area_min_targets_no_S.setEnabled(enable);
				area_min_targets_size_S.setEnabled(enable);
				area_max_no_S.setEnabled(enable);
				area_max_extends_w_S.setEnabled(enable);
				area_max_extends_h_S.setEnabled(enable);
				area_max_targets_no_S.setEnabled(enable);
				area_max_targets_size_S.setEnabled(enable);
				create_B.setEnabled(
						!create_area_none_RB.isSelected() || !create_waypoint_none_RB.isSelected()
				);
			}
		});
		
		//waypoint section		
		waypoint_P.setBorder(new TitledBorder("Waypoints"));
		doLayout(
				waypoint_P,
				new JLabel[] {		waypoint_no_L,		waypoint_size_L },
				new JSpinner[] {	waypoint_min_no_S,	waypoint_min_size_S },
				new JSpinner[] {	waypoint_max_no_S,	waypoint_max_size_S }
		);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 5, 0, 5);		
		
        set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_waypoint_intersecting_RB, center_P);
        set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_waypoint_RB, center_P);
        set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, create_waypoint_none_RB, center_P);
        bg = new ButtonGroup();
        bg.add(create_waypoint_RB);
        bg.add(create_waypoint_none_RB);
        bg.add(create_waypoint_intersecting_RB);
		gbc.insets = new Insets(0,20, 10, 5);
		gbc.fill = GridBagConstraints.BOTH;
		set(gbl, gbc, REMAINDER, 1, 1.0, 1.0, waypoint_P, center_P);
		
		create_waypoint_none_RB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean enable = !create_waypoint_none_RB.isSelected();
				waypoint_min_no_S.setEnabled(enable);
				waypoint_max_no_S.setEnabled(enable);
				waypoint_min_size_S.setEnabled(enable);
				waypoint_max_size_S.setEnabled(enable);
				create_B.setEnabled(
						!create_area_none_RB.isSelected() || !create_waypoint_none_RB.isSelected()
				);
			}
		});
		
		// edge section
		edge_P.setBorder(new TitledBorder("Edges"));
		doLayout(
				edge_P,
				new JLabel[] {		edge_no_L,	edge_no_ip_L,	edge_size_ip_L },
				new JSpinner[] {	edge_min_no_S,	edge_min_no_ip_S,	edge_min_size_ip_S },
				new JSpinner[] {	edge_max_no_S,	edge_max_no_ip_S,	edge_max_size_ip_S }
		);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 5, 0, 5);		
		set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, edge_none_RB, center_P);
		set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, edge_planar_RB, center_P);
		set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, edge_custom_RB, center_P);
		
		bg = new ButtonGroup();
		bg.add(edge_none_RB);
		bg.add(edge_planar_RB);
		bg.add(edge_custom_RB);
		
		gbc.insets = new Insets(0, 20, 10, 5);
		gbc.fill = GridBagConstraints.BOTH;
		set(gbl, gbc, REMAINDER, REMAINDER, 1.0, 1.0, edge_P, center_P);
		
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean enable = edge_custom_RB.isSelected();
				edge_min_no_S.setEnabled(enable);
				edge_min_no_ip_S.setEnabled(enable);
				edge_min_size_ip_S.setEnabled(enable);
				edge_max_no_S.setEnabled(enable);
				edge_max_no_ip_S.setEnabled(enable);
				edge_max_size_ip_S.setEnabled(enable);
				create_B.setEnabled(
						create_area_none_RB.isSelected() || !create_waypoint_none_RB.isSelected()
				);
			}
		};

		edge_none_RB.addChangeListener(cl);
		edge_planar_RB.addChangeListener(cl);
		edge_custom_RB.addChangeListener(cl);
		
		// buttons
		button_P.setLayout(new GridLayout(1, 2, 30, 0));
		button_P.add(create_B);
		button_P.add(cancel_B);
		
		create_B.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createGraph();
				setVisible(false);
			}
		});
		
		cancel_B.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}
	
	private void set(GridBagLayout gbl, GridBagConstraints gbc, int gw, int gh,
			double wx, double wy, Component c, Container cont) {
		gbc.gridwidth  = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}
	
	private void doLayoutSizePanel() {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		size_P.setLayout(gbl);
		
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		set(gbl, gbc, 1, 1, .5, 0.0, size_offset_L, size_P);
		set(gbl, gbc, 1, 1, .25, 0.0, size_x_offset_L, size_P);
		set(gbl, gbc, REMAINDER, 1, .25, 0.0, size_extend_x_TF, size_P);
		
		gbc.insets = new Insets(5, 5, 0, 5);
		set(gbl, gbc, 1, 1, .5, 0.0, new JLabel(""), size_P);
		set(gbl, gbc, 1, 1, .25, 0.0, size_y_offset_L, size_P);
		set(gbl, gbc, REMAINDER, 1, .25, 0.0, size_extend_y_TF, size_P);
		
		gbc.insets = new Insets(10, 5, 0, 5);
		set(gbl, gbc, 1, 1, .5, 0.0, size_extend_L, size_P);		
		set(gbl, gbc, 1, 1, .25, 0.0, size_extend_w_L, size_P);
		set(gbl, gbc, REMAINDER, 1, .25, 0.0, size_extend_w_TF, size_P);
		
		gbc.insets = new Insets(5, 5, 0 ,5);
		set(gbl, gbc, 1, 1, .5, 0.0, new JLabel(""), size_P);
		set(gbl, gbc, 1, 1, .25, 0.0, size_extend_h_L, size_P);
		set(gbl, gbc, REMAINDER, REMAINDER, .25, 0.0, size_extend_h_TF, size_P);
	}
	
	private void doLayout(JPanel panel, JLabel[] labels, JSpinner[] mins, JSpinner[] maxs) {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		panel.setLayout(gbl);
		
		gbc.insets = new Insets(5,5,0,5);

		set(gbl, gbc, 1, 1, .5, 0.0, new JLabel(""), panel);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		set(gbl, gbc, 1, 1, 0.25, 0.0, new JLabel("min"), panel);
		set(gbl, gbc, REMAINDER, 1, 0.25, 0.0, new JLabel("max"), panel);
		
		for (int i=0; i<labels.length; i++) {
			JLabel label = (labels[i] == null ? new JLabel() : labels[i]);
			final JSpinner min = (mins[i] == null ? new JSpinner() : mins[i]);
			final JSpinner max = (maxs[i] == null ? new JSpinner() : maxs[i]);
			
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			set(gbl, gbc, 1, 1, .5, 0.0, label, panel);			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			set(gbl, gbc, 1, 1, 0.25, 0.0, min, panel);
			set(gbl, gbc, REMAINDER, ( i==labels.length - 1 ? REMAINDER : 1), 0.25, 0.0, max, panel);
			
			min.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					int min_val = ((Integer)(min.getValue())).intValue();
					int max_val = ((Integer)(max.getValue())).intValue();
					max.setValue( max_val < min_val ? new Integer(min_val) : new Integer(max_val) );
				}
			});
			
			max.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					int min_val = ((Integer)(min.getValue())).intValue();
					int max_val = ((Integer)(max.getValue())).intValue();
					min.setValue( max_val < min_val ? new Integer(max_val) : new Integer(min_val) );
				}
			});
		}
	}
	
	private void presetValues() {
		// size section
		size_extend_x_TF.setText("0");
		size_extend_y_TF.setText("0");
		size_extend_w_TF.setText("" + DEFAULT_MODEL_WIDTH);
		size_extend_h_TF.setText("" + DEFAULT_MODEL_HEIGHT);
		
		// area section
		area_min_no_S.setValue(new Integer(1));
		area_max_no_S.setValue(new Integer(3));
		area_min_extends_w_S.setValue(new Integer((int)(DEFAULT_MODEL_WIDTH * .01)));
		area_max_extends_w_S.setValue(new Integer((int)(DEFAULT_MODEL_WIDTH * .02)));
		area_min_extends_h_S.setValue(new Integer((int)(DEFAULT_MODEL_HEIGHT * .01)));
		area_max_extends_h_S.setValue(new Integer((int)(DEFAULT_MODEL_HEIGHT * .02)));	
		area_min_targets_no_S.setValue(new Integer(1));
		area_max_targets_no_S.setValue(new Integer(5));
		area_min_targets_size_S.setValue(new Integer(Settings.getInt(DEFAULT_TARGET_SIZE)));
		area_max_targets_size_S.setValue(new Integer(Settings.getInt(DEFAULT_TARGET_SIZE)));
				
		// waypoint section
		waypoint_min_no_S.setValue(new Integer(5));
		waypoint_max_no_S.setValue(new Integer(10));
		waypoint_min_size_S.setValue(new Integer(Settings.getInt(DEFAULT_WAYPOINT_SIZE)));
		waypoint_max_size_S.setValue(new Integer(Settings.getInt(DEFAULT_WAYPOINT_SIZE)));
		
		// edge section
		edge_min_no_S.setValue(new Integer(10));
		edge_max_no_S.setValue(new Integer(20));
		edge_min_no_ip_S.setValue(new Integer(0));
		edge_max_no_ip_S.setValue(new Integer(2));
		edge_min_size_ip_S.setValue(new Integer((int)(Settings.getDouble(DEFAULT_INNER_POINT_SIZE))));		
		edge_max_size_ip_S.setValue(new Integer((int)(Settings.getDouble(DEFAULT_INNER_POINT_SIZE))));
	}
	
	private void createGraph() {
		Rectangle size = new Rectangle(
				getValue(size_extend_x_TF),
				getValue(size_extend_y_TF),
				getValue(size_extend_w_TF),
				getValue(size_extend_h_TF)
		);
		
		Pair area_no 		= new Pair( getValue(area_min_no_S), 			getValue(area_max_no_S) );
		Pair target_no		= new Pair( getValue(area_min_targets_no_S),	getValue(area_max_targets_no_S) );
		Pair area_width		= new Pair( getValue(area_min_extends_w_S),		getValue(area_max_extends_w_S) );
		Pair area_height	= new Pair( getValue(area_min_extends_h_S),		getValue(area_max_extends_h_S) );
		Pair target_size	= new Pair( getValue(area_min_targets_size_S),	getValue(area_max_targets_size_S) );
		
		Pair waypoint_no	= new Pair( getValue(waypoint_min_no_S),		getValue(waypoint_max_no_S) );
		Pair waypoint_size	= new Pair( getValue(waypoint_min_size_S), 		getValue(waypoint_max_size_S) );
		
		Pair edges_no		= new Pair( getValue(edge_min_no_S),			getValue(edge_max_no_S) );
		Pair edges_ip_no	= new Pair( getValue(edge_min_no_ip_S),			getValue(edge_max_no_ip_S) );
		Pair edges_ip_size	= new Pair( getValue(edge_min_size_ip_S),		getValue(edge_max_size_ip_S) );
		
		// apply checkboxes
		if (create_area_none_RB.isSelected()) {
			area_no = new Pair(0, 0);
			target_no = new Pair(0, 0);			
		}
		
		if (create_waypoint_none_RB.isSelected()) {
			waypoint_no = new Pair(0, 0);			
		}
		
		if (!edge_custom_RB.isSelected()) {
			edges_no = new Pair(0, 0);			
		}
		
		// create graph
		 PathNetTools.createGraph(
				model,
				size,
				area_no,create_area_intersecting_RB.isSelected(), target_no, 
                waypoint_no,create_waypoint_intersecting_RB.isSelected(),
                edges_no, edges_ip_no,
				area_width, area_height, target_size, waypoint_size, edges_ip_size
		 );
		
		// add planar edges to model
		if (edge_planar_RB.isSelected())
			PathNetTools.addPlanarEdges(model);
	}
	
	private int getValue(JTextField tf) {
		try {
			return Integer.parseInt(tf.getText());
		} catch (NumberFormatException e) {
			System.err.println("Illegal number in textfield, text: "+tf.getText()+": assuming value 0");
		}
		
		return 0;
	}
	
	private int getValue(JSpinner sp) {
		return ((Integer)(sp.getValue())).intValue();
	}
	
	public static void main(String[] args) {
		PathNetModel model = new DefaultPathNetModel();
		RandomGraphDialog main = new RandomGraphDialog(model, null);
		main.setVisible(true);
	}
}
