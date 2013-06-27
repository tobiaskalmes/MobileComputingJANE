/*****************************************************************************
 * 
 * PolygonShape.java
 * 
 * $Id: PolygonShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
 * The <code>PolygonShape</code> is used to visualize a sequence of points as a closed polygon.
 */
public class PolygonShape implements Shape {

	private final static String VERSION = "$Id: PolygonShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";
	
	private PositionList positionList;	
	private Color color;
	private boolean filled;

    private boolean closed;

	/**
	 * Constructs a new <code>PolygonShape</code> object.
	 * @param positionList the sequence of points defining the polygon
	 * @param color th ecolor of the polygon
	 * @param filled <code>true</code> if filled
	 */
	public PolygonShape(PositionList positionList, Color color, boolean filled) {
	    this(positionList,color,filled,true);
	}
	
	public PolygonShape(PositionList positionList, Color color, boolean filled,boolean closed) {
        this.positionList = positionList;
        this.color = color;
        this.filled = filled;
        this.closed=closed;
    }

    /**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		
		worldspace.drawPolygon(	new OffsetPositionIterator(	position,
									positionList.getPositionIterator()),
								color,filled);
		
    }

    /**
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return positionList.getRectangle();
	}

	
	private static class OffsetPositionIterator implements PositionIterator {
		private Position offset;
		private PositionIterator positionIterator;
		public OffsetPositionIterator(Position offset, PositionIterator positionIterator) {
			this.offset = offset;
			this.positionIterator = positionIterator;
		}
		public boolean hasNext() {
			return positionIterator.hasNext();
		}
		public Position next() {
			return positionIterator.next().add(offset);
		}
	}
}
