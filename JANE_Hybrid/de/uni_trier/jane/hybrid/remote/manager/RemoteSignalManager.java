/*****************************************************************************
 * 
 * RemoteSignalManager.java
 * 
 * $Id: RemoteSignalManager.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.remote.manager; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.ListenerHandlerID;
import de.uni_trier.jane.hybrid.server.RemoteSignalManagerServer;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.LocalSignalManager;
import de.uni_trier.jane.signaling.SignalListener;

import java.rmi.RemoteException;
import java.util.*;


/**
 * @author daniel
 *
 * TODO comment class
 */
public class RemoteSignalManager extends LocalSignalManager {
    
    
    /**
     * @author daniel
     *
     * TODO comment class
     */
    private static final class RemoteListenerID extends ListenerID{

        private Class listenerClass;
        private long id;

        /**
         * Constructor for class <code>RemoteListenerID</code>
         * @param class1
         * @param l
         */
        public RemoteListenerID(Class listenerClass, long id) {
            this.listenerClass=listenerClass;
            this.id=id;
        }

        public Class getListenerClass() {
            return listenerClass;
        }

        public String toString() {
            return listenerClass+":"+id;
        }

   

        public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            if (listenerClass != null) {
                result = PRIME * result + listenerClass.hashCode();
            }
            result = PRIME * result + (int) (id >>> 32);
            result = PRIME * result + (int) (id & 0xFFFFFFFF);

            return result;
        }



