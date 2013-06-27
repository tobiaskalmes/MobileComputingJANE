/*****************************************************************************
 * 
 * EmptyShape.java
 * 
 * $Id: EmptyShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This shape implementation can be used, if a shape has to be returned,
 * but there is nothing to visulaize.
 */
public class EmptyShape implements Shape {

	private final static String VERSION = "$Id: EmptyShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";
	private final static EmptyShape instance = new EmptyShape();

	private EmptyShape() {
	}
	
	public static EmptyShape getInstance() {
		return instance;
	}


	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
	 */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return Rectangle.NULL_RECTANGLE;
	}
}

