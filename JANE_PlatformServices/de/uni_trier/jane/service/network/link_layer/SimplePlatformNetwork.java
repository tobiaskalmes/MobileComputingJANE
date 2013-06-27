/*****************************************************************************
 * 
 * PlatformNetwork.java
 * 
 * $Id: SimplePlatformNetwork.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer; 

import java.io.*;
import java.net.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.platform.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.shapes.*;



/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimplePlatformNetwork extends Network implements RuntimeService,LinkLayer,PlatformNetwork {
    
    
   
    
  
	private static final int DEFAULT_RECEIVE_PORT = 9001;
	private static final int DEFAULT_SEND_PORT = 9002;
	private final static int MAX_PACKET_SIZE=64000;

    
    private PlatformLinkLayerAddress address;
    private RuntimeOperatingSystem operatingSystem;
    private int receivePort;
    private int sendPort;
    private DatagramSocket udp_socket;
    private ServiceID serviceID;
    private PacketReceiverService receiverService;
    private DataSerializer dataSerializer;
    
    
    
    
    /**
     * Constructor for class <code>SimplePlatformNetwork</code>
     * @throws NetworkException
     * @throws SocketException
     * @throws UnknownHostException
     * 
     */
    public SimplePlatformNetwork() throws NetworkException {
        this(DEFAULT_RECEIVE_PORT,DEFAULT_SEND_PORT);
    }
    
    /**
     * 
     * Constructor for class <code>SimplePlatformNetwork</code>
     * @param receivePort
     * @param sendPort
     * @throws NetworkException
     */
    public SimplePlatformNetwork(int receivePort, int sendPort) throws NetworkException {
        this(receivePort,sendPort,getFirstInetAddress());
    }
    
  
   
    /**
     * 
     * Constructor for class <code>SimplePlatformNetwork</code>
     *
     * @param receivePort
     * @param sendPort
     * @param netAddress
     * @throws NetworkException
     */
    public SimplePlatformNetwork(int receivePort, int sendPort, InetAddress netAddress) throws NetworkException {
        this(receivePort,sendPort,netAddress,new DefaultDataSerializer(), new SimpleReceiverService());
    }
    
    /**
     * 
     * Constructor for class <code>SimplePlatformNetwork</code>
     *
     * @param receivePort
     * @param sendPort
     * @param netAddress
     * @param dataSerializer
     * @param receiverService
     * @throws NetworkException
     */
    public SimplePlatformNetwork(int receivePort, int sendPort,InetAddress netAddress,DataSerializer dataSerializer, PacketReceiverService receiverService) throws NetworkException {
        
        this.receivePort=receivePort;
        this.sendPort=sendPort;
        this.dataSerializer=dataSerializer;
        this.receiverService=receiverService;
        
     //if(!address.isLinkLocalAddress()||!address.isAnyLocalAddress()) throw new NetworkException("InetAddress is not a local address");
     
		this.address=new PlatformLinkLayerAddress(netAddress);   
		
		serviceID=new NetworkServiceID(receivePort,sendPort,getClass());
		try {
		    udp_socket= new DatagramSocket(sendPort,netAddress);
		} catch (SocketException e) {
         throw new NetworkException(e.getMessage());
		}
    }
    
	/* (non-Javadoc)
     * @see de.uni_trier.ssds.service.RuntimeService#start(de.uni_trier.ssds.service.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        receiverService.start(new NetworkConfiguration(receivePort,sendPort,address.getInetAddress(),MAX_PACKET_SIZE),runtimeOperatingSystem,serviceID);
       
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
    public void sendBroadcast(LinkLayerMessage message, BroadcastCallbackHandler handler) {
     	try {
			
    		byte[] data=dataSerializer.getData(message);
			DatagramPacket packet =new DatagramPacket(data,data.length,InetAddress.getByName("255.255.255.255"),receivePort);
			
			//TODO: receiver.receiveLocalcast(message,sender);
			//receiveMessage(message,address.getInetAddress(),false);
			udp_socket.send(packet);
			

			
		}catch (IOException e) {
			e.printStackTrace();
		} catch (DataSerializerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
		    operatingSystem.sendSignal(new LinkLayerObserver.BroadcastProcessedSignal(message));
			
			if (handler!=null){
                handler.notifyBroadcastProcessed(message);
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
    public void sendUnicast(Address receiver,
            LinkLayerMessage message, UnicastCallbackHandler handle) {
    	try {
			
			byte[] data=dataSerializer.getData(message);
			
			DatagramPacket packet =new DatagramPacket(data,data.length,((PlatformLinkLayerAddress)receiver).getInetAddress(),receivePort);
			
			udp_socket.send(packet);
			operatingSystem.sendSignal(new LinkLayerObserver.UnicastReceivedSignal(receiver,message));
			if (handle!=null){
                handle.notifyUnicastReceived(receiver,message);
				//operatingSystem.sendCallback(handle,new UnicastCallbackHandler.UnicastReceivedCallback(receiver,message));
			
			}

			
		}catch (IOException e) {
			e.printStackTrace();
			operatingSystem.sendSignal(new LinkLayerObserver.UnicastLostSignal(receiver,message));
			if (handle!=null){
                handle.notifyUnicastLost(receiver,message);
				//operatingSystem.sendCallback(handle,new UnicastCallbackHandler.UnicastLostCallback(receiver,message));
			
			}
		} catch (DataSerializerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
		    operatingSystem.sendSignal(new LinkLayerObserver.UnicastProcessedSignal(receiver,message));
			
			if (handle!=null){
                handle.notifyUnicastProcessed(receiver,message);
				operatingSystem.finishListener(handle);
			}
			
		}
    }
    
    public void finish() {
        receiverService.stop();
        udp_socket.close();

    }
    public ServiceID getServiceID() {

        return serviceID;
    }
    public Shape getShape() {
        
        return null;
    }


    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.platform.network.PlatformNetwork#receiveMessage(java.lang.Object, de.uni_trier.ssds.service.ServiceID)
     */
    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.platform.network.PlatformNetwork#receiveMessage(java.lang.Object, java.net.InetAddress)
     */
    public void receiveMessage(byte[] data, InetAddress sender,boolean unicast) {
     
        try {
            LinkLayerMessage linkLayerMessage = dataSerializer.getMessage(data);
            Address senderAddress=new PlatformLinkLayerAddress(sender);
            operatingSystem.sendSignal(
                    new MessageReceiveSignal(
                            new LinkLayerInfoImplementation(senderAddress,address,unicast,Double.NEGATIVE_INFINITY),
                            linkLayerMessage));
        } catch (DataSerializerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

}
