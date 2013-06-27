package de.uni_trier.jane.service.network.link_layer.global;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.simulation.service.*;

public class UDGLinkLayerProxy extends GlobalNetworkLinkLayerProxy implements UDGLinkLayer {

	public static final ServiceReference DEFAULT_INSTANCE = new ServiceReference("linkLayer") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
			if(!serviceUnit.hasService(UDGLinkLayer.class)) {
		    	DeviceID deviceID = serviceUnit.getDeviceID();
		    	Address linkLayerAddress = new SimulationLinkLayerAddress(deviceID);
		    	ServiceID globalLinkLayerID = serviceUnit.getService(GlobalLinkLayer.class);
		    	ServiceID ownServiceID = new EndpointClassID(UDGLinkLayerProxy.class.getName());
				Service service = new UDGLinkLayerProxy(ownServiceID, linkLayerAddress, globalLinkLayerID);
				serviceUnit.addService(service);
			}
			return serviceUnit.getService(UDGLinkLayer.class);
		}
	};

	private double udgRadius;
	
    public static void createFactory(ServiceUnit serviceUnit) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				if(!serviceUnit.hasService(LinkLayer.class)) {
					UDGLinkLayerProxy.createInstance(serviceUnit);
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
		GlobalNetworkLinkLayerProxy linkLayerProxy = new UDGLinkLayerProxy(
				new StackedClassID(GlobalNetworkLinkLayerProxy.class.getName(), globalLinkLayerService),
				linkLayerAddress, globalLinkLayerService);
		serviceUnit.addService(linkLayerProxy);
    }

	public UDGLinkLayerProxy(ServiceID ownServiceID, Address linkLayerAddress, ServiceID globalNetworkServiceID) {
		super(ownServiceID, linkLayerAddress, globalNetworkServiceID);
		// TODO Auto-generated constructor stub
	}

	public void start(SimulationOperatingSystem operatingService) {
		super.start(operatingService);
        operatingService.registerAccessListener(UDGLinkLayer.class);

		double minimumRadius = operatingService.getGlobalKnowledge().getMinimumTransmissionRadius();
		double maximumRadius = operatingService.getGlobalKnowledge().getMaximumTransmissionRadius();
		if(minimumRadius != maximumRadius) {
			throw new IllegalStateException("An UDG link layer requires unique transmission radii.");
		}
		
		udgRadius = minimumRadius;
		
	}

	public double getUDGRadius() {
		return udgRadius;
	}

}
