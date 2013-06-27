/*****************************************************************************
 * 
 * ArrowShape.java
 * 
 * $Id: orig_RelativeArrowShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * An arrow shape draws an arrow from a start point to an end point.
 * @deprecated
 */
public class orig_RelativeArrowShape implements Shape {

	private final static String VERSION = "$Id: orig_RelativeArrowShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address fromDevice;
	private Address toDevice;
	private Color color;
	private double headLength;
		
	/**
	 * Construct a line shape.
	 * @param from start position
	 * @param to end position
	 * @param color line color
	 * @param headLength the length of the head lines
	 */
	public orig_RelativeArrowShape(DeviceID from, DeviceID to, Color color, double headLength) {
		this.fromDevice = from;
		this.toDevice = to;
		this.color = color;
		this.headLength = headLength;
	}

	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		Position from = addressPositionMap.getPosition(fromDevice).transform(matrix);
        Position to = addressPositionMap.getPosition(toDevice).transform(matrix);
    	worldspace.getCanvas().drawLine(from.add(position), to.add(position), color, 1);
		Position difference = to.sub(from);
		if(difference.length() > 0.0) {
			double angle = difference.getAngleZ();
			Position line = new Position(headLength, 0);
			worldspace.getCanvas().drawLine(to.add(position), to.add(line.turnZ(angle + 90 + 45)).add(position), color, 1);
			worldspace.getCanvas().drawLine(to.add(position), to.add(line.turnZ(angle + 180 + 45)).add(position), color, 1);
		}
    }

    /**
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(0,0,0,0); // TODO ignored at the moment!!!
		//return new Rectangle(from.add(position),to.add(position));
	}
}
