/*****************************************************************************
 * 
 * ${Id}$
 *  
 ***********************************************************************
 *  
 * JANE - The Java Ad-hoc Network simulation and evaluation Environment
 *
 ***********************************************************************
 *
 * Copyright (C) 2002-2006
 * Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * Systemsoftware and Distributed Systems
 * University of Trier 
 * Germany
 * http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.service.routing.gcr; 

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.service.routing.face.NetworkNodeImpl;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class ClusterNetworkNodeImpl extends NetworkNodeImpl implements
        NetworkNode {

    private Address receiver;
    private Position receiverPosition;

    /**
     * Constructor for class <code>ClusterNetworkNodeImpl</code>
     * @param address
     * @param position
     * @param receiver
     * @param receiverPosition
     */
    public ClusterNetworkNodeImpl(LinkLayerAddress address, Position position, Address receiver, Position receiverPosition) {
        super(address,position);
        this.receiver=receiver;
        this.receiverPosition=receiverPosition;
    }

    /**
     * @return Returns the receiver.
     */
    public Address getReceiver() {
        return this.receiver;
    }
    
    /**
     * @return Returns the receiverPosition.
     */
    public Position getReceiverPosition() {
        return this.receiverPosition;
    }
    

}
