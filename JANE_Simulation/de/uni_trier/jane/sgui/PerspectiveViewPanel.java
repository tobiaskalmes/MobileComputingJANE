/*
 * Created on Jun 16, 2005
 *
 */
package de.uni_trier.jane.sgui;


import java.awt.*;
import java.awt.geom.Arc2D.Double;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JPanel;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Klaus Sausen
 *
 */
public class PerspectiveViewPanel extends JPanel {

	private VisualizationPanel visualizationPanel;
	
	//private JPanel jPanel = null;

	private JSlider zoomSlider = null;
	private Font sliderLabelFont = new Font("SansSerif", Font.PLAIN, 10);

	private JToggleButton jToggleButton0 = null;
	private JToggleButton jToggleButton1 = null;

    private JPanel jPanel;

	/**
	 * This is the default constructor
	 */
	public PerspectiveViewPanel() {
		super();
		initialize();
	}

	public void setVisualizationPanel(VisualizationPanel vpanel) {
		visualizationPanel = vpanel;
		actualizeUserControlledTransformMode();
	}

	public void actualizeUserControlledTransformMode() {
		if (getJToggleButton0().isSelected())
			visualizationPanel.setUserControlledTransformMode(
				VisualizationPanel.TRANSFORM_TRANSLATE);
		else
			visualizationPanel.setUserControlledTransformMode(
				VisualizationPanel.TRANSFORM_ROTATE);
	}

	private JLabel createSliderLabel(String text) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(sliderLabelFont);
		return label;
	}

	
	/**
	 * function mapping the slider values onto zoom values.
	 * [0..100] -> [0.5 .. 3.5] 
	 * @param val
	 * @return bijective fun
	 */
	private double zoomFunc(double val) {
		return 0.1+(val)*0.05;
	}
	
	public void setSliderProperties() {
		Dictionary labelTable = new Hashtable();
		String s;
		for (int i=0;i<=200;i+=10) {
			s = ""+zoomFunc((double)i);
			s = s.substring(0,3);
			labelTable.put(
					new Integer(i), 
					createSliderLabel(s)
			);
		}
		zoomSlider.setOrientation(JSlider.VERTICAL);
		zoomSlider.setPaintLabels(true);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setMajorTickSpacing(10);
		zoomSlider.setSnapToTicks(false);
		zoomSlider.setLabelTable(labelTable);
		zoomSlider.setValue(zoomSlider.getMaximum());
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				double val = s.getValue();
				//System.out.println(Double.toString(val));
				visualizationPanel.getTransformationParameters().zoom =	zoomFunc(val);
				visualizationPanel.actualizeTransformationMatrix();
				visualizationPanel.repaint();
			}
		});

	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		this.setSize(231, 311);
        
		//this.setLayout(new GridLayout(2,1));
        //this.setLayout(new BorderLayout());
        this.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();
        c.fill = GridBagConstraints.VERTICAL;
		//this.add(getJPanel(), null);
        c.gridx=1;
        this.add(getJToggleButton0(), c);
        this.add(getJToggleButton1(), c);
        
        c.weighty = 0;
        //c.gridheight=GridBagConstraints.SOUTH;
		this.add(getJSlider(), c);
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridLayout(2,1));
			jPanel.add(getJToggleButton0(), null);
			jPanel.add(getJToggleButton1(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getJSlider() {
		if (zoomSlider == null) {
			zoomSlider = new JSlider(JSlider.VERTICAL,0,200,0);
		}
		setSliderProperties();
		return zoomSlider;
	}
	/**
	 * This method initializes jToggleButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */    
	public JToggleButton getJToggleButton0() {
		if (jToggleButton0 == null) {
			jToggleButton0 = new JToggleButton();
			jToggleButton0.setSelected(true);
			jToggleButton0.setIcon(new ImageIcon(getClass().getResource("control_arrows96.jpg")));
			jToggleButton0.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jToggleButton1.setSelected(!getJToggleButton0().isSelected());
					actualizeUserControlledTransformMode();	
				}
			});
		}
		return jToggleButton0;
	}
	/**
	 * This method initializes jToggleButton1	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */    
	public JToggleButton getJToggleButton1() {
		if (jToggleButton1 == null) {
			jToggleButton1 = new JToggleButton();
			jToggleButton1.setIcon(new ImageIcon(getClass().getResource("control_rotate96.jpg")));
			jToggleButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jToggleButton0.setSelected(!getJToggleButton1().isSelected());
					actualizeUserControlledTransformMode();
				}
			});
		}
		return jToggleButton1;
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
