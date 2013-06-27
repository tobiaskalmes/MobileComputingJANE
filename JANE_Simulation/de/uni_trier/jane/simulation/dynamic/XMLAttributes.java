/*****************************************************************************
 * 
 * XMLAttributes.java
 * 
 * $Id: XMLAttributes.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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

import java.util.*;

/**
 * This class represents a set of XML attributes.
 * 
 * For internal use only. Do not use this class in your code. 
 */
public class XMLAttributes {
	private HashMap attributes;

	/**
	 * Constructs a new empty set of XML attributes.
	 */
	public XMLAttributes() {
		attributes = new HashMap();
	}

	/**
	 * Adds an attribute to the set
	 * @param attribute the XML attribute to add
	 */
	public void addAttribute(XMLAttribute attribute) {
		attributes.put(attribute.getName(), attribute);
	}

	/**
	 * Checks if an attribute with the given name exists in the set
	 * @param name the name of the attribute to look for
	 * @return true, if an attribute with the given name exists; false, otherwise
	 */
	public boolean existsAttribute(String name) {
		return attributes.get(name) != null;
	}

	/**
	 * Returns the attribute with the given name. 
	 * @param name the name of the attribute to look for
	 * @return the attribute with the given name or null if no such attribute exists
	 */
	public XMLAttribute getAttribute(String name) {
		return (XMLAttribute) attributes.get(name);
	}

	/**
	 * Returns all attributes defined in the set.
	 * @return an array containing all attributes
	 */
	public XMLAttribute[] getAllAttributes() {
		Collection atts = attributes.values();
		XMLAttribute[] res = new XMLAttribute[atts.size()];
		atts.toArray(res);
		return res;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		Iterator iter = attributes.values().iterator();
		while (iter.hasNext()) {
			buf.append(iter.next().toString());
			if (iter.hasNext()) {
				buf.append(",");
			}
		}
		buf.append("]");
		return buf.toString();
	}
}