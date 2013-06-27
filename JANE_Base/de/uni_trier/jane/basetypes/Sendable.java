/*
 * Created on 26.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.basetypes;

import java.io.*;

import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Sendable extends Serializable{

	/**
	 * Get the size in Bits. 
	 * @return the size 
	 */
	public int getSize();

	/**
	 * Get the shape of this object. Return "null" if no message shape is being used.
	 * @return the shape
	 */
	public Shape getShape();
    
    
    

}
