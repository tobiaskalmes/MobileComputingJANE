package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;

/**
 * This beaconing service is intended to be used in a stationary network. The
 * beaconing service runs for a specified time period and stops to send any
 * message after that time. This requires less computation and memory resources
 * during a simulation run.
 * 
 * @author Hannes Frey
 */
public class TemporaryBeaconingService extends GenericBeaconingService {

	private double beaconDelta;
	private double beaconJitter;
	private double duration;
	private boolean cummulative;
	private ContinuousDistribution interBeaconDistribution;
	private RuntimeOperatingSystem runtimeOperatingSystem;
	private double startTime;
	
	public static ServiceID createInstance(ServiceUnit serviceUnit) {
        return createInstance(serviceUnit, 1.0, 0.5, 10.0, true);
    }

	public static ServiceID createInstance(ServiceUnit serviceUnit, double beaconDelta, double beaconJitter, double duration, boolean cummulative) {
        ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
        ServiceID ownServiceID = new StackedClassID(GenericBeaconingService.class.getName(), linkLayerService);
        Service temporaryBeaconingService = new TemporaryBeaconingService(ownServiceID, linkLayerService, beaconDelta, beaconJitter, duration, cummulative);
        return serviceUnit.addService(temporaryBeaconingService);
    }

	/**
	 * Construct a new temporary beaconing service.
	 * @param linkLayerService the link layer used to send the beacon signals
     * @param beaconDelta the interval between to sucessive beacons without any jitter
     * @param beaconJitter the amont of jitter added to the base beacon interval. The next beacon delta is
     * calculated uniformly distributed in the interval [(1.0-beaconJitter)*beaconDelta, (1.0+beaconJitter)*beaconDelta].
     * The jitter value has to be inside the interval [0.0,1.0].
     * @param duration the duration how long this beaconing service is sending a beacon signal
     * @param cummulative if set true, the beaconing service will not remove any neighbor node when a unicast message was lost
     */
    public TemporaryBeaconingService(ServiceID ownServiceID, ServiceID linkLayerService, double beaconDelta, double beaconJitter, double duration, boolean cummulative) {
		super(ownServiceID, linkLayerService);
        this.beaconDelta = beaconDelta;
        this.beaconJitter = beaconJitter;
        this.duration = duration;
        this.cummulative = cummulative;
    }

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        DistributionCreator distributionCreator = runtimeOperatingSystem.getDistributionCreator();
		double a = (1.0-beaconJitter)*beaconDelta;
		double b = (1.0+beaconJitter)*beaconDelta;
		interBeaconDistribution = distributionCreator.getContinuousUniformDistribution(a,b);
		this.runtimeOperatingSystem = runtimeOperatingSystem;
		startTime = runtimeOperatingSystem.getTime();
		super.start(runtimeOperatingSystem);
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("beacon delta", beaconDelta);
		parameters.addParameter("beacon jitter", beaconJitter);
		parameters.addParameter("duration", duration);
		parameters.addParameter("cummulative", cummulative);
	}

	public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
		if(!cummulative) {
			super.notifyUnicastLost(receiver, message);
		}
	}

	public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message) {
		if(!cummulative) {
			super.notifyUnicastUndefined(receiver, message);
		}
	}

	protected double getBeaconingDelta() {
		return interBeaconDistribution.getNext();
	}

	protected double getCleanupDelta() {
		// called only once
		return 1.0;
	}

	protected double getExpirationDelta() {
		// never called
		return 0;
	}

	protected void handleBeaconingTimeout() {
		double time = runtimeOperatingSystem.getTime();
		if(time-startTime <= duration) {
			super.handleBeaconingTimeout();
		}
	}

	protected void handleCleanupTimeout() {
		// ignore cleanup timouts
	}

}
