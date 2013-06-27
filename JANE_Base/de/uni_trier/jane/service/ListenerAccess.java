/*
 * Created on 07.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service;

import java.io.Serializable;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ListenerAccess extends Dispatchable, Serializable {

	public Object handle(SignalListener listener);
	
}
