/*
 * Created on 03.05.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.reflectionSignal;

import java.lang.reflect.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.ServiceManager;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AutogenSignalProxy implements Signal {

	private SerializableMethod method;
	private Object[] args;
	private Class listenerClass;
	private boolean callBack;
    //private RuntimeEnvironment runtimeEnvironment;
	private ServiceManager serviceManager;
	private ServiceContext senderContext;
	private ServiceContext receiverContext;

    
    
	/** 
     * Constructor for class <code>AutogenSignalProxy</code>
     * @param senderContext
     * @param receiverContext
     * @param listenerClass
     * @param method
     * @param args
     * @param callBack
	 */
	public AutogenSignalProxy(ServiceContext senderContext,ServiceContext receiverContext, Class listenerClass, SerializableMethod method, Object[] args, boolean callBack) {
		this.listenerClass=listenerClass;
		this.receiverContext=receiverContext;
		this.senderContext=senderContext;
		this.method=method;
		this.args=args;
		this.callBack=callBack;
		
	}
	/**
	 * @param serviceManager
	 */
	public void setServiceManager(ServiceManager serviceManager) {
		
		this.serviceManager=serviceManager;
		
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Signal#handle(de.uni_trier.jane.signaling.SignalListener)
	 */
	public void handle(SignalListener listener) {
//		
		if (callBack){
			for (int i=0;i<args.length;i++){
				if (args[i] instanceof ProxyDescriptionObject){
						
					ProxyDescriptionObject descriptionObject=(ProxyDescriptionObject)args[i];
					args[i]=serviceManager.getServiceInformation(senderContext).getSignalStub(receiverContext,descriptionObject.getListenerID(),descriptionObject.getListenerClass());
					//args[i]=runtimeEnvironment.getSignalListenerStub(descriptionObject.getListenerID(),descriptionObject.getListenerClass());
				}
			}
		}
		try {
			method.invoke(listener,args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
		    throw new RuntimeException(e.getTargetException());
			
			//e.printStackTrace();
		} catch (SecurityException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        } catch (NoSuchMethodException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        }
		
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
