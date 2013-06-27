/*
 * Created on Apr 19, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.SelectionModel;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PropertiesDialog extends JDialog implements PathNetConstants {
	
	private static final long serialVersionUID = 3257009847634244914L;
	private static final Color 	MULTIPLE_SELECTION_COLOR = Color.LIGHT_GRAY;
	private static final int	SYMBOL_SIZE_MIN	= 1;
	private static final int	SYMBOL_SIZE_MAX	= 1000;
	private static final int	SYMBOL_SIZE_STEP = SYMBOL_SIZE_MAX / 100;
	
//	private GraphicsPanel	panel;
	private PathNetModel	model;	
	
	private boolean			descrChanged = false;
	private boolean			colorChanged = false;
	private boolean			targetChanged = false;
	private boolean			waypointChanged = false;
	private boolean 		edgeChanged = false;
	
	// gui
	private static final int 	REMAINDER = GridBagConstraints.REMAINDER;
	private JPanel	generalNspecificNinfoP = new JPanel();
	private	JPanel	generalP			= new JPanel();		
	private JPanel	selectionInfoP				= new JPanel();
	private JTextField descriptionTF	= new JTextField();
	private ColorField	colorField		= new ColorField();
	private JButton	colorB				= new JButton(new ImageIcon("icons/ColorChooser.gif"));
	private JPanel	targetP				= new JPanel();
	private JSpinner	targetSizeSP	= new JSpinner();	
	private JPanel	waypointP			= new JPanel();
	private JSpinner	waypointSizeSP	= new JSpinner();	
	private JPanel	edgeP				= new JPanel();
	private JSpinner	edgeInnerPointWidthSP		= new JSpinner();
	private JLabel	areaCountL			= new JLabel();
	private JLabel	targetCountL		= new JLabel();
	private JLabel	waypointCountL		= new JLabel();
	private JLabel	edgeCountL			= new JLabel();
	private JLabel	allCountL			= new JLabel();
	private JPanel	buttonP				= new JPanel();
	private JButton	okB					= new JButton("OK");
	private JButton	applyB				= new JButton("Apply");
	private JButton cancelB				= new JButton("Cancel");
		
	// constructor
	public PropertiesDialog(PathNetModel model, Point p) {
//		this.panel = panel;
		this.model = model;

		this.setTitle("Properties");
		this.setModal(true);
		// this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setSize(new Dimension(270,450));
		
		this.setLocation(p);
		
		setProperties();
		init();		
	}
	
	private void init() {
		// the container
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(generalNspecificNinfoP, BorderLayout.CENTER);
		c.add(buttonP, BorderLayout.SOUTH);
		
		generalNspecificNinfoP.setLayout(new BoxLayout(generalNspecificNinfoP, BoxLayout.Y_AXIS));
		generalNspecificNinfoP.add(generalP);
		generalNspecificNinfoP.add(targetP);
		generalNspecificNinfoP.add(waypointP);
		generalNspecificNinfoP.add(edgeP);
		generalNspecificNinfoP.add(selectionInfoP);		
		
		/* **************************************		 
		 * General section
		 * **************************************/		 
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		generalP.setLayout(gbl);
		generalP.setBorder(
				new TitledBorder(
						BorderFactory.createLineBorder(Color.LIGHT_GRAY),
						"General settings",
						TitledBorder.LEFT,
						TitledBorder.TOP
				)
		);
				
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		set(gbl, gbc, 1, 1, 0.0, 0.0, new JLabel("Description:"), generalP);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		descriptionTF.setPreferredSize(new Dimension(100, 18));
		set(gbl, gbc, REMAINDER, 2, 1.0, 0.0, descriptionTF, generalP);
		
		gbc.fill = GridBagConstraints.NONE;
		set(gbl, gbc, 1, 1, 0.0, 0.0, new JLabel("Color:"), generalP);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		set(gbl, gbc, 1, 1, 1.0, 0.0, colorField, generalP);
		
		gbc.fill = GridBagConstraints.NONE;
		colorB.setPreferredSize(new Dimension(18, 18));
		set(gbl, gbc, REMAINDER, REMAINDER, 0.0, 0.0, colorB, generalP);
		
		descriptionTF.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				descrChanged = true;
			}
		});
				
		colorB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color selectedC = JColorChooser.showDialog(PropertiesDialog.this, "Select an object color", colorField.getColor());
				if (selectedC != null) {
					colorField.setColor(selectedC);
					colorChanged = true;
				}
			}
		});
		
		/* **********************************
		 * Target section
		 * **********************************/
		targetP.setBorder(
				new TitledBorder(
					BorderFactory.createLineBorder(Color.LIGHT_GRAY),
					"Targets",
					TitledBorder.LEFT,
					TitledBorder.TOP
				)
		);
		
		targetP.setLayout(new GridLayout(1, 2, 5, 10));
		targetP.add(new JLabel("Size:"));
		targetP.add(targetSizeSP);
		targetSizeSP.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				targetChanged = true;
			}
		});
		
		/* **********************************
		 * Waypoint section
		 * **********************************/
		waypointP.setBorder(
				new TitledBorder(
					BorderFactory.createLineBorder(Color.LIGHT_GRAY),
					"Waypoints",
					TitledBorder.LEFT,
					TitledBorder.TOP
				)
		);
		
		waypointP.setLayout(new GridLayout(1, 2, 5, 10));
		waypointP.add(new JLabel("Size:"));
		waypointP.add(waypointSizeSP);
		waypointSizeSP.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				waypointChanged = true;
			}
		});
		
		/* **********************************
		 * Edge section
		 * **********************************/
		edgeP.setBorder(
				new TitledBorder(
					BorderFactory.createLineBorder(Color.LIGHT_GRAY),
					"Edges",
					TitledBorder.LEFT,
					TitledBorder.TOP
				)
		);
				
		edgeP.setLayout(new GridLayout(1, 2, 5, 10));
		edgeP.add(new JLabel("InnerPoint Widths:"));
		edgeP.add(edgeInnerPointWidthSP);
		edgeInnerPointWidthSP.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				edgeChanged = true;
			}
		});
		
		/* **********************************
		 * Info section
		 * **********************************/
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		selectionInfoP.setLayout(gbl);
		selectionInfoP.setBorder(
				new TitledBorder(
						BorderFactory.createLineBorder(Color.LIGHT_GRAY),
						"Selection",
						TitledBorder.LEFT,
						TitledBorder.TOP
				)
		);
				
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		set(gbl, gbc, 1, 1, 0.0, 0.0, new JLabel("Areas"), selectionInfoP);
		gbc.anchor = GridBagConstraints.EAST;
		set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, areaCountL, selectionInfoP);
		
		gbc.anchor = GridBagConstraints.WEST;
		set(gbl, gbc, 1, 1, 0.0, 0.0, new JLabel("Targets:"), selectionInfoP);
		gbc.anchor = GridBagConstraints.EAST;
		set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, targetCountL, selectionInfoP);
		
		gbc.anchor = GridBagConstraints.WEST;
		set(gbl, gbc, 1, 1, 0.0, 0.0, new JLabel("Waypoints:"), selectionInfoP);
		gbc.anchor = GridBagConstraints.EAST;
		set(gbl, gbc, REMAINDER, 1, 1.0, 0.0, waypointCountL, selectionInfoP);
		
		gbc.anchor = GridBagConstraints.WEST;
		set(gbl, gbc, 1, 1, 0.0, 0.0, new JLabel("Edges:"), selectionInfoP);
		gbc.anchor = GridBagConstraints.EAST;
		set(gbl, gbc, REMAINDER, REMAINDER, 1.0, 0.0, edgeCountL, selectionInfoP);
		
		/* **********************************
		 * Button section
		 * **********************************/
		buttonP.setLayout(new GridLayout(1, 3, 5, 5));
		buttonP.add(okB);
		buttonP.add(applyB);
		buttonP.add(cancelB);
		
		okB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChanges();
				PropertiesDialog.this.setVisible(false);
			}
		});
		
		applyB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChanges();
			}
		});
		
		cancelB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PropertiesDialog.this.setVisible(false);
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
	
	private void setProperties() {
		SelectionModel selection = model.getSelectionModel();
			
		// count selected object types
		PathNetObject[] selected = selection.getSelectedObjects();
		int ac=0, tc=0, wc=0, ec=0;
		for (int i=0; i<selected.length; i++) {
			switch(selected[i].getObjectType()) {
				case PathNetObject.AREA:		ac++; break;
				case PathNetObject.TARGET:		tc++; break;
				case PathNetObject.WAYPOINT:	wc++; break;
				case PathNetObject.EDGE:		ec++; break;
			}
		}
		areaCountL.setText(""+ac);
		targetCountL.setText(""+tc);
		waypointCountL.setText(""+wc);
		edgeCountL.setText(""+ec);
		allCountL.setText(""+selected.length);
		
		// disable gui elements from unselected objects
		targetSizeSP.setEnabled(tc!=0);
		waypointSizeSP.setEnabled(wc!=0);
		edgeInnerPointWidthSP.setEnabled(ec!=0);		
		
		// no selection
		if (selected.length == 0)
			return;
		
		// single selection
		if (selection.getSelectionSize()==1) {
			PathNetObject obj = selected[0];
			colorField.setColor(obj.getObjectColor());
			descriptionTF.setText(obj.getDescription());
			switch(obj.getObjectType()) {
				case TARGET:
					targetSizeSP.setModel(new SpinnerNumberModel(((Target)obj).getSymbolSize(), SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP));
					break;
				case WAYPOINT:
					waypointSizeSP.setModel(new SpinnerNumberModel(((Waypoint)obj).getSymbolSize(), SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP));
					break;
				case EDGE:
					int ipSize = getEdgeInnerPointSize((Edge)obj);
					edgeInnerPointWidthSP.setModel(new SpinnerNumberModel(ipSize, SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP));
					break;
			}
			return;
		}
		
		// multiple selection
		PathNetObject ref = selected[0];
		
		// check for same color and description
		int sameDescr = 1, sameColor = 1;
		
		for (int i=1; i<selected.length; i++) {
			sameDescr += (ref.getDescription().equals(selected[i].getDescription())) ? 1 : 0;
			sameColor += (ref.getObjectColor().equals(selected[i].getObjectColor())) ? 1 : 0;			
		}
					
		// set if they are equal at all object, else grey it
		if (sameColor == selected.length)
			colorField.setColor(ref.getObjectColor());
		else
			colorField.setColor(MULTIPLE_SELECTION_COLOR);
		
		if (sameDescr == selected.length)
			descriptionTF.setText(ref.getDescription());
		else
			descriptionTF.setText("");
		
		// check for same target properties
		int refTargetSize = -1;
		for (int i=0; i<selected.length; i++) {
			if (selected[i].getObjectType() != TARGET)
				continue;
			
			if (refTargetSize == -1) {
				refTargetSize = ((Target)selected[i]).getSymbolSize();
				targetSizeSP.setModel(new SpinnerNumberModel(((Target)selected[i]).getSymbolSize(), SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP));
			}
			
			if (refTargetSize != ((Target)selected[i]).getSymbolSize()) {
				targetSizeSP.setValue(new Integer(SYMBOL_SIZE_MIN));
				break;
			}
		}
		
		// check for same waypoint properties
		int refWaypointSize = -1;
		for (int i=0; i<selected.length; i++) {
			if (selected[i].getObjectType() != WAYPOINT)
				continue;
			
			if (refWaypointSize == -1) {
				refWaypointSize = ((Waypoint)selected[i]).getSymbolSize();
				waypointSizeSP.setModel(new SpinnerNumberModel(((Waypoint)selected[i]).getSymbolSize(), SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP));
			}
			
			if (refWaypointSize != ((Waypoint)selected[i]).getSymbolSize()) {
				waypointSizeSP.setValue(new Integer(SYMBOL_SIZE_MIN));
				break;
			}
		}
		
		// check for same edge properties
		int refEdgeWidth = -1;
		for (int i=0; i<selected.length; i++) {
			if (selected[i].getObjectType() != EDGE)
				continue;
			
			if (refEdgeWidth == -1) {
				refEdgeWidth = getEdgeInnerPointSize((Edge)selected[i]);
				edgeInnerPointWidthSP.setModel(new SpinnerNumberModel(refEdgeWidth, SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP));
			}
			
			if (refEdgeWidth != getEdgeInnerPointSize((Edge)selected[i])) {
				edgeInnerPointWidthSP.setValue(new Integer(SYMBOL_SIZE_MIN));
				break;
			}
		}
	}
	
	private int getEdgeInnerPointSize(Edge e) {
		double[] sizes = e.getWidths();
		if (sizes.length < 3)
			return (int)(Settings.getDouble(DEFAULT_INNER_POINT_SIZE));
		
		double size = sizes[1];
		for (int i=2; i<sizes.length-1; i++) {
			if (sizes[i] != size) {
				size = 0;
				break;
			}
		}
		
		return (int)size;		
	}
	
	private void applyChanges() {
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(false);
		
		PathNetObject[] selected = model.getSelectionModel().getSelectedObjects();
		
		for (int i=0; i<selected.length; i++) {
			if (colorChanged)
				selected[i].setObjectColor(colorField.getColor());
			if (descrChanged)
				selected[i].setDescription(descriptionTF.getText());
			
			switch(selected[i].getObjectType()) {
				case TARGET:			
					if (targetChanged)
						((Target)selected[i]).setSymbolSize(((Integer)(targetSizeSP.getValue())).intValue());
					break;
				case WAYPOINT:
					if (waypointChanged)
						((Waypoint)selected[i]).setSymbolSize(((Integer)(waypointSizeSP.getValue())).intValue());
					break;
				case EDGE:
					if (edgeChanged) {
						Edge e = (Edge)selected[i];
						int newSize = ((Integer)(edgeInnerPointWidthSP.getValue())).intValue();
						for (int j=0; j<e.getInnerPointsSize(); j++)
							e.setWidth(j+1, newSize);
					}
					break;
			}
		}
		
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(true);
		
//		panel.update();
	}
	
	private class ColorField extends JPanel {
		private static final long serialVersionUID = 1L;

		public ColorField() {
			this.setPreferredSize(new Dimension(80,18));
			this.setBorder(new LineBorder(Color.BLACK));
			setColor(Color.GREEN);
		}
		
		public void setColor(Color c) {
			this.setBackground(c);			
		}
		
		public Color getColor() {
			return this.getBackground();
		}
	}	
	
	public static void main(String[] args) {
		GraphicsPanel panel = new GraphicsPanel(new DefaultPathNetModel());
		PropertiesDialog main = new PropertiesDialog(panel.getPathnetModel(), new Point(0,0));
		main.setVisible(true);
	}
}
