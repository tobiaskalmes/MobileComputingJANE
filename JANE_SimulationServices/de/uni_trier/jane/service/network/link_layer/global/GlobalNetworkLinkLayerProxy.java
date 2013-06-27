/*****************************************************************************
 * 
 * GlobalNetworkLinkLayer.java
 * 
 * $Id: GlobalNetworkLinkLayerProxy.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
import de.uni_trier.jane.service.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerObserver.LinkLayerObserverStub;
import de.uni_trier.jane.service.network.link_layer.collision_free.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
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
// TODO der Namen sollte klar ausdrücken, dass es sich hierbei um einen lokalen Proxy für GlobaleLinkLayer handelt.
public class GlobalNetworkLinkLayerProxy extends LinkLayerBase implements LinkLayer,LinkLayer_sync,LinkLayer_async,SimulationService,GlobalLinkLayerMessageReceiver, LinkLayerObserver {

	public static final IdentifiedServiceElement INITIALIZATION_ELEMENT = new IdentifiedServiceElement("linkLayer") {

		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			ServiceID networkID = GlobalLinkLayer.REQUIRED_SERVICE.getInstance(serviceUnit, initializationContext);
			Address address = new SimulationLinkLayerAddress(serviceUnit.getDeviceID());
			serviceUnit.addService(new GlobalNetworkLinkLayerProxy(ownServiceID, address, networkID));
		}
		
		public Parameter[] getParameters() {
			return new Parameter[] { GlobalLinkLayer.REQUIRED_SERVICE };
		}

	};

    protected Address linkLayerAddress;
    private ServiceID globalNetworkServiceID;
    protected SimulationOperatingSystem operatingService;
    private ServiceID serviceID;
    private LinkLayerObserverStub observer;

    protected GlobalLinkLayer globalLinkLayer;

    private GlobalLinkLayer_sync linkLayer_sync;

    public static void createFactory(ServiceUnit serviceUnit) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				if(!serviceUnit.hasService(LinkLayer.class)) {
					GlobalNetworkLinkLayerProxy.createInstance(serviceUnit);
				}
			}
       	});
    }

    public static void createInstance(ServiceUnit serviceUnit) {
    	DeviceID deviceID = serviceUnit.getDeviceID();
    	Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);
    	createInstance(serviceUnit, linkLayerAddress);
    }

    public static void createInstance(ServiceUnit serviceUnit, Address linkLayerAddress) {
    	ServiceID globalLinkLayerService = serviceUnit.getService(GlobalLinkLayer.class);
		GlobalNetworkLinkLayerProxy linkLayerProxy = new GlobalNetworkLinkLayerProxy(
		        new StackedClassID(GlobalNetworkLinkLayerProxy.class.getName(), globalLinkLayerService),
				linkLayerAddress, globalLinkLayerService);
		serviceUnit.addService(linkLayerProxy);
    }

    public GlobalNetworkLinkLayerProxy(Address linkLayerAddress, ServiceID globalNetworkServiceID) {
    	this(new StackedClassID(GlobalNetworkLinkLayerProxy.class.getName(), globalNetworkServiceID), linkLayerAddress, globalNetworkServiceID);
    }

    /**
     * Cosntructor for class GlobalNetworkLinkLayer
     * @param linkLayerAddress			the linkLayerAddress for this device
     * @param globalNetworkServiceID	the serviceID of the GlobalLinkLayer 
     */
    public GlobalNetworkLinkLayerProxy(ServiceID serviceID, Address linkLayerAddress, ServiceID globalNetworkServiceID) {
        this.linkLayerAddress=linkLayerAddress;
        this.globalNetworkServiceID=globalNetworkServiceID;
        this.serviceID = serviceID;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#getLinkLayerAddress()
     */
//    public LinkLayerAddress getLinkLayerAddress() {
//        return linkLayerAddress;
//    }
    
    public Address getNetworkAddress() {
        return linkLayerAddress;
    }
    
    private boolean visualize() {
        
        return operatingService.isVisualized(operatingService.getCallingServiceID());
    }

    
    public void sendBroadcast(LinkLayerMessage message) {

        sendBroadcast(message,null);
    }

    
    
    public void sendUnicast(Address receiver, LinkLayerMessage message) {
        sendUnicast(receiver,message,null);
    }
    
    public void sendBroadcast(LinkLayerMessage message, final BroadcastCallbackHandler callbackHandler) {
        //TaskHandle globalTask=operatingService.startTask(operatingService.getGlobalDeviceID(),globalNetworkServiceID,new GlobalLinkLayer.SendBroadcastTask
        globalLinkLayer.sendBroadcast(message,visualize(),new BroadcastCallbackHandler() {
            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.network.link_layer.BroadcastObserver#handleBroadcastSent(de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
             */
            public void notifyBroadcastProcessed(LinkLayerMessage message) {
                observer.notifyBroadcastProcessed(message);
                if (callbackHandler!=null)
                    callbackHandler.notifyBroadcastProcessed(message);


            }

         
        });

        
    }

    
    public void sendUnicast(Address receiver, LinkLayerMessage message, final UnicastCallbackHandler callbackHandler) {

        globalLinkLayer.sendUnicast(receiver,message,visualize(),new UnicastCallbackHandler() {
           
            public void notifyUnicastProcessed(Address receiver,
                    LinkLayerMessage message) {
                observer.notifyUnicastReceived(receiver,message);
                if (callbackHandler!=null)
                    callbackHandler.notifyUnicastProcessed(receiver,message);
                
                

            }

            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastReceived(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
             */
            public void notifyUnicastReceived(Address receiver,
                    LinkLayerMessage message) {
                observer.notifyUnicastReceived(receiver,message);
                if (callbackHandler!=null)
                    callbackHandler.notifyUnicastReceived(receiver,message);


            }

            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastLost(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
             */
            public void notifyUnicastLost(Address receiver,
                    LinkLayerMessage message) {
                observer.notifyUnicastLost(receiver,message);
                if (callbackHandler!=null)
                    callbackHandler.notifyUnicastLost(receiver,message);



            }

            /* (non-Javadoc)
             * @see de.uni_trier.ssds.service.network.link_layer.UnicastObserver#notifyUnicastUndefined(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
             */
            public void notifyUnicastUndefined(Address receiver,
                    LinkLayerMessage message) {
                observer.notifyUnicastUndefined(receiver,message);
                if (callbackHandler!=null)
                    callbackHandler.notifyUnicastUndefined(receiver,message);
                


            }

        });
        

    }
  
    public void start(SimulationOperatingSystem operatingService) {
        this.operatingService=operatingService;
        
        operatingService.registerAccessListener(LinkLayer_sync.class);
        operatingService.registerSignalListener(LinkLayer_async.class);
        operatingService.registerAtService(operatingService.getGlobalDeviceID(),globalNetworkServiceID,GlobalLinkLayer.class);
        globalLinkLayer=(GlobalLinkLayer)operatingService.getSignalListenerStub(globalNetworkServiceID,GlobalLinkLayer.class);
        
        linkLayer_sync=(GlobalLinkLayer_sync)operatingService.getAccessListenerStub(globalNetworkServiceID,GlobalLinkLayer_sync.class);
        linkLayer_sync.registerDevice(linkLayerAddress);
        //operatingService.sendSignal(operatingService.getGlobalDeviceID(),globalNetworkServiceID,new GlobalLinkLayer.RegisterDeviceSignal(linkLayerAddress));
        
        operatingService.registerAddress(linkLayerAddress);
        observer=new LinkLayerObserver.LinkLayerObserverStub(operatingService);
        
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getServiceID()
     */
    public ServiceID getServiceID() {
        return serviceID;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#finish()
     */
    public void finish() {
       // operatingService.unregisterAtService(globalNetworkServiceID,GlobalLinkLayer.class);
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.Service#getShape()
     */
    public Shape getShape() {
        return EmptyShape.getInstance();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.shared_network.GlobalLinkLayerMessageReceiver#deliverMessage(de.uni_trier.ssds.service.network.link_layer.LinkLayerAddress, de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage)
     */
    public void deliverMessage(LinkLayerInfo info, LinkLayerMessage linkLayerMessage) {
        operatingService.sendSignal(new MessageReceiveSignal(info,linkLayerMessage));
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.network.link_layer.LinkLayer#getLinkLayerProperties()
     */
    public LinkLayerProperties getLinkLayerProperties() {

        return linkLayer_sync.getLinkLayerProperties();//new LinkLayerProperties(linkLayerAddress,-1,Double.MAX_VALUE);
    }
    
    public void setLinkLayerProperties(LinkLayerProperties props) {
        linkLayer_sync.setLinkLayerProperties(props);
        
    }

    public void setPromiscuous(boolean promiscuous) {
        globalLinkLayer.setPromiscuous(promiscuous);
        
    }
	public void getParameters(Parameters parameters) {
		parameters.addParameter("address", linkLayerAddress);
		parameters.addParameter("network", globalNetworkServiceID);
	}

    public void notifyBroadcastProcessed(LinkLayerMessage message) {
        observer.notifyBroadcastProcessed(message);
    }

    public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
        observer.notifyUnicastLost(receiver, message);
    }

    public void notifyUnicastProcessed(Address receiver, LinkLayerMessage message) {
        observer.notifyUnicastProcessed(receiver, message);
    }

    public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
        observer.notifyUnicastReceived(receiver, message);
    }

    public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message) {
        observer.notifyUnicastUndefined(receiver, message);
    }

  
    
    



}
