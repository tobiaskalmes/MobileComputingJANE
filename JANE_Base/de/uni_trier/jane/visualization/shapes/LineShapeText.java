/*****************************************************************************
 * 
 * LineShape.java
 * 
 * $Id: LineShapeText.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
public class LineShapeText extends LineShape {

	private final static String VERSION = "$Id: LineShapeText.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";
	private String text; 

	
	/**
     * Constructor for class <code>LineShapeText</code>
     * @param from
     * @param to
     * @param color
     * @param text
     */
    public LineShapeText(Address from, Address to, Color color, String text) {
        super(from, to, color);
        this.text = text;
    }
    
    /**
     * Constructor for class <code>LineShapeText</code>
     * @param from
     * @param to
     * @param color
     * @param text
     */
    public LineShapeText(Position from, Position to, Color color, String text) {
        super(from, to, color);
        this.text = text;
    }
    
    

    /**
     * Constructor for class <code>LineShapeText</code>
     * @param from
     * @param to
     * @param color
     * @param width
     * @param text
     */
    public LineShapeText(Address from, Address to, Color color, int width, String text) {
        super(from, to, color, width);
        // TODO Auto-generated constructor stub
        this.text = text;
    }

    /**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {

    	Position fromPos = from;
    	Position toPos = to;
    	if(fromPos == null) {
    		fromPos = addressPositionMap.getPosition(fromAddr);
            if (fromPos==null) return;
    	}
    	if(toPos == null) {
    		toPos = addressPositionMap.getPosition(toAddr);
            if (toPos==null) return;
    	}
        fromPos=fromPos.add(position);
        toPos=toPos.add(position);
    	worldspace.drawLine(fromPos,toPos, color,width);
        worldspace.drawText(text,new Rectangle(fromPos,toPos),color);
    	
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


