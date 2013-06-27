/*****************************************************************************
 * 
 * PrematureEOFException.java
 * 
 * $Id: PrematureEOFException.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * This exception is thrown by the SimpleOnDemandPseudoXMLParser if 
 * EOF occurs prematurely. 
 * 
 * For internal use only. Do not use this class in your code.     
 */
public class PrematureEOFException extends Exception {
	/**
	 * 
	 */
	public PrematureEOFException() {
		super();
	}

	/**
	 * @param message
	 */
	public PrematureEOFException(String message) {
		super(message);
	}

	/* only used since java1.4
	  
	public PrematureEOFException(String message, Throwable cause) {
		super(message, cause);
	}

	public PrematureEOFException(Throwable cause) {
		super(cause);
	}*/
}