        //
        public int getCodingSize() {
            return listenerClass.getName().length()*8+8*8;
        }


        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }

            if (!(oth instanceof RemoteListenerID)) return false;

            RemoteListenerID other = (RemoteListenerID) oth;
            if (this.listenerClass == null) {
                if (other.listenerClass != null) {
                    return false;
                }
            } else {
                if (!this.listenerClass.equals(other.listenerClass)) {
                    return false;
                }
            }

            if (this.id != other.id) {
                return false;
            }

            return true;
        }
    }
    private RemoteSignalManagerServer  managerServer;
    private HashMap internalFinishListenerMap;
    private int listenerHandlerID;
    private HashMap internalFinishListenerIDMap;
    private HashMap internalFinishListenerIDPairMap;
	private SyncObject syncObject;
    
    

    /**
     * 
     * Constructor for class <code>RemoteSignalManager</code>
     * @param managerServer
     * @param syncObject
     */
    public RemoteSignalManager(RemoteSignalManagerServer  managerServer,SyncObject syncObject) {
        super(-1);
        this.managerServer=managerServer;
        this.syncObject=syncObject;
        internalFinishListenerMap=new HashMap();
        internalFinishListenerIDMap=new HashMap();
        internalFinishListenerIDPairMap=new HashMap();
        
        
    }
    
    private boolean isLocal(ListenerID listenerID){
        return IDListenerMap.containsKey(listenerID)||autogenListeners.containsKey(listenerID);
    }
    
    public void addFinishListener(ServiceContext executingService,
            ListenerID listenerID, ListenerFinishedHandler handler) {
        
        
        if (isLocal(listenerID)){
            super.addFinishListener(executingService, listenerID,handler);
        }else{
            ListenerFinishedHandler listener=new InternalFinishListener(handler,  executingService);
            ListenerHandlerID id = new ListenerHandlerID(executingService,listenerHandlerID++);
            HandlerListenerPair pair=new HandlerListenerPair(handler,listenerID);  
            internalFinishListenerMap.put(pair,id);
            internalFinishListenerIDMap.put(id,listener);
            internalFinishListenerIDPairMap.put(id,pair);
            try {
                managerServer.addFinishListener(listenerID,id);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    

    public void removeFinishListener(ListenerID listenerID,
            ListenerFinishedHandler handler) {
        if (isLocal(listenerID)){
            super.removeFinishListener(listenerID, handler);
        }else{
            //ListenerFinishedHandler listener=new InternalFinishListener(handler,  executingService);
            HandlerListenerPair pair = new HandlerListenerPair(handler,listenerID);
            ListenerHandlerID id=(ListenerHandlerID)internalFinishListenerMap.remove(pair);
            internalFinishListenerIDMap.remove(id);
            internalFinishListenerIDPairMap.remove(id);
            try {
                managerServer.removeFinishListener(listenerID,id);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    /**
     * 
     * TODO Comment method
     * @param id
     * @param callerContext
     */
    public void notifyListenerFinished(ListenerHandlerID id, ServiceContext callerContext) {
        final ListenerFinishedHandler handler=(ListenerFinishedHandler)internalFinishListenerIDMap.remove(id);
        
        final HandlerListenerPair pair=(HandlerListenerPair)internalFinishListenerIDPairMap.remove(id);
        internalFinishListenerMap.remove(pair);
        executionManager.schedule(new Action(id.getExecutingContext(),callerContext) {
            public void execute(Service executingService) {
                handler.handleFinished(pair.getListenerID());

            }
        });
        
        
        
    }
    

    public ListenerID registerListener(ServiceContext currentContext,
            SignalListener listener) {
        
        RemoteListenerID listenerID=new RemoteListenerID(listener.getClass(),firstFreeID++);
        registerListener(currentContext,listener,listenerID);
        
        return listenerID;
    }
    
  

    public boolean registerListener(final ServiceContext currentContext,
            SignalListener listener, ListenerID listenerID) {
        try {
            if (managerServer.registerListener(currentContext,listenerID,listener.getClass(),false)||listener instanceof Service){
                super.registerListener(currentContext, listener, listenerID);
                addFinishListener(currentContext, listenerID,new ListenerFinishedHandler() {

                    public void handleFinished(ListenerID listenerID) {
                        try {
                            managerServer.unregisterListener(currentContext,listenerID);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
                
                return true;
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
  
    public ListenerID registerOneShotListener(ServiceContext currentContext,
            SignalListener listener) {
        
            RemoteListenerID listenerID=new RemoteListenerID(listener.getClass(),firstFreeID++);
            try {
                managerServer.registerListener(currentContext,listenerID,listener.getClass(),true);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            super.registerOneShotListener(currentContext, listener,listenerID);
            return listenerID;
    }
    
 

    public boolean registerOneShotListener(ServiceContext currentContext,
            SignalListener listener, ListenerID listenerID) {
        try {
            if (managerServer.registerListener(currentContext,listenerID,listener.getClass(),true)){
                super.registerOneShotListener(currentContext, listener, listenerID);
                return true;
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
  

    public void sendSignal(ServiceContext senderContext,
            ListenerID receiver, Signal signal) {
        if (isLocal(receiver)){
            super.sendSignal(senderContext, receiver, signal);
        }else{
            try {
                managerServer.sendSignal(senderContext,receiver,signal);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void registerEventListener(ServiceContext context, ServiceEvent eventByExample, ListenerID listenerID) {
        try{
            managerServer.registerEventListener(context, eventByExample, listenerID);
        }catch(RemoteException e){
            e.printStackTrace();
        }
     
        
    }
    
    public boolean sendEvent(ServiceContext senderContext, ServiceEvent serviceEvent, Class senderClass) {
        try{
            return managerServer.sendEvent(senderContext, serviceEvent, senderClass);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * TODO Comment method
     * @param context
     * @param receiver
     * @param signal
     */
    public void sendRemoteSignal(ServiceContext context, ListenerID receiver, Signal signal) {
        if (isLocal(receiver)){
            super.sendSignal(context, receiver, signal);
        }else{
            //TODO: the listener is already deregistered?
            System.err.println("Listener already deregistered?");
        }
        
    }
    
    /**
     * 
     * TODO Comment method
     * @param senderContext
     * @param signal
     * @param receivers
     */
    public void sendRemoteSignal(ServiceContext senderContext, Signal signal,
            Collection receivers){
        
        //if(isLocal(receivers)){
            super.sendSignal(senderContext, receivers, signal);
        //}else{
            //TODO: the listener is already deregistered?
        //}
    }
    



    public void sendSignal(ServiceContext senderContext, Collection receivers,
            Signal signal) {
        
        ArrayList local=new ArrayList();
        ArrayList remote=new ArrayList();
        Iterator iterator=receivers.iterator();
        while (iterator.hasNext()) {
            ListenerID element = (ListenerID) iterator.next();
            
        
            if (isLocal(element)){
                local.add(element);
            }else{
                remote.add(element);
            }
        }
        super.sendSignal(senderContext, local,signal);//(ListenerID[])local.toArray(new ListenerID[local.size()]), signal);
        if (!remote.isEmpty()){
            try {
                managerServer.sendSignal(senderContext, remote, signal);//(ListenerID[])remote.toArray(new ListenerID[remote.size()]));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    
    public void finishLocal(ListenerID receiver, ServiceContext callerContext) {
        super.finish(callerContext, receiver);
    }

    public void finish(ServiceContext callerContext, final ListenerID receiver) {
        if (isLocal(receiver)){
            super.finish(callerContext, receiver);
        }
        
        executionManager.schedule(new Action(callerContext,callerContext) {
            public void execute(Service executingService) {
                try {
                    managerServer.finish(receiver,getCallerContext());
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }
    

    //
    public boolean hasListener(ListenerID listenerID) {
     
        if (!super.hasListener(listenerID)){
            try {
                return managerServer.hasListener(listenerID);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
    
    private class InternalFinishListener implements ListenerFinishedHandler {

        private ListenerFinishedHandler handler;
        private ServiceContext executingService;
        private ServiceContext callingService;

        /**
         * Constructor for class InternalFinishListener 
         *
         * @param handler
         * @param executingDeviceID
         * @param executingService
         */
        public InternalFinishListener(ListenerFinishedHandler handler,  ServiceContext executingService) {
            this.handler=handler;
            this.executingService=executingService;
        }
        public void setCallingServiceContext(ServiceContext callingService){
            this.callingService=callingService;
        }
        //
        public void handleFinished(ListenerID listenerID) {
            
            
            executionManager.schedule(new ListenerAction(executingService,callingService,handler,listenerID));

        }

    }
    
    /**
     * TODO: comment class  
     * @author daniel
     **/

    private static final class ListenerAction extends Action {

        private ListenerFinishedHandler handler;
        private ListenerID finishedListener;

        /**
         * Constructor for class ListenerAction 
         *
         * @param executingServiceID
         * @param callingServiceID
         * @param callingDeviceID
         */
        public ListenerAction(ServiceContext executingService, ServiceContext callingService,
                ListenerFinishedHandler handler,ListenerID finishedListener) {
            super(executingService, callingService);
            this.handler=handler;
            this.finishedListener=finishedListener;

        }

        //
        public void execute(Service executingService) {
            handler.handleFinished(finishedListener);
        }

    }

 
    
    
    public ServiceContext getContextForListener(ListenerID listenerID) {
    	try {
			return managerServer.getContextForListener(listenerID);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public Object accessSynchronous(ServiceContext callerContext,ListenerID requestedListener, ListenerAccess listenerAccess) {
        Object returnObject;
        if (super.hasListener(requestedListener)){
            returnObject= super.accessSynchronous(callerContext, requestedListener,
                    listenerAccess);    
        }else{
            try {
                if (managerServer.hasListener(requestedListener)){
                    returnObject=managerServer.accessSynchronous(callerContext,requestedListener,listenerAccess);
                }else{
                    throw new OperatingServiceException("Requested service does not exists");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new OperatingServiceException("Requested service does not exists");
            }
            
            
        }
        return returnObject;
        
    }
  
    
  
    public Object accessSynchronous(ServiceContext callerContext, ListenerID requestedListener, ListenerAccess listenerAccess, Object object) {
        Object returnObject=null;
        synchronized(syncObject){
            if (super.hasListener(requestedListener)){
                returnObject= super.accessSynchronous(callerContext, requestedListener,
                        listenerAccess);
            }
//            ServiceContext oldContext=executionManager.setCallerContext(new ServiceContext(senderService,deviceID));
//            enterContext(requestedService);
//            returnObject=listenerAccess.handle(getServiceThread(requestedService).getService());
//            exitContext();
//            executionManager.setCallerContext(oldContext);
            
        }
        return returnObject;
    }
    
    public ListenerID registerListenerAutogen(ServiceContext context, SignalListener listener) {
        ListenerID listenerID;
        try {
            listenerID = managerServer.addAutogenListener(context,listener.getClass());
            super.registerListenerAutogen(context, listener,listenerID);
            return listenerID;
        } catch (RemoteException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        }
        return null;
 
    }


    
    
}
