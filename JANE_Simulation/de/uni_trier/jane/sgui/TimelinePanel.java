/*
 * Created on 16.06.2005
 *
 */
package de.uni_trier.jane.sgui;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JSlider;

import javax.swing.JProgressBar;
/**
 * @author Klaus Sausen
 *
 */
public class TimelinePanel extends JPanel {

	private JSlider jTimeSlider = null;
	
	/**
	 * This is the default constructor
	 */
	public TimelinePanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		this.setSize(178, 26);
		this.setLayout(new BorderLayout());
		this.add(getJTimeSlider(), BorderLayout.CENTER);
	}

	/**
	 * this method initializes the time slider
	 * @return JSlider 
	 */
	public JSlider getJTimeSlider() {
		if (jTimeSlider == null) {
			jTimeSlider = new JSlider();
			jTimeSlider.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
			jTimeSlider.setMinimum(0);
			jTimeSlider.setMajorTickSpacing(10);
			jTimeSlider.setPaintTrack(false);
			jTimeSlider.setPaintTicks(true);
			jTimeSlider.setToolTipText("choose timeslice");
			//jTimeSlider.setPaintLabels(true);
		}
		return jTimeSlider;
	}
	
	///////// SOME GETTERS AND SETTERS ////////////////////////////
	public void setMinimum(int min) {
		jTimeSlider.setMinimum(min);
	}
	
	public void setMaximum(int max) {
		jTimeSlider.setMaximum(max);
	}
	
	public int getValue() {
		return jTimeSlider.getValue();
	}
	
	public void setValue(int val) {
		jTimeSlider.setValue(val);
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
