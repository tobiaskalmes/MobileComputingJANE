/*******************************************************************************
 * 
 * RandomBeaconingService.java
 * 
 * $Id: RandomBeaconingService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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

package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

/**
 * This beacon service implementation avoids synchronization of neighbor beacons. A beacon transmission
 * is jittered in an interval between two successive beacon transmissions.
 */
public class RandomBeaconingService extends GenericBeaconingService {

	public static final IdentifiedServiceElement INITIALIZER = new BeaconingInitializer(
			"randomBeaconing", "This beacon service implementation avoids synchronization of neighbor beacons. " +
					"A beacon transmission is jittered in an interval between two successive beacon transmissions.") {
		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			ServiceID linkLayerID = LinkLayerBase.REQUIRED_SERVICE.getInstance(serviceUnit, initializationContext);
			double bd = BEACONING_DELTA.getValue(initializationContext);
			double bj = BEACONING_JITTER.getValue(initializationContext);
			double bcd = CLEANUP_DELTA.getValue(initializationContext);
			double bed = EXPIRATION_DELTA.getValue(initializationContext);
			serviceUnit.addService(new RandomBeaconingService(ownServiceID, linkLayerID, bd, bj, bcd, bed));
		}
//		public ServiceID getServiceID(ServiceUnit serviceUnit) {
//			if(!serviceUnit.hasService(BeaconingService.class)) {
//		        ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
//		        ServiceID ownServiceID = new StackedClassID(GenericBeaconingService.class.getName(), linkLayerService);
//		        Service randomBeaconingService = new RandomBeaconingService(ownServiceID, linkLayerService, beaconDelta, beaconJitter, cleanupDelta, expireDelta);
//		        return serviceUnit.addService(randomBeaconingService,visualize);
//				RandomBeaconingService.createInstance(serviceUnit);
//			}
//			return serviceUnit.getService(BeaconingService.class);
//		}

		public Parameter[] getParameters() {
			return new Parameter[] { LinkLayerBase.REQUIRED_SERVICE, BEACONING_DELTA, BEACONING_JITTER,
					CLEANUP_DELTA, EXPIRATION_DELTA };
		}
	};

	private double beaconDelta;
	private double beaconJitter;
	private double cleanupDelta;
	private double expireDelta;
	private ContinuousDistribution interBeaconDistribution;

    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @param visualize
     * @return
     * @deprecated visualization has to be set in the ServiceUnit!!!
     */
	// TODO: visualize ist kein parameter des beaconing services!!!
	public static ServiceID createInstance(ServiceUnit serviceUnit, boolean visualize) {
        return createInstance(serviceUnit, 1.0, 0.5, 1.0, 3.0,visualize);
        
    }
    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @param beaconDelta
     * @param beaconJitter
     * @param cleanupDelta
     * @param expireDelta
     * @param visualize
     * @return
     * @deprecated visualization has to be set in the ServiceUnit!!!
     */
	// TODO: visualize ist kein parameter des beaconing services!!!
	public static ServiceID createInstance(ServiceUnit serviceUnit, double beaconDelta, double beaconJitter, double cleanupDelta, double expireDelta,boolean visualize) {
        ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
        ServiceID ownServiceID = new StackedClassID(GenericBeaconingService.class.getName(), linkLayerService);
        Service randomBeaconingService = new RandomBeaconingService(ownServiceID, linkLayerService, beaconDelta, beaconJitter, cleanupDelta, expireDelta);
        return serviceUnit.addService(randomBeaconingService,visualize);
    }
    
    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
	    return createInstance(serviceUnit, serviceUnit.getVisualizeAddedServices());
	}

    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @param beaconDelta
     * @param beaconJitter
     * @param cleanupDelta
     * @param expireDelta
     * @return
     */
	public static ServiceID createInstance(ServiceUnit serviceUnit, double beaconDelta, double beaconJitter, double cleanupDelta, double expireDelta) {
	    return createInstance(serviceUnit, beaconDelta, beaconJitter, cleanupDelta, expireDelta, serviceUnit.getVisualizeAddedServices());
	}

    public RandomBeaconingService(ServiceID linkLayerService, double beaconDelta, double beaconJitter, double cleanupDelta, double expireDelta) {
    	this(new StackedClassID(GenericBeaconingService.class.getName(), linkLayerService), linkLayerService, beaconDelta, beaconJitter, cleanupDelta, expireDelta);
    }

	/**
	 * Construct a new random beacon service.
     * @param beaconDelta the interval between to sucessive beacons without any jitter
     * @param beaconJitter the amont of jitter added to the base beacon interval. The next beacon delta is
     * calculated uniformly distributed in the interval [(1.0-beaconJitter)*beaconDelta, (1.0+beaconJitter)*beaconDelta].
     * The jitter value has to be inside the interval [0.0,1.0].
     * @param cleanupDelta the timeout delta between two sucessive cleanus of the table containing all current
     * neighbor information.
     * @param expireDelta the delta between to sucessive neighbor beacons before the entry will be removed
     * from the map of all current neighbors
     */
    public RandomBeaconingService(ServiceID ownServiceID, ServiceID linkLayerService, double beaconDelta, double beaconJitter, double cleanupDelta, double expireDelta) {
		super(ownServiceID, linkLayerService);
        this.beaconDelta = beaconDelta;
        this.beaconJitter = beaconJitter;
        this.cleanupDelta = cleanupDelta;
        this.expireDelta = expireDelta;
    }

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        DistributionCreator distributionCreator = runtimeOperatingSystem.getDistributionCreator();
		double a = (1.0-beaconJitter)*beaconDelta;
		double b = (1.0+beaconJitter)*beaconDelta;
		interBeaconDistribution = distributionCreator.getContinuousUniformDistribution(a,b);
		super.start(runtimeOperatingSystem);
	}
	
    public double getBeaconingDelta() {
        return interBeaconDistribution.getNext();
    }

    public double getCleanupDelta() {
        return cleanupDelta;
    }

    public double getExpirationDelta() {
        return expireDelta;
    }

	public void getParameters(Parameters parameters) {
		super.getParameters(parameters);
		parameters.addParameter("beaconDelta", beaconDelta);
		parameters.addParameter("beaconJitter", beaconJitter);
		parameters.addParameter("cleanupDelta", cleanupDelta);
		parameters.addParameter("expireDelta", expireDelta);
	}



}
