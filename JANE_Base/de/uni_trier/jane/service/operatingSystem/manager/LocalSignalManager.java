/*****************************************************************************
 * 
 * LocalSignalManager.java
 * 
 * $Id: LocalSignalManager.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.operatingSystem.manager;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.reflectionSignal.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.event.EventListener;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.operatingSystem.*;

import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.util.*;



import java.lang.reflect.*;
import java.util.*;


/**
 * TODO: comment class  
 * @author daniel
 **/

public class LocalSignalManager {

    


  
    /**
     * @author goergen
     *
     * TODO comment class
     */
    public static final class AutogenListenerID extends DefaultListenerID {

        /**
         * Constructor for class <code>AutogenListenerID</code>
         * @param deviceID
         * @param id
         * @param listenerClassName
         */
        public AutogenListenerID(DeviceID deviceID, long id, String listenerClassName) {
            super(deviceID, id, listenerClassName);
            // TODO Auto-generated constructor stub
        }

        public boolean isAutogenListener() {
            return true;
        }

 
    }
    
    
    private static  class DefaultListenerID extends ListenerID {

        private long id;
        private String listenerClassName;
        private DeviceID deviceID;

        /**
         * Constructor for class DefaultListenerID 
         * @param deviceID
         *
         * @param id
         * @param listenerClassName
         */
        public DefaultListenerID(DeviceID deviceID, long id, String listenerClassName) {
            this.id=id;
            this.deviceID=deviceID;
            this.listenerClassName=listenerClassName;

        }




        public String toString() {
            return id+"("+deviceID+"):"+listenerClassName;
        }


        public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            result = PRIME * result + (int) (id >>> 32);
            result = PRIME * result + (int) (id & 0xFFFFFFFF);
            if (listenerClassName != null) {
                result = PRIME * result + listenerClassName.hashCode();
            }
            if (deviceID!=null){
                 result = PRIME * result +deviceID.hashCode();
            }

