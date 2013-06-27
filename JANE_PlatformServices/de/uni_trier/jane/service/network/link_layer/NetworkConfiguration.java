/*****************************************************************************
 * 
 * NetworkConfiguration.java
 * 
 * $Id: NetworkConfiguration.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer;

import java.net.InetAddress;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class NetworkConfiguration {
    private int receivePort;
    private int sendPort;
    private InetAddress address;
    private int maxPacketSize;
    private double pendingPacketsDelta;
    
    

    /**
     * Constructor for class <code>NetworkConfiguration</code>
     *
     * @param receivePort
     * @param sendPort
     * @param address
     */
    public NetworkConfiguration(int receivePort, int sendPort,
            InetAddress address) {
        this(receivePort,sendPort,address,40000,60);
        
    }
    
    /**
     * 
     * Constructor for class <code>NetworkConfiguration</code>
     * @param receivePort
     * @param sendPort
     * @param address
     * @param maxPacketSize
     */
    public NetworkConfiguration(int receivePort, int sendPort,
            InetAddress address, int maxPacketSize) {
        this(receivePort,sendPort,address,maxPacketSize,60);
        
    }
    
    
    /**
     * Constructor for class <code>NetworkConfiguration</code>
     * @param receivePort
     * @param sendPort
     * @param address
     * @param maxPacketSize
     * @param pendingPacketsDelta
     */
    public NetworkConfiguration(int receivePort, int sendPort, InetAddress address, int maxPacketSize, double pendingPacketsDelta) {
        this.receivePort = receivePort;
        this.sendPort = sendPort;
        this.address = address;
        this.maxPacketSize = maxPacketSize;
        this.pendingPacketsDelta = pendingPacketsDelta;
    }
    
    /**
     * @return Returns the maxPacketSize.
     */
    public int getMaxPacketSize() {
        return this.maxPacketSize;
    }
    
    
    /**
     * @return Returns the pendingPacketsDelta.
     */
    public double getPendingPacketsDelta() {
        return this.pendingPacketsDelta;
    }


    /**
     * @return Returns the address.
     */
    public InetAddress getAddress() {
        return address;
    }
    /**
     * @return Returns the receivePort.
     */
    public int getReceivePort() {
        return receivePort;
    }
    /**
     * @return Returns the sendPort.
     */
    public int getSendPort() {
        return sendPort;
    }
}
