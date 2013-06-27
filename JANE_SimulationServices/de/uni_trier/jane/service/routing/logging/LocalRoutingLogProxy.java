/*******************************************************************************
 * 
 * LocalRoutingLogProxy.java
 * 
 * $Id: LocalRoutingLogProxy.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This service provides a proxy in order to pass information about all routing actions
 * from the routing service to the global routing log.
 */
public class LocalRoutingLogProxy implements SimulationService, LocalRoutingLogService {

    private ServiceID globalRoutingLogID;
    private GlobalRoutingLogServiceStub globalRoutingLogStub;
    private Address address;
    private SimulationOperatingSystem simulationOperatingSystem;
    

    public LocalRoutingLogProxy(ServiceID globalRoutingLogID) {
        this.globalRoutingLogID = globalRoutingLogID;
    }
    
    public void start(SimulationOperatingSystem simulationOperatingSystem) {
    	this.simulationOperatingSystem = simulationOperatingSystem;
        globalRoutingLogStub = new GlobalRoutingLogServiceStub(simulationOperatingSystem, globalRoutingLogID);
        
        simulationOperatingSystem.registerAtService(DefaultRoutingService.SERVICE_ID, RoutingService.class); // TODO der Routing Log Proxy könnte eventuell auch für andere RoutingService-Implementierungen von Interesse sein!!!
        
    }

    public ServiceID getServiceID() {
        return null;
    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        return null;
    }

    public void getParameters(Parameters parameters) {
        // ignore
    }

    public void logStart(RoutingHeader header) {
        globalRoutingLogStub.logStart(getAddress(), header.getMessageID());
    }

    public void logDropMessage(RoutingHeader header) {
        globalRoutingLogStub.logDropMessage(getAddress(), header.getMessageID());
    }

    public void logIgnoreMessage(RoutingHeader header) {
        globalRoutingLogStub.logIgnoreMessage(getAddress(), header.getMessageID());
    }

    public void logDeliverMessage(RoutingHeader header) {
        globalRoutingLogStub.logDeliverMessage(getAddress(), header.getMessageID());
    }

    public void logForwardUnicast(MessageID messageID, RoutingHeader header, Address receiver) {
        globalRoutingLogStub.logForwardUnicast(getAddress(), messageID, header, receiver);
    }

    public void logForwardBroadcast(MessageID messageID, RoutingHeader header) {
        globalRoutingLogStub.logForwardBroadcast(getAddress(), messageID, header);
    }
    
    public void logForwardMulticast(MessageID messageID, RoutingHeader header, Address[] receivers) {
        //globalRoutingLogStub.lo
        throw new IllegalAccessError("not implemented");
        
    }

    public void logForwardError(MessageID messageID, RoutingHeader header, Address receiver) {
        globalRoutingLogStub.logForwardError(getAddress(), messageID, header, receiver);
    }

    public void logMessageReceived(MessageID messageID, RoutingHeader header, Address sender) {
        globalRoutingLogStub.logMessageReceived(getAddress(), messageID, header, sender);
    }
    
    public void logDelegateMessage(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
    	globalRoutingLogStub.logDelegateMessage(getAddress(), routingAlgorithmID, messageID, routingHeader);
    }

	public void logLoopMessage(RoutingHeader header, int loopLength) {
    	globalRoutingLogStub.logLoopMessage(getAddress(), header.getMessageID(), loopLength);
	}

    private Address getAddress() {
    	if(address == null) {
            RoutingService_sync routingService = (RoutingService_sync)simulationOperatingSystem.getAccessListenerStub(
            		DefaultRoutingService.SERVICE_ID, RoutingService_sync.class);
            address = routingService.getOwnAddress();
    	}
    	return address;
    }
    
    public void addLoggingAlgorithm(ServiceID routingAlgorithmToLog){
       // globalRoutingLogStub.addLoggingAlgorithm(routingAlgorithmToLog);
        //TODO:
    }

}
