/*****************************************************************************
* 
* $Id: ProxySignal.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.reflectionSignal;

import java.lang.reflect.*;
import java.util.*;



import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.DeviceServiceManager;
import de.uni_trier.jane.service.operatingSystem.manager.ServiceManager;
import de.uni_trier.jane.service.operatingSystem.manager.LocalSignalManager.AutogenListenerID;
import de.uni_trier.jane.service.operatingSystem.manager.LocalSignalManager.ListenerWrapper;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProxySignal implements Signal {
     static final long serialVersionUID=0L;
	private SerializableMethod method;
	private Object[] args;
	private Class listenerClass;
	private boolean callBack;
    //private RuntimeEnvironment runtimeEnvironment;
	private ServiceManager serviceManager;
	private ServiceContext senderContext;
	private ServiceContext receiverContext;

	/**
     * 
     * Constructor for class <code>ProxySignal</code>
     * @param senderContext
     * @param receiverContext
     * @param listenerClass
     * @param method
     * @param args
     * @param callBack
	 */
	public ProxySignal(ServiceContext senderContext,ServiceContext receiverContext, Class listenerClass, SerializableMethod method, Object[] args, boolean callBack) {
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
                    if (descriptionObject.getListenerID() instanceof AutogenListenerID){
                        args[i]=createProxy(descriptionObject);
                    }else{
                        args[i]=serviceManager.getServiceInformation(senderContext).getSignalStub(receiverContext,descriptionObject.getListenerID(),descriptionObject.getListenerClass());
					//args[i]=runtimeEnvironment.getSignalListenerStub(descriptionObject.getListenerID(),descriptionObject.getListenerClass());
                    }
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

	/**
     * TODO Comment method
	 * @param descriptionObject 
     * @return
     */
    private Object createProxy(ProxyDescriptionObject descriptionObject) {


            Set classList=new HashSet();
            classList.add(ListenerStub.class);
            
            classList.addAll(Arrays.asList(descriptionObject.getListenerClass().getInterfaces()));
                
            
            return (SignalListener) Proxy.newProxyInstance(getClass().getClassLoader(),(Class[])classList.toArray(new Class[classList.size()]),
                    new AutogenSignalListenerProxy(descriptionObject.getListenerID(),receiverContext,senderContext,descriptionObject.getListenerClass(),serviceManager.getOperatingSystem(senderContext)));
        
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
