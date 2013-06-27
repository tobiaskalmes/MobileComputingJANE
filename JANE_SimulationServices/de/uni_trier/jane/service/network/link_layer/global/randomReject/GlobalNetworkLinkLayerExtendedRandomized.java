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
package de.uni_trier.jane.service.network.link_layer.global.randomReject; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.StackedClassID;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.extended.LinkLayerConfiguration;
import de.uni_trier.jane.service.network.link_layer.global.*;
import de.uni_trier.jane.service.network.link_layer.global.GlobalNetworkLinkLayerExtended;
import de.uni_trier.jane.service.unit.ServiceFactory;
import de.uni_trier.jane.service.unit.ServiceUnit;


/**
 * @author goergen
 *
 * TODO comment class
 */
public class GlobalNetworkLinkLayerExtendedRandomized extends
        GlobalNetworkLinkLayerExtended {

    
    
    
    
    /**
     * TODO Comment method
     * @param serviceUnit
     */
    public static void createFactory(ServiceUnit serviceUnit){
        serviceUnit.addServiceFactory(new ServiceFactory() {
            public void initServices(ServiceUnit serviceUnit) {
                if(!serviceUnit.hasService(LinkLayer.class)) {
                    GlobalNetworkLinkLayerExtendedRandomized.createInstance(serviceUnit);
                }
            }
        });
    }   
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param defaultConfiguration
     * @param receiveDecider
     */
    public static void createFactory(ServiceUnit serviceUnit,final LinkLayerConfiguration defaultConfiguration, final ReceiveDecider receiveDecider) {
        serviceUnit.addServiceFactory(new ServiceFactory() {
            public void initServices(ServiceUnit serviceUnit) {
                if(!serviceUnit.hasService(LinkLayer.class)) {
                    DeviceID deviceID = serviceUnit.getDeviceID();
                    Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);            
                    GlobalNetworkLinkLayerExtendedRandomized.createInstance(serviceUnit,linkLayerAddress,defaultConfiguration,receiveDecider);
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
        createInstance(serviceUnit,linkLayerAddress,new LinkLayerConfiguration(10,0),new LinearReceiveDecider(serviceUnit.getDistributionCreator(),4));
    }
    
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param linkLayerAddress
     * @param defaultConfiguration
     * @param receiveDecider
     */
    public static void createInstance(ServiceUnit serviceUnit, Address linkLayerAddress,LinkLayerConfiguration defaultConfiguration, ReceiveDecider receiveDecider) {
        ServiceID globalLinkLayerService = serviceUnit.getService(GlobalLinkLayer.class);
        GlobalNetworkLinkLayerExtendedRandomized linkLayerProxy = new GlobalNetworkLinkLayerExtendedRandomized(
                new StackedClassID(GlobalNetworkLinkLayerProxy.class.getName(), globalLinkLayerService),
                linkLayerAddress, globalLinkLayerService, defaultConfiguration, receiveDecider);
        serviceUnit.addService(linkLayerProxy);
    }
    
    
    
    
    
    
    
    private ReceiveDecider receiveDecider;

    /**
     * 
     * Constructor for class <code>GlobalNetworkLinkLayerExtendedRandomized</code>
     * @param serviceID
     * @param linkLayerAddress
     * @param globalNetworkServiceID
     * @param defaultConfiguration
     * @param receiveDecider
     */
    public GlobalNetworkLinkLayerExtendedRandomized(ServiceID serviceID, Address linkLayerAddress,
            ServiceID globalNetworkServiceID, LinkLayerConfiguration defaultConfiguration, ReceiveDecider receiveDecider) {
        super(serviceID, linkLayerAddress, globalNetworkServiceID, defaultConfiguration);
        this.receiveDecider=receiveDecider;
    }
    
    // Adrian, 20.10.2006, java 1.3
    //@Override
    public void deliverMessage(LinkLayerInfo info, LinkLayerMessage linkLayerMessage) {
        if (receiveDecider.receive(info,operatingService.getGlobalKnowledge())){
            super.deliverMessage(info, linkLayerMessage);
        }
    }
    
    

    

}
