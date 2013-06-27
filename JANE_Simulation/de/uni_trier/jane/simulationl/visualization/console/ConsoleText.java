/*****************************************************************************
 * 
 * ConsoleText.java
 * 
 * $Id: ConsoleText.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulationl.visualization.console;

import de.uni_trier.jane.console.*;

/**
 * This class represents a line of text intended to be printed on the simulation
 * console.
 */
public class ConsoleText {

	private final static String VERSION = "$Id: ConsoleText.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private String text;

	/**
	 * Construct a new console text.
	 * @param text the text line
	 */
	public ConsoleText(String text) {
		this.text = text;
	}

	/**
	 * Print the console text line to the given console.
	 * @param console the console
	 */
	public void println(Console console) {
		console.println(text);
	}

	/**
	 * return the text actually stored... 
	 * @return String
	 */
	public String getText() {
		return text;
	}
	
}

