/*****************************************************************************
 * 
 * MyBeaconingData.java
 * 
 * $Id: MyBeaconingData.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.multicast.spbm;

import java.util.BitSet;

import de.uni_trier.jane.basetypes.*;

/**
 * TODO: comment class
 * 
 * @author daniel
 */

public class MyBeaconingData implements Data {
	public static final DataID DATA_ID = new ClassDataID(MyBeaconingData.class);

	private BitSet myGroups;

	public MyBeaconingData(BitSet groups) {
		this.myGroups = groups;
	}

	public DataID getDataID() {
		return DATA_ID;
	}

	public Data copy() {
		// return a new Instance if this Data
		// or this if it is immutable
		return this;
	}

	//
	public int getSize() {
		// return the simulated size in Bits
		// TODO: this is not very clever - should return maximum number of groups
		// or size of list if only few groups are used
		return myGroups.length();
	}

	public BitSet getMyGroups() {
		return myGroups;
	}
}
