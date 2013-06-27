/*****************************************************************************
 * 
 * XMLAttribute.java
 * 
 * $Id: XMLAttribute.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

/**
 * This class represents a single attribute of a XML element, i.e. a (name,value)
 * pair.
 * 
 * For internal use only. Do not use this class in your code.
 */
public class XMLAttribute {
	private String name;
	private String value;

	/**
	 * Constructs a new XMLAttribute with the given name and value.
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public XMLAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns the name of the attribute
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the attribute
	 * @return the value of the attribute
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + "=" + value; 
	}
}