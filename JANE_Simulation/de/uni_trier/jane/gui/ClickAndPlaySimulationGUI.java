/*
 * Created on Mar 5, 2005
 * File: ClickAndPlaySimulationGUI.java
 */
package de.uni_trier.jane.gui; 

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.gui.OutputFrameRenderer;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * Simple implementation of a click and play GUI for the simulation environment. 
 * The user can move the devices from one point to another, by clicking with
 * the mouse on a device and then clicking on the target position.
 * 
 * @author ulf.wehling
 * @version Jul 2, 2005
 */
public class ClickAndPlaySimulationGUI extends SimulationGUI {
	
    private static final long serialVersionUID = 3978142153799119668L;
    
    /**
     * The ID of the device selected by a left mouse click.
     */
    protected DeviceID selectedDevice;
   
    private ClickAndPlayMobilitySource clickAndPlayMobilitySource;
    private boolean firstClick;
    
    
    /**
     * Constructor of the class <code>ClickAndPlaySimulationGUI</code>.
     * @param clickAndPlayMobilitySource Mobility source for the current GUI.
     */
    public ClickAndPlaySimulationGUI(
    		ClickAndPlayMobilitySource clickAndPlayMobilitySource
    		) {
    	this(clickAndPlayMobilitySource, null);
    }
    
    /**
     * Constructor of the class <code>ClickAndPlaySimulationGUI</code>.
     * @param clickAndPlayMobilitySource Mobility source for the current GUI.
     * @param screenshotRenderer Renderer which is used for making screenshots.
     */
    public ClickAndPlaySimulationGUI(
    		ClickAndPlayMobilitySource clickAndPlayMobilitySource,
    		OutputFrameRenderer screenshotRenderer
    		) {
    	this(clickAndPlayMobilitySource, screenshotRenderer, null);
    }
    
    /**
     * Constructor of the class <code>ClickAndPlaySimulationGUI</code>.
     * @param clickAndPlayMobilitySource Mobility source for the current GUI.
     * @param screenshotRenderer Renderer which is used for making screenshots.
     */
    public ClickAndPlaySimulationGUI(
    		ClickAndPlayMobilitySource clickAndPlayMobilitySource,
    		OutputFrameRenderer screenshotRenderer,
    		OutputFrameRenderer videoRenderer
    		) {
    	super(
    			clickAndPlayMobilitySource.getRectangle().getExtent(), 
    			screenshotRenderer,
    			videoRenderer
    			);
    	this.clickAndPlayMobilitySource = clickAndPlayMobilitySource;
        this.firstClick = true;
    }
    
    /**
     * Overriden. Returns the simulation frame.
	 * @see lu.uni.jane.gui.SimulationGUI#createVisualizationFrame()
	 * @return The simulation frame.
	 */
	protected JFrame createVisualizationFrame() {
//		System.out.println("createVisualizationFrame.firstClick: " + firstClick);
		visualizationPanel.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#
			 *      mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				Position clickPosition = visualizationPanel.translate(
						e.getX(), 
						e.getY()
						);
				if (firstClick) {
					DeviceID [] devices = 
						clickAndPlayMobilitySource.getAddress(
								new de.uni_trier.jane.basetypes.Rectangle(
										clickPosition, 
										new Extent(5, 5)
										)
								);
					if (devices.length > 0) {
						firstClick = false;
						selectedDevice = devices[0];
					}
				} 
				else {
					firstClick = true;
					clickAndPlayMobilitySource.setPosition(
							selectedDevice, 
							clickPosition
							);
				}
			}
		});
		return this;
	}
	
	/**
	 * Overriden.<br>
	 * Sets the shape for the visualization panel. The current selected device
	 * is marked with a red rectangle.
	 * @param frame Current frame.
	 */
	protected void setShape(
			de.uni_trier.jane.simulation.visualization.Frame frame
			) {
		if (!firstClick) {
			ShapeCollection collection = new ShapeCollection();
			collection.addShape(frame.getShape());
			collection.addShape(
					new RectangleShape(
							selectedDevice,
							new Extent(10, 10),
							Color.RED,
							false	
							)				
					);
			visualizationPanel.setShape(
					collection, 
					frame.getAddressPositionMap()
					);
		}
		else {
			visualizationPanel.setShape(
					frame.getShape(), 
					frame.getAddressPositionMap()
					);
		}
	}
	
}
