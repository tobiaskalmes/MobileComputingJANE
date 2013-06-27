package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;

/**
 * This class implements a beconing service with the a fixed beaconing delta bd, cleanup delta cd,
 * and expiration delta ed. For a given start time st, beacons will be transmitted at
 * time st + n * bd, for n in IN. Analogous, expired data will be removed at every time
 * st + n * cd. Data is termed as expired if it is older than the the current time -
 * expiration delta.
 */
public class FixedBeaconingService extends GenericBeaconingService {

    private double beaconingDelta;
    private double cleanupDelta;
    private double expirationDelta;

	public static void createInstance(ServiceUnit serviceUnit) {
		createInstance(serviceUnit, 1.0, 1.0, 3.0);
	}

	public static void createInstance(ServiceUnit serviceUnit, double beaconDelta, double cleanupDelta, double expireDelta) {
		ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
        ServiceID ownServiceID = new StackedClassID(GenericBeaconingService.class.getName(), linkLayerService);
		Service fixedBeaconingService = new FixedBeaconingService(ownServiceID, linkLayerService, beaconDelta, cleanupDelta, expireDelta);
		serviceUnit.addService(fixedBeaconingService);
	}

    /**
     * Construct a new beaconing service.
     * @param beaconingDelta the interval between two successive beacon messages
     * @param cleanupDelta the interval between two successive cleanups
     * @param expirationDelta data older than current time - expiration delta will be removed at every cleanup
     */
    public FixedBeaconingService(ServiceID ownServiceID, ServiceID linkLayerService,
    		double beaconingDelta, double cleanupDelta, double expirationDelta) {
		super(ownServiceID, linkLayerService);
        this.beaconingDelta = beaconingDelta;
        this.cleanupDelta = cleanupDelta;
        this.expirationDelta = expirationDelta;
    }

    public double getBeaconingDelta() {
        return beaconingDelta;
    }

    public double getCleanupDelta() {
        return cleanupDelta;
    }

    public double getExpirationDelta() {
        return expirationDelta;
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}

}
