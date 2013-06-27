/*****************************************************************************
 * 
 * ConsoleTextBufferImpl.java
 * 
 * $Id: ConsoleTextBufferImpl.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

import java.util.*;

import de.uni_trier.jane.console.*;

/**
 * This is the default implemementation of the <code>ConsoleTextBuffer</code> implementation.
 */
public class ConsoleTextBufferImpl implements ConsoleTextBuffer {

	private final static String VERSION = "$Id: ConsoleTextBufferImpl.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private Console bypassConsole;
	private LinkedList textList;

	/**
	 * Constructs a new ConsoleTextBufferImpl.
	 */
	public ConsoleTextBufferImpl(Console bypassConsole) {
		this.bypassConsole = bypassConsole;
		textList = new LinkedList();
	}
	
	/**
	 * @see de.uni_trier.jane.simulationl.visualization.console.ConsoleTextBuffer#add(ConsoleText)
	 */
	public void add(ConsoleText consoleText) {
		if(bypassConsole != null) {
			consoleText.println(bypassConsole);
		}
		textList.addLast(consoleText);
	}

	/**
	 * @see de.uni_trier.jane.simulationl.visualization.console.ConsoleTextBuffer#flush()
	 */
	public ConsoleTextIterator flush() {
		if(textList.isEmpty()) {
			return new ConsoleTextIterator() {
				public boolean hasNext() {
					return false;
				}
				public ConsoleText next() {
					return null;
				}
			};
		}
		else {
			ConsoleTextIterator result = new ConsoleTextIterator() {
				private Iterator iterator = textList.iterator();
				public boolean hasNext() {
					return iterator.hasNext();
				}
				public ConsoleText next() {
					return (ConsoleText)iterator.next();
				}
			};
			textList = new LinkedList();
			return result;
		}
	}

}

