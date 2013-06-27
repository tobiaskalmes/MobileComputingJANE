/*****************************************************************************
 * 
 * DefaultShapeBuilder.java
 * 
 * $Id: DefaultShapeBuilder.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This shape builder arranges the shapes in a default manner.
 */
public class DefaultShapeBuilder implements ShapeBuilder {

	private final static String VERSION = "$Id: DefaultShapeBuilder.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	/**
	 * @see de.uni_trier.jane.simulation.visualization.ShapeBuilder#build(de.uni_trier.ubi.appsim.kernel.network.NetworkShapes, de.uni_trier.ubi.appsim.kernel.visualization.Shape, de.uni_trier.ubi.appsim.kernel.visualization.Shape, de.uni_trier.ubi.appsim.kernel.visualization.Shape, de.uni_trier.ubi.appsim.kernel.visualization.Shape)
	 */
	public Shape build( Shape dynamicSourceShape, Shape localBackgroundShapes, Shape backgroundShape, Shape deviceShapes) {
		Position pos = new Position(0,0);
		ShapeCollection shape = new ShapeCollection();
		shape.addShape(dynamicSourceShape, pos);
		shape.addShape(localBackgroundShapes, pos);
		shape.addShape(backgroundShape, pos);
		//shape.addShape(networkShapes.getBackgroundShape(), pos);
		shape.addShape(deviceShapes, pos);
		//shape.addShape(networkShapes.getForegroundShape(), pos);
		//shape.addShape(networkShapes.getMessageShape(), pos);
		return shape;
	}

}
