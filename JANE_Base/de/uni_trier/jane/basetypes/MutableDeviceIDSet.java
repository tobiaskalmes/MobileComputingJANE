/*****************************************************************************
 * 
 * MutableDeviceIDSet.java
 * 
 * $Id: MutableDeviceIDSet.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.util.*;


/**
 * @author daniel
 *
 * This is a DeviceIDSet, where it is possible to add or remove elements
 * @see de.uni_trier.jane.basetypes.DeviceIDSet
 */
public class MutableDeviceIDSet extends DeviceIDSet {

	
	/**
	 * 
	 * Constructor for class <code>MutableDeviceIDSet</code>
	 *
	 */
	public MutableDeviceIDSet() {
		super(new HashSet());
	}
	/**
	 * 
	 * Constructor for class <code>MutableDeviceIDSet</code>
	 * @param deviceIDSet
	 */
	public MutableDeviceIDSet(DeviceIDSet deviceIDSet){
		super(deviceIDSet);
	}
	
	/**
	 * 
	 * Constructor for class <code>MutableDeviceIDSet</code>
	 * @param collection	a collection containing only deviceIDs
	 */
	public MutableDeviceIDSet(Collection collection){
		super(collection);
	}
	
	/**
	 * Remove all elements from this set
	 */
	public void clear() {
		set.clear();		
	}

	/**
	 * Adds an element to this set
	 * @param deviceID	the deviceID to add
	 * @return	true, if the set did not already contain this element
	 */
	public boolean add(DeviceID deviceID) {
		return set.add(deviceID);
	}


    /**
     * Removes the specified element from this set if it is present.
     *
     * @param deviceID  deviceID to be removed from this set, if present.
     * @return <tt>true</tt> if the set contained the specified element.
     */
	public boolean remove(DeviceID deviceID) {
		return set.remove(deviceID);
	}

	/**
	 * Adds all elements of the given set to this set
	 * @param deviceIDSet	the elements to add
	 * @return <tt>true</tt> if this collection changed as a result of the
     *         call.
	 */
	public boolean addAll(DeviceIDSet deviceIDSet) {
		return set.addAll(deviceIDSet.set);
	}


	/**
	 * Remove all elements of the given set from this set
	 * @param deviceIDSet	the elements to remove
	 * @return <tt>true</tt> if this set changed as a result of the call.
	 */
	public boolean removeAll(DeviceIDSet deviceIDSet) {
		return set.removeAll(deviceIDSet.set);
	}

	/**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this collection all of its elements that are not contained in the
     * specified collection. <p>
	 * @param deviceIDSet	the elements to retain
	 * @return	<tt>true</tt> if this set changed as a result of the call.
	 */
	public boolean retainAll(DeviceIDSet deviceIDSet) {
		return set.retainAll(deviceIDSet.set);
	}


}
