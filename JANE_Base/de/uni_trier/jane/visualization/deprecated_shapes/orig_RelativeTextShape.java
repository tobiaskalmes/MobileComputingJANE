/*****************************************************************************
 * 
 * TextShape.java
 * 
 * $Id: orig_RelativeTextShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
import de.uni_trier.jane.visualization.shapes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This shape is used to draw Text on the canvas.
 * @deprecated
 */
public class orig_RelativeTextShape implements Shape {

	private final static String VERSION = "$Id: orig_RelativeTextShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private String text;
	private DeviceID origin;
	private Color color;
	private Position offset;

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 */
	public orig_RelativeTextShape(String text, DeviceID origin, Color color) {
		this.text = text;
		this.origin = origin;
		this.color = color;
		this.offset = new Position(0.0,0.0);
	}

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 * @param offset the offset relative to the device position
	 */
	public orig_RelativeTextShape(String text, DeviceID origin, Color color, Position offset) {
		this.text = text;
		this.origin = origin;
		this.color = color;
		this.offset = offset;
	}

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 */
	public orig_RelativeTextShape(String text, Position pos, Color color) {
		this.text = text;
		this.offset = pos;
		this.color = color;
	}
	
	
	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		Rectangle rectangle;
		Position centerPos;
		if (origin == null) {
			centerPos = offset.add(position).transform(matrix);
			//centerPos = position.transform(matrix);//.add(position);
		} else {
			centerPos = addressPositionMap
				.getPosition(origin)
				.add(position)
				.transform(matrix);
		}	
		rectangle = new Rectangle(centerPos.add(offset), new Extent(0,0));
			
		//FIXME
		worldspace.getCanvas()
			.drawText(text, rectangle, color);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     * FIXME: new Extent(0,0).transform(matrix)
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return new Rectangle(position.transform(matrix), new Extent(0,0));
	}

}

