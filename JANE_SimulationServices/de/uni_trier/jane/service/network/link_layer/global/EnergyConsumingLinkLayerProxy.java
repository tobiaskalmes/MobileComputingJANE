/*****************************************************************************
 * 
 * GlobalNetworkLinkLayer.java
 * 
 * $Id: EnergyConsumingLinkLayerProxy.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.global;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.energy.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a proxy service for a GlobalLinkLayer implementation
 * All link layer signals are passed to the GlobalLinkLayer
 * This service must be started on each device to use the GlobalLinkLayer implementation
 * @see de.uni_trier.jane.service.network.link_layer.global.GlobalLinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.collision_free.CollisionFreeNetwork
 * @see de.uni_trier.jane.service.network.link_layer.shared_network.SharedNetwork
 * @see de.uni_trier.jane.service.network.link_layer.LinkLayer
 * @see de.uni_trier.jane.service.network.link_layer.UnicastCallbackHandler
 * @see de.uni_trier.jane.service.network.link_layer.BroadcastCallbackHandler
 */
public class EnergyConsumingLinkLayerProxy extends GlobalNetworkLinkLayerProxy implements LinkLayer, LinkLayer_sync, SimulationService, GlobalLinkLayerMessageReceiver {

	// initialized in constructor
    //private LinkLayerAddress linkLayerAddress;
    //private ServiceID globalNetworkServiceID;
    private ServiceID energyServiceID;
    private double alpha;
    private double a;
    private double b;
    private double c;
    private double broadcastRadius;
    

    // initialize on startup
    private EnergyConsumptionListenerServiceStub energyConsumptionListenerServiceStub;
    private GlobalKnowledge globalKnowledge;
    private DeviceID ownDeviceID;


    public static void createFactory(ServiceUnit serviceUnit) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				if(!serviceUnit.hasService(LinkLayer.class)) {
					EnergyConsumingLinkLayerProxy.createInstance(serviceUnit);
				}
			}
       	});
    }

    public static void createFactory(ServiceUnit serviceUnit, final double alpha, final double a,
    		final double b, final double c, final double broadcastRadius) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				if(!serviceUnit.hasService(LinkLayer.class)) {
					EnergyConsumingLinkLayerProxy.createInstance(serviceUnit, alpha, a, b, c, broadcastRadius);
				}
			}
       	});
    }

    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, 1.0, 0.0, 0.0, 0.0, 0.0);
    }

    public static void createInstance(ServiceUnit serviceUnit, double alpha, double a, double b, double c, double broadcastRadius) {
    	DeviceID deviceID = serviceUnit.getDeviceID();
    	Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);
    	if(!serviceUnit.hasService(EnergyConsumptionListenerService.class)) {
    		DefaultEnergyService.createInstance(serviceUnit);
    	}
    	ServiceID energyServiceID = serviceUnit.getService(EnergyConsumptionListenerService.class);
    	ServiceID globalLinkLayerService = serviceUnit.getService(GlobalLinkLayer.class);
		EnergyConsumingLinkLayerProxy linkLayerProxy = new EnergyConsumingLinkLayerProxy(
				linkLayerAddress, globalLinkLayerService, energyServiceID, alpha, a, b, c, broadcastRadius);
		serviceUnit.addService(linkLayerProxy);
    }
    
    /**
     * Cosntructor for class GlobalNetworkLinkLayer
     * @param linkLayerAddress			the linkLayerAddress for this device
     * @param globalNetworkServiceID	the serviceID of the GlobalLinkLayer 
     */
    public EnergyConsumingLinkLayerProxy(Address linkLayerAddress, ServiceID globalNetworkServiceID, ServiceID energyServiceID, double alpha, double a, double b, double c, double broadcastRadius) {
        super(new StackedClassID(EnergyConsumingLinkLayerProxy.class.getName(),globalNetworkServiceID),linkLayerAddress,globalNetworkServiceID);
        
        
        this.energyServiceID = energyServiceID;
        this.alpha = alpha;
        this.a = a;
        this.b = b;
        this.c = c;
        this.broadcastRadius = broadcastRadius;
        
    }

    public void start(SimulationOperatingSystem operatingService) {
        super.start(operatingService);
        
        energyConsumptionListenerServiceStub = new EnergyConsumptionListenerServiceStub(operatingService, energyServiceID);
        globalKnowledge = operatingService.getGlobalKnowledge();
        ownDeviceID = operatingService.getDeviceID();
    }

 

    public void sendBroadcast(LinkLayerMessage message) {
        super.sendBroadcast(message);
        handleBroadcastEnergyConsumption(message);
    }

 



    public void sendBroadcast(LinkLayerMessage message, BroadcastCallbackHandler broadcastCallbackHandler) {
        super.sendBroadcast(message,broadcastCallbackHandler);
        handleBroadcastEnergyConsumption(message);
    }

    public void sendUnicast(Address receiver, LinkLayerMessage message) {
        super.sendUnicast(receiver,message);
        handleUnicastEnergyConsumption(receiver, message);
    }

    public void sendUnicast(Address receiver, LinkLayerMessage message,UnicastCallbackHandler  handle) {
        super.sendUnicast(receiver,message,handle);
        handleUnicastEnergyConsumption(receiver, message);
    }

    public void deliverMessage(LinkLayerInfo info, LinkLayerMessage linkLayerMessage) {
        operatingService.sendSignal(new MessageReceiveSignal(info,linkLayerMessage));
        handleReceiveEnergyConsumption(info, linkLayerMessage);
    }

//    public LinkLayerProperties getLinkLayerProperties() {
//        return new LinkLayerProperties(linkLayerAddress,-1,Double.MAX_VALUE);
//    }

	public void getParameters(Parameters parameters) {
		parameters.addParameter("address", linkLayerAddress.toString());
		parameters.addParameter("transmit power", "len * (" + a + " * d^" + alpha + " + " + b + " * d + " + c + ")");
		parameters.addParameter("receive power", "len * " + c);
		parameters.addParameter("broadcast radius", broadcastRadius);
	}

	private void handleBroadcastEnergyConsumption(LinkLayerMessage message) {
		handleTransmitEnergyConsumption(broadcastRadius, message);
	}

	private void handleUnicastEnergyConsumption(Address receiver, LinkLayerMessage message) {
		DeviceID receiverDeviceID = globalKnowledge.getDeviceID(receiver);
		Position senderPosition = globalKnowledge.getTrajectory(ownDeviceID).getPosition();
		Position receiverPosition = globalKnowledge.getTrajectory(receiverDeviceID).getPosition();
		handleTransmitEnergyConsumption(senderPosition.distance(receiverPosition), message);
	}

	private void handleTransmitEnergyConsumption(double distance, LinkLayerMessage message) {
		double joule = a * Math.pow(distance, alpha) + b * distance + c;
		handleEnergyConsumption(joule);
	}

    private void handleReceiveEnergyConsumption(LinkLayerInfo info, LinkLayerMessage message) {
    	double joule = c;
		handleEnergyConsumption(joule);
    }

    private void handleEnergyConsumption(double joule) {
    	energyConsumptionListenerServiceStub.reduceEnergy(joule);
    }

}
