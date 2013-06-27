/*****************************************************************************
 * 
 * DynamicSource.java
 * 
 * $Id: DynamicSource.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * DynamicSources provide Actions that can be mapped to events in the simulation
 * by defining a DynamicInterpreter, e.g. movement and link actions may be precomputed
 * and read from a file instead of being computed on the fly. The actions of a dynamic
 * source have to be ordered by time. The EnterAction has to come before any other action.
 * After the ExitAction only an EnterAction is allowed.
 * 
 * @see de.uni_trier.ubi.appsim.kernel.dynamic.FileDynamicSource
 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkCalculator
 */
public interface DynamicSource {

    
    
	/**
	 * Checks whether this dynamic source has more actions available.
	 * @return true, if more actions are available; false, otherwise.
	 */
	public boolean hasNext();

	/**
	 * Returns the next action from this dynamic source.
	 * @return the next action available.
	 */
	public Action next();

	/**
	 * Returns the background shape to use when this dynamic source is visualized.
	 * @return the background shape
	 */
	public Shape getShape();
	
	/**
	 * Returns a rectangle with the area where all actions of this dynamic source take place.
	 * Please note that this rectangle does not necessarily need to have an upper left corner of (0,0), 
	 * but may contain arbitrary coordinates instead (that is why Rectangle instead of Extent is used).
	 * @return the rectangle defining the area for this dynamic source
	 */
	public Rectangle getRectangle();

    /**
     * @return
     */
    public Condition getTerminalCondition(Clock clock);
}

