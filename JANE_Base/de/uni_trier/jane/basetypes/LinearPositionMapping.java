/*****************************************************************************
 * 
 * LinearPositionMapping.java
 * 
 * $Id: LinearPositionMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes;

/**
 * This class maps a double betwee start and end time linearily between start and
 * end position.
 */
public class LinearPositionMapping implements PositionMapping {

	private final static String VERSION = "$Id: LinearPositionMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private double startTime;
	private double endTime;
	private Position startPosition;
	private Position endPosition;

	/**
	 * Construct a new <code>LinearPositionMapping</code> object.
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param startPosition the start position
	 * @param endPosition the end position
	 */
	public LinearPositionMapping(double startTime, double endTime, Position startPosition, Position endPosition) {
		if(startTime <= endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}
		else {
			this.startTime = endTime;
			this.endTime = startTime;
			this.startPosition = endPosition;
			this.endPosition = startPosition;
		}
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionMapping#getInfimum()
	 */
	public Position getInfimum() {
		return startPosition.min(endPosition);
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionMapping#getSupremum()
	 */
	public Position getSupremum() {
		return startPosition.max(endPosition);
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionMapping#getValue(double)
	 */
	public Position getValue(double time) {
		if(time < startTime) {
			return startPosition;
		}
		else if(time > endTime) {
			return endPosition;
		}
		else {
			double delta = endTime-startTime;
			if(delta == 0) {
				return endPosition;
			}
			else {
//				return startPosition.add(endPosition.sub(startPosition).scale((time-startTime)/delta));
				MutablePosition tmp = new MutablePosition(endPosition);
				tmp.sub(startPosition);
				tmp.scale((time-startTime)/delta);
				tmp.add(startPosition);
				return tmp.getPosition();
			}
		}
	}

}

