/*
 * Created on 03.05.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.reflectionSignal;

import java.lang.reflect.*;
import java.lang.reflect.InvocationHandler;

import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.OperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AccessListenerProxy implements InvocationHandler {

	private ListenerID listenerID;
	private RuntimeEnvironment operatingSystem;
	private Class signalListenerClass;
	

	/**
	 * 
	 * Constructor for class <code>SignalListenerProxy</code>
	 *
	 * @param listenerID
	 * @param signalListenerClass
	 * @param operatingSystem
	 */
	public AccessListenerProxy(ListenerID listenerID, Class signalListenerClass, RuntimeEnvironment operatingSystem) {
		this.listenerID=listenerID;
		
		this.signalListenerClass=signalListenerClass;
		this.operatingSystem=operatingSystem;
		
		

	}
	
	/**
     * @param operatingSystem The operatingSystem to set.
     */
    public void setOperatingSystem(RuntimeEnvironment operatingSystem) {
        this.operatingSystem = operatingSystem;
    }


	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		return operatingSystem.accessSynchronous(listenerID,new ProxyAccess(signalListenerClass,new SerializableMethod(method),args));
		
		
	}

}
