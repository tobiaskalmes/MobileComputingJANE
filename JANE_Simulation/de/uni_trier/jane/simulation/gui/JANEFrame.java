/*
 * Created on 18.02.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.simulation.gui;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface JANEFrame {

	/**
	 * @param b
	 */
	void setVisible(boolean visible);
	
	  /**
     * TODO: comment method 
     * @return
     */
    boolean isVisible();
	
	void addFrameListener(JANEFrameListener frameListener);

}
