/*****************************************************************************
 * 
 * InvalidPathNetException.java
 * 
 * $Id: InvalidPathNetException.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;

/**
 * This Exception is thrown by method <code>loadDefinition</code> in
 * <code>PathNet</code> when the specified XML-File is not valid.
 */
public class InvalidPathNetException extends Exception {

	/**
	 * Construct a <code>InvalidPathNetException</code> with no specified
	 * detail message.
	 */
	public InvalidPathNetException() {
		super();
	}

	/**
	 * Construct a <code>InvalidPathNetException</code> with the specified
	 * detail message.
	 * @param msg the detail message
	 */
	public InvalidPathNetException(String msg) {
		super(msg);
	}

}

