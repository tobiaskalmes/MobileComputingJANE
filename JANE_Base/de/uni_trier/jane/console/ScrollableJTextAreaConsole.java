/*****************************************************************************
 * 
 * JTextAreaConsole.java
 * 
 * $Id: ScrollableJTextAreaConsole.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.console;

import javax.swing.*;

import de.uni_trier.jane.basetypes.*;

/**
 * This class is a wrapper for <code>JTextArea</code> objects. It implements the
 * <code>Console</code> interface for simulation text output.
 */
public class ScrollableJTextAreaConsole implements Console {

	private final static String VERSION = "$Id: ScrollableJTextAreaConsole.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private JTextArea textArea;

	private JScrollPane scrollPane;
	
	/**
	 * Construct a new console.
	 * @param textArea the text area to write in
	 */
	public ScrollableJTextAreaConsole(JTextArea textArea, JScrollPane scrollPane) {
		this.textArea = textArea;
		this.scrollPane=scrollPane;
		scrollPane.getVerticalScrollBar().setMaximum(0);
		
	}
	void autoScroll() {
		textArea.validate();
		scrollPane.validate();
		 JScrollBar sb = scrollPane.getVerticalScrollBar();
		 System.out.println(sb.getMaximum());
		 sb.setValue(sb.getMaximum());
		 //System.out.println(max);
		 //
		 //scrollPane.getVerticalScrollBar().setValue(max);
		}

	/**
	 * @see de.uni_trier.jane.console.Console#println(String)
	 */
	public void println(String text) {
		autoScroll();
		textArea.append(text+"\n");
	}
    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Console#print(java.lang.String)
     */
    public void print(String text) {
        autoScroll();
		textArea.append(text);
    }
    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Console#println()
     */
    public void println() {
        autoScroll();
		textArea.append("\n");
    }

}

