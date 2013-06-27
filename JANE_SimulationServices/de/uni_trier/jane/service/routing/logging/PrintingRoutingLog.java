/*******************************************************************************
 * 
 * PrintingGlobalRoutingLog.java
 * 
 * $Id: PrintingRoutingLog.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
import de.uni_trier.jane.service.*;

import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This global routing log simply writes all routing actions to the console.
 * This service can be started locally or globally
 */
public class PrintingRoutingLog implements GlobalService,SimulationService, LocalRoutingLogService {

    private static final ServiceID SERVICE_ID = new EndpointClassID(PrintingRoutingLog.class.getName());
    
    private GlobalOperatingSystem operatingSystem;

    public static void createInstance(ServiceUnit serviceUnit) {
        PrintingRoutingLog routingLog = new PrintingRoutingLog();
        serviceUnit.addService(routingLog);
        
    }

    public void start(SimulationOperatingSystem simulationOperatingSystem) {
        this.operatingSystem=simulationOperatingSystem;
        
    }
    
    public void start(GlobalOperatingSystem globalOperatingSystem) {
        this.operatingSystem = globalOperatingSystem;
    }

    public ServiceID getServiceID() {
        return SERVICE_ID;
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

    public void logDropMessage(RoutingHeader header) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " DROP");
    }

    public void logIgnoreMessage(RoutingHeader header) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " IGNORE");
    }

    public void logDeliverMessage( RoutingHeader header) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " DELIVER");
    }

    public void logForwardUnicast( MessageID messageID, RoutingHeader header,
    		Address receiver) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " UNICAST " + receiver);
    }

    public void logForwardBroadcast(MessageID messageID, RoutingHeader header) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " BROADCAST");
    }
    public void logForwardMulticast(MessageID messageID, RoutingHeader header, Address[] receivers) {
        StringBuffer recvs=new StringBuffer();
        for (int i=0;i<receivers.length;i++){
            recvs.append(": "+receivers[i]);
        }
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " MULTICAST " + recvs.toString());
        
    }

    public void logForwardError(MessageID messageID, RoutingHeader header, Address receiver) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " ERROR " + receiver);
    }

    public void logMessageReceived(MessageID messageID, RoutingHeader header, Address sender) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " RECEIVE " + sender);
    }

    public void logStart(RoutingHeader header) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " START");
    }
    
    public void logDelegateMessage(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " DELEGATE");
        
    }

	public void logLoopMessage(RoutingHeader header, int loopLength) {
        operatingSystem.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " LOOP " + loopLength);
	}

    //
    public void addLoggingAlgorithm(ServiceID routingAlgorithmToLog) {
        // TODO Auto-generated method stub
        
    }


}
