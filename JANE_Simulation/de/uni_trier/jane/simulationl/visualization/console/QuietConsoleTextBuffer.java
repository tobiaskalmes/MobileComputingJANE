/*****************************************************************************
 * 
 * QuietConsoleTextBuffer.java
 * 
 * $Id: QuietConsoleTextBuffer.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
 * This class is used if simulation output is of no interest to be buffered for further output.
 */
public class QuietConsoleTextBuffer implements ConsoleTextBuffer {

	private final static String VERSION = "$Id: QuietConsoleTextBuffer.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private Console console;
	private ConsoleTextIterator consoleTextIterator;
	
	/**
	 * Constructs a new QuietConsoleTextBuffer.
	 * @param console not null, if the added text has to be directly printed.
	 */
	public QuietConsoleTextBuffer(Console console) {
		this.console = console;
		consoleTextIterator = new ConsoleTextIterator() {
			public boolean hasNext() {
				return false;
			}
			public ConsoleText next() {
				throw new IllegalStateException("There is nothing to return.");
			}
		};
	}

	/**
	 * @see de.uni_trier.jane.simulationl.visualization.console.ConsoleTextBuffer#add(ConsoleText)
	 */
	public void add(ConsoleText consoleText) {
		if(console != null) {
			consoleText.println(console);
		}
	}

	/**
	 * @see de.uni_trier.jane.simulationl.visualization.console.ConsoleTextBuffer#flush()
	 */
	public ConsoleTextIterator flush() {
		return consoleTextIterator;
	}

}

