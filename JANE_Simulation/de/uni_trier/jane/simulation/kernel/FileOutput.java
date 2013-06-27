/*****************************************************************************
 * 
 * FileOutput.java
 * 
 * $Id: FileOutput.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.kernel;

import java.io.*;

import de.uni_trier.jane.basetypes.*;

/**
 * Output implementation for plain files. 
 */
public class FileOutput implements Output {

	private final static String VERSION = "$Id: FileOutput.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private PrintWriter printWriter;

	/**
	 * Constructs a new FileOutput for the given filename.
	 * @param fileName the name of the file where the output should be saved
	 * @throws IOException if an IO exception occurs while opening the file
	 */
	public FileOutput(String fileName) throws IOException {
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.Output#print(String)
	 */
	public void print(String text) {
		printWriter.print(text);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.Output#println(String)
	 */
	public void println(String text) {
		printWriter.println(text);
	}

	/**
	 * Closes the underlying file. 
	 */
	public void close() {
		printWriter.close();
	}
    
    public void flush() {
        printWriter.flush();
        
    }

}

