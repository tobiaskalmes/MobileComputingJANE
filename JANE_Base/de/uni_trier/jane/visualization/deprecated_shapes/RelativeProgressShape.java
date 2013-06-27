/*****************************************************************************
 * 
 * RealtiveProgressShape.java
 * 
 * $Id: RelativeProgressShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.visualization.deprecated_shapes;

//import com.sun.media.content.application.x_shockwave_flash.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A progress shape draws a progress bar for a value between 0.0 and 1.0.
 * @deprecated
 */
public class RelativeProgressShape implements Shape {

	private final static String VERSION = "$Id: RelativeProgressShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address device;
	private Color barColor;
	private Color borderColor;
	private double length;
	private double width;
	private double progress;
	private Position relative = Position.NULL_POSITION;
	
	public RelativeProgressShape(Address device, double progress) {
		this(device, Color.BLUE, Color.BLACK, 20.0, 4.0, progress);
	}

	/**
	 * Construct a progress shape.
	 * @param from start position
	 * @param to end position
	 * @param color line color
	 * @param headLength the length of the head lines
	 */
	public RelativeProgressShape(Address device, Color borderColor, Color barColor, double length, double width, double progress) {
		this.device = device;
		this.barColor = barColor;
		this.borderColor = borderColor;
		this.length = length;
		this.width = width;
		this.progress = progress;
	}

	/**
	 * Construct a progress shape with an additional 
	 * relative position to the device
	 * @param from start position
	 * @param to end position
	 * @param color line color
	 * @param headLength the length of the head lines
	 * @param position relative to device
	 */
	public RelativeProgressShape(Address device, Color borderColor, Color barColor, double length, double width, double progress, Position relative) {
		this(device, borderColor, barColor, length, width, progress);
		this.relative = relative;
	}

	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position pos, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
    	MutablePosition devicePosition = new MutablePosition(addressPositionMap.getPosition(device));
				
		MutablePosition barPosition = new MutablePosition(devicePosition);

		devicePosition
			.transform(matrix)
			.add(pos);
		barPosition
			.add(relative)
			.transform(matrix)
			.add(pos);

		double bx = barPosition.getX();
		double by = barPosition.getY();
		double dtx = devicePosition.getX() - bx;
		double dty = devicePosition.getY() - by;
		
		double half_width = 0.5*width;	//width actually is 'height' of bar
    	double x0 = bx - 0.5*length;
    	double y0 = by - half_width;
    	double y1 = by + half_width; 

    	Canvas canvas = worldspace.getCanvas();
    	//we simply omit the square root
    	if (dtx*dtx+dty*dty>100.0) {
    		canvas.drawLine(new Position(bx,by),devicePosition.getPosition(), Color.LIGHTGREY, 1);
    	}
    	
    	canvas.drawRectangle(new Rectangle(x0,y0,x0 + progress*length, y1), barColor, true, 1);
    	canvas.drawRectangle(new Rectangle(x0,y0,x0 +          length, y1), borderColor, false, 1);
    }

    /**
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(0,0,0,0); // TODO ignored at the moment!!!
		//return new Rectangle(from.add(position),to.add(position));
	}
}
