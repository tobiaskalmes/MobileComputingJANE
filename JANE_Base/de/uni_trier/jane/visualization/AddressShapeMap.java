/*****************************************************************************
 * 
 * AddressShapeMap.java
 * 
 * $Id: AddressShapeMap.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.visualization;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * This class collects background shapes produced locally on each device.
 */
public class AddressShapeMap {

	private final static String VERSION = "$Id: AddressShapeMap.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Map addressShapeMap;
	private Shape globalBackgroundShape;

	public AddressShapeMap() {
		addressShapeMap = new HashMap();
	}

	/**
	 * Set the local background shape for a device.
	 * @param address the device address
	 * @param shape the background shape
	 */
	public void set(DeviceID address, Shape shape) {
		addressShapeMap.put(address, shape);
	}

	/**
	 * Remove a local background shape for a device.
	 * @param address the device address
	 */
	public void remove(DeviceID address) {
		addressShapeMap.remove(address);
	}

	/**
	 * Get the shape collection of all background shapes currently set.
	 * @return Shape the shape collection
	 */
	public Shape getShape() {
		ShapeCollection shapeCollection = new ShapeCollection();
		if(globalBackgroundShape != null) {
			shapeCollection.addShape(globalBackgroundShape, Position.NULL_POSITION);
		}
		Iterator it = addressShapeMap.values().iterator();
		while(it.hasNext()) {
			Shape shape = (Shape)it.next();
			if(shape != null) {
				shapeCollection.addShape(shape, Position.NULL_POSITION);
			}
		}
		return shapeCollection;
	}

	/**
	 * Set the global background shape.
	 * @param shape the background shape
	 */
	public void setGlobal(Shape shape) {
		globalBackgroundShape = shape;
	}

	/**
	 * Remove a global background shape.
	 */
	public void removeGlobal() {
		globalBackgroundShape = null;
	}

}
