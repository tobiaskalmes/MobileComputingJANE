/*******************************************************************************
 * 
 * PrintingGlobalRoutingLog.java
 * 
 * $Id: PrintingGlobalRoutingLog.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
 */
public class PrintingGlobalRoutingLog implements GlobalService, GlobalRoutingLogService {

    private static final ServiceID SERVICE_ID = new EndpointClassID(PrintingGlobalRoutingLog.class.getName());
    
    private GlobalOperatingSystem globalOperatingSystem;

    public static void createInstance(ServiceUnit serviceUnit) {
        PrintingGlobalRoutingLog routingLog = new PrintingGlobalRoutingLog();
        serviceUnit.addService(routingLog);
        LocalRoutingLogProxyFactory proxyFactory = new LocalRoutingLogProxyFactory(routingLog.getServiceID());
        serviceUnit.addServiceFactory(proxyFactory);
    }

    public void start(GlobalOperatingSystem globalOperatingSystem) {
        this.globalOperatingSystem = globalOperatingSystem;
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

    public void logDropMessage(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " DROP");
    }

    public void logIgnoreMessage(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " IGNORE");
    }

    public void logDeliverMessage(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " DELIVER");
    }

    public void logForwardUnicast(Address address, MessageID messageID, RoutingHeader header,
    		Address receiver) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " UNICAST " + receiver);
    }

    public void logForwardBroadcast(Address address, MessageID messageID, RoutingHeader header) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " BROADCAST");
    }

    public void logForwardError(Address address, MessageID messageID, RoutingHeader header, Address receiver) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " ERROR " + receiver);
    }

    public void logMessageReceived(Address address, MessageID messageID, RoutingHeader header, Address sender) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " RECEIVE " + sender);
    }

    public void logStart(Address address, MessageID messageID) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " START");
    }

	public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " DELEGATE");
	}

	public void logLoopMessage(Address address, MessageID messageID, int loopLength) {
        globalOperatingSystem.write(globalOperatingSystem.getSimulationTime() + " " + address + " " + messageID + " LOOP" + loopLength);
	}

}
