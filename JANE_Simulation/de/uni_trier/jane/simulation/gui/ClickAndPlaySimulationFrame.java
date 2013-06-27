/*****************************************************************************
 * 
 * ClickAndPlaySimulationFrame.java
 * 
 * $Id: ClickAndPlaySimulationFrame.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.gui; 

import java.awt.event.*;

import javax.swing.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.sgui.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.deprecated_shapes.RelativeRectangleShape;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class ClickAndPlaySimulationFrame extends SimulationMainFrame {
    
    private ClickAndPlayMobilitySource clickAndPlayMobilitySource;
    private boolean firstClick;
    protected DeviceID selectedDevice;
    
    protected VisualizationPanel visualizationPanel;
    
    /**
     * 
     * Constructor for class <code>ClickAndPlaySimulationFrame</code>
     * @param clickAndPlayMobilitySource
     */


    public ClickAndPlaySimulationFrame(ClickAndPlayMobilitySource clickAndPlayMobilitySource) {
        this.clickAndPlayMobilitySource=clickAndPlayMobilitySource;
        firstClick=true;
    }
    
    /**
     * enhance the initialization of the frame by adding another mouselistener
     */
    public void show(SimulationParameters parameters) {
    	super.show(parameters);
    	visualizationPanel = this.getJVisualizationPanel();

    	/**
    	 * adds an action to move items within the pathnet
    	 * the inverse transformation currently works for plan view
    	 */
    	visualizationPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            	Matrix matrix = visualizationPanel.getInverseTransformationMatrix();
            	Position clickPosition=new Position(e.getX(),e.getY()).transform(matrix);

            	System.out.println("ClickAndPlaySimulationFrame.show() : ");
            	System.out.println("mouse clicked: ("+e.getX()+","+e.getY()+")");
				System.out.println("->transformed: ("+clickPosition.getX()+","+clickPosition.getY()+","+clickPosition.getZ()+")");
				
				if (firstClick){
					DeviceID[] devices=clickAndPlayMobilitySource.getAddress(
							new de.uni_trier.jane.basetypes.Rectangle(clickPosition, 
									new Extent(5,5)));
					if (devices.length>0){
						firstClick=false;
						selectedDevice=devices[0];
					}
					
				}else{
					firstClick=true;
					clickAndPlayMobilitySource.setPosition(selectedDevice,clickPosition);
					
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
    
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 * enhance the <code>actionPerformed</code> from the super class 
	 */
//	public void actionPerformed(ActionEvent arg0) {
//		super.actionPerformed(arg0);
//
//		//frame = frameSource.getFrame();
//		
//		/*if (console != null) {
//			ConsoleTextIterator it = frame.getConsoleTextIterator();
//			while (it.hasNext()) {
//				it.next().println(console);
//			}
//		}*/
//
//		/*simTime.setText("time " + (int) frame.getTime());*/
//        String time = frame.getTimeString();
//        
//        //simulationControlPanel.setTimeLabelText("time " + ((int) (frame.getTime()*100))/100.0);
//        simulationControlPanel.setTimeLabelText("time " +time);
//
//	}
    
    protected Shape buildShape(Shape frameShape) {
        
        Shape shape;   
        if (!firstClick||markDevice!=null){
            ShapeCollection collection=new ShapeCollection();
            collection.addShape(frameShape);
            if (markDevice!=null){
                collection.addShape(new RectangleShape(markDevice,new Extent(9,9),Color.BLUE,false));
            }
            if (!firstClick){
                collection.addShape(new RectangleShape(selectedDevice,new Extent(10,10),Color.RED,false));
            }
            shape=collection;
        }else{
            shape=frameShape;
        }
        return shape;
    }
}
