/*****************************************************************************
 * 
 * PolygonPathIterator.java
 * 
 * $Id: PolygonPathIterator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.awt.geom.*;

import de.uni_trier.jane.basetypes.*;

/**
 * This is an implementation of the <code>PathIterator</code> interface
 * for arbitrary closed polygons.
 */
public class PolygonPathIterator implements PathIterator {

	private final static String VERSION = "$Id: PolygonPathIterator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private PositionIterator positionIterator;
	private boolean firstPosition;
	private boolean finished;
	private Position currentPosition;

	/**
	 * @param positionIterator an iterator over all polygon points
	 */
	public PolygonPathIterator(PositionIterator positionIterator) {
		this.positionIterator = positionIterator;
		firstPosition = true;
		finished = false;
		if(positionIterator.hasNext()) {
			currentPosition = positionIterator.next();
		}
		else {
			currentPosition = null;
		}
	}

	public int getWindingRule() {
		return WIND_EVEN_ODD;
	}

	public void next() {
		if(currentPosition == null) {
			finished = true;
		}
		else {
			if(positionIterator.hasNext()) {
				currentPosition = positionIterator.next();
			}
			else {
				currentPosition = null;
			}
		}
		firstPosition = false;
	}

	public boolean isDone() {
		return finished;
	}

	public int currentSegment(double[] coords) {
		if(currentPosition != null) {
			coords[0] = currentPosition.getX();
			coords[1] = currentPosition.getY();
		}
		return getCommand();
	}

	public int currentSegment(float[] coords) {
		if(currentPosition != null) {
			coords[0] = (float)currentPosition.getX();
			coords[1] = (float)currentPosition.getY();
		}
		return getCommand();
	}

	private int getCommand() {
		if(currentPosition == null) {
			return SEG_CLOSE;
		}
		else {
			if(firstPosition) {
				return SEG_MOVETO;
			}
			else {
				return SEG_LINETO;
			}
		}
	}

}