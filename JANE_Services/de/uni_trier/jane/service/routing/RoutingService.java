/*******************************************************************************
 * 
 * RoutingService.java
 * 
 * $Id: RoutingService.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
 * 
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K.
 * Lehnert
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 ******************************************************************************/
package de.uni_trier.jane.service.routing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;


/**
 * A routing service which provides message routing algorithms
 * should implement this interface.
 */
public interface RoutingService {

    /**
     * Start a unicast routing task when this method is called.
     * @param routingAlgorithmID    the id of the routing algorithm used to handle this message
     * @param destination           the destination address
     * @param routingData the data to be sent to the destination
     * @deprecated use startRoutingTask instead
     */
   public void startUnicast(ServiceID routingAlgorithmID, RoutingData routingData, Address destination);

    /**
     * Starts a routing task given by the RoutingHeader. The RoutingHeader must contain the ID of the used 
     * RoutingAlgorithm and the basis Parameters for the RoutingAlgorithms. It is also possible to adjust the 
     * algorithm on a per message basis
     * @param routingHeader	the minimum routing header for the addressed RoutingAlgorithm
     * @param routingData	the data to be send by the routing algorithm
     */
    public void startRoutingTask(RoutingHeader routingHeader, RoutingData routingData);

    /**
     * Starts a routing task given by the RoutingHeader. The RoutingHeader must contain the ID of the used 
     * RoutingAlgorithm and the basis Parameters for the RoutingAlgorithms. It is also possible to adjust the 
     * algorithm on a per message basis
     * @param routingHeader the minimum routing header for the addressed RoutingAlgorithm
     * @param routingData   the data to be send by the routing algorithm
     * @param callback      callback object for local message status
     */
    public void startRoutingTask(RoutingHeader routingHeader, RoutingData routingData, RoutingServiceCallback callback);
    
    /**
     * This signal can be used in order to invoke a unicast routing task.
     */
//    public static class StartUnicastSignal implements Signal {
//
//    	private ServiceID routingAlgorithmID;
//    	private RoutingData routingData;
//        private LinkLayerAddress destination;
//        
//        /**
//         * Construct a new start unicast signal object.
//         * @param routingAlgorithmClass the class of the routing algorithm used to handle this unicast packet
//         * @param destination the message destination
//         * @param payload the data to be sent to the destination
//         */
//		public StartUnicastSignal(ServiceID routingAlgorithmID,
//				RoutingData routingData, LinkLayerAddress destination) {
//			this.routingAlgorithmID = routingAlgorithmID;
//			this.routingData = routingData;
//			this.destination = destination;
//		}
//
//        public Class getReceiverServiceClass() {
//            return RoutingService.class;
//        }
//
//        public void handle(SignalListener service) {
//            RoutingService listener = (RoutingService)service;
//            listener.startUnicast(routingAlgorithmID, routingData, destination);
//        }
//
//        public Dispatchable copy() {
//        	RoutingData routingDataCopy = (RoutingData)routingData.copy();
//        	if(routingDataCopy == routingData) {
//        		return this;
//        	}
//            return new StartUnicastSignal(routingAlgorithmID, routingDataCopy, destination);
//        }
//
//    }
}