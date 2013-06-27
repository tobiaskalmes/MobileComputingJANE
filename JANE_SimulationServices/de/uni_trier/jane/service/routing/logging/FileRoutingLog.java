/*****************************************************************************
 * 
 * FileRoutingLog.java
 * 
 * $Id: FileRoutingLog.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.logging.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.io.*;
import java.util.*;

public class FileRoutingLog implements  GlobalService,SimulationService, LocalRoutingLogService {
    private static final ServiceID SERVICE_ID = new EndpointClassID(FileRoutingLog.class.getName());
    
    private GlobalOperatingSystem operatingSystem;

    private String filenname;

    private PrintWriter fis;

    private Set logAlgorithms;

    public FileRoutingLog(String filenname) {
        this.filenname=filenname;
        logAlgorithms=new LinkedHashSet();
    }

    public static void createInstance(ServiceUnit serviceUnit,String filenname) {
        FileRoutingLog routingLog = new FileRoutingLog(filenname);
        serviceUnit.addService(routingLog);
        
    }
    
    public void addLoggingAlgorithm(ServiceID routingAlgorithmToLog){
        logAlgorithms.add(routingAlgorithmToLog);
    }

    public void start(SimulationOperatingSystem simulationOperatingSystem) {
        this.operatingSystem=simulationOperatingSystem;
        try {
            fis=new PrintWriter(new FileOutputStream(filenname));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public void start(GlobalOperatingSystem globalOperatingSystem) {
        this.operatingSystem = globalOperatingSystem;
        try {
            fis=new PrintWriter(new FileOutputStream(filenname));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ServiceID getServiceID() {
        return SERVICE_ID;
    }

    public void finish() {
        fis.close();
    }

    public Shape getShape() {
        return null;
    }

    public void getParameters(Parameters parameters) {
        // ignore
    }

    public void logDropMessage(RoutingHeader header) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " DROP\n");
    }

    public void logIgnoreMessage(RoutingHeader header) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " IGNORE\n");
    }

    public void logDeliverMessage( RoutingHeader header) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " DELIVER\n");
    }

    public void logForwardUnicast( MessageID messageID, RoutingHeader header,
            Address receiver) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " UNICAST " + receiver+"\n");
    }

    public void logForwardBroadcast(MessageID messageID, RoutingHeader header) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " BROADCAST\n");
    }
    
    public void logForwardMulticast(MessageID messageID, RoutingHeader header, Address[] receivers) {
        if (!print(header.getRoutingAlgorithmID())) return;
        StringBuffer recvs=new StringBuffer();
        for (int i=0;i<receivers.length;i++){
            recvs.append(": "+receivers[i]);
        }
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " MULTICAST " + recvs.toString());
        
    }

    public void logForwardError(MessageID messageID, RoutingHeader header, Address receiver) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " ERROR " + receiver+"\n");
    }

    public void logMessageReceived(MessageID messageID, RoutingHeader header, Address sender) {
        if (!print(header.getRoutingAlgorithmID())) return;
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " RECEIVE " + sender+"\n");
    }

    private boolean print(ServiceID algo) {
        if (logAlgorithms.isEmpty()) return true;
        Iterator iterator=logAlgorithms.iterator();
        while (iterator.hasNext()){
            if (algo.equals(iterator.next())) return true;
        }
        return false;
    }

    public void logStart(RoutingHeader header) {
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " START\n");
    }
    
    public void logDelegateMessage(ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + messageID + " DELEGATE\n");
    }

	public void logLoopMessage(RoutingHeader header, int loopLength) {
        fis.write(operatingSystem.getSimulationTime() + " " + operatingSystem.getCallingDeviceID() + " " + header.getMessageID() + " LOOP " + loopLength + "\n");
	}

}
