/*****************************************************************************
 * 
 * ShapeCollection.java
 * 
 * $Id: ShapeCollection.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.io.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This class is used to collect shapes in one total shape.
 */
public class ShapeCollection implements Shape {

	private final static String VERSION = "$Id: ShapeCollection.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private ArrayList shapes;

    private Address deviceAddress;

	/**
	 * Constructs a new ShapeCollection.
	 */
	public ShapeCollection() {
		shapes = new ArrayList();
	}

	/**
	 * Constructs a new ShapeCollection.
	 * @param shapeArray an array of instanciated shapes 
	 */
	public ShapeCollection(Shape[] shapeArray) {
        this();
		for (int a=0;a<shapeArray.length;a++) {
			shapes.add(new ShapeWrapper(shapeArray[a], Position.NULL_POSITION));
		}
	}
	
	/**
     * Constructor for class <code>ShapeCollection</code>
     * @param deviceID
     */
    public ShapeCollection(Address deviceAddress) {
        this();
        this.deviceAddress=deviceAddress;
    }

    /**
	 * Add an additional shape to this collection.
	 * @param shape the shape to be added
	 * @param position the relative position in the shape collection
	 */
	public void addShape(Shape shape, Position position) {

		shapes.add(new ShapeWrapper(shape, position));
	}
	
	/**
	 * Add an additional shape to this collection.
	 * @param shape the shape to be added
	 */
	public void addShape(Shape shape) {

		shapes.add(new ShapeWrapper(shape, Position.NULL_POSITION));
	}


    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
        if (deviceAddress!=null){
            position=addressPositionMap.getPosition(deviceAddress);
        }
		Iterator iter = shapes.iterator();
		while (iter.hasNext()) {
			ShapeWrapper shapeWrapper = (ShapeWrapper) iter.next();
            Position newpos=position;
            if (!shapeWrapper.getPosition().equals(Position.NULL_POSITION)){
                newpos=position.add(shapeWrapper.getPosition());
            }
			shapeWrapper.getShape().visualize(newpos, worldspace, addressPositionMap);
		}
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
	 */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		Rectangle result = null;
		if (shapes.size() == 0) {
			// no shapes in this collection => return rectangle with extent 0 at position
			return new Rectangle(position.transform(matrix), new Extent(0,0));
		}
		Iterator iter = shapes.iterator();
		while (iter.hasNext()) {
			ShapeWrapper shapeWrapper = (ShapeWrapper) iter.next();
			Rectangle shapeRecttangle = shapeWrapper.getShape().getRectangle(position, matrix);
		    if (shapeRecttangle.getBottomLeft().getX()!=shapeRecttangle.getBottomLeft().getX()){
		    	// TODO: ???
		        System.out.println("");
		    }		
			if(result == null) {
				result = shapeRecttangle;
			}
			else {
			    
			    result = result.union(shapeRecttangle);
			    if (result.getBottomLeft().getX()!=result.getBottomLeft().getX()){
			    	// TODO: ???
			        System.out.println("");
			    }
			}
		}
		return result;
	}

	private static class ShapeWrapper implements Serializable{
		private Shape shape;
		private Position position;
		public ShapeWrapper(Shape shape, Position position) {
			this.shape = shape;
			this.position = position;
		}
		public Shape getShape() {
			return shape;
		}
		public Position getPosition() {
//			return Position.NULL_POSITION;
			return position;
		}
	}
}
