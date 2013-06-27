/*
 * Created on Jun 16, 2005
 *
 */
package de.uni_trier.jane.sgui;

import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
/**
 * @author Klaus Sausen
 *
 */
public class PlanViewPanel extends JPanel {

	protected VisualizationPanel visualizationPanel = null;
	
	private JRadioButton jRadioButton = null;
	private JRadioButton jRadioButton1 = null;
	private JRadioButton jRadioButton2 = null;
	private JPanel jActionSelectPanel = null;
	private JLabel jActionInfoLabel = null;
	/**
	 * This is the default constructor
	 */
	public PlanViewPanel() {
		super();
		initialize();
	}
	
	public void setVisualizationPanel(VisualizationPanel vpanel) {
		visualizationPanel = vpanel;
		actualizeUserControlledTransformMode();
	}
	
	public void actualizeUserControlledTransformMode() {
		if (visualizationPanel == null)
			return;

		if (jRadioButton.isSelected())
			visualizationPanel.setUserControlledTransformMode(
					VisualizationPanel.TRANSFORM_TRANSLATE);
		else
		if (jRadioButton1.isSelected())
			visualizationPanel.setUserControlledTransformMode(
					VisualizationPanel.TRANSFORM_ROTATE);
		else
		if (jRadioButton2.isSelected())
			visualizationPanel.setUserControlledTransformMode(
					VisualizationPanel.TRANSFORM_ZOOM);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		//this.setSize(200, 181);
        setSize(300,300);
		//jActionInfoLabel.setText("jActionInfoLabel");
		//jActionInfoLabel = new JLabel();
		this.add(getJActionSelectPanel(), null);
		//this.add(jActionInfoLabel, null);
		this.setLayout(new java.awt.GridLayout(3,1));
	}
	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton() {
		if (jRadioButton == null) {
			jRadioButton = new JRadioButton();
			jRadioButton.setText("Move");
			jRadioButton.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					if (jActionInfoLabel!=null)
						jActionInfoLabel.setText("Move Visualization");
					actualizeUserControlledTransformMode();	
				}
			});

		}
		return jRadioButton;
	}
	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("Rotate");
			jRadioButton1.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					if (jActionInfoLabel!=null)
						jActionInfoLabel.setText("Rotate Visualization");
					actualizeUserControlledTransformMode();
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jRadioButton2	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton2() {
		if (jRadioButton2 == null) {
			jRadioButton2 = new JRadioButton();
			jRadioButton2.setText("Zoom");
			jRadioButton2.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					if (jActionInfoLabel!=null)
						jActionInfoLabel.setText("Zoom Visualization");
					actualizeUserControlledTransformMode();
				}
			});
		}
		return jRadioButton2;
	}
	/**
	 * This method initializes jActionSelectPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJActionSelectPanel() {
		ButtonGroup group = new ButtonGroup();
		if (jActionSelectPanel == null) {
			jActionSelectPanel = new JPanel();
			jActionSelectPanel.setSize(146, 81);
			jActionSelectPanel.setLayout(new java.awt.GridLayout(3,1));
			jActionSelectPanel.add(getJRadioButton2(), null);
			jActionSelectPanel.add(getJRadioButton1(), null);
			jActionSelectPanel.add(getJRadioButton(), null);
			group.add(getJRadioButton());
			group.add(getJRadioButton1());
			group.add(getJRadioButton2());
			getJRadioButton2().setSelected(true);
			actualizeUserControlledTransformMode();
		}
		return jActionSelectPanel;
	}
    }  //  @jve:decl-index=0:visual-constraint="12,57"
