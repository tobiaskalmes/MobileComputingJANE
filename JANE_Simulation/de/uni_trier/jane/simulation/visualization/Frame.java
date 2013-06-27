/*****************************************************************************
 * 
 * Frame.java
 * 
 * $Id: Frame.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.visualization;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.VisualizationParameters;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class collects all information to render the current simulation state.
 */
public class Frame {

	private final static String VERSION = "$Id: Frame.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private double time;
	private Shape shape;
	//private Matrix transformation;
	private VisualizationParameters parameters;
	
	private DeviceIDPositionMap addressPositionMap;
	private ConsoleTextIterator consoleTextIterator;

	/*public Frame(double time, Shape shape, 
			 DeviceIDPositionMap addressPositionMap, 
			 ConsoleTextIterator consoleTextIterator) {
	}*/

	/* deprecated 
	 */
	public Frame(double time, Shape shape, 
			 ConsoleTextIterator consoleTextIterator) {
	}

	/**
	 * Constuct a new frame.
	 * @param time the time of the shape
	 * @param shape the shape to render
	 * @param transformation the vector transformator when shape is rendered
	 * @param consoleTextIterator the text to be displayed
	 * @param keyVisualizedValueMap the next statistics values to be visualized on the fly
	 */
	public Frame(double time, Shape shape, 
			Matrix transformation,
				 DeviceIDPositionMap addressPositionMap, 
				 ConsoleTextIterator consoleTextIterator) {
	
	}
	public Frame(double time, Shape shape, 
			 VisualizationParameters parameters,
			//Matrix transformation,
				 DeviceIDPositionMap addressPositionMap, 
				 ConsoleTextIterator consoleTextIterator) {
		this.time = time;
		this.shape = shape;
		//this.transformation = transformation;
		this.parameters = parameters;
		this.addressPositionMap = addressPositionMap;
		this.consoleTextIterator = consoleTextIterator;

	}

	/**
	 * Constuct a new frame.
	 * @param time the time of the shape
	 * @param shape the shape to render
	 * @param consoleTextIterator the text to be displayed
	 * @param keyVisualizedValueMap the next statistics values to be visualized on the fly
	 */
	public Frame(double time, Shape shape, 
				 VisualizationParameters parameters, //Matrix transformation,
				 ConsoleTextIterator consoleTextIterator) {
		//this(time,shape,transformation,new DeviceIDPositionMapImpl(),consoleTextIterator);
		this(time,shape,parameters,new DeviceIDPositionMapImpl(),consoleTextIterator);
	}
	/**
	 * Get the time of this frame.
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @return a formatted simulation time string of the current time in the frame
	 */
	public String getTimeString() {
        double frameTime=this.getTime();
        
        int sec=(int)frameTime ;
        frameTime=frameTime-sec;
        int msec=(int)(frameTime*10000);
        String time=""+msec;
        String retTime=""+frameTime;
        while (time.length()<4){
        
            time="0"+time;
        }
        return sec+"."+time;
	}
	
	/**
	 * Get the simulation shape to be rendered.
	 * @return the shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Get the matrix to transform the shape to be rendered
	 * @return the transformation
	 */
	public Matrix getTransformation() {
		return parameters.getTransformationMatrix();
	}
	
	/**
	 * Get the text to be displayed.
	 * @return a console text iterator
	 */
	public ConsoleTextIterator getConsoleTextIterator() {
		return consoleTextIterator;
	}


    /**
     * TODO comment
     * @return
     */
    public DeviceIDPositionMap getAddressPositionMap() {
        return addressPositionMap;
    }

}
