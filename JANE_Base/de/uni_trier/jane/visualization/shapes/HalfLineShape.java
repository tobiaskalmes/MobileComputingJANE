/*****************************************************************************
 * 
 * LineShape.java
 * 
 * $Id: HalfLineShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class is used to draw lines on a canvas.
 */
public class HalfLineShape implements Shape {

	private final static String VERSION = "$Id: HalfLineShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Position from;
	private Position to;
	private Color color;
	
	private Address fromAddr;
	private Address toAddr;
	
	/**
	 * Construct a line shape.
	 * @param from start position
	 * @param to end position
	 * @param color line color
	 */
	public HalfLineShape(Position from, Position to, Color color) {
		this.from = from;
		this.to = to;
		this.color = color;
		
		this.fromAddr = null;
		this.toAddr = null;
	}

	/**
	 * construct a line shape
	 * @param from
	 * @param to
	 * @param color
	 */
	public HalfLineShape(Address from, Address to, Color color) { 
		this.fromAddr = from;
		this.toAddr = to;
		this.color = color;
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
    	Position fp;
    	Position tp;
		if (fromAddr!=null) {
			fp = addressPositionMap.getPosition(fromAddr);
			tp = addressPositionMap.getPosition(toAddr);
		}
		else {
			fp = from;
			tp = to;
		}
		fp = fp.add(position);
		tp = tp.add(position);
		worldspace.drawLine(fp, fp.sub(tp).scale(0.5).add(tp), color);

    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		Position pos = position.transform(matrix);
		return new Rectangle(to.add(pos), from.add(pos));
	}
}
