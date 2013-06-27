/*****************************************************************************
 * 
 * ShapeBuilder.java
 * 
 * $Id: ShapeBuilder.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.visualization;

import de.uni_trier.jane.visualization.shapes.*;

/**
 * Use this interface to define the order of the visualized simulation shapes.
 */
public interface ShapeBuilder {
	
	/**
	 * Build the vizualized shape from the given shape components.
	 * @param networkShapes the network shapes
	 * @param dynamicSourceShape the shape form the dynamic source
	 * @param localBackgroundShapes the background shapes produced by the
	 * devices
	 * @param backgroundShape the simulation background shape
	 * @param deviceShapes the device shapes
	 * @return the shape to be visualized
	 */
	Shape build(Shape dynamicSourceShape, Shape localBackgroundShapes, Shape backgroundShape, Shape deviceShapes);

}
