/*
 * Created on Mar 6, 2005
 * File: VisualizationPanel.java
 */
package de.uni_trier.jane.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import de.uni_trier.jane.basetypes.DefaultWorldspace;
import de.uni_trier.jane.basetypes.DeviceIDPositionMap;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.simulation.visualization.Graphics2DCanvas;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * Visualization panel for the devices of the simulation.<br>
 * 
 * @author ulf.wehling
 * @version Jun 28, 2005 
 */
public class VisualizationPanel extends JPanel {
	
	private static final long serialVersionUID = 4050199747820205361L;
	
	private int topPixelValue;
	private int leftPixelValue;
	private double zoom;
	private de.uni_trier.jane.visualization.shapes.Shape shape;
	private DeviceIDPositionMap addressPositionMap;
	private Graphics2DCanvas painter;
	private Worldspace worldspace;
	
	/**
	 * Constructor for the class <code>VisualizationPanel</code>.
 	 * @param shape Shape which is used for the visualization.
	 */
	public VisualizationPanel(
			de.uni_trier.jane.visualization.shapes.Shape shape
			) {
		setBackground(java.awt.Color.white);
		
		this.shape = shape;
		this.zoom = 1;	
		this.painter = new Graphics2DCanvas();
		worldspace = new DefaultWorldspace(painter);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).scale(zoom, zoom);
		((Graphics2D) g).translate(-leftPixelValue, -topPixelValue);
		painter.setGraphics((Graphics2D) g);
		shape.visualize(new Position(0, 0), worldspace, addressPositionMap);
	}
	
	/**
	 * Translates the position (x, y) into a new position (x', y') depending
	 * on the zoom and returns the new position.
	 * @param x X coordinate of the old position.
	 * @param y Y coordinate of the old position.
	 * @return Translated position.
	 */
	public Position translate(int x, int y) {
		return new Position(x/zoom, y/zoom);
	}
	
	// ##### Getters and setters ###############################################
	
	/**
	 * No functionality at the moment!<br>
	 * Sets the top pixel value.
	 * @param topPixelValue Value of the top pixel.
	 */
	public void setTopPixelValue(int topPixelValue) {
		this.topPixelValue = topPixelValue;
	}
	
	/**
	 * No functionality at the moment!<br>
	 * Sets the left pixel value.
	 * @param leftPixelValue Value of the left pixel.
	 */
	public void setLeftPixelValue(int leftPixelValue) {
		this.leftPixelValue = leftPixelValue;
	}
	
	/**
	 * Sets the zoom of the visualization.
	 * @param zoom Zoom of the visualization.
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
		repaint();
	}
	
	/**
	 * Gets the current zoom.
	 * @return Zoom of the current visualization. 
	 */
	public double getZoom() {
		return zoom;
	}
	
	/**
	 * Sets the shape, which is drawn on the visualization panel.
	 * @param shape New shape.
	 * @param addressPositionMap New address position map.
	 */
	public void setShape(
			de.uni_trier.jane.visualization.shapes.Shape shape, 
			DeviceIDPositionMap addressPositionMap
			) {
		this.shape = shape;
		this.addressPositionMap = addressPositionMap;
		repaint();
	}	
	
}
