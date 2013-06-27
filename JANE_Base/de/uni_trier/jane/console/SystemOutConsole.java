package de.uni_trier.jane.console;

import de.uni_trier.jane.basetypes.*;

/**
 * @version $Rev$
 * @author  Johannes K. Lehnert
 */
public class SystemOutConsole implements Console {

	/*
	 *  (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Console#println(java.lang.String)
	 */
	public void println(String text) {
		System.out.println(text);
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Console#print(java.lang.String)
     */
    public void print(String text) {
        System.out.print(text);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.basetypes.Console#println()
     */
    public void println() {
        System.out.println();
        
    }

}
