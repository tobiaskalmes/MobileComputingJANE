/*****************************************************************************
 * 
 * OutputConsole.java
 * 
 * $Id: OutputConsole.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;

/**
 * This class wraps an output as a console passing all strings to the output.
 */
public class OutputConsole implements  Console {

	private final static String VERSION = "$Id: OutputConsole.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private Output output;
	
	/**
	 * Constructs a new <code>OutputConsole</code>.
	 * @param output the output to pass the strings.
	 */
	public OutputConsole(Output output) {
		this.output = output;
	}

	/**
	 * @see de.uni_trier.jane.console.Console#println(String)
	 */
	public void println(String text) {
		output.println(text);
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.console.Console#print(java.lang.String)
     */
    public void print(String text) {
        output.print(text);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.console.Console#println()
     */
    public void println() {
        output.println("");
        
    }

}

