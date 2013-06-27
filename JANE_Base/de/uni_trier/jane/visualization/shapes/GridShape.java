/*****************************************************************************
 * 
 * GridShape.java
 * 
 * $Id: GridShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This shape displays a grid with the given number of columns and rows.
 */
public class GridShape implements Shape {
	
	private Position center;
	private Extent extent;
	private int columnCount;
	private int rowCount;
	private Color color;
	private Address address = null;
	
	private MutablePosition	p0, p1;
	private Position[]		posLeft, posRight, posBot, posTop;
	
	public GridShape(DeviceID address, Position center, Extent extent, int cols, int rows, Color color) {
		this(center,extent,cols,rows,color);
		this.address = address;
	}

	/**
	 * Creates a grid in the z-plane.
	 * @param center the center of the grid
	 * @param extent the extent of the grid
	 * @param columnCount the number of columns
	 * @param rowCount the number of rows
	 * @param color the color of the grid
	 */
	public GridShape(Position center, Extent extent, int columnCount, int rowCount, Color color) {
		this.center = center;
		this.extent = extent;
		this.columnCount = columnCount;
		this.rowCount = rowCount;
		this.color = color;
	
		//these are used in the visualization method
		p0 = new MutablePosition();
		p1 = new MutablePosition();
		
		posLeft  = new Position[rowCount+1];
		posRight = new Position[rowCount+1];
		posBot = new Position[columnCount+1];
		posTop = new Position[columnCount+1];
		
		Position topleft  = new Position(center.getX()-.5*extent.getWidth(),
										 center.getY()-.5*extent.getHeight());
		Position topright = new Position(center.getX()+.5*extent.getWidth(),
										 center.getY()-.5*extent.getHeight());
		Position botleft  = new Position(center.getX()-.5*extent.getWidth(),
										 center.getY()+.5*extent.getHeight());
		Position delta;
		//simply precalculate the grid vectors
		delta = new Position(.0,extent.getHeight()/(double)(rowCount),.0);
		for (int y=0;y<rowCount+1;y++) {
			posLeft[y]  = topleft.add(delta.scale((double)y));  
			posRight[y] = topright.add(delta.scale((double)y)); 
		}
		delta = new Position(extent.getWidth()/(double)(columnCount),.0,.0);
		for (int x=0;x<columnCount+1;x++) {
			posBot[x] = botleft.add(delta.scale((double)x));
			posTop[x] = topleft.add(delta.scale((double)x));
		}
	}

	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {

    	Position pos = position;
    	if (address!=null)
			pos = pos.add(addressPositionMap.getPosition(address));
    	
    	
    	for (int x=0;x<columnCount+1;x++) {
			p0.set(posTop[x])
			  .add(pos);
			p1.set(posBot[x])
			  .add(pos);
			worldspace.drawLine(p0,p1,color);
		}
		
		for (int y=0;y<rowCount+1;y++) {
			p0.set(posLeft[y])
			  .add(pos);
			p1.set(posRight[y])
			  .add(pos);
			worldspace.drawLine(p0,p1,color);
		}
    }

    /**
     * TODO: this one is unused
     */
    public Rectangle getRectangle(Position position, Matrix matrix) {
		Position absoluteCenter = center.add(position);
		Position offset = new Position(extent.getWidth()/2, extent.getHeight()/2);
		return new Rectangle(absoluteCenter.sub(offset), absoluteCenter.add(offset));
	}
}
