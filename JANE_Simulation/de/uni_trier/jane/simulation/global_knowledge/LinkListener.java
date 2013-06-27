/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.simulation.global_knowledge;

import de.uni_trier.jane.basetypes.*;


/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface LinkListener {

	/**
	 * @param sender
	 * @param receiver
	 */
	public void handleDetach(DeviceID sender, DeviceID receiver);

	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	public void handleAttach(DeviceID sender, DeviceID receiver);


}
