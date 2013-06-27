/*
 * Created on 03.05.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.reflectionSignal;

import java.lang.reflect.*;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProxyAccess implements ListenerAccess {

	private SerializableMethod method;
	private Object[] args;
	private Class listenerClass;



	/**
	 * 
	 * Constructor for class <code>ProxyAccess</code>
	 *
	 * @param listenerClass
	 * @param method
	 * @param args
	 */
	public ProxyAccess(Class listenerClass, SerializableMethod method, Object[] args) {
		this.listenerClass=listenerClass;
		this.method=method;
		this.args=args;

		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Signal#handle(de.uni_trier.jane.signaling.SignalListener)
	 */
	public Object handle(SignalListener listener) {

		try {
			return method.invoke(listener,args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);//.printStackTrace();
		} catch (SecurityException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        } catch (NoSuchMethodException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        }
		return null;
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#copy()
	 */
	public Dispatchable copy() {
		//reflected copy method?
		return this;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#getReceiverServiceClass()
	 */
	public Class getReceiverServiceClass() {
		return listenerClass;
	}

}
