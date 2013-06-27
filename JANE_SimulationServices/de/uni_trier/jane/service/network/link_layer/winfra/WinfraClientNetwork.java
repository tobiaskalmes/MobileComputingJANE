package de.uni_trier.jane.service.network.link_layer.winfra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDIterator;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
import de.uni_trier.jane.basetypes.TrajectoryMapping;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.network.link_layer.BroadcastCallbackHandler;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfoImplementation;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.LinkLayerObserverGlobalStub;
import de.uni_trier.jane.service.network.link_layer.LinkLayerProperties;
import de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler;
import de.uni_trier.jane.service.network.link_layer.collision_free.DeviceInfo;
import de.uni_trier.jane.service.network.link_layer.collision_free.InternalReceiveListener;
import de.uni_trier.jane.service.network.link_layer.collision_free.ReceiveListener;
import de.uni_trier.jane.service.network.link_layer.collision_free.ReceiveScheduler;
import de.uni_trier.jane.service.network.link_layer.global.GlobalLinkLayer;
import de.uni_trier.jane.service.network.link_layer.global.GlobalLinkLayerMessageReceiver;
import de.uni_trier.jane.service.network.link_layer.global.GlobalLinkLayer_sync;
import de.uni_trier.jane.service.network.link_layer.global.GlobalNetworkLinkLayerProxy;
import de.uni_trier.jane.service.network.link_layer.global.randomReject.AllRecieveDecider;
import de.uni_trier.jane.service.network.link_layer.global.randomReject.ReceiveDecider;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceBS;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceUCN;
import de.uni_trier.jane.service.network.link_layer.winfra.signals.InquireMessageTypeSignal;
import de.uni_trier.jane.service.network.link_layer.winfra.signals.TellingMessageResponseSignal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.global_knowledge.DeviceListener;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.global_knowledge.LinkListener;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.ArrowShape;
import de.uni_trier.jane.visualization.shapes.EllipseShape;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;


/**
 * 
 * @author christian.hiedels
 * 
 * This is a simple implementation of a Wireless single-hop infrastructured Network.
 * It supports message Queueing and works in combination with another Network which
 * organizes connected Basestations and Communication between Base Stations.
 * There are no packet collisions and the transmission rate does not change, if the
 * network is under high load. The link reliability is assumed to be 100%, thus no
 * message will be lost.
 */
