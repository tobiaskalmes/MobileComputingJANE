/*****************************************************************************
 * 
 * RoutingServiceListener.java
 * 
 * $Id: RoutingServiceListener.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.routing; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;



/**
 * TODO: comment class  
 * @author daniel
 **/
public interface RoutingServiceListener {
    
    
    public void handleDropMessage(RoutingHeader routingHeader, RoutingData routingData);
    
    public class DropMessageSignal implements Signal {

        
        private RoutingHeader routingHeader;
        private RoutingData routingData;

        /**
         * Constructor for class <code>DropMessageSignal</code>
         *
         * @param routingHeader
         * @param routingData
         */
        public DropMessageSignal(RoutingHeader routingHeader, RoutingData routingData) {
            this.routingHeader=routingHeader;
            this.routingData=routingData;        
        }

        public void handle(SignalListener listener) {
            ((RoutingServiceListener)listener).handleDropMessage(routingHeader,routingData);
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return RoutingServiceListener.class;
        }

    }

    public void handleIgnoreMessage(RoutingHeader header, RoutingData routingData);


    public class IgnoreMessageSignal implements Signal{
        
        private RoutingHeader routingHeader;
        private RoutingData routingData;

        /**
         * Constructor for class <code>IgnoreMessageSignal</code>
         *
         * @param routingHeader
         * @param routingData
         */
        public IgnoreMessageSignal(RoutingHeader routingHeader, RoutingData routingData) {
            this.routingHeader=routingHeader;
            this.routingData=routingData;        
        }

        public void handle(SignalListener listener) {
            ((RoutingServiceListener)listener).handleIgnoreMessage(routingHeader,routingData);
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return RoutingServiceListener.class;
        }

    }

    
    public void handleDelegateMessage(RoutingHeader routingHeader, RoutingHeader oldRoutingHeader, RoutingData routingData);
   // public void handleDelegateDeliverMessage(RoutingHeader header, RoutingData routingData);
    public class DelegateMessageSignal implements Signal{
        
        private RoutingHeader routingHeader;
        private RoutingData routingData;
        private RoutingHeader oldRoutingHeader;

        /**
         * Constructor for class <code>IgnoreMessageSignal</code>
         *
         * @param routingHeader
         * @param oldRoutingHeader 
         * @param routingData
         */
        public DelegateMessageSignal(RoutingHeader routingHeader, RoutingHeader oldRoutingHeader, RoutingData routingData) {
            this.routingHeader=routingHeader;
            this.oldRoutingHeader=oldRoutingHeader;
            this.routingData=routingData;        
        }

        public void handle(SignalListener listener) {
            ((RoutingServiceListener)listener).handleDelegateMessage(routingHeader,oldRoutingHeader,routingData);
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return RoutingServiceListener.class;
        }

    }

    
    

    /**
     * 
     * TODO: comment class  
     * @author daniel
     *
     */
//    public class DelegateDeliverMessageSignal implements Signal {
//
//        private RoutingHeader header;
//        private RoutingData routingData;
//
//        public DelegateDeliverMessageSignal(RoutingHeader header, RoutingData routingData) {
//            this.header=header;
//            this.routingData=routingData;
//        }
//
//        public void handle(SignalListener listener) {
//            ((RoutingServiceListener)listener).handleDelegateDeliverMessage(header,routingData);
//            
//        }
//
//        public Dispatchable copy() {
//            return this;
//        }
//
//        public Class getReceiverServiceClass() {
//            return RoutingServiceListener.class;
//        }
//
//    }

    

}
