/*
 * Created on 16.06.2005
 *
 */
package de.uni_trier.jane.sgui;

import java.awt.Font;
import java.awt.BorderLayout;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Klaus Sausen
 *
 */
public class SpeedPanel extends JPanel {

	private JSlider speedSlider = null;
	private Font sliderLabelFont = new Font("SansSerif", Font.PLAIN, 10);

	protected boolean linear = false;
	
	public void setLinear() {
		linear = true;
		actualizeSliderProperties();
	}
	
	public void setExponential() {
		linear = false;
		actualizeSliderProperties();
	}

	public boolean isLinear() {
		return linear;
	}
	
	public boolean isExponential() {
		return !linear;
	}
	
	/**
	 * @return reference to the speedSlider instance
	 */
	public JSlider getSpeedSlider() {
		return speedSlider;
	}
	
	/**
	 * This is the default constructor
	 */
	public SpeedPanel() {
		super();
		initialize();
	}

	private JLabel createSliderLabel(String text) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(sliderLabelFont);
		return label;
	}
	
	/**
	 * actualize the labels of the slider, e.g. if switched from
	 * linear to exponential and vice versa
	 */
	public void actualizeSliderProperties() {
///		frameSource.setFrameInterval((1.0 / DefaultSimulationFrameControl.this.fps) * 1);		
		if (linear){
			//speedSlider= new JSlider(-39, 99, 0);
			Dictionary labelTable = new Hashtable();
			for (int i=-47;i<0;i=i+8){
				labelTable.put(new Integer(i), createSliderLabel("1/"+(-(i/2)+1)+"x"));			
			}
			labelTable.put(new Integer(0), createSliderLabel("1x"));	
			for (int i=9;i<100;i=i+10){
				labelTable.put(new Integer(i), createSliderLabel((i+1)+"x"));
			
			}
			speedSlider.setLabelTable(labelTable);
		}else{
			//speedSlider= new JSlider(-50, 100, 0);

			Dictionary labelTable = new Hashtable();
			for (int i=-100;i<0;i+=20){
				labelTable.put(new Integer(i), createSliderLabel("1/"+(int)Math.pow(2,((-i)/10))+"x"));			
			}
			labelTable.put(new Integer(0), createSliderLabel("1x"));	
			for (int i=0;i<=80;i=i+10){
				labelTable.put(new Integer(i), createSliderLabel((int)Math.pow(2,i/10)+"x"));
			
			}
			speedSlider.setLabelTable(labelTable);
		}
		speedSlider.setOrientation(JSlider.HORIZONTAL);
		speedSlider.setPaintLabels(true);
		speedSlider.setPaintTicks(true);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setSnapToTicks(true);
		
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		//this.setSize(548, 38);
		this.setLayout(new BorderLayout());
		this.add(getJSlider(), BorderLayout.CENTER);
	}
	
	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getJSlider() {
		if (speedSlider == null) {
			speedSlider = new JSlider(-100,80,0);
			speedSlider.setSize(600,38);
			//speedSlider.setBounds(0,0,600,40);
		}
		actualizeSliderProperties();
		return speedSlider;
	}

    /**
     * TODO: comment method 
     * @param speed
     */
    public void setSpeed(int speed) {
        speedSlider.setValueIsAdjusting(true);
        speedSlider.setValue(speed);
        
    }
 }  //  @jve:decl-index=0:visual-constraint="10,10"
