/*****************************************************************************
 * 
 * RemoteSignalServer.java
 * 
 * $Id: RemoteSignalServer.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.hybrid.local.manager; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.hybrid.remote.manager.*;
import de.uni_trier.jane.service.ListenerAccess;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.operatingSystem.manager.LocalSignalManager;
import de.uni_trier.jane.signaling.*;

import java.rmi.RemoteException;
import java.util.*;

/**
 * @author daniel
 *
 * TODO comment class
 */
public class RemoteSignalServer extends LocalSignalManager {

    /**
     * TODO: comment class  
     * @author daniel
     **/

    private static final class RemoteListenerObject {

        private Class listenerClass;
        private ServiceID executingServiceID;

        /**
         * Constructor for class RemoteListenerObject 
         *
         * @param listenerClass
         * @param executingServiceID
         */
        public RemoteListenerObject(Class listenerClass, ServiceID executingServiceID) {
            this.executingServiceID=executingServiceID;
            this.listenerClass=listenerClass;
        }
        
       
        

        public ServiceID getExecutingServiceID() {
            return executingServiceID;
        }
        public Class getListenerClass() {
            return listenerClass;
        }
    }
    private Map remoteListeners;
    private RemoteSignalClient client;
    private HybridOperatingSystemServer server;
    
    /**
     * Constructor for class <code>RemoteSignalServer</code>
     * @param eventTemplateReflectionDepth 
     * 
     */
    public RemoteSignalServer(int eventTemplateReflectionDepth) {
        super(eventTemplateReflectionDepth);
        remoteListeners=new HashMap();
        
    }
    
    /**
     * @param client The client to set.
     * @param server
     */
    public void setClient(RemoteSignalClient client, HybridOperatingSystemServer server) {
        this.client = client;
        this.server=server;
    }

    /**
     * TODO Comment method
     * @param runningService
     * @param listenerID
     * @param listenerClass
     * @param oneShot
     * @return
     */
    public boolean addRemoteListener(ServiceContext callingServiceContext, ListenerID listenerID, Class listenerClass, boolean oneShot) {
        
        if (super.registerListener(callingServiceContext,new SignalListener(){},listenerID)){
            remoteListeners.put(listenerID,new RemoteListenerObject(listenerClass,callingServiceContext.getServiceID()));
            return true;
        }
        return false;
    }
    
    public boolean isRemote(ListenerID listenerID){
        return remoteListeners.containsKey(listenerID);
    }
    
    protected boolean registerListener(ServiceContext currentContext,SignalListener listener, ListenerID listenerID, boolean oneShot){
        boolean ok=super.registerListener(currentContext,listener,listenerID,oneShot);
        if (ok&&listener instanceof RemoteService){
            
            remoteListeners.put(listenerID,new RemoteListenerObject(((RemoteService)listener).getServiceClass(),currentContext.getServiceID()));
            
        }
        return ok;
    }
    
    public void sendSignal(ServiceContext callingServiceContext,
            ListenerID receiver, Signal signal) {
        if (isRemote(receiver)){
            try {
                client.sendRemoteSignal(callingServiceContext,receiver,signal);
            } catch (RemoteException e) {
                ServiceContext context=getContextForListener(receiver);
                if (context!=null){
                    server.hybridCallFailed(context.getServiceID());
                }
                //e.printStackTrace();
            }
        }else{
            super.sendSignal(callingServiceContext, receiver, signal);
        }
    }    
    
    
    public void sendSignal(ServiceContext senderContext, Collection receivers,
            Signal signal) {
        ArrayList local=new ArrayList();
        ArrayList remote=new ArrayList();
        Iterator iterator=receivers.iterator();
        while (iterator.hasNext()) {
            ListenerID element = (ListenerID) iterator.next();
            
            if (isRemote(element)){
                remote.add(element);
            }else{
                local.add(element);
            }
        }
        
        
        if (remote.size()>0){
            try {
                client.sendRemoteSignal(senderContext, signal,remote);//(ListenerID[])remote.toArray(new ListenerID[remote.size()]));
            } catch (RemoteException e) {
                server.hybridCallFailed(null);//??
                iterator=remote.iterator();
                while (iterator.hasNext()) {
                    ListenerID element = (ListenerID) iterator.next();
                    server.hybridCallFailed(element);//??    
                }
                e.printStackTrace();
            }
        }
        if (!local.isEmpty()){
            super.sendSignal(senderContext, local, signal);
        }
    }
    

    public void finish(ServiceContext callerContext, ListenerID receiver) {
        if (isRemote(receiver)){
            remoteListeners.remove(receiver);
            try {
                client.finish(callerContext,receiver);
            } catch (RemoteException e) {
                //server.hybridCallFailed(getServiceForListener(receiver));
                //e.printStackTrace();
            }
        }//else{
            super.finish(callerContext, receiver);
        //}
    }

    /**
     * TODO Comment method
     * @param listenerID
     * @param runningService
     */
    public void removeRemoteListener( ServiceContext callerContext,ListenerID listenerID) {
        super.finish(callerContext,listenerID);   
        remoteListeners.remove(listenerID);
    }
    
    //
    public Class getListenerClass(ListenerID registeredListenerID) {
        if (isRemote(registeredListenerID)){
            return ((RemoteListenerObject)remoteListeners.get(registeredListenerID)).getListenerClass();
        }
        return super.getListenerClass(registeredListenerID);
    }
    
    public Object accessSynchronous(ServiceContext callerContext,
            ListenerID requestedService, ListenerAccess listenerAccess) {
    
        if (isRemote(requestedService)){
            //TODO: Synchronisationsproblem => DEADLOCK!
            throw new IllegalStateException("Not implemented");
//            ServiceThread requestedServiceThread = getServiceThread(requestedService);
//    		if(!checkImplements(requestedService, serviceAccess.getReceiverServiceClass())) {
//    			throw new OperatingServiceException("The service data object is not handled by the requested service.");
//    		}
//    		ServiceID senderService = getRunningService();
//    		exitContext();
//    		Object object=null;
//            try {
//                object=remoteClient.handleAccessSynchronous(requestedService, senderService,serviceAccess);
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            enterContext(senderService);
//            return object;
        }
        return super.accessSynchronous(callerContext, requestedService,
                listenerAccess);
    
    }
    


    /**
     * TODO Comment method
     * @param context
     * @param listenerClass
     * @return
     */
    public ListenerID addAutogenListener(ServiceContext context, Class listenerClass) {
        ListenerID listenerID=super.generateListenerIDAutogen(context,listenerClass);
        super.registerListenerAutogen(context,new SignalListener(){},listenerID);
        remoteListeners.put(listenerID, new RemoteListenerObject(listenerClass,context.getServiceID()));
        return listenerID;
    }

}
