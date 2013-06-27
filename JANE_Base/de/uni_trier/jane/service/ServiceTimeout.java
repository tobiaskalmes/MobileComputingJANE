/*****************************************************************************
 * 
 * Timeout.java
 * 
 * $Id: ServiceTimeout.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service;

/**
 * This class represents a timeout with a given duration.
 * Derive a class and implement the abstract method handle to 
 * define your own timeouts.
 */
public abstract class ServiceTimeout {

	private final static String VERSION = "$Id: ServiceTimeout.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	protected double delta;

	/**
	 * Constructs a new timeout with the given duration.
	 * @param delta the duration of the timeout
	 */
	public ServiceTimeout(double delta) {
		if(delta < 0) {
			throw new IllegalArgumentException("the timeout delta has to be greater or equal zero.");
		}
		this.delta = delta;
	}

	/**
	 * Returns the duration of this timeout.
	 * @return the duration of this timeout
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * Abstract method implemented by derived classes to execute 
	 * own functionality when this timeout occurs.
	 */
	public abstract void handle();
}
