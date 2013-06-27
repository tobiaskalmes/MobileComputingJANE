/*****************************************************************************
 * 
 * PositionGenerator.java
 * 
 * $Id: PositionGenerator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.position_generator;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A position generator is used to produce randomly distributed positions.
 */
public interface PositionGenerator {

	/**
	 * Get the extent of this position generator. Each generated position is located
	 * inside the rectangle.
	 * @return the extent
	 */
	public Rectangle getRectangle();

	/**
	 * Get the extent of this position generator at the given time. Each generated
	 * position at this time is located inside the rectangle.
	 * @param time the time of interest
	 * @return the extent at the given time
	 */
	public Rectangle getRectangle(double time);

	/**
	 * Create the next position.
	 * @param time the actual time 
	 * @return the next position
	 */
	public Position getNext(double time);

	/**
	 * Returns the current Shape of the PositionGenerator 
	 * @return the shape of the PositionGenerator
	 */

	public Shape getShape();
}
