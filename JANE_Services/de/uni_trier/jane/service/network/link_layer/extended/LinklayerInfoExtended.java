/*****************************************************************************
 * 
 * LinklayerInfoExtended.java
 * 
 * $Id: LinklayerInfoExtended.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 *  
 * Copyright (C) 2002-2006 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.extended;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.*;

public class LinklayerInfoExtended implements LinkLayerInfo {

    private boolean isBroadcast;
    private boolean isMulticast; 
    private Address[] receivers;
    
    
    
    
    private Address sender;
    private Address receiver;
    private double signalStrength;
    private boolean reliable;
    
    
    /**
     * 
     * Constructor for class <code>LinklayerInfoExtended</code>
     * @param isBroadcast
     * @param isMulticast
     * @param receivers
     * @param sender
     * @param receiver
     * @param signalStrength
     */
    public LinklayerInfoExtended(boolean isBroadcast, boolean isMulticast, Address[] receivers, Address sender, Address receiver, double signalStrength) {
        super();
        this.signalStrength=signalStrength;
        this.isBroadcast = isBroadcast;
        this.isMulticast = isMulticast;
        this.receivers = receivers;
        this.sender = sender;
        this.receiver = receiver;
        reliable=true;
    }

    /**
     * Constructor for class <code>LinklayerInfoExtended</code>
     * @param info
     */
    public LinklayerInfoExtended(LinkLayerInfo info) {
        signalStrength=info.getSignalStrength();
        sender=info.getSender();
        receiver=info.getReceiver();
        if (info.isUnicastMessage()){
            receivers=new Address[]{receiver};
        }
        isBroadcast=info.isBroadcastMessage();
        reliable=info.isReliable();
        
        
        
    }

    public Address getSender() {
        return sender;
    }

    public boolean isAddressedMulticastMessage(){
        return !isBroadcastMessage()&&!isUnicastMessage();
    }
    
    public boolean isAddressedBroadcastMessage(){
        return isBroadcast&&receivers!=null;
    }
    
    public boolean isReliable() {
        return reliable;
    }
    
    public boolean isUnicastMessage() {
        return receivers.length==1;
    }

    public boolean isBroadcastMessage() {
        return isBroadcast;
    }
    
    public boolean isMulticastMessage(){
        return isMulticast;
    }


    public Address getReceiver() {
        return receiver;
    }
    
    /**
     * @return Returns the receivers.
     */
    public Address[] getReceivers() {
        return this.receivers;
    }
    
    public double getSignalStrength() {
        return signalStrength;
    }
    
    public LinkLayerInfo copy() {
        return this;
    }



}