public class WinfraClientNetwork implements
GlobalLinkLayer_sync,GlobalLinkLayer, GlobalService, InterfaceUCN, TellingMessageResponseSignal {

	private static final Shape DEFAULT_MESSAGE_SHAPE = new EllipseShape(Position.NULL_POSITION, new Extent(4,4), Color.GREY, true);

	private double dataRate;
	private NetworkGraph graph;
	private MessageProcessor processor;
	private MessageQueue queue;

	private ReceiveScheduler scheduler;
	private Set addressSet;

	private Set clientSet;	// <deviceID>
	private Set baseStationSet;	// <deviceID>

	/**
	 * stores the basestations, with which the clients are connected
	 * - format: client deviceID / bs deviceID
	 */
	private HashMap connectedClients;
	/**
	 * connections between basestations and clients, that may become useful later
	 * - format: client deviceID / ArrayList of bs deviceIDs
	 */
	private HashMap additionalConnections;
	/**
	 * stores the device id of a basestation and its operating system to be able to generate stubs
	 * - format: deviceID / runtimeOperatingSystem
	 */ 
	private HashMap baseStationSystems;

	/**
	 * A HashMap containing stored messages for sending to devices, which are
	 * maybe available at a later time.
	 * - format: deviceID / LinkLayerMessage
	 */
	private HashMap storedMessages;
	
	// A Stub to a BaseStation
	InterfaceBS.BSStub bs;


    protected GlobalOperatingSystem operatingSystem;
    private GlobalKnowledge globalKnowledge;

    private HashMap deviceIDInfoMap;

    private HashMap addressInfoMap;
    
    private ServiceID serviceID;

    private boolean visualizeMessages;

    private boolean visualizeSendingRadius;

    private boolean visualizeCommunicationLinks;

    private LinkLayerObserverGlobalStub linkLayerObserver;

    public ReceiveDecider receiveDecider;
    
	// The ServiceID of the Message Knowledge Service
	ServiceID messageKnowledgeServiceID = null;
    
    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, 10240*8);
    }

    public static void createInstance(ServiceUnit serviceUnit, double dataRate) {
    	createInstance(serviceUnit, dataRate, true, true, true);
    }
    
    public static void createInstance(ServiceUnit serviceUnit, double dataRate, boolean visualizeMessages,boolean visualizeSendingRadius,boolean visualizeCommunicationLinks) {
    	serviceUnit.addService(new WinfraClientNetwork(new EndpointClassID(WinfraClientNetwork.class.getName()), dataRate, new AllRecieveDecider(), visualizeMessages, visualizeSendingRadius, visualizeCommunicationLinks));
    	
   		GlobalNetworkLinkLayerProxy.createFactory(serviceUnit);
    }

    /**
	 * Construct a new <code>WinfraClientNetwork</code> object.
	 * @param dataRate the transmission rate in Bits per second. 
	 */
	public WinfraClientNetwork(double dataRate) {
	    this( new EndpointClassID(WinfraClientNetwork.class.getName()),dataRate,new AllRecieveDecider(),true,true,true);
	}
	
	/**
     * Constructor for class <code>WinfraClientNetwork</code>
     * 
     */
    public WinfraClientNetwork(ServiceID serviceID, double dataRate, ReceiveDecider receiveDecider, boolean visualizeMessages,boolean visualizeSendingRadius,boolean visualizeCommunicationLinks) {
        this.receiveDecider = receiveDecider;
    	this.visualizeMessages = visualizeMessages;
        this.visualizeSendingRadius = visualizeSendingRadius;
        this.visualizeCommunicationLinks = visualizeCommunicationLinks;
        this.deviceIDInfoMap = new HashMap();
	    this.addressInfoMap = new HashMap();
	    this.connectedClients = new HashMap();
	    this.additionalConnections = new HashMap();
		this.dataRate = dataRate;
//		this.serviceID = new EndpointClassID(getClass().getName());
		this.serviceID = serviceID;
		this.graph = new NetworkGraph();
		this.processor = new MessageProcessor();
		this.queue = new MessageQueue();
		this.addressSet = new HashSet();
		this.clientSet = new HashSet();
		this.baseStationSet = new HashSet();
		this.baseStationSystems = new HashMap();
		this.storedMessages = new HashMap();
    }
    
    /**
     * Start Method
     */
    public void start(final GlobalOperatingSystem operatingSystem) {
        this.operatingSystem=operatingSystem;
        operatingSystem.registerSignalListener(GlobalLinkLayer.class);
        operatingSystem.registerAccessListener(GlobalLinkLayer_sync.class);
        globalKnowledge = operatingSystem.getGlobalKnowledge();
        linkLayerObserver=new LinkLayerObserverGlobalStub(operatingSystem);
        // check if some kind of messageknowledge service is running
		if( operatingSystem.hasService(MessageKnowledge.class) && this.messageKnowledgeServiceID == null )
			this.messageKnowledgeServiceID = operatingSystem.getServiceIDs(MessageKnowledge.class)[0];

        globalKnowledge.addDeviceListener(new DeviceListener() {

            public void enter(DeviceID deviceID) {
                addressSet.add(deviceID);
            }

            public void exit(DeviceID deviceID) {
           		addressSet.remove(deviceID);
           		processor.remove(deviceID);
           		queue.clean(deviceID);
                Address address=((DeviceInfo)deviceIDInfoMap.get(deviceID)).getLinkLayerAddress();
                deviceIDInfoMap.remove(deviceID);
                addressInfoMap.remove(address);
            }
            
            public void changeTrack(DeviceID deviceID,
                    TrajectoryMapping trajectoryMapping, boolean suspended) {/*ignore*/}
        });
        // note: most of the calculated links are discarded and the sender is always the one, which 'sending range border' was traversed !
        //
        // note also: only one link between a basestation and a client is drawn (i.e. stored in the graph), but other links
        // are stored also in the additionalConnections HashMap, which has the form <client deviceID / ArrayList of bs deviceIDs>.
        // the calculation maintains this HashMap too, for if a link breaks, but the client is actually in the vicinity of another
        // 'fallback' basestation, this link is added and displayed in the graph immediately, even when the node was in the range
        // of the station for some time already... this means, a link to a base station is kept up as long as possible
        
        globalKnowledge.addLinkListener(new LinkListener() {
            /**
             * Occurs when the LinkCalculator has calculated a disconnection between two devices
             */
            public void handleDetach(DeviceID sender, DeviceID receiver) {
            	if( baseStationSet.contains( sender ) ) {	// sender is a baseStation
            		if( baseStationSet.contains( receiver ) ) {	// receiver is a baseStation
            			// nop - base stations won't be detached
            		}
            		else if( clientSet.contains( receiver ) ) {	// receiver is a client
            			if( graph.isConnected( sender, receiver ) ) {
	            			removeBidirectionalLink(sender, receiver);
	    					connectedClients.remove( receiver );
	            			if( additionalConnections.containsKey( receiver ) ) {
	            				ArrayList list = (ArrayList)additionalConnections.get( receiver );
		        				if( list.size() != 0 ) {
		        					DeviceID fallbackStation = (DeviceID)(list.remove(0));
		        					storeBidirectionalLink( fallbackStation, receiver );
		        					connectedClients.put( receiver, fallbackStation );
		        				}
	            			}
            			}
            			else { // remove a previously stored link to a fallbackStation that is no longer used
            				if( additionalConnections.containsKey( receiver )) {
            					ArrayList list = (ArrayList)additionalConnections.get( receiver );
            					if( list.size() != 0 ) {
            						for( int i = 0; i < list.size(); i++ ) {
            							if( sender.compareTo( (DeviceID)list.get(i)) == 0 ) {
            								list.remove(i);
            							}
            						}
            					}
            				}
            			}
            			// check if the client needs to get stored messages 
            			if( storedMessages.containsKey( receiver ) ) {
            				sendAStoredUnicastMessage( receiver, (LinkLayerMessage)storedMessages.get( receiver ) );
            				storedMessages.remove( receiver );
            			}
            		}
            	}
            	else {	// sender is a client
            		if( baseStationSet.contains( receiver ) ) { // receiver is a baseStation
            			// nop
            		}
            		else if( clientSet.contains( receiver ) ) {	// receiver is also a client
            			// nop - ignore detach events of two clients
            		}
            	}
            }
            
            /**
             * Occurs when the LinkCalculator has calculated a link between two devices
             */
            public void handleAttach(DeviceID sender, DeviceID receiver) {
            	if( baseStationSet.contains( sender ) ) {	// sender is a baseStation
            		if( baseStationSet.contains( receiver ) ) { // receiver is a baseStation
            			// nop
            		}
            		else if( clientSet.contains( receiver ) )  {	// receiver is a client
            			if( !connectedClients.containsKey( receiver ) ) {
            				storeBidirectionalLink( sender, receiver );
            				connectedClients.put( receiver, sender );
            			}
            			else {	// sender is connected already, but store all other links nethertheless! (fallback links)
            				if( additionalConnections.containsKey( receiver ) ) {	// there are some other links already .. append the new one
	        					ArrayList al = (ArrayList)additionalConnections.get( receiver );
	        					al.add( sender );
	        					additionalConnections.put( receiver, al );
            				} else {											// this link is the first one ... store it
	        					ArrayList al = new ArrayList();
	        					al.add( sender );
	        					additionalConnections.put( receiver, al );
            				}
            			}
            			// check if the client needs to get stored messages 
            			if( storedMessages.containsKey( receiver ) ) {
            				sendAStoredUnicastMessage( receiver, (LinkLayerMessage)storedMessages.get( receiver ) );
            				storedMessages.remove( receiver );
            			}
            		}
            	}
            	else if( clientSet.contains( sender ) ) {	// sender is a client
            		if( baseStationSet.contains( receiver ) ) { // receiver is a baseStation
            			// nop
            		}
            		else if( clientSet.contains( receiver ) ) {	// receiver is a client
            			// nop - do not link two clients !
            		}
            	}
            }
        });
        
        DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
        while (iterator.hasNext()){
        	addressSet.add(iterator.next());
        }
        iterator = globalKnowledge.getNodes().iterator();
        while (iterator.hasNext()){
            DeviceID deviceID = iterator.next();
            
            DeviceIDIterator deviceIDIterator = globalKnowledge.getConnected(deviceID);
            while (deviceIDIterator.hasNext()){
                notifyAttach(deviceID, deviceIDIterator.next());
            }
        }
        
		scheduler=new ReceiveScheduler(operatingSystem);
		scheduler.initialize(new ReceiveListener() {
			public void notifyFinished(DeviceID sender) {
				deliver(sender);
			}
		}, new InternalReceiveListener() {
            public void notifyBroadcastFinished(DeviceID sender, LinkLayerMessage message) {
                deliverInternalBroadcast(sender,message);
            }
            
            public void notifyUnicastFinished(DeviceID sender, LinkLayerMessage message, UnicastCallbackHandler handle) {
                deliverInternalUnicast(sender,message,handle);
            }
		});
    }

    private void storeBidirectionalLink( DeviceID sender, DeviceID receiver ) {
    	notifyAttach( sender, receiver );
    	notifyAttach( receiver, sender);
    }
    
    private void removeBidirectionalLink( DeviceID sender, DeviceID receiver ) {
    	notifyDetach( sender, receiver );
    	notifyDetach( receiver, sender );
    }
    
	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	private void notifyAttach(DeviceID sender, DeviceID receiver) {
		graph.addEdge(sender, receiver);
	}

	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	private void notifyDetach(DeviceID sender, DeviceID receiver) {

		graph.removeEdge(sender, receiver);
		processor.removeReceiver(sender, receiver);
	}

	/**
	 * Method deliver calls the deliver method of the processer for a sender
	 */
	protected void deliver(DeviceID sender) {
		processor.deliver(sender);
		if(!queue.isEmpty(sender)) {
			queue.remove(sender).handle();
		}
	}

	/**
	 * A method for delivering internal signals 
	 */
	protected void deliverInternalUnicast(DeviceID sender, LinkLayerMessage message, UnicastCallbackHandler unicastCallbackHandler) {
	    DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
	    operatingSystem.sendSignal(deviceInfo.getDeviceID(),deviceInfo.getServiceID(),
	            new GlobalLinkLayerMessageReceiver.ReceiveSignal(
	                    createLinklayerInfo(deviceInfo,deviceInfo,true),message));
	    if (unicastCallbackHandler!=null){
            linkLayerObserver.notifyUnicastProcessed(sender,deviceInfo.getLinkLayerAddress(),message);
            linkLayerObserver.notifyUnicastReceived(sender,deviceInfo.getLinkLayerAddress(),message);
            unicastCallbackHandler.notifyUnicastProcessed(deviceInfo.getLinkLayerAddress(),message);
            unicastCallbackHandler.notifyUnicastReceived(deviceInfo.getLinkLayerAddress(),message);
	        operatingSystem.finishListener(deviceInfo.getDeviceID(),unicastCallbackHandler);
	    }	        
	}
    protected void deliverInternalBroadcast(DeviceID sender, LinkLayerMessage message) {
        DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
        operatingSystem.sendSignal(deviceInfo.getDeviceID(),deviceInfo.getServiceID(),
                new GlobalLinkLayerMessageReceiver.ReceiveSignal(
                        createLinklayerInfo(deviceInfo,deviceInfo,false),message));
        linkLayerObserver.notifyBroadcastProcessed(deviceInfo.getDeviceID(),message);
        
    }

	private void handleEntry( DeviceID sender, MessageQueueEntry entry ) {
		if (queue.isEmpty(sender)) {
			if (processor.isBusy(sender)) {
				queue.add(sender, entry);
			} else {
				entry.handle();
			}
		} else {
			queue.add(sender, entry);
		}
	}

	/**
	 * The calculated links of the LinkCalculator are stored in this class. Every other method (even the getShape method)
	 * uses this class, which stores only the Links, that are of real interest for this Network.
	 */
	private static class NetworkGraph {
		private HashMap senderReceiverSetMap;	// <Sender/Receiver>
		public NetworkGraph() {
			senderReceiverSetMap = new HashMap();
		}
		public void addEdge(DeviceID sender, DeviceID receiver) {
			HashSet receiverSet = (HashSet)senderReceiverSetMap.get(sender);
			if(receiverSet == null) {
				receiverSet = new HashSet();
				senderReceiverSetMap.put(sender, receiverSet);
			}
			receiverSet.add(receiver);
		}
		public void removeEdge(DeviceID sender, DeviceID receiver) {
			HashSet receiverSet = (HashSet)senderReceiverSetMap.get(sender);
			if(receiverSet != null) {
				receiverSet.remove(receiver);
			}
		}
		public boolean isConnected(DeviceID sender, DeviceID receiver) {
			HashSet receiverSet = (HashSet)senderReceiverSetMap.get(sender);
			if(receiverSet != null) {
				return receiverSet.contains(receiver);
			}
			else {
				return false;
			}
		}
		public DeviceID[] getReceiverSet(DeviceID sender) {
			DeviceID[] result;
			HashSet receiverSet = (HashSet)senderReceiverSetMap.get(sender);
			if(receiverSet != null) {
			    result=(DeviceID[])receiverSet.toArray(new DeviceID[receiverSet.size()]);
				
			}else{
			    result=new DeviceID[0];
			}
			return result;
		}
	}

	private class MessageProcessor {
		private HashMap senderPendingMessageMap;
		public MessageProcessor() {
			senderPendingMessageMap = new HashMap();
		}
		
		public Shape getMessageShape(DeviceID sender) {
			PendingMessage pm = (PendingMessage)senderPendingMessageMap.get(sender);
			if(pm == null) {
				return null;
			}
			else {
				return pm.getMessageShape();
			}
		}
		
		public DeviceID[] getReceiverSet(DeviceID sender) {
			PendingMessage pm = (PendingMessage)senderPendingMessageMap.get(sender);
			if(pm == null) {
				return null;
			}
			else {
				return pm.getReceiverset();
			}
		}
		/**
		 * Sets a Pending Unicast Message in the pendingUnicastMessage Map for further processing.
		 * Check also if the sender and the receiver of the message is connected at all.
		 * (This Method is called through the handle() of the UnicastMessageQueueEntry())
		 */
		public void setUnicastMessage(DeviceID sender, LinkLayerMessage message, DeviceID receiver, DeviceID finalReceiver, UnicastCallbackHandler handle) {
			PendingUnicastMessage pendingUnicastMessage = new PendingUnicastMessage( message, sender, receiver, finalReceiver, handle );
			if( clientSet.contains( sender ) && !graph.isConnected( sender, receiver )) {
				pendingUnicastMessage.removeReceiver( receiver );
			}
			if( baseStationSet.contains( sender ) && !graph.isConnected( receiver, sender )) {
				pendingUnicastMessage.removeReceiver( receiver );
				// Note: at this position it could be added as well, that a basestation which is no longer connected to a client
				// but was connected, as the message was created, is changed to a bs which is connected now to the receiver.
				// this means: it could happen, that the message does not have to simply vanish, but could be conserved and transmitted successfully
				// by determining the current basestation of the receiver ... anyway, artificial case in reality, especially, when
				// the transmission data rate is set to a realistic value!
			}

			senderPendingMessageMap.put( sender, pendingUnicastMessage );
		}
		
		public void setBroadcastMessage(DeviceID sender, LinkLayerMessage message, DeviceID[] receivers, BroadcastCallbackHandler handle) {
			senderPendingMessageMap.put(sender, new PendingBroadcastMessage(message, sender, receivers, handle));
		}
		
		// Returns true if the processor has a pending message for the given sender
		public boolean isBusy(DeviceID sender) {
			return senderPendingMessageMap.get(sender) != null;
		}
		
		public void removeReceiver(DeviceID sender, DeviceID receiver) {
			PendingMessage pendingMessage = ((PendingMessage)senderPendingMessageMap.get(sender));
			if(pendingMessage != null) {
				pendingMessage.removeReceiver(receiver);
			}
		}
		
		public void deliver(DeviceID sender) {
			PendingMessage pendingMessage = (PendingMessage)senderPendingMessageMap.remove(sender);
			if(pendingMessage != null) {
				
				pendingMessage.deliver(sender);
			}
		}
		
		public void remove(DeviceID address) {
			senderPendingMessageMap.remove(address);
		}
		
		private abstract class PendingMessage {
			protected LinkLayerMessage message;
			
            private DeviceID sender;
			public PendingMessage(LinkLayerMessage message, DeviceID sender) {
				this.message = message;
				
				this.sender=sender;
			}
			
			public Shape getMessageShape() {
				Shape shape = message.getShape();
				if(shape == null) {
					return DEFAULT_MESSAGE_SHAPE;
				}
				return shape;
			}
			
			public abstract void removeReceiver(DeviceID receiver);
			public abstract void deliver(DeviceID sender);
			public abstract DeviceID[] getReceiverset();
		}
		
		private class PendingUnicastMessage extends PendingMessage {
			private DeviceID receiver;
			private DeviceID finalReceiver;
			
            private boolean failed;
            private UnicastCallbackHandler handle;
			
			public PendingUnicastMessage(LinkLayerMessage message, DeviceID sender, DeviceID receiver, DeviceID finalReceiver, UnicastCallbackHandler handle) {
				super(message, sender);
				this.receiver = receiver;	// the temporal receiver
				this.finalReceiver = finalReceiver;	// the final receiver which is also stored in the message
				this.handle=handle;
				failed = false;
			}
			
			public void removeReceiver(DeviceID receiver) {
				if(this.receiver != null) {
					if(this.receiver.equals(receiver)) {
						failed = true;
					}
				}
			}
			
			public void deliver(DeviceID sender) {
				// Sender is a BASE STATION
				if( baseStationSet.contains(sender) ) {
				    DeviceInfo receiverInfo=(DeviceInfo)deviceIDInfoMap.get(receiver);
				    DeviceInfo finalReceiverInfo = (DeviceInfo)deviceIDInfoMap.get(finalReceiver);
				    DeviceInfo senderInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
			    
				    LinkLayerInfoImplementation info = createLinklayerInfo(senderInfo, finalReceiverInfo, true);
				    failed=failed&!receiveDecider.receive(info,operatingSystem.getGlobalKnowledge());
 
				    if(failed) {
						if(handle != null) {
                            handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),message);
                            handle.notifyUnicastLost(receiverInfo.getLinkLayerAddress(),message);

                            operatingSystem.finishListener(senderInfo.getDeviceID(),handle);	//
						}
					}
					else {
						GlobalLinkLayerMessageReceiver.ReceiveSignal rs = new GlobalLinkLayerMessageReceiver.ReceiveSignal(
                                createLinklayerInfo(senderInfo,finalReceiverInfo,true),message);
						operatingSystem.sendSignal( receiver, receiverInfo.getServiceID(), rs );
						
						if(handle != null) {
                            handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),message);
                            handle.notifyUnicastReceived(receiverInfo.getLinkLayerAddress(),message);

                            operatingSystem.finishListener(senderInfo.getDeviceID(),handle);	//
						}
					}
				}
				// Sender is a CLIENT
				else if( clientSet.contains(sender) ) {	// sender is a client -> receiver is a basestation - get a stub to the current bs
					DeviceInfo receiverInfo=(DeviceInfo)deviceIDInfoMap.get(receiver);
					//DeviceID baseStationID = (DeviceID) connectedClients.get(sender);
					RuntimeOperatingSystem ros = (RuntimeOperatingSystem) baseStationSystems.get( receiver );
					bs = new InterfaceBS.BSStub( ros, ros.getServiceID() );

					linkLayerObserver.notifyUnicastProcessed(sender,receiverInfo.getLinkLayerAddress(),message);
                    linkLayerObserver.notifyUnicastLost(sender,receiverInfo.getLinkLayerAddress(),message);
					
					DeviceID nextBaseStation = (DeviceID)connectedClients.get(this.finalReceiver);

					// special case: the final receiver may be set to a base station, if a message shall be stored
					// this case is identified, if the returned nextbasestation equals zero; then the receiver is the
					// base station itself!
					if( nextBaseStation == null ) {
						if( baseStationSystems.containsKey( this.finalReceiver ) )
							nextBaseStation = this.finalReceiver;
					}
					
					if(nextBaseStation != null) {
						if(handle != null) {
                            handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),message);
                            handle.notifyUnicastReceived(receiverInfo.getLinkLayerAddress(),message);
						}
						operatingSystem.finishListener(sender,handle);	//
						bs.receiveMessageFromCN( message, nextBaseStation, this.finalReceiver );	// tell the basestation the message and the id of the next bs
					}
					else {
						// Since the receiver is not connected, the message cannot be delivered
						
						if(handle != null) {
                            handle.notifyUnicastProcessed(receiverInfo.getLinkLayerAddress(),message);
                            handle.notifyUnicastLost(receiverInfo.getLinkLayerAddress(),message);
						}
						operatingSystem.finishListener(sender,handle);	//
						throw new IllegalStateException("The recipient ("+this.finalReceiver+") of the message is currently not connected!");
					}
				}
			}
			
			public DeviceID[] getReceiverset() {
				return new DeviceID[]{receiver};
			}
		}
		
		private class PendingBroadcastMessage extends PendingMessage {
			private List receivers;
            private BroadcastCallbackHandler handle;
			public PendingBroadcastMessage(LinkLayerMessage message, DeviceID sender, DeviceID[] receivers, BroadcastCallbackHandler handle) {
				super(message,sender);
				this.receivers=new ArrayList(Arrays.asList(receivers));
                this.handle=handle;
			}
			public void removeReceiver(DeviceID receiver) {
				receivers.remove(receiver);
			}
			public void deliver(DeviceID sender) {
				// Sender is a BASE STATION
				if( baseStationSet.contains( sender ) ) {
					Iterator it = receivers.iterator();
					DeviceInfo senderInfo=(DeviceInfo)deviceIDInfoMap.get(sender);
					
					while(it.hasNext()) {
					    DeviceInfo deviceInfo=(DeviceInfo)deviceIDInfoMap.get(it.next());
					    operatingSystem.sendSignal(deviceInfo.getDeviceID(),deviceInfo.getServiceID(),
					            new GlobalLinkLayerMessageReceiver.ReceiveSignal(createLinklayerInfo(senderInfo,deviceInfo,false),message));
					}
					if(handle != null) {
                        handle.notifyBroadcastProcessed(message);
					}				
				}
				// Sender is a CLIENT
				if( clientSet.contains( sender )) {
					if( connectedClients.containsKey( sender ) ) {
						DeviceID baseStation = (DeviceID) connectedClients.get( sender );
						RuntimeOperatingSystem ros = (RuntimeOperatingSystem) baseStationSystems.get( baseStation );
						bs = new InterfaceBS.BSStub( ros, ros.getServiceID() );

						bs.sendCellBroadcast( message );
					}
					else
						System.out.println("Winfra: Client "+sender+" cannot send Broadcast, because it is not connected!");
				}
			}
			public DeviceID[] getReceiverset() {
				return (DeviceID[])receivers.toArray(new DeviceID[receivers.size()]);
			}
		}
	}

	private static class MessageQueue {
		private HashMap senderEntryListMap;
		public MessageQueue() {
			senderEntryListMap = new HashMap();
		}
		public boolean isEmpty(DeviceID sender) {
			LinkedList messageList = (LinkedList)senderEntryListMap.get(sender);
			if(messageList == null) {
				return true;
			}
			else {
				return messageList.isEmpty();
			}
		}
		public void add(DeviceID sender, MessageQueueEntry entry) {
			
			LinkedList messageList = (LinkedList)senderEntryListMap.get(sender);
			
			if(messageList == null) {
				messageList = new LinkedList();
				senderEntryListMap.put(sender, messageList);
			}
			//if (messageList.size()>1000) throw new IllegalStateException("Message Buffer greater 1000");
			messageList.addLast(entry);
		}
		public MessageQueueEntry remove(DeviceID sender) {
			LinkedList messageList = (LinkedList)senderEntryListMap.get(sender);
			if(messageList == null || messageList.isEmpty()) {
				throw new IllegalArgumentException("The sender has no pending messages.");
			}
			return (MessageQueueEntry)messageList.removeFirst();
		}
		public void clean(DeviceID address) {
			senderEntryListMap.remove(address);
		}
	}

	/**
	 * A Queue which stores Messages.
	 */
	private abstract class MessageQueueEntry {
		protected DeviceID sender;
		protected LinkLayerMessage message;
		
		public MessageQueueEntry(DeviceID sender, LinkLayerMessage message) {
			this.sender = sender;
			this.message = message;
		
		}
		public abstract void handle();
	}

	private class BroadcastMessageQueueEntry extends MessageQueueEntry {
		private BroadcastCallbackHandler handle;
        public BroadcastMessageQueueEntry(DeviceID sender, LinkLayerMessage message, BroadcastCallbackHandler handle) {
			super(sender, message);
            this.handle=handle;
            
		}
		public void handle() {
			processor.setBroadcastMessage(sender, message, graph.getReceiverSet(sender), handle);
			scheduler.add(message.getSize()/dataRate,1.0, sender);
		}
	}

	private class UnicastMessageQueueEntry extends MessageQueueEntry {
		private DeviceID finalReceiver;
        private UnicastCallbackHandler handle;
		// Note: sender can be either a basestation or a client. finalReceiver is always the receiving client
		public UnicastMessageQueueEntry(DeviceID sender, LinkLayerMessage message, DeviceID finalReceiver, UnicastCallbackHandler handle) {
			super(sender, message);
            this.handle=handle;
			this.finalReceiver = finalReceiver;
		}
		public void handle() {
			DeviceID tempReceiverAddress = null;
			// determine the address where to send the message: if the sender is a client, the target will be a base station
			// if the sender is a base station, the target is a client
			// note: communication between base stations does not happen in this network !
			if( clientSet.contains( sender )) {
				tempReceiverAddress = (DeviceID) connectedClients.get( sender );	// the basestation to which the client is currently connected
			}
			else if( baseStationSet.contains( sender )) {
				tempReceiverAddress = finalReceiver;	// no more hops between a sending basestation and a client
			}
			// even if the receiver is not reachable the processor needs to handle the message
			processor.setUnicastMessage(sender, message, tempReceiverAddress, finalReceiver, handle);
			//processor.setUnicastMessage(sender, message, finalReceiver, finalReceiver, handle);
			scheduler.add(message.getSize()/dataRate,1.0, sender);
		}
	}

	/**
	 * Register the ROS of a BaseStation
	 */
	public void registerBS( DeviceID deviceID, RuntimeOperatingSystem ros ) {
		baseStationSystems.put( deviceID, ros );
	}
	
	/**
	 * Registers a new Device
	 */
	public void registerDevice(Address linkLayerAddress) {
        DeviceID deviceID=operatingSystem.getCallingDeviceID();
        ServiceID sender=operatingSystem.getCallingServiceID();

        if( operatingSystem.hasService( deviceID, WinfraBaseStation.SERVICE_ID ) ) {
    		baseStationSet.add( deviceID );
    	}
    	else {
    		clientSet.add( deviceID );
    	}
		// The sender service id is the id of the winfraclientnetwork service
    	DeviceInfo deviceInfo = new DeviceInfo( deviceID, linkLayerAddress, sender );
		deviceIDInfoMap.put( deviceID, deviceInfo );
		addressInfoMap.put( linkLayerAddress, deviceInfo );
    }

    public void sendBroadcast(LinkLayerMessage message, boolean visualize) {
        sendBroadcast(message,visualize,null);
    }

    public void sendBroadcast(LinkLayerMessage message, boolean visualize, BroadcastCallbackHandler handle) {
		// broadcast messages are received by the sender!
		// => send an internal unicast message to the sender
        
		if(message.getSize()<=0) throw new IllegalStateException("Message size must be greater than 0!");
		
		
		DeviceID sender=operatingSystem.getCallingDeviceID();
		
		scheduler.addInternalBroadcast(sender, message);
		// and a broadcast message to all other receivers
		BroadcastMessageQueueEntry entry = new BroadcastMessageQueueEntry(sender, message, handle);		
		handleEntry(sender, entry);
    }

     public void sendUnicast(Address receiver, LinkLayerMessage message, boolean visualize) {
        sendUnicast(receiver,message,visualize,null);
    }

    /**
     * A Client or a BaseStation can call this method through a LinkLayerStub.
     * The senderAddress can be either of a client or of a basestation, depending on the station where the message is.
     * The receiverAddress will be always the same, that of the client who shall receive the message.
     */
    public void sendUnicast( Address receiverAddress, LinkLayerMessage message, boolean visualize, UnicastCallbackHandler handle ) {
    	if(message.getSize()<=0) throw new IllegalStateException("Message size must be greater than 0!");
    	

    	if (!addressInfoMap.containsKey(receiverAddress)){
		    throw new IllegalStateException("ReceiverAddress does not exist!");
		}
		DeviceID sender =  operatingSystem.getCallingDeviceID();
		DeviceID receiver = ((DeviceInfo)addressInfoMap.get(receiverAddress)).getDeviceID();

		if (sender.equals(receiver)) {
			scheduler.addInternalUnicast(sender, message,handle);
		} else {
			if( !connectedClients.containsKey( sender ) && !baseStationSet.contains( sender )) {	// sender is not available (weird case)
				operatingSystem.write("Winfra: Cannot send the Message, because the Sender ("+sender+") is not connected to a Base Station!");
                if( handle != null ) {
					handle.notifyUnicastProcessed(receiverAddress,message);
	                handle.notifyUnicastLost(receiverAddress,message);
                }
			}
			else if( !connectedClients.containsKey( receiver ) ) {	// receiver is not available

				if( this.messageKnowledgeServiceID != null ) {
					InquireMessageTypeSignal imts =
						(InquireMessageTypeSignal) this.operatingSystem.getSignalListenerStub(
								this.messageKnowledgeServiceID,
								InquireMessageTypeSignal.class);
					imts.inquireThisMessage(sender, receiver, message, handle);
				}
				else {
					operatingSystem.write("Winfra: Cannot send the Message, because the Receiver ("+receiver+") is not connected to a Base Station!");
	                if( handle != null ) {
						handle.notifyUnicastProcessed(receiverAddress,message);
		                handle.notifyUnicastLost(receiverAddress,message);
	                }
				}
			}
			else {	// everything is fine
				MessageQueueEntry entry = new UnicastMessageQueueEntry(sender, message, receiver, handle);
				handleEntry(sender, entry); // adds it to the queue or handles it directly
			}
		}
    }

    /**
     * If a MessageKnowledge Service is running, a
     * <code>TellingMessageResponseSignal</code> Signal calls this method.
     */
	public void inquiryResponse( DeviceID sender, Address receiverAddress, LinkLayerMessage llm, UnicastCallbackHandler handle, boolean store ) {
		if( store ) {	// store the message, but visualize the part from client to base station !
			// NOTE: it is only necessary, to store the receiver and the message, not the sender
			// if the receiver becomes available, the sender is set to the base station, which is
			// connected to the receiver!
			DeviceID receiver = ((DeviceInfo)addressInfoMap.get(receiverAddress)).getDeviceID();
			DeviceID currentConnectedBS = null;
			if( this.connectedClients.containsKey( sender ) ) {
				currentConnectedBS = (DeviceID)this.connectedClients.get(sender);
				MessageQueueEntry entry = new UnicastMessageQueueEntry(sender, llm, currentConnectedBS, null);
				handleEntry(sender, entry); // adds it to the queue or handles it directly
				this.storedMessages.put( receiver, llm );
			}
			else {
				System.out.println("winfra: inquiryResponse(): This should never happen...");
			}
		} else {		// do not store the message
            if( handle != null ) {
				handle.notifyUnicastProcessed( receiverAddress,llm );
                handle.notifyUnicastLost( receiverAddress,llm );
            }
		}
//		System.out.println("ok, the message:"+llm.toString()+" shall be stored: "+store);
	}
	
	/**
	 * Send a previously stored Unicast to a Device, which appeared finally somewhere in
	 * the range of an arbitrary base station
	 * @param receiver The DeviceID of the receiver
	 * @param llm The LinkLayer Message
	 */
	private void sendAStoredUnicastMessage( DeviceID receiver, LinkLayerMessage llm ) {
		DeviceID currentConnectedBS = null;
		if( this.connectedClients.containsKey( receiver )) {
			currentConnectedBS = (DeviceID)this.connectedClients.get(receiver);
			MessageQueueEntry entry = new UnicastMessageQueueEntry(currentConnectedBS, llm, receiver, null);
			handleEntry(currentConnectedBS, entry); // adds it to the queue or handles it directly
		}
		else {
			System.out.println("winfra:sendAStoredUnicastMessage() failed (Method should not have been called)!");
		}
	}
    
    public ServiceID getServiceID() {
        return serviceID;
    }
    public void finish() {
    	// nop
    }
    
    public void visualize(boolean messages, boolean sendingRadii, boolean links) {
        visualizeCommunicationLinks=links;
        visualizeMessages=messages;
        visualizeSendingRadius=sendingRadii;
        
    }

    /**
     * Returns a Collection of Shape which visualizes the Communication and the Devices.
     */
    public Shape getShape() {
        if (!visualizeCommunicationLinks && !visualizeMessages && !visualizeSendingRadius) return null;
        Iterator iterator = addressSet.iterator();
        ShapeCollection shape = new ShapeCollection();
        GlobalKnowledge globalKnowledge = operatingSystem.getGlobalKnowledge();
        
        while (iterator.hasNext()){
            SimulationDeviceID deviceID = (SimulationDeviceID)iterator.next();
            if (visualizeSendingRadius){
                double radius = globalKnowledge.getSendingRadius(deviceID);
                Color color = Color.BLUE;
                if(clientSet.contains(deviceID)) color = Color.BLUE;
                if(baseStationSet.contains(deviceID)) color = Color.GREEN;
                shape.addShape(new EllipseShape(deviceID,new Extent(radius*2,radius*2),color,false),Position.NULL_POSITION);
            }
            if (visualizeCommunicationLinks){
            	DeviceID[] devIds = graph.getReceiverSet( deviceID );
            	for( int i = 0; i< devIds.length; i++ ){
            		DeviceID neighborID = devIds[i];
            		shape.addShape( new ArrowShape( neighborID, deviceID, Color.LIGHTGREY, 3), Position.NULL_POSITION );
            	}
            }
            if (processor.isBusy(deviceID) && visualizeMessages){
                Position ownerPosition = globalKnowledge.getTrajectory(deviceID).getPosition();
                DeviceID[] deviceIDs = processor.getReceiverSet(deviceID);
                
                for (int i = 0; i < deviceIDs.length; i++){
                	Position receiverPosition = globalKnowledge.getTrajectory((SimulationDeviceID)(deviceIDs[i])).getPosition();
          	        Position sr = receiverPosition.sub(ownerPosition);
        	        sr = sr.scale(scheduler.getProgress(deviceID));
        	        sr = ownerPosition.add(sr);
        	        Shape messageShape = processor.getMessageShape(deviceID);

        	        // the message might have no shape
        	        if(messageShape != null) {
            	        shape.addShape(messageShape, sr);
        	        }
                }
            }
        }
        return shape;
    }	
  

    
    //TODO: linklayer Properties can be changed! (@see wlan.MacLayer)
    public LinkLayerProperties getLinkLayerProperties() {
        DeviceInfo info=(DeviceInfo) deviceIDInfoMap.get(operatingSystem.getCallingDeviceID());
        return new LinkLayerProperties(info.getLinkLayerAddress(),false,1,java.lang.Double.MAX_VALUE);
    }
    
    public void setLinkLayerProperties(LinkLayerProperties props) {
        throw new IllegalAccessError("this linkLayer does not provide changing linklayer properties");
    }
    
    public void setPromiscuous(boolean promiscuous) {
        throw new IllegalAccessError("this linkLayer does not provide promiscuous mode");
    }
    
    public Address getNetworkAddress() {
        return ((DeviceInfo) deviceIDInfoMap.get(operatingSystem.getCallingDeviceID())).getLinkLayerAddress();
    }

	public void getParameters(Parameters parameters) {
		// nop
	}
    
    /**
     * 
     * TODO: comment method 
     * @param sender
     * @param receiver
     * @param unicast
     * @return
     */
    private LinkLayerInfoImplementation createLinklayerInfo(DeviceInfo sender, DeviceInfo receiver, boolean unicast) {
        double signalStrength=java.lang.Double.MAX_VALUE;
        //TODO: signalStrength does not exist :)
        return new LinkLayerInfoImplementation(sender.getLinkLayerAddress(),receiver.getLinkLayerAddress(),unicast, signalStrength);
    }
}

