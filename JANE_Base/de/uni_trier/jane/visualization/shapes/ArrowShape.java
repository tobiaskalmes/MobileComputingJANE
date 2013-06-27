/*****************************************************************************
 * 
 * ArrowShape.java
 * 
 * $Id: ArrowShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * An arrow shape draws an arrow from a start point to an end point.
 */
public class ArrowShape implements Shape {

	private final static String VERSION = "$Id: ArrowShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Position from;
	private Position to;
	private Color color;
	private double headLength;
	
	private Address fromAddr;
	private Address toAddr;

    private Position direction;
	
	
	/**
	 * Construct a line shape.
	 * @param from start position
	 * @param to end position
	 * @param color line color
	 * @param headLength the length of the head lines
	 */
	public ArrowShape(Position from, Position to, Color color, double headLength) {
		this.from = from;
		this.to = to;
		this.color = color;
		this.headLength = headLength;
		this.fromAddr = null;
		this.toAddr = null;
	}

	/**
	 * Construct an arrow shape
	 * @param from
	 * @param to
	 * @param color
	 * @param headLength
	 */
	public ArrowShape(Address from, Address to, Color color, double headLength) {
		this.fromAddr = from;
		this.toAddr = to;
		this.color = color;
		this.headLength = headLength;
	}
	
	public ArrowShape(DeviceID fromAddr, Position direction, Color color, double headLength) {
        this.fromAddr=fromAddr;
	    this.direction=direction;
        this.color=color;
        this.headLength=headLength;
    }

    /**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		//Matrix matrix = worldspace.getTransformation();
		//Position position = pos.transform(matrix);
		
		MutablePosition frompos, topos;
		
		if (fromAddr!=null) {
			frompos = new MutablePosition(addressPositionMap.getPosition(fromAddr));
            if (toAddr!=null){
                topos   = new MutablePosition(addressPositionMap.getPosition(toAddr));
            }else{
                topos=new MutablePosition(frompos).add(direction);
            }
		} else {
			frompos = new MutablePosition(from);
			topos = new MutablePosition(to);
		}
			
		frompos
			.add(position);
//			.transform(matrix);
//			
		topos
///			.transform(matrix)
			.add(position);
		
		worldspace.drawLine(frompos, topos,color);
		
		MutablePosition difference = new MutablePosition(topos).sub(frompos);
		if(difference.lengthnosqrt() > 0.0) {
			double angle = difference.getAngleZ();
			Position line = new Position(headLength, 0);
            
			worldspace.drawLine(topos, new MutablePosition(topos).add(line.turnZ(angle + 90 + 60)), color);
			worldspace.drawLine(topos, new MutablePosition(topos).add(line.turnZ(angle + 180 + 30)), color);
		}
    }

    /**
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		Position pos = position.transform(matrix);
		return new Rectangle(from.add(pos),to.add(pos));
	}
}
