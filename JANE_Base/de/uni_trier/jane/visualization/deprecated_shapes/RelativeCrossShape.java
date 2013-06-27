/*****************************************************************************
 * 
 * CrossShape.java
 * 
 * $Id: RelativeCrossShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This shape is used to paint a cross on the canvas.
 * @deprecated
 */
public class RelativeCrossShape implements Shape {

	private final static String VERSION = "$Id: RelativeCrossShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address center;
	private double size;
	private Color color;
	private boolean diagonal;

	/**
	 * Constructs a new cross shape.
	 * @param center the center of the shape
	 * @param size the size of the shape
	 * @param color the color of the shape
	 * @param diagonal true <=> the cross is drawn diagonal
	 */
	public RelativeCrossShape(DeviceID center, double size, Color color, boolean diagonal) {
		this.center = center;
		this.size = size;
		this.color = color;
		this.diagonal = diagonal;
	}


	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		double sizeh=0.5*size;
		Position line1Offset1, line1Offset2, line2Offset1, line2Offset2;
    	Position ctr = addressPositionMap.getPosition(center)
				.transform(matrix)
				.add(position);
		if(diagonal) {
			line1Offset1 = new Position(-sizeh, -sizeh);
			line1Offset2 = new Position(sizeh, sizeh);
			line2Offset1 = new Position(-sizeh, sizeh);
			line2Offset2 = new Position(sizeh, -sizeh);
		}
		else {
			line1Offset1 = new Position(0, -sizeh);
			line1Offset2 = new Position(0, sizeh);
			line2Offset1 = new Position(-sizeh, 0);
			line2Offset2 = new Position(sizeh, 0);
		}
		//FIXME
		worldspace.getCanvas()
			.drawLine(ctr.add(line1Offset1), ctr.add(line1Offset2), color, 1);
		worldspace.getCanvas()
			.drawLine(ctr.add(line2Offset1), ctr.add(line2Offset2), color, 1);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(position.transform(matrix), new Extent(0,0)); // TODO ignored at the moment!!!
	}
}
