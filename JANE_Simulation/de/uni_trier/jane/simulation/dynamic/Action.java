/*****************************************************************************
 * 
 * Action.java
 * 
 * $Id: Action.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic;

/**
 * All actions coming from a dynamic source must implement this interface.
 * When an action is executed it uses the supplied dynamic interpreter to 
 * express whatever meaning it has.
 */
public interface Action {

	/**
	 * Executes the action and supplies the dynamic interpreter needed for executing.
	 * @param dynamicInterpreter the dynamic interpreter
	 */
	public void execute(DynamicInterpreter dynamicInterpreter);
}

