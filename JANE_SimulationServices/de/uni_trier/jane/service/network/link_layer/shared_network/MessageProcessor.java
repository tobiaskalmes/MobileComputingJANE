/*
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.service.network.link_layer.shared_network;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerObserver.LinkLayerObserverStub;
import de.uni_trier.jane.service.network.link_layer.collision_free.DeviceInfo;
import de.uni_trier.jane.service.network.link_layer.global.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MessageProcessor {
	/**
	 * @author goergen
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	private abstract class SendEvent extends ServiceTimeout{

		protected double endTime;
		private double delta;

		/**
		 * @param time
		 */
		public SendEvent(double time) {
			super(time-operatingSystem.getSimulationTime());
			this.delta=time-operatingSystem.getSimulationTime();
			endTime=time;
			
		}
		
		/**
		 * 
		 * @param receiver
		 * @param currentTime
		 */
		public abstract void remove(DeviceID receiver, double currentTime);

		/**
		 * @return
		 */
		public abstract Set getReceiverSet();

		/**
		 * @return
		 */
		public abstract Shape getMessageShape() ;
		
		/**
		 * @return
		 */
		public double getProgress() {
			return 1-((endTime-operatingSystem.getSimulationTime())/delta);
		}
		
	}
	/**
	 * @author daniel
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */

    public boolean promisc;

	/**
	 * @author daniel
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	private class UnicastCompletedEvent extends SendEvent {

		private DeviceID receiver;
		private LinkLayerMessage linkLayerMessage;
		private UnicastCallbackHandler handle;
		private boolean failed;
		private double failingTime;
        private Set receivers;
		

		/**
		 * @param time
		 */
		public UnicastCompletedEvent(double time, DeviceID receiver, LinkLayerMessage linkLayerMessage, UnicastCallbackHandler handle) {
			super(time);
			this.receiver=receiver;
			this.linkLayerMessage=linkLayerMessage;
			this.handle=handle;
            receivers=new LinkedHashSet(neighbors.keySet());
            receivers.remove(receiver);
			failed=false;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
		 */
		public void handle() {
			free=true;
			lastSendEvent=null;
		    DeviceInfo receiverInfo=network.getDeviceInfo(receiver);
		    DeviceInfo senderInfo=network.getDeviceInfo(owner);
            linkLayerObserver.notifyUnicastProcessed(owner,receiverInfo.getLinkLayerAddress(),linkLayerMessage);
            if (handle!=null){
                handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),linkLayerMessage);
            }
			if (failed){
				if (failingTime+network.VIRTUAL_PACKET_SIZE/dataRate>endTime){
                    linkLayerObserver.notifyUnicastUndefined(owner,receiverInfo.getLinkLayerAddress(),linkLayerMessage);
                    if (handle!=null)
                        handle.notifyUnicastUndefined(receiverInfo.getLinkLayerAddress(),linkLayerMessage);
					
				}else{
                    linkLayerObserver.notifyUnicastLost(owner,receiverInfo.getLinkLayerAddress(),linkLayerMessage);
                    if (handle!=null)
                        handle.notifyUnicastLost(receiverInfo.getLinkLayerAddress(),linkLayerMessage);                    
				}
			
			
				
			}
	
			if (neighbors.containsKey(receiver)){
				MessageProcessor receiverProcessor=(MessageProcessor)neighbors.get(receiver);
				if (receiverProcessor.sendFinished(owner)){
					if (!failed){
						//network.deliver(owner,receiver,linkLayerMessage);
                        LinkLayerInfoImplementation info = 
                            createLinklayerInfo(senderInfo,receiverInfo,true);
					    if (promisc){
                            //using promiscous mode
					        Iterator iterator=receivers.iterator();
                            while (iterator.hasNext()){
                                operatingSystem.sendSignal((DeviceID) iterator.next(),receiverInfo.getServiceID(),
                                        new GlobalLinkLayerMessageReceiver.ReceiveSignal(info,linkLayerMessage));
                            }
                            
                        }
					    operatingSystem.sendSignal(receiverInfo.getDeviceID(),receiverInfo.getServiceID(),
					            new GlobalLinkLayerMessageReceiver.ReceiveSignal(info,linkLayerMessage));
						networkStatistic.unicastReceived(owner,receiver);
                        linkLayerObserver.notifyUnicastReceived(owner,receiverInfo.getLinkLayerAddress(),linkLayerMessage);
                        if (handle!=null)
                            handle.notifyUnicastReceived(receiverInfo.getLinkLayerAddress(),linkLayerMessage);
					
						
					}
				}else {
                    linkLayerObserver.notifyUnicastUndefined(owner,receiverInfo.getLinkLayerAddress(),linkLayerMessage);
                    if (handle!=null)
                        handle.notifyUnicastUndefined(receiverInfo.getLinkLayerAddress(),linkLayerMessage);
					networkStatistic.unicastCollission(owner,receiver);
				}
			}else{
				if (!failed){
                    linkLayerObserver.notifyUnicastLost(owner,receiverInfo.getLinkLayerAddress(),linkLayerMessage);
                    if (handle!=null)
                        handle.notifyUnicastLost(receiverInfo.getLinkLayerAddress(),linkLayerMessage);
				    
				}
			}
			Iterator iter =neighbors.values().iterator();
			while (iter.hasNext()){
				((MessageProcessor)iter.next()).sendFinished(owner);
			}
			sendFinished(owner);
			medium.unicastFinished();
			if (operatingSystem.hasListener(handle)){
			   // operatingSystem.sendCallback(handle,new UnicastCallbackHandler.UnicastProcessedCallback(receiverInfo.getLinkLayerAddress(),linkLayerMessage));
			    operatingSystem.finishListener(senderInfo.getDeviceID(),handle);
			}
			network.sendFinished(owner);
			

		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.sharednetwork.MessageProcessor.SendEvent#remove(de.uni_trier.ubi.appsim.kernel.basetype.Address)
		 */
		public void remove(DeviceID receiver, double time) {
			if (!failed&&receiver.equals(this.receiver)){
				failed=true;
				failingTime=time;
				
				operatingSystem.removeTimeout(this);
				
				
				handle();
				
			}
            receivers.remove(receiver);
		}
		
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.sharednetwork.MessageProcessor.SendEvent#getReceiverSet()
		 */
		public Set getReceiverSet() {
			Set receivers=new LinkedHashSet();
			if (!failed){
				receivers.add(receiver);
			}
			return receivers;
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.sharednetwork.MessageProcessor.SendEvent#getMessageShape()
		 */
		public Shape getMessageShape() {
		
			return linkLayerMessage.getShape();
		}

	}
	/**
	 * @author daniel
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	private class BroadcastCompletedEvent extends SendEvent {

	    private LinkLayerMessage linkLayerMessage;
		private BroadcastCallbackHandler handle;
		private Set receivers;

		/**
		 * 
		 * @param time
		 * @param linkLayerMessage
		 * @param handle
		 * @param receivers
		 */
		public BroadcastCompletedEvent(double time,LinkLayerMessage linkLayerMessage, BroadcastCallbackHandler handle, Set receivers) {
			
			super(time);
			this.receivers=receivers;
			this.handle=handle;
			this.linkLayerMessage=linkLayerMessage;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.Event#handleInternal()
		 */
		public void handle() {
lastSendEvent=null;
			free=true;
            DeviceInfo senderInfo=network.getDeviceInfo(owner);
            linkLayerObserver.notifyBroadcastProcessed(owner,linkLayerMessage);
			if (handle!=null){
			    handle.notifyBroadcastProcessed(linkLayerMessage);
			    operatingSystem.finishListener(senderInfo.getDeviceID(),handle);
				
			}
			Iterator iterator=neighbors.keySet().iterator();
		    
			while(iterator.hasNext()){
				DeviceID current=(DeviceID)iterator.next();
				MessageProcessor currentProcessor=(MessageProcessor)neighbors.get(current);
				if (currentProcessor.sendFinished(owner)){
					if (receivers.contains(current)){
						//network.deliver(owner,current,linkLayerMessage);
					    DeviceInfo receiverInfo=network.getDeviceInfo(current);
					    operatingSystem.sendSignal(receiverInfo.getDeviceID(),receiverInfo.getServiceID(),
					            new GlobalLinkLayerMessageReceiver.ReceiveSignal(
                                        createLinklayerInfo(senderInfo,receiverInfo,false),
					                    linkLayerMessage));
						networkStatistic.broadcastReceived(owner,current);
					}
				}else{
					networkStatistic.broadcastCollision(owner,current);
				}
				
			}
			sendFinished(owner);
			medium.broadcastFinished();
			network.sendFinished(owner);
			
			
			
			

		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.sharednetwork.MessageProcessor.SendEvent#getMessageShape()
		 */
		public Shape getMessageShape() {
		
			return linkLayerMessage.getShape();
		}

		
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.sharednetwork.MessageProcessor.SendEvent#remove(de.uni_trier.ubi.appsim.kernel.basetype.Address, double)
		 */
		public void remove(DeviceID receiver, double currentTime) {
			receivers.remove(receiver);
			
		}
		
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.sharednetwork.MessageProcessor.SendEvent#getReceiverSet()
		 */
		public Set getReceiverSet() {
		
			return receivers;
		}

	}
	/**
	 * @author daniel
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	private class BroadcastSendHandler implements SendHandler {
	    private LinkLayerMessage linkLayerMessage;
		private BroadcastCallbackHandler handle;
		private boolean handled;
		
		

		/**
		 * 
		 * @param linkLayerMessage
		 * @param handle
		 */
		public BroadcastSendHandler(
		        LinkLayerMessage linkLayerMessage,
		        BroadcastCallbackHandler handle) {
			this.linkLayerMessage=linkLayerMessage;
			this.handle=handle;
			handled=false;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.network.SendHandler#handle()
		 */
		public void handle() {
			if (!handled){
				handled=true;
			
				double endTime=linkLayerMessage.getSize()/dataRate+operatingSystem.getSimulationTime();
				//sendInfo=new SendInfo(endTime,protocolMessage.getShape());
				lastSendEvent=new BroadcastCompletedEvent(endTime, linkLayerMessage,handle, new LinkedHashSet(neighbors.keySet()));
				operatingSystem.setTimeout(lastSendEvent);
				//if (!currentSenders.isEmpty()) throw new IllegalStateException("Unable to start Broadcast, Medium already in use!!");
				
				sendingMessage(owner);
				Iterator iterator=neighbors.values().iterator();
				while (iterator.hasNext()){
					MessageProcessor currentNeighbor=(MessageProcessor)iterator.next();
					currentNeighbor.sendingMessage(owner);
				}
			}
			
			//receiveScheduler.add(protocolMessage.getSize()/dataRate,1.0,owner);

		}

	}
	
	private class UnicastSendHandler implements SendHandler{
	    private DeviceID receiver;
	    private LinkLayerMessage linkLayerMessage;
		private UnicastCallbackHandler handle;
		private boolean handled;

		/**
		 * 
		 * @param receiver
		 * @param linkLayerMessage
		 * @param handle
		 */
		public UnicastSendHandler(
		        DeviceID receiver,
				LinkLayerMessage linkLayerMessage,
				 UnicastCallbackHandler handle) {
			
			this.receiver = receiver;
			this.linkLayerMessage=linkLayerMessage;
			this.handle=handle;
			handled=false;
		}		
		
		public void handle() {
			if (!handled) {
								
			
				handled=true;
				double endTime=(linkLayerMessage.getSize()/dataRate)+operatingSystem.getSimulationTime();
				//sendInfo=new SendInfo(receiver,endTime,protocolMessage.getShape());
				lastSendEvent=new UnicastCompletedEvent(endTime, receiver, linkLayerMessage,handle);
				
				//if (!currentSenders.isEmpty()) throw new IllegalStateException("Unable to start Unicast, Medium already in use!!");
				sendingMessage(owner);
				Iterator iterator=neighbors.values().iterator();
				while (iterator.hasNext()){
					MessageProcessor currentNeighbor=(MessageProcessor)iterator.next();
					currentNeighbor.sendingMessage(owner);
				}
				
				eventReceiverMap.put(receiver,lastSendEvent);
				if (!neighbors.containsKey(receiver)||!((MessageProcessor)neighbors.get(receiver)).neighbors.containsKey(owner)){
					lastSendEvent.handle();
				}else{
					
					operatingSystem.setTimeout(lastSendEvent);
				}

			}
		}
	}
	
	protected Map eventReceiverMap;
	
	protected Map neighbors;
	private  Set currentSenders;
	protected Medium medium;
//	private UnicastSendHandler pendingMessage;
	protected boolean free;
	protected double dataRate;

	
	protected SharedNetwork network;
	private Set collisionSenders;
	//private SendInfo sendInfo;
	protected NetworkStatistic networkStatistic;
	protected SendEvent lastSendEvent;

	protected GlobalOperatingSystem operatingSystem;

    protected DeviceID owner;

    protected LinkLayerObserverGlobalStub linkLayerObserver;
	

	
	/**
     * 
     * Constructor for class <code>MessageProcessor</code>
     *
     * @param address
     * @param dataRate
     * @param operatingSystem
     * @param network
     * @param timestampCreator
     * @param networkStatistic
     * @param promisc
	 */
	public MessageProcessor(DeviceID address, double dataRate,
            GlobalOperatingSystem operatingSystem, SharedNetwork network,
            GlobalTimestampCreator timestampCreator,
            NetworkStatistic networkStatistic, boolean promisc) {
		eventReceiverMap=new HashMap();
		this.networkStatistic=networkStatistic;
		this.network=network;

		this.operatingSystem=operatingSystem;
		this.dataRate=dataRate;
        this.promisc=promisc;
		owner=address;
		
		medium=new Medium(owner,operatingSystem,timestampCreator, networkStatistic);
		currentSenders=new LinkedHashSet();
		collisionSenders=new LinkedHashSet();
		neighbors=new HashMap();
		free=true;
        linkLayerObserver=new LinkLayerObserverGlobalStub(operatingSystem);
		
	}
	
	/**
	 * 
	 * @param sender
	 * @return	false in case of a collision
	 */
	protected boolean sendFinished(DeviceID sender) {
		if (currentSenders.contains(sender)){
			currentSenders.remove(sender);
			
			if (!collisionSenders.isEmpty()){
				collisionSenders.remove(sender);
				return false;
			}
			return true;
			
		}
		return false;
	}

	/**
	 * 
	 * @param sender
	 */
	protected void sendingMessage(DeviceID sender) {
		if (!currentSenders.isEmpty()){
			if (collisionSenders.isEmpty()){
				Iterator iterator=currentSenders.iterator();
				while (iterator.hasNext()){
					collisionSenders.add(iterator.next());
				}
			}
			collisionSenders.add(sender);
		}
		currentSenders.add(sender);
		
		
		
	}
	/**
	 * 
	 * @param receiver
	 * @param message
	 * @param handle
	 */
	public void sendUnicast(DeviceID receiver,LinkLayerMessage message, UnicastCallbackHandler handle){
		free=false;
		medium.reserveUnicast(receiver,new UnicastSendHandler(receiver,message,handle));
		
		
		
	}
	
	/**
	 * 
	 * @param message
	 * @param handle
	 */
	public void sendBroadcast(LinkLayerMessage message, BroadcastCallbackHandler handle){
		free=false;
		medium.reserveBroadcast(new BroadcastSendHandler(message,handle));
	}
	
	/**
	 * 
	 * @return	true if messageProcessor is free
	 */
	public boolean isFree(){
		return free;
	}

	/**
	 * 
	 */
	public void notifyExit() {
		if (currentSenders.contains(owner)){
			currentSenders.remove(owner);
			Iterator iterator=neighbors.values().iterator();
			while(iterator.hasNext()){
				((MessageProcessor)iterator.next()).sendFinished(owner);
			}
		}
		if (lastSendEvent!=null){
		    operatingSystem.removeTimeout(lastSendEvent);
		}
		medium.notifyExit();
		
	}



	/**
	 * 
	 * @param receiver
	 * @param processor
	 */
	public void notifyAttach(DeviceID receiver,  MessageProcessor processor) {
		if (currentSenders.contains(owner)){
			processor.sendingMessage(owner);
		}
		neighbors.put(receiver,processor);
		medium.addNeighbor(receiver,processor.medium);
		
	}

	/**
	 * @param receiver
	 */
	public void notifyDetach(DeviceID receiver) {

		
		MessageProcessor messageProcessor=(MessageProcessor)neighbors.remove(receiver);
		if (currentSenders.contains(owner)){
			messageProcessor.sendFinished(owner);
		}
		medium.removeNeighbor(receiver);
		if (lastSendEvent!=null){
			lastSendEvent.remove(receiver,operatingSystem.getSimulationTime());
		}
		
		
	}



    /**
     * @return
     */
    public Shape getShape() {
    	if (currentSenders.contains(owner)&&lastSendEvent!=null){
    		//MovingShape shape=new MovingShape(lastSendEvent.getMessageShape());
    		Shape messageShape=lastSendEvent.getMessageShape();
    		if (messageShape==null){
    			messageShape=new RectangleShape(new Extent(5,5),Color.BLACK,false);
    		}
			//SendingVisualizationInfo sendVisInfo;
    	    ShapeCollection shape=new ShapeCollection();
    	    GlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
    	   Position ownerPosition=globalKnowledge.getTrajectory(owner).getPosition();
    	    Iterator iterator=lastSendEvent.getReceiverSet().iterator();
    	    while (iterator.hasNext()){
    	        Position receiverPosition=globalKnowledge.getTrajectory((DeviceID)iterator.next()).getPosition();
    	        Position sr=receiverPosition.sub(ownerPosition);
    	        sr=sr.scale(lastSendEvent.getProgress());
    	        sr=ownerPosition.add(sr);
//    	    	MovingShape movingShape=new MovingShape(lastSendEvent.getMessageShape()); 
 //   	    	movingShape.setProgressInfo(owner,(DeviceID)iterator.next(),lastSendEvent.getProgress());
  //  	        shape.addShape(movingShape);
    	        shape.addShape(messageShape,sr);
    	        
    	    }
    	    

			return shape; 
			//new SendingVisualizationInfo(true, lastSendEvent.getProgress(),new AddressSetImplementation(lastSendEvent.getReceiverSet()),lastSendEvent.getMessageShape());
		}
		return EmptyShape.getInstance();
		//SendingVisualizationInfo(false,0,null,null);
    }

    /**
     * TODO Comment method
     * @param promiscuous
     */
    public void setPromiscuous(boolean promiscuous) {
        promisc=promiscuous;
        
    }
    
    /**
     * @return Returns the promisc.
     */
    public boolean isPromiscuous() {
        return this.promisc;
    }
	
    private LinkLayerInfoImplementation createLinklayerInfo(DeviceInfo sender, DeviceInfo receiver, boolean unicast) {
        double signalStrength=java.lang.Double.MAX_VALUE;
        if (!sender.equals(receiver)){
            GlobalKnowledge gk=operatingSystem.getGlobalKnowledge();
            signalStrength=1/gk.getTrajectory(sender.getDeviceID()).getPosition().distance(gk.getTrajectory(receiver.getDeviceID()).getPosition());
        }
        return new LinkLayerInfoImplementation(sender.getLinkLayerAddress(),receiver.getLinkLayerAddress(),unicast, signalStrength);
    }
	

}
