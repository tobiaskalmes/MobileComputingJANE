/*****************************************************************************
 * 
 * TextShape.java
 * 
 * $Id: TextShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This shape is used to draw Text on the canvas.
 */
public class TextShape implements Shape {

	private final static String VERSION = "$Id: TextShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address address;
	private String text;
	private Color color;
	private Position offset;

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 */
	public TextShape(String text, Address address, Color color) {
		this.text = text;
		this.address = address;
		this.color = color;
		this.offset = Position.NULL_POSITION;
	}

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 * @param offset the offset relative to the device position
	 */
	public TextShape(String text, Address address, Color color, Position offset) {
		this.text = text;
		this.address = address;
		this.color = color;
		this.offset = offset;
	}

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 */
	public TextShape(String text, Rectangle r, Color color) {
		this.address = null;
		this.text = text;
		this.color = color;
		this.offset = r.getCenter();
	}

	/**
	 * Construct a text shape.
	 * @param text the text to be drawn
	 * @param rectangle the extent of the text
	 * @param color the color of the text
	 * @param offset a relative position offset the text is moved
	 */
	public TextShape(String text, Rectangle rectangle, Color color, Position offset) {
		this.address = null;
		this.text = text;
		this.color = color;
		this.offset = rectangle.getCenter().add(offset);
	}

	/**
	 * Construct a text shape
	 * @param text the text to be drawn
	 * @param color the color of the text
	 * @param position its position
	 */
	public TextShape(String text, Color color, Position position) {
		this.address = null;
		this.text = text;
		this.color = color;
		this.offset = position;
	}
	
	/**
     * 
     * Constructor for class <code>TextShape</code>
     * @param text  the text to be drawn
     * @param color the color of the text
	 */
    public TextShape(String text, Color color) {
        this(text,color,Position.NULL_POSITION);
    }

    /**
	 * @see Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
    	MutablePosition pos = new MutablePosition(position);

    	pos.add(offset);
    	if (address!=null)
			pos.add(addressPositionMap.getPosition(address));
		
    	worldspace.drawText(text,new Rectangle(pos, Extent.NULL_EXTENT),color);
    }

	/**
	 * @see Shape#getRectangle(Position, Matrix)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return new Rectangle(position, new Extent(0,0));
	}

}

