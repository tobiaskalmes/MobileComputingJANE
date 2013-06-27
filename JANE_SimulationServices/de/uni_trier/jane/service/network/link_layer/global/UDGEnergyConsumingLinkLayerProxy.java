package de.uni_trier.jane.service.network.link_layer.global;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.energy.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;

public class UDGEnergyConsumingLinkLayerProxy extends EnergyConsumingLinkLayerProxy implements UDGLinkLayer {

	private double udgRadius;

    public static void createFactory(ServiceUnit serviceUnit) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				if(!serviceUnit.hasService(LinkLayer.class)) {
					UDGEnergyConsumingLinkLayerProxy.createInstance(serviceUnit);
				}
			}
       	});
    }

    public static void createFactory(ServiceUnit serviceUnit, final double alpha, final double a,
    		final double b, final double c, final double broadcastRadius) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				if(!serviceUnit.hasService(LinkLayer.class)) {
					UDGEnergyConsumingLinkLayerProxy.createInstance(serviceUnit, alpha, a, b, c, broadcastRadius);
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
		UDGEnergyConsumingLinkLayerProxy linkLayerProxy = new UDGEnergyConsumingLinkLayerProxy(
				linkLayerAddress, globalLinkLayerService, energyServiceID, alpha, a, b, c, broadcastRadius);
		serviceUnit.addService(linkLayerProxy);
    }

    public UDGEnergyConsumingLinkLayerProxy(Address linkLayerAddress, ServiceID globalNetworkServiceID, ServiceID energyServiceID, double alpha, double a, double b, double c, double broadcastRadius) {
		super(linkLayerAddress, globalNetworkServiceID, energyServiceID, alpha, a, b, c, broadcastRadius);
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
