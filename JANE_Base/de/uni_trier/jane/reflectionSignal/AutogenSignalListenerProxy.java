/*
 * Created on 03.05.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.reflectionSignal;

import java.io.Serializable;
import java.lang.reflect.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.ProxySignalManager;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AutogenSignalListenerProxy implements InvocationHandler {

	private ListenerID listenerID;
	private RuntimeOperatingSystemImpl operatingSystem;
	private Class signalListenerClass;
	
	//private ServiceContext listenerContext;
	private ServiceContext senderContext;
	private ServiceContext receiverContext;
    
   // private ProxySignalManager proxySignalManager;

	/**
     * 
     * Constructor for class <code>AutogenSignalListenerProxy</code>
     * @param listenerID
     * @param senderContext
     * @param receiverContext
     * @param signalListenerClass
     * @param operatingSystem
	 */
	public AutogenSignalListenerProxy(ListenerID listenerID, ServiceContext senderContext, ServiceContext receiverContext, 
						Class signalListenerClass, RuntimeOperatingSystemImpl operatingSystem) {
		this.listenerID=listenerID;
		//this.listenerContext=listenerContext;
		this.senderContext=senderContext;
		this.receiverContext=receiverContext;
		
		this.signalListenerClass=signalListenerClass;
		this.operatingSystem=operatingSystem;
  
		
		

	}
	
//	/**
//     * @param operatingSystem The operatingSystem to set.
//     */
//    public void setOperatingSystem(RuntimeOperatingSystemImpl operatingSystem) {
//        this.operatingSystem = operatingSystem;
//    }

	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	    if (method.getDeclaringClass().equals(ListenerStub.class)){
	    	return listenerID;
	    }
	    if (method.getDeclaringClass().equals(Object.class)||Proxy.isProxyClass(method.getDeclaringClass())){

	        return method.invoke(this,args);
	    }
        if (!operatingSystem.hasListener(listenerID)) 
            return null; 
	   // if (callback){
	    //ineffizient!
	    boolean callback=false;
	    if (args!=null){
	    	for (int i=0;i<args.length;i++){
	    		if (args[i] instanceof SignalListener&& !(args[i] instanceof Sendable || args[i] instanceof Dispatchable || args[i] instanceof Serializable)){
                    ListenerID listenerID=operatingSystem.registerSignalListenerAutogen((SignalListener)args[i]);
	    			args[i]=new ProxyDescriptionObject(listenerID,args[i].getClass());
	    			callback=true;
	    		
	    		}
	    	}
	    }
        operatingSystem.sendSignal(receiverContext,listenerID,new ProxySignal(senderContext,receiverContext,signalListenerClass,new SerializableMethod(method),args,callback));
		return null;
		
	}
	
	protected void finalize() throws Throwable {
//    	//if (method.getName().equals("finialize")){
    		operatingSystem.finishListener(listenerID);
    	//}
		//operatingSystem.write("Huhu2");
		super.finalize();
	}

}
