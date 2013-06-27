package de.uni_trier.jane.service.network.link_layer.packetNetwork; 

import java.io.*;
import java.net.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.platform.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.extended.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PacketPlatformNetwork implements PlatformNetwork,
        LinkLayerExtended, RuntimeService {

    
//	private static final int DEFAULT_RECEIVE_PORT = 9001;
//	private static final int DEFAULT_SEND_PORT = 9002;
	//private final static int MAX_PACKET_SIZE=40000;
	//private static final double PENDING_PACKETS_DELTA = 60;
	

    private PlatformLinkLayerAddress address;
    private RuntimeOperatingSystem operatingSystem;
//    private int receivePort;
//    private int sendPort;
    private DatagramSocket udp_socket;
    private ServiceID serviceID;
    private PacketReceiverService receiverService;
    private DataSerializer dataSerializer;
    private byte sequenceNumber;
    
    private HashMap addressPacketMap;
	private TreeSet packetTimeCache;
    private ReceiveDecider receiveDeceider;
    private LinkLayerExtended_Plugin extendedPlugin;
	private NetworkConfiguration networkConfiguration;
    
    
    
    public static ServiceID createInstance(ServiceUnit serviceUnit) throws NetworkException{
        return createInstance(serviceUnit, new DefaultDataSerializer());
        
        
    }
    public static ServiceID createInstance(ServiceUnit serviceUnit, DataSerializer serializer) throws NetworkException {
        return createInstance(serviceUnit, new NetconfigGUI().open(),serializer,new SimpleReceiveDecider());
        
    }
    public static ServiceID createInstance(ServiceUnit serviceUnit, ReceiveDecider receiveDecider) throws NetworkException{
        return createInstance(serviceUnit, new NetconfigGUI().open(),receiveDecider);
    }
    
    
    public static ServiceID createInstance(ServiceUnit serviceUnit, NetworkConfiguration networkConfiguration) throws NetworkException{
        return createInstance(serviceUnit,networkConfiguration,new DefaultDataSerializer(),new SimpleReceiveDecider());
        
        
    }
    
    public static ServiceID createInstance(ServiceUnit serviceUnit, NetworkConfiguration networkConfiguration,DataSerializer dataSerializer,ReceiveDecider receiveDecider) throws NetworkException{
        return serviceUnit.addService(new PacketPlatformNetwork(networkConfiguration,dataSerializer,receiveDecider));  
    }
    
    public static ServiceID createInstance(ServiceUnit serviceUnit, NetworkConfiguration networkConfiguration, ReceiveDecider receiveDecider) throws NetworkException{
        return serviceUnit.addService(new PacketPlatformNetwork(networkConfiguration,new DefaultDataSerializer(),receiveDecider));  
    }
	
	 
    /**
     * Constructor for class <code>SimplePlatformNetwork</code>
     * @throws NetworkException
     * @throws SocketException
     * @throws UnknownHostException
     * 
     */
    public PacketPlatformNetwork() throws NetworkException {
        
        this(new NetconfigGUI().open());
    }
    
    /**
     * 
     * Constructor for class <code>SimplePlatformNetwork</code>
     * @param receivePort
     * @param sendPort
     * @throws NetworkException
     */
    public PacketPlatformNetwork(int receivePort, int sendPort) throws NetworkException {
        this(new NetworkConfiguration(receivePort,sendPort,Network.getFirstInetAddress()));
    }
    
  
   

    /**
     * 
     * Constructor for class <code>PacketPlatformNetwork</code>
     * @param networkConfiguration
     * @param dataSerializer
     * @param packetReceiverService
     * @throws NetworkException
     */
    public PacketPlatformNetwork(NetworkConfiguration networkConfiguration,DataSerializer dataSerializer,ReceiveDecider receiveDeceider) throws NetworkException 
    {
        this.networkConfiguration = networkConfiguration;
        // this.receivePort=networkConfiguration.getReceivePort();
        // this.sendPort=networkConfiguration.getSendPort();
        this.dataSerializer = dataSerializer;
        this.receiverService = new SimpleReceiverService();
        this.receiveDeceider = receiveDeceider;

        //if(!address.isLinkLocalAddress()||!address.isAnyLocalAddress()) throw new NetworkException("InetAddress is not a local address");
     
		this.address = new PlatformLinkLayerAddress( networkConfiguration.getAddress() );   
		
		serviceID = new NetworkServiceID( networkConfiguration.getReceivePort(), networkConfiguration.getSendPort(),getClass() );
		
		try 
		{
			//System.out.println( "System os.name: " + System.getProperty("os.name") );
			
			// Adrian 08.05.2008: to get it working on N810
			if( System.getProperty( "os.name" ).startsWith( "Windows" ) ||
					System.getProperty( "os.name" ).startsWith( "Linux" ) )
			{
				udp_socket= new DatagramSocket( networkConfiguration.getSendPort(), networkConfiguration.getAddress() );
			}
			else
			{
				udp_socket= new DatagramSocket( networkConfiguration.getSendPort(), InetAddress.getByName( "0.0.0.0" ) );
			}
			
			// System.err.println( networkConfiguration.getAddress() );
		    
			udp_socket.setBroadcast( true );
		} 
		catch( SocketException e ) 
		{
			e.printStackTrace();
			throw new NetworkException( e.getMessage() );
		} 
		catch( UnknownHostException e ) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		addressPacketMap = new HashMap();
		packetTimeCache = new TreeSet(); 
    }
    
    
    
    /**
     * Constructor for class <code>PacketPlatformNetwork</code>
     *
     * @param configuration
     * @throws NetworkException
     */
    public PacketPlatformNetwork(NetworkConfiguration configuration) throws NetworkException {
        this(configuration,new DefaultDataSerializer(), new SimpleReceiveDecider());

    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.platform.network.PlatformNetwork#receiveMessage(byte[], java.net.InetAddress, boolean)
     */
    public void receiveMessage(byte[] data, InetAddress sender, boolean isUnicast) {
    	
		ReceivePacket packet;

		if (addressPacketMap.containsKey(sender)){
			packet=(ReceivePacket)addressPacketMap.get(sender);
			if (!packet.addPacket(data,data.length,operatingSystem.getTime())){
				packet=new ReceivePacket(data,data.length,operatingSystem.getTime(),networkConfiguration.getMaxPacketSize());
			}
		}else{
			packet=new ReceivePacket(data,data.length,operatingSystem.getTime(),networkConfiguration.getMaxPacketSize());
		}
		if (packet.isComplete()){
			addressPacketMap.remove(sender);
			try {
                byte[] cdata=packet.getCompleteData();
		         LinkLayerMessage linkLayerMessage = dataSerializer.getMessage(cdata);
		         LinkLayerAddress senderAddress=new PlatformLinkLayerAddress(sender);

                 if (receiveDeceider.receive(sender,cdata)){

                     operatingSystem.sendSignal(
		                 new MessageReceiveSignal(
		                         new LinkLayerInfoImplementation(senderAddress,address,isUnicast,Double.NEGATIVE_INFINITY),
		                         linkLayerMessage));
                 }
			}catch (DataSerializerException e){
			    e.printStackTrace();
			    
			}
		}else{
			addressPacketMap.put(sender,packet);
			packetTimeCache.add(new PacketTimeEntry(operatingSystem.getTime(),sender));
			
		}
		cleanAddressPacketMap();
	}
	
	private void cleanAddressPacketMap() {
		Iterator iterator=packetTimeCache.iterator();
		while (iterator.hasNext()){
			PacketTimeEntry entry=(PacketTimeEntry)iterator.next();
			if (entry.getTime()<operatingSystem.getTime()-networkConfiguration.getPendingPacketsDelta()){
				addressPacketMap.remove(entry.getSender());
				iterator.remove();
			}else{
				break;
			}
		}
		
	}

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#getLinkLayerAddress()
     */
    public Address getNetworkAddress() {
        
        return address;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#sendBroadcast(de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
     */
    public void sendBroadcast(LinkLayerMessage message) {
        sendBroadcast(message,null);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#sendBroadcast(de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage, de.uni_trier.ssds.service.TaskHandle)
     */
    public void sendBroadcast(LinkLayerMessage message,BroadcastCallbackHandler handler) {
		try {
			
	
			SendPacket currentPacket=new SendPacket(dataSerializer.getData(message),sequenceNumber++,networkConfiguration.getMaxPacketSize());
			
			while (currentPacket.hasData()){
				
				byte[] data=currentPacket.getNextData();
				DatagramPacket packet =new DatagramPacket(data,data.length,InetAddress.getByName("255.255.255.255"),networkConfiguration.getReceivePort());
				
			//udp_socket.
//				System.err.println("send: " + packet.getAddress() + ", " + packet.getLength());
//				System.err.println("broadcast: " + udp_socket.getBroadcast() + " local: " + udp_socket.getLocalAddress() + ", " + udp_socket.getLocalPort() + " isBound: " + udp_socket.isBound() + " remote: " + udp_socket.getInetAddress() + ", " + udp_socket.getPort());
				udp_socket.send(packet);
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		} catch (DataSerializerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
		    operatingSystem.sendSignal(new LinkLayerObserver.BroadcastProcessedSignal(message));
			
			if (handler!=null){
                handler.notifyBroadcastProcessed(message);
				//operatingSystem.sendCallback(handle,new BroadcastCallbackHandler.BroadcastProcessedCallback(message));
				operatingSystem.finishListener(handler);
			}
			
		}
			
			
			
		
			
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#sendUnicast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
     */
    public void sendUnicast(Address receiver, LinkLayerMessage message) {
        sendUnicast(receiver,message,null);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#sendUnicast(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage, de.uni_trier.ssds.service.TaskHandle)
     */
    public void sendUnicast(Address receiver, LinkLayerMessage message, UnicastCallbackHandler handler) {
		
		try{
		    SendPacket currentPacket=new SendPacket(dataSerializer.getData(message),sequenceNumber++,networkConfiguration.getMaxPacketSize());
			while (currentPacket.hasData()){
				
				byte[] data=currentPacket.getNextData();
				DatagramPacket packet =new DatagramPacket(data,data.length,((PlatformLinkLayerAddress)receiver).getInetAddress(),networkConfiguration.getReceivePort());
			//udp_socket.
				udp_socket.send(packet);
			}
		
		}catch (IOException e) {
			e.printStackTrace();
			operatingSystem.sendSignal(new LinkLayerObserver.UnicastLostSignal(receiver,message));
			if (handler!=null){
                handler.notifyUnicastLost(receiver,message);
				//operatingSystem.sendCallback(handle,new UnicastCallbackHandler.UnicastLostCallback(receiver,message));
			
			}
		} catch (DataSerializerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
		    operatingSystem.sendSignal(new LinkLayerObserver.UnicastProcessedSignal(receiver,message));
			
			if (handler!=null){
                handler.notifyUnicastProcessed(receiver,message);
				//operatingSystem.sendCallback(handle,new UnicastCallbackHandler.UnicastProcessedCallback(receiver,message));
				operatingSystem.finishListener(handler);
			}
			
		}
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        operatingSystem.registerAccessListener(LinkLayer_sync.class);
        operatingSystem.registerSignalListener(LinkLayer_async.class);
        receiverService.start(networkConfiguration,runtimeOperatingSystem,serviceID);
       receiveDeceider.init(runtimeOperatingSystem);
       extendedPlugin=new LinkLayerExtended_Plugin(this,runtimeOperatingSystem,new LinkLayerConfiguration(1,3));
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getServiceID()
     */
    public ServiceID getServiceID() {
        return serviceID;
    }

    public void finish() {
        receiverService.stop();
        udp_socket.close();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getShape()
     */
    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

	private class PacketTimeEntry implements Comparable{
		private double time;
		private InetAddress sender;
		

		/**
		 * @param time
		 * @param sender
		 */
		public PacketTimeEntry(double time, InetAddress sender) {
			super();
			this.time = time;
			this.sender = sender;
		}
		/**
		 * @return Returns the sender.
		 */
		public InetAddress getSender() {
			return sender;
		}
		/**
		 * @return Returns the time.
		 */
		public double getTime() {
			return time;
		}
		
		
		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			PacketTimeEntry entry=(PacketTimeEntry)o;
			if (time<entry.time) return -1;
			if (time>entry.time) return 1;
			return sender.getHostAddress().compareTo(entry.sender.getHostAddress());
		}
		
		public int hashCode() {
			final int PRIME = 1000003;
			int result = 0;
			long temp = Double.doubleToLongBits(time);
			result = PRIME * result + (int) (temp >>> 32);
			result = PRIME * result + (int) (temp & 0xFFFFFFFF);
			if (sender != null) {
				result = PRIME * result + sender.hashCode();
			}

			return result;
		}

		public boolean equals(Object oth) {
			if (this == oth) {
				return true;
			}

			if (oth == null) {
				return false;
			}

			if (oth.getClass() != getClass()) {
				return false;
			}

			PacketTimeEntry other = (PacketTimeEntry) oth;

			if (this.time != other.time) {
				return false;
			}
			if (this.sender == null) {
				if (other.sender != null) {
					return false;
				}
			} else {
				if (!this.sender.equals(other.sender)) {
					return false;
				}
			}

			return true;
		}
	}

    
    

	   /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#getLinkLayerProperties()
     */
    public LinkLayerProperties getLinkLayerProperties() {
        
        return new LinkLayerProperties(address,false,-1,-1);
    }
    
    public void setLinkLayerProperties(LinkLayerProperties props) {
        throw new IllegalAccessError("this linkLayer does not provide property changes");
        
    }
    
    public void setPromiscuous(boolean promiscuous) {
        throw new IllegalAccessError("this linkLayer does not provide promiscuous mode");
        
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}
    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        this.extendedPlugin.sendAddressedBroadcast(receiver, message, configuration, callbackHandler);
    }
    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message) {
        this.extendedPlugin.sendAddressedBroadcast(receiver, message);
    }
    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        this.extendedPlugin.sendAddressedBroadcast(receivers, message, configuration, callbackHandler);
    }
    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message) {
        this.extendedPlugin.sendAddressedBroadcast(receivers, message);
    }
    public void sendAddressedMulticast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        this.extendedPlugin.sendAddressedMulticast(receivers, message, configuration, callbackHandler);
    }
    public void sendBroadcast(LinkLayerMessage message, LinkLayerConfiguration configuration, BroadcastCallbackHandler callbackHandler) {
        System.err.println("Ignoring Linklayer configuration");
        sendBroadcast(message, callbackHandler);
    }
    public void sendUnicast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        System.err.println("Ignoring Linklayer configuration");
        sendUnicast(receiver, message, callbackHandler);
    }

}
