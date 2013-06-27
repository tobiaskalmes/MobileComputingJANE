/*****************************************************************************
 * 
 * LineShape.java
 * 
 * $Id: orig_RelativeLineShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class is used to draw lines on a canvas.
 * @deprecated
 */
public class orig_RelativeLineShape implements Shape {

	private final static String VERSION = "$Id: orig_RelativeLineShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address from;
	private Address to;
	private Color color;
		
	/**
	 * Construct a line shape.
	 * @param from start position
	 * @param to end position
	 * @param color line color
	 */
	public orig_RelativeLineShape(Address from, Address to, Color color) {
		this.from = from;
		this.to = to;
		this.color = color;
	}

	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		Position fromPosition = addressPositionMap.getPosition(from).transform(matrix);
        Position toPosition = addressPositionMap.getPosition(to).transform(matrix);
        worldspace.getCanvas().drawLine(fromPosition, toPosition, color, 1);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(0,0,0,0); // TODO ignored at the moment!!!
//		return new Rectangle(to.add(position), from.add(position));
	}
}
