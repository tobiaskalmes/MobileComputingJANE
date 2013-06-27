/*****************************************************************************
 * 
 * Path.java
 * 
 * $Id: Path.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.util.dijkstra;

import java.util.*;

/**
 * An instance of this class represents a shortest path form source to destination
 */
public class Path {

	private final static String VERSION = "$Id: Path.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Map predecessorMap;
	private int source;
	private int destination;

	/**
	 * Create an instance of this object.
	 * @param predecessorMap a mapping from nodes to their predecessor along the shortest path
	 * @param source the source used by shortest path calculation
	 * @param destination the destination of the shortest path
	 */
	public Path(Map predecessorMap, int source, int destination) {
		this.predecessorMap = predecessorMap;
		this.source = source;
		this.destination = destination;
	}

	/**
	 * get the sucessor of the given node
	 * @param node the node id
	 * @return the id of the successor
	 */
	public int getSuccessor(int node) {
		Integer current = new Integer(destination);
		Integer predecessor = (Integer)predecessorMap.get(current);
		while(predecessor.intValue() != node) {
			current = predecessor;
			predecessor = (Integer)predecessorMap.get(current);
		}
		return current.intValue();
	}

	/**
	 * Get the source used by shortest path calculation
	 * @return the source id
	 */
	public int getSource() {
		return source;
	}

	/**
	 * Get the destination of the shortest path
	 * @return the destination id
	 */
	public int getDestination() {
		return destination;
	}
	
	public String toString() {
		String result = "";
		int current = getSource();
		while (current != getDestination()) {
			result += current + " ";
			current = getSuccessor(current);
		}
		return result;
	}

}