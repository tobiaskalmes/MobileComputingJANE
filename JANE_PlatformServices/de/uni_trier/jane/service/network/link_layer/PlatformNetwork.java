/*****************************************************************************
 * 
 * PlatformNetwork.java
 * 
 * $Id: PlatformNetwork.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
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

import java.net.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;


/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface PlatformNetwork {

    
   
    
    
    

    
    /**
     * @param message
     * @param sender,boolean isUnicast
     */
    abstract void receiveMessage(byte[] data, InetAddress sender,boolean isUnicast);
	

    /**
     * @author goergen
     *
     * To change the template for this generated type comment go to
     * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
     */
    public class ReceiveSignal implements Signal{

        private byte[] data;
        private InetAddress sender;
        private boolean isUnicast;

        /**
         * Constructor for class <code>ReceiveSignal</code>
         * @param object
         * @param address
         */
        public ReceiveSignal(byte[] data, InetAddress sender, boolean isUnicast) {
            this.data=data;
            this.sender=sender;
            this.isUnicast=isUnicast;
            
            
        }

        /* (non-Javadoc)
         * @see de.uni_trier.ssds.service.ServiceSignal#handle(de.uni_trier.ssds.service.ServiceID, de.uni_trier.ssds.service.Service)
         */
        public void handle(SignalListener service) {
           ((PlatformNetwork)service).receiveMessage(data,sender,isUnicast);
            
        }

        /* (non-Javadoc)
         * @see de.uni_trier.ssds.service.Dispatchable#copy()
         */
        public Dispatchable copy() {
            return this;
        }

        /* (non-Javadoc)
         * @see de.uni_trier.ssds.service.Dispatchable#getReceiverServiceClass()
         */
        public Class getReceiverServiceClass() {
           
            return PlatformNetwork.class;
        }

    }

   
}
