/*****************************************************************************
 * 
 * DeviceIDIterator.java
 * 
 * $Id: DeviceIDIterator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
 * 
 * @author goergen
 *
 * An iterator over DeviceIDs
 * @see de.uni_trier.jane.basetypes.DeviceID
 * @see de.uni_trier.jane.basetypes.DeviceIDSet
 */
public interface DeviceIDIterator {
   /**
    * Returns the next device id
    * @return the next deviceID
    */ 
	public DeviceID next();
	
	/**
	 * Returns true if the iterator still contains elements
	 * @return true if the iterator has more elements
	 */
	public boolean hasNext();
	
	/**
	 * Remove the current element
	 */
	public void remove();
	

}
