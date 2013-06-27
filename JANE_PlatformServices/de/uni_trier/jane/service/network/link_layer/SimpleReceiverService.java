/*****************************************************************************
 * 
 * SimpleReceiverService.java
 * 
 * $Id: SimpleReceiverService.java,v 1.2 2007/07/25 12:24:14 uwehling Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleReceiverService implements Runnable, PacketReceiverService {

    private DatagramSocket udp_socket;
    //private EventHandler eventHandler;
    private ServiceID linkLayerService;
    private boolean finished;
    private int sendPort;
    private int maxPacketSize;
    private Thread thread;
    private RuntimeOperatingSystem runtimeOperatingSystem;

    
    
    public void start(NetworkConfiguration networkConfiguration, RuntimeOperatingSystem runtimeOperatingSystem, ServiceID linkLayerService) {
    
        this.sendPort=networkConfiguration.getSendPort();		
		this.maxPacketSize=networkConfiguration.getMaxPacketSize();
		try {
            udp_socket= new DatagramSocket(networkConfiguration.getReceivePort());
            udp_socket.setBroadcast(true);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.runtimeOperatingSystem=runtimeOperatingSystem;
        this.linkLayerService=linkLayerService;
        thread=new Thread(this);
        thread.start();
    }
    
    

    

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
    	while(!finished){
			
			try {
				
				DatagramPacket packet=new DatagramPacket(new byte[maxPacketSize],maxPacketSize,InetAddress.getByName("localhost"),sendPort);
				udp_socket.receive(packet);
//				System.err.println(packet.getAddress() + ", "+ packet.getLength());

				runtimeOperatingSystem.sendSignal(linkLayerService,new PlatformNetwork.ReceiveSignal(packet.getData(),packet.getAddress(),true));
				
				//System.out.println("received st");

						
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
			    if (!finished){
			        e.printStackTrace();
			    }
			}
		}
        
    }
    



    public void stop(){
        finished=true;
        udp_socket.close();
        thread.interrupt();
    }

  
}
