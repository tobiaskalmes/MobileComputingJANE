/*****************************************************************************
 * 
 * Advanced_ClickAndPlaySimulationFrame.java
 * 
 * @author Tom Leclerc (tom.leclerc{at}loria.fr)
 * 
 * NB: this class was done by using as base the ClickAndPlayMobilitySourceSimple class
 * 
 * *********************************************************************** 
 * JANE - The Java Ad-hoc Network simulation and evaluation Environment
 * 
 * **********************************************************************
 *  
 * Copyright (C) 2007 Tom Leclerc
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package fr.loria.jane.simulation.dynamic.mobility_source.advanced_mobility;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.sgui.SimulationMainFrame;
import de.uni_trier.jane.sgui.VisualizationPanel;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * 
 * 1) Select a node with left click  REMARK: Nodes can be select during movements!
 * 2.1) Move to a position by clicking left on that position
 * or
 * 2.2) Move toward a position (node continues beyond the position until the end of the simulation frame) by clicking shift+left on that position
 * or
 * 2.3) Stop moving with the middle click
 * 
 * REMARK: At any time you can change between the 3 movements!
 * 
 * 3) Release the node by clicking left
 * 
 */
public class Advanced_ClickAndPlaySimulationFrame extends SimulationMainFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ClickAndPlayMobilitySource clickAndPlayMobilitySource;

	private boolean firstClick;

	protected DeviceID selectedDevice;

	protected VisualizationPanel visualizationPanel;

	/**
	 * 
	 * Constructor for class <code>ClickAndPlaySimulationFrame</code>
	 * 
	 * @param clickAndPlayMobilitySource
	 */

	public Advanced_ClickAndPlaySimulationFrame(
			ClickAndPlayMobilitySource clickAndPlayMobilitySource) {
		this.clickAndPlayMobilitySource = clickAndPlayMobilitySource;
		firstClick = true;
	}

	/**
	 * enhance the initialization of the frame by adding another mouselistener
	 */
	public void show(SimulationParameters parameters) {
		super.show(parameters);
		visualizationPanel = this.getJVisualizationPanel();

		/**
		 * adds an action to move items within the pathnet the inverse
		 * transformation currently works for plan view
		 */
		visualizationPanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				// if left Click
				if (e.getButton() == 1) {
					// get matrix to adapt the click to the corresponding plan position (used when moving plan in the GUI)
					 Matrix matrix = visualizationPanel
					 .getInverseTransformationMatrix();
					 // set the position the respective (to the plan) clicked position
					 Position clickPosition = new Position(e.getX(), e
					 .getY()).transform(matrix);
					 
					System.out.println("ClickAndPlaySimulationFrame.show() : ");
					System.out.println("mouse clicked: (" + e.getX() + ","
							+ e.getY() + ")");
					System.out.println("->transformed: ("
							+ clickPosition.getX() + "," + clickPosition.getY()
							+ "," + clickPosition.getZ() + ")");
					// if no node selected
					if (firstClick) {
						// get the devices which are within the click area of 10x10
						DeviceID[] devices = clickAndPlayMobilitySource
								.getAddress(new de.uni_trier.jane.basetypes.Rectangle(
										clickPosition, new Extent(10, 10)));
						// if there is a device in the click area
						if (devices.length > 0) {
							// select the first device of the click area device list
							selectedDevice = devices[0];
							firstClick = false;
						}
					// if a node is selected
					} else {
						// if shift is not pressed
						if (!e.isShiftDown()){
							// move toward the click position and stop there
							clickAndPlayMobilitySource.setPosition(selectedDevice,
									clickPosition);
						// if shift is pressed
						}else {
							// move toward and beyond the click position until you reach the bounds of the simulation area
							((Advanced_ClickAndPlayMobilitySource)clickAndPlayMobilitySource).moveToward(selectedDevice, clickPosition);
						}
					}
				// if middle click
				} else if (e.getButton() == 2) {
					// if a node is selected
					if (!firstClick) {
						// stop moving
						((Advanced_ClickAndPlayMobilitySource) clickAndPlayMobilitySource)
								.stopMoving(selectedDevice);
					}
				// if right click
				} else if (e.getButton() == 3) {
					// deselect node
					firstClick = true;
				}
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
	}

	// builds the shape of the node
	protected Shape buildShape(Shape frameShape) {

		Shape shape;
		if (!firstClick || markDevice != null) {
			ShapeCollection collection = new ShapeCollection();
			collection.addShape(frameShape);
			if (markDevice != null) {
				collection.addShape(new RectangleShape(markDevice, new Extent(
						9, 9), Color.BLUE, false));
			}
			if (!firstClick) {
				collection.addShape(new RectangleShape(selectedDevice,
						new Extent(10, 10), Color.RED, false));
			}
			shape = collection;
		} else {
			shape = frameShape;
		}
		return shape;
	}
}
