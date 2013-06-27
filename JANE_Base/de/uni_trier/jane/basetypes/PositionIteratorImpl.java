/*****************************************************************************
 * 
 * PositionIteratorImpl.java
 * 
 * $Id: PositionIteratorImpl.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes;

import java.util.*;

/**
 * This is an implementation of the <code>PositionIterator</code> interface.
 */
public class PositionIteratorImpl implements PositionIterator {

	private final static String VERSION = "$Id: PositionIteratorImpl.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";
	
	private Iterator iterator;

	/**
	 * Constructs a new <code>PositionIteratorImpl</code> object.
	 * @param iterator an iterator over all position objects.
	 */
	public PositionIteratorImpl(Iterator iterator) {
		this.iterator = iterator;
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionIterator#hasNext()
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * @see de.uni_trier.jane.basetypes.PositionIterator#next()
	 */
	public Position next() {
		return (Position)iterator.next();
	}

}
