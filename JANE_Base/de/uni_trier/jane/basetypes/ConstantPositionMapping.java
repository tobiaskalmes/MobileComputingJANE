/*****************************************************************************
 * 
 * ConstantPositionMapping.java
 * 
 * $Id: ConstantPositionMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * This class implements the <code>PositionMapping</code> interface. It constantly
 * maps a time value to one position.
 */
public class ConstantPositionMapping implements PositionMapping {

	private final static String VERSION = "$Id: ConstantPositionMapping.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Position position;

	/**
	 * Construct a new <code>ConstantPositionMapping</code> object.
	 * @param position the constant position
	 */
	public ConstantPositionMapping(Position position) {
		this.position = position;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionMapping#getInfimum()
	 */
	public Position getInfimum() {
		return position;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionMapping#getSupremum()
	 */
	public Position getSupremum() {
		return position;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionMapping#getValue(double)
	 */
	public Position getValue(double time) {
		return position;
	}

}

