/*****************************************************************************
 * 
 * CrossShape.java
 * 
 * $Id: CrossShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This shape is used to paint a cross on the canvas.
 */
public class CrossShape implements Shape {

	private final static String VERSION = "$Id: CrossShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	public final static boolean DIM3D = true;
	public static final boolean DIM2D = false;
	
	private DeviceID address;
	private Position center;
	private double size;
	private Color color;
	private boolean isDiagonal;
	private boolean is3D;
	
	/**
	 * Constructs a new cross shape.
	 * @param center the center of the shape
	 * @param size the size of the shape
	 * @param color the color of the shape
	 * @param diagonal true <=> the cross is drawn diagonal
	 * @param is3D either CrossShape.DIM3D or CrossShape.DIM2D
	 */
	public CrossShape(Position center, double size, Color color, boolean diagonal, boolean is3D) {
		this.center = center;
		this.size = size;
		this.color = color;
		this.isDiagonal = diagonal;
		this.is3D = is3D;
	}

	/**
	 * Constructs a new cross shape.
	 * @param address
	 * @param center
	 * @param size
	 * @param color
	 * @param diagonal
	 * @param is3D either CrossShape.DIM3D or CrossShape.DIM2D
	 */
	public CrossShape(DeviceID address, Position center, double size, Color color, boolean diagonal, boolean is3D) {
		this.address = address;
		this.center = center;
		this.size = size;
		this.color = color;
		this.isDiagonal = diagonal;
		this.is3D = is3D;
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		position = position.add(center);
    	if (address==null) {			//the absolute case
			if (is3D)
	    		worldspace.drawXYCross(
						position,
						size, 
						color, isDiagonal
				);
			else
				worldspace.drawCross(
					position,
					size, 
					color, isDiagonal
				);
    	} else {						//the case relative to deviceID
			if (is3D) 
				worldspace.drawXYCross(
					new MutablePosition(addressPositionMap.getPosition(address))
						.add(position), 
					size, 
					color, isDiagonal
				);
			else
	    		worldspace.drawCross(
						new MutablePosition(addressPositionMap.getPosition(address))
							.add(position), 
						size, 
						color, isDiagonal
				);
    	}
	}


	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * this is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		Position absoluteCenter = center.add(position);
		Position offset = new Position(size/2, size/2);
		return new Rectangle(absoluteCenter.sub(offset), absoluteCenter.add(offset));
	}
}
