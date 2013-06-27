/*
 * Created on Jun 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.reflectionSignal;

import java.io.Serializable;

import de.uni_trier.jane.basetypes.ListenerID;

/**
 * @author goergen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProxyDescriptionObject implements Serializable{

	private ListenerID listenerID;
	private Class listenerClass;

	/**
	 * @param listenerID
	 * @param class1
	 */
	public ProxyDescriptionObject(ListenerID listenerID, Class listenerClass) {
		this.listenerID=listenerID;
		this.listenerClass=listenerClass;
		
	}

	
	public Class getListenerClass() {
		return listenerClass;
	}
	public ListenerID getListenerID() {
		return listenerID;
	}
}
