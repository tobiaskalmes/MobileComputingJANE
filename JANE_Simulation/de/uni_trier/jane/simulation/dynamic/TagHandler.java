/*****************************************************************************
 * 
 * TagHandler.java
 * 
 * $Id: TagHandler.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * Listener class for the SimpleOnDemandPseudoXMLParser. 
 * 
 * For internal use only. Do not use this class in your code.
 */
public interface TagHandler {
	/**
	 * Called when body text is parsed.
	 * @param text the text parsed
	 */
	public void text(String text);
	/**
	 * Called when the start of an element is parsed
	 * @param name the name of the element
	 * @param attributes all attributes of the element
	 */
	public void startElement(String name, XMLAttributes attributes);
	/**
	 * Called when the end of an element is parsed
	 * @param name the name of the element
	 */
	public void endElement(String name);
}
