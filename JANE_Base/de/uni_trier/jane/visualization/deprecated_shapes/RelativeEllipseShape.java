/*****************************************************************************
 * 
 * EllipseShape.java
 * 
 * $Id: RelativeEllipseShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class is used to draw an ellipse on the canvas.
 * @deprecated
 */
public class RelativeEllipseShape implements Shape {

	private final static String VERSION = "$Id: RelativeEllipseShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address center;
	private Extent extent;
	private Color color;
	private boolean filled;

	/**
	 * Construct an ellipse shape.
	 * @param center the center of the rectangle
	 * @param extent the extent of the rectangle
	 * @param color the color of the rectangle
	 * @param filled true <=> filled rectangle
	 */
	public RelativeEllipseShape(DeviceID center, Extent extent, Color color, boolean filled) {
		this.center = center;
		this.extent = extent;
		this.color = color;
		this.filled = filled;
	}


	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		Position centerPosition = addressPositionMap.getPosition(center).transform(matrix);
    	//Rectangle rectangle = new Rectangle(centerPosition.add(position), extent.transform(matrix));
    	Rectangle rectangle = new Rectangle(centerPosition, extent);
		worldspace.getCanvas().drawEllipse(rectangle, color, filled);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(position.transform(matrix), new Extent(0,0)); // TODO ignored at the moment!!!
	}
}
