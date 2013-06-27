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
 * Systemsoftware and Distrubuted Systems
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
package de.uni_trier.jane.service.network.link_layer.global; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.StackedClassID;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.extended.*;
import de.uni_trier.jane.service.unit.ServiceFactory;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.service.SimulationOperatingSystem;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class GlobalNetworkLinkLayerExtended extends GlobalNetworkLinkLayerProxy
        implements LinkLayerExtended {

    public static void createFactory(ServiceUnit serviceUnit){
        serviceUnit.addServiceFactory(new ServiceFactory() {
            public void initServices(ServiceUnit serviceUnit) {
                if(!serviceUnit.hasService(LinkLayer.class)) {
                    GlobalNetworkLinkLayerExtended.createInstance(serviceUnit);
                }
            }
        });
    }   
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param defaultConfiguration
     */
    public static void createFactory(ServiceUnit serviceUnit,final LinkLayerConfiguration defaultConfiguration) {
        serviceUnit.addServiceFactory(new ServiceFactory() {
            public void initServices(ServiceUnit serviceUnit) {
                if(!serviceUnit.hasService(LinkLayer.class)) {
                    DeviceID deviceID = serviceUnit.getDeviceID();
                    Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);            
                    GlobalNetworkLinkLayerExtended.createInstance(serviceUnit,linkLayerAddress,defaultConfiguration);
                }
            }
        });
    }
    
    /**
     * 
     */
    public static void createInstance(ServiceUnit serviceUnit) {
        DeviceID deviceID = serviceUnit.getDeviceID();
        Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);
        createInstance(serviceUnit, linkLayerAddress);
    }

    /**
     * 
     */
    public static void createInstance(ServiceUnit serviceUnit, Address linkLayerAddress){
        createInstance(serviceUnit,linkLayerAddress,new LinkLayerConfiguration(10,0));
    }
    
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param linkLayerAddress
     * @param defaultConfiguration
     */
    public static void createInstance(ServiceUnit serviceUnit, Address linkLayerAddress,LinkLayerConfiguration defaultConfiguration) {
        ServiceID globalLinkLayerService = serviceUnit.getService(GlobalLinkLayer.class);
        GlobalNetworkLinkLayerExtended linkLayerProxy = new GlobalNetworkLinkLayerExtended(
                new StackedClassID(GlobalNetworkLinkLayerProxy.class.getName(), globalLinkLayerService),
                linkLayerAddress, globalLinkLayerService, defaultConfiguration);
        serviceUnit.addService(linkLayerProxy);
    }
    
    
    
    private LinkLayerExtended_Plugin plugin;
    
    private LinkLayerConfiguration defaultConfiguration;

    /**
     * 
     * Constructor for class <code>GlobalNetworkLinkLayerExtended</code>
     * @param serviceID
     * @param linkLayerAddress
     * @param globalNetworkServiceID
     * @param defaultConfiguration
     */
    public GlobalNetworkLinkLayerExtended(ServiceID serviceID, Address linkLayerAddress, ServiceID globalNetworkServiceID,
            LinkLayerConfiguration defaultConfiguration) {
        super(serviceID, linkLayerAddress, globalNetworkServiceID);
        this.defaultConfiguration=defaultConfiguration;
    }
    

    public void start(SimulationOperatingSystem operatingService) {
        super.start(operatingService);
        plugin=new LinkLayerExtended_Plugin(this,operatingService,defaultConfiguration);
        operatingService.registerSignalListener(LinkLayerExtended_async.class);
        //plugin.
    }


    public void sendAddressedBroadcast(Address receiver,  LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        this.plugin.sendAddressedBroadcast(receiver,  message,configuration, callbackHandler);
    }


    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message) {
        this.plugin.sendAddressedBroadcast(receiver, message);
    }


    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration,AddressedBroadcastCallbackHandler callbackHandler) {
        this.plugin.sendAddressedBroadcast(receivers,  message,configuration, callbackHandler);
    }


    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message) {
        this.plugin.sendAddressedBroadcast(receivers, message);
    }


    public void sendAddressedMulticast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        this.plugin.sendAddressedMulticast(receivers,  message,configuration, callbackHandler);
    }

    public void sendBroadcast(LinkLayerMessage message, LinkLayerConfiguration configuration, BroadcastCallbackHandler callbackHandler) {
        this.plugin.sendBroadcast(message, configuration, callbackHandler);
    }

    public void sendUnicast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        this.plugin.sendUnicast(receiver, message, configuration, callbackHandler);
    }
    
    // Adrian, 20.10.2006, java 1.3
    //@Override
    public void deliverMessage(LinkLayerInfo info, LinkLayerMessage linkLayerMessage) {
        // TODO Auto-generated method stub
        operatingService.sendSignal(new MessageReceiveSignal(new LinklayerInfoExtended(info),linkLayerMessage));
        //super.deliverMessage(new LinklayerInfoExtended(info), linkLayerMessage);
    }
    

  

}
