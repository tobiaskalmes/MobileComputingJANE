/*****************************************************************************
 * 
 * DeviceIDSet.java
 * 
 * $Id: DeviceIDSet.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * 
 * @author goergen
 *
 * A set containing only deviceIDs. No elements can be added or removed. Thus, this set is immutable.
 * @see de.uni_trier.jane.basetypes.MutableDeviceIDSet
 */
public class DeviceIDSet{
	protected Set set;

	/**
	 * 
	 * Constructor for class <code>DeviceIDSet</code>
	 *
	 */
	public DeviceIDSet(){
	    set=new LinkedHashSet();
	}
	
	/**
	 * 
	 * Constructor for class <code>DeviceIDSet</code>
	 * 
	 * @param collection a collection containing only deviceIDs
	 */
	public DeviceIDSet(Collection collection) {
	    set=new LinkedHashSet();
		Iterator iterator=collection.iterator();
		while(iterator.hasNext()){
		    DeviceID deviceID=(DeviceID)iterator.next();
			set.add(deviceID);
		}
	}
	
	/**
	 * 
	 * Constructor for class <code>DeviceIDSet</code>
	 * @param deviceIDSet	the DeviceIDSet to copy
	 */
	public DeviceIDSet(DeviceIDSet deviceIDSet){
		this(deviceIDSet.set);
	}
	
	/**
	 * 
	 * Returns the size of the set
	 * @return	the size of the set
	 */
	public int size(){
		return set.size();
	}


	/**
	 * 
	 * Returns true if the set is empty
	 * @return	true, if the set is empty
	 */
	public boolean isEmpty(){
		return set.isEmpty();
	}
	
	/**
	 * 
	 * Copies all values to an arry 
	 * @return	the array of deviceIDs
	 */
	public DeviceID[] toArray(){
		return (DeviceID[])set.toArray(new DeviceID[set.size()]);
	}

	/**
	 * Returns true, if the set contains the given deviceID
	 * @param deviceID	the deviceID to check
	 * @return	true, if the set contians the given deviceID
	 */
	public boolean contains(DeviceID deviceID){
		return set.contains(deviceID);
	}

	/**
	 * @param deviceIDSet	the set to check
	 * @return	true, if this set contains all elements of the given set
	 */
	public boolean containsAll(DeviceIDSet deviceIDSet){
		return set.containsAll(deviceIDSet.set);
	}

	/**
	 * Returns an iterator over all deviceIDs of this set 
	 * 
	 * @return the DeviceIDIterator
	 */
	public DeviceIDIterator iterator(){
		return new DeviceIDIterator() {
			private final Iterator iterator=set.iterator();
			/* (non-Javadoc)
			 * @see de.uni_trier.ssds.service.globalKnowledge.DeviceIDIterator#next()
			 */
			public DeviceID next() {
				return (DeviceID)iterator.next();

			}

			/* (non-Javadoc)
			 * @see de.uni_trier.ssds.service.globalKnowledge.DeviceIDIterator#hasNext()
			 */
			public boolean hasNext() {
				return iterator.hasNext();

			}

			public void remove() {
				throw new RuntimeException("Remove not supported");
				
			}
		};
	}

}