            return result;
        }

        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }

            if (!(oth instanceof DefaultListenerID)) return false;
            DefaultListenerID other = (DefaultListenerID) oth;

            if (this.id != other.id) {
                return false;
            }
            if (this.listenerClassName == null) {
                if (other.listenerClassName != null) {
                    return false;
                }
            } else {
                if (!this.listenerClassName.equals(other.listenerClassName)) {
                    return false;
                }
            } 
            if (this.deviceID == null) {
                if (other.deviceID != null) {
                    return false;
                }
            }else {
                if (!this.deviceID.equals(other.deviceID)) {
                    return false;
                }
            }

            return true;
        }


        //
        public int getCodingSize() {
            return listenerClassName.length()*8+8*8;
        }
        
        public boolean isAutogenListener(){
            return false;
        }
    }

    /**
     * @author daniel
     *
     * TODO comment class
     */
    private static final class FinishListenerAction  extends Action{

        private ListenerFinishedHandler handler;
        private ListenerID listenerID;


        /**
         * 
         * Constructor for class <code>FinishListenerAction</code>
         * @param handler
         * @param serviceContext
         * @param listenerID
         */
        public FinishListenerAction(ListenerFinishedHandler handler, ServiceContext serviceContext, ListenerID listenerID) {
            super(serviceContext, null);
            this.handler=handler;
            this.listenerID=listenerID;

        }

        	
        public void execute(Service executingService) {
            handler.handleFinished(listenerID);
            
        }


        /**
         * TODO Comment method
         * @param callerContext
         */
        public void setCallerContext(ServiceContext callerContext) {
            this.callerContext=callerContext;
            
        }

    }
    protected long firstFreeID;
    protected HashMap IDListenerMap;
    protected HashMapSet listenerIDMap;
    protected ServiceManager serviceManager;
    protected ExecutionManager executionManager;
    private HashMapSet serviceListenerMap;
    private HashSet synchronousCallSet;
    private int autoGenListenerCount;
    protected HashMap autogenListeners;
   // private ServiceContext receiverContext;
   // private RuntimeOperatingSystemImpl operatingSystem;
    private EventDB eventDB;
    

   
   
    
    
    /**
     * 
     * Constructor for class <code>LocalSignalManager</code>
     * @param eventTemplateReflectionDepth
     */
    public LocalSignalManager(int eventTemplateReflectionDepth) {
        IDListenerMap=new HashMap();
        listenerIDMap=new HashMapSet();
        firstFreeID=1;
        autoGenListenerCount=1;
        serviceListenerMap=new HashMapSet();
        synchronousCallSet=new HashSet();
        autogenListeners=new HashMap();
        if (eventTemplateReflectionDepth>0){
            eventDB=new EventDBImplementationDepth(eventTemplateReflectionDepth);
        }else if (eventTemplateReflectionDepth==0){
            eventDB=new EventDBImplementation();
        }
        
    }
    
    /**
     * 
     * TODO Comment method
     * @param executionManager
     * @param finishManager
     * @param serviceManager
     */
    public void init(ExecutionManager executionManager,FinishManager finishManager,ServiceManager serviceManager){
        this.executionManager=executionManager;        
        this.serviceManager=serviceManager;
        finishManager.addFinishListener(new FinishListener() {
            //
            public void notifyFinished(ServiceID serviceID, ServiceContext finishContext) {
                if (serviceListenerMap.containsKey(serviceID)){
                    Iterator iterator=serviceListenerMap.remove(serviceID).iterator();
                    while (iterator.hasNext()) {
                        ListenerID element = (ListenerID) iterator.next();
                        finish(finishContext,element);
                        
                    }
                }
                finish(finishContext,serviceID);
                
            }
        });
         
    }
    
    

    
    /**
     * 
     * TODO Comment method
     * @param currentContext
     * @param listener
     * @param listenerID
     * @return
     */
    public boolean registerListener(ServiceContext currentContext,SignalListener listener, ListenerID listenerID) {
        return registerListener(currentContext,listener,listenerID,false);
        
    }

    /**
     * 
     * TODO Comment method
     * @param currentContext
     * @param listener
     * @return
     */
    public ListenerID registerListener(ServiceContext currentContext,SignalListener listener) {

        ListenerID listenerID=generateID(listener.getClass());
        registerListener(currentContext,listener,listenerID,false);
        return listenerID;
    }

  

   

    /**
     * 
     * TODO Comment method
     * @param currentContext
     * @param listener
     * @return
     */
    public ListenerID registerOneShotListener(ServiceContext currentContext,SignalListener listener) {
       // ListenerID listenerID=(ListenerID)listenerIDMap.get(listener);
        //if (listenerID==null){
    	ListenerID listenerID=generateID(listener.getClass());
        //}
        registerListener(currentContext,listener,listenerID,true);
        return listenerID;
    }

    /**
     * 
     * TODO Comment method
     * @param currentContext
     * @param listener
     * @param listenerID
     * @return
     */
    public boolean registerOneShotListener(ServiceContext currentContext,SignalListener listener, ListenerID listenerID) {
        return registerListener(currentContext,listener,listenerID,true);
    }
    
    /**
     * TODO: comment method 
     * @param listenerClass
     * @return
     */
    private ListenerID generateID(Class listenerClass) {
        
        return new DefaultListenerID(serviceManager.getDeviceID(),firstFreeID++,listenerClass.getName());
    }
    
    /**
     * 
     * TODO Comment method
     * @param currentContext
     * @param listener
     * @param listenerID
     * @param oneShot
     * @return
     */
    protected boolean registerListener(ServiceContext currentContext,SignalListener listener, ListenerID listenerID, boolean oneShot) {
        if (IDListenerMap.containsKey(listenerID)) return false;
        if (listener==null){
            throw new OperatingServiceException("could not register a null listener!");
        }
        serviceListenerMap.put(currentContext.getServiceID(),listenerID);
        //ServiceID executingService=serviceInformation.getRunningService();
//        if (listener!=null&&!listenerID.getListenerClass().isInstance(listener)){
//            // ^- RemotePatch!
//            throw new OperatingServiceException("Registered listener is not a subclass of given listenerID.getListenerClass()");
//        }
        IDListenerMap.put(listenerID,new ListenerWrapper(listener,listenerID,currentContext,oneShot));
        listenerIDMap.put(listener,listenerID);
        return true;
        
    }
    
    
    /**
     * 
     * TODO Comment method
     * @param callerContext
     * @param receiver
     * @param signal
     */
    public void sendSignal(ServiceContext callerContext, ListenerID receiver, Signal signal) {
        
        sendSignalInternal(callerContext, receiver, signal,true);
        
    }

    /**
     * TODO Comment method
     * @param callerContext
     * @param receiver
     * @param signal
     * @param hardCheck 
     */
    private void sendSignalInternal(ServiceContext callerContext, ListenerID receiver, Signal signal, boolean hardCheck) {
        
        ListenerWrapper listenerWrapper;
        if (receiver instanceof AutogenListenerID){
            listenerWrapper=(ListenerWrapper)autogenListeners.get(receiver);
            
        }else{
            listenerWrapper=(ListenerWrapper)IDListenerMap.get(receiver);
        }
        if (listenerWrapper==null){
            if (serviceManager.isShuttingDown()) return;
            if (hardCheck){
                throw new OperatingServiceException("Receiver of the signal does not exist");
            }
            return;
        }
        if (!signal.getReceiverServiceClass().isInstance(listenerWrapper.getListener())){
            //listenerWrapper.getListener().getClass().getInterfaces();
//            Class superSignal=signal.getClass().getSuperclass()
//            if (Signal.class.isAssignableFrom(superSignal)){
//                MethodsuperSignal.getMethod("getReceiverServiceClass");
//            }
            if (!EventListener.class.isInstance(listenerWrapper.getListener())){// instanceof EventListener)){
                if (hardCheck){
                    throw new OperatingServiceException("Receiver of the signal does not implement the SignalReceiverClass");
                }
                return;
            }
            
        }
        Signal signalCopy=(Signal)signal.copy();
        if (signalCopy==null){
            throw new OperatingServiceException("Signal does not implement the copy() method correctly");
        }
        
        										
        executionManager.schedule(new SignalAction(listenerWrapper.getContext(),callerContext,
                listenerWrapper.getListener(),(Signal)signal.copy()));
       
        checkFinishReceiver(callerContext, receiver,listenerWrapper);
    }
    
    /**
     * 
     * TODO Comment method
     * @param callerContext
     * @param receiver
     * @param listenerWrapper
     */
    private void checkFinishReceiver(ServiceContext callerContext, ListenerID receiver, ListenerWrapper listenerWrapper) {
        if (listenerWrapper.isOneShot()){
            finish(listenerWrapper.getContext(),receiver);            
        }
    }
    
    /**
     * 
     * TODO Comment method
     * @param callerContext
     * @param receiver
     */
    public void finish(ServiceContext callerContext, ListenerID receiver) {
        if (receiver instanceof AutogenListenerID){
            autogenListeners.remove(receiver);
            return;
        }
        ListenerWrapper listenerWrapper=(ListenerWrapper)IDListenerMap.remove(receiver);
        
        if (listenerWrapper==null) return; //TODO: Exception or return false?
        listenerIDMap.remove(listenerWrapper.getListener(),receiver);
        serviceListenerMap.remove(listenerWrapper.getContext().getServiceID(),receiver);
        FinishListenerAction[] actions=listenerWrapper.finish();
        for (int i=0;i<actions.length;i++){
            actions[i].setCallerContext(callerContext);
            executionManager.schedule(actions[i]);
        }
    }
    
    /**
     * TODO Comment method
     * @param context
     * @param signalListener
     */
    public void finish(ServiceContext context, SignalListener signalListener) {
        if (signalListener instanceof ListenerStub){
            finish(context,((ListenerStub)signalListener).getListenerIDOfTheStub());
        }else{
            if (!listenerIDMap.containsKey(signalListener)) return;
            Iterator listenerIDs=listenerIDMap.remove(signalListener).iterator();
            while (listenerIDs.hasNext()) {
                ListenerID element = (ListenerID) listenerIDs.next();
                finish(context,element);
            }
        }
        
        
    }
    

    /**
     * 
     * TODO Comment method
     * @param senderContext
     * @param receivers
     * @param signal
     */
    public void sendSignal(ServiceContext senderContext, Collection receivers, Signal signal) {



	    // send the signal to each receiver
	    //int n = receivers.length;
	    //for(int i=0; i<n; i++) {
        Iterator iterator=receivers.iterator();
        while (iterator.hasNext()) {
            ListenerID element = (ListenerID) iterator.next();
            sendSignalInternal(senderContext,element,signal,false);
        
//	        ListenerWrapper listenerWrapper=(ListenerWrapper)IDListenerMap.get(element);//receivers[i]);
//	        if (listenerWrapper!=null){
//	            Signal signalCopy = (Signal)signal.copy();
//	            if (signalCopy==null){
//	                throw new OperatingServiceException("Signal does not implement the copy() method correctly");
//	            }
//	            Action action = new SignalAction(listenerWrapper.getContext(),senderContext,
//	                    listenerWrapper.getListener(),signalCopy);
//	            executionManager.schedule(action);
//	            checkFinishReceiver(senderContext,element,//,receivers[i],
//                        listenerWrapper);
//	            
//	        }else{
//	            //ooops! unregistered
//	            throw new OperatingServiceException("signal receiver does not exist");
//	            //serviceInformation.unregisterService(runningService,receivers[i],runningService.getListenerClass());
//	        }
	    }

    }
    
    
    
	/**
	 * @param context
	 * @param signalListener
	 * @param handler
	 */
	public void addFinishListener(ServiceContext context, SignalListener signalListener, ListenerFinishedHandler handler) {
        if (signalListener instanceof ListenerStub){
            addFinishListener(context,((ListenerStub)signalListener).getListenerIDOfTheStub(),handler);
        }else{
            Iterator listenerIDs=listenerIDMap.get(signalListener).iterator();
	        while (listenerIDs.hasNext()) {
	            ListenerID element = (ListenerID) listenerIDs.next();
	            addFinishListener(context,element,handler);
	        }
        
        }
		
	}
    
    /**
     * 
     * TODO Comment method
     * @param executingContext
     * @param listenerID
     * @param handler
     */
    public void addFinishListener(ServiceContext executingContext, ListenerID listenerID, ListenerFinishedHandler handler){
//        if (listenerID instanceof DeprecatedTaskHandle){
//            listenerID=((DeprecatedTaskHandle)listenerID).getListenerID();
//        }
        if (IDListenerMap.containsKey(listenerID)){
            ((ListenerWrapper)IDListenerMap.get(listenerID)).addFinishListener(handler,executingContext);
        }else{
            throw new OperatingServiceException("Listener with the given id does not exist");
        }
    }
    
	/**
     * 
     * TODO Comment method
     * @param signalListener
     * @param handler
	 */
	public void removeFinishListener(SignalListener signalListener, ListenerFinishedHandler handler) {
        if (signalListener instanceof ListenerStub){
            removeFinishListener(((ListenerStub)signalListener).getListenerIDOfTheStub(),handler);
        }else{
            Iterator listenerIDs=listenerIDMap.get(signalListener).iterator();
	        while (listenerIDs.hasNext()) {
				ListenerID element = (ListenerID) listenerIDs.next();
				removeFinishListener(element,handler);
				
			}
        }
		
	}
    
    /**
     * TODO Comment method
     * @param listenerID
     * @param handler
     */
    public void removeFinishListener(ListenerID listenerID, ListenerFinishedHandler handler){
        if (IDListenerMap.containsKey(listenerID)){
            ((ListenerWrapper)IDListenerMap.get(listenerID)).removeFinishListener(handler);
        }else{
            throw new OperatingServiceException("Listener with the given id does not exist");
        }
    }
    
    
    
    
    
    /**
     * TODO: comment class  
     * @author daniel
     **/


    
    /**
     * TODO: comment class  
     * @author daniel
     **/

    public static final class ListenerWrapper {
        private SignalListener listener;
        
        private boolean oneShot;
        private Map finishListenerMap;
        private ListenerID listenerID;
        private ServiceContext context;
        
        /**
         * 
         * Constructor for class <code>ListenerWrapper</code>
         * @param listener
         * @param listenerID
         * @param executingContext
         * @param oneShot
         */
        public ListenerWrapper(SignalListener listener, ListenerID listenerID,
                ServiceContext executingContext, boolean oneShot) {
            super();
            this.listener = listener;
            this.listenerID=listenerID;
            context=executingContext;
            this.oneShot = oneShot;
            finishListenerMap=new HashMap();
        }
        
        /**
         * TODO Comment method
         * @return
         */
        public ServiceID getExecutingService() {

            return context.getServiceID();
        }

        /**
         * TODO Comment method
         * @return
         */
        public ServiceContext getContext() {

            return context;
        }

        /**
         * 
         * TODO Comment method
         * @return
         */
        public FinishListenerAction[] finish() {
            return (FinishListenerAction[])finishListenerMap.values().toArray(new FinishListenerAction[finishListenerMap.size()]);
            
        }
        
        /**
         * @return Returns the listenerID.
         */
        public ListenerID getListenerID() {
            return listenerID;
        }
      
        /**
         * 
         * TODO Comment method
         * @return
         */
        public SignalListener getListener() {
            return listener;
        }
        
        /**
         * 
         * TODO Comment method
         * @return
         */
        public boolean isOneShot() {
            return oneShot;
        }
        
        /**
         * 
         * TODO Comment method
         * @param handler
         * @param serviceContext
         */
        public void addFinishListener(ListenerFinishedHandler handler, ServiceContext serviceContext) {
            finishListenerMap.put(handler,new FinishListenerAction(handler, serviceContext,listenerID));
        }
        
        /**
         * 
         * TODO Comment method
         * @param handler
         */
        public void removeFinishListener(ListenerFinishedHandler handler) {
            finishListenerMap.remove(handler);
        }

    }

    /**
     * TODO: comment class  
     * @author daniel
     **/

    private  final class SignalAction extends Action {

        private SignalListener listener;
        private Signal signal;

        /**
         * 
         * Constructor for class <code>SignalAction</code>
         * @param executingContext
         * @param callerContext
         * @param listener
         * @param signal
         */
        public SignalAction(ServiceContext executingContext, ServiceContext callerContext, SignalListener listener, Signal signal) {
            super(executingContext, callerContext);
            
            this.listener=listener;
            this.signal=signal;

        }





        public void execute(Service executingService) {
        	if (signal instanceof ProxySignal){
        		((ProxySignal)signal).setServiceManager(serviceManager);//RuntimeEnvironment(serviceManager.getOperatingSystem(getExecutingContext()));
        	}
            if (listener instanceof EventListener&& signal instanceof ServiceEvent){
                ((EventListener)listener).handle((ServiceEvent)signal);
            }else{
                signal.handle(listener);
            }

        }

    }

    /**
     * TODO: comment method 
     * @param listenerID
     * @return
     */
    public boolean hasListener(ListenerID listenerID) {
        return IDListenerMap.containsKey(listenerID)||
            autogenListeners.containsKey(listenerID);
    }
    
    
    /**
     * TODO Comment method
     * @param signalListener
     */
    public boolean hasListener(SignalListener signalListener) {
        if (signalListener instanceof ListenerStub){
            return hasListener(((ListenerStub)signalListener).getListenerIDOfTheStub());
        }
        return listenerIDMap.containsKey(signalListener);
        
    }


    /**
     * 
     * TODO Comment method
     * @param listenerID
     * @return
     */
    public ServiceContext getContextForListener(ListenerID listenerID) {
        ListenerWrapper wrapper=(ListenerWrapper)IDListenerMap.get(listenerID);
        if (wrapper!=null){
            return wrapper.getContext();
        }
        return null;
    }

    /**
     * TODO: comment method 
     * @param registeredListenerID
     * @return
     */
    public Class getListenerClass(ListenerID registeredListenerID) {
        if (IDListenerMap.containsKey(registeredListenerID)){
            return ((ListenerWrapper)IDListenerMap.get(registeredListenerID))
			.getListener().getClass();
        }
        return null;
        //TODO: return Object.class; //?
    }

    /**
     * TODO Comment method
     * @param listenerClass
     * @return
     */
    public List getListeners(Class listenerClass) {
        //TODO: ineffizient!
        ArrayList list=new ArrayList();
        Iterator iterator=IDListenerMap.values().iterator();
        while (iterator.hasNext()){
            ListenerWrapper listenerWrapper=(ListenerWrapper)iterator.next();
            if (listenerClass.isInstance(listenerWrapper.getListener())){
                list.add(listenerWrapper.getListenerID());
                    
                
            }
        }
        return list;//(ListenerID[])list.toArray(new ListenerID[list.size()]);
    }

    /**
     * 
     * TODO Comment method
     * @param callerContext
     * @param requestedListener
     * @param listenerAccess
     * @return
     */
    public Object accessSynchronous(ServiceContext callerContext,ListenerID requestedListener, ListenerAccess listenerAccess) {
        ListenerWrapper listenerWrapper=(ListenerWrapper)IDListenerMap.get(requestedListener);
        if (listenerWrapper==null){
            throw new OperatingServiceException("the given listener ID does not exist");
        }
        SignalListener listener=listenerWrapper.getListener();
        
        ServiceContext executionContext=getContextForListener(requestedListener);
        if (synchronousCallSet.contains(executionContext)){
            throw new OperatingServiceException("Illegal cycle in synchronous method call.");
        }
        synchronousCallSet.add(callerContext);
        ServiceInformation serviceThread=serviceManager.getServiceInformation(executionContext);
        
        if (serviceThread==null){
            throw new OperatingServiceException("Synchronous requested service does not exist");
        }
        if (!serviceThread.isAllowed(callerContext.getServiceID())){
            throw new OperatingServiceException("Synchronous request to this service is denied");
        }
        if (!listenerAccess.getReceiverServiceClass().isInstance(listener)){
        
        //listener..checkInstanceOf(listenerAccess.getReceiverServiceClass());//){
            throw new OperatingServiceException("Synchronous requested listener does not implement the given listener class");
    	}
        ListenerAccess listenerAccessCopy=null; 
        try{
            listenerAccessCopy=(ListenerAccess) listenerAccess.copy();
        }catch(Exception e){
            //NOP
        }
        if (listenerAccessCopy==null){
            throw new OperatingServiceException("Synchronous request object does not implement correct copy method");
        }
        ServiceContext oldContext=executionManager.setCallerContext(executionContext,callerContext);
        Object object=listenerAccessCopy.handle(listener);
        executionManager.setCallerContext(executionContext,oldContext);
        synchronousCallSet.remove(callerContext);
        return object;
        
    }

    /**
     * TODO Comment method
     * @param context
     * @param listener
     * @return
     */
    public ListenerID registerListenerAutogen(ServiceContext context, SignalListener listener) {
        
        ListenerID listenerID=generateListenerIDAutogen(context,listener.getClass());
        registerListenerAutogen(context,listener,listenerID);
        return listenerID;
    }

    /**
     * TODO Comment method
     * @param context
     * @param listener
     * @param listenerID
     * @return
     */
    protected void registerListenerAutogen(ServiceContext context, SignalListener listener, ListenerID listenerID) {
        autogenListeners.put(listenerID,new ListenerWrapper(listener,listenerID,context,false));
    }

    /**
     * TODO Comment method
     * @param context
     * @param listenerClass
     * @return
     */
    public ListenerID generateListenerIDAutogen(ServiceContext context, Class listenerClass) {

        return new AutogenListenerID(context.getServiceDeviceID(),autoGenListenerCount++,listenerClass.getName());
    }

    public boolean sendEvent(ServiceContext senderContext, ServiceEvent serviceEvent, Class senderClass) {
        try {
            //Field[] fields= ServiceEvent.class.getDeclaredFields();
            Field eventSenderID=ServiceEvent.class.getDeclaredField("eventSenderID");
            Field eventSenderClass=ServiceEvent.class.getDeclaredField("eventSenderClass");
//            for (int i=0;i<fields.length;i++){
//                if (fields[i].getType().equals(ServiceID.class)){
//                    eventSenderID=fields[i];
//                }else if (fields[i].getType().equals(Class.class)){
//                    eventSenderClass=fields[i];
//                }
//            }
            
            
            eventSenderID.setAccessible(true);
            eventSenderClass.setAccessible(true);
            eventSenderID.set(serviceEvent,senderContext.getServiceID());
            eventSenderClass.set(serviceEvent,senderClass);
        } catch (SecurityException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        } catch (IllegalArgumentException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        } catch (IllegalAccessException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        } catch (NoSuchFieldException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        }
        Set set=eventDB.getListeners(serviceEvent);
        if (!set.isEmpty()){
            sendSignal(senderContext,set,serviceEvent);
            return true;
        }
        return false;

        
    }

    public void registerEventListener(ServiceContext context, ServiceEvent eventByExample, ListenerID listenerID) {
        addFinishListener(context.getOSContext(), listenerID, new ListenerFinishedHandler() {
            
            public void handleFinished(ListenerID listenerID) {
                eventDB.removeEventListener(listenerID);
            }
        
        });
        eventDB.registerEventListener(eventByExample,listenerID);
        
    }
    






  



    


}
